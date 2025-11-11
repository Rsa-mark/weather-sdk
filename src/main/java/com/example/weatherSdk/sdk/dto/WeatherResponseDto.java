package com.example.weatherSdk.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeatherResponseDto {

    private WeatherInfo weather;
    private Temperature temperature;
    private Integer visibility;
    private Wind wind;
    private Long datetime;
    private Sys sys;
    private Integer timezone;
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