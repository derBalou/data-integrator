package de.henrik.bt;

import com.google.cloud.bigquery.*;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.net.HttpURLConnection;


public class StateChangeFunction implements HttpFunction {

	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		String key = System.getenv("API_KEY");

		if (!request.getHeaders().containsKey("Authorization")) {
			response.setStatusCode(HttpURLConnection.HTTP_UNAUTHORIZED);
			response.getWriter().write("Unauthorized");
			return;
		}

		if (!request.getHeaders().get("Authorization").get(0).equals("Bearer " + key)) {
			response.setStatusCode(HttpURLConnection.HTTP_FORBIDDEN);
			response.getWriter().write("Forbidden");
			return;
		}

		if (!request.getHeaders().containsKey("Content-Type")) {
			response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
			response.getWriter().write("Content-Type header is missing");
			return;
		}

		if (!request.getHeaders().get("Content-Type").get(0).equals("application/json")) {
			response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
			response.getWriter().write("Only Content-Type application/json is supported");
			return;
		}

		if (request.getMethod().equals("POST")) {
			String rawBody = request.getReader().lines().reduce("", (s1, s2) -> s1 + s2);
			try {
				// Parse JSON
				AD_WorkDTA work = new GsonBuilder().create().fromJson(rawBody, AD_WorkDTA.class);
				changeState(work, response);
			} catch (JsonSyntaxException e) {
				response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
				response.getWriter().write("Invalid JSON");
			}
		} else if (request.getMethod().equals("OPTIONS")) {
			response.appendHeader("Access-Control-Allow-Methods", "POST");
			response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
			response.setStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
			response.getWriter().write("OK");
		} else {
			response.setStatusCode(HttpURLConnection.HTTP_BAD_METHOD);
			response.getWriter().write("Method not allowed!");
		}
	}

	private void changeState(AD_WorkDTA newWork, HttpResponse response) throws Exception {

		try {
			BigQuery bq = BigQueryOptions.getDefaultInstance().getService();

			String datasetName = "poc";
			String tableName = "work";

			String query =
					"SELECT * FROM `poc.work` " +
							"WHERE id = @id";

			QueryJobConfiguration queryConfig =
					QueryJobConfiguration.newBuilder(query)
							.addNamedParameter("id", QueryParameterValue.string(newWork.getId()))
							.build();

			TableResult result = bq.query(queryConfig);

			if (result.getTotalRows() == 0) {
				response.setStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
				response.getWriter().write("Row not found.");
				return;
			}

			// load oldWork data from BigQuery

			AD_WorkDTA oldWork = new AD_WorkDTA();

			for (FieldValueList row : result.iterateAll()) {
				oldWork.setId(row.get("id").getStringValue());
				oldWork.setWork_parent_id(row.get("work_parent_id").getStringValue());
				oldWork.setPlannedDuration(row.get("plannedDuration").getStringValue());
				oldWork.setActualDuration(row.get("actualDuration").getStringValue());

				// add more fields here

			}




			/*if (bqResponse.hasErrors()) {
				response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
				response.getWriter().write("Failed to insert row.");
				return;
			}*/
		}
		catch (BigQueryException ex) {
			response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
			response.getWriter().write("Failed to insert row.");
			return;
		}

		response.setStatusCode(HttpURLConnection.HTTP_OK);
		response.getWriter().write("Successfully inserted row.");
	}
}