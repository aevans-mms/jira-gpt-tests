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

import java.util.List;

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

		RequestSpecification request = RestAssured.given().log().all()
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

}
