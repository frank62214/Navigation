package com.example.navigation.My;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
    private double redundant_dis = 0;
    private int count_step = 0;
    private double smooth_step = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    double now_speed = 0;

    double smooth_one_step_dis = 0;
    double smooth_one_step_count = 0;

    public My_New_Navigation(Context cont, My_Layout layout, My_Map my_map_1) {
        context = cont;
        my_layout = layout;
        my_map = my_map_1;
        my_direction = new My_Direction();
    }

    public void Navigation(LatLng now_position, double dis, long pass_time) {
        if(initData) {
            //Call_API(now_position);
        }else{
            Navigation_test2(now_position, dis, pass_time);
        }
    }
    public void Call_API(LatLng now_position){
        try {
            initData = true;
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
                    smooth_one_step_dis = 0;
                    smooth_one_step_count = 0;
                    now_speed = 0;
                    navigation_now_position = null;     //將相機位置改掉
                    straight_now_step = 0;                      //刷新現在步數
                    count_step = 0;                             //刷新計算步數
                    Navigation_test2(now_position, 0, 1000);
                    initData = false;
                    //show(PolylineOverView.get(0), 1000);
                    //Navigation_test3(now_position, 0, true);

                }
            });
        }catch (Exception e){
            Cal_Method.Catch_Error_Log("Call_API", e.toString());
        }
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
                            //NavigationCamera(Navigation_MK_Position, Now_Bearing);
                            ChangeUserMK(Navigation_MK_Position, Now_Bearing);
                            //Draw_Direction(Navigation_MK_Position, PolylineOverView);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Cal_Method.Catch_Error_Log("Navigation_test1", e.toString());
        }
    }
    //開始
    //smooth_one_step_dis = 0
    public void Navigation_test2(LatLng now_position, double dis, long pass_time) {



        //try{
            //dis就是速度
            //將速度存起來，每秒更新速度的時候(dis為速度)，將執行續不斷顯示的最終距離存起來
            if(redundant_dis != 0){
                dis += redundant_dis;
                redundant_dis = 0;
            }
//            if(now_speed==0 || dis !=0){
//                now_speed = dis;
//            }
        if(dis!=0){
//            if(now_speed!=0){
//                double tmp = now_speed - smooth_step;
//                //修改導航顯示位置
//                navigation_now_position = Cal_Method.Cal_LatLng(navigation_now_position, Now_Bearing, tmp);
//            }
            now_speed = dis;
            smooth_step = 0;
        }

            //判斷每段PolyLine的距離，利用刷新等於0，計算是否要進到下一段顯示
        if (smooth_one_step_dis == 0) {
            //取得每段判斷用的距離
            smooth_one_step_dis = Cal_Method.Cal_Distance(PolylineOverView.get(count_step), PolylineOverView.get(count_step + 1));
            //取得每段顯示用的方向角
            Now_Bearing = Cal_Method.Cal_Bearing(PolylineOverView.get(count_step), PolylineOverView.get(count_step + 1));
            //將每段判斷用的距離存入，偏航用的
            init_Dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(count_step + 1));
        }
        //判斷距離是否大於計算平滑一段步數
        if (smooth_one_step_count < smooth_one_step_dis) {
            //判斷每次取得速度，將中間距離滑順處理
            if (smooth_step <= now_speed) {
                //如果平滑步數小於秒速，便繼續進行平滑步速的處理
                if (navigation_now_position == null) {
                    //如果沒有導航顯示位置，取得導航顯示位置(基本在於每段的第0個點會取得)
                    navigation_now_position = PolylineOverView.get(count_step);
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {my_layout.setdataviewCalPosition(Double.toString(smooth_step));}});
                    //平滑處理預計顯示10次，所以每0.1秒顯示一次
                    //System.out.println(now_speed);
                    double tmp = now_speed / 10.0;
                    //修改導航顯示位置
                    navigation_now_position = Cal_Method.Cal_LatLng(navigation_now_position, Now_Bearing, tmp);
                    //增加平滑步數
                    smooth_step += tmp;
                    //增加一段平滑步數
                    smooth_one_step_count += tmp;
                }
                //顯示所有東西(導航圖示、相機移動、畫線、歷史路徑)
                show(navigation_now_position, (int)pass_time);
                //------------------------------------------------------------------------
                //計算偏行
                double real_dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(count_step + 1));
                double cal_dis  = Cal_Method.Cal_Distance(navigation_now_position, PolylineOverView.get(count_step + 1));
                if(Math.abs(real_dis - cal_dis) > 10){
                    if(!Data.AutoPlay) {
                        initData = true;
                        Remove_Straight_Marker();
                    }
                    Toast("重新規劃路線");
                }
                //------------------------------------------------------------------------
            } else {
                //如果平滑步數大於秒速，代表一小段平滑結束，刷新參數
                //刷新平滑步數
                smooth_step = 0;
                //刷新秒速，為了取得下次的秒速
                now_speed = 0;
            }
        } else {
//               //抵達目的地
//               double arrive = Cal_Method.Cal_Distance(navigation_now_position, PolylineOverView.get(PolylineOverView.size()-1));
//               if(arrive<10){
//                   Data.Navigation_Status = false;
//                   my_layout.Direction_Page(my_map);
//               }
            //將多餘的步數儲存起來
            redundant_dis = smooth_one_step_count - smooth_one_step_dis;
            //如果平滑部署 等於 每段的距離，顯示最後一個點
            if (smooth_one_step_count >= smooth_one_step_dis) {
                navigation_now_position = PolylineOverView.get(count_step + 1);
                show(navigation_now_position, (int) pass_time);
            }
            //如果平滑步數 大於 每段的距離，便進行下一段
            //判斷下一對的參數++
            count_step++;
            //將每段距離刷新
            smooth_one_step_dis = 0;
            //將導航顯示位置刷新
            navigation_now_position = null;
            //將平滑一段步數刷新
            smooth_one_step_count = 0;
        }
        //抵達目的地
        if((count_step+1) == PolylineOverView.size() && !initData){
            Data.Navigation_Status = false;
            initData = true;
            handler.postDelayed(End_Navigation, 200);
        }

        //取得目前與第一段的尾巴做距離判斷，
