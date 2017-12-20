package com.example.sungju1.photonavi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by SungJu1 on 2017-06-18.
 */

public class fristDio extends View {
    private Movie mMovie;
    private long mMovieStart;
    public fristDio(Context context) {
        super(context);
        setFocusable(true);
                java.io.InputStream is;
              //  is = context.getResources().openRawResource(R.drawable.locationtag);
            //    mMovie = Movie.decodeStream(is);
            }
            @Override protected void onDraw(Canvas canvas) {
                canvas.drawColor(0xFFCCCCCC);

                Paint p = new Paint();
                p.setAntiAlias(true);

                long now = android.os.SystemClock.uptimeMillis();
                if (mMovieStart == 0) { // first time
                    mMovieStart = now;
                }
                if (mMovie != null) {
                    int dur = mMovie.duration();
                    if (dur == 0) {
                        dur = 1000;
                    }
                    int relTime = (int)((now - mMovieStart) % dur);
                    mMovie.setTime(relTime);
//mMovie.draw(canvas, getWidth() - mMovie.width(),getHeight() - mMovie.height());
                    mMovie.draw(canvas, 0,0);
                    invalidate();
                }
           }
}
