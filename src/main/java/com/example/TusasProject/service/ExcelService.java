    package com.example.TusasProject.service;

    import com.example.TusasProject.dto.DriverDTO;
    import org.apache.poi.ss.usermodel.Row;
    import org.apache.poi.ss.usermodel.Sheet;
    import org.apache.poi.ss.usermodel.Workbook;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import org.springframework.stereotype.Service;

    import java.io.FileInputStream;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

    @Service
    public class ExcelService {

        public List<DriverDTO> readExcelDrivers(String filePath) throws IOException {
            List<DriverDTO> drivers = new ArrayList<>();
            FileInputStream fis = new FileInputStream(filePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue;

                try {
                    if (row.getCell(0) == null || row.getCell(2) == null ||
                            row.getCell(3) == null || row.getCell(4) == null) {
                        continue; // Gerekli hücrelerden biri boşsa atla
                    }

                    DriverDTO dto = new DriverDTO();
                    dto.setTrend(row.getCell(0).getStringCellValue().trim());
                    dto.setDriver(row.getCell(2).getStringCellValue().trim());
                    dto.setImpact(row.getCell(3).getNumericCellValue());
                    dto.setUncertainty(row.getCell(4).getNumericCellValue());

                    drivers.add(dto);
                } catch (Exception e) {
                    // Hatalı satırı logla ve devam et
                    System.out.println("Satır atlandı (Hata): " + row.getRowNum() + " => " + e.getMessage());
                }
            }

            workbook.close();
            fis.close();
            return drivers;
        } }
