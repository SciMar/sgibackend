package com.sgi.backend.adapter.impl;

import com.sgi.backend.adapter.ReportService;
import com.sgi.backend.external.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * PATRÓN ADAPTER
 * Adapta PdfGenerator a la interfaz ReportService
 */
@Service("pdfReportAdapter")
public class PdfReportAdapter implements ReportService {

    @Autowired
    private PdfGenerator pdfGenerator;

    @Override
    public byte[] generateReport(String title, List<String> headers, List<Map<String, Object>> data) {
        // Adapta la llamada al servicio externo de PDF
        return pdfGenerator.createPdfDocument(title, headers, data);
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public String getFileExtension() {
        return ".pdf";
    }

    @Override
    public String getReportType() {
        return "PDF";
    }
}
