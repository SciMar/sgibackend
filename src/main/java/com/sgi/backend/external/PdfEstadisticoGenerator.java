package com.sgi.backend.external;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class PdfEstadisticoGenerator {

    // Colores del sistema Ciempiés
    private static final BaseColor PRIMARY_COLOR = new BaseColor(102, 126, 234);
    private static final BaseColor SECONDARY_COLOR = new BaseColor(118, 75, 162);
    private static final BaseColor SUCCESS_COLOR = new BaseColor(76, 175, 80);
    private static final BaseColor DANGER_COLOR = new BaseColor(244, 67, 54);
    private static final BaseColor WARNING_COLOR = new BaseColor(255, 193, 7);
    private static final BaseColor INFO_COLOR = new BaseColor(33, 150, 243);

    public byte[] generarReporteEstadistico(String titulo, Map<String, Object> estadisticas,
                                            String tipoGrafico) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Header con logo
            agregarHeader(document, titulo);

            // Línea separadora decorativa
            agregarLineaSeparadora(document);

            // Tarjetas de resumen (estilo dashboard)
            agregarTarjetasResumen(document, estadisticas);

            // Gráfico según el tipo
            if (tipoGrafico.equalsIgnoreCase("pie")) {
                agregarGraficoPastel(document, estadisticas);
            } else {
                agregarGraficoBarras(document, estadisticas);
            }

            // Tabla detallada
            agregarTablaDetallada(document, estadisticas);

            // Footer
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
        headerTable.setWidths(new float[]{1.2f, 3.8f});

        // Logo
        try {
            ClassPathResource imgFile = new ClassPathResource("static/images/logo.jpeg");
            InputStream inputStream = imgFile.getInputStream();
            byte[] imageBytes = inputStream.readAllBytes();
            Image logo = Image.getInstance(imageBytes);
            logo.scaleToFit(70, 70);

            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            logoCell.setPaddingBottom(10);
            headerTable.addCell(logoCell);
        } catch (Exception e) {
            // Si no hay logo, celda vacía
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(emptyCell);
        }

        // Título y subtítulo
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, PRIMARY_COLOR);
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);

        Paragraph titleBlock = new Paragraph();
        titleBlock.add(new Chunk("Sistema Ciempiés\n", titleFont));
        titleBlock.add(new Chunk(titulo + "\n", subtitleFont));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        titleBlock.add(new Chunk("Generado: " + sdf.format(new Date()), dateFont));

        PdfPCell titleCell = new PdfPCell(titleBlock);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(titleCell);

        document.add(headerTable);
    }

    private void agregarLineaSeparadora(Document document) throws Exception {
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        lineTable.setSpacingBefore(15);
        lineTable.setSpacingAfter(20);

        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineCell.setFixedHeight(4);
        lineCell.setBackgroundColor(PRIMARY_COLOR);
        lineTable.addCell(lineCell);

        document.add(lineTable);
    }

    private void agregarTarjetasResumen(Document document, Map<String, Object> estadisticas) throws Exception {
        // Obtener valores (busca múltiples claves posibles)
        Long presentes = getValueAsLong(estadisticas, "presentes", "totalPresentes", "PRESENTE");
        Long ausentes = getValueAsLong(estadisticas, "ausentes", "totalAusentes", "AUSENTE");
        Long total = getValueAsLong(estadisticas, "total", "totalRegistros");

        // Calcular total si no existe
        if (total == null && presentes != null && ausentes != null) {
            total = presentes + ausentes;
        }

        // Si todo es null, usar 0
        if (presentes == null) presentes = 0L;
        if (ausentes == null) ausentes = 0L;
        if (total == null) total = 0L;

        // Calcular porcentaje
        double porcentaje = 0;
        Object porcentajeObj = estadisticas.get("porcentajeAsistencia");
        if (porcentajeObj instanceof Number) {
            porcentaje = ((Number) porcentajeObj).doubleValue();
        } else if (total > 0 && presentes != null) {
            porcentaje = (presentes * 100.0) / total;
        }

        // Título de sección
        Font sectionFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        Paragraph sectionTitle = new Paragraph("Resumen de Asistencias", sectionFont);
        sectionTitle.setSpacingAfter(15);
        document.add(sectionTitle);

        // Crear tabla de 4 tarjetas
        PdfPTable cardsTable = new PdfPTable(4);
        cardsTable.setWidthPercentage(100);
        cardsTable.setSpacingAfter(25);

        // Tarjeta Total
        cardsTable.addCell(crearTarjeta("Total Registros", total.toString(), INFO_COLOR));

        // Tarjeta Presentes
        cardsTable.addCell(crearTarjeta("Presentes", presentes.toString(), SUCCESS_COLOR));

        // Tarjeta Ausentes
        cardsTable.addCell(crearTarjeta("Ausentes", ausentes.toString(), DANGER_COLOR));

        // Tarjeta Porcentaje
        BaseColor porcentajeColor = porcentaje >= 80 ? SUCCESS_COLOR :
                (porcentaje >= 60 ? WARNING_COLOR : DANGER_COLOR);
        cardsTable.addCell(crearTarjeta("% Asistencia", String.format("%.1f%%", porcentaje), porcentajeColor));

        document.add(cardsTable);
    }

    private PdfPCell crearTarjeta(String label, String value, BaseColor color) {
        PdfPTable cardTable = new PdfPTable(1);

        // Valor grande
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD, color);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setPaddingTop(15);
        valueCell.setPaddingBottom(5);
        cardTable.addCell(valueCell);

        // Label pequeño
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        labelCell.setPaddingBottom(15);
        cardTable.addCell(labelCell);

        // Contenedor con borde
        PdfPCell containerCell = new PdfPCell(cardTable);
        containerCell.setBorderColor(new BaseColor(220, 220, 220));
        containerCell.setBorderWidth(1);
        containerCell.setPadding(5);
        containerCell.setBackgroundColor(new BaseColor(250, 250, 250));

        return containerCell;
    }

    private void agregarGraficoPastel(Document document, Map<String, Object> estadisticas) throws Exception {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        Paragraph seccionTitle = new Paragraph("Distribución de Asistencias", headerFont);
        seccionTitle.setSpacingBefore(10);
        seccionTitle.setSpacingAfter(15);
        document.add(seccionTitle);

        Long presentes = getValueAsLong(estadisticas, "presentes", "totalPresentes", "PRESENTE");
        Long ausentes = getValueAsLong(estadisticas, "ausentes", "totalAusentes", "AUSENTE");

        if (presentes == null) presentes = 0L;
        if (ausentes == null) ausentes = 0L;

        if (presentes == 0 && ausentes == 0) {
            Font noDataFont = new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, BaseColor.GRAY);
            Paragraph noData = new Paragraph("No hay datos disponibles para mostrar el gráfico.", noDataFont);
            noData.setAlignment(Element.ALIGN_CENTER);
            noData.setSpacingAfter(20);
            document.add(noData);
            return;
        }

        // Crear dataset
        DefaultPieDataset dataset = new DefaultPieDataset();

        if (presentes > 0) {
            dataset.setValue("Presentes (" + presentes + ")", presentes);
        }
        if (ausentes > 0) {
            dataset.setValue("Ausentes (" + ausentes + ")", ausentes);
        }

        // Crear gráfico
        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(java.awt.Color.WHITE);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Presentes (" + presentes + ")", new java.awt.Color(76, 175, 80));
        plot.setSectionPaint("Ausentes (" + ausentes + ")", new java.awt.Color(244, 67, 54));
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {2}"));

        // Convertir a imagen
        BufferedImage bufferedImage = chart.createBufferedImage(500, 320);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartOut);

        Image chartImage = Image.getInstance(chartOut.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(420, 280);
        chartImage.setSpacingAfter(25);

        document.add(chartImage);
    }

    private void agregarGraficoBarras(Document document, Map<String, Object> estadisticas) throws Exception {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        Paragraph seccionTitle = new Paragraph("Comparativa de Asistencias", headerFont);
        seccionTitle.setSpacingBefore(10);
        seccionTitle.setSpacingAfter(15);
        document.add(seccionTitle);

        Long presentes = getValueAsLong(estadisticas, "presentes", "totalPresentes", "PRESENTE");
        Long ausentes = getValueAsLong(estadisticas, "ausentes", "totalAusentes", "AUSENTE");

        if (presentes == null) presentes = 0L;
        if (ausentes == null) ausentes = 0L;

        if (presentes == 0 && ausentes == 0) {
            Font noDataFont = new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, BaseColor.GRAY);
            Paragraph noData = new Paragraph("No hay datos disponibles para mostrar el gráfico.", noDataFont);
            noData.setAlignment(Element.ALIGN_CENTER);
            noData.setSpacingAfter(20);
            document.add(noData);
            return;
        }

        // Crear dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(presentes, "Cantidad", "Presentes");
        dataset.addValue(ausentes, "Cantidad", "Ausentes");

        // Crear gráfico
        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Estado",
                "Cantidad de Estudiantes",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        chart.setBackgroundPaint(java.awt.Color.WHITE);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new java.awt.Color(220, 220, 220));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new java.awt.Color(102, 126, 234));
        renderer.setShadowVisible(false);

        // Convertir a imagen
        BufferedImage bufferedImage = chart.createBufferedImage(500, 320);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", chartOut);

        Image chartImage = Image.getInstance(chartOut.toByteArray());
        chartImage.setAlignment(Element.ALIGN_CENTER);
        chartImage.scaleToFit(420, 280);
        chartImage.setSpacingAfter(25);

        document.add(chartImage);
    }

    private void agregarTablaDetallada(Document document, Map<String, Object> estadisticas) throws Exception {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PRIMARY_COLOR);
        Paragraph seccionTitle = new Paragraph("Detalle de Estadísticas", headerFont);
        seccionTitle.setSpacingBefore(10);
        seccionTitle.setSpacingAfter(15);
        document.add(seccionTitle);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(70);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidths(new float[]{2f, 1.5f});

        // Header de tabla
        Font thFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPCell headerCell1 = new PdfPCell(new Phrase("Concepto", thFont));
        headerCell1.setBackgroundColor(PRIMARY_COLOR);
        headerCell1.setPadding(12);
        headerCell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerCell1);

        PdfPCell headerCell2 = new PdfPCell(new Phrase("Valor", thFont));
        headerCell2.setBackgroundColor(PRIMARY_COLOR);
        headerCell2.setPadding(12);
        headerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(headerCell2);

        // Filas de datos
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        boolean alternate = false;

        for (Map.Entry<String, Object> entry : estadisticas.entrySet()) {
            BaseColor bgColor = alternate ? new BaseColor(248, 249, 250) : BaseColor.WHITE;

            PdfPCell keyCell = new PdfPCell(new Phrase(formatearLabel(entry.getKey()), cellFont));
            keyCell.setPadding(10);
            keyCell.setBackgroundColor(bgColor);
            table.addCell(keyCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(formatearValor(entry.getValue()), cellFont));
            valueCell.setPadding(10);
            valueCell.setBackgroundColor(bgColor);
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(valueCell);

            alternate = !alternate;
        }

        document.add(table);
    }

    private void agregarFooter(Document document) throws Exception {
        document.add(new Paragraph(" "));

        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(30);

        // Línea superior
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.NO_BORDER);
        lineCell.setFixedHeight(2);
        lineCell.setBackgroundColor(new BaseColor(220, 220, 220));
        footerTable.addCell(lineCell);

        // Texto footer
        Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);

        Paragraph footerText = new Paragraph();
        footerText.add(new Chunk("Sistema Ciempiés - Sistema de Gestión de Transporte Escolar\n", footerFont));
        footerText.add(new Chunk("© 2024 Todos los derechos reservados", footerFont));
        footerText.setAlignment(Element.ALIGN_CENTER);

        PdfPCell textCell = new PdfPCell(footerText);
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPaddingTop(10);
        textCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footerTable.addCell(textCell);

        document.add(footerTable);
    }

    private String formatearLabel(String key) {
        if (key == null) return "N/A";
        return switch (key.toLowerCase()) {
            case "totalpresentes", "presentes" -> "Total Presentes";
            case "totalausentes", "ausentes" -> "Total Ausentes";
            case "total", "totalregistros" -> "Total Registros";
            case "porcentajeasistencia", "porcentaje" -> "Porcentaje de Asistencia";
            case "fechainicio" -> "Fecha Inicio";
            case "fechafin" -> "Fecha Fin";
            case "nombreestudiante" -> "Estudiante";
            case "nombrecolegio" -> "Colegio";
            default -> {
                // Capitalizar primera letra
                String result = key.replaceAll("([A-Z])", " $1").trim();
                yield result.substring(0, 1).toUpperCase() + result.substring(1);
            }
        };
    }

    private String formatearValor(Object value) {
        if (value == null) return "N/A";
        if (value instanceof Double) {
            return String.format("%.2f%%", ((Double) value));
        }
        if (value instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) value).toString();
        }
        return value.toString();
    }

    /**
     * Busca un valor Long en el mapa usando múltiples claves posibles
     */
    private Long getValueAsLong(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            // Búsqueda exacta
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }

            // Búsqueda ignorando mayúsculas
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(key) && entry.getValue() instanceof Number) {
                    return ((Number) entry.getValue()).longValue();
                }
            }
        }
        return null;
    }
}