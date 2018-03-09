package com.example.princesaoud.egu;

import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by princesaoud on 19/2/18.
 */

public class MyListAdapter extends ArrayAdapter<String> {
    private final Activity mContext;
//    private final String[] maintitle;
//    private String[] subtitle;
    private final Integer[] imgId;
    private JSONObject subtitle;
//    private List<String> key = (List<String>) subtitle.keys();
    private String[] key;
    public MyListAdapter(Activity mContext, String[] key, JSONObject subtitle, Integer[] imgId) {
        super(mContext, R.layout.mylist, key);
        this.mContext = mContext;
//        this.maintitle = maintitle;
        this.subtitle = subtitle;
        this.imgId = imgId;
    }

    public void updateView(JSONObject subtitle){
        this.subtitle = null;
        this.subtitle = subtitle;
        notifyDataSetChanged();
    }


    private void tempo(String string, TextView titleText, TextView subText){
        titleText.setText(string);
        try {
            subText.setText(subtitle.getString(string.toLowerCase()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public View getView (int position, View view, ViewGroup parent){
        LayoutInflater layoutInflater = mContext.getLayoutInflater();
        View rowView = null;
        try {
            rowView = layoutInflater.inflate(R.layout.mylist, null,true);
        }catch (Exception e){
            Log.e("rowView", "rowView");
        }


        TextView titleText = (TextView) rowView.findViewById(R.id.tv_title);
        TextView subText = (TextView) rowView.findViewById(R.id.tv_subtitle);
        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
        switch (position){
            case 0:
                tempo("Fadjr",titleText,subText);
                break;
            case 1:
                tempo("Dhuhr",titleText,subText);
                break;
            case 2:
                tempo("Asr",titleText,subText);
                break;
            case 3:
                tempo("Maghrib",titleText,subText);
                break;
            case 4:
                tempo("Ishaa",titleText,subText);
         }
//        titleText.setText(maintitle[position]);
//        subText.setText(subtitle[position]);
        icon.setImageResource(imgId[position]);

        return rowView;
    }

}
