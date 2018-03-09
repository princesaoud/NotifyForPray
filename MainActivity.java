package com.example.princesaoud.egu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {


    ListView listView;

    String[] mantitle ={"Fadjr", "Dhouhr", "Asr", "Maghrib", "Ishaa"};

    String[] subtitle ={""};

    final Integer[] imgId = {
            R.drawable.salat,
            R.drawable.salat2,
            R.drawable.salat3,
            R.drawable.salat4,
            R.drawable.salat5
    };
    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            myReq();
            try {
                String fadjr = jsonObject.getString("fadjr");
                String dhuhr = jsonObject.getString("dhuhr");
                String asr = jsonObject.getString("asr");
                String maghrib = jsonObject.getString("maghrib");
                String ishaa = jsonObject.getString("ishaa");

                if(currentDate().equals(fadjr))
                    SendNotify("Fadjr Time is Up", "Get ready fadjr time is up");

                if(currentDate().equals(dhuhr))
                    SendNotify("Dhuhr time is up", "get ready dhuhr time is up");

                if(currentDate().equals(asr))
                    SendNotify("Asr time is up", "get ready Asr time is up");

                if(currentDate().equals(maghrib))
                    SendNotify("maghrib time is up", "get ready maghrib time is up");

                if(currentDate().equals(ishaa))
                    SendNotify("ishaa time is up", "get ready ishaa time is up");



            } catch (JSONException e) {
                e.printStackTrace();
            }

            handler.postDelayed(r,1000);
        }
    };

    final String url = "http://namaztimes.000webhostapp.com/times.php";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    JSONObject jsonObject;
    FileOutputStream foust;
    MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ReadingData() != null) {
            jsonObject = ReadingData();
        }else{
            try {
                jsonObject = new JSONObject();
                jsonObject.put("fadjr","1");
                jsonObject.put("dhuhr","1");
                jsonObject.put("asr","1");
                jsonObject.put("maghrib","1");
                jsonObject.put("ishaa","1");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        myListAdapter = new MyListAdapter(this, mantitle, jsonObject,imgId);
        listView =(ListView) findViewById(R.id.listView);
        listView.setAdapter(myListAdapter);
        handler.postDelayed(r,1000);

    }

    @Override
    protected void onStop() {
        super.onStop();

        myListAdapter = new MyListAdapter(this, mantitle, jsonObject,imgId);
        listView =(ListView) findViewById(R.id.listView);
        listView.setAdapter(myListAdapter);
        handler.postDelayed(r,1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.postDelayed(r,1000);

    }

    public void SendNotify(String title, String text){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.mosque);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setAutoCancel(true);
        int notifId = 195;

        Intent notifIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(notifId,builder.build());

    }

    protected String currentDate(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("h:mm a");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        String localTime = date.format(currentLocalTime);

        return localTime;

    }

    public JSONObject ReadingData(){
        FileInputStream fist = null;
        String temp = "";
        JSONObject jo = null;

        try {
            fist = openFileInput("data");
            int c;
//        Reading each char onto the file

            while((c = fist.read()) != -1){
                temp+= Character.toString((char)c);
            }

        } catch (IOException e) {
            e.printStackTrace();
         }
        try {
            jo = new JSONObject(temp);
        } catch (JSONException e) {
            Log.e("Json Parsin", "My string could not parse the json object");
        }
        return jo;
    }

    public void SavingData(JSONObject toWrite){

        //        Hm !!
//        I am trying to retrieve data from the data file which is
//        inside the internal storage of the mobile

        try {
//            openFileOutput contains the file in Output mode

            foust = openFileOutput("data", MODE_PRIVATE);
            foust.write(toWrite.toString().getBytes());
            } catch (FileNotFoundException e) {
                Log.e("file not found",e.getMessage());
                e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void myReq(){
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Toast.makeText(getApplicationContext(),jsonObject.toString(),Toast.LENGTH_LONG).show();
//                            Log.e("message", jsonObject.toString());
//                            if (foust==null) {
//                                foust = openFileOutput("data", MODE_PRIVATE);
//                            }
                            JSONObject oldData = new JSONObject();
                            if(ReadingData() != null) {
                                oldData = ReadingData();
                            }else{
                                oldData = new JSONObject();
                                oldData.put("fadjr","1");
                                oldData.put("dhuhr","1");
                                oldData.put("asr","1");
                                oldData.put("maghrib","1");
                                oldData.put("ishaa","1");

                            }
                            JSONObject newData = response;
                            jsonObject = response;

                            if(!oldData.getString("fadjr").equals(newData.getString("fadjr"))){
                                SendNotify("Fadjr", "fadjr has been changed to "+newData.getString("fadjr"));
                                SavingData(newData);
                                myListAdapter.updateView(newData);
                            }

                            if(!oldData.getString("dhuhr").equals(newData.getString("dhuhr"))){
                                SendNotify("Dhuhr", "Dhuhr has been changed to "+newData.getString("dhuhr") );
                                SavingData(response);
                                myListAdapter.updateView(newData);
                            }

                            if(!oldData.get("asr").equals(newData.getString("asr"))){
                                SendNotify("Asr", "Asr has been changed to "+newData.getString("asr") );
                                SavingData(response);
                                myListAdapter.updateView(newData);
                            }

                            if(!oldData.get("magrhib").equals(newData.getString("magrhib"))){
                                SendNotify("Maghrib", "Maghrib has been changed to "+newData.getString("maghrib") );
                                SavingData(response);
                                myListAdapter.updateView(newData);
                                }

                            if(!oldData.get("ishaa").equals(newData.getString("ishaa"))){
                                SendNotify("Ishaa", "Ishaa has been changed to "+newData.getString("ishaa") );
                                SavingData(response);
                                myListAdapter.updateView(newData);
                            }


                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        Log.e("error", error.toString());
                    }
                }
        );
        queue.add(getRequest);

    }
}
