package com.example.weatherservice.weatherservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="weather-client-v1", url="http://api.openweathermap.org/")
interface OpenWeatherClientV1 {
    @RequestMapping(method = RequestMethod.GET, value = "data/2.5/weather?units=imperial&zip={zip}&appid={apiKey}", consumes = "application/json")
    OpenWeatherApiResponse getWeatherbyZip(@RequestParam("q") String q, @RequestParam("apiKey") String apiKey);
}

@RestController()
@RequestMapping("api/v1")
public class WeatherRestControllerV1 {

    @Value( "${open-weather-api-key}" )
    private String apiKey;

    Logger logger = LoggerFactory.getLogger(WeatherRestControllerV1.class);

    private final OpenWeatherClientV1 weatherClient;

    public WeatherRestControllerV1(OpenWeatherClientV1 weatherClient) {
        this.weatherClient = weatherClient;
    }

    @GetMapping("/weather")
    OpenWeatherApiResponse getWeatherByZip(@RequestParam("q") String q) {
        logger.info("Getting weather for zip = " + q);
        OpenWeatherApiResponse weatherApiResponse = weatherClient.getWeatherbyZip(q, apiKey);
        return weatherApiResponse;
    }

}