
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
            request.put("name", "Pooja Singh");
            request.put("regNo", "1RF22CS080");
            request.put("email", "poojasingh8246@gmail.com");

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getBody() == null) {
                System.out.println("No response received from generateWebhook.");
                return;
            }

            String webhookUrl = (String) response.getBody().get("webhook");
            String accessToken = (String) response.getBody().get("accessToken");

            if (webhookUrl == null || accessToken == null) {
                System.out.println("Missing webhook or accessToken in response.");
                return;
            }

            String regNo = request.get("regNo");
            int lastTwoDigits = Integer.parseInt(regNo.replaceAll("\\D+", "")) % 100;

            String finalQuery;

            if (lastTwoDigits % 2 == 1) {
                finalQuery =
                        "SELECT p.AMOUNT AS SALARY, " +
                        "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                        "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                        "d.DEPARTMENT_NAME " +
                        "FROM PAYMENTS p " +
                        "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                        "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                        "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
                        "ORDER BY p.AMOUNT DESC " +
                        "LIMIT 1;";
            } else {
                finalQuery =
                        "SELECT e1.EMP_ID, " +
                        "       e1.FIRST_NAME, " +
                        "       e1.LAST_NAME, " +
                        "       d.DEPARTMENT_NAME, " +
                        "       COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT " +
                        "FROM EMPLOYEE e1 " +
                        "JOIN DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID " +
                        "LEFT JOIN EMPLOYEE e2 " +
                        "       ON e1.DEPARTMENT = e2.DEPARTMENT " +
                        "      AND e2.DOB > e1.DOB " +
                        "GROUP BY e1.EMP_ID, e1.FIRST_NAME, e1.LAST_NAME, d.DEPARTMENT_NAME " +
                        "ORDER BY e1.EMP_ID DESC;";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            Map<String, String> body = new HashMap<>();
            body.put("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> submitResponse =
                    restTemplate.postForEntity(webhookUrl, entity, String.class);

            System.out.println("Submission response: " + submitResponse.getBody());

        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
