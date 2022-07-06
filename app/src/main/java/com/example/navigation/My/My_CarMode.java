package com.example.navigation.My;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.UrlTileProvider;

public class My_CarMode implements Runnable{


    private String url = "";


    private My_Layout my_layout;
    private My_Map my_map;
    private LatLng cal_position;

    public My_CarMode(My_Layout layout, My_Map map){
        my_layout = layout;
        my_map = map;
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
                my_map.initCarCamera(cal_position);
            }
        });
    }
    private void get_Cal_Position(){
        My_Snap_Road my_snap_road = new My_Snap_Road();
        my_snap_road.setSnapRoadUrl();
        my_snap_road.SearchLocation(new My_Snap_Road.onDataReadyCallback() {
            @Override
            public void onDataReady(LatLng data) {
                cal_position = data;
            }
        });

    }
    private void set_Direction_Camera(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.moveCamera(Data.now_position, 15, 0, 0);
                my_map.setMyLocationEnabled(true);
            }
        });
    }
}
