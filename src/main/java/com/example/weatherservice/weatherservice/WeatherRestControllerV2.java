package com.example.weatherservice.weatherservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@FeignClient(name="weather-client-v2", url="http://api.openweathermap.org/")
interface OpenWeatherClientV2 {
    @RequestMapping(method = RequestMethod.GET, value = "data/2.5/weather?units=imperial&zip={zip}&appid={apiKey}", consumes = "application/json")
    OpenWeatherApiResponse getWeatherByZip(@RequestParam("zip") String zip, @RequestParam("apiKey") String apiKey);

    @RequestMapping(method = RequestMethod.GET, value = "data/2.5/weather?units=imperial&q={cityStateCountry}&appid={apiKey}", consumes = "application/json")
    OpenWeatherApiResponse getWeatherByCity(@RequestParam("cityStateCountry") String cityStateCountry, @RequestParam("apiKey") String apiKey);
}

@RestController("v2")
@RequestMapping("api/v2")
public class WeatherRestControllerV2 {

    @Value( "${open-weather-api-key}" )
    private String apiKey;

    Logger logger = LoggerFactory.getLogger(WeatherRestControllerV2.class);

    private final OpenWeatherClientV2 weatherClient;

    public WeatherRestControllerV2(OpenWeatherClientV2 weatherClient) {
        this.weatherClient = weatherClient;
    }

    @GetMapping("/weather")
    OpenWeatherApiResponse getWeather(@RequestParam(required = false) String zip, @RequestParam(required = false) String cityStateCountry) {
        if (zip != null) {
            logger.info("Getting weather for zip = " + zip);
            OpenWeatherApiResponse weatherApiResponse = weatherClient.getWeatherByZip(zip, apiKey);
            return weatherApiResponse;
        }
        else if (cityStateCountry != null) {
            logger.info("Getting weather for cityStateCountry = " + cityStateCountry);
            OpenWeatherApiResponse weatherApiResponse = weatherClient.getWeatherByCity(cityStateCountry, apiKey);
            return weatherApiResponse;
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must specify either `zip` or `cityStateCountry`");
        }
    }

}