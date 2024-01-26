package qa.hack.jira;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class JiraApi {

	ObjectMapper mapper;

	Logger log;
	Config config;

	String JIRA_TOKEN;
	String JIRA_BASE_URL;
	String JIRA_API_PATH;
	String ZEPHYR_API_PATH;

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

	public JiraApi(String JIRA_BASE_URL, String JIRA_TOKEN) {
		init();

		this.JIRA_TOKEN = JIRA_TOKEN;
		this.JIRA_BASE_URL = JIRA_BASE_URL;

		log.info("JIRA_TOKEN: " + JIRA_TOKEN);
		log.info("JIRA_BASE_URL: " + JIRA_BASE_URL);
		log.info("JIRA_API_PATH: " + JIRA_API_PATH);
		log.info("ZEPHYR_API_PATH: " + ZEPHYR_API_PATH);
	}

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

	public Project getProject(String projectId) {
		RequestSpecification request = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(JIRA_API_PATH);

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

		String json = response.getBody().asString();

		log.debug("response: " + json);

		try {
			Project project = Project.fromJson(json);
			return project;
		}
		catch (JsonProcessingException e) {
			log.warn("unable to convert json to Project: " + json);
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

	public IssueDetail getIssue(String issueId) {

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

		IssueDetail issueDetail = new IssueDetail();
		issueDetail.id = id;
		issueDetail.key = key;
		issueDetail.self = url;
		issueDetail.summary = summary;

		//TODO: get more details from Issue

		return issueDetail;
	}

	public RequestSpecification jiraRequest() {
		RequestSpecification request = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(JIRA_API_PATH);

		return request.log().all();
	}

	public RequestSpecification zephyrRequest() {

		RequestSpecification request = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_BASE_URL)
				.basePath(ZEPHYR_API_PATH);

		return request.log().all();
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
