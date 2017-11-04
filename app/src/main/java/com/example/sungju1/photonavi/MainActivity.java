package com.example.sungju1.photonavi;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.daum.android.map.coord.MapCoordLatLng;
import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener,
        MapView.CurrentLocationEventListener,
        MapReverseGeoCoder.ReverseGeoCodingResultListener,
        MapView.POIItemEventListener {

    private static final int CAMERA_REQUEST = 1000;
    private static final int GALLERY_REQUEST = 1001;
    private static final int video_REQUEST = 2001;
    private static final int video_Capture_REQUEST = 2002;
    private Uri mCapturedImageURI;
    private String[] locationArray;
    MapView mapView;
    private int buttonClickCount = 0;
    private ImageButton myLocationButton;
    private MapReverseGeoCoder geoCoder;
    protected static String daumAPI_KEY = "30b632481ffc93211629d4ad18939df2";
    private MapPoint myLocatonPoint;
    private Double myLacationlatitude;
    private Double myLacationlongitude;
    private String setMarkerName;

    MapPoint MARKER_POINT;
    MapPoint SELECT_POINT;

    String plusLocation;


    private short cntCurrentLocationUpdate = 0;
    String photoAddress;
    Bitmap cameraBitmap;
    Bitmap galleryBitmap;
    Bitmap uniBitmap;
    Dialog dialog;
    Dialog videodialog;
    Uri videoUri;

    ImageView iv;
    String balloonText;//현재 좌표값이지만 지오코딩 후 주소넣기
    AllowControlView allowControlView;
    public static float mapDegree;
    public double allowDegree;

    public static String action;

    private RelativeLayout container2;// 사용 X
    int num = 0;
    protected String cameraUrl;
    protected Uri galleryUri;
    protected static Uri uniUri;

    EditText phoneNum;
    Button okBtn;
    Button clBtn;
    public String phoneNumber;
    public String smsText;

    Intent reIntent;

    boolean on = true;
    ImageButton mapTypeSet;
    ImageButton poiTypeSet;


    EditText mEditTextQuery;
    Button mButtonSearch;
    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();

    RoadTracker roadTracker;
    CarTracker carTracker;
    ArrayList<MapCoordLatLng> mapPoints;
    ArrayList<MapCoordLatLng> mapPointPoints;
    ArrayList<String> mapPointsTurnType;
    ArrayList<String> mapPintsDescripton;
    ArrayList<String> roadInfoDescription;
    Dialog listDialog;
    TextView textView5;
    private BackPressCloseHandler backPressCloseHandler;
    LoadingDialogActivity loadingDialogActivity;

    TextView oddDistance;
    TextView roadInfo;
    Uri getVideoUri;
    public Double locationLat = null;
    public Double locationLon = null;
    private AdView mAdView;
    static Dialog closedialog;

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(final MapView mapView, final MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("선택하신 사진의 위치를 지우시겠습니까?");

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Toast.makeText(MainActivity.this,"지웠당",Toast.LENGTH_SHORT);
                mapView.removePOIItem(mapPOIItem);
                poiTypeSet.setImageResource(R.drawable.sp);
                MARKER_POINT = null;
                uniUri = null;

            }
        });
        // Cancel 버튼 이벤트
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }


    // CalloutBalloonAdapter 인터페이스 구현
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }


        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            Item item = mTagItemMap.get(poiItem.getTag());


            if (poiItem == null) return null;

            if (item != null) {
                ImageView imageViewBadge = (ImageView) mCalloutBalloon.findViewById(R.id.badge);
                TextView textViewTitle = (TextView) mCalloutBalloon.findViewById(R.id.title);
                textViewTitle.setText(item.title);
                TextView textViewDesc = (TextView) mCalloutBalloon.findViewById(R.id.desc);
                textViewDesc.setText(item.address);
                imageViewBadge.setImageDrawable(createDrawableFromUrl(item.imageUrl));
                if (balloonText != null) {
                    ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageURI(uniUri);
                    ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
                    ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText(balloonText);
                    balloonText = null;
                }
            }
            if (balloonText != null) {
                ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageURI(uniUri);
                ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
                ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText(balloonText);
                balloonText = null;
            }
            return mCalloutBalloon;
        }


        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        //backPressCloseHandler.onBackPressed();

        MobileAds.initialize(this, "ca-app-pub-4280928874742229/7604672524");

        mAdView = (AdView) closedialog.findViewById(R.id.adView2);
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
        backPressCloseHandler.onBackPressed();
        uniUri = null;
    }

    public void close2(View view) {
        finish();
    }

    public void cancle2(View view) {
        closedialog.cancel();
    }

    LayoutInflater inflater;
    RelativeLayout relativeLayout;
    RelativeLayout naviLayout;
    RelativeLayout.LayoutParams layoutParams;
    Window window;
    public static String type;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //super.setContentView(R.layout.activity_main); //메인 레이아웃 설정
        window = getWindow();
        window.setContentView(R.layout.activity_main); //메인 레이아웃 설정

        //버튼레이아웃 설정
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.button_setting, null);
        naviLayout = (RelativeLayout) inflater.inflate(R.layout.navi_layout, null);
        layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.FILL_PARENT);
        window.addContentView(relativeLayout, layoutParams);

        backPressCloseHandler = new BackPressCloseHandler(this);

        reIntent = getIntent();
        //   uniUri = reIntent;
        myLacationlatitude = reIntent.getExtras().getDouble("lat");
        myLacationlongitude = reIntent.getExtras().getDouble("lon");
        mEditTextQuery = (EditText) findViewById(R.id.editTextQuery); // 검색창
        myLocatonPoint = MapPoint.mapPointWithGeoCoord(myLacationlatitude, myLacationlongitude);

        mapView = new MapView(this);
        //다음이 제공하는 MapView객체 생성 및 API Key 설정
        mapView.setDaumMapApiKey(daumAPI_KEY);
        //xml에 선언된 map_view 레이아웃을 찾아온 후, 생성한 MapView객체 추가
        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);
        container.addView(mapView);


        //      ivImage = (ImageView) findViewById(R.id.iv_image); //메인 레이아웃 이미지뷰 현재 주석처리됨
        //      tvLocation = (TextView) findViewById(R.id.tv_location);
        myLocationButton = (ImageButton) findViewById(R.id.myLocation);
        mapView.setMapViewEventListener(this);// this에 MapView.MapViewEventListener 구현.
        mapView.setPOIItemEventListener(this); // this 에 Mapview.POI Event Handler 구현
        //다이얼로그 셋팅
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.imageview_plus);


        // phoneNum = (EditText) phoneDialog.findViewById(R.id.PhoneNum);
        //okBtn = (Button) phoneDialog.findViewById(R.id.okbtn);
        // clBtn = (Button) phoneDialog.findViewById(R.id.clbtn);

        listDialog = new Dialog(this);
        listDialog.setContentView(R.layout.list_view);
        //textView5 = (TextView)listDialog.findViewById(R.id.totaldist);

        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        iv = (ImageView) dialog.findViewById(R.id.imageView2);
        //  capImg = (ImageButton) findViewById(R.id.captureimg);
        container2 = (RelativeLayout) findViewById(R.id.map_view);
        mapTypeSet = (ImageButton) findViewById(R.id.mapTypeSet);
        poiTypeSet = (ImageButton) findViewById(R.id.poiTypeSet);

      /* reIntent = getIntent();
       action = reIntent.getAction();
       type = reIntent.getType();*/

        if (uniUri != null) {
            if (type == "image/*") {
                iv.setImageURI(uniUri);
                getImageDetail(getRealPathFromURI(uniUri));
            }else{
                getImageDetail(getVideoRealPathFromURI(uniUri));
            }

        }

        loadingDialogActivity = new LoadingDialogActivity();
        mButtonSearch = (Button) findViewById(R.id.buttonSearch); // 검색버튼
        mButtonSearch.setOnClickListener(new View.OnClickListener() { // 검색버튼 클릭 이벤트 리스너
            @Override
            public void onClick(View v) {
                String query = mEditTextQuery.getText().toString();
                if (query == null || query.length() == 0) {
                    Toast.makeText(MainActivity.this, "검색어를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                hideSoftKeyboard(); // 키보드 숨김
                MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude; // 위도
                double longitude = geoCoordinate.longitude; // 경도
                int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                int page = 3; // 페이지 번호 (1 ~ 3). 한페이지에 15개

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, daumAPI_KEY, new OnFinishSearchListener() {

                    @Override
                    public void onSuccess(List<Item> itemList) {
                        mapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                        showResult(itemList); // 검색 결과 보여줌

                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(MainActivity.this, "API_KEY의 제한 트래픽이 초과되었습니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        roadTracker = new RoadTracker(mapView);
        carTracker = new CarTracker(mapView);

        closedialog = new Dialog(this);
        closedialog.setContentView(R.layout.closediolg);


        //검색창 숨기기
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.serchlayout);
        linearLayout.setVisibility(View.INVISIBLE);
    }


    public void clickfab(View view) {

        final CharSequence[] items = {"사진촬영", "사진가져오기", "동영상촬영", "동영상가져오기", "취소"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("사진촬영")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "PhotoNavi_");
                    mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    uniUri = mCapturedImageURI;
                    Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intentPicture.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    startActivityForResult(intentPicture, CAMERA_REQUEST);

                } else if (items[item].equals("사진가져오기")) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    //intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent, GALLERY_REQUEST);

                } else if (items[item].equals("동영상촬영")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.TITLE, "PhotoNavi_");
                    values.put(MediaStore.META_DATA_STILL_IMAGE_CAMERA_PREWARM_SERVICE, true);
                    mCapturedImageURI = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                    MainActivity.uniUri = mCapturedImageURI;
                    Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intentVideo.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    startActivityForResult(intentVideo, video_Capture_REQUEST);

                } else if (items[item].equals("동영상가져오기")) {
                    // startActivityForResult( Intent.createChooser( new Intent(Intent.ACTION_PICK) .setType(MediaStore.Video.Media.CONTENT_TYPE ), "Select video"), video_REQUEST);

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    // intent.setType(android.provider.MediaStore.Video.Media.CONTENT_TYPE);
                    //   intent.setType(MediaStore.Video.Media.CONTENT_TYPE);
                    //     intent.setType("video/*");
                    startActivityForResult(intent, video_REQUEST);

                } else if (items[item].equals("취소")) {
                    dialog.dismiss();
                }
            }
        });

        builder.setTitle(getResources().getString(R.string.app_name));
        builder.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  예외처리
        if (resultCode != RESULT_OK) {
            return;
        }
        // 사진, 갤러리, Crop
        switch (requestCode) {

            //사진촬영
            case CAMERA_REQUEST: {
                cameraUrl = getImagePath(mCapturedImageURI);
                File imgFile = new File(cameraUrl);
                if (imgFile.exists()) {
                    cameraBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    uniBitmap = cameraBitmap;

                    try {
                        ExifInterface exif = new ExifInterface(getImagePath(mCapturedImageURI));


                        double alat = Math.abs(myLacationlatitude);
                        String dms = Location.convert(alat, Location.FORMAT_SECONDS);
                        String[] splits = dms.split(":");
                        String[] secnds = (splits[2]).split("\\.");
                        String seconds;
                        if (secnds.length == 0) {
                            seconds = splits[2];
                        } else {
                            seconds = secnds[0];
                        }

                        String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);

                        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, myLacationlatitude > 0 ? "N" : "S");


                        double alon = Math.abs(myLacationlongitude);


                        dms = Location.convert(alon, Location.FORMAT_SECONDS);
                        splits = dms.split(":");
                        secnds = (splits[2]).split("\\.");

                        if (secnds.length == 0) {
                            seconds = splits[2];
                        } else {
                            seconds = secnds[0];
                        }
                        String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";


                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
                        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, myLacationlongitude > 0 ? "E" : "W");

                        exif.saveAttributes();


                        Toast.makeText(MainActivity.this, "위치값이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                        //      ivImage.setImageBitmap(cameraBitmap);
                        iv.setImageBitmap(cameraBitmap);

                        videoUri = null;
                        // getImageDetail(getRealPathFromURI(mCapturedImageURI));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }getImageDetail(cameraUrl);
                }else {
                    Toast.makeText(this, "나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            // 갤러리가져오기
            case GALLERY_REQUEST: {
                galleryUri = data.getData();
                uniUri = galleryUri;
                try {
                    galleryBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), galleryUri);
                    uniBitmap = galleryBitmap;
                    //    ivImage.setImageBitmap(galleryBitmap);
                    iv.setImageBitmap(galleryBitmap);
                    getImageDetail(getRealPathFromURI(galleryUri));
                    videoUri = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            //동영상  촬영
            case video_Capture_REQUEST: {
                Log.d("시발", String.valueOf(mCapturedImageURI));
              /*  String vidioaUrl = getVideoPath(mCapturedImageURI);
                videoUri = mCapturedImageURI;

                getImageDetail(vidioaUrl);*/
                videoUri = data.getData();
                uniUri = videoUri;
                getImageDetail(getVideoRealPathFromURI(videoUri));
                break;


            }
                //동영상 가져오기
            case video_REQUEST: {
                videoUri = data.getData();
                uniUri = videoUri;
                getImageDetail(getVideoRealPathFromURI(videoUri));
                break;
            }
        }
    }

    /**
     * Method to get real path from Uri
     *
     * @param url url
     * @return real path
     */
    public String getVideoPath(Uri url) {
        try {

            Cursor cursor = getContentResolver().query(url, null, null, null, null);

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } catch (Exception e) {
            return url.getPath();
        }
    }

    /**
     * Method to get real path from content path
     *
     * @param contentURI content path
     * @return real path
     */
    protected String getVideoRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }



    public String getImagePath(Uri url) {
        try {

            Cursor cursor = getContentResolver().query(url, null, null, null, null);

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } catch (Exception e) {
            return url.getPath();
        }
    }

    /**
     * Method to get real path from content path
     *
     * @param contentURI content path
     * @return real path
     */
    protected String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    /**
     * Method that fetch info from your image file path.
     *
     * @param file file path of image
     */


    // file Final 로 인해서 상수로 만든다 else 부분에서 쓰기위하여
    protected void getImageDetail( String file) {
        Double latitude = null;
        Double longitude ;

        try {
            ExifInterface exifInterface = new ExifInterface(file);

            try {
                GeoDegree geoDegree = new GeoDegree(exifInterface);
                locationArray = geoDegree.toString().split(",");
                if (Double.valueOf(locationArray[0]) == 0.0) {
                    return;
                } else {
                    MARKER_POINT = MapPoint.mapPointWithGeoCoord(Double.valueOf(locationArray[0]), Double.valueOf(locationArray[1]));

                    geoCoder = new MapReverseGeoCoder(daumAPI_KEY, MARKER_POINT, MainActivity.this, MainActivity.this);
                    geoCoder.startFindingAddress();
                }

            } catch (NumberFormatException | NullPointerException e) {
                e.printStackTrace();
                try {


                    Cursor cursor = getContentResolver().query(uniUri, null, null, null, null);


                        try {
                            cursor.moveToFirst();


                            latitude = cursor.getDouble(
                                    cursor.getColumnIndex(
                                            MediaStore.Images.ImageColumns.LATITUDE)); // 위도
                            longitude = cursor.getDouble(
                                    cursor.getColumnIndex(
                                            MediaStore.Images.ImageColumns.LONGITUDE)); // 경도

                        if (latitude != 0.0) {
                            MARKER_POINT = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                            geoCoder = new MapReverseGeoCoder(daumAPI_KEY, MARKER_POINT, MainActivity.this, MainActivity.this);
                            geoCoder.startFindingAddress(); }
                        }
                        catch (Exception e1){


                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                            dialog.setTitle("사진에 위치정보가 없습니다.....");


                            // OK 버튼 이벤트
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(MainActivity.this, "공유받은 사진을 저장후 다시 한번 확인하거나 확인 버튼을 누른 후 위치를 수동으로 추가해주세요", Toast.LENGTH_SHORT).show();
                                    // 위치 추가할떈 이거 푸셈
                                    plusLocation = "GO";

                                }
                            });
                            // Cancel 버튼 이벤트
                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uniUri = null;
                                    dialog.cancel();
                                }
                            });
                            dialog.show();

                    }
                    Intent intent = getIntent();
                    if (Intent.ACTION_SEND.equals(action)) {
                        //Toast.makeText(this, "사진의 위치정보가 없습니다.", Toast.LENGTH_LONG).show();
                    }else if(latitude != 0.0) {
                    }else{

                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                            dialog.setTitle("사진에 위치정보가 없습니다...    추가하시겠습니까?");


                            // OK 버튼 이벤트
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(MainActivity.this, "사진의 장소를 확인해주세요", Toast.LENGTH_SHORT).show();

                                    plusLocation = "GO";

                                }
                            });
                            // Cancel 버튼 이벤트
                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    uniUri = null;
                                    dialog.cancel();
                                }
                            });
                            dialog.show();

                        }

                    //}
                    cursor.close();
                } catch (NullPointerException i) {
                    i.printStackTrace();
//                    phoneNum.setText(action);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


    }





    public void clickImage(View view) {
        if (uniUri == null) {

            return;
            // videodialog.show();

        }
        if (videoUri == null) {
            dialog.show();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = videoUri;
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);

        }

    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

        if (uniUri != null && MARKER_POINT != null) {

            mapView.setMapCenterPoint(MARKER_POINT, true);
            mapView.setZoomLevel(3, true);
            mapView.setCurrentLocationEventListener(this);
        } else {
            if (myLocatonPoint != null) {
                mapView.setMapCenterPoint(myLocatonPoint, true);
                mapView.setZoomLevel(3, true);
                mapView.setCurrentLocationEventListener(this);
                mapView.setDefaultCurrentLocationMarker();
                mapView.setShowCurrentLocationMarker(true);
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                mapView.setCurrentLocationEventListener(this);
                mapView.setZoomLevel(3, true);
                buttonClickCount = 2;
            } else {
                mapView.setDefaultCurrentLocationMarker();
                mapView.setShowCurrentLocationMarker(true);
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                mapView.setCurrentLocationEventListener(this);
                mapView.setZoomLevel(3, true);
                buttonClickCount = 2;
            }
        }
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        if (myLocatonPoint != null) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            buttonClickCount = 1;
            Drawable drawable = getDrawable(R.drawable.ic_my_location_false2);
            myLocationButton.setImageDrawable(drawable);
        }
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    // Touch 시 반응 하는부분
    public void onMapViewSingleTapped(final MapView mapView, MapPoint mapPoint) {
        if (plusLocation == "GO")
// 위치 선택
        {
            final Double checkLat = (mapPoint.getMapPointGeoCoord().latitude);
            final Double checkLong = (mapPoint.getMapPointGeoCoord().longitude);

            // 위치 출력
            Toast.makeText(MainActivity.this, Double.toString(checkLat) + " , " + Double.toString(checkLong), Toast.LENGTH_LONG).show();

            // POI 생성
            SELECT_POINT = MapPoint.mapPointWithGeoCoord(Double.valueOf(checkLat), Double.valueOf(checkLong));
            MARKER_POINT = SELECT_POINT;


            final MapPOIItem selectMaker = onMapCustomMarkerPlus(SELECT_POINT, "사진 위치");


            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("이 위치로 하시겠습니까?");


            // OK 버튼 이벤트
            dialog.setPositiveButton("여기닷!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                                           try {
                            ExifInterface exif = new ExifInterface(getRealPathFromURI(uniUri));


                            double alat = Math.abs(checkLat);
                            String dms = Location.convert(alat, Location.FORMAT_SECONDS);
                            String[] splits = dms.split(":");
                            String[] secnds = (splits[2]).split("\\.");
                            String seconds;
                            if (secnds.length == 0) {
                                seconds = splits[2];
                            } else {
                                seconds = secnds[0];
                            }

                            String latitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
                            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitudeStr);

                            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, checkLat > 0 ? "N" : "S");


                            double alon = Math.abs(checkLong);


                            dms = Location.convert(alon, Location.FORMAT_SECONDS);
                            splits = dms.split(":");
                            secnds = (splits[2]).split("\\.");

                            if (secnds.length == 0) {
                                seconds = splits[2];
                            } else {
                                seconds = secnds[0];
                            }
                            String longitudeStr = splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";


                            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitudeStr);
                            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, checkLong > 0 ? "E" : "W");

                            exif.saveAttributes();


                        Toast.makeText(MainActivity.this, "위치값이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        plusLocation = "Stop";

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            });
            // Cancel 버튼 이벤트
            dialog.setNegativeButton("여기아니에요!", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mapView.removePOIItem(selectMaker);
                    dialog.cancel();
                }
            });
            dialog.show();


        } else if (plusLocation == "Stop") return;


        // POI Event


    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }


    // 내 위치찾기 Onclick
    public void myLocation(View view) {

        if (buttonClickCount == 1) { //내위치표시하기
            Drawable drawable = getDrawable(R.drawable.ic_my_location_true12);
            myLocationButton.setImageDrawable(drawable);
            //내 위치표시
            mapView.setDefaultCurrentLocationMarker();
            mapView.setShowCurrentLocationMarker(true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            buttonClickCount++;
        } else if (buttonClickCount == 2) { //내위치표시와 헤딩표현
            Drawable drawable = getDrawable(R.drawable.ic_my_location_true22);
            myLocationButton.setImageDrawable(drawable);
            mapView.setShowCurrentLocationMarker(true);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            buttonClickCount++;
        } else {//내위치 끄기
            Drawable drawable = getDrawable(R.drawable.ic_my_location_false2);
            myLocationButton.setImageDrawable(drawable);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            buttonClickCount = 1;
        }
    }


    //내 위치찾기 리스너 등록
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        if (cntCurrentLocationUpdate == 0) {
            mapDegree = mapView.getMapRotationAngle();
            //     allowControlView.setMapDegree(mapDegree);
            myLocatonPoint = mapPoint;
            cntCurrentLocationUpdate++;
            myLocatonPoint = mapPoint;
            myLacationlatitude = myLocatonPoint.getMapPointGeoCoord().latitude;
            myLacationlongitude = myLocatonPoint.getMapPointGeoCoord().longitude;
            setMarkerName = "출발 점";
            balloonText = myLacationlatitude + "," + myLacationlongitude;
            onMapMyLocationCustomMarkerPlus();
            mapView.setMapCenterPoint(myLocatonPoint, true);
            mapView.setZoomLevel(3, true);
        } else {

        }


    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }

    //역 지오코딩 리스너등록
    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String geoCoderAddress) {
        photoAddress = geoCoderAddress;
        Toast.makeText(this, photoAddress, Toast.LENGTH_LONG).show();
        setMarkerName = "사진위치";
        balloonText = String.valueOf((float) MARKER_POINT.getMapPointGeoCoord().latitude) + " , " + String.valueOf((float) MARKER_POINT.getMapPointGeoCoord().longitude) + photoAddress;
        // balloonText = locationArray[0] + "," +locationArray[1] + " \n" + photoAddress;
        // onMapCustomMarkerPlus();
        onMapMarkerPlus();
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        Toast.makeText(this, "지오코딩에 실패하였습니다.", Toast.LENGTH_LONG).show();
    }

    //마커등록하기
    public void onMapMarkerPlus() {
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(setMarkerName);
        marker.setTag(0);
        marker.setMapPoint(MARKER_POINT);
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setShowDisclosureButtonOnCalloutBalloon(true);
        marker.setCustomPressedCalloutBalloonBitmap(uniBitmap);
        mapView.addPOIItem(marker);
        mapView.setMapCenterPoint(MARKER_POINT, true);
        mapView.setZoomLevel(3, true);
        poiTypeSet.setImageResource(R.drawable.pp);
        if (myLocatonPoint != null) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            buttonClickCount = 1;
            Drawable drawable = getDrawable(R.drawable.ic_my_location_false2);
            myLocationButton.setImageDrawable(drawable);
        }
    }

    //사진위치 마커등록하기
    public MapPOIItem onMapCustomMarkerPlus(MapPoint mappoint, String makerName) {
        MapPOIItem ptMarker = new MapPOIItem();
        ptMarker.setItemName(makerName);
        ptMarker.setTag(1);
        ptMarker.setMapPoint(mappoint);
        ptMarker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 기본으로 제공하는 BluePin 마커 모양.
        ptMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        ///customMarker.setCustomImageBitmap(galleryBitmap);
        ptMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        ptMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        mapView.addPOIItem(ptMarker);

        mapView.setMapCenterPoint(mappoint, true);
        mapView.setZoomLevel(3, true);

        return ptMarker;
    }

    //내위치 마커등록하기
    public void onMapMyLocationCustomMarkerPlus() {
        MapPOIItem customMarker = new MapPOIItem();
        customMarker.setItemName(setMarkerName);
        customMarker.setTag(1);
        customMarker.setMapPoint(myLocatonPoint);
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.marker_start); // 마커 이미지.
        customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
        mapView.addPOIItem(customMarker);

    }

    //길찾기버튼
    float pointOddDistace = 0;
    MapPoint a;
    MapPoint b;

    public void chaRoadFind(View view) {
        if (myLacationlatitude == null || locationArray == null) {
            Toast.makeText(this, "현제위치를 키거나 사진을 등록하세요", Toast.LENGTH_LONG).show();
        } else if (myLacationlatitude != null && locationArray[1] != null) {
            final CharSequence[] items = {"도보길경로", "대중교통길찾기", "자가용길찾기", "취소",};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(items, new DialogInterface.OnClickListener() {

                @SuppressLint("MissingPermission")
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("취소")) {
                        dialog.dismiss();
                    } else if (items[item].equals("도보길경로")) {

                        mapPoints = roadTracker.getJsonData(myLocatonPoint, MARKER_POINT);
                        //int totalDistance = roadTracker.totalDistance;
                        ArrayList<String> description = RoadTracker.getDescription();
                        mapPointPoints = roadTracker.getPointPoints();
                        mapPintsDescripton = new ArrayList<>();
                        mapPointsTurnType = new ArrayList<>();
                        mapPointsTurnType = RoadTracker.mapTurnType;
                        mapPintsDescripton = RoadTracker.pointDescription;

                        if (RoadTracker.totalDistance < 20000) {
                            ((ViewManager) relativeLayout.getParent()).removeView(relativeLayout);
                            window.addContentView(naviLayout, layoutParams);
                            oddDistance = (TextView) findViewById(R.id.oddDistance);
                            roadInfo = (TextView) findViewById(R.id.descriptionView);
                            ArrayAdapter<String> arrayAdapter;
                            arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, description);
                            ListView listView = (ListView) listDialog.findViewById(R.id.lsit);
                            TextView totalDisView = (TextView) listDialog.findViewById(R.id.totalDis);
                            totalDisView.setText(String.valueOf(RoadTracker.totalDistance) + "M");
                            TextView totalTimeView = (TextView) listDialog.findViewById(R.id.totalTime);
                            String s = RoadTracker.totaltime / 3600 + " : " + RoadTracker.totaltime % 3600 / 60;

                            Log.i("Check Data", s);

                            totalTimeView.setText(RoadTracker.totaltime / 3600 + " : " + RoadTracker.totaltime % 3600 / 60);
                            listView.setAdapter(arrayAdapter);
                            listDialog.show();

                            ImageView imageView = (ImageView) findViewById(R.id.navi_dialog_add);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listDialog.show();
                                }
                            });


                            MapPolyline polyline = new MapPolyline();
                            polyline.setTag(1000);
                            polyline.setLineColor(Color.argb(128, 255, 51, 0));

                            for (int i = 0; i < mapPoints.size(); i++) {
                                MapPoint point;
                                point = MapPoint.mapPointWithGeoCoord(mapPoints.get(i).getLatitude(), mapPoints.get(i).getLongitude());
                                polyline.addPoint(point);
                                mapView.addPolyline(polyline);
                            }
                            mapView.setMapCenterPoint(myLocatonPoint, true);
                            mapView.setZoomLevel(-1, true);
                            mapView.setMapViewEventListener(new MapView.MapViewEventListener() {
                                @Override
                                public void onMapViewInitialized(MapView mapView) {

                                }

                                @Override
                                public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

                                }

                                @Override
                                public void onMapViewZoomLevelChanged(MapView mapView, int i) {

                                }

                                @Override
                                public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

                                }

                                @Override
                                public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

                                }

                                @Override
                                public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

                                }

                                @Override
                                public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

                                }

                                @Override
                                public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

                                }

                                @Override
                                public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

                                }
                            });
                            mapView.setShowCurrentLocationMarker(true);
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                            roadInfo.setText(mapPintsDescripton.get(0));
                            try {
                                // 현재시간을 msec 으로 구한다.
                                long now = System.currentTimeMillis();
                                // 현재시간을 date 변수에 저장한다.
                                // Date date = new Date(now);
                                // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                                //SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
                                // nowDate 변수에 값을 저장한다.
                                //String formatDate = sdfNow.format(date);
//    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                TextView oddTime = (TextView) findViewById(R.id.oddTime);
                                oddTime.setText((int) now);
                            } catch (Exception e) {
                                e.getMessage();
                                TextView oddTime = (TextView) findViewById(R.id.oddTime);
                                oddTime.setText("테블릿에선 출력이 안됩니다.");

                            }
                            oddDistance.setText(0.0 + "M");


                            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            LocationListener locationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    locationLat = location.getLatitude();
                                    locationLon = location.getLongitude();
                                    a = MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude());
                                    if (b != null) {
                                        MapPolyline polyline = new MapPolyline();
                                        polyline.setTag(1000);
                                        polyline.setLineColor(Color.argb(128, 0, 255, 0));
                                        polyline.addPoint(b);
                                        polyline.addPoint(a);
                                        mapView.addPolyline(polyline);
                                        pointOddDistace += getDistance(a, b);
                                        oddDistance.setText(pointOddDistace + "M");
                                    }
                                    b = a;

                                    for (int i = 0; i < mapPointPoints.size(); i++) {
                                        MapPoint point;
                                        point = MapPoint.mapPointWithGeoCoord(mapPointPoints.get(i).getLatitude(), mapPointPoints.get(i).getLongitude());
                                        float pointDistace = getDistance(a, point);
                                        // oddDistance.setText(pointDistace + "M");
                                        if (pointDistace < 15.0) {
                                            roadInfo.setText(mapPintsDescripton.get(i));
                                        }
                                    }
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

                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


                            ImageButton imageButton = (ImageButton) findViewById(R.id.navicancle);
                            imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((ViewManager) naviLayout.getParent()).removeView(naviLayout);
                                    window.addContentView(relativeLayout, layoutParams);

                                    // mapView.removeAllPOIItems();
                                    mapView.setMapViewEventListener(MainActivity.this);
                                    mapView.removeAllPolylines();
                                    mapView.setMapCenterPoint(myLocatonPoint, true);
                                    mapView.setZoomLevel(3, true);
                                    mapView.setShowCurrentLocationMarker(true);
                                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                                    mapView.setCurrentLocationEventListener(MainActivity.this);
                                }
                            });
                        } else
                            Toast.makeText(MainActivity.this, "20km초과", Toast.LENGTH_SHORT).show();
                    } else if (items[item].equals("대중교통길찾기")) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?" + "sp=" + myLacationlatitude + "," + myLacationlongitude + "&"
                                    + "ep=" + Double.valueOf(locationArray[0]) + "," + Double.valueOf(locationArray[1]) + "&by=PUBLICTRANSIT"));
                            startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"));
                            startActivity(intent);
                        }
                    } else if (items[item].equals("자가용길찾기")) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://route?" + "sp=" + myLacationlatitude + "," + myLacationlongitude + "&"
                                    + "ep=" + Double.valueOf(locationArray[0]) + "," + Double.valueOf(locationArray[1]) + "&by=CAR"));
                            startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"));
                            startActivity(intent);
                        }
                    }
                }
            });

            builder.setTitle("길안내를 선택하세요.");
            builder.show();
        }
    }

    //지도 타입 설정
    public void mapTypeSetting(View view) {
        if (on == true) {
            mapView.setMapType(MapView.MapType.Hybrid);
            mapTypeSet.setImageResource(R.drawable.satellitemap2);
            on = false;
        } else {
            mapView.setMapType(MapView.MapType.Standard);
            mapTypeSet.setImageResource(R.drawable.roadmap2);
            on = true;
        }

    }

    //poi 타입 설정
    public void poiTypeSetting(View view) {

        final CharSequence[] items = {"내 위치보기", "사진 위치보기", "같이 보기", "취소"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("내 위치보기")) {
                    try {
                        mapView.setMapCenterPoint(myLocatonPoint, true);
                        mapView.setZoomLevel(3, true);
                        poiTypeSet.setImageResource(R.drawable.sp);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "내 위치버튼을 클릭해주세요", Toast.LENGTH_LONG).show();
                    }
                } else if (items[item].equals("사진 위치보기")) {
                    try {
                        mapView.setMapCenterPoint(MARKER_POINT, true);
                        mapView.setZoomLevel(3, true);
                        poiTypeSet.setImageResource(R.drawable.pp);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "사진위치를 등록해주세요", Toast.LENGTH_LONG).show();
                    }
                } else if (items[item].equals("같이 보기")) {
                    try {
                        MapPoint[] togetherPoint = {MARKER_POINT, myLocatonPoint};
                        mapView.fitMapViewAreaToShowMapPoints(togetherPoint);
                        poiTypeSet.setImageResource(R.drawable.top);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "사진위치 혹은 내 위치버튼을 클릭해주세요", Toast.LENGTH_LONG).show();
                    }
                } else if (items[item].equals("취소")) {
                    dialog.dismiss();
                }
            }
        });

        builder.setTitle("마커 보기");
        builder.show();

    }

    private void captureView() {
/*
buildDrawingCache()로 현재 뷰를 캡쳐후
getDrawingCache로 bitmap을 가져온다.
setDrawingCacheEnabled(boolean) 메소드로 true 설정을 해놓으면,
getDrawingCache로 현재뷰를 바로 가져올 수 있다고 한다.
허나 계속적으로 뷰 화면을 가져온다면, 성능상 문제가 있는 부분도 있지 않을까 싶은데,
확인은 해보지 않았다.
*/

        container2.setDrawingCacheEnabled(true);
        container2.buildDrawingCache();
        Bitmap captureView = container2.getDrawingCache();
        saveBitmap(captureView, "test" + (num++));
        //ivImage.setImageBitmap(captureView);
    }

    private void setText(String msg) {
        // TextView textView = (TextView)findViewById(R.id.tv_location);
        //    textView.setText(msg);
    }

    private void saveBitmap(Bitmap bitmap, String fileName) {
/*
Bitmap 저장 부분이다.
저장소/data/test 폴더를 만들어서 그 안에 이미지를 저장한다.
*/
        boolean result = true;
        String savePath = Environment.getExternalStorageDirectory() + "/data/test";
        File saveFile = new File(savePath);

        if (!saveFile.exists())
            result = saveFile.mkdir();

        if (!result) {
            setText("Fail! Create Directory ... path:" + savePath);
        }


        if (!result) return;
        savePath += "/" + fileName + ".jpg";

/*
JPEG 파일로 저장하는 과정이며 CompressFormat 2번째 인자는 퀄리티 인데,
정확히 어떤식으로 값을 정하는지는 모르겠다. 그냥 이미지 저장부분
*/
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(savePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Toast.makeText(this, "캡쳐버튼이 실행되었습니다", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException nfe) {
            Log.d("FileNotFoundException:", nfe.getMessage());
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ioe) {
                Log.d("IOException:", ioe.getMessage());
            }
        }
    }

    public void sendMMS(View view) {

               if(uniUri == null)
               {
                   Toast.makeText(MainActivity.this, "공유할 사진이 없습니다!", Toast.LENGTH_SHORT).show();
                   return;
               }

                smsText = balloonText;

                Intent sendIntent = new Intent(Intent.ACTION_SEND, uniUri);
                sendIntent.putExtra("sms_body", smsText); // 보낼 문자
                //sendIntent.putExtra("address", phoneNumber); // 받는사람 번호
                sendIntent.putExtra(Intent.EXTRA_STREAM, uniUri);
                sendIntent.setType("vnd.android-dir/mms-sms");
                if (uniUri.toString().contains("images")) {
                    sendIntent.setType("image/*");
                } else if (uniUri.toString().contains("video")) {
                    sendIntent.setType("video/*");
                }
               // startActivity(Intent.createChooser(sendIntent, "선택해주세요"));
                startActivity(sendIntent);
    }

    public void textClick(View view) {
        mEditTextQuery.setText("");
    }

    public void sendSMS(String smsNumber, String smsText) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", smsText); // 보낼 문자
        sendIntent.putExtra("address", smsNumber); // 받는사람 번호
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
         /*   Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM,uniUri);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent,"선택하세요!"));*/
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextQuery.getWindowToken(), 0);
    }

    private void showResult(List<Item> itemList) {
        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);

            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);
            mapPointBounds.add(mapPoint);
            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);
            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
        }

        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.selectPOIItem(poiItems[0], false);
        }
    }

    private Drawable createDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object fetch(String address) throws MalformedURLException, IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }

    public Float getDistance(final MapPoint startPoint, final MapPoint endPoint) {
        float[] results = new float[3];
        Location location = new Location("start");
        location.distanceBetween(
                startPoint.getMapPointGeoCoord().latitude,
                startPoint.getMapPointGeoCoord().longitude,
                endPoint.getMapPointGeoCoord().latitude,
                endPoint.getMapPointGeoCoord().longitude,
                results);
        Float mes = results[0];
        return mes;
    }




}



