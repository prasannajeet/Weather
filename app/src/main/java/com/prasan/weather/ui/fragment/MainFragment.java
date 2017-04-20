package com.prasan.weather.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prasan.weather.R;
import com.prasan.weather.adapter.ForecastAdapter;
import com.prasan.weather.pojo.Forecast;
import com.prasan.weather.pojo.Weather;
import com.prasan.weather.reciever.WeatherReceiver;
import com.prasan.weather.service.WeatherService;
import com.prasan.weather.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static com.prasan.weather.utils.Constants.API_KEY;
import static com.prasan.weather.utils.Constants.CURRENT_WEATHER_URL;
import static com.prasan.weather.utils.Constants.TEN_DAY_FORECAST_URL;
import static com.prasan.weather.utils.Constants.WEATHER_ICON_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements View.OnClickListener, WeatherReceiver.OnApiResponseReceive {

    // Util values
    private String mCity;
    private SharedPreferences pref;

    // UI components
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayout mWeatherLayout;
    private TextView emptyView;
    private LinearLayout mCityInputLayout;
    private EditText mCityNameEditText;
    private FloatingActionButton mCityInputBtn;
    private Button mCityNameSubmitBtn;
    private ImageView mWeatherIcon;
    private TextView mCityName;
    private TextView mCurrentTemp;
    private TextView mDescription;
    private TextView mMinTemp;
    private TextView mMaxTemp;
    private TextView mHumidity;

    // Data Objects
    private Weather currentWeather = null;
    private ArrayList<Forecast> forecastArrayList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true); // I used fragment to ensure change in orientation has no affect in the app behavior via this call
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and initialize views
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initLayout(view);
        return view;
    }


    /**
     * Initiates the layout views
     * @param view - View object from onCreateView()
     */
    private void initLayout(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.ten_day_weather);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mWeatherLayout = (LinearLayout) view.findViewById(R.id.city_weather_data);
        emptyView = (TextView) view.findViewById(R.id.empty_city_placeholder);
        mCityInputLayout = (LinearLayout) view.findViewById(R.id.city_input_layout);
        mCityNameEditText = (EditText) view.findViewById(R.id.city_editext);
        mCityNameSubmitBtn = (Button) view.findViewById(R.id.city_submit);
        mWeatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
        mCityName = (TextView) view.findViewById(R.id.city_name);
        mCurrentTemp = (TextView) view.findViewById(R.id.current_temp);
        mDescription = (TextView) view.findViewById(R.id.weather_desc);
        mMinTemp = (TextView) view.findViewById(R.id.min_temp);
        mMaxTemp = (TextView) view.findViewById(R.id.max_temp);
        mHumidity = (TextView) view.findViewById(R.id.humidity);
        mCityInputBtn = (FloatingActionButton) view.findViewById(R.id.enter_city);


        //Configure
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Listeners
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // UI interaction to visualize webservice call
                sendCurrentDataRequest();
            }
        });
        mCityInputBtn.setOnClickListener(this);
        mCityNameSubmitBtn.setOnClickListener(this);

        checkSavedCityInfo();
    }

    /**
     * Runs when the app is loaded, shows appropriate UI if City is saved already or asks for input if not
     */
    private void checkSavedCityInfo() {
        // Check if city is already input, if yes call API to retrieve weather info, else inform user to input city
        pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        mCity = pref.getString(getString(R.string.city_name), null);

        if(mCity != null) { // City value was not saved in preferences
            showWeatherData(); // Flip UI to show the weather data
            loadDataOnAppLoad();
        } else {
            showEmptyLayout(); // Show a layout requesting user to input city
        }
    }

    /**
     * This is a UI gimmick to show the swipeable layout to the user for the first time the app loads, as a tutorial
     */
    private void loadDataOnAppLoad() {
        //Refresh layout
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
            }
        });
        sendCurrentDataRequest(); // This sends the webservice requests for current/forecast
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mRefreshLayout.isRefreshing())
                    mRefreshLayout.setRefreshing(false);
            }
        }, 5000);
    }

    /**
     * Calls the webservice to show current weather layout
     */
    private void sendCurrentDataRequest() {
        if (Utils.isNetworkAvailable(getContext())) {
            // Webservice call
            String url = String.format(Locale.US, CURRENT_WEATHER_URL, mCity, API_KEY);
            ResultReceiver rr = new WeatherReceiver(new Handler(), getContext(), this);

            Bundle b = new Bundle();
            b.putString(getString(R.string.api_url), url);
            b.putBoolean(getString(R.string.current_weather), true);
            b.putParcelable(getString(R.string.result_reciever), rr);

            Intent i = new Intent(getActivity(), WeatherService.class);
            i.putExtras(b);
            getActivity().startService(i);

            sendTenDayForecastRequest();
        } else {
            // Show no Internet available dialog
            Utils.showSimpleDialog(getContext(), getString(R.string.network_error_title), getString(R.string.internet_unavailable_error_message));
        }

    }

    /**
     * API call to populate the forecast list
     */
    private void sendTenDayForecastRequest() {
        if(Utils.isNetworkAvailable(getContext())) {
            String url = String.format(Locale.US, TEN_DAY_FORECAST_URL, mCity, API_KEY);
            ResultReceiver rr = new WeatherReceiver(new Handler(), getContext(), this);

            Bundle b = new Bundle();
            b.putString(getString(R.string.api_url), url);
            b.putBoolean(getString(R.string.current_weather), false);
            b.putParcelable(getString(R.string.result_reciever), rr);

            Intent i = new Intent(getActivity(), WeatherService.class);
            i.putExtras(b);
            getActivity().startService(i);
        } else {
            // Show Internet Not available dialog
            Utils.showSimpleDialog(getContext(), getString(R.string.network_error_title), getString(R.string.internet_unavailable_error_message));
        }



    }


    @Override
    public void onClick(View v) {
        if(v == mCityInputBtn) {
            if((mCityInputLayout.getVisibility() == LinearLayout.GONE)) {
                showCityInputLayout();
            } else {
                hideCityInputLayout();
            }
        } else if(v == mCityNameSubmitBtn) {
            if(mCityNameEditText.getText().toString().isEmpty()) {
                mCityNameEditText.setError("Please enter city name");
            } else {
                mCity = mCityNameEditText.getText().toString();
                // A better way to write this implementation will be to save the JSON list of cities (http://bulk.openweathermap.org/sample/)
                // provided by OpenWeather API in the app as a raw file/SQLiteDB table. When the input is validated, we validate the city name
                // with the list of cities with "country": "US" attribute in that list. In case of
                // ambiguity, we show user a list of <city>,<state> to choose city from. We then obtain
                // the city ID from the json list provided by OpenWeather and obtain the data via the API
                // by passing the city ID instead of using the <city>,US query as implemented in this solution
                // This is a very simplistic implementation due to time constraints
                saveCityNameToPref(pref, mCity);
                hideCityInputLayout();
                loadDataOnAppLoad();
            }
        }
    }


    private void saveCityNameToPref(SharedPreferences preferences, String city) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(getString(R.string.city_name), city);
        edit.apply();
    }
    /**
     * Parses the Json response for current weather for city into POJO
     * @param resultJson - json obtained from API
     * @throws JSONException
     */
    private void parseWeatherObject(JSONObject resultJson) throws JSONException {

        showWeatherData();
        currentWeather = new Weather();
        currentWeather.setIconId(resultJson.optJSONArray("weather").getJSONObject(0).optString("icon", "Null"));
        currentWeather.setCityName(resultJson.optString("name", "Null"));
        currentWeather.setCurrentTempInF(String.valueOf(kelvinToFarenheit(Double.parseDouble(resultJson.optJSONObject("main").optString("temp", "0.00")))));
        currentWeather.setWeatherDesc(resultJson.optJSONArray("weather").getJSONObject(0).optString("main", "Null"));
        currentWeather.setMinTempInF(String.valueOf(kelvinToFarenheit(resultJson.optJSONObject("main").optDouble("temp_min", 0.00))));
        currentWeather.setMaxTempInF(String.valueOf(kelvinToFarenheit(resultJson.optJSONObject("main").optDouble("temp_max", 0.00))));
        currentWeather.setHumidity(String.valueOf((int) resultJson.optJSONObject("main").optDouble("humidity", 0.00)));

    }

    /**
     * Parses the 10 day forecast response from API
     * @param resultJson - json obtained from API
     * @throws JSONException
     */
    private void parseForcastJsonObject(JSONObject resultJson) throws JSONException {
        forecastArrayList = new ArrayList<>();

        int count = resultJson.optInt("cnt", 0);
        if(count > 0) {
            JSONArray forecastList = resultJson.optJSONArray("list");
            if(forecastList != null) {
                for(int i = 0; i < count; i++) {
                    Forecast forecast = new Forecast();
                    forecast.setWeatherDesc(forecastList.optJSONObject(i).optJSONArray("weather").optJSONObject(0).optString("main", "invalid"));
                    forecast.setWeatherIcon(forecastList.optJSONObject(i).optJSONArray("weather").optJSONObject(0).optString("icon", null));
                    forecast.setMinTemp(String.valueOf(kelvinToFarenheit(forecastList.optJSONObject(i).optJSONObject("temp").optDouble("min", 0.00))));
                    forecast.setMaxTemp(String.valueOf(kelvinToFarenheit(forecastList.optJSONObject(i).optJSONObject("temp").optDouble("max", 0.00))));
                    forecastArrayList.add(forecast);
                }
            }
        }
    }

    /**
     * Standard K to F conversion formula
     * @param kelvin -- Temp in K
     * @return Temp in F
     */
    private int kelvinToFarenheit(double kelvin) {
        return (int) (((kelvin - 273) * 9/5) + 32);
    }


    /**
     * Populate selected city weather data with values from service
     */
    private void populateCurrentWeatherLayoutWithData() {

        Picasso.with(getActivity()).load(String.format(WEATHER_ICON_URL, currentWeather.getIconId())).resize(200, 200).into(mWeatherIcon);
        mCityName.setText(currentWeather.getCityName());
        mCurrentTemp.setText(String.format(Locale.US, "Temp(F): %s", currentWeather.getCurrentTempInF()));
        mDescription.setText(currentWeather.getWeatherDesc());
        mMinTemp.setText(String.format(Locale.US, "Min(F): %s", currentWeather.getMinTempInF()));
        mMaxTemp.setText(String.format(Locale.US, "Max(F): %s", currentWeather.getMaxTempInF()));
        mHumidity.setText(String.format("Humidity: %s%s", currentWeather.getHumidity(), "%"));

    }

    /**
     * Method to show the weather layout and hide other UI layouts
     */
    private void showWeatherData() {
        mWeatherLayout.setVisibility(LinearLayout.VISIBLE);
        emptyView.setVisibility(TextView.GONE);
        mCityInputLayout.setVisibility(LinearLayout.GONE);

    }

    /**
     * Method to display a placeholder indicating user to enter a city in the appropriate option
     */
    private void showEmptyLayout() {
        mWeatherLayout.setVisibility(LinearLayout.GONE);
        emptyView.setVisibility(TextView.VISIBLE);
        mCityInputLayout.setVisibility(LinearLayout.GONE);
    }

    /**
     * Method to display a layout for the user to enter a city
     */
    private void showCityInputLayout() {
        mCityInputBtn.setImageResource(R.drawable.ic_home_white_48dp);
        mWeatherLayout.setVisibility(LinearLayout.GONE);
        emptyView.setVisibility(TextView.GONE);
        mCityInputLayout.setVisibility(LinearLayout.VISIBLE);
        if(mCity != null) {
            mCityNameEditText.setText(mCity);
        }
    }

    /**
     * Method to hide the city input layout
     */
    private void hideCityInputLayout() {
        mCityInputBtn.setImageResource(R.drawable.ic_room_white_48dp);

        mCityInputLayout.setVisibility(LinearLayout.GONE);
        if(mCity == null) {
            showEmptyLayout();
        } else {
            loadDataOnAppLoad();
        }
    }


    @Override
    public void stopRefreshLayout() {
        if (mRefreshLayout != null && mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void parseCurrentWeatherData(JSONObject resultJson) throws JSONException {
        parseWeatherObject(resultJson);
        populateCurrentWeatherLayoutWithData();
    }

    @Override
    public void parseForecastWeatherData(JSONObject resultJson) throws JSONException {
        parseForcastJsonObject(resultJson);
        mRecyclerView.setAdapter(new ForecastAdapter(forecastArrayList));
    }

}
