package com.example.navigation.My;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;

public class My_Navigation implements Runnable{
    private My_Layout my_layout;
    private My_Map my_map;
    private String Road = "";
    private String Road_Detail = "";
    private double distance = 100;
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
        while (count < Data.Steps.size()){
            distance = my_map.Camera_Dis_cal(Data.now_position, Data.Steps.get(count - 1));
            if(distance < 15){
                count++;
            }
            Road = Data.Road.get(count);
            Road_Detail = Data.Road_Detail.get(count);
            get_Main_Thread();
            SystemClock.sleep(1000);
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
            }
        });
    }

}
