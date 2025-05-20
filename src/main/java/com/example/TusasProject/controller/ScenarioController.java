package com.example.TusasProject.controller;

import com.example.TusasProject.dto.ScenarioGenerationRequest;
import com.example.TusasProject.entity.*;
import com.example.TusasProject.entity.enums.ScenarioType;
import com.example.TusasProject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.example.TusasProject.entity.Driver;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('MANAGER')")
    public String generateScenario(@RequestBody ScenarioGenerationRequest request, Model model) {
        Trend trend = trendRepository.getReferenceById(request.getTrendId());
        List<Driver> allDrivers = new ArrayList<>(driverRepository.findByTrendId(request.getTrendId()));

        // Puanla ve sırala
        allDrivers.sort((o1, o2) -> {
            float impact1 = o1.getImpact() != null ? o1.getImpact() : 0f;
            float uncertainty1 = o1.getUncertainty() != null ? o1.getUncertainty() : 0f;
            float impact2 = o2.getImpact() != null ? o2.getImpact() : 0f;
            float uncertainty2 = o2.getUncertainty() != null ? o2.getUncertainty() : 0f;

            double score1 = (impact1 + uncertainty1) / 2.0;
            double score2 = (impact2 + uncertainty2) / 2.0;

            return Double.compare(score2, score1); // yüksek skor önce
        });

        // Kullanılmış driver'ları takip et
        Set<Long> usedDriverIds = new HashSet<>();

        // Growth: En yüksek skorlu 2 driver
        List<Driver> growthDrivers = allDrivers.stream()
                .filter(d -> !usedDriverIds.contains(d.getId()))
                .limit(2)
                .peek(d -> usedDriverIds.add(d.getId()))
                .collect(Collectors.toList());

        // Collapse: En düşük skorlu 2 driver + negatif polarity driver'lardan kalan ilk 2
        List<Driver> collapseDrivers = allDrivers.stream()
                .filter(d -> !usedDriverIds.contains(d.getId()))
                .sorted((d1, d2) -> {
                    float impact1 = d1.getImpact() != null ? d1.getImpact() : 0f;
                    float uncertainty1 = d1.getUncertainty() != null ? d1.getUncertainty() : 0f;
                    float impact2 = d2.getImpact() != null ? d2.getImpact() : 0f;
                    float uncertainty2 = d2.getUncertainty() != null ? d2.getUncertainty() : 0f;

                    double score1 = (impact1 + uncertainty1) / 2.0;
                    double score2 = (impact2 + uncertainty2) / 2.0;
                    return Double.compare(score1, score2); // düşük skorlular önce
                })
                .filter(d -> d.getPolarity() != null && d.getPolarity() == -1 || true)
                .limit(2)
                .peek(d -> usedDriverIds.add(d.getId()))
                .collect(Collectors.toList());

        // Discipline: yüksek etki, düşük belirsizlik
        List<Driver> disciplineDrivers = allDrivers.stream()
                .filter(d -> !usedDriverIds.contains(d.getId()))
                .filter(d -> {
                    float impact = d.getImpact() != null ? d.getImpact() : 0f;
                    float uncertainty = d.getUncertainty() != null ? d.getUncertainty() : 0f;
                    return impact >= 4.0 && uncertainty <= 2.0;
                })
                .sorted((d1, d2) -> {
                    float score1 = d1.getImpact() - d1.getUncertainty();
                    float score2 = d2.getImpact() - d2.getUncertainty();
                    return Float.compare(score2, score1);
                })
                .limit(2)
                .peek(d -> usedDriverIds.add(d.getId()))
                .collect(Collectors.toList());

        // Transformative: düşük etki, yüksek belirsizlik
        List<Driver> transformativeDrivers = allDrivers.stream()
                .filter(d -> !usedDriverIds.contains(d.getId()))
                .filter(d -> {
                    float impact = d.getImpact() != null ? d.getImpact() : 0f;
                    float uncertainty = d.getUncertainty() != null ? d.getUncertainty() : 0f;
                    return impact <= 2.0 && uncertainty >= 4.0;
                })
                .sorted((d1, d2) -> {
                    float score1 = d1.getUncertainty() - d1.getImpact();
                    float score2 = d2.getUncertainty() - d2.getImpact();
                    return Float.compare(score2, score1);
                })
                .limit(2)
                .peek(d -> usedDriverIds.add(d.getId()))
                .collect(Collectors.toList());


        // Senaryo tiplerine göre eşleştir
        Map<String, List<Driver>> scenarioDriversMap = Map.of(
                "growth", growthDrivers,
                "collapse", collapseDrivers,
                "discipline", disciplineDrivers,
                "transformative", transformativeDrivers
        );

        // Prompt oluşturucu fonksiyon
        Function<String, String> getPromptTemplate = scenarioType -> switch (scenarioType) {
            case "growth" -> """
                    Write a forward-looking, optimistic scenario titled 'Growth' based on the trend and the key drivers listed below.
                    Highlight opportunities, technological advances, and positive developments.
                                
                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;
            case "collapse" -> """
                    Write a critical, cautionary scenario titled 'Collapse' that explores risks, system breakdowns, or negative consequences driven by the factors below.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;
            case "discipline" -> """
                    Write a disciplined and stable scenario titled 'Discipline' emphasizing strong governance, strategic planning, and risk mitigation.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;
            case "transformative" -> """
                    Write a bold, innovative scenario titled 'Transformative' that envisions radical change and disruption sparked by the drivers below.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;
            default -> "";
        };

        // Flask API çağrısı
        String flaskUrl = "https://primary-skunk-allowing.ngrok-free.app/generate";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> generatedScenarios = new LinkedHashMap<>();

        for (Map.Entry<String, List<Driver>> entry : scenarioDriversMap.entrySet()) {
            String scenarioType = entry.getKey();
            List<Driver> scenarioDrivers = entry.getValue();

            if (scenarioDrivers.isEmpty()) {
                generatedScenarios.put(scenarioType, "Yeterli sayıda uygun driver bulunamadı.");
                continue;
            }

            // Driver metni
            String driversText = IntStream.range(0, scenarioDrivers.size())
                    .mapToObj(i -> (i + 1) + ". " + scenarioDrivers.get(i).getDriverName())
                    .collect(Collectors.joining("\n"));

            // Prompt oluştur
            String promptTemplate = getPromptTemplate.apply(scenarioType);
            String prompt = String.format(promptTemplate, trend.getTrendName(), driversText);

            // Flask API'ye gönder
            Map<String, String> payload = Map.of("prompt", prompt);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, entity, Map.class);
                String scenario = (String) response.getBody().get("response");
                generatedScenarios.put(scenarioType, scenario);
            } catch (Exception e) {
                generatedScenarios.put(scenarioType, "API Hatası: " + e.getMessage());
            }
        }

        model.addAttribute("trend", trend);
        model.addAttribute("scenarios", generatedScenarios);
        return "show-scenario";
    }


    @GetMapping("/scenarios/create/{trendId}")
    public String createScenario(@PathVariable Long trendId, Model model) {
        Trend trend = trendRepository.getReferenceById(trendId);
        model.addAttribute("trend", trend);
        return "create-scenario";
    }

    @GetMapping("/scenarios/show/{trendId}")
    public String showScenario(@PathVariable Long trendId, Model model, Principal principal) {

        Trend trend = trendRepository.getReferenceById(trendId);
        model.addAttribute("trend", trend);
        Scenario scenario = scenarioRepository.findByTrendId(trendId);
        model.addAttribute("scenarioText", scenario != null ? scenario.getScenarioText() : null);
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());
            model.addAttribute("userRole", user.getRole().getName());
            model.addAttribute("userExpertise", user.getExpertise());
        }
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
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Scenario not found for this trend");
        }
    }

    @GetMapping("/published-scenarios")
    public String getPublishedScenarios(Model model, Principal principal) {
        List<Scenario> publishedScenarios = scenarioRepository.findByIsPublishedTrueOrderByUpdatedAtDesc();
        User user = userRepository.findByEmail(principal.getName()).orElse(null);

        if (user != null) {
            model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());
            model.addAttribute("userRole", user.getRole().getName());
            model.addAttribute("userExpertise", user.getExpertise());
        }
        model.addAttribute("scenarios", publishedScenarios);

        return "published-scenarios";

    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @RequestParam String content,
                             Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        Scenario scenario = scenarioRepository.findById(id).orElseThrow();
        Comment comment = new Comment();
        comment.setText(content);
        comment.setUser(user);
        comment.setScenario(scenario);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        return "redirect:/api/scenario/published-scenarios";
    }

}