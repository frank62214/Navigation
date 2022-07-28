package com.example.navigation.My;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.navigation.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class My_Event {
    private My_Layout my_layout;
    private My_Map my_map;

    public My_Event(My_Layout layout, My_Map map) {
        my_layout = layout;
        my_map = map;
    }

    public void setEvent() {
        my_layout.btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Data.Destination == null) {
                    Toast.makeText(my_layout.getContext(), "請先選擇終點", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //Data.Page_Order.add(Data.Direction_Page);
                    Toast.makeText(my_layout.getContext(), "規劃路線", Toast.LENGTH_SHORT).show();
                    My_Direction my_direction = new My_Direction();
                    my_direction.searchDirection();
                    my_direction.SearchData(new My_Direction.onDataReadyCallback() {
                        @Override
                        public void onDataReady(ArrayList<LatLng> data) {
                            my_layout.Direction_Page(my_map);
                            //my_map.set_Direction_Camera();
                            my_map.Draw_Direction(data);
                        }
                        @Override
                        public void onDisReady(int dis) {}
                        @Override
                        public void onStartLocationReady(LatLng start, LatLng end) {}
                    });
                }
            }
        });
        my_layout.btnFocusUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(my_layout.getContext(), "現在位置", Toast.LENGTH_SHORT).show();
                System.out.println(Data.now_position);
                my_map.moveCamera(Data.now_position);
            }
        });
        my_layout.btnFocusUser.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                if(Data.Lock_User){
                    Toast.makeText(my_layout.getContext(), "解除鎖定使用者位置", Toast.LENGTH_SHORT).show();
                    Data.Lock_User = false;
                    my_layout.btnFocusUser.setBackgroundResource(R.drawable.btn_round);
                }
                else{
                    Toast.makeText(my_layout.getContext(), "鎖定使用者位置", Toast.LENGTH_SHORT).show();
                    Data.Lock_User = true;
                    my_layout.btnFocusUser.setBackgroundResource(R.drawable.btn_round_pressed);
                }
                return false;
            }
        });
        my_layout.btnNavigation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Toast.makeText(my_layout.getContext(), "開始導航", Toast.LENGTH_SHORT).show();
                my_layout.Navigation_Page(my_map);
//                My_Navigation my_navigation = new My_Navigation(my_layout, my_map);
//                Thread t = new Thread(my_navigation);
//                t.start();
                My_API_Navigation my_api_navigation = new My_API_Navigation(my_layout, my_map);
                Thread t = new Thread(my_api_navigation);
                t.start();
            }
        });
        my_layout.et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(my_layout.getContext(), "搜尋目的地", Toast.LENGTH_SHORT).show();
//                Data.Page_Order.add(Data.Search_Page);
                my_layout.Search_Page();
            }
        });
        my_layout.btnDrivingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_layout.Select_Mode(Data.Driving);
                System.out.println(Data.Mode);
            }
        });
        my_layout.btnBicyclingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_layout.Select_Mode(Data.Bicycling);
                System.out.println(Data.Mode);
            }
        });
        my_layout.btnWalkingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_layout.Select_Mode(Data.Walking);
                System.out.println(Data.Mode);
            }
        });
        my_layout.btnViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_layout.Remove_Result();
                //Data.Page_Order.add(Data.Search_Page);
                close_keyboard(view);
                my_layout.SearchProgressBar.setVisibility(View.VISIBLE);
                String destination = my_layout.et_search.getText().toString();
                System.out.println(destination);
                //Toast.makeText(my_layout.getContext(), "搜尋目的地", Toast.LENGTH_SHORT).show();
                My_Search my_search = new My_Search();
                my_search.SearchDestination(destination);
                my_search.SearchData(new My_Search.onDataReadyCallback() {
                    @Override
                    public void onDataNameReady(ArrayList<String> data, ArrayList<LatLng> location) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                my_layout.Set_Search_Place_Result(data);
                                my_layout.SearchProgressBar.setVisibility(View.GONE);
                                Set_Search_Button_Event(my_layout.Search_Button_Group, location);
                            }
                        });
                    }

                });
            }
        });
        my_layout.btnCarMode.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //Data.Page_Order.add(Data.CarMode_Page);
                my_layout.CarMode_Page();
                my_map.setMyLocationEnabled(false);
                My_Sensor my_sensor = new My_Sensor(my_layout.getContext());
                my_sensor.registerListener();
                My_CarMode my_carMode = new My_CarMode(my_layout, my_map);
                Thread t = new Thread(my_carMode);
                t.start();
            }
        });
    }
    private void Set_Search_Button_Event(ArrayList<Button> group, ArrayList<LatLng> location){
        for(int i=0; i<group.size();i++){
            Button button = (Button) group.get(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int id = view.getId();
                    my_layout.Toast(location.get(id).toString());
                    Data.Destination = location.get(id);
                    my_map.Add_Destination_Mark(location.get(id));
                    Toast.makeText(my_layout.getContext(), "規劃路線", Toast.LENGTH_SHORT).show();
                    My_Direction my_direction = new My_Direction();
                    my_direction.searchDirection();
                    my_direction.SearchData(new My_Direction.onDataReadyCallback() {
                        @Override
                        public void onDataReady(ArrayList<LatLng> data) {
                            my_layout.Direction_Page(my_map);
                            my_map.Draw_Direction(data);
                        }
                        @Override
                        public void onDisReady(int dis) {}
                        @Override
                        public void onStartLocationReady(LatLng start, LatLng end) {}
                    });
                }
            });
        }
    }
    public void close_keyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(view.getContext().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
