package com.example.TusasProject.controller;

import com.example.TusasProject.dto.ScenarioGenerationRequest;
import com.example.TusasProject.entity.*;
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
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                    return impact >= 3.0 && uncertainty <= 2.0;
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
                    return impact <= 2.0 && uncertainty >= 3.0;
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
                    Write a forward-looking and coherent narrative scenario titled 'Growth' that envisions a future shaped by positive trends and impactful drivers. Do not use bullet points or lists; instead, write a smooth.Write in a clear and uninterrupted narrative form without using lists.y.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;

            case "collapse" -> """
                    Write a cautionary and realistic narrative scenario titled 'Collapse' that explores a future where key systems deteriorate due to critical challenges and risks. Focus on negative consequences and potential failures, but do not use bullet points or lists. Write in a clear and uninterrupted narrative form without using lists..

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;

            case "discipline" -> """
                    Write a structured and stable narrative scenario titled 'Discipline' where the future is shaped by strong governance, regulation, and responsible decision-making. Emphasize strategic planning, order, and resilience. Write in a clear and uninterrupted narrative form without using lists.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;

            case "transformative" -> """
                    Write a bold and visionary narrative scenario titled 'Transformative' that imagines a future defined by radical innovation and disruptive change. Highlight how the following drivers contribute to a dramatic transformation of systems and society. Write in a clear and uninterrupted narrative form without using lists.

                    Trend: %s

                    Key Drivers:
                    %s

                    Scenario:
                    """;

            default -> "";
        };

        // Flask API çağrısı
        String flaskUrl = "https://e1df-34-143-203-58.ngrok-free.app/generate";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> generatedScenarios = new LinkedHashMap<>();
// Her senaryo tipi için ayrı ayrı prompt oluştur ve Flask API'ye gönder

        for (String scenarioType : List.of("growth", "collapse", "discipline", "transformative")) {
            List<Driver> scenarioDrivers = scenarioDriversMap.get(scenarioType);
            if (scenarioDrivers == null || scenarioDrivers.isEmpty()) {
                generatedScenarios.put(scenarioType, "Yeterli sayıda uygun driver bulunamadı.");
                continue;
            }

            String driversText = scenarioDrivers.stream()
                    .map(d -> {
                        String name = d.getDriverName();
                        String category = d.getDriverCategory() != null ? "(" + d.getDriverCategory() + ")" : "";
                        String impact = d.getImpact() != null ? "Impact Rate: " + d.getImpact().intValue() : "";
                        String uncertainty = d.getUncertainty() != null ? "Uncertainty: " + d.getUncertainty().intValue() : "";
                        String polarity = d.getPolarity() != null
                                ? (d.getPolarity() == 1 ? "positive polarity"
                                : d.getPolarity() == -1 ? "negative polarity"
                                : "neutral polarity")
                                : "";
                        return String.format("%s %s [%s, %s, %s]",
                                name,
                                category,
                                impact,
                                uncertainty,
                                polarity);
                    })
                    .collect(Collectors.joining("; "));

            String promptTemplate = getPromptTemplate.apply(scenarioType);
            String prompt = String.format(promptTemplate, trend.getTrendName(), driversText);

            Map<String, Object> payload = Map.of("prompts", List.of(prompt));
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, entity, Map.class);
                Object body = response.getBody().get("responses");

                if (body instanceof List<?> rawList && !rawList.isEmpty() && rawList.get(0) instanceof String scenario) {
                    generatedScenarios.put(scenarioType, scenario);
                } else {
                    generatedScenarios.put(scenarioType, "API yanıtı boş veya beklenen formatta değil.");
                }
            } catch (Exception e) {
                generatedScenarios.put(scenarioType, "API Hatası: " + e.getMessage());
            }
        }



        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email); // kullanıcıyı al
        Scenario scenario = scenarioRepository.findByTrendId(trend.getId());
        if (scenario != null) {
            scenarioRepository.deleteById(scenario.getId());
        }
        saveScenarioDB(generatedScenarios.get("growth"), trend, user, "growth");
        saveScenarioDB(generatedScenarios.get("collapse"), trend, user, "collapse");
        saveScenarioDB(generatedScenarios.get("transformative"), trend, user, "transformative");
        saveScenarioDB(generatedScenarios.get("discipline"), trend, user, "discipline");
        if (user.isPresent()) {
            model.addAttribute("userName", user.get().getFirstName() + " " + user.get().getLastName());
            model.addAttribute("userRole", user.get().getRole().getName());
            model.addAttribute("userExpertise", user.get().getExpertise());
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
        Map<String, String> generatedScenarios = new LinkedHashMap<>();
        model.addAttribute("trend", trend);
        List<Scenario> scenarioList = scenarioRepository.findAllByTrendId(trendId);
        scenarioList.forEach(scenario1 -> generatedScenarios.put(scenario1.getScenarioType(), scenario1.getScenarioText()));

        User user = userRepository.findByEmail(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());
            model.addAttribute("userRole", user.getRole().getName());
            model.addAttribute("userExpertise", user.getExpertise());
        }
        if (generatedScenarios.keySet().isEmpty()) {
            generatedScenarios.put("growth", "Scenario not generated yet!");
            generatedScenarios.put("collapse", "Scenario not generated yet!");
            generatedScenarios.put("transformative", "Scenario not generated yet!");
            generatedScenarios.put("discipline", "Scenario not generated yet!");
        }
        model.addAttribute("scenarios", generatedScenarios);
        model.addAttribute("trend", trend);
        return "show-scenario";
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('MANAGER')")
    public String saveScenario(@RequestParam Long trendId, @RequestParam String scenarioText,
                               @RequestParam String type,
                               Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email); // kullanıcıyı al
        Trend trend = trendRepository.getReferenceById(trendId);
        Map<String, String> generatedScenarios = new LinkedHashMap<>();
        Scenario scenario = scenarioRepository.findByTrendIdAndScenarioType(trendId, type);
        List<Scenario> scenarioList = scenarioRepository.findAllByTrendId(trendId);
        if (scenario != null) {
            // Var olan senaryoyu güncelle
            scenarioRepository.deleteById(scenario.getId());
        }

        saveScenarioDB(scenarioText, trend, user, type); // type'ı da gönderiyoruz
        scenarioList.forEach(scenario1 -> generatedScenarios.put(scenario1.getScenarioType(), scenario1.getScenarioText()));
        model.addAttribute("scenarios", generatedScenarios);
        model.addAttribute("trend", trend);
        model.addAttribute("message", "Scenario saved successfully.");
        return "show-scenario";
    }

    private void saveScenarioDB(String scenarioText, Trend trend, Optional<User> user, String type) {
        Scenario scenario = new Scenario();
        scenario.setTrend(trend);
        scenario.setScenarioText(scenarioText);
        scenario.setUser(user.orElse(null));
        scenario.setIsPublished(false);
        scenario.setScenarioType(type);
        scenarioRepository.save(scenario);
    }

    // ScenarioController.java
    @PostMapping("/publish/{trendId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> publishScenario(@PathVariable Long trendId, @RequestParam String type
    ) {
        Scenario scenario = scenarioRepository.findByTrendIdAndScenarioType(trendId, type);
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