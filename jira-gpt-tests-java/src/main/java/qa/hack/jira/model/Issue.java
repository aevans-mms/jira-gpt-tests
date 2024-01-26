package qa.hack.jira.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Issue {
	public String expand;
	public String id;
	public String self;
	public String key;
	public Map<String, Object> fields;
	public String summary;

	// TODO exapand fields;

	public static Issue fromJson(String json) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(json, Issue.class);
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return String.format(
		            """
					{
						"id": "%s",
						"key": "%s",
						"url": "%s", 
						"summary": "%s"
					}		
					"""
					, id, self, key, summary);
		}
	}

	public String toString() {
		return String.format(
		"""
		Issue
		-----
		id: %s,
		key: %s,
		url: %s, 
		summary: %s 
		"""
		, id, key, self, summary);
	}
}
