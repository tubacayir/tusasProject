package com.example.TusasProject.controller;

import com.example.TusasProject.dto.ScenarioGenerationRequest;
import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Scenario;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.repository.ScenarioRepository;
import com.example.TusasProject.repository.TrendRepository;
import com.example.TusasProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/scenario")
public class ScenarioController {

    @Autowired
    private TrendRepository trendRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ScenarioRepository scenarioRepository;
    @Autowired
    private UserRepository userRepository;


    @PostMapping("/generate")
    @PreAuthorize("hasRole('MANAGER')")
    public String generateScenario(@RequestBody ScenarioGenerationRequest request, Model model) {
        Trend trend = trendRepository.getReferenceById(request.getTrendId());
        List<Driver> drivers = driverRepository.findByTrendId(request.getTrendId());

        // Build key driver text (just first 2)
        StringBuilder driversText = new StringBuilder();
        for (int i = 0; i < Math.min(2, drivers.size()); i++) {
            driversText.append((i + 1)).append(". ").append(drivers.get(i).getDriverName()).append("\n");
        }

        String prompt = """
                Write a well-structured and coherent scenario in a narrative style (not in bullet points) based on the trend and key drivers below. 
                Start with "Scenario:" and follow a natural flow. Do not include explanations or extra details outside the core theme. 
                Keep it focused and insightful.

                Trend: %s

                Key Drivers:
                %s

                Scenario:
                """.formatted(trend.getTrendName(), driversText.toString().trim());

        String flaskUrl = "https://primary-skunk-allowing.ngrok-free.app/generate";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = new HashMap<>();
        payload.put("prompt", prompt);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, entity, Map.class);
            String scenario = (String) response.getBody().get("response");

            model.addAttribute("trend", trend);
            model.addAttribute("scenarioText", scenario);  // Just one scenario text

            return "show-scenario"; // This now uses the updated HTML you translated
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while generating the scenario: " + e.getMessage());
            return "error";
        }
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
        Scenario scenario = scenarioRepository.findByTrendId(trendId);
        model.addAttribute("scenarioText", scenario != null ? scenario.getScenarioText() : null);
        return "show-scenario";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveScenario(@RequestParam Long trendId, @RequestParam String scenarioText, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email); // kullanıcıyı al
        Trend trend = trendRepository.getReferenceById(trendId);
        Scenario scenario1 = scenarioRepository.findByTrendId(trendId);
        if (scenario1 != null) {
            scenarioRepository.deleteById(scenario1.getId());
            saveScenarioDB(scenarioText, trend, user);

        } else {
            saveScenarioDB(scenarioText, trend, user);
        }


        model.addAttribute("trend", trend);
        model.addAttribute("scenarioText", scenarioText);
        model.addAttribute("message", "Scenario saved successfully.");

        return "show-scenario";
    }

    private void saveScenarioDB(String scenarioText, Trend trend, Optional<User> user) {
        Scenario scenario = new Scenario();
        scenario.setTrend(trend);
        scenario.setScenarioText(scenarioText);
        scenario.setUser(user.orElse(null));
        scenario.setIsPublished(false);
        scenarioRepository.save(scenario);
    }

    // ScenarioController.java
    @PostMapping("/publish/{trendId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> publishScenario(@PathVariable Long trendId) {
        Scenario scenario = scenarioRepository.findByTrendId(trendId);
        if (scenario != null) {
            scenario.setIsPublished(true);
            scenarioRepository.save(scenario);
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scenario not found for this trend");
        }
    }

    @GetMapping("/published-scenarios")
    public String getPublishedScenarios(Model model) {
            List<Scenario> publishedScenarios = scenarioRepository.findByIsPublishedTrueOrderByUpdatedAtDesc();

        model.addAttribute("scenarios", publishedScenarios);

        // published-scenarios.html Thymeleaf şablonuna yönlendir
        return "published-scenarios";

    }
}
