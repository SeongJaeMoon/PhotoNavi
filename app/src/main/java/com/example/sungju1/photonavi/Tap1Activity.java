package com.example.sungju1.photonavi;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SungJu1 on 2017-12-03.
 */

public class Tap1Activity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;
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
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap1layout);
        backPressCloseHandler = new BackPressCloseHandler(this);


        mainActivity = new MainActivity();
        ImageDBhelper imageDBhelper = new ImageDBhelper(this); //ImageADBhelper 객체 생성
        imageDBhelper.open(); //DB 오픈
        imageAdapters = imageDBhelper.fetchListOrdeBYDec(); //리스트에 내림차순으로 받아오기
        imageDBhelper.close(); //DB 닫기 (안 해주면 메모리 릭)
        mPathList = new ArrayList<>(); // 실제 리턴할 값
        if (imageAdapters!=null&&imageAdapters.size()>0) {
            for(int i = 0; i< imageAdapters.size(); i++) {
                ImageAdapter obj = imageAdapters.get(i); // imageAdapter 형으로 리스트의 값 할당 -> 그리드 뷰에 뿌릴 개수 크기에 따라 설정 가능 단, 예외처리 안 해주면 Array Exception 발생
                try {
                    File imgFile = new File(obj.getUri());
                    basePath = imgFile.getPath();
                    if (basePath != null && basePath.length() > 0) {
                        mPathList.add(basePath); //리스트형 값 리턴
                        Log.w(TAG, basePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mGridView = (GridView) findViewById(R.id.grid); // 그리드 뷰 연결
            GirdViewAdapter adapter = new GirdViewAdapter(com.example.sungju1.photonavi.Tap1Activity.this, mPathList,R.layout.grid_single); // 그리드 뷰 어뎁터 객체 생성자(context, 뿌릴 uri리스트, 그리드 싱글)
            mGridView.setAdapter(adapter);//그리드 뷰 어뎁터와 연결!
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show(); //그리드 뷰 아이템 클릭 리스너, position 0부터 시작
                }
            });
            imageDBhelper.close();
        }

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
              //  galleryUri = data.getData();
                //MainActivity.uniUri = galleryUri;
               // mainActivity.getImageDetail(getRealPathFromURI(MainActivity.uniUri));
                MainActivity.pass = true;
                HomeActivity.tabHost.setCurrentTab(1);
                break;
            }
            case GALLERY_REQUEST: {
                galleryUri = data.getData();
                MainActivity.uniUri = galleryUri;
               // String galPath = getRealPathFromURI(galleryUri);
               // mainActivity.startMapPoint(galPath);
                MainActivity.pass = true;
                HomeActivity.tabHost.setCurrentTab(1);
                break;
            }
            case video_Capture_REQUEST: {
                videoUri = data.getData();
                MainActivity.uniUri = videoUri;
                MainActivity.pass = true;
                HomeActivity.tabHost.setCurrentTab(1);
                break;
            }
            //동영상 가져오기
            case video_REQUEST: {
                videoUri = data.getData();
                MainActivity.uniUri = videoUri;
                MainActivity.pass = true;
                HomeActivity.tabHost.setCurrentTab(1);
                break;
            }

        }
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
    protected String getRealPathFromURI(Uri contentURI) {
        String result;
        Log.d("리얼패스Uri",contentURI.getPath());
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
            Log.d("리얼패스Uri if", result);
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            Log.d("리얼패스Uri else", result);
            cursor.close();
        }

        return result;

    }
}
