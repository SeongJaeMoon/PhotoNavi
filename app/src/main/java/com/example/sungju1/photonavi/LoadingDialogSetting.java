package com.example.sungju1.photonavi;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;

/**
 * Created by SungJu1 on 2017-07-19.
 */

public class LoadingDialogSetting extends Application {

    private static LoadingDialogSetting loadingDialogSetting;
    static AppCompatDialog progressDialog;
    static AppCompatDialog listDialog;


    public static LoadingDialogSetting getInstance() {
        return loadingDialogSetting;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadingDialogSetting = this;
    }

    public static void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            //progressDialog = new AppCompatDialog(activity);
           // progressDialog.setCancelable(false);
            //progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
           // progressDialog.setContentView(R.layout.loadingdialog);
           // progressDialog.show();

        }
    }

    public static void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
    }

    public static void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
