package com.sgi.backend.dto.clima;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Mapea la respuesta de OpenWeatherMap API
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClimaResponseAPI {

    private List<Weather> weather;
    private Main main;
    private String name;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String main; // "Rain", "Clear", "Clouds"
        private String description; // "lluvia ligera", "cielo claro"
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        private Double temp;
        private Double feels_like;
        private Integer humidity;
    }
}
