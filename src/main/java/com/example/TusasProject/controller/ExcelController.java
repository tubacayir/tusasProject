package com.example.TusasProject.controller;
import com.example.TusasProject.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel") // Buraya dikkat!
@RequiredArgsConstructor
public class ExcelController {
    @Autowired

    private final ExcelService excelService;

    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        excelService.importDriversFromExcel(file);
        return ResponseEntity.ok("Excel import successful!");
    }
}
