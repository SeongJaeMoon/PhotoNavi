package com.example.sungju1.photonavi;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by SungJu1 on 2017-07-19.
 */

public class LoadingDialogActivity extends AppCompatActivity {


    public void progressON() {
        LoadingDialogSetting.getInstance().progressON(this, null);
    }

    public void progressON(String message) {
        LoadingDialogSetting.getInstance().progressON(this, message);
    }

    public void progressOFF() {
        LoadingDialogSetting.getInstance().progressOFF();
    }

}