package com.example.TusasProject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageGenerationService {

    @Value("${stability.api.key}")
    private String stabilityApiKey;

    private final RestTemplate restTemplate;

    public ImageGenerationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateImageForScenario(String scenarioText) {
        String prompt = "futuristic, highly detailed, concept art style, digital painting of: " + scenarioText;

        String apiUrl = "https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(stabilityApiKey);
        headers.set("Accept", "application/json");

        Map<String, Object> payload = payloadConfig(prompt);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<?> dataList = (List<?>) response.getBody().get("artifacts");
                if (dataList != null && !dataList.isEmpty()) {
                    Map<String, Object> imageData = (Map<String, Object>) dataList.get(0);
                    return imageData.get("base64").toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Map<String, Object> payloadConfig(String prompt) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("text_prompts", List.of(Map.of("text", prompt)));
        payload.put("cfg_scale", 7);
        payload.put("clip_guidance_preset", "FAST_BLUE");
        payload.put("height", 1024);
        payload.put("width", 1024);
        payload.put("samples", 1);
        payload.put("steps", 30);
        return payload;
    }


}
