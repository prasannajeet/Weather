package com.prasan.weather.pojo;

/**
 * POJO for the 10 day forecast list
 */

public class Forecast {
    private String minTemp; // Maximum temperature for the day
    private String maxTemp; // Minimum temperature for the day
    private String weatherDesc; // Description of the weather
    private String weatherIcon; // Icon ID provided in response

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }
}