//           double now_dis = Cal_Method.Cal_Distance(now_position, PolylineOverView.get(count_step + 1));
//           //如果現在距離小於初始化的距離，刷新初始化距離
//           if (now_dis <= init_Dis) {
//               init_Dis = now_dis;
//           }
//           else{
//                //否則記一次偏移，累計三次重新呼叫API
//                too_far++;//            }

        //}catch (Exception e){
        //    System.out.println(e.toString());
        //    Cal_Method.Catch_Error_Log("Navigation_test2", e.toString());
        //}
    }



    //畫面顯示
    public void show(LatLng point, int Ms){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                //畫面顯示
                add_marker(point);                                  //新增點
                change_camera(point, Ms);                               //更改相機
                //change_MK(point);                                   //更改標記
                //Draw_First_Step(point, PolylineOverView.get(1));  //畫圖
                Draw_Direction(navigation_now_position, PolylineOverView, count_step+1);
            }});
    }
    public void add_marker(LatLng point){
        try {
            straight_Opt.position(point);
            straight_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.history_point));
            straight_marker.add(my_map.Add_Marker(straight_Opt));
        }
        catch (Exception e){
            Cal_Method.Catch_Error_Log("add_marker", e.toString());
        }
    }
    public void change_camera(LatLng point, int Ms){
        try {
            my_map.moveCamera(Cal_Method.Cal_LatLng(point, Now_Bearing, 60), 20, Now_Bearing, 65, Ms);
        }
        catch (Exception e){
            Cal_Method.Catch_Error_Log("change_camera", e.toString());
        }
    }
    public void change_MK(LatLng point){
        //my_map.moveCamera(point, 20, Now_Bearing, 65);
        try {
            Navigation_MK.setPosition(Cal_Method.Cal_LatLng(point, Cal_Method.reverse(Now_Bearing), 10));
        }
        catch (Exception e){
            Cal_Method.Catch_Error_Log("change_MK", e.toString());
        }
    }
    public ArrayList<LatLng> Divide_Straight(LatLng start, LatLng end){
        ArrayList<LatLng> ans = new ArrayList<LatLng>();
        double dis = Cal_Method.Cal_Distance(start, end);
        int divide_num = (int) dis;
        double bearing = Cal_Method.Cal_Bearing(start, end);
        //System.out.println(divide_num);
        LatLng tmp = start;
        for(int i=0; i<divide_num; i++){
            LatLng cal = Cal_Method.Cal_LatLng(tmp, bearing, 1);//m
            //LatLng cal = Cal_Method.Cal_LatLng(tmp, bearing, 0.02);//m
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
    public LatLng Next_Point(LatLng start, LatLng end, double dis) {
        LatLng ans = new LatLng(0,0);
        double bearing = Cal_Method.Cal_Bearing(start, end);
        dis = dis / 10.0;
        ans = Cal_Method.Cal_LatLng(start, bearing, dis);
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
    public void Draw_Direction(LatLng point, ArrayList<LatLng> points, int step) {
        try {
            my_map.Remove_PolyLine(Direction);
            Direction = my_map.Draw_PolyLine(point, points, step);
        }
        catch (Exception e){
            Cal_Method.Catch_Error_Log("Draw_Direction", e.toString());
        }
    }
//    public void NavigationCamera(LatLng point, float bearing) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            public void run() {
//                if (Data.Navigation_Status) {
//                    my_map.moveCamera(Cal_Method.Cal_LatLng(point, bearing, 450), 17, bearing, 65);
//                }
//            }
//        });
//    }

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
        //handler.removeCallbacks(NavigationCamera);
        //handler.removeCallbacks(ChangeUserMK);
        //System.out.println(Direction.getPoints().size());
        handler.removeCallbacks(Smooth);
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
//    private final Runnable NavigationCamera = new Runnable() {
//        public void run() {
//            if (Data.Navigation_Status) {
//                System.out.println("FYBR");
//                my_map.moveCamera(Cal_Method.Cal_LatLng(Navigation_MK_Position, Now_Bearing, 25), 17, Now_Bearing, 65);
//            }
//        }
//    };
    private final Runnable Smooth = new Runnable() {
        public void run() {

        }
    };
    private final Runnable End_Navigation = new Runnable() {
        public void run() {
           my_layout.Direction_Page(my_map);
           handler.removeCallbacks(this);
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
