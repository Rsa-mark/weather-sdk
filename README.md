# WeatherSDK

**Java SDK for fetching weather data from OpenWeather API.**

---

## Features

- Fetch current weather by city.
- Returns structured `WeatherResponseDto`.
- LRU cache (last 10 cities).
- Supports **ON_DEMAND** and **POLLING** modes.
- Asynchronous fetching via `CompletableFuture`.
- Proper HTTP error handling.

---

## Quick Start

- Chose you`re mode POLLING/ON_DEMAND
- Type you`re API key

## Installation

### Maven
```xml
<dependency>
  <groupId>com.example</groupId>
  <artifactId>weather-sdk</artifactId>
  <version>1.0.0</version>
</dependency>
