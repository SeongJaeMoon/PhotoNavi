package com.example.sungju1.photonavi;

import android.app.ActivityGroup;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by SungJu1 on 2017-10-26.
 */

public class HomeActivity extends ActivityGroup {
    private AdView mAdView;
    public static TabHost tabHost;
    private static final int CAMERA_REQUEST = 1000;
    private static final int GALLERY_REQUEST = 1001;
    private static final int video_REQUEST = 2001;
    private static final int video_Capture_REQUEST = 2002;
    private Uri mCapturedImageURI;
    protected Uri galleryUri;
    private Uri videoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


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


        ImageButton homeBtn1 = (ImageButton) findViewById(R.id.homebtn1);
        homeBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "PhotoNavi_");
                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                MainActivity.uniUri = mCapturedImageURI;
                Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                startActivityForResult(intentPicture, CAMERA_REQUEST);
            }
        });
        ImageButton homeBtn2 = (ImageButton) findViewById(R.id.homebtn2);
        homeBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                //intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
               startActivityForResult(intent, GALLERY_REQUEST);
            }
        });
        ImageButton homeBtn3 = (ImageButton) findViewById(R.id.homebtn3);
        homeBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                //values.put(MediaStore.Video.Media.TITLE, "PhotoNavi_");
                values.put(MediaStore.META_DATA_STILL_IMAGE_CAMERA_PREWARM_SERVICE, true);
                mCapturedImageURI = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                MainActivity.uniUri = mCapturedImageURI;
                Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intentVideo.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                startActivityForResult(intentVideo, video_Capture_REQUEST);
            }
        });
        ImageButton homeBtn4 = (ImageButton) findViewById(R.id.homebtn4);
        homeBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                // intent.setType(android.provider.MediaStore.Video.Media.CONTENT_TYPE);
                //   intent.setType(MediaStore.Video.Media.CONTENT_TYPE);
                //     intent.setType("video/*");
                startActivityForResult(intent, video_REQUEST);
            }
        });



        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup(getLocalActivityManager());


        TabHost.TabSpec tabSpecTab1 = tabHost.newTabSpec("TAB1").setIndicator("홈");
        tabSpecTab1.setContent(R.id.tab1);
        tabHost.addTab(tabSpecTab1);


        Intent reIntent = getIntent();
        final double locationLat = reIntent.getExtras().getDouble("lat");
        final double locationLon = reIntent.getExtras().getDouble("lon");

        TabHost.TabSpec tabSpecTab2 = tabHost.newTabSpec("TAB2").setIndicator("지도").setContent(new Intent(HomeActivity.this, MainActivity.class)
                .putExtra("lat", locationLat).putExtra("lon", locationLon));
              	                        //.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        tabHost.addTab(tabSpecTab2);

        TabHost.TabSpec tabSpecTab3 = tabHost.newTabSpec("TAB3").setIndicator("로그인정보");
        tabSpecTab3.setContent(R.id.tab3);
        tabHost.addTab(tabSpecTab3);

        TabHost.TabSpec tabSpecTab4 = tabHost.newTabSpec("TAB4").setIndicator("문의");
        tabSpecTab4.setContent(new Intent(HomeActivity.this, Tap4Activity.class));
        tabHost.addTab(tabSpecTab4);

        tabHost.setCurrentTab(0);
        if(MainActivity.type !=null) {
            tabHost.setCurrentTab(1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        // 사진, 갤러리, Crop
        switch (requestCode) {
            case CAMERA_REQUEST: {
                tabHost.setCurrentTab(1);
                break;
            }
            case GALLERY_REQUEST: {
                galleryUri = data.getData();
                MainActivity.uniUri = galleryUri;
                tabHost.setCurrentTab(1);
                break;
            }
            case video_Capture_REQUEST: {
                videoUri = data.getData();
                MainActivity.uniUri = videoUri;
                tabHost.setCurrentTab(1);
                break;
            }
            //동영상 가져오기
            case video_REQUEST: {
                videoUri = data.getData();
                MainActivity.uniUri = videoUri;
                tabHost.setCurrentTab(1);
                break;
            }

        }
    }
}
