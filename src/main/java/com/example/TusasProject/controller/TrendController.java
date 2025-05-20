package com.example.TusasProject.controller;

import com.example.TusasProject.dto.TrendDTO;
import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.entity.User;
import com.example.TusasProject.entity.enums.DriverCategory;
import com.example.TusasProject.repository.UserRepository;
import com.example.TusasProject.service.CustomUserDetails;
import com.example.TusasProject.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TrendController {

    @Autowired
    private final TrendService trendService;

    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }
    @Autowired
    UserRepository userRepository;


    @GetMapping("/drivers.html")
    public String getDrivers(@RequestParam("trend") String trend, Model model) {
        List<Driver> drivers = trendService.getDriversByTrend(trend);

        // Kategoriye göre gruplama
        Map<DriverCategory, List<Driver>> driversByCategory = drivers.stream()
                .collect(Collectors.groupingBy(Driver::getDriverCategory, LinkedHashMap::new, Collectors.toList()));

        model.addAttribute("trendName", trend);
        model.addAttribute("driversByCategory", driversByCategory);
        return "drivers";
    }

    @GetMapping("/api/trends")
    @ResponseBody
    public List<TrendDTO> getTrends(@RequestParam(value = "search", required = false) String search) {
        List<Trend> trends = (search == null || search.isEmpty())
                ? trendService.getAllTrends()
                : trendService.searchByName(search);

        return trends.stream()
                .map(t -> new TrendDTO(t.getId(), t.getTrendName(), t.getDefinition(), t.getAvgImpact()))
                .collect(Collectors.toList());
    }
    @GetMapping("/trends")
    public String showTrendsPage(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepository.findByEmail(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());
                model.addAttribute("userRole", user.getRole().getName());
                model.addAttribute("userExpertise", user.getExpertise());
            }
        }
        return "trends"; // HTML sayfası yüklenir, veriler JS ile alınır
    }


}
