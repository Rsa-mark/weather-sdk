package com.example.weatherSdk.sdk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityCoordinates {
    private String name;

    @JsonProperty("local_names")
    private Map<String,String> localNames;

    private double lat;
    private double lon;

    private String country;
    private String state;
}
