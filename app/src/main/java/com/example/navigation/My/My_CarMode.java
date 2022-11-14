package com.example.navigation.My;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

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
    My_Direction my_direction = new My_Direction();
    private Handler handler = new Handler(Looper.getMainLooper());

    public LatLng now_position;
    public double now_distance;


    private LatLng nowPosition , lastPosition;
    private float nowBearing = 0 , lastBearing = 361;
    private double nowDistance = 0;
    private double passDistance = 0;
    private double stepDistance = 0;

    boolean firstCamera = true;
    private MarkerOptions GPS_Opt = new MarkerOptions();
    private ArrayList<Marker> recGPS = new ArrayList<Marker>();

    public My_CarMode(Context cont, My_Layout layout, My_Map map){
        context = cont;
        my_layout = layout;
        my_map = map;
    }
    private RouteData now  = new RouteData();
    private RouteData next = new RouteData();
    private ArrayList<LatLng> Routes = new ArrayList<LatLng>();
    private ArrayList<ArrayList<LatLng>> PolyLineSteps = new ArrayList<ArrayList<LatLng>>();
    private ArrayList<LatLng> firstSteps = new ArrayList<LatLng>();
    private double firstStepsDis = 0;
    private boolean halfCall  = false;
    private boolean callApi   = true;
    private boolean dataReady = false;
    private void RestoreData(RouteData data){
        Routes        = data.Routes;
        PolyLineSteps = data.PolyLineSteps;
        firstSteps    = data.firstSteps;
        //nowBearing    = data.Bearing;
        //nowPosition   = data.Position;
        firstStepsDis = data.firstStepsDis;
    }
    public void Call_API() {
        if(Data.CarMode_Status && callApi) {
            callApi = false;
            LatLng pos = now_position;
            LatLng tmp = Cal_Method.Cal_LatLng(now_position, nowBearing, 500);
            if(Data.AutoPlay) {
                tmp = Cal_Method.Cal_LatLng(nowPosition, nowBearing, 500);
                pos = nowPosition;
            }
            my_direction.setDistanceUrl(pos, tmp);
            my_direction.SearchNavigationData(new My_Direction.onNavigationDataReadyCallBack() {
                @Override
                public void onDataReady(String text) {
                    if(!halfCall) {
                        now.StoreData(text, 0);
                        RestoreData(now);
                        nowBearing  = now.Bearing;
                        nowPosition = now.Position;
                        My_Json.show(now.Routes);
                        show(nowPosition, nowBearing);
                    }
                    else{
                        next.StoreData(text, 1);
                        My_Json.show(next.Routes);
                        dataReady = false;
                        RestoreData(next);
                        allClean();
                    }
                    halfCall = false;
                    dataReady = true;

//
//                PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine(text);
//
//                Turn = My_Json.Get_Navigation_Turn(text);
//                Road = My_Json.Get_Navigation_Road(text);
//                Road_Detail = My_Json.Get_Navigation_Road_Detail(text);
//                Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(0), PolylineOverView.get(1));
//                //Now_Bearing = Cal_Method.Cal_Bearing(Data.now_position, PolylineOverView.get(0));
//                //---------------------------------------------------------------------------------------
//                Last_Bearing = Now_Bearing;
//                Now_Step_Dis = Cal_Method.Cal_Distance(PolylineOverView.get(0), PolylineOverView.get(1));
//                //---------------------------------------------------------------------------------------
//                //Draw_Direction(PolylineOverView);
//                //init_Dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(1));
//                //刷新參數
////                    smooth_one_step_dis = 0;
////                    smooth_one_step_count = 0;
////                    now_speed = 0;
//                Navigation_Now_Position = PolylineOverView.get(0);     //將相機位置改掉
//                show(Navigation_Now_Position);
//                //showRoad();
////                    straight_now_step = 0;                      //刷新現在步數
////                    count_step = 0;                             //刷新計算步數
////                    Navigation_test2(now_position, 0, 1000);
//                //------------------------------------
//                //initUserMK(Navigation_Now_Position);
//                last_position_diff = Cal_Method.Cal_Distance(Data.now_position, Navigation_Now_Position);
//                //------------------------------------
//                count = 0;
//                count_dis = 0;
//                count_step_dis = 0;
//                polyline_dis = Now_Step_Dis;
//                //total_distance = 0;
//
//                too_far = 0;
//                initacc = true;
//                too_far_dis = 0;
//
//                far_away_once = true;
//                road_count = 0;
//                turn_count = 0;
//                last_dis = 0;
//
//
//                road = Road.get(0);
//                detail = Road_Detail.get(0);
//                turn = Turn.get(0);
//
//
//                initData = false;
                }
            });
        }
    }
    private double countDis = 0;
    private double countFirstStepDis = 0;
    private int countFirstSteps = 0;
    private double finalDis = 0;
    private LatLng finalPosition;
    private float finalBearing;
    private int count=0;
    public void carMode_Process(){
        Call_API();
        if(dataReady){
            //dataReady = false; //測試用 會讓執行續只跑一次
            if(now_distance!=0){
                double dis = now_distance / 10.0;
                countDis += dis;
                countFirstStepDis += dis;
                if(countDis <= now_distance){
                    nowPosition = calPosition(nowPosition, nowBearing, dis);
                }
                if(countDis >= now_distance){
                    countDis = 0;
                    now_distance = 0;
                }
                if(countFirstStepDis > firstStepsDis){
                    countFirstSteps++;
                    double tmp = countFirstStepDis - firstStepsDis;
                    LatLng first  = firstSteps.get(countFirstSteps);
                    LatLng second = firstSteps.get(countFirstSteps + 1);
                    nowBearing  = calBearing(first, second);
                    firstStepsDis = calDistance(first, second);
                    nowPosition = calPosition(first, nowBearing, tmp);
                    countFirstStepDis = tmp;
                }

                if(finalDis > 0){
                    countFirstStepDis = 0;
                    finalPosition = calPosition(finalPosition, finalBearing, dis);
                    nowPosition = finalPosition;
                    finalDis = finalDis - dis;
                }
                else if(finalDis!=0){
                    double tmp = Math.abs(finalDis);
                    count = 1;
                    LatLng first  = firstSteps.get(countFirstSteps);
                    LatLng second = firstSteps.get(countFirstSteps + 1);
                    nowBearing  = calBearing(first, second);
                    firstStepsDis = calDistance(first, second);
                    nowPosition = calPosition(first, nowBearing, tmp);
                    finalDis = 0;
                    //nowPosition =
                }
                double targetDis = calDistance(now_position, nowPosition);
                if(targetDis>30 && !Data.AutoPlay){
                    Toast("重新規劃路線");
                    allClean();
                    dataReady = false;
                    callApi   = true;
                }

                //double newRoute = calDistance(now_position, Routes.get(0));
                double newRoute = calDistance(nowPosition, Routes.get(count));
                if(newRoute < 50 && finalDis == 0){
                    finalDis = newRoute;
                    finalPosition = nowPosition;
                    finalBearing  = nowBearing;
                    callApi = true;
                    halfCall  = true;
                }
                show(nowPosition, nowBearing);
            }
        }

    }
    private void allClean(){
        //now_distance = 0;
        countDis = 0;
        countFirstStepDis = 0;
        countFirstSteps = 0;
    }
    public void show(LatLng point, float bearing){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                //畫面顯示
                changeCamera(point, bearing);                               //更改相機
                draw_direction(point, firstSteps, countFirstSteps + 1);
            }});
    }
    public void addMarker(LatLng point){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                GPS_Opt.position(point);
                GPS_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.rec_nav_gps));
                recGPS.add(my_map.Add_Marker(GPS_Opt));
            }
        });
    }
    public static Polyline Direction;
    public void draw_direction(LatLng point, ArrayList<LatLng> points, int step) {
        my_map.Remove_PolyLine(Direction);
        Direction = my_map.Draw_PolyLine(point, points, step);
    }
    public void changeCamera(LatLng point, float bearing){
        int ms = 70;
        my_map.moveCamera(Cal_Method.Cal_LatLng(point, bearing, 120), 19, bearing, 65, ms);
    }
    private float calBearing(LatLng p1, LatLng p2){
        return Cal_Method.Cal_Bearing(p1, p2);
    }
    private double calDistance(LatLng last, LatLng now){
        return Cal_Method.Cal_Distance(last, now);
    }
    private LatLng calPosition(LatLng last, float bearing, double dis){
        return Cal_Method.Cal_LatLng(last, bearing, dis);
    }
    private void Toast(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
