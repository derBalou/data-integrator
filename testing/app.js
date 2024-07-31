const axios = require('axios');
var microtime = require('microtime');
var fs = require('fs');

const baseUrl = "https://bt.derbalou.dev/";

let sentIds = [];
let requests = {
    "create": [],
    "state.change": [],
    "attribute.value.change": []
}

function sleep(ms) {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
}

async function warmUp() {
    for (let i = 0; i < 10; i++) {
        data = generateData();
        let headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer XXXX'
        };
        await axios.post(baseUrl + 'create', data, { headers }).then((response) => {
            sentIds.push(data.id);
            console.log("Create Warmup " + i + ": " + response.status);
        });
        //await sleep(1000);
    }

    for (let i = 0; i < 10; i++) {
        data = generateStateChangeData(randomId());
        let headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer XXXX'
        };
        await axios.post(baseUrl + 'state.change', data, { headers }).then((response) => {
            console.log("State.Change Warmup " + i + ": " + response.status);
        });
        //await sleep(1000);
    }

    for (let i = 0; i < 10; i++) {
        data = generateAttributeValChangeData(randomId());
        let headers = {
            'Content-Type': 'application/json',
            'Authorization ': 'Bearer XXXX'
        };
        await axios.post(baseUrl + 'attribute.value.change', data, { headers }).then((response) => {
            console.log("Attribute.Value.Change Warmup " + i + ": " + response.status);
        });
        //await sleep(1000);
    }
    
}

preFillErrors = 0;
errors = {
    "create": 0,
    "state.change": 0,
    "attribute.value.change": 0
}


async function mainTest() {
    // populate database with 1000 create requests to build a pool of ids
    for (let i = 0; i < 200; i++) {
        data = generateData();
        let headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer XXXX'
        };
        axios.post(baseUrl + 'create', data, { headers }).then((response) => {
            sentIds.push(data.id);
            console.log("Create " + i + ": " + response.status);
        }).catch((error) => {
            preFillErrors += 1;
        });
        await sleep(500);
    }

    await sleep(5000);

    console.log("Errors: " + preFillErrors);

    await sleep(10000);

    for (let i = 0; i < 1000; i++) {
        // 33% chance to change state, 33% chance to change attribute values, 33% chance to create new data
        let random = Math.random();
        if (random < 0.33) {
            data = generateStateChangeData(randomId());
            let headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer XXXX'
            };
            axios.post(baseUrl + 'state.change', data, { headers }).then((response) => {
                console.log("State.Change " + i + ": " + response.status);
            }).catch((error) => {
                errors["state.change"] += 1;
            });
            await sleep(59);
        }
        else if (random < 0.66) {
            data = generateAttributeValChangeData(randomId());
            let headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer XXXX'
            };
            axios.post(baseUrl + 'attribute.value.change', data, { headers }).then((response) => {
                console.log("Attribute.Value.Change " + i + ": " + response.status);
            }).catch((error) => {
                errors["attribute.value.change"] += 1;
            });
            await sleep(59);
        }
        else {
            data = generateData();
            let headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer XXXX'
            };
            axios.post(baseUrl + 'create', data, { headers }).then((response) => {
                sentIds.push(data.id);
                console.log("Create " + i + ": " + response.status);
            }).catch((error) => {
                errors["create"] += 1;
            });
            await sleep(59);
        }
    }

    await sleep(10000);

    // write requests to file
    fs.writeFile("requests.json", JSON.stringify(errors), function (err) {
        if (err) {
            console.log(err);
        }
    });
        
}

// generate random String
function randomString(length) {
    var result = '';
    var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for (var i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

function randomDate(start, end) {
    return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()));
}

function randomState() {
    let states = ["ACKNOWLEDGED", "REJECTED", "PENDING", "IN_PROGRESS", "CANCELLED", "COMPLETED", "FAILED"];
    return states[Math.floor(Math.random() * states.length)];
}

/* Format for random data:
{
  "id": "sdjlkfhijoskdlhf",
  "work_parent_id": null,
  "plannedDuration": "10",
  "actualDuration": "11",
  "requestedStartDate": "2024-07-20T13:45:00.000Z",
  "expectedStartDate": "2024-07-20T13:45:00.000Z",
  "expectedCompletionDate": "2024-07-20T18:00:00.000Z",
  "cancellationDate": null,
  "cancellationReason": null,
  "completionStartDate": "2024-07-20T17:00:00.000Z",
  "completionEndDate": "2024-07-20T18:00:00.000Z",
  "description": "Testbeschreibung",
  "bundleId": null,
  "isActivated": true,
  "isSplittable": true,
  "isAppointmentAgreed": true,
  "isBundle": false,
  "isWorkEnabled": true,
  "jeopardy": null,
  "isQualityGateEnabled": false,
  "name": "OE0401",
  "orderDate": "2024-07-18T09:30:00.000Z",
  "state": "IN_PROGRESS",
  "workPriority": 0,
  "type": "NORMAL_WORK",
  "relevance": "NORMAL",
  "schedulingType": "TYPE_1",
  "plannedQuantity_amount": 10.0,
  "plannedQuantity_units": "Hours",
  "actualQuantity_amount": 11.0,
  "actualQuantity_units": "Hours",
  "workSpecification": "Meeeeep"
}
*/

