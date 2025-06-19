package com.example.TusasProject.controller;

import com.example.TusasProject.entity.Scenario;
import com.example.TusasProject.repository.ScenarioRepository;
import com.example.TusasProject.service.ImageGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/image")
public class ImageGenerationController {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private ImageGenerationService imageGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateImage(@RequestBody Map<String, String> request) {
        String scenarioId = request.get("scenarioId");

        Optional<Scenario> optionalScenario = scenarioRepository.findById(Long.valueOf(scenarioId));
        if (optionalScenario.isEmpty()) {
            return ResponseEntity.badRequest().body("Scenario not found.");
        }

        Scenario scenario = optionalScenario.get();

        String imageBase64 = imageGenerationService.generateImageForScenario(scenario.getScenarioText());

        if (imageBase64 == null) {
            return ResponseEntity.status(500).body("Image generation failed.");
        }

        String dataUrl = "data:image/png;base64," + imageBase64;
        scenario.setImageUrl(dataUrl);
        scenarioRepository.save(scenario);

        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", dataUrl);
        response.put("scenarioText", scenario.getScenarioText());

        return ResponseEntity.ok(response);
    }
}
