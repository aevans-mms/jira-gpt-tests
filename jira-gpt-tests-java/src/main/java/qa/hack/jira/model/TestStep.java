package qa.hack.jira.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class TestStep {
	public Integer id;
	public Integer orderId;
	public String step;
	public String data;
	public String result;

	public String createdBy;
	public String modifiedBy;

//	public String htmlStep;
//	public String htmlData;
//	public String htmlResult;

//	public List<Object> attachmentsMap;
//
//	public Object customFields;
//	public Object customFieldValuesMap;

	public int totalStepCount;

	public static TestStep fromJson(String json) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(json, TestStep.class);
	}

	public String toJson() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return String.format(
					"""
					{
						"orderId": "%s",
						"step": "%s",
						"data": "%s",
						"result": "%s"
					}		
					"""
					, orderId, step, data, result);
		}
	}

	public String toString() {
		return String.format(
		"""
		Step: %s
		--------,
		actions: %s
		data: %s 
		expected result: %s 
		"""
		, orderId, step, data, result);
	}
}
