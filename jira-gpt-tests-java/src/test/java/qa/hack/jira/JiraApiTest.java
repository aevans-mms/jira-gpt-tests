package qa.hack.jira;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JiraApiTest {

	Logger log;
	Config config;

	String JIRA_TOKEN;
	String JIRA_BASE_URL;
	String JIRA_API_PATH;
	String ZEPHYR_API_PATH;

	public JiraApiTest() {
		log = LoggerFactory.getLogger(this.getClass());
		config = ConfigFactory.load();

		JIRA_BASE_URL = config.getString("JIRA_BASE_URL");
		JIRA_API_PATH = config.getString("JIRA_API_PATH");
		JIRA_TOKEN = config.getString("JIRA_TOKEN");
		ZEPHYR_API_PATH = config.getString("ZEPHYR_API_PATH");

		log.info("JIRA_TOKEN: " + JIRA_TOKEN);
		log.info("JIRA_BASE_URL: " + JIRA_BASE_URL);
		log.info("JIRA_API_PATH: " + JIRA_API_PATH);
		log.info("ZEPHYR_API_PATH: " + ZEPHYR_API_PATH);
	}
}
