package com.example.TusasProject.controller;

import com.example.TusasProject.dto.DriverDTO;
import com.example.TusasProject.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
public class DriverThymeleafController {

    @Autowired
    private ExcelService excelService;

    private final String excelPath = "/Users/tubacayir/IdeaProjects/TusasProject/src/main/resources/Drivers.xlsx";

    // GET isteğiyle form sayfasını döner
    @GetMapping("/driver-form")
    public String showDriverForm(Model model) throws IOException {
        List<DriverDTO> drivers = excelService.readExcelDrivers(excelPath);
        model.addAttribute("drivers", drivers);
        return "drivers"; // templates/drivers.html sayfasına gider
    }

    // POST ile slider değerlerini alır
    @PostMapping("/api/submit-scores")
    public String submitScores(@ModelAttribute("drivers") DriverDTO[] drivers) {
        for (DriverDTO driver : drivers) {
            System.out.println("Trend: " + driver.getTrend() +
                    ", Driver: " + driver.getDriver() +
                    ", Impact: " + driver.getImpact() +
                    ", Uncertainty: " + driver.getUncertainty());
        }

        // İstersen burada DB'ye ya da Excel'e yazabilirsin

        return "redirect:/driver-form"; // yeniden sayfaya yönlendir
    }
}
