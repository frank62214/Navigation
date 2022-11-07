package com.example.navigation.My;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.example.navigation.R;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.util.ArrayList;

public class My_CarMode{

    private Context context;
    private My_Layout my_layout;
    private My_Map my_map;
    private Handler handler = new Handler(Looper.getMainLooper());

    public LatLng now_position;
    public LatLng now_direction;
    public double now_distance;
    public double gps_distance;
    public double now_bearing;

    private LatLng carMode_postition = null;
    private LatLng carMode_last_postition = null;

    ArrayList<LatLng> Routes = new ArrayList<LatLng>();
    ArrayList<LatLng> PolylineOverView = new ArrayList<LatLng>();
    ArrayList<String> Turn = new ArrayList<>();
    ArrayList<String> Road = new ArrayList<String>();
    ArrayList<String> Road_Detail = new ArrayList<String>();
    ArrayList<ArrayList<LatLng>> PolyLineSteps = new ArrayList<>();

    private double count_dis = 0;

    private float Bearing =0;

    private float Now_Bearing = 0;
    private float Last_Bearing = 0;
    double Now_Step_Dis = 0;

    boolean initData = true;
    My_Direction my_direction = new My_Direction();

    LatLng Navigation_Now_Position = null;
    //private MarkerOptions straight_Opt = new MarkerOptions();
    //private ArrayList<Marker> straight_marker = new ArrayList<Marker>();
    private Polyline Direction;

    private double redundant_dis = 0;
    public double compensate_dis = 0;

    private double last_position_diff = 0;
    private double position_diff = 0;
    private double bearing_diff = 0;
    private boolean initacc = true;

    boolean far_away_once = true;
    int road_count = 0;
    int turn_count = 0;
    double last_dis = 0;

    private double count_step_dis = 0;
    private double count_compensate_dis = 0;
    private double total_dis = 0;
    private double total_compensate_dis = 0;

    private int count = 0;
    String road = "繼續直行";
    String turn = "繼續直行";
    String detail = "";

    private double polyline_dis = 0;
    private int too_far = 0;
    private double too_far_dis = 0;


