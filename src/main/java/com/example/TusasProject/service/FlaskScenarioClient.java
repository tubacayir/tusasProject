package com.example.TusasProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FlaskScenarioClient {

    private final RestTemplate restTemplate;

    @Value("${scenario.generator.url}")
    private String scenarioGeneratorUrl;

    public String generateScenario(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of("prompts", List.of(prompt));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(scenarioGeneratorUrl, request, Map.class);
            Object responses = response.getBody().get("responses");

            if (responses instanceof List<?> list
                    && !list.isEmpty()
                    && list.get(0) instanceof String scenario) {
                return scenario;
            }

            return "API yanıtı boş veya beklenen formatta değil.";

        } catch (Exception exception) {
            return "API Hatası: " + exception.getMessage();
        }
    }
}