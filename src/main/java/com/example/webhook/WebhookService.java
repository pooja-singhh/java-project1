package com.example.webhook;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {
    private final RestTemplate restTemplate = new RestTemplate();

    public void executeFlow() {
        try {
            
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> request = new HashMap<>();
            request.put("name", "Pooja Singh P Rajput");      
            request.put("regNo", "1RF22CS080");          
            request.put("email", "poojasingh8246@email.com");     

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getBody() == null) {
                System.out.println("No response received!");
                return;
            }

            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");
            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            String finalQuery = "SELECT e.employee_id, e.first_name, e.last_name, " +
                    "d.department_name, m.first_name AS manager_first_name, m.last_name AS manager_last_name " +
                    "FROM employees e " +
                    "LEFT JOIN departments d ON e.department_id = d.department_id " +
                    "LEFT JOIN employees m ON e.manager_id = m.employee_id " +
                    "WHERE d.location_id = 1700 " +
                    "ORDER BY e.last_name, e.first_name;";


            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, String> body = new HashMap<>();
            body.put("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, entity, String.class);

            System.out.println("Submission Response: " + submitResponse.getBody());

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
