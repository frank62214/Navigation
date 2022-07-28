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
    private LatLng last_position;
    private int distance;


    public My_API_Navigation(My_Layout layout, My_Map map){
        my_layout = layout;
        my_map = map;
    }

    @Override
    public void run() {
        my_map.initUserMK();
        //System.out.println(Data.Navigation_Status);
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
                my_layout.Toast(Float.toString(bearing));
                if(last_position==null){last_position=start;}
                if(cal_distance(last_position, start)<15){
                    last_position = start;
                    my_map.set_Navigation_Camera(start, bearing);
                }
                else {
                    my_map.set_Navigation_Camera(last_position, bearing);
                }
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
                System.out.println("distance: " + distance);
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
    private double cal_distance(LatLng Start, LatLng End){
        double EARTH_RADIUS = 6378137.0;
        //double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (Start.latitude * Math.PI / 180.0);
        double radLat2 = (End.latitude * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (Start.longitude - End.longitude) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2)
                        * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
