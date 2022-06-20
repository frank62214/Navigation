package com.example.navigation.My;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.navigation.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class My_Event {
    private My_Layout my_layout;
    private My_Map my_map;
    public My_Event(My_Layout layout, My_Map map){
        my_layout = layout;
        my_map = map;
    }
    public void setEvent(){
        my_layout.btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Data.Destination==null) {
                    Toast.makeText(my_layout.getContext(), "請先選擇終點", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Toast.makeText(my_layout.getContext(), "規劃路線", Toast.LENGTH_SHORT).show();
                    My_Direction my_direction = new My_Direction();
                    my_direction.searchDirection();
                    my_direction.SearchData(new My_Direction.onDataReadyCallback() {
                        @Override
                        public void onDataReady(ArrayList<LatLng> data) {
                            my_layout.Direction_Page();
                            my_map.Draw_Direction(data);
                        }
                        @Override
                        public void onDisReady(int dis) {

                        }
                    });
                }
            }
        });
        my_layout.btnFocusUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(my_layout.getContext(), "現在位置", Toast.LENGTH_SHORT).show();
                my_map.moveCamera(Data.now_position);
            }
        });
        my_layout.btnNavigation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Toast.makeText(my_layout.getContext(), "開始導航", Toast.LENGTH_SHORT).show();
                my_layout.Navigation_Page();
                My_Navigation my_navigation = new My_Navigation(my_layout, my_map);
                Thread t = new Thread(my_navigation);
                t.start();
            }
        });
    }

}
