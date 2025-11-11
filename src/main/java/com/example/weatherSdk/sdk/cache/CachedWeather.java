package com.example.weatherSdk.sdk.cache;

import com.example.weatherSdk.sdk.dto.WeatherResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CachedWeather {
    private final WeatherResponse weather;
    private final Instant timestamp = Instant.now();

    public CachedWeather(WeatherResponse weather) {
        this.weather = weather;
    }

    public boolean isExpired() {
        return Instant.now().minusSeconds(600).isAfter(timestamp); // 10 minutes
    }
}
