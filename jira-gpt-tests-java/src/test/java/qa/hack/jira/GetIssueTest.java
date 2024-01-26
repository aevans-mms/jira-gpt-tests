package qa.hack.jira;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import qa.hack.jira.model.TestStep;

import java.util.List;

public class GetIssueTest extends JiraApiTestBase {

	@Test
	public void testGetIssueWithSteps() throws JsonProcessingException {
		String issue = "QAUTO-748";

		log.info("===== >getting issue : " + issue);

		RequestSpecification getIssueRequest = RestAssured.given().log().all()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(JIRA_API_PATH)
				.pathParam("issue", issue);

		Response getIssueResponse = getIssueRequest.get("/issue/{issue}");

		JsonPath json = getIssueResponse.jsonPath();
		String summary = json.getString("fields.summary");
		String issueId = json.getString("id");
		String key = json.getString("key");
		String url = json.getString("self");

		System.out.println("summary: " + summary);
		System.out.println("issueId: " + issueId);
		System.out.println("key: " + key);
		System.out.println("url: " + url);
		System.out.println("");

		/* get test steps */
		log.info("=====> get test steps for issue id: " + issueId);

		RequestSpecification zephyrRequest = RestAssured.given().log().all()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(ZEPHYR_API_PATH)
				.pathParam("issueId", issueId);

		Response testStepResponse = zephyrRequest.get("/teststep/{issueId}");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

		JsonPath testStepsJsonPath = testStepResponse.jsonPath();
		List<Object> testSteps = testStepsJsonPath.getList("stepBeanCollection");
		System.out.println("total steps: " + testSteps.size());

		testSteps.forEach(object -> {
			TestStep testStep = mapper.convertValue(object, TestStep.class);
			System.out.println("step number: " + testStep.orderId);
			System.out.println("step: " + testStep.step);
			System.out.println("expected result: " + testStep.result);
		});
	}
}
