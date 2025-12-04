package com.sgi.backend.external;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class PdfGenerator {

    public byte[] createPdfDocument(String titulo, List<String> columnas, List<Map<String, Object>> datos) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);

            document.open();

            // ==========================================
            // HEADER CON LOGO Y TÍTULO
            // ==========================================
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1.5f, 3.5f});

            // Logo a la izquierda
            try {
                ClassPathResource imgFile = new ClassPathResource("images/logo.jpeg");
                InputStream inputStream = imgFile.getInputStream();
                byte[] imageBytes = inputStream.readAllBytes();
                Image logo = Image.getInstance(imageBytes);
                logo.scaleToFit(80, 80);

                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoCell.setPaddingBottom(10);
                headerTable.addCell(logoCell);
            } catch (Exception e) {
                // Si no se encuentra el logo, agregar celda vacía
                PdfPCell emptyCell = new PdfPCell(new Phrase(""));
                emptyCell.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyCell);
            }

            // Título y subtítulo a la derecha
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);

            Paragraph titleBlock = new Paragraph();
            titleBlock.add(new Chunk(titulo + "\n", titleFont));
            titleBlock.add(new Chunk("Sistema de Gestión Integral - Ciempiés", subtitleFont));

            PdfPCell titleCell = new PdfPCell(titleBlock);
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(titleCell);

            document.add(headerTable);

            // Línea separadora
            document.add(new Paragraph(" "));

            // ==========================================
            // FECHA DE GENERACIÓN
            // ==========================================
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Paragraph date = new Paragraph("Generado: " + new java.util.Date(), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(15);
            document.add(date);

            // ==========================================
            // TABLA DE DATOS
            // ==========================================
            PdfPTable table = new PdfPTable(columnas.size());
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // Headers
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
            for (String columna : columnas) {
                PdfPCell cell = new PdfPCell(new Phrase(columna, headerFont));
                cell.setBackgroundColor(new BaseColor(52, 73, 94)); // Azul oscuro
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Datos
            Font dataFont = new Font(Font.FontFamily.HELVETICA, 9);
            boolean alternate = false;
            for (Map<String, Object> fila : datos) {
                for (String columna : columnas) {
                    Object valor = fila.get(columna);
                    PdfPCell cell = new PdfPCell(new Phrase(valor != null ? valor.toString() : "", dataFont));
                    cell.setPadding(6);

                    // Alternar colores de fila para mejor lectura
                    if (alternate) {
                        cell.setBackgroundColor(new BaseColor(245, 245, 245));
                    }

                    table.addCell(cell);
                }
                alternate = !alternate;
            }

            document.add(table);

            // ==========================================
            // FOOTER
            // ==========================================
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
            Paragraph footer = new Paragraph(
                    "Sistema de Gestión Integral - Ciempiés | Total de registros: " + datos.size(),
                    footerFont
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }
}