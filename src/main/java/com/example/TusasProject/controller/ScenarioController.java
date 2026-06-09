package com.example.TusasProject.controller;

import com.example.TusasProject.dto.ScenarioGenerationRequest;
import com.example.TusasProject.repository.TrendRepository;
import com.example.TusasProject.service.ScenarioService;
import com.example.TusasProject.service.UserModelService;
import com.example.TusasProject.service.records.ScenarioGenerationResult;
import com.example.TusasProject.service.records.ScenarioViewResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/api/scenario")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final UserModelService userModelService;
    private final TrendRepository trendRepository;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('MANAGER')")
    public String generateScenario(@RequestBody ScenarioGenerationRequest request, Model model, Principal principal) {

        ScenarioGenerationResult result = scenarioService.generateAndSaveScenarios(request.getTrendId(), principal.getName());

        userModelService.addUserInfoToModel(model, principal.getName());

        model.addAttribute("trend", result.trend());
        model.addAttribute("scenarios", result.scenarios());

        return "show-scenario";
    }

    @GetMapping("/scenarios/create/{trendId}")
    public String createScenario(@PathVariable Long trendId, Model model) {
        model.addAttribute("trend", trendRepository.getReferenceById(trendId));
        return "create-scenario";
    }

    @GetMapping("/scenarios/show/{trendId}")
    public String showScenario(@PathVariable Long trendId, Model model, Principal principal) {

        ScenarioViewResult result = scenarioService.getScenariosByTrend(trendId);

        userModelService.addUserInfoToModel(model, principal.getName());

        model.addAttribute("trend", result.trend());
        model.addAttribute("scenarios", result.scenarios());

        return "show-scenario";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveScenario(@RequestParam Long trendId, @RequestParam String scenarioText, @RequestParam String type, Model model, Principal principal) {

        ScenarioViewResult result = scenarioService.saveScenario(trendId, scenarioText, type, principal.getName());

        userModelService.addUserInfoToModel(model, principal.getName());

        model.addAttribute("trend", result.trend());
        model.addAttribute("scenarios", result.scenarios());
        model.addAttribute("message", "Scenario saved successfully.");

        return "show-scenario";
    }

    @PostMapping("/publish/{trendId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<String> publishScenario(@PathVariable Long trendId, @RequestParam String type) {
        scenarioService.publishScenario(trendId, type);
        return ResponseEntity.ok("Scenario published successfully.");
    }

    @GetMapping("/published-scenarios")
    public String getPublishedScenarios(Model model, Principal principal) {
        userModelService.addUserInfoToModel(model, principal.getName());
        model.addAttribute("scenarios", scenarioService.getPublishedScenarios());
        return "published-scenarios";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam String content, Principal principal) {
        scenarioService.addComment(id, content, principal.getName());
        return "redirect:/api/scenario/published-scenarios";
    }
}