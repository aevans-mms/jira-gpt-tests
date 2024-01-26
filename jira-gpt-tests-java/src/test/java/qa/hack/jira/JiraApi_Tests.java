package qa.hack.jira;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JiraApi_Tests extends JiraApiTest {

	@Test
	public void test_getProjects() {
		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);

		List<Project> projects = jira.getProjects();

		projects.forEach(project -> {
			log.info(project.id);
			log.info(project.key);
			log.info(project.name);
		});

		assertThat(projects.size()).isGreaterThan(70);
	}

	@Test
	public void test_getProject() {
		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);

		String projectId = "15000";
		Project project = jira.getProject(projectId);
	}

//	@Test
//	public void test_getComponentsFromProject() {
//		var jira = new JiraApi(JIRA_URL, JIRA_TOKEN);
//		List<Component> components = jira.getProjectComponents("QAUTO");
//		System.out.println(components.size());
//		System.out.println(components);
//	}

	@Test
	public void test_getIssue() {
		JiraApi jira = new JiraApi(JIRA_BASE_URL, JIRA_TOKEN);
		jira.getIssue("QAUTO-748");
	}

}