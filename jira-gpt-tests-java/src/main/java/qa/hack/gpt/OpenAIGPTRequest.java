package qa.hack.gpt;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OpenAIGPTRequest {

    public static void main(String[] args) throws Exception {
        RestAssured.baseURI = "https://api.openai.com/v1/chat/completions";
        String apiKey = args[0];



        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "What is DNS?");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(userMessage));
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 50);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String jsonRequestBody = objectMapper.writeValueAsString(requestBody);

        Response gptResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .log().all()
                .body(jsonRequestBody)
                .post();


        System.out.println("Status code is: " + gptResponse.getStatusCode());
        Object jsonResponse = objectMapper.readValue(gptResponse.getBody().asString(), Object.class);
        String prettyJsonResponse = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse);
        System.out.println(prettyJsonResponse);
    }
}
