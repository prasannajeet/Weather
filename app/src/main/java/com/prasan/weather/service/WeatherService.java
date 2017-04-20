package com.prasan.weather.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.prasan.weather.R;
import com.prasan.weather.utils.VolleySingleton;

import org.json.JSONObject;

/**
 * Intent Service parsing the weather data requests using Volley and OpenWeather GET APIs
 */

public class WeatherService extends IntentService {
    public WeatherService() {
        super("Weather");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null && intent.getExtras() != null) {
            final boolean isRequestForCurrentWeather = intent.getExtras().getBoolean(getString(R.string.current_weather)); // Used to differentiate current/forecast calls
            String url = intent.getExtras().getString(getString(R.string.api_url)); // GET URL to recieve data
            final ResultReceiver resultReciver = intent.getExtras().getParcelable(getString(R.string.result_reciever)); // ResultReciever object implemented in Fragment to process result

            if (url != null && resultReciver != null) {
                JsonObjectRequest currentWeatherRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Bundle bundle = new Bundle();
                        bundle.putString("string", response.toString());
                        if (isRequestForCurrentWeather) {
                            resultReciver.send(200, bundle); // Notify receiver to parse it as "Current weather" response
                        } else {
                            resultReciver.send(300, bundle); // Notify receiver to parse it as "10 Day Forecast" response
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Bundle bundle = new Bundle();
                        bundle.putString("string", error.getLocalizedMessage());
                        resultReciver.send(500, bundle); // Generic error message, to be shown as a dialog
                    }
                });
                VolleySingleton.getInstance(this.getApplicationContext()).addToRequestQueue(currentWeatherRequest);
            }
        }

    }
}
