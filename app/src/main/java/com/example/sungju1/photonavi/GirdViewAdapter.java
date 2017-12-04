package com.example.sungju1.photonavi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clear on 2017-11-04.
 */

public class GirdViewAdapter extends ArrayAdapter {
    private Context context;
    private int resourceId;
    private LayoutInflater inflater;
    private List<String>data = new ArrayList<String>();

    static class ViewHolder {
        ImageView image;
    }
    public GirdViewAdapter(Context context, List<String>data, int resourceId){ //그리드 뷰 어뎁터 생성자
        super(context, resourceId, data); //슈퍼클래스 상속
        this.resourceId = resourceId;
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            row = inflater.inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.grid_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag(); //리싸이클러 뷰
        }
        Glide.with(context)
                .load(data.get(position))
                .into(holder.image);

        return row;
    }
}
