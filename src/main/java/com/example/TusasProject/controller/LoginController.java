package com.example.TusasProject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/expert-login")
    public String expertLoginPage() {
        return "expert-login"; // expert-login.html
    }

    @GetMapping("/manager-login")
    public String managerLoginPage() {
        return "manager-login"; // manager-login.html
    }

    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("MANAGER")) {
            return "redirect:/manager/dashboard";
        } else if (request.isUserInRole("EXPERT")) {
            return "redirect:/expert/dashboard";
        }
        return "redirect:/login?unauthorized";
    }
}