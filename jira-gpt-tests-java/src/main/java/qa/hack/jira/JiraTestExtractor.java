package qa.hack.jira;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraTestExtractor {

	Logger log = LoggerFactory.getLogger(JiraTestExtractor.class);

	public String JIRA_URL;
	public String JIRA_TOKEN;

	public String PROJECT_ID;
	public JiraTestExtractor(String JIRA_URL, String JIRA_TOKEN) {
		log.debug("creating instance");
		this.JIRA_URL = JIRA_URL;
		this.JIRA_TOKEN = JIRA_TOKEN;
	}

	public void doit() {
		log.info("authenticating");

		RequestSpecBuilder request = new RequestSpecBuilder();
		request.setBaseUri(JIRA_URL);
		request.set

	}


}
