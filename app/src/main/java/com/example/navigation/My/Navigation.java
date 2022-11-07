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
    public LatLng now_direction;
    public double now_distance = 0;
    public long passtime;
    public float now_bearing = 0;
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

    ArrayList<String> Turn = new ArrayList<>();
    ArrayList<String> Road = new ArrayList<String>();
    ArrayList<String> Road_Detail = new ArrayList<String>();
    ArrayList<LatLng> straight_line_point = new ArrayList<LatLng>();

    ArrayList<ArrayList<LatLng>> PolyLineSteps = new ArrayList<>();

    private float Now_Bearing = 0;
    private float Last_Bearing = 0;
    double Now_Step_Dis = 0;

    LatLng Navigation_Now_Position = null;
    //private MarkerOptions straight_Opt = new MarkerOptions();
    //private ArrayList<Marker> straight_marker = new ArrayList<Marker>();
    public static Polyline Direction;

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

    private final MarkerOptions Navigation_MK_Opt = new MarkerOptions();
    private Marker Navigation_MK;

    public LatLng last_GPS_position;
    private double Calculate_dis = 0;
    public double acc_dis = 0;
    private int too_far = 0;
    private double too_far_dis = 0;
    double coefficient = 1;
    private double last_position_diff = 0;
    private double position_diff = 0;
    private double bearing_diff = 0;
    private boolean initacc = true;

    boolean far_away_once = true;
    int road_count = 0;
    int turn_count = 0;
    double last_dis = 0;
    String road = "繼續直行";
    String turn = "繼續直行";
    String detail = "";

    MarkerOptions car_op = new MarkerOptions();
    public static Marker car;
    MarkerOptions gps_op = new MarkerOptions();
    public static ArrayList<Marker> gps = new ArrayList<>();
    MarkerOptions api_op = new MarkerOptions();
    public static ArrayList<Marker> api = new ArrayList<Marker>();

    public int car_mode_count = 0;
    public boolean car_mode_next_step = false;
    //queuing
    ArrayList<LatLng> New_Routes = new ArrayList<>();
    ArrayList<ArrayList<LatLng>> New_PolyLineSteps = new ArrayList<>();
    ArrayList<LatLng> New_PolylineOverView = new ArrayList<>();
    ArrayList<String> New_Turn = new ArrayList<>();
    ArrayList<String> New_Road = new ArrayList<>();
    ArrayList<String> New_Road_Detail = new ArrayList<>();

    public Navigation(Context cont, My_Layout layout, My_Map map) {
        context = cont;
        my_layout = layout;
        my_map = map;
    }

    public void Call_API() {
        try {
//            if(too_far > 10){
//                initData = true;
//            }
            //每次執行
            if(Data.Navigation_Status || Data.CarMode_Status) {
                //System.out.println("initData = " + initData);
                //System.out.println("now_position = " + now_position);
                //System.out.println("now_distance = " + now_distance);
                if (initData && now_position != null && now_distance != 0) {
//                    initData = false;
                    //now_distance = 0;
                    if(Data.Navigation_Status){
                        my_direction.searchDirection(now_position);
                    }else{
                        //Data.CarMode
                        //now_direction = Cal_Method.Cal_LatLng(now_position, Now_Bearing, now_distance * 2);
                        //now_direction = Cal_Method.Cal_LatLng(now_position, Now_Bearing, 500);
                        now_direction = Cal_Method.Cal_LatLng(now_position, now_bearing, 500);
                        add_marker(now_direction);
                        my_direction.setDistanceUrl(now_position, now_direction);
                    }
                    my_direction.SearchNavigationData(new My_Direction.onNavigationDataReadyCallBack() {
                        @Override
                        public void onDataReady(String text) {
                            //取得資料
                            Routes = My_Json.Get_Steps(text);
                            PolyLineSteps = My_Json.Get_Navigation_PolyLine_Step(text);
                            PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine(text);

                            Turn = My_Json.Get_Navigation_Turn(text);
                            Road = My_Json.Get_Navigation_Road(text);
                            Road_Detail = My_Json.Get_Navigation_Road_Detail(text);

//                        System.out.println("Routes = " + Routes.size());
//                        System.out.println("Road = " + Road.size());
//                        System.out.println("Road_Detail = " + Road_Detail.size());
                            //---------------------------------------------------------------------------------------
                            Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(0), PolylineOverView.get(1));
                            //Now_Bearing = Cal_Method.Cal_Bearing(Data.now_position, PolylineOverView.get(0));
                            //---------------------------------------------------------------------------------------
                            Last_Bearing = Now_Bearing;
                            Now_Step_Dis = Cal_Method.Cal_Distance(PolylineOverView.get(0), PolylineOverView.get(1));
                            //---------------------------------------------------------------------------------------
                            //Draw_Direction(PolylineOverView);
                            //init_Dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(1));
                            //刷新參數
//                    smooth_one_step_dis = 0;
//                    smooth_one_step_count = 0;
//                    now_speed = 0;
                            Navigation_Now_Position = PolylineOverView.get(0);     //將相機位置改掉
                            show(Navigation_Now_Position);
                            //showRoad();
//                    straight_now_step = 0;                      //刷新現在步數
//                    count_step = 0;                             //刷新計算步數
//                    Navigation_test2(now_position, 0, 1000);
                            //------------------------------------
                            //initUserMK(Navigation_Now_Position);
                            last_position_diff = Cal_Method.Cal_Distance(Data.now_position, Navigation_Now_Position);
                            //------------------------------------
                            count = 0;
                            count_dis = 0;
                            count_step_dis = 0;
                            polyline_dis = Now_Step_Dis;
                            //total_distance = 0;

                            too_far = 0;
                            initacc = true;
                            too_far_dis = 0;

                            far_away_once = true;
                            road_count = 0;
                            turn_count = 0;
                            last_dis = 0;


                            road = Road.get(0);
                            detail = Road_Detail.get(0);
                            turn = Turn.get(0);


                            initData = false;
                            //show(PolylineOverView.get(0), 1000);
                            //Navigation_test3(now_position, 0, true);
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.out.println(e);
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
            if (now_distance != 0 ) {
                if(compensate_dis!=0){
                    now_distance += compensate_dis;
                    compensate_dis = 0;
                }
                //----------------------------------------------
                //第二種計算方法(利用固定係數)
                //if(Data.SnapRoad_Status) {
                //position_diff = Cal_Method.Cal_Distance(Data.now_position, Navigation_Now_Position);
                //double diff = last_position_diff - position_diff;
                bearing_diff = getBearing();
                //夾角大於90度(現在位置在圖標得後方)，所以加速
                if (bearing_diff > 90){
                    coefficient = 1.1;
                }
                //夾角小於90度(現在位置在圖標得前方)，所以減速
                else if(bearing_diff < 90){
                    coefficient = 0.9;
                }
                //last_position_diff = position_diff;
                //}
                //第三種計算方法(利用加速度技)
                if(initacc && Data.AccStatus){
                    initacc = false;
                    coefficient = 1;
                    now_distance = now_distance + acc_dis;
                }
                //將移動的距離除10
                //double dis = now_distance / 10.0;
                double dis = now_distance * coefficient / 10.0;
                //----------------------------------------------
                //double dis = now_distance / 100.0;
                //宣告比例補償變數
                //double cal = Calculate_dis / 10.0;
                //判斷 切分的速度 是否小於等於 現在的速度
                if(count_dis <= now_distance){
                    //判斷 計算每段的距離 是否小於 PolyLine的一段距離
                    if(count_step_dis < Now_Step_Dis) {
                        //計算這次要移動的距離，tmp是切分的補償距離，dis是切分的GPS移動距離
                        double tmp = dis;
                        //double tmp = dis + cal;
                        //Navigation_Now_Position = Cal_Method.Cal_LatLng(Navigation_Now_Position, Now_Bearing, dis);
                        //經緯度用疊加的方式，新位置=舊位置+方向角+距離
                        Navigation_Now_Position = Cal_Method.Cal_LatLng(Navigation_Now_Position, Now_Bearing, tmp);
                        //計算並累加切分的速度
                        count_dis      = Math.round((count_dis + dis) * 10) / 10.0;
                        //計算並累加每段的距離(要轉向與判定導航，要將補償的距離加進去)
                        count_step_dis = Math.round((count_step_dis + tmp) * 10) / 10.0;
                        //計算並累加總長度(GPS在Nav中累計的距離)
                        total_dis      = Math.round((total_dis + dis) * 10) / 10.0;
                        //---------------------------------------------
                        car_mode_count += 1;
                        //---------------------------------------------
                    }
                    //System.out.println("count_step_dis=" + count_step_dis);
                    //System.out.println("Now_Step_Dis=" + Now_Step_Dis);

                    //判斷 每段距離 是否大於等於 PolyLine的一段距離
                    if(count_step_dis >= Now_Step_Dis){
                        //PolyLine的計數器跳下一段(e.g 0 -> 1  或是 1 -> 2)
                        count = count + 1;
                        //判斷PolyLine的計數器是否與此段路程一樣的長度，代表導航結束
                        //System.out.println("PolylineOverView.size()=" + PolylineOverView.size());
                        //System.out.println("count" + count);
                        if (count == PolylineOverView.size() - 1 ) {
                            //結束導航，還有Bug
                            if(Data.Navigation_Status) {
                                initData = true;
                                handler.postDelayed(remove, 5);
                            }
                            //---------------------------------------------

                            //---------------------------------------------
//                            else if(Data.CarMode_Status && !Data.AutoPlay){
//                                initData = true;
//                            }
//                            else if (Data.CarMode_Status) {
//                                initData = true;
//                                //now_position = now_direction;
//                            }
                            turn_count = 0;
                        }
                        else {
                            //--------------------
                            //未知的問題:當切斷的補償距離還長於下一段時會出現甚麼問題
                            //--------------------
                            //如果沒有沒有結束導航，就進行下一段
                            //由於每段的距離不一定能個整數結束，先前有先判斷 每段距離 已經大於 上一段的PolyLine的距離
                            //當已經大於的時候計算 多餘的部分 要直接顯示
                            //所以儲存多餘的距離(1. 將現在的經緯度與上一段的最後一點取距離  或是  2. 利用每段距離 減上一段PolyLine的距離)
                            //目前是使用2
                            //redundant_dis = Cal_Method.Cal_Distance(Navigation_Now_Position, PolylineOverView.get(count));
                            if(count < PolylineOverView.size()) {
                                redundant_dis = count_step_dis - Now_Step_Dis;
                                //計算下一段的方向角
                                Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(count), PolylineOverView.get(count + 1));
                                //計算下一段的PolyLine的距離
                                Now_Step_Dis = Cal_Method.Cal_Distance(PolylineOverView.get(count), PolylineOverView.get(count + 1));
                                //將位置更新成新一段的線上，所以再次計算經緯度，將上一段的最後一個點當作最後的位置
                                //並將新的方向角 與 多餘的距離放進去進算，更新位置
                                Navigation_Now_Position = Cal_Method.Cal_LatLng(PolylineOverView.get(count), Now_Bearing, redundant_dis);
                                //此時將PolyLine的距離儲存起來可以用來驗證PolyLine是不是真的比真實路線長
                                polyline_dis = polyline_dis + Now_Step_Dis;
                                //---------------------------------------------

                                //---------------------------------------------
                            }
                            //------------------------------------
//                            if(getTheta(Last_Bearing, Now_Bearing) < 80){
//                                System.out.println(getTheta(Last_Bearing, Now_Bearing));
//                            }
                            //此時將目前位置丟給Google SnapRoad的API 中，看目前位置 與 導航的位置 有沒有誤差
                            //Get_Compensate(Data.SnapRoad_Status);
                            //------------------------------------
                        }
                        //如果大於等於  就代表要進行下一段PolyLine
                        //將每段距離歸零
                        count_step_dis = 0;
                    }

                }
                //統一顯示我想要證明的數據
                showText();
                //顯示導航的相機移動或畫線
                show(Navigation_Now_Position);
                //顯示導航的下一個路徑
                showRoad();
                //車載模式
                //------------------------------------------
                if(car_mode_count==6) {
                    if (Data.CarMode_Status && PolylineOverView.size() != 1) {
                        car_mode_next_step = false;
                        Helf_Call_API(false);
                    }
                }
                //------------------------------------------
                //判斷 切分的速度 是否大於等於 現在的速度
                //由於上面速度除以10，故第10次會進來這裡
                if(count_dis >= now_distance){
                    if(Data.CarMode_Status && car_mode_next_step){
                        Routes = New_Routes;
                        PolyLineSteps = New_PolyLineSteps;
                        PolylineOverView = New_PolylineOverView;
                        Turn = New_Turn;
                        Road = New_Road;
                        Road_Detail = New_Road_Detail;
                        count = 0;
                        road_count = 0;
                        count_step_dis = 0;
                        //Navigation_Now_Position = PolylineOverView.get(0);
                        if(!Data.AutoPlay) {
                            compensate_dis = Cal_Method.Cal_Distance(Navigation_Now_Position, PolylineOverView.get(0));
                        }
                        Now_Step_Dis = Cal_Method.Cal_Distance(Navigation_Now_Position, PolylineOverView.get(1));
                        Now_Bearing = Cal_Method.Cal_Bearing(Navigation_Now_Position, PolylineOverView.get(1));
                        //dataClear();
                        car_mode_next_step = false;
                    }
//                    if(Data.CarMode_Status){
//                        if(Data.AutoPlay){
//                            now_position = Navigation_Now_Position;
//                        }
//                        initData = true;
//                    }
                    //如果切分速度大於等於的話，代表此段移動已結束，因為執行續是一直在執行
                    //所以要將參數歸零，才能讓執行續不要進來這裡
                    t = 0 ;
                    //速度歸零
                    now_distance = 0;
                    //切分的速度歸零，下次才能再使用
                    count_dis = 0;
                    //計算距離歸零
                    Calculate_dis = 0;
                    //車載計數歸零
                    car_mode_count = 0;
                    initacc = false;
                    if(Data.CarMode_Status){
                        initData = true;
                    }
                }
//                if((getTheta()>120 || too_far_dis < position_diff) &&
//                        !Data.AutoPlay && !Data.CarMode_Status){
//                    too_far_dis = position_diff;
//                    too_far++;
//                    Toast("重新規劃路線");
//                }
//                if(too_far>10 && !Data.AutoPlay && !Data.CarMode_Status){
//                    initData = true;
//                }
                //show(Navigation_Now_Position);
                //----------------------------------------------
            }
        }
    }
    public void Helf_Call_API(boolean bear) {
        try {
            double bearing;
            if(bear){
                //bearing = Now_Bearing + 90;
                bearing = now_bearing + 90;
                if(bearing > 360){
                    bearing = bearing - 360;
                }
            }
            else{
                //bearing = Now_Bearing;
                bearing = now_bearing;
            }
            //Data.CarMode
            //now_direction = Cal_Method.Cal_LatLng(Navigation_Now_Position, Now_Bearing, now_distance * 2);
            //now_direction = Cal_Method.Cal_LatLng(Navigation_Now_Position, Now_Bearing, 50);
            //now_direction = Cal_Method.Cal_LatLng(Navigation_Now_Position, bearing, 50);
            now_direction = Cal_Method.Cal_LatLng(now_position, bearing, 500);
            add_marker(now_direction);
            //my_direction.setDistanceUrl(Navigation_Now_Position, now_direction);
            my_direction.setDistanceUrl(now_position, now_direction);
            my_direction.SearchNavigationData(new My_Direction.onNavigationDataReadyCallBack() {
                @Override
                public void onDataReady(String text) {
                    //取得資料
                    New_Routes = My_Json.Get_Steps(text);
                    New_PolyLineSteps = My_Json.Get_Navigation_PolyLine_Step(text);
                    New_PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine(text);
                    New_Turn = My_Json.Get_Navigation_Turn(text);
                    New_Road = My_Json.Get_Navigation_Road(text);
                    New_Road_Detail = My_Json.Get_Navigation_Road_Detail(text);

                    if(New_Routes.size()==0 || New_Road.size()==0 || New_PolylineOverView.size()==0){
                        Helf_Call_API(true);
                    }
                    else {
                        car_mode_next_step = true;
                    }
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
            Cal_Method.Catch_Error_Log("Helf_Call_API", e.toString());
        }

    }
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
    private double getTheta(float bearing1, float bearing2){
        double T=0;
        if(bearing1 > bearing2){
            T = bearing1 - bearing2;
        }else{
            T = bearing2 - bearing1;
        }
        if(T > 180){
            T = 360 - 180;
        }
        return T;
    }
    private double getBearing(){
        double ans = 0;
        //now_bearing
        double real_bearing = Cal_Method.Cal_Bearing(now_position, Navigation_Now_Position);
        if(now_bearing > real_bearing){
            ans = now_bearing - real_bearing;
        }
        else{
            ans = real_bearing - now_bearing;
        }
        if(ans > 180){
            ans = 360 - 180;
        }
        return  ans;
    }
    public void showText(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_layout.setdataviewNavigationCountDis(total_dis + "m");
                my_layout.setdataviewNavigationNowDistance(now_distance + "m");
                my_layout.setdataviewGPSSimulationS(passtime + "ms");
                my_layout.setdataviewPolyLineDis(polyline_dis + "m");
                my_layout.setdataviewTotalCompensateDis(total_compensate_dis + "m");
                my_layout.setdataviewNavigationCalDistance(Calculate_dis + "m");
                my_layout.setdataviewSeneorCurrentdis(acc_dis + "m/s");
                my_layout.setdataviewCoefficient(coefficient + "");
                my_layout.setdataviewgetBearing(bearing_diff + "度");
            }});
    }
    public void show(LatLng point){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                //畫面顯示
                change_MK(point);
                add_history_marker(point);                          //新增點
                change_camera(point);                               //更改相機
                //if(Data.Navigation_Status) {
                    draw_direction(point, PolylineOverView, count + 1);
                //}
            }});
    }
    public void add_history_marker(LatLng point){
        Data.Nav_Record.add(point);
        if(Data.Nav_History) {
            Data.Nav_Opt.position(point);
            Data.Nav_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.history_point));
            Data.Nav_marker.add(my_map.Add_Marker(Data.Nav_Opt));
        }
    }
    public void add_marker(LatLng point){
        if(Navigation_Now_Position!=null) {
            if (car == null) {
                car_op.position(point);
                car_op.icon(Cal_Method.BitmapFromVector(context, R.drawable.rec_cal));
                car = my_map.Add_Marker(car_op);
                gps_op.position(now_position);
                gps_op.icon(Cal_Method.BitmapFromVector(context, R.drawable.rec_gps));
                gps.add(my_map.Add_Marker(gps_op));
                api_op.position(Navigation_Now_Position);
                api_op.icon(Cal_Method.BitmapFromVector(context, R.drawable.rec_api));
                api.add(my_map.Add_Marker(api_op));
            }
            car.setPosition(point);
            gps_op.position(now_position);
            gps.add(my_map.Add_Marker(gps_op));
        }
        //api_op.position(Navigation_Now_Position);
        //api.add(my_map.Add_Marker(api_op));
        //rec_gps.add(my_map.Add_Marker(GPS_Opt));
    }

    boolean close_once = true;

    int detail_count = 0;
    boolean next_turn = true;
    int next_turn_count = 0;
    private void showRoad(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                try{
                My_Json.show_detail(Road, Turn, Road_Detail);
//                System.out.println("Routes=" + Routes.size());
//                System.out.println("Road=" + Road.size());
//                System.out.println("Turn=" + Turn.size());
//                System.out.println("Road_Detail=" + Road_Detail.size());
                if(!initData){
                    String s = Routes.get(road_count) + " " + Road.get(road_count)+ " " + Turn.get(road_count) + " " + Road_Detail.get(road_count);
                    String size = Routes.size() + " " + Road.size()+ " " + Turn.size() + " " + Road_Detail.size();
                    System.out.println("content = " + s);
                    System.out.println("size = " + size);
                    if(road_count < Routes.size() && turn_count<Turn.size()){
                        double d = Cal_Method.Cal_Distance(Navigation_Now_Position, Routes.get(road_count));
                        my_layout.setNextRoadDistance(d + "公尺");
                        if (!Data.CarMode_Status) {
                            System.out.println("road_count = " + road_count);
                            System.out.println("turn_count = " + turn_count);
                            System.out.println("d = " + d);
                            System.out.println("last_d = " + last_dis);
                            if(last_dis != 0 ) {

                                if (last_dis < d) {
                                    System.out.println("遠離");
                                    if (d > 10 && far_away_once) {
                                        System.out.println("遠離一次");
                                        far_away_once = false;
                                        close_once = true;
                                        road_count++;

                                        road = Road.get(road_count);
                                        detail = "繼續直行";
                                        turn = "繼續直行";
                                    }
                                } else {
                                    System.out.println("接近");
                                    far_away_once = true;
                                    if (d < 100 && close_once) {
                                        System.out.println("接近一次");
                                        close_once = false;
                                        turn_count++;
                                        road = Road.get(road_count);
                                        detail = Road_Detail.get(turn_count);
                                        turn = Turn.get(turn_count);
                                    }
                                }
                            }
                            last_dis = d;
                        }
                        else{
                            road = Road.get(0);
                            turn = Turn.get(0);
                            detail = Road_Detail.get(0);
                        }
                    }else{
                        road = Road.get(Road.size()-1);
                        detail = Road_Detail.get(Road_Detail.size()-1);
                        turn = Road_Detail.get(Road_Detail.size()-1);
                    }
                    System.out.print("road = " + road);
                    System.out.print("   turn = " + turn);
                    System.out.println("   detail = " +detail);
                    my_layout.Set_Turn_Pic(turn);
                    my_layout.setNextRoadText(road);
                    my_layout.setNextRoadDetailText(detail);
                }
                System.out.println("---------------------------------");
                }catch(Exception e){
                    System.out.println(e);
                    Cal_Method.Catch_Error_Log("Helf_Call_API", e.toString());
                }
            }

        });
    }
    private String getDirectionTurn(String text){
        String ans = "目的地";
        if(text.contains("目的地在右邊")){ ans = "目的地在右邊"; }
        if(text.contains("目的地在左邊")){ ans = "目的地在左邊"; }
        return ans;
    }

    private String getTurn(String text){
        String s = "";
        if(text.contains("前進")){ s = "繼續直行";}
        if(text.contains("繼續直行")){ s = "繼續直行";}
        if(text.contains("右")){ s = "向右轉";}
        if(text.contains("左")){ s = "向左轉";}
        return s;
    }
    public void change_camera(LatLng point){
        int ms = 70;
        //int ms = 10;
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
    public void initUserMK(LatLng point) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if(Navigation_MK==null) {
                    Navigation_MK_Opt.position(point);
                    Navigation_MK_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.mk_user_arrow));
                    //Navigation_MK = mMap.addMarker(Navigation_MK_Opt);
                    Navigation_MK = my_map.Add_Marker(Navigation_MK_Opt);
                }
            }
        });
    }
    public void change_MK(LatLng point){
        if(Navigation_MK!=null) {
            Navigation_MK.setPosition(Cal_Method.Cal_LatLng(point, Cal_Method.reverse(Now_Bearing), 10));
        }
    }
    public void remove_MK(){
        if(Navigation_MK!=null){
            Navigation_MK.remove();
        }
    }
    private void dataClear(){
        New_Routes.clear();
        New_PolyLineSteps.clear();
        New_PolylineOverView.clear();
        New_Turn.clear();
        New_Road.clear();
        New_Road_Detail.clear();
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
            remove_MK();
            my_map.Remove_Direction(Direction);
            Direction.remove();
            //car.remove();
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
