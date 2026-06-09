package com.example.TusasProject.service;

import com.example.TusasProject.entity.Scenario;
import com.example.TusasProject.repository.ScenarioRepository;
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
import java.util.Optional;

@Service
public class ImageGenerationService {

    @Value("${stability.api.key}")
    private String stabilityApiKey;

    private final String API_URL = "https://api.stability.ai/v1/generation/stable-diffusion-xl-1024-v1-0/text-to-image";

    private final RestTemplate restTemplate;

    private final ScenarioRepository scenarioRepository;

    public ImageGenerationService(RestTemplate restTemplate, ScenarioRepository scenarioRepository) {
        this.restTemplate = restTemplate;
        this.scenarioRepository = scenarioRepository;
    }

    public String generateImageForScenario(String scenarioText) {
        String prompt = "futuristic, highly detailed, concept art style, digital painting of: " + scenarioText;

        HttpHeaders headers = createHttpHeaders();
        Map<String, Object> payload = payloadConfig(prompt);

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

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

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(stabilityApiKey);
        headers.set("Accept", "application/json");
        return headers;
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


    public ResponseEntity<?> generateImage(Map<String, String> request) {
        String scenarioId = request.get("scenarioId");
        Optional<Scenario> optionalScenario = scenarioRepository.findById(Long.valueOf(scenarioId));
        if (optionalScenario.isEmpty()) {
            return ResponseEntity.badRequest().body("Scenario not found.");
        }

        Scenario scenario = optionalScenario.get();

        String imageBase64 = generateImageForScenario(scenario.getScenarioText());
        if (imageBase64 == null) {
            return ResponseEntity.status(500).body("Image generation failed.");
        }

        String dataUrl = "data:image/png;base64," + imageBase64;
        scenario.setImageUrl(dataUrl);
        scenarioRepository.save(scenario);

        Map<String, String> response = createResponseMap(dataUrl, scenario);

        return ResponseEntity.ok(response);
    }

    private static Map<String, String> createResponseMap(String dataUrl, Scenario scenario) {
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", dataUrl);
        response.put("scenarioText", scenario.getScenarioText());
        return response;
    }
}
