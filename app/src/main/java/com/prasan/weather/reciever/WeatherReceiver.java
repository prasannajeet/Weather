package com.prasan.weather.reciever;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

import com.prasan.weather.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Recieves the result from the Weather Service and updates UI accordingly
 */

public class WeatherReceiver extends ResultReceiver {

    private Context mContext;
    private OnApiResponseReceive interfaceInstance;

    public WeatherReceiver(Handler handler, Context ctx, OnApiResponseReceive instance) {
        super(handler);
        this.mContext = ctx;
        this.interfaceInstance = instance;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);

        // Interface call to stop refreshing
        interfaceInstance.stopRefreshLayout();

        switch (resultCode) {
            case 200: // Data pulled successfully for current weather
                try {
                    JSONObject resultJson = new JSONObject(resultData.getString("string"));

                    // Check if the cod for the response is 200 or not
                    if (resultJson.optInt("cod") == 200) {
                        // Weather data obtained, parse into Pojo and display in UI
                        interfaceInstance.parseCurrentWeatherData(resultJson);

                    } else {
                        // There was an error, show message to user
                        Utils.showSimpleDialog(mContext, "API Error", String.format(Locale.US, "%s::%s. Please pull to refresh and try again", resultJson.optInt("cod"), resultJson.optInt("message")));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    // Show an "unknown error" dialog
                    Utils.showSimpleDialog(mContext, "Error", "Unknown error. Please try again");
                }

                break;
            case 300: // Data obtained successfully for 5 day weather forecast

                try {
                    JSONObject resultJson = new JSONObject(resultData.getString("string"));

                    // Check if the cod for the response is 200 or not
                    if (resultJson.optInt("cod", 404) == 200) {

                        // Parse data and fill in arraylist
                        interfaceInstance.parseForecastWeatherData(resultJson);
                    } else {
                        // There was an error, show message to user
                        Utils.showSimpleDialog(mContext, "API Error", String.format(Locale.US, "%s::%s. Please pull to refresh and try again", resultJson.optInt("cod"), resultJson.optInt("message")));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    // Show an "unknown error" dialog
                    Utils.showSimpleDialog(mContext, "Error", "Unknown error. Please try again");
                }


                break;
            case 500:
                if (resultData != null && resultData.getString("string") != null) {
                    Utils.showSimpleDialog(mContext, "Error", resultData.getString("string"));
                } else {
                    Utils.showSimpleDialog(mContext, "Error", "Bad data recieved from server. Try again");
                }


        }

    }

    public interface OnApiResponseReceive {
        void stopRefreshLayout();

        void parseCurrentWeatherData(JSONObject resultJson) throws JSONException;

        void parseForecastWeatherData(JSONObject resultJson) throws JSONException;
    }
}
