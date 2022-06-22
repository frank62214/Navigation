package com.example.navigation.My;

import android.os.SystemClock;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class My_API_Navigation implements Runnable{
    private My_Layout my_layout;
    private My_Map my_map;



    public My_API_Navigation(My_Layout layout, My_Map map){
        my_layout = layout;
        my_map = map;
    }

    @Override
    public void run() {
        my_map.initUserMK();
        while (Data.Steps.size()>0) {
            draw_Direction();
            SystemClock.sleep(1000);
        }
    }
    public void draw_Direction(){
        My_Direction my_direction = new My_Direction();
        my_direction.searchDirection();
        my_direction.SearchNavigationData(new My_Direction.onDataReadyCallback() {
            @Override
            public void onDataReady(ArrayList<LatLng> data) {
                //my_layout.Direction_Page();
                my_map.Draw_Direction(data);
            }
            @Override
            public void onStartLocationReady(LatLng start, LatLng end) {
                //get bearing
                float bearing = my_map.Cal_Bearing(start, end);
                my_map.set_Navigation_Camera(start, bearing);
            }
            @Override
            public void onDisReady(int dis) {}
        });
    }
}
