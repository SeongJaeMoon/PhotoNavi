package com.example.sungju1.photonavi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by SungJu1 on 2017-10-30.
 */

public class Tap4Activity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap4layout);
        backPressCloseHandler = new BackPressCloseHandler(this);
    }
}
