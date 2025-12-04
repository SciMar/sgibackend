package com.sgi.backend.adapter.impl;

import com.sgi.backend.adapter.ReportService;
import com.sgi.backend.external.ExcelGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * PATRÓN ADAPTER
 * Adapta ExcelGenerator a la interfaz ReportService
 */
@Service("excelReportAdapter")
public class ExcelReportAdapter implements ReportService {

    @Autowired
    private ExcelGenerator excelGenerator;

    @Override
    public byte[] generateReport(String title, List<String> headers, List<Map<String, Object>> data) {
        // Adapta la llamada al servicio externo de Excel
        return excelGenerator.createExcelFile(title, headers, data);
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getFileExtension() {
        return ".xlsx";
    }

    @Override
    public String getReportType() {
        return "EXCEL";
    }
}