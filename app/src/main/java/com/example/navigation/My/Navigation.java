package com.example.navigation.My;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.navigation.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

public class Navigation {
    private Context context;
    private My_Layout my_layout;
    private My_Map my_map;

    public LatLng now_position;
    public double now_distance = 0;
    public long passtime;
    //private double total_distance = 0;
    //private double last_distance = 0;


    private double step_dis=0;
    private double compensate_tmp_dis = 0;

    private double count_dis = 0;
    private double count_step_dis = 0;
    private double count_compensate_dis = 0;
    private double total_dis = 0;
    private double total_compensate_dis = 0;

    private int count = 0;

    private boolean initData = true;
    My_Direction my_direction = new My_Direction();
    ArrayList<LatLng> Routes = new ArrayList<LatLng>();
    ArrayList<LatLng> PolylineOverView = new ArrayList<LatLng>();
    ArrayList<String> Road = new ArrayList<String>();
    ArrayList<String> Road_Detail = new ArrayList<String>();
    ArrayList<LatLng> straight_line_point = new ArrayList<LatLng>();
    private float Now_Bearing = 0;
    double Now_Step_Dis = 0;

    LatLng Navigation_Now_Position = null;
    //private MarkerOptions straight_Opt = new MarkerOptions();
    //private ArrayList<Marker> straight_marker = new ArrayList<Marker>();
    private Polyline Direction;

    private double redundant_dis = 0;
    public double compensate_dis = 0;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Handler hand = new Handler();

    private double polyline_dis = 0;

    private int t = 0;

    private MarkerOptions GPS_Opt = new MarkerOptions();
    private MarkerOptions API_Opt = new MarkerOptions();
    private MarkerOptions Now_Opt = new MarkerOptions();
    private ArrayList<Marker> rec_gps = new ArrayList<Marker>();
    private ArrayList<Marker> rec_api = new ArrayList<Marker>();
    private ArrayList<Marker> rec_now = new ArrayList<Marker>();

    public Navigation(Context cont, My_Layout layout, My_Map map) {
        context = cont;
        my_layout = layout;
        my_map = map;
    }

    public void Call_API() {
        try {

            if(initData && now_position!=null) {
                now_distance = 0;
                my_direction.searchDirection(now_position);
                my_direction.SearchNavigationData(new My_Direction.onNavigationDataReadyCallBack() {
                    @Override
                    public void onDataReady(String text) {
                        //Navigation_MK_Position = now_position;
                        //取得資料
                        //Next_Step_Distance = My_Json.Get_Next_Step_Distance(text);
                        Routes = My_Json.Get_Steps(text);
                        PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine(text);
                        Road = My_Json.Get_Navigation_Road(text);
                        Road_Detail = My_Json.Get_Navigation_Road_Detail(text);
                        //straight_line_point = Divide_Straight(PolylineOverView.get(0), PolylineOverView.get(1));
                        Now_Bearing  = Cal_Method.Cal_Bearing(PolylineOverView.get(0), PolylineOverView.get(1));
                        Now_Step_Dis = Cal_Method.Cal_Distance(PolylineOverView.get(0), PolylineOverView.get(1));
                        //Draw_Direction(PolylineOverView);
                        //init_Dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(1));
                        //刷新參數
//                    smooth_one_step_dis = 0;
//                    smooth_one_step_count = 0;
//                    now_speed = 0;
                        Navigation_Now_Position = PolylineOverView.get(0);     //將相機位置改掉
                        show(Navigation_Now_Position);
//                    straight_now_step = 0;                      //刷新現在步數
//                    count_step = 0;                             //刷新計算步數
//                    Navigation_test2(now_position, 0, 1000);
                        count = 0;
                        count_dis = 0;
                        count_step_dis = 0;
                        polyline_dis = Now_Step_Dis;
                        //total_distance = 0;
                        initData = false;
                        //show(PolylineOverView.get(0), 1000);
                        //Navigation_test3(now_position, 0, true);
                    }
                });
            }
        } catch (Exception e) {
            Cal_Method.Catch_Error_Log("Call_API", e.toString());
        }
    }

