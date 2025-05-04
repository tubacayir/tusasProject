package com.example.TusasProject.controller;

import com.example.TusasProject.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import java.util.List;

@Controller
public class TrendController {

    private final TrendService trendService;

    @Autowired
    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

    @GetMapping("/drivers.html")
    public String getDrivers(@RequestParam("trend") String trend, Model model) {
        List<String> drivers = trendService.getDriversByTrend(trend);
        model.addAttribute("trendName", trend);
        model.addAttribute("drivers", drivers);
        return "drivers";
    }
}
