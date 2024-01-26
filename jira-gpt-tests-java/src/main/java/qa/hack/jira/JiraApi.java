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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.hack.jira.model.Issue;
import qa.hack.jira.model.Project;
import qa.hack.jira.model.TestStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JiraApi {

	ObjectMapper mapper;

	Logger log;
	Config config;

	String JIRA_TOKEN = System.getenv("JIRA_TOKEN");
	String JIRA_BASE_URL = System.getenv("JIRA_BASE_URL");
	String JIRA_API_PATH = System.getenv("JIRA_API_PATH");
	String ZEPHYR_API_PATH = System.getenv("ZEPHYR_API_PATH");

	boolean LOG_REQUESTS = false; // Hard coded

	public String QAUTO_PROJECT_ID = "15000";

	public void init() {
		log = LoggerFactory.getLogger(this.getClass());
		config = ConfigFactory.load();

		JIRA_TOKEN = config.getString("JIRA_TOKEN");
		JIRA_BASE_URL = config.getString("JIRA_BASE_URL");
		JIRA_API_PATH = config.getString("JIRA_API_PATH");
		ZEPHYR_API_PATH = config.getString("ZEPHYR_API_PATH");

		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
	}

	/**
	 * Constructor
	 *
	 * @param JIRA_BASE_URL
	 * @param JIRA_TOKEN
	 */
	public JiraApi(String JIRA_BASE_URL, String JIRA_TOKEN) {
		init();

		this.JIRA_TOKEN = JIRA_TOKEN;
		this.JIRA_BASE_URL = JIRA_BASE_URL;

		log.debug("JIRA_TOKEN: " + JIRA_TOKEN);
		log.debug("JIRA_BASE_URL: " + JIRA_BASE_URL);
		log.debug("JIRA_API_PATH: " + JIRA_API_PATH);
		log.debug("ZEPHYR_API_PATH: " + ZEPHYR_API_PATH);
	}

	/**
	 * Jira getProjects API call example:
	 * curl -H "Authorization: Bearer $JIRA_TOKEN" https://jira.mms.org/rest/api/latest/project
	 *
	 * @return
	 */
	public List<Project> getProjects() {

		var request = jiraRequest();
		var response = RestAssured.given(request).get("/project");
		log.debug("response status: " + response.statusCode());

		String body = response.getBody().asPrettyString();
		log.debug("response body: \n" + body);

		try {
			List<Project> projectsList = Arrays.asList(mapper.readValue(body, Project[].class));
			return projectsList;
		} catch (JsonProcessingException e) {
			log.warn("unable to parse projects response: " + body);
			throw new RuntimeException(e);
		}
	}

	/**
	 *  Jira getProject API call example:
	 * 	curl -H "Authorization: Bearer $JIRA_TOKEN" https://jira.mms.org/rest/api/latest/project/15000
	 *
	 * @param projectId
	 * @return
	 */
	public Project getProject(String projectId) {
		log.info("get project: " + projectId);

		RequestSpecification request = jiraRequest();
		Response response = request
				.pathParam("projectId", "15000")
				.get("/project/{projectId}");

		if (! (response.getStatusCode() == 200)) {
			String json = response.getBody().asPrettyString();
			log.warn("unexpected response status: " + response.getStatusCode());
			logRequest(request);
			throw new RuntimeException("couldn't get project: " + projectId);
		}

		if (! (response.getContentType().contains("json"))) {
			log.warn("unexpected response content type: " + response.getContentType());
			throw new RuntimeException("not json response: " + response.getContentType());
		}

		String body = response.getBody().asPrettyString();
		log.debug("response: " + body);

		try {
			Project project = Project.fromJson(body);
			return project;
		}
		catch (JsonProcessingException e) {
			log.warn("unable to convert json to Project: " + body);
			throw new RuntimeException(e);
		}
	}

//	public List<Component> getProjectComponents(String projectKey) {
//		var response = jiraRequest()
//				.pathParam("projectKey", projectKey)
//				.get("/projects/{projectKey}");
//
//		log.info(response.getStatusLine());
//
//		List<Component> components = response.jsonPath().<Component>getList("components");
//		return components;
//	}


	/**
	 *  Jira getIssue API call example:
	 * 	curl -H "Authorization: Bearer $JIRA_TOKEN" https://jira.mms.org/rest/api/latest/issue/QAUTO-1234
	 *
	 * @param issueId
	 * @return
	 */
	public Issue getIssue(String issueId) {
		log.info("get issue: " + issueId);

		Response response = jiraRequest()
				.pathParam("issueId", issueId)
				.get("/issue/{issueId}");

		int statusCode = response.getStatusCode();
		if (! (statusCode == 200)) {
			log.warn("unexpected status code: " + statusCode);
			throw new RuntimeException("unexpected status code: " + statusCode);
		}

		String contentType = response.getContentType();
		if (! (contentType.contains("json"))) {
			log.warn("unexpected content type: " + contentType);
			throw new RuntimeException("unexpected content type: " + contentType);
		}

		String body = response.getBody().asPrettyString();
		if (body == null || body.isEmpty()) {
			log.warn("unexpected empty response body");
			throw new RuntimeException("response body is empty");
		}

		JsonPath json = response.jsonPath();
		String summary = json.getString("fields.summary");
		String id = json.getString("id");
		String key = json.getString("key");
		String url = json.getString("self");

		log.debug("summary: " + summary);
		log.debug("id: " + id);
		log.debug("key: " + key);
		log.debug("url: " + url);
		log.debug("");

		Issue issueDetail = new Issue();
		issueDetail.id = id;
		issueDetail.key = key;
		issueDetail.self = url;
		issueDetail.summary = summary;

		//TODO: get more details from Issue

		return issueDetail;
	}


	/**
	 * Example Zephyr API call:
	 * curl -H "Authorization: Bearer $JIRA_TOKEN" https://jira.mms.org/rest/zapi/latest/teststep/{testId}
	 *
	 * @param testId
	 * @return
	 */
	public List<TestStep> getTestSteps(String testId) {
		log.info("get test steps for test: " + testId);
		/* call zephyr API on JIRA server */
		RequestSpecification request = zephyrRequest();

		/* testId is same as issueId */
		Response response = request
				.pathParam("testId", testId)
				.get("/teststep/{testId}");

		int statusCode = response.getStatusCode();
		log.debug("response status: " + statusCode);
		if (! (statusCode == 200)) {
			log.warn("unexpected request status: " + statusCode);
			logRequest(request);
			throw new RuntimeException("couldn't get response for test: " + testId);
		}

		String contentType = response.getContentType();
		log.debug("response contentType: " + contentType);
		if (! (contentType.contains("json"))) {
			log.warn("unexpected response content type: " + contentType);
			throw new RuntimeException("not json response: " + contentType);
		}

		String body = response.getBody().asPrettyString();
		log.debug("response body: " + body);
		if (body == null || body.isEmpty()) {
			log.warn("unexpected empty body");
			throw new RuntimeException("no body in response");
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

		JsonPath testStepsJsonPath = response.jsonPath();
		List<Object> testSteps = testStepsJsonPath.getList("stepBeanCollection");
		log.debug("total steps: " + testSteps.size());


		List<TestStep> testStepsList = new ArrayList<>();

		testSteps.forEach(object -> {
			TestStep testStep = mapper.convertValue(object, TestStep.class);
//			System.out.println("step number: " + testStep.orderId);
//			System.out.println("step: " + testStep.step);
//			System.out.println("expected result: " + testStep.result);

			testStepsList.add(testStep);
		});

		return testStepsList;
	}


	/**
	 * JIra Request Builder
	 * @return
	 */
	public RequestSpecification jiraRequest() {
		RequestSpecification request = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(JIRA_API_PATH);

		if (LOG_REQUESTS)
		{
			return request.log().all();
		}

		return request;
	}

	/**
	 * Zephyr Request Builder
	 * @return
	 */
	public RequestSpecification zephyrRequest() {

		RequestSpecification request = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(ZEPHYR_API_PATH);

		if (LOG_REQUESTS)
		{
			return request.log().all();
		}

		return request;
	}


	public void logRequest(RequestSpecification request) {
		QueryableRequestSpecification queryable = SpecificationQuerier.query(request);
		log.info("request method: " + queryable.getMethod());
		log.info("request URL: " + queryable.getURI());
		log.info("request path params: " + queryable.getPathParams());
		log.info("request query params: " + queryable.getQueryParams());
		log.info("request headers: " + queryable.getHeaders());
		log.info("request body: " + queryable.getBody());
	}


}
