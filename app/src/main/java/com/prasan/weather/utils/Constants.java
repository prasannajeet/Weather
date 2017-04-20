package com.prasan.weather.utils;

/**
 * Constants used across the application
 */

public class Constants {

    public static final String API_KEY = "f18700f82a484a8ca4220d2f812eb74b"; //API key used for generating weather data

    public static final String CURRENT_WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s,US&appid=%s";
    public static final String TEN_DAY_FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s,US&cnt=10&appid=%s";
    public static final String WEATHER_ICON_URL = "http://openweathermap.org/img/w/%s.png";

}
