package com.example.sungju1.photonavi;

import android.net.Uri;

/**
 * Created by clear on 2017-11-04.
 */

public class ImageAdapter {
    private String uri;
    private int click;
    public ImageAdapter(String uri, int click){
        this.uri = uri;
        this.click = click;
    }
    public String getUri(){
        return uri;
    }
    public int getClick(){return click;}
}
