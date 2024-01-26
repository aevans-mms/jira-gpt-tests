package qa.hack.jira;

import org.junit.jupiter.api.Test;
import qa.hack.jira.model.Issue;
import qa.hack.jira.model.Project;
import qa.hack.jira.model.TestStep;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JiraApi_Tests extends JiraApiTestBase {

	@Test
	public void test_getProjects() {
		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);

		List<Project> projects = jira.getProjects();

		projects.forEach(project -> {
			System.out.println(project);
			System.out.println();
		});

		System.out.println("projects: " + projects.size());

		assertThat(projects.size()).isGreaterThan(70);
	}

	@Test
	public void test_getProject() {
		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);

		String projectId = "15000";
		Project project = jira.getProject(projectId);

		System.out.println(project);
		System.out.println(project.toJson());

		assertThat(project).isNotNull();
		assertThat(project.id).isNotEmpty();
	}


	@Test
	public void test_getIssue() {
		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);

		String issueKey = "QAUTO-748";
		Issue issue = jira.getIssue(issueKey);

		System.out.println(issue);
		System.out.println(issue.toJson());

		assertThat(issue).isNotNull();
		assertThat(issue.id).isNotEmpty();
	}


	@Test
	public void test_getTestSteps() {
		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);

		String testKey = "QAUTO-748";
		Issue issue = jira.getIssue(testKey);
		log.debug("got issue: " + issue);

		String testId = "221525";
		List<TestStep> testSteps = jira.getTestSteps(testId);

		for (TestStep testStep: testSteps) {
			System.out.println(testStep);
//			System.out.println(testStep.toJson());

			assertThat(testStep).isNotNull();
			assertThat(testStep.id).isNotNull();
			assertThat(testStep.orderId).isPositive();
		}
	}
}