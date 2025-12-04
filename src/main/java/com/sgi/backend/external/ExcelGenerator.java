package com.sgi.backend.external;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class ExcelGenerator {

    public byte[] createExcelFile(String sheetName, List<String> headers, List<Map<String, Object>> rows) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(sheetName);

            int currentRow = 0;

            // ==========================================
            // LOGO Y TÍTULO
            // ==========================================
            try {
                // Agregar logo
                ClassPathResource imgFile = new ClassPathResource("images/logo.jpeg");
                InputStream inputStream = imgFile.getInputStream();
                byte[] imageBytes = IOUtils.toByteArray(inputStream);

                int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
                CreationHelper helper = workbook.getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();

                // Posicionar logo en A1, tamaño 2 filas x 2 columnas
                anchor.setCol1(0);
                anchor.setRow1(0);
                anchor.setCol2(2);
                anchor.setRow2(2);

                Picture picture = drawing.createPicture(anchor, pictureIdx);

                currentRow = 2; // Empezar después del logo
            } catch (Exception e) {
                System.err.println("No se pudo cargar el logo: " + e.getMessage());
                currentRow = 0;
            }

            // Título
            Row titleRow = sheet.createRow(currentRow++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(sheetName);

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Subtítulo
            Row subtitleRow = sheet.createRow(currentRow++);
            Cell subtitleCell = subtitleRow.createCell(0);
            subtitleCell.setCellValue("Sistema de Gestión Integral - Ciempiés");

            CellStyle subtitleStyle = workbook.createCellStyle();
            Font subtitleFont = workbook.createFont();
            subtitleFont.setFontHeightInPoints((short) 11);
            subtitleFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            subtitleStyle.setFont(subtitleFont);
            subtitleCell.setCellStyle(subtitleStyle);

            // Fecha
            Row dateRow = sheet.createRow(currentRow++);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generado: " + new java.util.Date());

            CellStyle dateStyle = workbook.createCellStyle();
            Font dateFont = workbook.createFont();
            dateFont.setFontHeightInPoints((short) 9);
            dateFont.setItalic(true);
            dateFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dateStyle.setFont(dateFont);
            dateCell.setCellStyle(dateStyle);

            // Fila vacía
            currentRow++;

            // ==========================================
            // HEADERS DE LA TABLA
            // ==========================================
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);

            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            Row headerRow = sheet.createRow(currentRow++);
            headerRow.setHeight((short) 500);

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // ==========================================
            // DATOS
            // ==========================================
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle alternateStyle = workbook.createCellStyle();
            alternateStyle.cloneStyleFrom(dataStyle);
            alternateStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            alternateStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            boolean alternate = false;
            for (Map<String, Object> rowData : rows) {
                Row row = sheet.createRow(currentRow++);

                int colNum = 0;
                for (String header : headers) {
                    Cell cell = row.createCell(colNum++);
                    Object value = rowData.get(header);

                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }

                    cell.setCellStyle(alternate ? alternateStyle : dataStyle);
                }
                alternate = !alternate;
            }

            // ==========================================
            // FOOTER
            // ==========================================
            currentRow++; // Fila vacía
            Row footerRow = sheet.createRow(currentRow);
            Cell footerCell = footerRow.createCell(0);
            footerCell.setCellValue("Total de registros: " + rows.size());
            footerCell.setCellStyle(dateStyle);

            // Auto-ajustar columnas
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
            }

            // Convertir a bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel: " + e.getMessage(), e);
        }
    }
}
