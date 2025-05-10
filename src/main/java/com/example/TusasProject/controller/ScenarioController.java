package com.example.TusasProject.controller;

import com.example.TusasProject.dto.ScenarioGenerationRequest;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.repository.TrendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/scenario")
public class ScenarioController {

  //  @Autowired
  //  private ScenarioService scenarioService;

    @Autowired
    private TrendRepository trendRepository;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('MANAGER')")  // sadece manager erişebilir
    public ResponseEntity<String> generateScenario(@RequestBody ScenarioGenerationRequest request) {
       // String result = scenarioService.generateScenario(request.getTrendId());
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/scenarios/create/{trendId}")
    public String createScenario(@PathVariable Long trendId, Model model) {
        Trend trend = trendRepository.getReferenceById(trendId);
        model.addAttribute("trend", trend);
        return "create-scenario";
    }

    @GetMapping("/scenarios/show/{trendId}")
    public String showScenario(@PathVariable Long trendId, Model model) {
        Trend trend = trendRepository.getReferenceById(trendId);
        model.addAttribute("trend", trend);

        model.addAttribute("scenarioA", "Rapid digitalization and global AI adoption drive positive trends.");
        model.addAttribute("scenarioB", "Technology improves but trust and governance collapse.");
        model.addAttribute("scenarioC", "Regression in innovation and increasing global conflicts.");
        model.addAttribute("scenarioD", "Social awareness increases despite economic stagnation.");

        return "show-scenario";
    }
}