    public void Navigation_Process() {
        //取得路徑
        Call_API();
        //外部輸入的GPS_dis
        //用來判斷0.1秒的Thread會不會進入判斷式
        if(!initData){
            //判斷是否有移動
            if (now_distance != 0) {
                //將移動的距離除10
                double dis = now_distance / 10.0;
                //宣告補償的變數
                double com = 0;
                //----------------------------------------------
                //判斷是否有補償的距離，且補償記數距離小於補償距離
                if(compensate_dis!=0 && count_compensate_dis < compensate_dis){
                    //將補償的距離切分成10等分
                    com = compensate_dis / 10.0;
                    //補償記數距離 用於判斷補償 是否執行完
                    count_compensate_dis = count_compensate_dis + com;
                }
                //----------------------------------------------
                //判斷 切分的速度 是否小於等於 現在的速度
                if(count_dis <= now_distance){
                    //判斷 計算每段的距離 是否小於 PolyLine的一段距離
                    if(count_step_dis < Now_Step_Dis) {
                        //計算這次要移動的距離，tmp是切分的補償距離，dis是切分的GPS移動距離
                        double tmp = dis + com;
                        //Navigation_Now_Position = Cal_Method.Cal_LatLng(Navigation_Now_Position, Now_Bearing, dis);
                        //經緯度用疊加的方式，新位置=舊位置+方向角+距離
                        Navigation_Now_Position = Cal_Method.Cal_LatLng(Navigation_Now_Position, Now_Bearing, tmp);
                        //計算並累加切分的速度
                        count_dis      = Math.round((count_dis + dis) * 10) / 10.0;
                        //計算並累加每段的距離(要轉向與判定導航，要將補償的距離加進去)
                        count_step_dis = Math.round((count_step_dis + tmp) * 10) / 10.0;
                        //計算並累加總長度(GPS在Nav中累計的距離)
                        total_dis      = Math.round((total_dis + dis) * 10) / 10.0;
                    }
                    //判斷 每段距離 是否大於等於 PolyLine的一段距離
                    if(count_step_dis >= Now_Step_Dis){
                        //如果大於等於  就代表要進行下一段PolyLine
                        //將每段距離歸零
                        count_step_dis = 0;
                        //PolyLine的計數器跳下一段(e.g 0 -> 1  或是 1 -> 2)
                        count = count + 1;
                        //判斷PolyLine的計數器是否與此段路程一樣的長度，代表導航結束
                        if (count == PolylineOverView.size() - 1) {
                            //結束導航，還有Bug
                            initData = true;
                            handler.postDelayed(remove, 50);
                        }
                        else {
                            //--------------------
                            //未知的問題:當切斷的補償距離還長於下一段時會出現甚麼問題
                            //--------------------
                            //如果沒有沒有結束導航，就進行下一段
                            //由於每段的距離不一定能個整數結束，先前有先判斷 每段距離 已經大於 上一段的PolyLine的距離
                            //當已經大於的時候計算 多餘的部分 要直接顯示
                            //所以儲存多餘的距離(1. 將現在的經緯度與上一段的最後一點取距離  或是  2. 利用每段距離 減上一段PolyLine的距離)
                            //目前是使用1
                            redundant_dis = Cal_Method.Cal_Distance(Navigation_Now_Position, PolylineOverView.get(count));
                            //計算下一段的方向角
                            Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(count), PolylineOverView.get(count + 1));
                            //計算下一段的PolyLine的距離
                            Now_Step_Dis = Cal_Method.Cal_Distance(PolylineOverView.get(count), PolylineOverView.get(count + 1));
                            //將位置更新成新一段的線上，所以再次計算經緯度，將上一段的最後一個點當作最後的位置
                            //並將新的方向角 與 多餘的距離放進去進算，更新位置
                            Navigation_Now_Position = Cal_Method.Cal_LatLng(PolylineOverView.get(count), Now_Bearing, redundant_dis);
                            //此時將PolyLine的距離儲存起來可以用來驗證PolyLine是不是真的比真實路線長
                            polyline_dis = polyline_dis + Now_Step_Dis;
                            //------------------------------------
                            //此時將目前位置丟給Google SnapRoad的API 中，看目前位置 與 導航的位置 有沒有誤差
                            Get_Compensate(Data.SnapRoad_Status);
                            //------------------------------------
                        }
                    }
                }
                //判斷補償記數距離 大於等於 補償距離
                if(count_compensate_dis >= compensate_dis){
                    //將補償的距離全部儲存起來，要看到底補償多遠
                    total_compensate_dis = total_compensate_dis + compensate_dis;
                    //將補償記數距離歸零
                    count_compensate_dis = 0;
                    //將補償距離歸零
                    compensate_dis = 0;
                }
                //統一顯示我想要證明的數據
                showText();
                //顯示導航的相機移動或畫線
                show(Navigation_Now_Position);
                //判斷 切分的速度 是否大於 現在的速度
                if(count_dis >= now_distance){
                    //如果切分速度大於等於的話，代表此段移動已結束，因為執行續是一直在執行
                    //所以要將參數歸零，才能讓執行續不要進來這裡
                    t = 0 ;
                    //速度歸零
                    now_distance = 0;
                    //切分的速度歸零，下次才能再使用
                    count_dis = 0;
                }

                //show(Navigation_Now_Position);
                //----------------------------------------------
            }
        }
    }
    public void showText(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_layout.setdataviewNavigationCountDis(total_dis + "m");
                my_layout.setdataviewNavigationNowDistance(now_distance + "m");
                my_layout.setdataviewGPSSimulationS(passtime + "ms");
                my_layout.setdataviewPolyLineDis(polyline_dis + "m");
                my_layout.setdataviewTotalCompensateDis(total_compensate_dis + "m");
            }});
    }
    public void show(LatLng point){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                //畫面顯示
                add_marker(point);                                  //新增點
                change_camera(point);                               //更改相機
                draw_direction(point, PolylineOverView, count + 1);
            }});
    }
    public void add_marker(LatLng point){
        Data.Nav_Record.add(point);
        if(Data.Nav_History) {
            Data.Nav_Opt.position(point);
            Data.Nav_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.history_point));
            Data.Nav_marker.add(my_map.Add_Marker(Data.Nav_Opt));
        }
    }
    public void change_camera(LatLng point){
        int ms = 70;
        //my_map.moveCamera(Cal_Method.Cal_LatLng(point, Now_Bearing, 60), 20, Now_Bearing, 65, ms);
        my_map.moveCamera(Cal_Method.Cal_LatLng(point, Now_Bearing, 120), 19, Now_Bearing, 65, ms);
    }
    public void draw_direction(LatLng point, ArrayList<LatLng> points, int step) {
        my_map.Remove_PolyLine(Direction);
        Direction = my_map.Draw_PolyLine(point, points, step);
    }
    private void Get_Compensate(boolean status){
        if(status && !Data.AutoPlay) {
            My_Snap_Road snap = new My_Snap_Road();
            snap.setSnapRoadUrl();
            snap.SearchLocation(new My_Snap_Road.onDataReadyCallback() {
                @Override
                public void onDataReady(LatLng data) {
                    double tmp_dis = Cal_Method.Cal_Distance(Navigation_Now_Position, data);
                    compensate_dis = tmp_dis;
                    record(data, compensate_dis);
                    //Data.API_Record.add(data);
                    Toast("補償:" + tmp_dis + "m");
                }
            });
        }
    }
    private void record(LatLng data, double dis){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                GPS_Opt.position(Data.now_position);
                GPS_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.rec_nav_gps));
                rec_gps.add(my_map.Add_Marker(GPS_Opt));
                API_Opt.position(data);
                API_Opt.title("Compensate_Dis=" + dis);
                API_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.rec_nav_api));
                rec_api.add(my_map.Add_Marker(API_Opt));
                Now_Opt.position(Navigation_Now_Position);
                Now_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.rec_nav_now));
                rec_now.add(my_map.Add_Marker(Now_Opt));
            }
        });
    }
    private void Toast(String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });

    }
    final Runnable remove = new Runnable() {
        @Override
        public void run() {
            my_map.Remove_PolyLine(Direction);
//            for(int i=0; i<straight_marker.size();i++){
//                my_map.Remove_Marker(straight_marker.get(i));
//            }
//            straight_marker.removeAll(straight_marker);
            Data.Nav_Record.removeAll(Data.Nav_Record);
            Data.Navigation_Status = false;
            initData = true;
            handler.postDelayed(return_page, 150);
        }
    };
    final Runnable return_page = new Runnable() {
        @Override
        public void run() {
            my_layout.Direction_Page(my_map);
            initData = true;
        }
    };

}
