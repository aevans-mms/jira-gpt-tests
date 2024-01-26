package qa.hack.jira.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Project {
	public String expand;
	public String self;
	public String id;
	public String key;
	public String name;
	public Map<String, String> avatarUrls;
	public Map<String, String> projectCategory;
	public String projectTypeKey;
	public Boolean archived;

	public static Project fromJson(String json) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(json, Project.class);
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
				"name": "%s"
			}		
			"""
			, id, key, self, name);
		}
	}

	public String toString() {
		return String.format(
		"""
		Project
		-------
		id: %s,
		key: %s,
		url: %s, 
		name: %s 
		"""
		, id, key, self, name);
	}
}