function generateData() {
    let data = {
        "id": randomString(64),
        "work_parent_id": Math.random() > 0.5 ? randomString(64) : null,
        "plannedDuration": Math.floor(Math.random() * 1000).toString(),
        "actualDuration": Math.floor(Math.random() * 1000).toString(),
        "requestedStartDate": randomDate(new Date(2024, 0, 1), new Date()).toISOString(),
        "expectedStartDate": randomDate(new Date(2024, 0, 1), new Date()).toISOString(),
        "expectedCompletionDate": randomDate(new Date(2024, 0, 1), new Date()).toISOString(),
        "cancellationDate": null,
        "cancellationReason": null,
        "completionStartDate": randomDate(new Date(2024, 0, 1), new Date()).toISOString(),
        "completionEndDate": randomDate(new Date(2024, 0, 1), new Date()).toISOString(),
        "description": randomString(100),
        "bundleId": null,
        "isActivated": true,
        "isSplittable": Math.random() > 0.5,
        "isAppointmentAgreed": Math.random() > 0.5,
        "isBundle": false,
        "isWorkEnabled": true,
        "jeopardy": null,
        "isQualityGateEnabled": Math.random() > 0.5,
        "name": randomString(10),
        "orderDate": randomDate(new Date(2024, 0, 1), new Date()).toISOString(),
        "state": randomState(),
        "workPriority": Math.floor(Math.random() * 10),
        "type": "NORMAL_WORK",
        "relevance": "NORMAL",
        "schedulingType": "TYPE_1",
        "plannedQuantity_amount": Math.random() * 100,
        "plannedQuantity_units": "Hours",
        "actualQuantity_amount": Math.random() * 100,
        "actualQuantity_units": "Hours",
        "workSpecification": randomString(100)
    };
    return data;
}

function generateDataWithId(id) {
    let data = generateData();
    data.id = id;
    return data;
}

function generateStateChangeData(id) {
    let data = {
        "id": id,
        "state": randomState()
    };

    // Add some more data but not all fields
    data.plannedDuration = Math.floor(Math.random() * 1000).toString();
    data.actualDuration = Math.floor(Math.random() * 1000).toString();
    data.expectedStartDate = randomDate(new Date(2024, 0, 1), new Date()).toISOString();
    data.expectedCompletionDate = randomDate(new Date(2024, 0, 1), new Date()).toISOString();
    data.completionStartDate = randomDate(new Date(2024, 0, 1), new Date()).toISOString();
    data.isWorkEnabled = true;
    data.jeopardy = null;
    data.isQualityGateEnabled = Math.random() > 0.5;
    data.name = randomString(10);
    data.orderDate = randomDate(new Date(2024, 0, 1), new Date()).toISOString();
    data.workPriority = Math.floor(Math.random() * 10);

    return data;
}

function generateAttributeValChangeData(id) {
    let data = generateDataWithId(id);

    // remove fields from object randomly
    if (Math.random() > 0.5) {
        delete data.work_parent_id;
    }
    if (Math.random() > 0.5) {
        delete data.plannedDuration;
    }
    if (Math.random() > 0.5) {
        delete data.actualDuration;
    }
    if (Math.random() > 0.5) {
        delete data.requestedStartDate;
    }
    if (Math.random() > 0.5) {
        delete data.expectedStartDate;
    }
    if (Math.random() > 0.5) {
        delete data.expectedCompletionDate;
    }
    if (Math.random() > 0.5) {
        delete data.cancellationDate;
    }
    if (Math.random() > 0.5) {
        delete data.cancellationReason;
    }
    if (Math.random() > 0.5) {
        delete data.completionStartDate;
    }
    if (Math.random() > 0.5) {
        delete data.completionEndDate;
    }
    if (Math.random() > 0.5) {
        delete data.description;
    }
    if (Math.random() > 0.5) {
        delete data.bundleId;
    }
    if (Math.random() > 0.5) {
        delete data.isActivated;
    }
    if (Math.random() > 0.5) {
        delete data.isSplittable;
    }
    if (Math.random() > 0.5) {
        delete data.isAppointmentAgreed;
    }
    if (Math.random() > 0.5) {
        delete data.isBundle;
    }
    if (Math.random() > 0.5) {
        delete data.isWorkEnabled;
    }
    if (Math.random() > 0.5) {
        delete data.jeopardy;
    }

    return data;
}

function randomId() {
    return sentIds[Math.floor(Math.random() * sentIds.length)];
}

async function startTest() {
    //await warmUp();
    console.log("Warmup done, starting main test...");
    await mainTest();
    console.log("Main test done.");
}

startTest();