package com.example.webhook;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebhookApp implements CommandLineRunner {

    private final WebhookService webhookService;

    public WebhookApp(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebhookApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        webhookService.executeFlow();
    }
}
