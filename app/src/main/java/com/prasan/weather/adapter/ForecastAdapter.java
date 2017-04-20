package com.prasan.weather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.prasan.weather.R;
import com.prasan.weather.pojo.Forecast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import static com.prasan.weather.utils.Constants.WEATHER_ICON_URL;

/**
 * Forecast list adapter to populate the 10 day forecast list
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> implements View.OnClickListener {

    private ArrayList<Forecast> mForecastList = new ArrayList<>(); //Dataset
    private Context ctx;

    public ForecastAdapter(ArrayList<Forecast> dataset) {
        this.mForecastList = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        this.ctx = parent.getContext(); // Stored for use in Picasso to load image in onBindViewHolder

        //Inflate UI
        View view = LayoutInflater.from(ctx).inflate(R.layout.forecast_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Forecast forecast = mForecastList.get(position);

        // Populate UI
        holder.mMaxTemp.setText(String.format(Locale.US, "Max Temp (F): %s", forecast.getMaxTemp()));
        holder.mMinTemp.setText(String.format(Locale.US, "Min Temp (F): %s", forecast.getMinTemp()));
        holder.mWeatherDesc.setText(forecast.getWeatherDesc());
        Picasso.with(ctx).load(String.format(WEATHER_ICON_URL, forecast.getWeatherIcon())).resize(200, 200).into(holder.mWeatherIcon);

    }

    @Override
    public int getItemCount() {
        // Can be hardcoded to 10 but would rather keep dynamic, personal preference
        if (mForecastList != null) {
            return mForecastList.size();
        } else {
            return 0;
        }

    }

    @Override
    public void onClick(View v) {
        // TODO Implement future features (item click shows more weather details etc.)
        Log.d(getClass().getSimpleName(), "Row clicked");
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mWeatherIcon;
        private final TextView mWeatherDesc;
        private final TextView mMinTemp;
        private final TextView mMaxTemp;

        ViewHolder(View v) {
            super(v);

            // Initialize UI
            mWeatherIcon = (ImageView) v.findViewById(R.id.fc_weather_icon);
            mWeatherDesc = (TextView) v.findViewById(R.id.fc_weather_desc);
            mMinTemp = (TextView) v.findViewById(R.id.fc_min_temp);
            mMaxTemp = (TextView) v.findViewById(R.id.fc_max_temp);
            v.setOnClickListener(ForecastAdapter.this);
        }
    }
}
