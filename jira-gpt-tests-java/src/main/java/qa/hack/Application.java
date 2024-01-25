package qa.hack;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.hack.jira.JiraTestExtractor;


public class Application {

	static Logger log = LoggerFactory.getLogger(Application.class);
	static Config config = ConfigFactory.load();

	public static void main(String[] args) {
		System.out.println("Extract tests from Jira");

		log.debug(config.root().render());

		String JIRA_URL = config.getString("JIRA_URL");
		log.info("JIRA_URL: " + JIRA_URL);

		String JIRA_TOKEN = config.getString("JIRA_TOKEN");
		log.info("JIRA_TOKEN: " + JIRA_TOKEN);

		JiraTestExtractor jira = new JiraTestExtractor(JIRA_URL, JIRA_TOKEN);
	}
}