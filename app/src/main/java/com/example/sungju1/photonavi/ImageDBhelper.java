package com.example.sungju1.photonavi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by clear on 2017-11-04.
 */
//Counting. 가장 많이 사용한 Image 3개 상단, 나머지 이용한 것 밑에 쌓이게.
public class ImageDBhelper {
    private static final String TAG = "ImageDBhelper";
    private static final String KEY_ROWID = "_id";
    private static final String KEY_URI = "uri";
    private static final String KEY_CLICK = "click";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private ArrayList<ImageAdapter>imageAdapter;
    private static final String DATABASE_NAME = "imagedb";
    private static final String DATABASE_TABLE = "image";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "CREATE TABLE "+ DATABASE_TABLE +" ("
                    +KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +KEY_URI+" TEXT "
                    +KEY_CLICK+" INTEGER"
                    + ");";
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);

        }
    }
    private final Context mCtx;

    public ImageDBhelper(Context context){
        this.mCtx =context;
    }

    public ImageDBhelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    //사진 클릭해서 길 찾기 시작할 때 값 저장
    public long saveUri(String uri){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_URI, uri);
        //contentValues.put();
        return mDb.insert(DATABASE_TABLE, null, contentValues);
    }
    //배열리스트에 값 내림차순으로 가져오기
    public ArrayList<ImageAdapter> fetchListOrdeBYDec(){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        imageAdapter = new ArrayList<>();
        String raw = "select * from " + DATABASE_TABLE + " order by " + KEY_ROWID + " desc";
        Cursor res = mDb.rawQuery(raw,null); //id를 기준으로 내림차순
        res.moveToFirst();
        while (!res.isAfterLast()) {
            String uri = res.getString(res.getColumnIndex(KEY_URI));
            Log.w(TAG, uri);
            imageAdapter.add(new ImageAdapter(uri , 0));
            res.moveToNext();
        }
        if (res.getCount()==0){
            return null;
        }
        res.close();
        return imageAdapter;
    }

    public ArrayList<ImageAdapter> fetchListOrderBYClcik(){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        imageAdapter = new ArrayList<>();
        String raw = "select * from "+DATABASE_TABLE + "order by" + KEY_CLICK + "desc";
        Cursor res = mDb.rawQuery(raw, null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            int click = res.getInt(res.getColumnIndex(KEY_CLICK));
            String uri = res.getString(res.getColumnIndex(KEY_URI));
            imageAdapter.add(new ImageAdapter(uri, click));
            res.moveToNext();
        }
        if(res.getCount()==0){
            return null;
        }
        return imageAdapter;
    }

    //_id로 내림차순 정렬하여 모두 가져오기 3, 2, 1...
    public Cursor fetchAllListOrderBYDec() {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        Cursor res = mDb.rawQuery("select * from " + DATABASE_TABLE + " order by " + KEY_ROWID + " desc", null);
        return res;
    }
    String clickImageUri;
    //클릭한 이미지 Uri 가져오기
    public String fetchClickUri(int index){
        Log.d("클릭uri","통과");
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        Log.d("클릭uri","통과1");
        Cursor res = mDb.rawQuery("select * from " + DATABASE_TABLE + " order by " + KEY_ROWID + " desc" , null );
        Log.d("클릭uri","통과2");
        res.moveToFirst();
        for (int i = 0; i <= index ; i++){
            Log.d("클릭urifor문","통과");
            clickImageUri  = res.getString(res.getColumnIndex(KEY_URI));
          Log.d("클릭한이미지uri",clickImageUri);

        res.moveToNext();
        }
        res.close();

        return clickImageUri;
    }


    //업데이트 클릭 수
    public Cursor updateList(String uri){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        String sql = "update "+ DATABASE_TABLE +" set "+KEY_CLICK +"="+ KEY_CLICK+1 +" where "+KEY_URI+"="+uri;
        Cursor res = mDb.rawQuery(sql,null);
        return res;
    }

    public void update(String id){
        try {
            mDbHelper = new DatabaseHelper(mCtx);
            mDb = mDbHelper.getWritableDatabase();
            String sql = "update " + DATABASE_TABLE + " set " + KEY_CLICK + "=" + KEY_CLICK + 1 + " where " + KEY_ROWID + "=" + id;
            mDb.execSQL(sql);
        }catch(SQLException e){
            Toast.makeText(mCtx, e.getMessage(), Toast.LENGTH_SHORT).show();
        }finally {
            mDb.close();
        }
    }

    public int removeList(int id){
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        return mDb.delete(DATABASE_TABLE, "_id = ?",new String[]{String.valueOf(id)});
    }
}
