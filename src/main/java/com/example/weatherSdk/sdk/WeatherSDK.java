package com.example.weatherSdk.sdk;

import com.example.weatherSdk.sdk.cache.CachedWeather;
import com.example.weatherSdk.sdk.dto.CityCoordinates;
import com.example.weatherSdk.sdk.dto.WeatherResponse;
import com.example.weatherSdk.sdk.dto.WeatherResponseDto;
import com.example.weatherSdk.sdk.mapper.WeatherResponseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Getter
public class WeatherSDK {

    private final WebClient webClient;

    // API key injected from application properties
    @Value("${weather.api.key}")
    private String apiKey;

    // SDK mode: ON_DEMAND fetch or POLLING background fetch
    @Value("${weather.mode:ON_DEMAND}")
    private Mode mode;

    // Simple LRU cache to store last 10 weather requests
    private final Map<String, CachedWeather> cache = Collections.synchronizedMap(
            new LinkedHashMap<String, CachedWeather>(10, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CachedWeather> eldest) {
                    return size() > 10;
                }
            }
    );

    /**
     * Returns the set of cities currently cached.
     * Thread-safe access to the cache.
     */
    public Set<String> getCities() {
        synchronized (cache) {
            return Set.copyOf(cache.keySet());
        }
    }

    /**
     * Fetches weather for the given city.
     * If a valid cached value exists, it will return that.
     * Otherwise, it fetches from the API and maps to WeatherResponseDto.
     */
    public WeatherResponseDto getWeather(String city) {
        CachedWeather cached;
        synchronized (cache) {
            cached = cache.get(city.toLowerCase());
        }

        if (cached != null && !cached.isExpired()) {
            return WeatherResponseMapper.toDto(cached.getWeather());
        }

        // Blocking call to fetch weather; consider async alternatives for high-load environments
        WeatherResponse weather = fetchWeather(city).block();

        if (weather == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to fetch weather for " + city);
        }

        WeatherResponseDto dto = WeatherResponseMapper.toDto(weather);

        synchronized (cache) {
            cache.put(city.toLowerCase(), new CachedWeather(weather));
        }

        return dto;
    }

    /**
     * Async fetch for external usage or polling.
     */
    @Async("asyncExecutor")
    public CompletableFuture<WeatherResponse> fetchWeatherAsync(String city) {
        return fetchWeather(city).toFuture();
    }

    /**
     * Main pipeline to fetch weather: first get coordinates, then fetch weather by coordinates.
     */
    private Mono<WeatherResponse> fetchWeather(String city) {
        return fetchCityCoordinates(city)
                .flatMap(coords -> fetchWeatherByCoordinates(coords.getLat(), coords.getLon()));
    }

    /**
     * Fetch city coordinates from OpenWeather Geocoding API.
     * Handles common HTTP errors and maps them to ResponseStatusException.
     */
    private Mono<CityCoordinates> fetchCityCoordinates(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geo/1.0/direct")
                        .queryParam("q", city)
                        .queryParam("limit", 1)
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(CityCoordinates[].class)
                .flatMap(array -> {
                    if (array.length == 0) {
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "City not found: " + city));
                    }
                    return Mono.just(array[0]);
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key");
                    }
                    return new ResponseStatusException(e.getStatusCode(), e.getMessage(), e);
                })
                .onErrorMap(Exception.class, e ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e)
                );
    }

    /**
     * Fetch weather by coordinates using OpenWeather Current Weather API.
     * Maps errors to appropriate HTTP status codes.
     */
    private Mono<WeatherResponse> fetchWeatherByCoordinates(double lat, double lon) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("units", "metric")
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(WeatherResponse.class)
                .onErrorMap(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key");
                    }
                    return new ResponseStatusException(e.getStatusCode(), e.getMessage(), e);
                })
                .onErrorMap(Exception.class, e ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e)
                );
    }

    /**
     * Polling mechanism for updating cached weather data every 10 minutes.
     * Only works if mode == POLLING.
     */
    @Scheduled(fixedRateString = "PT10M")
    public void pollAllCities() {
        if (mode != Mode.POLLING) return;

        getCities().forEach(city ->
                fetchWeatherAsync(city)
                        .thenAccept(resp -> cache.put(city.toLowerCase(), new CachedWeather(resp)))
        );
    }

    /**
     * SDK operation modes.
     * ON_DEMAND - fetches weather on each request.
     * POLLING - background updates cache every 10 minutes.
     */
    public enum Mode {
        ON_DEMAND,
        POLLING
    }
}
