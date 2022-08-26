package com.example.navigation.My;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.navigation.MainActivity;
import com.example.navigation.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApiNotAvailableException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 獨立的Class  相機移動 路口判斷全在這
 */
public class My_New_Navigation {
    private My_Layout my_layout;
    private My_Map my_map;
    private Context context;
    private My_Direction my_direction;

    private boolean initData = true;
    private boolean initMK = true;

    //導航圖示
    private final MarkerOptions Navigation_MK_Opt = new MarkerOptions();
    private final MarkerOptions Predict_MK_Opt = new MarkerOptions();
    private Marker Navigation_MK;
    private Marker Predict_MK;

    private int Next_Step_Distance = 0;
    private ArrayList<LatLng> PolylineOverView = new ArrayList<LatLng>();
    private ArrayList<LatLng> Routes = new ArrayList<LatLng>();
    private ArrayList<String> Road = new ArrayList<>();
    private ArrayList<String> Road_Detail = new ArrayList<String>();
    private Polyline Direction;
    private Polyline First_Step;
    private LatLng Navigation_MK_Position;
    private float Now_Bearing = 0;

    boolean init_API = true;
    long API_last_time = 0;
    long API_now_time = 0;
    long API_pass_time = 0;
    LatLng API_last_position;
    LatLng API_now_position;
    LatLng API_cal_position;
    double move_distance = 0;
    double API_ms;

    private ArrayList<LatLng> straight_line_point = new ArrayList<LatLng>();
    private ArrayList<Marker> straight_marker = new ArrayList<Marker>();
    private MarkerOptions straight_Opt = new MarkerOptions();
    private boolean test_triggle = true;
    private int straight_now_step = 0;
    private int pass_step = 0;

    private LatLng navigation_now_position;

    private double init_Dis = 0;
    private int too_far = 0;
    private int redundant_dis = 0;
    private int count_step = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    public My_New_Navigation(Context cont, My_Layout layout, My_Map my_map_1) {
        context = cont;
        my_layout = layout;
        my_map = my_map_1;
        my_direction = new My_Direction();
    }

