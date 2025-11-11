package com.example.weatherSdk.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherResponse {

    @JsonProperty("weather")
    private List<WeatherInfo> weather;

    @JsonProperty("main")
    private Temperature temperature;

    @JsonProperty("visibility")
    private Integer visibility;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("dt")
    private Long datetime;

    @JsonProperty("sys")
    private Sys sys;

    @JsonProperty("timezone")
    private Integer timezone;

    @JsonProperty("name")
    private String name;

    @Data
    public static class WeatherInfo {
        @JsonProperty("main")
        private String main;

        @JsonProperty("description")
        private String description;
    }

    @Data
    public static class Temperature {
        @JsonProperty("temp")
        private Double temp;

        @JsonProperty("feels_like")
        private Double feelsLike;
    }

    @Data
    public static class Wind {
        @JsonProperty("speed")
        private Double speed;
    }

    @Data
    public static class Sys {
        @JsonProperty("sunrise")
        private Long sunrise;

        @JsonProperty("sunset")
        private Long sunset;
    }
}

