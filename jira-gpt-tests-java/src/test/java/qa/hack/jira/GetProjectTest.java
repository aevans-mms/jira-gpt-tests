package qa.hack.jira;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GetProjectTest {
	static Logger log = LoggerFactory.getLogger(GetProjectTest.class);
	static Config config = ConfigFactory.load();

	public static String JIRA_BASE_URL;
	public static String JIRA_API_PATH;
	public static String JIRA_TOKEN;

	@BeforeAll
	public static void init() {
		JIRA_BASE_URL = config.getString("JIRA_BASE_URL");
		JIRA_API_PATH = config.getString("JIRA_API_PATH");
		JIRA_TOKEN = config.getString("JIRA_TOKEN");
	}

	@Test
	public void testGetProject() {
		RequestSpecification request = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(JIRA_API_PATH);

		String projectId = "15000";

		Response response = request
				.pathParam("projectId", projectId)
				.get("/project/{projectId}");

		QueryableRequestSpecification queryable = SpecificationQuerier.query(request);
		System.out.println(queryable.getMethod());
		System.out.println(queryable.getURI());
		System.out.println(queryable.getPathParams());
		System.out.println(queryable.getHeaders());

		System.out.println(response.getStatusCode());
		System.out.println(response.getBody().asPrettyString());
	}

	@Test
	public void testGetIssue() throws JsonProcessingException {
		/* get test case (issue) */
		RequestSpecification request = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(JIRA_API_PATH);

		Response response = request.get("/issue/QAUTO-748");

		QueryableRequestSpecification queryable = SpecificationQuerier.query(request);
//		System.out.println(queryable.getMethod());
//		System.out.println(queryable.getURI());
//		System.out.println(queryable.getPathParams());
//		System.out.println(queryable.getHeaders());

//		System.out.println(response.getStatusCode());
//		System.out.println(response.getBody().asPrettyString());

		JsonPath json = response.jsonPath();
		String summary = json.getString("fields.summary");
		String issueId = json.getString("id");
		String key = json.getString("key");
		String url = json.getString("self");

		System.out.println("key: " + key);
		System.out.println("issueId: " + issueId);
		System.out.println("url: " + url);
		System.out.println("summary: " + summary);

		System.out.println("");

		/* get test steps */

		String ZEPHYR_API_PATH = "/rest/zapi/latest";
		RequestSpecification zephyrRequest = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(ZEPHYR_API_PATH)
				.pathParam("issueId", issueId);

		Response testStepResponse = zephyrRequest.get("/teststep/{issueId}");

//		System.out.println(testStepResponse.getStatusCode());
//		System.out.println(testStepResponse.getBody().asPrettyString());


		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		JsonPath testStepsJsonPath = testStepResponse.jsonPath();
		List<Object> testSteps= testStepsJsonPath.getList("stepBeanCollection");
		System.out.println("total steps: " + testSteps.size());

		testSteps.forEach(object -> {
			TestStep testStep = mapper.convertValue(object, TestStep.class);
			System.out.println("step number: " + testStep.orderId);
			System.out.println("step: " + testStep.step);
			System.out.println("expected result: " + testStep.result);
		});
	}
}