    public void Navigation(LatLng now_position, double dis) {
//        //Navigation_test1(now_position);
        //記得將dis修改回來，5米為測試用的
        //已將測試變數移至mainActivity
//        if(navigation_now_position==null) {
//            Navigation_test2(now_position, dis);
//        }
//        else{
//            Navigation_test2(navigation_now_position, dis);
//
//       }
        //if(initData || too_far > 3) {
        if(initData) {
            if(too_far!=0){
                Toast("重新規劃路線");
                too_far = 0;
            }
            initData = false;
            Call_API(now_position);
        }else{
            Navigation_test2(now_position, dis);
        }
//        try {
//            //Navigation_MK_Position = now_position;
//            //handler.postDelayed(NavigationCamera, 500);
//            //handler.postDelayed(ChangeUserMK, 500);
//            my_direction.searchDirection(now_position);
//            my_direction.SearchNavigationData(new My_Direction.onNavigationDataReadyCallBack() {
//                @Override
//                public void onDataReady(String text) {
//                    //Navigation_MK_Position = now_position;
//                    //取得資料
//                    Next_Step_Distance = My_Json.Get_Next_Step_Distance(text);
//                    Routes = My_Json.Get_Steps(text);
//                    PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine(text);
//                    Road = My_Json.Get_Navigation_Road(text);
//                    Road_Detail = My_Json.Get_Navigation_Road_Detail(text);
//                    //取得方向角與位置
//                    Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(0), PolylineOverView.get(1));
//                    //Navigation_MK_Position = PolylineOverView.get(0);
//                    //初始化API與計算
//                    if(init_API){
//                        init_API = false;
//                        API_last_time = System.currentTimeMillis();
//                        API_last_position = PolylineOverView.get(0);
//                    }
//                    else {
//                        //計算時間
//                        API_now_time = System.currentTimeMillis();
//                        API_pass_time = API_now_time - API_last_time;
//                        API_last_time = API_now_time;
//                        //計算距離
//                        API_now_position = PolylineOverView.get(0);
//                        move_distance = Cal_Method.Cal_Distance(API_last_position, API_now_position);
//                        API_last_position = API_now_position;
//
//                        //計算速度
//                        API_ms = (move_distance / API_pass_time) * 1000;
//                        my_layout.settvNavigationSpeed(Double.toString(API_ms));
//
//                        if (API_ms != 0) {
//                            //預測位置
//                            double dis = (API_ms / 1000 * API_pass_time);
//                            API_cal_position = Cal_Method.Cal_LatLng(API_now_position, Now_Bearing, dis);
//                            //Navigation_MK_Position = Cal_Method.Navigation_Drift(API_now_position, API_cal_position);
//                            //將預測的點打出來
//                            add_predict_marker(API_cal_position);
//                            //判斷觸發飄移
//                            if (Cal_Method.Navigation_Drift(API_now_position, API_cal_position)) {
//                                //觸發飄移，利用計算後的值顯示
//                                Navigation_MK_Position = API_cal_position;
//                                API_now_position = API_cal_position;
//                            } else {
//                                Navigation_MK_Position = API_now_position;
//                            }
//                            //移動相機、導航標記與繪圖
//                            NavigationCamera(Navigation_MK_Position, Now_Bearing);
//                            ChangeUserMK(Navigation_MK_Position, Now_Bearing);
//                            Draw_Direction(Navigation_MK_Position , PolylineOverView);
//                        }
//                    }
//                }
//            });
//        }
//        catch (Exception e){
//           Cal_Method.Catch_Error_Log("Navigation", e.toString());
//        }
    }
    public void Call_API(LatLng now_position){
        my_direction.searchDirection(now_position);
        my_direction.SearchNavigationData(new My_Direction.onNavigationDataReadyCallBack() {
            @Override
            public void onDataReady(String text) {
                //Navigation_MK_Position = now_position;
                //取得資料
                Next_Step_Distance = My_Json.Get_Next_Step_Distance(text);
                Routes = My_Json.Get_Steps(text);
                PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine(text);
                Road = My_Json.Get_Navigation_Road(text);
                Road_Detail = My_Json.Get_Navigation_Road_Detail(text);
                straight_line_point = Divide_Straight(PolylineOverView.get(0), PolylineOverView.get(1));
                Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(0), PolylineOverView.get(1));
                //Draw_Direction(PolylineOverView);
                init_Dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(1));
                //刷新參數
                navigation_now_position = now_position;     //將相機位置改掉
                straight_now_step = 0;                      //刷新現在步數
                count_step = 0;                             //刷新計算步數
                Navigation_test2(now_position, 0);

            }
        });
    }
    public void Navigation_test1(LatLng now_position) {
        try {
            //Navigation_MK_Position = now_position;
            //handler.postDelayed(NavigationCamera, 500);
            //handler.postDelayed(ChangeUserMK, 500);
            my_direction.searchDirection(now_position);
            my_direction.SearchNavigationData(new My_Direction.onNavigationDataReadyCallBack() {
                @Override
                public void onDataReady(String text) {
                    //Navigation_MK_Position = now_position;
                    //取得資料
                    Next_Step_Distance = My_Json.Get_Next_Step_Distance(text);
                    Routes = My_Json.Get_Steps(text);
                    PolylineOverView = My_Json.Get_Navigation_OverView_PolyLine(text);
                    Road = My_Json.Get_Navigation_Road(text);
                    Road_Detail = My_Json.Get_Navigation_Road_Detail(text);
                    //取得方向角與位置
                    Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(0), PolylineOverView.get(1));
                    //Navigation_MK_Position = PolylineOverView.get(0);
                    //初始化API與計算
                    if (init_API) {
                        init_API = false;
                        API_last_time = System.currentTimeMillis();
                        API_last_position = PolylineOverView.get(0);
                    } else {
                        //計算時間
                        API_now_time = System.currentTimeMillis();
                        API_pass_time = API_now_time - API_last_time;
                        API_last_time = API_now_time;
                        //計算距離
                        API_now_position = PolylineOverView.get(0);
                        move_distance = Cal_Method.Cal_Distance(API_last_position, API_now_position);
                        API_last_position = API_now_position;

                        //計算速度
                        API_ms = (move_distance / API_pass_time) * 1000;
                        my_layout.settvNavigationSpeed(Double.toString(API_ms));

                        if (API_ms != 0) {
                            //預測位置
                            double dis = (API_ms / 1000 * API_pass_time);
                            API_cal_position = Cal_Method.Cal_LatLng(API_now_position, Now_Bearing, dis);
                            //Navigation_MK_Position = Cal_Method.Navigation_Drift(API_now_position, API_cal_position);
                            //將預測的點打出來
                            add_predict_marker(API_cal_position);
                            //判斷觸發飄移
                            if (Cal_Method.Navigation_Drift(API_now_position, API_cal_position)) {
                                //觸發飄移，利用計算後的值顯示
                                Navigation_MK_Position = API_cal_position;
                                API_now_position = API_cal_position;
                            } else {
                                Navigation_MK_Position = API_now_position;
                            }
                            //移動相機、導航標記與繪圖
                            NavigationCamera(Navigation_MK_Position, Now_Bearing);
                            ChangeUserMK(Navigation_MK_Position, Now_Bearing);
                            Draw_Direction(Navigation_MK_Position, PolylineOverView);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Cal_Method.Catch_Error_Log("Navigation_test1", e.toString());
        }
    }

    public void Navigation_test2(LatLng now_position, double dis) {
        //用try catch會出事，原因未知
       //try {
//            if(initData || too_far > 3) {
//                if(too_far!=0){
//                    Toast("重新規劃路線");
//                    too_far = 0;
//                }
//                initData = false;
//                Call_API(now_position);
//            }
            if(straight_line_point!=null){
                if(straight_line_point.size()==0){
                    //Call_API(navigation_now_position);
                    straight_line_point = Divide_Straight(PolylineOverView.get(count_step), PolylineOverView.get(count_step+1));
                    Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(count_step), PolylineOverView.get(count_step+1));
                    navigation_now_position = straight_line_point.get(0);
                    //Draw_Direction(navigation_now_position, PolylineOverView, count_step+1);
                }
                if(redundant_dis!=0){
                    dis += redundant_dis;
                    redundant_dis = 0;
                }
                //將目前位置加上位移的距離
                straight_now_step += Math.round(dis);
                //判斷目前位置小於，此段的總數
                if(straight_now_step < straight_line_point.size()) {
                    //取得位移後的經緯度
                    navigation_now_position = straight_line_point.get(straight_now_step);
                    //顯示
                    //show(straight_line_point.get(straight_now_step));
                    show(navigation_now_position);
                }
                if(straight_now_step >= straight_line_point.size()){
                    //儲存多餘的距離(公尺)
                    redundant_dis = straight_now_step - straight_line_point.size();
                    //顯示最後一個點
                    navigation_now_position = straight_line_point.get(straight_line_point.size()-1);
                    //show(straight_line_point.get(straight_line_point.size()-1));
                    show(navigation_now_position);
                    //移動下一段距離
                    count_step+=1;
                    //刷新
                    //將目前顯示的步數移除
                    straight_now_step = 0;
                    //將切割後的線段移除
                    straight_line_point.removeAll(straight_line_point);
                }
                double now_dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(count_step+1));
                if(init_Dis < now_dis){
                    too_far++;
                }
            }else{
                Alert("抵達目的地");
            }

            System.out.println("count:" + count_step);
            System.out.println("size:" + PolylineOverView.size());
            if(Cal_Method.Cal_Distance(navigation_now_position, Data.Destination)<10 ||
               count_step >= PolylineOverView.size()-2 ){
                //Data.Navigation_Status = false;
                Alert("抵達目的地");
            }
//        }catch (Exception e) {
//            System.out.println("Navigation_test2");
//            System.out.println(e.toString());
//            Cal_Method.Catch_Error_Log("Navigation_test2", e.toString());
//        }
    }

    //畫面顯示
    public void show(LatLng point){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                //畫面顯示
                add_marker(point);                                  //新增點
                change_camera(point);                               //更改相機
                change_MK(point);                                   //更改標記
                //Draw_First_Step(point, PolylineOverView.get(1));  //畫圖
                Draw_Direction(navigation_now_position, PolylineOverView, count_step+1);
            }});
    }

    public void add_marker(LatLng point){
        straight_Opt.position(point);
        straight_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.history_point));
        straight_marker.add(my_map.Add_Marker(straight_Opt));
    }
    public void change_camera(LatLng point){
        my_map.moveCamera(Cal_Method.Cal_LatLng(point, Now_Bearing, 60), 20, Now_Bearing, 65);
    }
    public void change_MK(LatLng point){
        //my_map.moveCamera(point, 20, Now_Bearing, 65);
        Navigation_MK.setPosition(Cal_Method.Cal_LatLng(point, Cal_Method.reverse(Now_Bearing), 10));
    }
    public ArrayList<LatLng> Divide_Straight(LatLng start, LatLng end){
        ArrayList<LatLng> ans = new ArrayList<LatLng>();
        double dis = Cal_Method.Cal_Distance(start, end);
        int divide_num = (int) dis;
        double bearing = Cal_Method.Cal_Bearing(start, end);
        //System.out.println(divide_num);
        LatLng tmp = start;
        for(int i=0; i<divide_num; i++){
            LatLng cal = Cal_Method.Cal_LatLng(tmp, bearing, 1);
            ans.add(cal);
            tmp = cal;
        }
        //顯示計算的點
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            public void run() {
//                if (test_triggle) {
//                    test_triggle = false;
//                    for (int i = 0; i < ans.size(); i++) {
//                        straight_Opt.position(ans.get(i));
//                        straight_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.rec_gps));
//                        straight_Opt.title("GPS:" + Integer.toString(i));
//                        straight_marker.add(my_map.Add_Marker(straight_Opt));
//                    }
//                }
//            }
//        });
        return ans;
    }
    public void add_predict_marker(LatLng point) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (Predict_MK != null) {
                    Predict_MK.remove();
                }
                Predict_MK_Opt.position(point);
                Predict_MK_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.flag));
                Predict_MK = my_map.Add_Marker(Predict_MK_Opt);
            }
        });
    }
    public void Draw_First_Step(LatLng Start, LatLng End) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.Remove_PolyLine(First_Step);
                First_Step = my_map.Draw_PolyLine(Start, End);
            }
        });
    }
    public void Draw_Direction(ArrayList<LatLng> points) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.Remove_PolyLine(Direction);
                Direction = my_map.Draw_PolyLine(points);
            }
        });
    }
    public void Draw_Direction(LatLng point, ArrayList<LatLng> points) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.Remove_PolyLine(Direction);
                Direction = my_map.Draw_PolyLine(point, points);
            }
        });
    }
    public void Draw_Direction(LatLng point, ArrayList<LatLng> points, int step) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.Remove_PolyLine(Direction);
                Direction = my_map.Draw_PolyLine(point, points, step);
            }
        });
    }
    public void NavigationCamera(LatLng point, float bearing) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (Data.Navigation_Status) {
                    my_map.moveCamera(Cal_Method.Cal_LatLng(point, bearing, 450), 17, bearing, 65);
                }
            }
        });
    }

    public void initUserMK(LatLng point) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Navigation_MK_Opt.position(point);
                Navigation_MK_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.mk_user_arrow));
                //Navigation_MK = mMap.addMarker(Navigation_MK_Opt);
                Navigation_MK = my_map.Add_Marker(Navigation_MK_Opt);
            }
        });
    }

    public void ChangeUserMK(LatLng point, float bearing) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (Data.Navigation_Status) {
                    Navigation_MK.setPosition(Cal_Method.Cal_LatLng(point, Cal_Method.reverse(bearing), 50));
                }
            }
        });
    }


    public void set_Navigation_Text() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                int select = 0;
                //if(distance<100 && Data.Steps.size()<2){
                //    select = 1;
                //}
                my_layout.setNextRoadText(Data.Road.get(select));
                my_layout.setNextRoadDetailText(Data.Road_Detail.get(select));
                my_layout.Set_Turn_Pic(Data.Road_Detail.get(select));
                my_layout.setNowPosition(Data.now_position.toString());
                //my_layout.setNextRoadDistance(Integer.toString(distance));
                //my_layout.setDisToDestination(Double.toString(Cal_Distance(Data.now_position , Data.Destination)));

                //測試用顯示文字

                //System.out.println("distance: " + distance);
            }
        });
    }

    public void Toast(String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                ;
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void Remove_Navigation_MK() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (Navigation_MK != null) {
                    Navigation_MK.remove();
                }
            }
        });
    }

    public void Final_Remove_Direction() {
        handler.removeCallbacks(NavigationCamera);
        handler.removeCallbacks(ChangeUserMK);
        //System.out.println(Direction.getPoints().size());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.Remove_PolyLine(Direction);
            }
        });
    }
    public void Remove_First_Step(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.Remove_PolyLine(First_Step);
            }
        });
    }

    //取得值
    public boolean get_initMK() {
        return initMK;
    }

    //設定值
    public void set_initMK(boolean value) {
        initMK = value;
    }
    public void Remove_Straight_Marker(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                for(int i=0; i<straight_marker.size();i++){
                    my_map.Remove_Marker(straight_marker.get(i));
                }
                straight_marker.removeAll(straight_marker);
            }
        });
    }
    public void Alert(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text).setTitle("完成");
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dialogInterface.dismiss();
                my_map.moveCamera(Data.Destination);
            }
        });
        builder.create().show();
        //
    }
    //Runnable 測試
    private final Runnable NavigationCamera = new Runnable() {
        public void run() {
            if (Data.Navigation_Status) {
                System.out.println("FYBR");
                my_map.moveCamera(Cal_Method.Cal_LatLng(Navigation_MK_Position, Now_Bearing, 25), 17, Now_Bearing, 65);
            }
        }
    };
    private final Runnable ChangeUserMK = new Runnable() {
        public void run() {
            if (Data.Navigation_Status) {
                System.out.println("FYBR1");
                LatLng test = Cal_Method.Cal_LatLng(Navigation_MK_Position, Cal_Method.reverse(Now_Bearing), 30);
                Navigation_MK.setPosition(test);
            }
        }
    };


    private void settvNavigationSpeed(String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_layout.settvNavigationSpeed(text);
            }
        });
    }

}
