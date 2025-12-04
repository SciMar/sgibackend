package com.sgi.backend.service;

import com.sgi.backend.dto.clima.ClimaDTO;
import com.sgi.backend.dto.clima.ClimaResponseAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClimaService {

    @Value("${openweather.api.key}")
    private String apiKey;

    @Value("${openweather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Obtener clima actual de una ciudad
     */
    public ClimaDTO obtenerClima(String ciudad) {
        try {
            String url = String.format("%s?q=%s,CO&appid=%s&units=metric&lang=es",
                    apiUrl, ciudad, apiKey);

            ClimaResponseAPI response = restTemplate.getForObject(url, ClimaResponseAPI.class);

            if (response == null) {
                throw new RuntimeException("No se pudo obtener información del clima");
            }

            return mapearAClimaDTO(response);

        } catch (Exception e) {
            System.err.println("Error consultando clima: " + e.getMessage());
            throw new RuntimeException("Error al consultar el clima: " + e.getMessage());
        }
    }

    /**
     * Obtener clima de Bogotá (por defecto para el sistema)
     */
    public ClimaDTO obtenerClimaBogota() {
        return obtenerClima("Bogota");
    }

    /**
     * Verificar si hay condiciones climáticas adversas
     */
    public boolean hayCondicionesAdversas() {
        try {
            ClimaDTO clima = obtenerClimaBogota();
            return clima.getAlerta().equals("ALERTA") || clima.getAlerta().equals("PRECAUCION");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtener recomendación según el clima
     */
    public String obtenerRecomendacion() {
        ClimaDTO clima = obtenerClimaBogota();

        return switch (clima.getAlerta()) {
            case "ALERTA" -> "⚠️ ALERTA CLIMÁTICA: Se recomienda suspender o reprogramar rutas. " +
                    "Condiciones: " + clima.getDescripcion();
            case "PRECAUCION" -> "⚡ PRECAUCIÓN: Tomar medidas preventivas en las rutas. " +
                    "Condiciones: " + clima.getDescripcion();
            default -> "✅ Condiciones normales para operar rutas. " +
                    "Clima: " + clima.getDescripcion();
        };
    }

    /**
     * Mapear respuesta de API a DTO
     */
    private ClimaDTO mapearAClimaDTO(ClimaResponseAPI response) {
        ClimaDTO clima = new ClimaDTO();
        clima.setCiudad(response.getName());
        clima.setTemperatura(response.getMain().getTemp());
        clima.setHumedad(response.getMain().getHumidity());
        clima.setSensacionTermica(response.getMain().getFeels_like());

        if (response.getWeather() != null && !response.getWeather().isEmpty()) {
            ClimaResponseAPI.Weather weather = response.getWeather().get(0);
            clima.setEstado(weather.getMain());
            clima.setDescripcion(weather.getDescription());

            // Determinar nivel de alerta
            clima.setAlerta(determinarAlerta(weather.getMain(), response.getMain().getTemp()));
        }

        return clima;
    }

    /**
     * Determinar nivel de alerta según condiciones
     */
    private String determinarAlerta(String estado, Double temperatura) {
        // Alerta por lluvia fuerte o tormenta
        if (estado.equalsIgnoreCase("Thunderstorm")) {
            return "ALERTA";
        }

        if (estado.equalsIgnoreCase("Rain") || estado.equalsIgnoreCase("Drizzle")) {
            return "PRECAUCION";
        }

        // Alerta por temperatura extrema
        if (temperatura < 5 || temperatura > 35) {
            return "PRECAUCION";
        }

        return "NORMAL";
    }
}
