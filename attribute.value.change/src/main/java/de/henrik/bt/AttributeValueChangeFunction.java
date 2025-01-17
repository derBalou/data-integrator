package de.henrik.bt;

import com.google.cloud.bigquery.*;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.logging.Logger;


public class AttributeValueChangeFunction implements HttpFunction {

	private static final Logger logger = Logger.getLogger(AttributeValueChangeFunction.class.getName());

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
				changeAttributeValue(work, response);
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

	private void changeAttributeValue(AD_WorkDTA newWork, HttpResponse response) throws Exception {

		try {
			if (isTest()) {
				response.setStatusCode(HttpURLConnection.HTTP_OK);
				response.getWriter().write("Successfully updated work.");
				return;
			}

			BigQuery bq = BigQueryOptions.getDefaultInstance().getService();

			String datasetName = "poc";
			String tableName = "work";

			if (newWork.getId() == null) {
				response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
				response.getWriter().write("ID is missing");
				logger.warning("ID is missing");
				return;
			}

			StringBuilder query = new StringBuilder();
			query.append("UPDATE `bt-data-integrator.poc.work` SET id = \"").append(newWork.getId()).append("\"");

			Field[] fields = newWork.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.get(newWork) != null && !field.getName().equals("id")){
					if (field.getType() == String.class) {
						query.append(", ").append(field.getName()).append(" = \"").append(field.get(newWork)).append("\"");
					} else if (field.getType() == Timestamp.class) {
						query.append(", ").append(field.getName()).append(" = \"").append(field.get(newWork)).append("\"");
					} else {
						query.append(", ").append(field.getName()).append(" = ").append(field.get(newWork));
					}
				}
			}

			query.append(" WHERE id = \"").append(newWork.getId()).append("\";");

			QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query.toString()).build();
			TableResult result = bq.query(queryConfig);

		}
		catch (BigQueryException | JobException | InterruptedException ex) {
			response.setStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
			response.getWriter().write("Failed to insert row.");
			logger.severe("Failed to insert row: " + ex.getMessage());
			return;
		}


		response.setStatusCode(HttpURLConnection.HTTP_OK);
		response.getWriter().write("Successfully updated work.");
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