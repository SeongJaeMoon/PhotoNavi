package com.example.sungju1.photonavi;

/**
 * Created by nsu on 2017-04-26.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class AllowControlView extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {
private double angle = 0.0;
private AllowListener listener;
        float x, y;
        float mx, my;
private float mapAngle;


public double setMapDegree(float mapDegree){
        mapAngle = mapDegree;
        invalidate();
        return mapAngle;
        }

public interface AllowListener{
    public void onChanged(double angle);

}
    public void setAllowListener(AllowListener lis){
        listener = lis;
    }
    public AllowControlView(Context context) {
        super(context);
        this.setImageResource(R.drawable.allow12);
        this.setOnTouchListener(this);
    }
    public AllowControlView(Context context, AttributeSet attrs) {
        super(context,attrs);
        this.setImageResource(R.drawable.allow12);
        this.setOnTouchListener(this);
    }
    public  double getAngle(float x , float y){
        mx = x - (getWidth()/2.0f);
        my = (getHeight()/2.0f) - y;

        double degree = Math.atan2(mx,my) * 180.0/3.141592;
        return degree;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = event.getX(0);
        y = event.getY(0);
        angle = getAngle(x,y);
        invalidate();
        listener.onChanged(angle);
        return true;
    }

    protected void onDraw(Canvas c) {
        Paint paint = new Paint();
        c.save();
        c.rotate(mapAngle,getWidth() / 2, getHeight() / 2);
        c.rotate((float) angle, getWidth() / 2, getHeight() / 2);
        super.onDraw(c);
        c.restore();
    }
}
