package de.henrik.bt.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import de.henrik.bt.AD_WorkDTA;
import de.henrik.bt.StateChangeFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;


class TestChangeStateFunction {

	@Mock private HttpRequest request;
	@Mock private HttpResponse response;

	private BufferedWriter writerOut;
	private StringWriter responseOut;

	private TestInfo testInfo;

	@BeforeEach
	public void beforeTest(TestInfo testInfo) throws IOException {
		// Initialize mocks and change HTTP Request Method
		MockitoAnnotations.openMocks(this);

		this.testInfo = testInfo;

		when(request.getMethod()).thenReturn("POST");
		Map<String, List<String>> headers = new java.util.HashMap<>(Map.of("Authorization", List.of("Bearer 123456")));
		headers.put("Content-Type", List.of("application/json"));
		when(request.getHeaders()).thenReturn(headers);

		//when(request.getHeaders()).thenReturn((Map<String, String>) Collections.singletonMap("Authorization", "Bearer"));

		if (testInfo.getDisplayName().equals("StateChangeFunctionTest()")) {
			when(request.getMethod()).thenReturn("POST");
		}

		responseOut = new StringWriter();
		writerOut = new BufferedWriter(responseOut);
		when(response.getWriter()).thenReturn(writerOut);
	}



	@Test
	public void StateChangeFunctionTest() throws Exception {

		AD_WorkDTA work = new AD_WorkDTA(true);
		String json = new Gson().toJson(work);

		when(request.getReader()).thenReturn(new BufferedReader(new java.io.StringReader(json)));

		new StateChangeFunction().service(request, response);

		writerOut.flush();

		assertTrue(responseOut.toString().contains("Successfully updated work."));
	}

	@Test
	public void StateChangeFunctionWrongAPIKeyTest() throws Exception {
		Map<String, List<String>> headers = Map.of("Authorization", List.of("Bearer ABCDEF"));
		when(request.getHeaders()).thenReturn(headers);
		new StateChangeFunction().service(request, response);

		writerOut.flush();
		assertTrue(responseOut.toString().contains("Forbidden"));
	}

	@Test
	public void StateChangeFunctionNoAPIKeyTest() throws Exception {
		when(request.getHeaders()).thenReturn(Collections.emptyMap());
		new StateChangeFunction().service(request, response);

		writerOut.flush();
		assertTrue(responseOut.toString().contains("Unauthorized"));
	}

}
