package qa.hack.jira;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JiraTestExtractor_Tests {

	Logger log;
	Config config;

	String JIRA_URL;
	String JIRA_TOKEN;

	public JiraTestExtractor_Tests() {
		log = LoggerFactory.getLogger(this.getClass());
		config = ConfigFactory.load();

		JIRA_URL = config.getString("JIRA_URL");
		JIRA_TOKEN= config.getString("JIRA_TOKEN");
	}

	@Test
	public void test_getProjects() {

		JiraTestExtractor jira = new JiraTestExtractor(JIRA_URL, JIRA_TOKEN);

		Response response = jira.getProjects();
		List<Object> list = response.jsonPath().getList(".");
		System.out.println("projects: " + list.size());

	}

	@Test
	public void test_getComponentsFromProject() {
		var jira = new JiraTestExtractor(JIRA_URL, JIRA_TOKEN);
		List<Component> components = jira.getProjectComponents("QAUTO");
		System.out.println(components.size());
		System.out.println(components);
	}

	@Test
	public void test_getIssue() {
		JiraTestExtractor jira = new JiraTestExtractor(JIRA_URL, JIRA_TOKEN);
		Response response = jira.getIssue("QAUTO-1234");
		System.out.println(response.getBody().asPrettyString());
	}

}