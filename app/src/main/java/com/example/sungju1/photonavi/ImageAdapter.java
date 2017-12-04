package com.example.sungju1.photonavi;

import android.net.Uri;

/**
 * Created by clear on 2017-11-04.
 */

public class ImageAdapter {
    private String uri;

    public ImageAdapter(String uri){
        this.uri = uri;
    }
    public String getUri(){
        return uri;
    }
}
