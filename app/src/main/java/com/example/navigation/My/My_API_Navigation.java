package com.example.navigation.My;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class My_API_Navigation implements Runnable{
    private My_Layout my_layout;
    private My_Map my_map;
    private int distance;


    public My_API_Navigation(My_Layout layout, My_Map map){
        my_layout = layout;
        my_map = map;
    }

    @Override
    public void run() {
        my_map.initUserMK();
        System.out.println(Data.Navigation_Status);
        Data.Navigation_Status = true;
        while (Data.Steps.size()>0) {
            if(Data.Navigation_Status) {
                draw_Direction();
                set_Navigation_Text();
                SystemClock.sleep(1000);
            }
            else{
                set_Direction_Camera();
                return;
            }
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
            public void onDisReady(int dis) {
                distance = dis;
            }
        });
    }
    public void set_Navigation_Text(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                int select = 0;
                if(distance<100){
                    select = 1;
                }
                my_layout.setNextRoadText(Data.Road.get(select));
                my_layout.setNextRoadDetailText(Data.Road_Detail.get(select));
                my_layout.Set_Turn_Pic(Data.Road_Detail.get(select));
                my_layout.setNowPosition(Data.now_position.toString());
                my_layout.setNextRoadDistance(Integer.toString(distance));
            }
        });
    }
    public void set_Direction_Camera(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.set_Direction_Camera();
            }
        });
    }
}
