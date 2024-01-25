package qa.hack.jira;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JiraTestExtractor {

	Logger log = LoggerFactory.getLogger(JiraTestExtractor.class);

	public String JIRA_URL;
	public String JIRA_TOKEN;
	public String JIRA_API_PATH = "/rest/api/latest";

	public String PROJECT_ID = "15000";
	public JiraTestExtractor(String JIRA_URL, String JIRA_TOKEN) {
		log.debug("creating instance");
		this.JIRA_URL = JIRA_URL;
		this.JIRA_TOKEN = JIRA_TOKEN;
		log.debug("JIRA_URL: " + this.JIRA_URL);
		log.debug("JIRA_TOKEN: " + this.JIRA_TOKEN);
	}

	public Response getProjects() {
		var request = jiraRequest();
		var response = RestAssured.given(request).get("/project");

		log.info("getProjects response: " + response.getBody().asPrettyString());
		return response;
	}

	public Response getProject(String projectId) {
		RequestSpecification request = RestAssured.given()
				.header("Authorization", "Bearer " + JIRA_TOKEN)
				.baseUri(JIRA_URL)
				.basePath(JIRA_API_PATH);

		Response response = request
				.pathParam("projectId", "15000")
				.get("/project/{projectId}");

		QueryableRequestSpecification queryable = SpecificationQuerier.query(request);
		System.out.println(queryable.getMethod());
		System.out.println(queryable.getURI());
		System.out.println(queryable.getHeaders());

		System.out.println(response.getStatusCode());
		System.out.println(response.getBody().asPrettyString());

		return response;
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

	public Response getIssue(String issueId) {
		var response = RestAssured.given(jiraRequest())
				.pathParam("issueId", issueId)
				.get("/issue/{issueId}");

		log.info("getIssue response: " + response.statusCode() + "\n" + response.getBody().asPrettyString());
		return response;
	}

	public RequestSpecification jiraRequest() {
		RequestSpecBuilder spec = new RequestSpecBuilder();
		spec.setBaseUri(JIRA_URL);
		spec.setBasePath(JIRA_API_PATH);
		spec.addHeader("Authorization", "Bearer " + JIRA_TOKEN);

		RequestSpecification request = spec.build().log().all();

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
