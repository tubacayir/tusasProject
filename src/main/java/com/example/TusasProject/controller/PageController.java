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

    @GetMapping("/expertLogin")
    public String expertLogin() {
        return "expertLogin";
    }

    @GetMapping("/managerLogin")
    public String managerLogin() {
        return "managerLogin";
    }

  @GetMapping("/panel")
  public String showPanelPage() {
    return "panel"; // src/main/resources/templates/panel.html
  }
    @GetMapping("/driver")
    public  String showDriverPage() {
        return "driver";
    }


    @GetMapping("/analyst")
    public String showAnalystPage() {
        return "analyst"; // src/main/resources/templates/analyst.html
    }

    @GetMapping("/expert")
    public String showExpertPage() {
        return "expert"; // src/main/resources/templates/expert.html
    }

    @GetMapping("/manager")
    public String showManagerPage() {
        return "manager"; // src/main/resources/templates/manager.html
    }

}

