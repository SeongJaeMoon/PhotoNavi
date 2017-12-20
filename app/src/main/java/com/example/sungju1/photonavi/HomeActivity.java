package com.example.sungju1.photonavi;

import android.app.ActivityGroup;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SungJu1 on 2017-10-26.
 */

public class HomeActivity extends ActivityGroup {
   BackPressCloseHandler backPressCloseHandler;
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }
    public void close2(View view) {
        MainActivity.uniUri=null;
        mainActivity.finish();
        finish();
    }

    public void cancle2(View view) {
        closedialog.cancel();
    }
    private static final String TAG = com.example.sungju1.photonavi.HomeActivity.class.getSimpleName();
    private GridView mGridView;
    private ArrayList<ImageAdapter>imageAdapters;
    private AdView mAdView1;
    static Dialog closedialog;
    private AdView mAdView;
    public static TabHost tabHost;
    private static final int CAMERA_REQUEST = 1000;
    private static final int GALLERY_REQUEST = 1001;
    private static final int video_REQUEST = 2001;
    private static final int video_Capture_REQUEST = 2002;
    private Uri mCapturedImageURI;
    protected Uri galleryUri;
    private Uri videoUri;
    public MainActivity mainActivity;
    private ImageAdapter mImageAdapter;
    private String basePath = null;
    private List<String> mPathList;
    private TextView mText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("갤6 확인","홈 진입");
        super.onCreate(savedInstanceState);
        Log.d("갤6 확인","홈 진입2");
        setContentView(R.layout.activity_home);

        mainActivity = new MainActivity();
        MobileAds.initialize(this, "ca-app-pub-4280928874742229/6841776267");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
            }
        });
        MobileAds.initialize(this, "ca-app-pub-4280928874742229/7604672524");

        closedialog = new Dialog(this);
        closedialog.setContentView(R.layout.closediolg);
        mAdView1 = (AdView) closedialog.findViewById(R.id.adView2);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest1);
        mAdView1.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.i("Ads", "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.i("Ads", "onAdOpened");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Log.i("Ads", "onAdClosed");
            }
        });




        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup(getLocalActivityManager());


        TabHost.TabSpec tabSpecTab1 = tabHost.newTabSpec("TAB1").setIndicator("홈").setContent(new Intent(HomeActivity.this, Tap1Activity.class)
               .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        tabHost.addTab(tabSpecTab1);


        Intent reIntent = getIntent();
        final double locationLat = reIntent.getExtras().getDouble("lat");
        final double locationLon = reIntent.getExtras().getDouble("lon");

        TabHost.TabSpec tabSpecTab2 = tabHost.newTabSpec("TAB2").setIndicator("지도").setContent(new Intent(HomeActivity.this, MainActivity.class)
                .putExtra("lat", locationLat).putExtra("lon", locationLon));//.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        tabHost.addTab(tabSpecTab2);

        TabHost.TabSpec tabSpecTab3 = tabHost.newTabSpec("TAB3").setIndicator("로그인정보");
        tabSpecTab3.setContent(R.id.tab3);
        tabHost.addTab(tabSpecTab3);

        TabHost.TabSpec tabSpecTab4 = tabHost.newTabSpec("TAB4").setIndicator("문의");
        tabSpecTab4.setContent(new Intent(HomeActivity.this, Tap4Activity.class));
        tabHost.addTab(tabSpecTab4);

        tabHost.setCurrentTab(1);
        tabHost.setCurrentTab(0);
        if(MainActivity.type !=null) {
            MainActivity.pass=true;
            tabHost.setCurrentTab(1);
        }
        backPressCloseHandler = new BackPressCloseHandler(this);

    }



}
