package com.sgi.backend.adapter;

import java.util.List;
import java.util.Map;

/**
 * Interfaz común para la generación de reportes
 * (Target del patrón Adapter)
 */
public interface ReportService {
    byte[] generateReport(String title, List<String> headers, List<Map<String, Object>> data);
    String getContentType();
    String getFileExtension();
    String getReportType();
}
