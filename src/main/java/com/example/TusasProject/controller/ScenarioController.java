package com.example.TusasProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ScenarioController {

    private String lastGeneratedScenario = "Henüz senaryo oluşturulmadı.";

    @GetMapping("/scenario")
    public String showForm(Model model) {
        model.addAttribute("generatedScenario", lastGeneratedScenario);
        return "scenario";
    }

    @PostMapping("/generate-scenario")
    public String generateScenario(Model model) {
        // Gerçek senaryo oluşturma yerine örnek bir senaryo metni
        lastGeneratedScenario = "Gelecekte yapay zeka, şehir planlamasında önemli rol oynayacak.";
        model.addAttribute("generatedScenario", lastGeneratedScenario);
        return "scenario";
    }

    @PostMapping("/rate-scenario")
    public String rateScenario(@RequestParam("rating") int rating, Model model) {
        // Ratingi kaydedebilir ya da loglayabilirsiniz
        System.out.println("Senaryo Puanı: " + rating);
        model.addAttribute("generatedScenario", lastGeneratedScenario);
        return "scenario";
    }
}
