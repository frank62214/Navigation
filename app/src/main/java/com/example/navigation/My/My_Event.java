package com.example.navigation.My;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.navigation.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class My_Event {
    private My_Layout my_layout;
    private My_Map my_map;

    private double time = 0.0;
    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler(Looper.getMainLooper());

    public My_Event(My_Layout layout, My_Map map) {
        my_layout = layout;
        my_map = map;
    }

    public void setEvent() {
        timer = new Timer();
        my_layout.btnSnapCalSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Data.CalStatus){
                    Data.CalStatus = false;
                    my_layout.btnSnapCalSwitch.setBackgroundResource(R.drawable.btn_round);
                    my_layout.Toast("關閉計算補償");
                }
                else{
                    Data.CalStatus = true;
                    my_layout.btnSnapCalSwitch.setBackgroundResource(R.drawable.btn_round_pressed);
                    my_layout.Toast("開啟計算補償");
                }
            }
        });
        my_layout.btnAccSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Data.AccStatus){
                    Data.AccStatus = false;
                    my_layout.btnAccSwitch.setBackgroundResource(R.drawable.btn_round);
                    my_layout.Toast("關閉加速度計補償");
                }
                else{
                    Data.AccStatus = true;
                    my_layout.btnAccSwitch.setBackgroundResource(R.drawable.btn_round_pressed);
                    my_layout.Toast("開啟加速度計補償");
                }
            }
        });
        my_layout.btnNavHistoryRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Data.Nav_History){
                    my_layout.btnNavHistoryRoute.setBackgroundResource(R.drawable.btn_round);
                    my_layout.Toast("關閉歷史紀錄");
                    my_map.removeNav();
                }
                else{
                    my_layout.btnNavHistoryRoute.setBackgroundResource(R.drawable.btn_round_pressed);
                    my_layout.Toast("開啟歷史紀錄");
                    my_map.Nav();
                }
            }
        });
        my_layout.btnAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Data.AutoPlay){
                    Data.AutoPlay = false;
                    my_layout.btnAutoPlay.setBackgroundResource(R.drawable.btn_round);
                    my_layout.Toast("關閉自動播放");
                }
                else{
                    Data.AutoPlay = true;
                    my_layout.btnAutoPlay.setBackgroundResource(R.drawable.btn_round_pressed);
                    my_layout.Toast("開啟自動播放");
                }
            }
        });
        my_layout.btnWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Data.lldata_view_visible) {
                    Data.lldata_view_visible = false;
                    my_layout.lldata_view.setVisibility(View.GONE);
                }
                else{
                    Data.lldata_view_visible = true;
                    my_layout.lldata_view.setVisibility(View.VISIBLE);
                }
            }
        });
        my_layout.btnTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data.GPS_Record.removeAll(Data.GPS_Record);
                Data.API_Record.removeAll(Data.API_Record);
                Data.Cal_Record.removeAll(Data.Cal_Record);
                my_layout.Toast("清除暫存資料");
            }
        });
        my_layout.btnSnapRoadSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Data.SnapRoad_Status){
                    Data.SnapRoad_Status = false;
                    my_layout.btnSnapRoadSwitch.setBackgroundResource(R.drawable.btn_round);
                    my_layout.Toast("關閉SnapRoad補償");
                }
                else{
                    Data.SnapRoad_Status = true;
                    my_layout.btnSnapRoadSwitch.setBackgroundResource(R.drawable.btn_round_pressed);
                    my_layout.Toast("開啟SnapRoad補償");
                }
                if(Data.AutoPlay) {
                    Data.compensate_dis_test = 10;
                }
            }
        });
        my_layout.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //True 為顯示History
                if(Data.History_Status) {
                    //更改Status
                    Data.History_Status = false;
                    //隱藏兩個按鈕
                    my_layout.btnHistoryDot.setVisibility(View.GONE);
                    my_layout.btnHistoryLine.setVisibility(View.GONE);
                    //更改圖示
                    if(item_select(Data.Dot_Items) && item_select(Data.Line_Items)){
                        my_layout.SetbtnHistory("both");
                    }
                    else if(item_select(Data.Dot_Items)){
                        my_layout.SetbtnHistory("dot");
                    }
                    else if(item_select(Data.Line_Items)){
                        my_layout.SetbtnHistory("line");
                    }
                    else{
                        my_layout.SetbtnHistory("none");
                    }
                }else{
                    //更改Status
                    Data.History_Status = true;
                    //將圖示改回來
                    my_layout.SetbtnHistory("none");
                    //顯示按鈕
                    my_layout.btnHistoryDot.setVisibility(View.VISIBLE);
                    my_layout.btnHistoryLine.setVisibility(View.VISIBLE);
                }
            }
        });
        my_layout.btnHistoryDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set up the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("選擇要顯示的歷史紀錄(點)");

                // Add a checkbox list
                String[] animals = {"GPS", "API", "計算點"};
                //boolean[] checkedItems = {Data.GPS_Line, Data.API_Line, Data.Cal_Line};
                builder.setMultiChoiceItems(animals, Data.Dot_Items, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Data.Dot_Items[which] = isChecked;
                    }
                });

                // Add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user clicked OK
                        boolean select = item_select(Data.Dot_Items);
                        my_map.Draw_Record_Marker();
                        my_layout.SetbtnHistoryDot(select);
                    }
                });
                // Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        my_layout.btnHistoryLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set up the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("選擇要顯示的歷史紀錄(線)");

                // Add a checkbox list
                String[] animals = {"GPS", "API", "計算點"};
                //boolean[] checkedItems = {Data.GPS_Line, Data.API_Line, Data.Cal_Line};
                builder.setMultiChoiceItems(animals, Data.Line_Items, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        Data.Line_Items[which] = isChecked;
                    }
                });

                // Add OK and Cancel buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // The user clicked OK
                        boolean select = item_select(Data.Line_Items);
                        my_map.Draw_Record_Marker_PolyLine();
                        my_layout.SetbtnHistoryLine(select);
                    }
                });
                // Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });




        my_layout.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Data.Record_Timer_Status){
                    Data.Record_Timer_Status = false;
                    my_layout.Toast("結束記錄");
                    my_layout.btnRecord.setBackgroundResource(R.drawable.rec_button_press_up);
                    timerTask.cancel();
                }
                else{
                    Data.Record_Timer_Status = true;
                    my_layout.Toast("開始記錄");
                    my_layout.btnRecord.setBackgroundResource(R.drawable.rec_button_press_down);
                    start_Timer();
                }
            }
        });
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

                //2022.08.03 測試紀錄
                //My_API_Navigation my_api_navigation = new My_API_Navigation(my_layout, my_map);
                //Thread t = new Thread(my_api_navigation);
                //t.start();
