package de.henrik.bt;

import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.storage.v1.TableName;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;


public class CreateFunction implements HttpFunction {

	private static final Logger logger = Logger.getLogger(CreateFunction.class.getName());

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
			logger.warning("Unauthorized access API key: " + request.getHeaders().get("Authorization").get(0));
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
				AD_WorkDTA work = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson(rawBody, AD_WorkDTA.class);
				create(work, response);
			} catch (JsonSyntaxException e) {
				logger.warning("Invalid JSON: " + e.getMessage());
				response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
				response.getWriter().write("Invalid JSON: " + e.getMessage());
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

	private void create(AD_WorkDTA work, HttpResponse response) throws Exception {

		try {
			if (isTest()) {
				response.setStatusCode(HttpURLConnection.HTTP_OK);
				response.getWriter().write("Successfully inserted row.");
				return;
			}
			BigQuery bq = BigQueryOptions.getDefaultInstance().getService();

			String projectId = "bt-data-integrator";
			String datasetName = "poc";
			String tableName = "work";

			Map<String, Object> rowContent = new HashMap<>();

			rowContent.put("id", work.getId());
			rowContent.put("work_parent_id", work.getWork_parent_id());
			rowContent.put("plannedDuration", work.getPlannedDuration());
			rowContent.put("actualDuration", work.getActualDuration());
			rowContent.put("requestedStartDate", work.getRequestedStartDate());
			rowContent.put("expectedStartDate", work.getExpectedStartDate());
			rowContent.put("expectedCompletionDate", work.getExpectedCompletionDate());
			rowContent.put("cancellationDate", work.getCancellationDate());
			rowContent.put("cancellationReason", work.getCancellationReason());

			// Add the rest of the fields
			rowContent.put("completionStartDate", work.getCompletionStartDate());
			rowContent.put("completionEndDate", work.getCompletionEndDate());

			rowContent.put("description", work.getDescription());
			rowContent.put("bundleId", work.getBundleId());

			rowContent.put("isActivated", work.isActivated());
			rowContent.put("isSplittable", work.isSplittable());
			rowContent.put("isAppointmentAgreed", work.isAppointmentAgreed());
			rowContent.put("isBundle", work.isBundle());
			rowContent.put("isWorkEnabled", work.isWorkEnabled());

			rowContent.put("jeopardy", work.getJeopardy());
			rowContent.put("isQualityGateEnabled", work.isQualityGateEnabled());
			rowContent.put("name", work.getName());
			rowContent.put("orderDate", work.getOrderDate());

			rowContent.put("state", work.getState());
			rowContent.put("workPriority", work.getWorkPriority());
			rowContent.put("type", work.getType());
			rowContent.put("relevance", work.getRelevance());
			rowContent.put("schedulingType", work.getSchedulingType());
			rowContent.put("plannedQuantity_amount", work.getPlannedQuantity_amount());
			rowContent.put("plannedQuantity_units", work.getPlannedQuantity_units());
			rowContent.put("actualQuantity_amount", work.getActualQuantity_amount());
			rowContent.put("actualQuantity_units", work.getActualQuantity_units());
			rowContent.put("workSpecification", work.getWorkSpecification());

			String query = "INSERT INTO `" + projectId + "." + datasetName + "." + tableName + "` ("
					+ String.join(",", rowContent.keySet()) + ") VALUES ("
					+ String.join(",", rowContent.values().stream().map(v -> {
				if (v instanceof String) {
					return "\"" + v + "\"";
				} else if (v instanceof Timestamp) {
					return "\"" + v + "\"";
				} else if (v == null) {
					return "null";
				} else {
					return v.toString();
				}
			}).toArray(String[]::new)) + ")";

			QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
			TableResult result = bq.query(queryConfig);

			response.setStatusCode(HttpURLConnection.HTTP_OK);
			response.getWriter().write("Successfully inserted row: " + result.toString());
			return;

		}
		catch (BigQueryException ex) {
			response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
			response.getWriter().write("Failed to insert row.");
			logger.severe("Failed to insert row: " + ex.getMessage());
			return;
		}
	}

	private  boolean isTest() {
		return System.getenv("TEST") != null && Objects.equals(System.getenv("TEST"), "true");
	}

	private Object getBetterTime(Timestamp t) {
		if (t == null) {
			return null;
		}
		return (int) Math.floor(t.getTime() /1000);
	}
}