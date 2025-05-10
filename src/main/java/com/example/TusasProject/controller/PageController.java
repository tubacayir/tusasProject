package com.example.TusasProject.controller;

import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.repository.TrendRepository;
import com.example.TusasProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

;


@Controller
public class PageController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TrendRepository trendRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/panel")
    public String panelPage(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElse(null);

        if (user != null) {
            model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());
            model.addAttribute("userRole", user.getRole().getName());
            model.addAttribute("userExpertise", user.getExpertise());
        }

        return "panel"; // src/main/resources/templates/panel.html
    }

    @GetMapping("/driver")
    public String showDriverPage() {
        return "driver";
    }

}

