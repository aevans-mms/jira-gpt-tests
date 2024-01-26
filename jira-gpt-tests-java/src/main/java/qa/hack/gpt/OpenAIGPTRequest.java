package qa.hack.gpt;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import qa.hack.jira.JiraApi;
import qa.hack.jira.model.Issue;
import qa.hack.jira.model.TestStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OpenAIGPTRequest {

    public static void main(String[] args) throws Exception {
        RestAssured.baseURI = "https://api.openai.com/v1/chat/completions";
        String apiKey = args[0];


        JiraApi jira = new JiraApi("https://jira.mms.org", args[1]);
        Issue issue = jira.getIssue("QAUTO-1234");
        List<TestStep> steps = jira.getTestSteps(issue.id);

        StringBuilder prompt = new StringBuilder();

        prompt.append("Given the following test case, create a detailed summary of the test case don't add extra or repetitive information try to conserve token.\n");

        prompt.append("summary " + issue.summary+"\n");


        for (var step : steps) {
            prompt.append(step);
        }

        System.out.println("This is the prompt: " + prompt+ "\n");


        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt.toString());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(userMessage));
        requestBody.put("temperature", 0.1);
//        requestBody.put("max_tokens", 100);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        Response gptResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body(jsonRequestBody)
                .post();


        System.out.println("Status code is: " + gptResponse.getStatusCode());
        System.out.println("GPT response: "+gptResponse.jsonPath().getString("choices.message.content"));
        Object jsonResponse = objectMapper.readValue(gptResponse.getBody().asString(), Object.class);
        String prettyJsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse);
        System.out.println();
    }
}
