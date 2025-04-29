package com.example.TusasProject.service;

import com.example.TusasProject.entity.Driver;
import com.example.TusasProject.entity.Trend;
import com.example.TusasProject.entity.enums.DriverCategory;
import com.example.TusasProject.repository.DriverRepository;
import com.example.TusasProject.repository.TrendRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {

    private final DriverRepository driverRepository;
    private final TrendRepository trendRepository;

    public ExcelService(DriverRepository driverRepository, TrendRepository trendRepository) {
        this.driverRepository = driverRepository;
        this.trendRepository = trendRepository;
    }

    public void importDriversFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Başlığı atla
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();

                String trendName = row.getCell(0).getStringCellValue(); // Trend
                String categoryValue = row.getCell(1).getStringCellValue(); // Category
                String driverName = row.getCell(2).getStringCellValue(); // Driver

                Double impact = getNumericCellValue(row, 3); // Impact_rate
                Double uncertainty = getNumericCellValue(row, 4); // Uncertainty
                Double polarity = getNumericCellValue(row, 5); // Polarity

                List<Trend> trends = trendRepository.findByTrendName(trendName);

                if (trends.isEmpty()) {
                    System.out.println("Trend bulunamadı: " + trendName);
                    continue;
                }

                Trend trend = trends.get(0); // İlk bulduğunu alıyoruz
                DriverCategory category = DriverCategory.fromString(categoryValue);

                Driver driver = new Driver();
                driver.setDriverName(driverName);
                driver.setTrend(trend);
                if(category!=null) {
                    driver.setDriverCategory(category);
                    driver.setImpact(impact != null ? impact.floatValue() : null);
                    driver.setUncertainty(uncertainty != null ? uncertainty.floatValue() : null);
                    driver.setPolarity(polarity != null ? polarity.floatValue() : null);

                    driverRepository.saveAndFlush(driver);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Excel dosyası işlenirken hata oluştu!", e);
        }
    }

    private Double getNumericCellValue(Row row, int cellIndex) {
        try {
            return row.getCell(cellIndex) != null ? row.getCell(cellIndex).getNumericCellValue() : null;
        } catch (Exception e) {
            return null;
        }
    }

}
