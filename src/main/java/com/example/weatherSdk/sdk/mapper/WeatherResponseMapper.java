package com.example.weatherSdk.sdk.mapper;

import com.example.weatherSdk.sdk.dto.WeatherResponse;
import com.example.weatherSdk.sdk.dto.WeatherResponseDto;

public class WeatherResponseMapper {

    public static WeatherResponseDto toDto(WeatherResponse weatherResponse) {
        if (weatherResponse == null) return null;

        WeatherResponseDto dto = new WeatherResponseDto();

        if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
            WeatherResponseDto.WeatherInfo weatherInfo = new WeatherResponseDto.WeatherInfo();
            WeatherResponse.WeatherInfo first = weatherResponse.getWeather().getFirst();
            weatherInfo.setMain(first.getMain());
            weatherInfo.setDescription(first.getDescription());
            dto.setWeather(weatherInfo);
        }

        if (weatherResponse.getTemperature() != null) {
            WeatherResponseDto.Temperature temp = new WeatherResponseDto.Temperature();
            temp.setTemp(weatherResponse.getTemperature().getTemp());
            temp.setFeelsLike(weatherResponse.getTemperature().getFeelsLike());
            dto.setTemperature(temp);
        }

        dto.setDatetime(weatherResponse.getDatetime());
        dto.setVisibility(weatherResponse.getVisibility());
        dto.setTimezone(weatherResponse.getTimezone());
        dto.setName(weatherResponse.getName());

        if (weatherResponse.getWind() != null) {
            WeatherResponseDto.Wind wind = new WeatherResponseDto.Wind();
            wind.setSpeed(weatherResponse.getWind().getSpeed());
            dto.setWind(wind);
        }

        if (weatherResponse.getSys() != null) {
            WeatherResponseDto.Sys sys = new WeatherResponseDto.Sys();
            sys.setSunrise(weatherResponse.getSys().getSunrise());
            sys.setSunset(weatherResponse.getSys().getSunset());
            dto.setSys(sys);
        }

        return dto;
    }
}
