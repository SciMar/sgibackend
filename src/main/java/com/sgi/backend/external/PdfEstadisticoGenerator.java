package com.sgi.backend.external;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

@Component
public class PdfEstadisticoGenerator {

    public byte[] generarReporteEstadistico(String titulo, Map<String, Object> estadisticas,
                                            String tipoGrafico) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // ==========================================
            // HEADER CON LOGO Y TÍTULO
            // ==========================================
            agregarHeader(document, titulo);

            // ==========================================
            // ESTADÍSTICAS NUMÉRICAS
            // ==========================================
            agregarEstadisticasNumericas(document, estadisticas);

            // ==========================================
            // GRÁFICO
            // ==========================================
            if (tipoGrafico.equalsIgnoreCase("pie")) {
                agregarGraficoPastel(document, estadisticas);
            } else {
                agregarGraficoBarras(document, estadisticas);
            }

            // ==========================================
            // FOOTER
            // ==========================================
            agregarFooter(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte estadístico: " + e.getMessage(), e);
        }
    }

    private void agregarHeader(Document document, String titulo) throws Exception {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1.5f, 3.5f});

        // Logo
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
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(emptyCell);
        }

        // Título
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.DARK_GRAY);

        Paragraph titleBlock = new Paragraph();
        titleBlock.add(new Chunk(titulo + "\n", titleFont));
        titleBlock.add(new Chunk("Reporte Estadístico\n", subtitleFont));
        titleBlock.add(new Chunk("Sistema de Gestión Integral - Ciempiés", subtitleFont));

        PdfPCell titleCell = new PdfPCell(titleBlock);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(titleCell);

        document.add(headerTable);
        document.add(new Paragraph(" "));

        // Fecha
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
        Paragraph date = new Paragraph("Generado: " + new java.util.Date(), dateFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setSpacingAfter(20);
        document.add(date);
    }

    private void agregarEstadisticasNumericas(Document document, Map<String, Object> estadisticas)
            throws Exception {

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph seccionTitle = new Paragraph("Resumen Estadístico", headerFont);
        seccionTitle.setSpacingBefore(10);
        seccionTitle.setSpacingAfter(10);
        document.add(seccionTitle);

        // Tabla de estadísticas
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(70);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        // Estilo para labels
        PdfPCell labelStyle = new PdfPCell();
        labelStyle.setBackgroundColor(new BaseColor(52, 73, 94));
        labelStyle.setPadding(8);

        // Agregar filas con estadísticas
        for (Map.Entry<String, Object> entry : estadisticas.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Label
            PdfPCell labelCell = new PdfPCell(new Phrase(formatearLabel(key),
                    new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE)));
            labelCell.setBackgroundColor(new BaseColor(52, 73, 94));
            labelCell.setPadding(8);
            table.addCell(labelCell);

            // Valor
            PdfPCell valueCell = new PdfPCell(new Phrase(formatearValor(value), valueFont));
            valueCell.setPadding(8);

            // Colorear según el tipo de dato
            if (key.contains("porcentaje")) {
                double porcentaje = value instanceof Number ? ((Number) value).doubleValue() : 0;
                if (porcentaje >= 80) {
                    valueCell.setBackgroundColor(new BaseColor(200, 255, 200)); // Verde claro
                } else if (porcentaje >= 60) {
                    valueCell.setBackgroundColor(new BaseColor(255, 255, 200)); // Amarillo claro
                } else {
                    valueCell.setBackgroundColor(new BaseColor(255, 200, 200)); // Rojo claro
                }
            }

            table.addCell(valueCell);
        }

        document.add(table);
        document.add(new Paragraph(" "));
    }

    private void agregarGraficoPastel(Document document, Map<String, Object> estadisticas)
            throws Exception {

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph seccionTitle = new Paragraph("Distribución de Asistencias", headerFont);
        seccionTitle.setSpacingBefore(15);
        seccionTitle.setSpacingAfter(10);
        document.add(seccionTitle);

        // Crear dataset
        DefaultPieDataset dataset = new DefaultPieDataset();

        Long presentes = getValueAsLong(estadisticas, "totalPresentes");
        Long ausentes = getValueAsLong(estadisticas, "totalAusentes");

        if (presentes != null && presentes > 0) {
            dataset.setValue("Presentes (" + presentes + ")", presentes);
        }
        if (ausentes != null && ausentes > 0) {
            dataset.setValue("Ausentes (" + ausentes + ")", ausentes);
        }

        // Crear gráfico
        JFreeChart chart = ChartFactory.createPieChart(
                "",  // Sin título (ya está arriba)
                dataset,
                true,  // Leyenda
                true,
                false
        );

        // Personalizar colores
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Presentes (" + presentes + ")", new Color(76, 175, 80)); // Verde
        plot.setSectionPaint("Ausentes (" + ausentes + ")", new Color(244, 67, 54)); // Rojo
        plot.setBackgroundPaint(Color.WHITE);

        // Convertir a imagen
        BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartOut);

        Image chartImage = Image.getInstance(chartOut.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(450, 270);

        document.add(chartImage);
    }

    private void agregarGraficoBarras(Document document, Map<String, Object> estadisticas)
            throws Exception {

        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Paragraph seccionTitle = new Paragraph("Comparativa de Asistencias", headerFont);
        seccionTitle.setSpacingBefore(15);
        seccionTitle.setSpacingAfter(10);
        document.add(seccionTitle);

        // Crear dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Long presentes = getValueAsLong(estadisticas, "totalPresentes");
        Long ausentes = getValueAsLong(estadisticas, "totalAusentes");

        if (presentes != null) {
            dataset.addValue(presentes, "Cantidad", "Presentes");
        }
        if (ausentes != null) {
            dataset.addValue(ausentes, "Cantidad", "Ausentes");
        }

        // Crear gráfico
        JFreeChart chart = ChartFactory.createBarChart(
                "",  // Sin título
                "Estado",
                "Cantidad de Estudiantes",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        // Convertir a imagen
        BufferedImage bufferedImage = chart.createBufferedImage(500, 300);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartOut);

        Image chartImage = Image.getInstance(chartOut.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(450, 270);

        document.add(chartImage);
    }

    private void agregarFooter(Document document) throws Exception {
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph(
                "Sistema de Gestión Integral - Ciempiés | Reporte Estadístico",
                footerFont
        );
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(20);
        document.add(footer);
    }

    private String formatearLabel(String key) {
        return switch (key) {
            case "totalPresentes" -> "Total Presentes";
            case "totalAusentes" -> "Total Ausentes";
            case "total" -> "Total Registros";
            case "porcentajeAsistencia" -> "Porcentaje de Asistencia";
            case "fechaInicio" -> "Fecha Inicio";
            case "fechaFin" -> "Fecha Fin";
            default -> key;
        };
    }

    private String formatearValor(Object value) {
        if (value == null) return "N/A";

        if (value instanceof Double) {
            return String.format("%.2f%%", ((Double) value));
        }

        return value.toString();
    }

    private Long getValueAsLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }
}
