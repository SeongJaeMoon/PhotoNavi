package com.example.sungju1.photonavi;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

/**
 * Created by SungJu1 on 2017-07-17.
 */

public class BackPressCloseHandler extends Activity {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;
    private AdView mAdView;
    private Dialog dialog;
    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }



    public void onBackPressed() {
    /*   if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();

            toast.cancel();
        }*/
      //  dialog.show();
        HomeActivity.closedialog.show();
    }

    public void showGuide() {
      /*  toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();*/
    }

}


