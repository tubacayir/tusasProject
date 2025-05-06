package com.example.TusasProject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String Login() {
        return "login";
    }
    @GetMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("MANAGER")) {
            return "redirect:/panel";
        } else if (request.isUserInRole("EXPERT")) {
            return "redirect:/panel";
        }
        return "redirect:/login?unauthorized";
    }
}