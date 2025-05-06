package com.example.TusasProject.controller;

;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

  @GetMapping("/panel")
  public String showPanelPage() {
    return "panel"; // src/main/resources/templates/panel.html
  }
    @GetMapping("/driver")
    public  String showDriverPage() {
        return "driver";
    }






}

