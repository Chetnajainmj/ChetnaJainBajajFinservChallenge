package com.example.webhook.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void executeWorkflow() throws Exception {
        // Step 1: Generate Webhook
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Chetna Jain");
        requestBody.put("regNo", "0827CI221046"); 
        requestBody.put("email", "chetnajain220293@acropolis.in");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(generateUrl, request, String.class);
        JsonNode json = objectMapper.readTree(response.getBody());

        String webhookUrl = json.get("webhook").asText();
        String accessToken = json.get("accessToken").asText();

        // Step 2: Build Final Query
        String finalQuery = "SELECT P.AMOUNT AS SALARY, " +
                "CONCAT(E.FIRST_NAME, ' ', E.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, E.DOB, CURDATE()) AS AGE, " +
                "D.DEPARTMENT_NAME " +
                "FROM PAYMENTS P " +
                "JOIN EMPLOYEE E ON P.EMP_ID = E.EMP_ID " +
                "JOIN DEPARTMENT D ON E.DEPARTMENT = D.DEPARTMENT_ID " +
                "WHERE DAY(P.PAYMENT_TIME) != 1 " +
                "ORDER BY P.AMOUNT DESC " +
                "LIMIT 1";

        // Step 3: Submit Final Query
        String testUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.set("Authorization", accessToken);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> queryRequest = new HttpEntity<>(queryMap, authHeaders);
        ResponseEntity<String> queryResponse = restTemplate.postForEntity(testUrl, queryRequest, String.class);

        System.out.println("Submission Response: " + queryResponse.getBody());
    }
}




