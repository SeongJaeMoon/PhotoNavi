package com.example.sungju1.photonavi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


/**
 * Created by SungJu1 on 2017-07-15.
 */

public class IntroActivity extends LoadingDialogActivity {
    public static final int RequestPermissionCode = 1;
    private BackPressCloseHandler backPressCloseHandler;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
     //   backPressCloseHandler.onBackPressed();
       // MainActivity.closedialog.show();
    }

    public Double locationLat = null;
    public Double locationLon = null;
    Intent intent;
    LocationListener locationListener;
    LocationManager manager;
    LoadingDialogActivity loadingDialogActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("탭오류","인트로실행1");
        loadingDialogActivity = new LoadingDialogActivity();

        //권한부여
        if (checkPermission()&&chkGpsService()) {
            manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationLat = location.getLatitude();
                    locationLon = location.getLongitude();
                    manager.removeUpdates(locationListener);
                    intent = new Intent(new Intent(IntroActivity.this, HomeActivity.class));
                    intent.putExtra("lat", locationLat);
                    intent.putExtra("lon", locationLon);
                   // progressOFF();
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {


                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
           intent = getIntent();
            String action = intent.getAction();
            MainActivity.action = action;
            String type = intent.getType();
            Uri uri  ;
            if (action.equals(Intent.ACTION_SEND)) {
                Log.d("인텐트타입",type);
                uri = intent.getParcelableExtra(intent.EXTRA_STREAM);
                //uri = intent.getData();
                MainActivity.type = type;
                MainActivity.uniUri = uri;
                MainActivity.pass=false;
            }
           // progressON("Loading...");

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        } else {
            if(chkGpsService()) {
                requestPermission();
            }
            else {
                requestPermission();
            }
        }

        backPressCloseHandler = new BackPressCloseHandler(this);

    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,
                        ACCESS_FINE_LOCATION
                }, RequestPermissionCode);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternalStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessFineLocationPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && WriteExternalStoragePermission && ReadExternalStoragePermission && AccessFineLocationPermission) {

                        Toast.makeText(this, "권한 승인", Toast.LENGTH_LONG).show();
                        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                        locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                locationLat = location.getLatitude();
                                locationLon = location.getLongitude();
                                manager.removeUpdates(locationListener);
                                intent = new Intent(new Intent(IntroActivity.this, HomeActivity.class));
                                intent.putExtra("lat", locationLat);
                                intent.putExtra("lon", locationLon);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {


                            }

                            @Override
                            public void onProviderEnabled(String s) {

                            }

                            @Override
                            public void onProviderDisabled(String s) {

                            }
                        };
                        intent = getIntent();
                        String action = intent.getAction();
                        String type = intent.getType();
                        Uri uri = null;
                        if (action.equals(Intent.ACTION_SEND)) {
                            uri = (Uri) intent.getParcelableExtra(intent.EXTRA_STREAM);
                            MainActivity.uniUri = uri;
                        }

                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


                    } else {
                        Toast.makeText(this, "권한 거절", Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }

    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    public double getLat() {
        return locationLat;
    }

    public double getLon() {
        return locationLon;
    }

    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        Log.d(gps, "aaaa");

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }


}
