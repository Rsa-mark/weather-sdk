package com.example.weatherSdk.sdk.rest;

import com.example.weatherSdk.sdk.WeatherSDK;
import com.example.weatherSdk.sdk.dto.WeatherResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherSDK weatherSDK;

    @GetMapping(value = "/{city}")
    public ResponseEntity<WeatherResponseDto> getWeather(@PathVariable String city) {
        return ResponseEntity.ok(weatherSDK.getWeather(city));
    }
}
