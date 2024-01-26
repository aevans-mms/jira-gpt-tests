package qa.hack;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.hack.jira.model.Issue;
import qa.hack.jira.JiraApi;
import qa.hack.jira.model.Project;
import qa.hack.jira.model.TestStep;

import java.util.List;


public class Application {

	static Logger log = LoggerFactory.getLogger(Application.class);
	static Config config = ConfigFactory.load();

	static String JIRA_TOKEN = System.getenv("JIRA_TOKEN");
	static String JIRA_BASE_URL = System.getenv("JIRA_BASE_URL");

	public static void main(String[] args) {
		System.out.println("Jira API client -- extract information from Jira");

		handleJiraArguments(args);
		handleConfig();
	}

	public static void listProjects() {
		log.info("list all projects...");

		JiraApi jira = getJiraApi();

		List<Project> projects = jira.getProjects();
		for (var project : projects) {
			System.out.println("Project: " + project.name);
			System.out.println("Project Key: " + project.key);
			System.out.println("Project Id: " + project.id);
		}
	}

	public static void getProject(String projectId) {
		System.out.println("Get Project: " + projectId);

		JiraApi jira = getJiraApi();

		Project project = jira.getProject(projectId);
		System.out.println("Project Key: " + project.key);
		System.out.println("Project Id: " + project.id);
		System.out.println("Project URL: " + project.self);
	}

	public static void getIssue(String issueId) {
		System.out.println("Get Issue: " + issueId);

		JiraApi jira = getJiraApi();

		Issue issue = jira.getIssue(issueId);
		System.out.println("Issue Key: " + issue.key);
		System.out.println("Issue Id: " + issue.id);
		System.out.println("Issue URL: " + issue.self);
	}

	public static void getTestDetails(String testId) {
		System.out.println("Get TestDetails: " + testId);

		JiraApi jira = getJiraApi();

		Issue issue = jira.getIssue(testId);
		List<TestStep> testSteps = jira.getTestSteps(issue.id);

		System.out.println(issue);
		for (TestStep testStep: testSteps) {
			System.out.println(testStep);
		}
	}

	public static JiraApi getJiraApi() {
		String JIRA_BASE_URL = config.getString("JIRA_BASE_URL");
		log.debug("JIRA_URL: " + JIRA_BASE_URL);

		String JIRA_TOKEN = config.getString("JIRA_TOKEN");
		log.debug("JIRA_TOKEN: " + JIRA_TOKEN);

		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);

		return jira;
	}

	public static void handleJiraArguments(String[] args) {
		ArgumentParser jiraArgs = ArgumentParsers.newFor("jira").build()
				.defaultHelp(true)
				.description("Extract project, issue, or test details from jira");

//		jiraArgs.addArgument("-j", "--jira-token")
//				.help("Jira token");

		jiraArgs.addArgument("-l", "--list")
				.help("list all projects");

		jiraArgs.addArgument("-p", "--project")
				.help("include project id or ke, e.g. 15000");

		jiraArgs.addArgument("-i", "--issue")
				.help("include issue id or key, e.g. QA-125");

		jiraArgs.addArgument("-t", "--test")
				.help("include test (issue) id, e.g. 12345");

		try {
			Namespace ns = jiraArgs.parseArgs(args);

			String projectId = ns.getString("project");
			String issueId = ns.getString("issue");
			String testId = ns.getString("test");
			String token = ns.getString("token");

			if (projectId != null) {
				getProject(projectId);
			}
			else if (issueId != null) {
				getIssue(issueId);
			}

			else if (testId != null) {
				getTestDetails(testId);
			}
			else {
				System.out.println("Unknown options");
				jiraArgs.printHelp();
			}

			if (token != null) {
				JIRA_TOKEN = token;
			}

		}
		catch (ArgumentParserException e) {
			System.out.println("invalid argument");
			jiraArgs.printHelp();
		}
	}

	public static void handleConfig() {
		if (JIRA_TOKEN != null) {
			config = config.withValue("JIRA_TOKEN", ConfigValueFactory.fromAnyRef(JIRA_TOKEN));
		}

		if (JIRA_BASE_URL != null) {
			config = config.withValue("JIRA_URL", ConfigValueFactory.fromAnyRef(JIRA_BASE_URL));
		}

		log.debug(config.root().render());
	}
}