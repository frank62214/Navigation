package com.example.navigation.My;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.example.navigation.R;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.util.ArrayList;

public class My_CarMode implements Runnable{
    private CameraPosition cameraPosition;

    private String url = "";
    private My_Direction my_direction;

    private My_Layout my_layout;
    private My_Map my_map;
    private LatLng cal_position;
    private LatLng last_position;

    private boolean initData = true;

    private LatLng now_API_position;
    private LatLng last_API_position;

    private double Now_Bearing;
    private double Last_Bearing;

    private LatLng Navigation_MK_Start = new LatLng(0,0);
    private LatLng Navigation_MK_End   = new LatLng(0,0);

    private final MarkerOptions Navigation_MK_Opt = new MarkerOptions();
    private final MarkerOptions Navigation_test = new MarkerOptions();
    private Marker Navigation_MK;
    private Context context;
    private Polyline Direction;

    private int distance;

    public My_CarMode(My_Layout layout, My_Map map){
        my_layout = layout;
        my_map = map;
        my_direction = new My_Direction();
        Data.Destination = Data.now_position;
    }
    @Override
    public void run() {
        my_map.initUserMK();
        Data.CarMode_Status = true;
        while(true) {
            if(Data.CarMode_Status) {
                get_Cal_Position();
                get_Main_loop();
                SystemClock.sleep(1000);
            }
            else{
                set_Direction_Camera();
                return;
            }
        }
    }
    private void get_Main_loop(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                //my_map.initUserMK();
                //System.out.println(Data.now_position);
                if(last_position==null){last_position = cal_position;}
                my_map.initCarCamera(cal_position,count_bearing(cal_position, last_position));
                last_position = cal_position;
            }
        });
    }
    private void get_Cal_Position(){
//        My_Snap_Road my_snap_road = new My_Snap_Road();
//        my_snap_road.setSnapRoadUrl();
//        my_snap_road.SearchLocation(new My_Snap_Road.onDataReadyCallback() {
//            @Override
//            public void onDataReady(LatLng data) {
//                cal_position = data;
//            }
//        });
        try {
            my_direction.searchDirection();
            my_direction.SearchNavigationData(new My_Direction.onDataReadyCallback() {
                @Override
                public void onDataReady(ArrayList<LatLng> data) {
                    now_API_position = data.get(0);
                    Data.API_Record.add(data.get(0));
                    //Data.GPS_Record.add(Data.now_position);
                    if (initData) {
                        //第一次執行
                        initData = false;
                        Last_Bearing = Cal_Method.Cal_Bearing(data.get(0), data.get(1));
                        //Last_Bearing = Cal_Bearing(Data.now_position, data.get(0));
                    } else {
                        Now_Bearing = Cal_Method.Cal_Bearing(data.get(0), data.get(1));
                        //Now_Bearing = Cal_Bearing(Data.now_position, data.get(0));
                    }
                    Navigation_MK_Start = data.get(0);
                    Navigation_MK_End = data.get(1);
                    if (!Cal_Method.Cal_Check_Drift()) {
                        Last_Bearing = Now_Bearing;
                        distance = (int) Cal_Method.Cal_Distance(Navigation_MK_Start, Navigation_MK_End);
                        //NavigationCamera();
                    }
                }

                @Override
                public void onDisReady(int dis) {
                    distance = dis;
                }

                @Override
                public void onStartLocationReady(LatLng start, LatLng end) {
                }
            });
        }
        catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(e.toString()).setTitle("Error").setIcon(R.drawable.warning);
            builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }
    private void set_Direction_Camera(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.moveCamera(Data.now_position, 15, 0, 0);
                my_map.setMyLocationEnabled(true);
            }
        });
    }
    private float count_bearing(LatLng Start, LatLng End){
        double degress = Math.PI/ 180.0;
        double phi1 = Start.latitude * degress;
        double phi2 = End.latitude * degress;
        double lam1 = Start.longitude * degress;
        double lam2 = End.longitude * degress;

        double y = Math.sin(lam2 - lam1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1);
        float bearing = (float)(((Math.atan2(y, x) * 180) / Math.PI) + 360) % 360;
        //System.out.println(bearing);
        if (bearing < 0) {
            bearing = bearing + 360;
        }
        return bearing;
    }
}
