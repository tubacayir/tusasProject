package com.example.TusasProject.controller;

import com.example.TusasProject.dto.ScenarioGenerationRequest;
import com.example.TusasProject.service.ScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scenario")
public class ScenarioController {

    @Autowired
    private ScenarioService scenarioService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('MANAGER')")  // sadece manager erişebilir
    public ResponseEntity<String> generateScenario(@RequestBody ScenarioGenerationRequest request) {
        String result = scenarioService.generateScenario(request.getTrendId());
        return ResponseEntity.ok(result);
    }
}
