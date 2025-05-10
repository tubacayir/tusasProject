package com.example.TusasProject.service;
import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ScenarioService {

    @Autowired
    private DriverRepository driverRepository;  // Bu repo üzerinden veritabanına erişeceğiz

    private final String HF_TOKEN = "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";  // Hugging Face Token
    private final String MODEL_URL = "https://huggingface.co/tubacayir/generator";

    public String generateScenario(Long trendId) {
        List<Driver> ratings = driverRepository.findByTrendId(trendId);

        StringBuilder prompt = new StringBuilder("Trend ID: " + trendId + " için driver puanları:\n");

        for (Driver rating : ratings) {
            prompt.append(String.format("Driver %s: Impact = %d, Uncertainty = %d\n",
                    rating.getDriverName(), rating.getImpact(), rating.getUncertainty()));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(HF_TOKEN);

        Map<String, String> payload = Map.of("inputs", prompt.toString());
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(MODEL_URL, requestEntity, String.class);

        return response.getBody();
    }
}