//

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
                //System.out.println(Data.Mode);
            }
        });
        my_layout.btnBicyclingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_layout.Select_Mode(Data.Bicycling);
                //System.out.println(Data.Mode);
            }
        });
        my_layout.btnWalkingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                my_layout.Select_Mode(Data.Walking);
                //System.out.println(Data.Mode);
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
                //System.out.println(destination);
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
                my_layout.CarMode_Page();
                my_map.setMyLocationEnabled(false);
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
                    //my_layout.Toast(location.get(id).toString());
                    Data.Destination = location.get(id);
                    my_map.Add_Destination_Mark(location.get(id));
                    Toast.makeText(my_layout.getContext(), "規劃路線", Toast.LENGTH_SHORT).show();
                    My_Direction my_direction = new My_Direction();
                    my_direction.searchDirection();
                    my_direction.SearchData(new My_Direction.onDataReadyCallback() {
                        @Override
                        public void onDataReady(ArrayList<LatLng> data) {
                            my_layout.Direction_Page(my_map);
                            //my_map.Draw_Direction(data);
                            Draw_Direction(data);
                            //my_map.Draw_Direction(Data.Decoder_Steps);
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
    private boolean item_select(boolean[] item){
        for(int i=0; i<item.length; i++){
            if(item[i]){
                return true;
            }
        }
        return false;
    }
    public void close_keyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(view.getContext().INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void start_Timer(){
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        my_layout.setdataviewRecordTimer(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }
    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }
    private void Draw_Direction(ArrayList<LatLng> decoder_Steps){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.Draw_Direction(decoder_Steps);
            }});
    }
}
