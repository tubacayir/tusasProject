package com.example.TusasProject.controller;

import com.example.TusasProject.service.UserService;
import com.example.TusasProject.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {


  @Autowired
  UserServiceImp userServiceImp;

    @GetMapping("/expertLogin")
    public String expertLogin() {
        return "expertLogin";
    }

    @GetMapping("/managerLogin")
    public String managerLogin() {
        return "managerLogin";
    }

    }

