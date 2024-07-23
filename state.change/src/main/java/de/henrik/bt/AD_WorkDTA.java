package de.henrik.bt;

import org.apache.arrow.flatbuf.Bool;

import java.sql.Timestamp;

public class AD_WorkDTA {

	public String id;
	public String work_parent_id;

	public String plannedDuration;
	public String actualDuration;

	public Timestamp requestedStartDate;
	public Timestamp expectedStartDate;
	public Timestamp expectedCompletionDate;

	public Timestamp cancellationDate;
	public String cancellationReason;

	public Timestamp completionStartDate;
	public Timestamp completionEndDate;

	public String description;

	public String bundleId;

	public Boolean isActivated;
	public Boolean isSplittable;
	public Boolean isAppointmentAgreed;
	public Boolean isBundle;
	public Boolean isWorkEnabled;

	public String jeopardy;

	public Boolean isQualityGateEnabled;

	public String name;

	public Timestamp orderDate;

	public String state;

	public Short workPriority;
	public String type;

	public String relevance;
	public String schedulingType;

	public Float plannedQuantity_amount;
	public String plannedQuantity_units;

	public Float actualQuantity_amount;
	public String actualQuantity_units;

	public String workSpecification;

	public AD_WorkDTA() {
		super();
	}

	public AD_WorkDTA(boolean mocked) {
		if (mocked) {
			this.id = "123";
			this.work_parent_id = "123";

			this.plannedDuration = "123";
			this.actualDuration = "123";

			this.requestedStartDate = new Timestamp(System.currentTimeMillis());
			this.expectedStartDate = new Timestamp(System.currentTimeMillis());
			this.expectedCompletionDate = new Timestamp(System.currentTimeMillis());

			this.cancellationDate = new Timestamp(System.currentTimeMillis());
			this.cancellationReason = "123";

			this.completionStartDate = new Timestamp(System.currentTimeMillis());
			this.completionEndDate = new Timestamp(System.currentTimeMillis());

			this.description = "123";

			this.bundleId = "123";

			this.isActivated = true;
			this.isSplittable = true;
			this.isAppointmentAgreed = true;
			this.isBundle = true;
			this.isWorkEnabled = true;

			this.jeopardy = "123";

			this.isQualityGateEnabled = true;

			this.name = "123";

			this.orderDate = new Timestamp(System.currentTimeMillis());

			this.state = "123";

			this.workPriority = 123;
			this.type = "123";

			this.relevance = "123";
			this.schedulingType = "123";

			this.plannedQuantity_amount = 123.0f;
			this.plannedQuantity_units = "123";

			this.actualQuantity_amount = 123.0f;
			this.actualQuantity_units = "123";

			this.workSpecification = "123";
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWork_parent_id() {
		return work_parent_id;
	}

	public void setWork_parent_id(String work_parent_id) {
		this.work_parent_id = work_parent_id;
	}

	public String getPlannedDuration() {
		return plannedDuration;
	}

	public void setPlannedDuration(String plannedDuration) {
		this.plannedDuration = plannedDuration;
	}

	public String getActualDuration() {
		return actualDuration;
	}

	@Override
	public String toString() {
		return """
				AD_WorkDTA {
				    id='$id', 
				    work_parent_id='$work_parent_id', 
				    plannedDuration='$plannedDuration', 
				    actualDuration='$actualDuration', 
				    requestedStartDate=$requestedStartDate, 
				    expectedStartDate=$expectedStartDate, 
				    expectedCompletionDate=$expectedCompletionDate, 
				    cancellationDate=$cancellationDate, 
				    cancellationReason='$cancellationReason', 
				    completionStartDate=$completionStartDate, 
				    completionEndDate=$completionEndDate, 
				    description='$description', 
				    bundleId='$bundleId', 
				    isActivated=$isActivated, 
				    isSplittable=$isSplittable, 
				    isAppointmentAgreed=$isAppointmentAgreed, 
				    isBundle=$isBundle, 
				    isWorkEnabled=$isWorkEnabled, 
				    jeopardy='$jeopardy', 
				    isQualityGateEnabled=$isQualityGateEnabled, 
				    name='$name', 
				    orderDate=$orderDate, 
				    state='$state', 
				    workPriority=$workPriority, 
				    type='$type', 
				    relevance='$relevance', 
				    schedulingType='$schedulingType', 
				    plannedQuantity_amount=$plannedQuantity_amount, 
				    plannedQuantity_units='$plannedQuantity_units', 
				    actualQuantity_amount=$actualQuantity_amount, 
				    actualQuantity_units='$actualQuantity_units', 
				    workSpecification='$workSpecification'
				}""";
	}

	public void setActualDuration(String actualDuration) {
		this.actualDuration = actualDuration;
	}

	public Timestamp getRequestedStartDate() {
		return requestedStartDate;
	}

	public void setRequestedStartDate(Timestamp requestedStartDate) {
		this.requestedStartDate = requestedStartDate;
	}

	public Timestamp getExpectedStartDate() {
		return expectedStartDate;
	}

	public void setExpectedStartDate(Timestamp expectedStartDate) {
		this.expectedStartDate = expectedStartDate;
	}

	public Timestamp getExpectedCompletionDate() {
		return expectedCompletionDate;
	}

	public void setExpectedCompletionDate(Timestamp expectedCompletionDate) {
		this.expectedCompletionDate = expectedCompletionDate;
	}

	public Timestamp getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(Timestamp cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public Timestamp getCompletionStartDate() {
		return completionStartDate;
	}

	public void setCompletionStartDate(Timestamp completionStartDate) {
		this.completionStartDate = completionStartDate;
	}

	public Timestamp getCompletionEndDate() {
		return completionEndDate;
	}

	public void setCompletionEndDate(Timestamp completionEndDate) {
		this.completionEndDate = completionEndDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean activated) {
		isActivated = activated;
	}

	public boolean isSplittable() {
		return isSplittable;
	}

	public void setSplittable(boolean splittable) {
		isSplittable = splittable;
	}

	public boolean isAppointmentAgreed() {
		return isAppointmentAgreed;
	}

	public void setAppointmentAgreed(boolean appointmentAgreed) {
		isAppointmentAgreed = appointmentAgreed;
	}

	public boolean isBundle() {
		return isBundle;
	}

	public void setBundle(boolean bundle) {
		isBundle = bundle;
	}

	public boolean isWorkEnabled() {
		return isWorkEnabled;
	}

	public void setWorkEnabled(boolean workEnabled) {
		isWorkEnabled = workEnabled;
	}

	public String getJeopardy() {
		return jeopardy;
	}

	public void setJeopardy(String jeopardy) {
		this.jeopardy = jeopardy;
	}

	public boolean isQualityGateEnabled() {
		return isQualityGateEnabled;
	}

	public void setQualityGateEnabled(boolean qualityGateEnabled) {
		isQualityGateEnabled = qualityGateEnabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Timestamp orderDate) {
		this.orderDate = orderDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public short getWorkPriority() {
		return workPriority;
	}

	public void setWorkPriority(short workPriority) {
		this.workPriority = workPriority;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRelevance() {
		return relevance;
	}

	public void setRelevance(String relevance) {
		this.relevance = relevance;
	}

	public String getSchedulingType() {
		return schedulingType;
	}

	public void setSchedulingType(String schedulingType) {
		this.schedulingType = schedulingType;
	}

	public double getPlannedQuantity_amount() {
		return plannedQuantity_amount;
	}

	public void setPlannedQuantity_amount(float plannedQuantity_amount) {
		this.plannedQuantity_amount = plannedQuantity_amount;
	}

	public String getPlannedQuantity_units() {
		return plannedQuantity_units;
	}

	public void setPlannedQuantity_units(String plannedQuantity_units) {
		this.plannedQuantity_units = plannedQuantity_units;
	}

	public double getActualQuantity_amount() {
		return actualQuantity_amount;
	}

	public void setActualQuantity_amount(float actualQuantity_amount) {
		this.actualQuantity_amount = actualQuantity_amount;
	}

	public String getActualQuantity_units() {
		return actualQuantity_units;
	}

	public void setActualQuantity_units(String actualQuantity_units) {
		this.actualQuantity_units = actualQuantity_units;
	}

	public String getWorkSpecification() {
		return workSpecification;
	}

	public void setWorkSpecification(String workSpecification) {
		this.workSpecification = workSpecification;
	}
}
