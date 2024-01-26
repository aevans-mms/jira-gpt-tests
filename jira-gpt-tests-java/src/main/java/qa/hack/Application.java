package qa.hack;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.hack.jira.JiraApi;
import qa.hack.jira.Project;

import java.util.List;


public class Application {

	static Logger log = LoggerFactory.getLogger(Application.class);
	static Config config = ConfigFactory.load();

	public static void main(String[] args) {
		System.out.println("Extract tests from Jira");

//		log.debug(config.root().render());

		String JIRA_BASE_URL = config.getString("JIRA_BASE_URL");
		log.info("JIRA_URL: " + JIRA_BASE_URL);

		String JIRA_TOKEN = config.getString("JIRA_TOKEN");
		log.info("JIRA_TOKEN: " + JIRA_TOKEN);

		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);

		log.info("get all projects...");
		List<Project> projects = jira.getProjects();
		for (var project : projects) {
			System.out.println("Project: " + project.name);
			System.out.println("Project Key: " + project.key);
			System.out.println("Project Id: " + project.id);
		}

		log.info("get QAUTO project details...");
		Project project = jira.getProject(jira.QAUTO_PROJECT_ID);
		System.out.println("Project Key: " + project.key);
		System.out.println("Project Id: " + project.id);
	}
}