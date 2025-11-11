package com.example.weatherSdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.weatherSdk")
@EnableScheduling
public class WeatherSdkApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherSdkApplication.class, args);
	}

}
