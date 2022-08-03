package com.example.navigation.My;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class My_Navigation implements Runnable{
    private My_Layout my_layout;
    private My_Map my_map;
    private String Road = "";
    private String Road_Detail = "";
    private double distance = 100;
    private double last_distance = 100;
    private int real_dis = 100;
    private ArrayList<Boolean> re_direction = new ArrayList<Boolean>();
    @RequiresApi(api = Build.VERSION_CODES.M)
    public My_Navigation(My_Layout layout, My_Map map){
        my_layout = layout;
        my_map = map;
        My_Sensor my_sensor = new My_Sensor(my_layout.getContext());
        my_sensor.registerListener();
    }
    @Override
    public void run() {
        int count = 1;
        distance = my_map.Camera_Dis_cal(Data.now_position, Data.Steps.get(count - 1));
        last_distance  = distance;
        while (count < Data.Steps.size()){
            distance = my_map.Camera_Dis_cal(Data.now_position, Data.Steps.get(count - 1));
            if(distance < 30){
                //count++;
                get_Real_Dis(Data.now_position, Data.Steps.get(count-1));
                if(real_dis<10){
                    count++;
                }
            }
            if(last_distance >= distance){
                //System.out.println(last_distance);
                last_distance = distance;
                re_direction.removeAll(re_direction);
            }
            else{
                re_direction.add(true);
            }
            if(re_direction.size()<10) {
                Road = Data.Road.get(count);
                Road_Detail = Data.Road_Detail.get(count);
                get_Main_Thread();

            }
            else{
                re_direction.removeAll(re_direction);
                Toast("重新搜尋");
            }
        }
    }
    private void get_Main_Thread(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() { ;
                my_map.set_Navigation_Camera();
                my_layout.setNextRoadText(Road);
                my_layout.setNextRoadDetailText(Road_Detail);
                my_layout.Set_Turn_Pic(Road_Detail);
                my_layout.setNowPosition(Data.now_position.toString());
                my_layout.setNextRoadDistance("距離:" + Double.toString(distance));
//                my_layout.setLastDistance("上次距離:" + Double.toString(last_distance));
//                my_layout.setNow_Bearing(Float.toString(Data.now_bearing));
            }
        });
    }
    private void get_Real_Dis(LatLng now, LatLng next){
        My_Direction my_direction = new My_Direction();
        my_direction.setDistanceUrl(now, next);
        my_direction.SearchDistance(new My_Direction.onDataReadyCallback() {

            @Override
            public void onDisReady(int dis) {
                real_dis = dis;
                Toast("距離:" + Integer.toString(dis));
                //my_layout.Toast("距離:" + Integer.toString(dis));
            }
            @Override
            public void onDataReady(ArrayList<LatLng> data) {
            }
            @Override
            public void onStartLocationReady(LatLng start, LatLng end) {}
        });
    }
    private void Toast(String text){
        my_layout.Toast(text);
    }

}
