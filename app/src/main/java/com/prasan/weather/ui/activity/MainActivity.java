package com.prasan.weather.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prasan.weather.R;

/**
 * Activity only used to host fragment
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Fragment hosted in XML statically, alternatively can be called from Java
    }
}
