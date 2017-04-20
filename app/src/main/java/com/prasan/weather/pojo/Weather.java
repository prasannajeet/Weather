package com.prasan.weather.pojo;

/**
 * POJO class displayed in the current weather UI
 */

public class Weather {

    private String cityName; // Name of the city
    private String currentTempInF; // Current temperature at the time of loading data
    private String weatherDesc; // weather description
    private String minTempInF; // Min temp for the day
    private String maxTempInF; // Max temp for the day
    private String humidity; // Humidity in %age
    private String iconId; // icon Id to show image for

    public String getCityName() {
        return cityName;
    }

    public String getCurrentTempInF() {
        return currentTempInF;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public String getMinTempInF() {
        return minTempInF;
    }

    public String getMaxTempInF() {
        return maxTempInF;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCurrentTempInF(String currentTempInF) {
        this.currentTempInF = currentTempInF;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public void setMinTempInF(String minTempInF) {
        this.minTempInF = minTempInF;
    }

    public void setMaxTempInF(String maxTempInF) {
        this.maxTempInF = maxTempInF;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }
}