    public My_CarMode(Context cont, My_Layout layout, My_Map map){
        context = cont;
        my_layout = layout;
        my_map = map;
    }
    public void Call_API(){
//        try {
////            if(too_far > 10){
////                initData = true;
////            }
//            if(initData && now_position!=null && Data.Navigation_Status) {
//                initData = false;
//                now_distance = 0;
//                now_direction = Cal_Method.Cal_LatLng(now_position, now_bearing, 100);
//                my_direction.setDistanceUrl(now_position, now_direction);
//                my_direction.SearchNavigationData(new My_Direction.onNavigationDataReadyCallBack() {
//                    @Override
//                    public void onDataReady(String text) {
//                        //Navigation_MK_Position = now_position;
//                        //取得資料
//                        //Next_Step_Distance = My_Json.Get_Next_Step_Distance(text);
//                        Routes = My_Json.Get_Steps(text);
//                        PolyLineSteps    = My_Json.Get_Navigation_PolyLine_Step(text);
//                        //System.out.println(Routes.size());
//                        //System.out.println(PolyLineSteps.size());
//                        //System.out.println(PolyLineSteps);
//                        PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine(text);
//                        //PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine_1(PolyLineSteps);
//
//
//                        Turn = My_Json.Get_Navigation_Turn(text);
//                        Road = My_Json.Get_Navigation_Road(text);
//                        Road_Detail = My_Json.Get_Navigation_Road_Detail(text);
//
////                        System.out.println("Routes = " + Routes.size());
////                        System.out.println("Road = " + Road.size());
////                        System.out.println("Road_Detail = " + Road_Detail.size());
//
//                        //straight_line_point = Divide_Straight(PolylineOverView.get(0), PolylineOverView.get(1));
//                        Now_Bearing  = Cal_Method.Cal_Bearing(PolylineOverView.get(0), PolylineOverView.get(1));
//                        Last_Bearing = Now_Bearing;
//                        Now_Step_Dis = Cal_Method.Cal_Distance(PolylineOverView.get(0), PolylineOverView.get(1));
//                        //Draw_Direction(PolylineOverView);
//                        //init_Dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(1));
//                        //刷新參數
////                    smooth_one_step_dis = 0;
////                    smooth_one_step_count = 0;
////                    now_speed = 0;
//                        Navigation_Now_Position = PolylineOverView.get(0);     //將相機位置改掉
//                        //show(Navigation_Now_Position);
//                        //showRoad();
////                    straight_now_step = 0;                      //刷新現在步數
////                    count_step = 0;                             //刷新計算步數
////                    Navigation_test2(now_position, 0, 1000);
//                        //------------------------------------
//                        //initUserMK(Navigation_Now_Position);
//                        last_position_diff = Cal_Method.Cal_Distance(Data.now_position, Navigation_Now_Position);
//                        //------------------------------------
//                        count = 0;
//                        count_dis = 0;
//                        count_step_dis = 0;
//                        polyline_dis = Now_Step_Dis;
//                        //total_distance = 0;
//
//                        too_far = 0;
//                        initacc = true;
//                        too_far_dis = 0;
//
//                        far_away_once = true;
//                        road_count = 0;
//                        turn_count = 0;
//                        last_dis = 0;
//                        String road = "繼續直行";
//
//
//                        initData = false;
//                        //show(PolylineOverView.get(0), 1000);
//                        //Navigation_test3(now_position, 0, true);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            Cal_Method.Catch_Error_Log("Call_API", e.toString());
//        }
    }
    public void Call_API_1(){
        try{
            if(Data.CarMode_Status) {
                My_Snap_Road my_snap_road = new My_Snap_Road();
                my_snap_road.setSnapRoadUrl(now_position);
                my_snap_road.SearchLocation(new My_Snap_Road.onDataReadyCallback() {
                    @Override
                    public void onDataReady(LatLng data) {
                        carMode_postition = data;
                    }
                });
            }
        }
        catch (Exception e){
            Cal_Method.Catch_Error_Log("CarMode Call_API", e.toString());
        }
    }
    public void carMode_Process(){
//        //取得路徑
//        Call_API();
//        //外部輸入的GPS_dis
//        //用來判斷0.1秒的Thread會不會進入判斷式
//        if(!initData){
//            //判斷是否有移動
//            if (now_distance != 0) {
//                //----------------------------------------------
//                //第二種計算方法(利用固定係數)
//                if(Data.SnapRoad_Status) {
//                    position_diff = Cal_Method.Cal_Distance(Data.now_position, Navigation_Now_Position);
//                    double diff = last_position_diff - position_diff;
//                    bearing_diff = getBearing();
//                    //夾角大於90度(現在位置在圖標得後方)，所以加速
//                    if (bearing_diff > 90){
//                        coefficient = 1.1;
//                    }
//                    //夾角小於90度(現在位置在圖標得前方)，所以減速
//                    else if(bearing_diff < 90){
//                        coefficient = 0.9;
//                    }
//                    last_position_diff = position_diff;
//                }
//                //第三種計算方法(利用加速度技)
//                if(initacc && Data.AccStatus){
//                    initacc = false;
//                    coefficient = 1;
//                    now_distance = now_distance + acc_dis;
//                }
//                //----------------------------------------------
//                //System.out.println("count_dis = " + count_dis);
//                //----------------------------------------------
//                //將移動的距離除10
//                //double dis = now_distance / 10.0;
//                double dis = now_distance * coefficient / 10.0;
//                //----------------------------------------------
//                //double dis = now_distance / 100.0;
//                //宣告比例補償變數
//                double cal = Calculate_dis / 10.0;
//                //判斷 切分的速度 是否小於等於 現在的速度
//                if(count_dis <= now_distance){
//                    //判斷 計算每段的距離 是否小於 PolyLine的一段距離
//                    if(count_step_dis < Now_Step_Dis) {
//                        //計算這次要移動的距離，tmp是切分的補償距離，dis是切分的GPS移動距離
//                        double tmp = dis + cal;
//                        //Navigation_Now_Position = Cal_Method.Cal_LatLng(Navigation_Now_Position, Now_Bearing, dis);
//                        //經緯度用疊加的方式，新位置=舊位置+方向角+距離
//                        Navigation_Now_Position = Cal_Method.Cal_LatLng(Navigation_Now_Position, Now_Bearing, tmp);
//                        //計算並累加切分的速度
//                        count_dis      = Math.round((count_dis + dis) * 10) / 10.0;
//                        //計算並累加每段的距離(要轉向與判定導航，要將補償的距離加進去)
//                        count_step_dis = Math.round((count_step_dis + tmp) * 10) / 10.0;
//                        //計算並累加總長度(GPS在Nav中累計的距離)
//                        total_dis      = Math.round((total_dis + dis) * 10) / 10.0;
//                    }
//                    //判斷 每段距離 是否大於等於 PolyLine的一段距離
//                    if(count_step_dis >= Now_Step_Dis){
//                        //PolyLine的計數器跳下一段(e.g 0 -> 1  或是 1 -> 2)
//                        count = count + 1;
//                        //判斷PolyLine的計數器是否與此段路程一樣的長度，代表導航結束
//                        if (count == PolylineOverView.size() - 1) {
//                            //結束導航，還有Bug
//                            initData = true;
//                            handler.postDelayed(remove, 5);
//                        }
//                        else {
//                            //--------------------
//                            //未知的問題:當切斷的補償距離還長於下一段時會出現甚麼問題
//                            //--------------------
//                            //如果沒有沒有結束導航，就進行下一段
//                            //由於每段的距離不一定能個整數結束，先前有先判斷 每段距離 已經大於 上一段的PolyLine的距離
//                            //當已經大於的時候計算 多餘的部分 要直接顯示
//                            //所以儲存多餘的距離(1. 將現在的經緯度與上一段的最後一點取距離  或是  2. 利用每段距離 減上一段PolyLine的距離)
//                            //目前是使用2
//                            //redundant_dis = Cal_Method.Cal_Distance(Navigation_Now_Position, PolylineOverView.get(count));
//                            redundant_dis = count_step_dis - Now_Step_Dis;
//                            //計算下一段的方向角
//                            Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(count), PolylineOverView.get(count + 1));
//                            //計算下一段的PolyLine的距離
//                            Now_Step_Dis = Cal_Method.Cal_Distance(PolylineOverView.get(count), PolylineOverView.get(count + 1));
//                            //將位置更新成新一段的線上，所以再次計算經緯度，將上一段的最後一個點當作最後的位置
//                            //並將新的方向角 與 多餘的距離放進去進算，更新位置
//                            Navigation_Now_Position = Cal_Method.Cal_LatLng(PolylineOverView.get(count), Now_Bearing, redundant_dis);
//                            //此時將PolyLine的距離儲存起來可以用來驗證PolyLine是不是真的比真實路線長
//                            polyline_dis = polyline_dis + Now_Step_Dis;
//                            //------------------------------------
////                            if(getTheta(Last_Bearing, Now_Bearing) < 80){
////                                System.out.println(getTheta(Last_Bearing, Now_Bearing));
////                            }
//                            //此時將目前位置丟給Google SnapRoad的API 中，看目前位置 與 導航的位置 有沒有誤差
//                            //Get_Compensate(Data.SnapRoad_Status);
//                            //------------------------------------
//                        }
//                        //如果大於等於  就代表要進行下一段PolyLine
//                        //將每段距離歸零
//                        count_step_dis = 0;
//                    }
//                }
//                //統一顯示我想要證明的數據
//                showText();
//                //顯示導航的相機移動或畫線
//                show(Navigation_Now_Position);
//                //顯示導航的下一個路徑
//                showRoad();
//                //判斷 切分的速度 是否大於 現在的速度
//                if(count_dis >= now_distance){
//                    //如果切分速度大於等於的話，代表此段移動已結束，因為執行續是一直在執行
//                    //所以要將參數歸零，才能讓執行續不要進來這裡
//                    t = 0 ;
//                    //速度歸零
//                    now_distance = 0;
//                    //切分的速度歸零，下次才能再使用
//                    count_dis = 0;
//                    //計算距離歸零
//                    Calculate_dis = 0;
//                    initacc = false;
//                }
//                if(getTheta()>120 || too_far_dis < position_diff && !Data.AutoPlay){
//                    too_far_dis = position_diff;
//                    too_far++;
//                    Toast("重新規劃路線");
//                }
//                if(too_far>10 && !Data.AutoPlay){
//                    initData = true;
//                }
//                //show(Navigation_Now_Position);
//                //----------------------------------------------
//            }
//        }
    }
//    public void show(LatLng point){
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            public void run() {
//                //畫面顯示
//                change_MK(point);
//                add_marker(point);                                  //新增點
//                change_camera(point);                               //更改相機
//                draw_direction(point, PolylineOverView, count + 1);
//            }});
//    }
    private double getTheta(){
        double T=0;
        if(now_bearing > Now_Bearing){
            T = now_bearing - Now_Bearing;
        }else{
            T = Now_Bearing - now_bearing;
        }
        if(T > 180){
            T = 360 - 180;
        }
        return T;
    }
    public void change_camera(LatLng point){
        int ms = 70;
        //int ms = 10;
        //my_map.moveCamera(Cal_Method.Cal_LatLng(point, Now_Bearing, 60), 20, Now_Bearing, 65, ms);
        my_map.moveCamera(Cal_Method.Cal_LatLng(point, Bearing, 120), 19, Bearing, 65, ms);
    }
    private void test_addMarker(LatLng data){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.addMark(data);
            }});
    }

//    public void showText(){
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            public void run() {
//                my_layout.setdataviewNavigationCountDis(total_dis + "m");
//                my_layout.setdataviewNavigationNowDistance(now_distance + "m");
//                my_layout.setdataviewGPSSimulationS(passtime + "ms");
//                my_layout.setdataviewPolyLineDis(polyline_dis + "m");
//                my_layout.setdataviewTotalCompensateDis(total_compensate_dis + "m");
//                my_layout.setdataviewNavigationCalDistance(Calculate_dis + "m");
//                my_layout.setdataviewSeneorCurrentdis(acc_dis + "m/s");
//                my_layout.setdataviewCoefficient(coefficient + "");
//                my_layout.setdataviewgetBearing(bearing_diff + "度");
//            }});
//    }

    final Runnable return_page = new Runnable() {
        @Override
        public void run() {
            my_layout.Main_Page(my_map);
        }
    };
}
