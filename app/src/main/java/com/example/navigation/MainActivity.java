package com.example.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.navigation.My.Cal_Method;
import com.example.navigation.My.Data;
import com.example.navigation.My.My_Event;
import com.example.navigation.My.My_Layout;
import com.example.navigation.My.My_Map;
import com.example.navigation.My.My_New_Navigation;
import com.example.navigation.My.My_Sensor;
import com.example.navigation.My.My_Snap_Road;
import com.example.navigation.My.Navigation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SensorEventListener{

    // 定義這個權限要求的編號
    private final int REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION = 100;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationManager mLocationMgr;
    private GoogleMap mMap;

    private CameraPosition cameraPosition;
    //private LatLng now_position = new LatLng(0,0);

    private My_Layout my_layout;
    private My_Map my_map;
    private My_Event my_event;
    private My_New_Navigation my_new_navigation;
    private Navigation navigation;
    private My_Sensor my_sensor;

    private SensorManager sm;
    Sensor magneticSensor;
    Sensor accelerometerSensor;
    Sensor SensorOrientation;
    private float lastRotateDegree;
    private int Linear_Acceleration_Count = 0;
    private ArrayList<Float> Speed_Count = new ArrayList<Float>();
    private double Speed = 0;
    private double Speed_tmp = 0;
    private double dis = 0;
    private double total_dis = 0;
    private double total_time = 0;
    private double avg_a = 0;
    private int count = 0;

    private LatLng last_GPS_position;
    private boolean init_API_position = true;
    private LatLng now_API_position;
    private LatLng last_API_position;
    private LatLng cal_position = new LatLng(0,0);
    private LatLng last_cal_position;
    private LatLng select_position;

    boolean init_time = true;
    long start_time = 0;
    long end_time = 0;
    long API_start_time = 0;
    long API_end_time = 0;
    long API_pass_time = 0;
    long pass_time = 0;
    long sensor_last_time = 0;
    long sensor_now_time = 0;
    long sensor_pass_time = 0;
    double sensor_pass_time_ms = 0;
    double GPS_dis = 0;
    double API_dis = 0;
    double GPS_ms  = 0;
    double GPS_kmh = 0;
    double API_kmh = 0;
    double API_ms  = 0;

    boolean sensor_init = true;



    private final float FILTERING_VALAUE = 0.1f;
    private float lowX,lowY,lowZ;

    double DisX = 0;
    double DisY = 0;
    double DisZ = 0;

    double accCurrentValue = 0;
    double accPreviousValue = 0;

    double sensor_speed = 0;

    boolean init_Corrected_Value = true;
    double Corrected_Value = 0;
    int peak = 0;

    int smooth_step = 0;

    My_Snap_Road my_snap_road;

    boolean thread_once = true;

    //模擬GPS刷新
    private final Handler handler = new Handler();

    private boolean init = true;
    private long last_time = 0;
    private long now_time = 0;

    long sim_start = 0;
    long sim_end   = 0;
    long sim_time  = 0;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        my_layout = new My_Layout(this);
        setContentView(my_layout);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        magneticSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorOrientation = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        my_snap_road = new My_Snap_Road();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 建立一個GoogleApiClient物件。
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //設定位置更新所執行的事情
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //首次進入APP有機會試null 所以先不刷新地圖
                if (locationResult == null) return;

                //取得經緯度
                Location location = locationResult.getLastLocation();
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                System.out.println(point);

                //存入Data
                Data.now_position = point;
                //初始化選擇點
                if(select_position==null) select_position = point;
                //刷新相機
                my_map.initCamera(Data.now_position);

                //my_map.moveCamera(Data.now_position);
                //Toast.makeText(MainActivity.this, "更新位置", Toast.LENGTH_SHORT).show();

                //Cal_Speed();
                //導航的Function
                Data.GPS_Status = true;
                //Navigation(Data.now_position, "GPS");
                GPS_data();
            }
        };
        mLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
    }
    public void GPS_data(){
        Data.GPS_Record.add(Data.now_position);
        if(init){
            //初始化數據
            init = false;
            last_GPS_position = Data.now_position;
            last_time = System.currentTimeMillis();
            navigation.now_position = Data.now_position;
        }
        else{
            //取距離
            GPS_dis = Cal_Method.Cal_Distance(last_GPS_position, Data.now_position);
            last_GPS_position = Data.now_position;
            total_dis = total_dis + GPS_dis;
            //取時間
            now_time = System.currentTimeMillis();
            pass_time = now_time - last_time;
            last_time = now_time;
            //更新至導航
            navigation.now_distance = GPS_dis;
            navigation.passtime = pass_time;
            navigation.now_position = Data.now_position;
        }
        show_GPS();
    }
    public void show_GPS(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                my_layout.Set_GPS_Status(Data.GPS_Status);
                my_layout.setdataviewRefreshGPSdis(GPS_dis + "m");
                my_layout.setdataviewNowGPSPassTime(pass_time + "ms");
                my_layout.setdataviewTotalGPSdis(total_dis + "m");
            }
        });
    }
    public void show_GPS_status(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                my_layout.Set_GPS_Status(Data.GPS_Status);
            }
        });
    }
    //模擬GPS刷新--------------------------------------------

    final Runnable GPS_Simulation = new Runnable() {
        public void run() {
            //取時間
            if(sim_start == 0){
                sim_start = System.currentTimeMillis();
            }
            sim_end = System.currentTimeMillis();
            sim_time = sim_end - sim_start;
            sim_start = sim_end;
            // 需要背景作的事，平滑顯示、模擬GPS刷新
            if(Data.now_position != null) {
                if(Data.AutoPlay) {
                    //Navigation(Data.now_position, "GPS_Simulation");
                    Random x = new Random();
                    int a = 0 + x.nextInt(15);
                    //int a = 17;
                    navigation.now_distance = a;
                    navigation.passtime     = sim_time;
                    total_dis += a;
                    System.out.println(a);
                    show_GPS();
                    if(Data.compensate_dis_test!=0){
                        navigation.compensate_dis = Data.compensate_dis_test;
                        Data.compensate_dis_test = 0;
                    }
                    //-------------------------------
                    //navigation.Navigation_Process();
                    //-------------------------------
                }
//                else{
//                    navigation.now_distance = 0;
//                }
            }
            Data.GPS_Status = false;
            show_GPS_status();
            //System.out.println("GPS_Simulation");
            handler.postDelayed(GPS_Simulation, 1000);
        }
    };
    final Runnable Smooth = new Runnable() {
        @Override
        public void run() {
            if(Data.now_position != null) {
                //Navigation(Data.now_position, "Smooth");
                if(Data.Navigation_Status) {
                    navigation.Navigation_Process();
                    //total_dis += GPS_dis;
                }
            }
            //System.out.println("Smooth");
            //Data.GPS_Status = false;
            handler.postDelayed(Smooth, 80);
        }
    };
    //------------------------------------------------------
    public void Navigation(LatLng point, String type){
        //try {
            GPS_kmh = 0;
            API_kmh = 0;
            API_ms = 0;
            //API_dis = 0;
            //紀錄時間
            //初始化
            if (init_time) {
                start_time = System.currentTimeMillis();
                end_time = System.currentTimeMillis();
                last_GPS_position = point;
                init_time = false;
            }
            //計算更新時間
            else {
                end_time = System.currentTimeMillis();
                pass_time = end_time - start_time;
                GPS_dis = Cal_Method.Cal_Distance(last_GPS_position, point);
                //更新前一個點與時間
                start_time = end_time;
                last_GPS_position = point;
                GPS_ms = (GPS_dis / pass_time) * 1000;
                GPS_kmh = (((GPS_dis / pass_time) * 1000) * 3600) / 1000;
            }

            //紀錄GPS定位
            //Data.GPS_Record.add(point);
            //紀錄API定位
            if (type.equals("GPS") && Data.SnapRoad_Status) {
                //利用GPS向Google API取得訂位
                my_snap_road.setSnapRoadUrl();
                my_snap_road.SearchLocation(new My_Snap_Road.onDataReadyCallback() {
                    @Override
                    public void onDataReady(LatLng data) {
                        //Data.API_Record.add(data);

                        //計算API距離與時速
                        if (init_API_position) {
                            init_API_position = false;
                            last_API_position = data;
                            API_start_time = System.currentTimeMillis();
                        } else {
                            now_API_position = data;
                            API_dis = Cal_Method.Cal_Distance(last_API_position, now_API_position);
                            API_end_time = System.currentTimeMillis();
                            API_pass_time = API_end_time - API_start_time;
                            API_ms = (API_dis / API_pass_time) * 1000;
                            API_kmh = (((API_dis / API_pass_time) * 1000) * 3600) / 1000;
                            API_start_time = System.currentTimeMillis();
                            //--------------------------------------------------------
                            //利用時速推估經緯度(時速->距離->經緯度)
                            if (API_ms != 0) {
                                //double cal_dis = API_ms;
                                //double cal_dis = Cal_Method.Cal_Distance(last_API_position, now_API_position);
                                double cal_dis = API_ms / 1000 * API_pass_time;
                                float bear = Cal_Method.Cal_Bearing(last_API_position, now_API_position);
                                cal_position = Cal_Method.Cal_LatLng(now_API_position, bear, cal_dis);
                                Data.API_Record.add(data);
                                Data.GPS_Record.add(point);
                                Data.Cal_Record.add(cal_position);

                                Save_patameter(Data.now_position, last_API_position, now_API_position, cal_position);
                                Save_Position("Cal-Snap" , cal_position , API_pass_time);
                                Save_Position("GPS-Snap" , Data.now_position, pass_time);
                                Save_Position("API-Snap" , now_API_position, API_pass_time);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        my_layout.setdataviewNowAPISpeedms(API_ms + "ms");
                                        my_layout.setdataviewNowAPISpeedkmh(API_kmh + "km/h");
                                    }
                                });
//                                if(last_cal_position!=null) {
//                                    select_position = Cal_Method.Drift(Data.now_position, now_API_position, last_cal_position);
//                                }
//                                else{
//                                    last_cal_position = cal_position;
//                                }
                                //??
                                select_position = now_API_position;
                            }
                            //Chick_Navigation(select_position, API_dis, type);
                            Chick_Navigation(select_position, API_dis, pass_time);
                            last_API_position = now_API_position;
                            //--------------------------------------------------------
                        }
                    }
                });
            }
            else if(type.equals("GPS")){
                //select_position = Data.now_position;
                //Chick_Navigation(select_position, GPS_dis, type);
                Chick_Navigation(Data.now_position, GPS_dis, pass_time);
            }
            else if(type.equals("GPS_Simulation")){
                Chick_Navigation(Data.now_position, 10, pass_time);
            }
            else if(type.equals("Smooth")){
                Chick_Navigation(Data.now_position, 0, pass_time);
            }
            //--------------------------------------------------------

            //是否在導航葉面
//            if (Data.Navigation_Status) {
//                //初始化導航圖示
//                if (my_new_navigation.get_initMK()) {
//                    my_new_navigation.set_initMK(false);
//                    my_new_navigation.initUserMK(Data.now_position);
//                    if(API_dis!=0){
//                        my_new_navigation.Navigation(select_position, API_dis);
//                    }
//                    else{
//                        my_new_navigation.Navigation(select_position, GPS_dis);
//                    }
//
//                } else {
//
//                    //測試用變數test_dis會不斷加總5
//                    my_new_navigation.Navigation(select_position, GPS_dis);
//                    //my_new_navigation.Navigation(select_position, 5);
//                    //test_dis+=5;
//
//                    //my_new_navigation.Navigation(select_position, GPS_dis);
//                }
//            } else {
//                //移除導航所有事情
//                my_new_navigation.set_initMK(true);
//                my_new_navigation.Remove_Navigation_MK();
//                my_new_navigation.Final_Remove_Direction();
//                my_new_navigation.Remove_Straight_Marker();
//                my_new_navigation.Remove_First_Step();
//            }

            //顯示資訊
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //my_layout.Set_GPS_Status(type);
                    my_layout.Set_GPS_Status(Data.GPS_Status);
                    my_layout.setdataviewRecordTimer("更新間格時間:" + pass_time + "ms");
                    my_layout.setdataviewNowAPIPassTime(API_pass_time + "ms");
                    my_layout.setdataviewRefreshGPSdis(GPS_dis + "m");
                    my_layout.setdataviewRefreshAPIdis(API_dis + "m");
//                    my_layout.setdataviewNowAPISpeedms(API_ms + "ms");
//                    my_layout.setdataviewNowAPISpeedkmh(API_kmh + "km/h");
                    my_layout.setdataviewNowGPSSpeedms(GPS_ms + "ms");
                    my_layout.setdataviewNowGPSSpeedkmh(GPS_kmh + "km/h");
                    my_layout.setdataviewNowGPSPassTime(pass_time + "ms");
                    //my_layout.setdataviewCalPosition(cal_position.toString());


                }
            });

            if (Data.GPS_Record.size() > 1000) {
                Data.GPS_Record.removeAll(Data.GPS_Record);
                Data.API_Record.removeAll(Data.API_Record);
                Data.Cal_Record.removeAll(Data.Cal_Record);
            }
//        }
//        catch (Exception e){
//            Cal_Method.Catch_Error_Log("Main-Navigation", e.toString());
//        }
    }
    //public void Chick_Navigation(LatLng position, double dis, String type) {
    public void Chick_Navigation(LatLng position, double dis, long pass_time) {
        if (Data.Navigation_Status) {
            //初始化導航圖示
            if (my_new_navigation.get_initMK()) {
                my_new_navigation.set_initMK(false);
               // my_new_navigation.initUserMK(Data.now_position);
                //my_new_navigation.Navigation(select_position, API_dis);
                my_new_navigation.Navigation(position, dis, pass_time);
            } else {
                //此行為主要程式----------------------------------------
                my_new_navigation.Navigation(position, dis, pass_time);
                //---------------------------------------------------
            }
        } else {
            //移除導航所有事情
            my_new_navigation.set_initMK(true);
            my_new_navigation.Remove_Navigation_MK();
            my_new_navigation.Final_Remove_Direction();
            my_new_navigation.Remove_Straight_Marker();
            my_new_navigation.Remove_First_Step();
            //my_map.set_Direction_Camera();

            //my_layout.Direction_Page(my_map);
        }
    }
    public void Save_patameter(LatLng GPS, LatLng api_start, LatLng api_end, LatLng cal){
        String date = "yyyyMMdd";
        SimpleDateFormat date_df = new SimpleDateFormat(date);
        date_df.applyPattern(date);
        String filename = date_df.format(new Date()) + "-1" + ".txt";
        //String filename = type + ".txt";
        // 存放檔案位置在 內部空間/Download/
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        try
        {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output = new FileOutputStream(file, true);

            String dateformat = "kk:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            df.applyPattern(dateformat);
            //String string =  df.format(new Date()) + " : " + type + "\n";
            //String string =  df.format(new Date()) + " : " + data.get(0)  + "\n";
            String string = "";
            //string = string + df.format(new Date()) + "," + data.get(i) + "\n";
            string = string + df.format(new Date()) + ",";
            string = string + "GPS"   + "," + GPS + ",";
            string = string + "start" + "," + api_start + ",";
            string = string + "end"   + "," + api_end   + ",";
            string = string + "cal"   + "," + cal + "\n";



            Output.write(string.getBytes());
            Output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void Save_Position(String type, LatLng point, long time){
        String date = "yyyyMMdd";
        SimpleDateFormat date_df = new SimpleDateFormat(date);
        date_df.applyPattern(date);
        String filename = date_df.format(new Date()) + type + ".txt";
        //String filename = type + ".txt";
        // 存放檔案位置在 內部空間/Download/
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        try
        {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output = new FileOutputStream(file, true);

            String dateformat = "kk:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            df.applyPattern(dateformat);
            //String string =  df.format(new Date()) + " : " + type + "\n";
            //String string =  df.format(new Date()) + " : " + data.get(0)  + "\n";
            String string = "";
//            for(int i=0; i<data.size() ;i++){
//                string = string + df.format(new Date()) + "," + data.get(i) + "\n";
//            }
            string = string + df.format(new Date()) + "," + point + "\n";
            Output.write(string.getBytes());
            Output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //Googel 地圖讀取好的時候執行
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        my_map = new My_Map(this, mMap);
        my_map.init();
        my_event = new My_Event(my_layout, my_map);
        my_event.setEvent();
        Data.Page_Order.add(Data.Main_Page);
        my_layout.Select_Page(my_map);
        //-------------------------------------------------
        my_new_navigation = new My_New_Navigation(this, my_layout, my_map);
        navigation = new Navigation(this, my_layout, my_map);
        //my_sensor = new My_Sensor(this);
        //my_sensor.registerListener();
        //-------------------------------------------------
        //模擬GPS刷新
        //Thread t = new Thread(runnable);
        Thread t= new Thread(GPS_Simulation);
        t.start();
        Thread t2 = new Thread(Smooth);
        t2.start();
        //handler.postDelayed(GPS_Simulation, 1000);
        //-------------------------------------------------
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latlng) {
                my_layout.setdataviewCalPosition(latlng.toString());
            }
        });
        //-------------------------------------------------
    }

    private void enableLocation(boolean on) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // 這項功能尚未取得使用者的同意
            // 開始執行徵詢使用者的流程
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder altDlgBuilder =
                        new AlertDialog.Builder(MainActivity.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("App需要啟動定位功能。");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                // 顯示詢問使用者是否同意功能權限的對話盒
                                // 使用者答覆後會執行onRequestPermissionsResult()
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{
                                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);
                            }
                        });
                altDlgBuilder.show();

                return;
            } else {
                // 顯示詢問使用者是否同意功能權限的對話盒
                // 使用者答覆後會執行callback方法onRequestPermissionsResult()
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION);

                return;
            }
        }

        // 這項功能之前已經取得使用者的同意，可以直接使用
        if (on) {
            // 取得上一次定位資料
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null) {
                        Toast.makeText(MainActivity.this, "成功取得上一次定位", Toast.LENGTH_LONG).show();
                        //取得上次定位點，並初始化zoom
                        LatLng last = new LatLng(location.getLatitude(), location.getLongitude());
                        Data.now_position = last;
                        my_map.initCamera(Data.now_position);
                        //float bearing = location.getBearing();
                        //my_layout.setDataViewBearing(last.toString());
                        //my_layout.setDataViewBearing(Float.toString(bearing));
                    } else {
                        Toast.makeText(MainActivity.this, "沒有上一次定位的資料", Toast.LENGTH_LONG).show();
                    }
                }
            });

            // 準備一個LocationRequest物件，設定定位參數，在啟動定位時使用
            LocationRequest locationRequest = LocationRequest.create();
            // 設定二次定位之間的時間間隔，單位是千分之一秒。
            locationRequest.setInterval(1000);
            // 二次定位之間的最大距離，單位是公尺。
            locationRequest.setSmallestDisplacement(1);

            // 啟動定位，如果GPS功能有開啟，優先使用GPS定位，否則使用網路定位。
            if (mLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationRequest.setPriority(
                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                Toast.makeText(MainActivity.this, "使用GPS定位",
                        Toast.LENGTH_LONG).show();
            } else if (mLocationMgr.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER)) {
                locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                Toast.makeText(MainActivity.this, "使用網路定位",
                        Toast.LENGTH_LONG).show();
            }

            // 啟動定位功能
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
        } else {
            // 停止定位功能
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Toast.makeText(MainActivity.this, "停止定位", Toast.LENGTH_LONG)
                    .show();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener((SensorEventListener) this, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
        sm.registerListener((SensorEventListener) this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener((SensorEventListener) this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener((SensorEventListener)this, SensorOrientation, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 啟動 Google API。
        mGoogleApiClient.connect();
    }
    @Override
    protected void onPause() {
        super.onPause();
        // 停止定位
        enableLocation(false);
    }
    @Override
    protected void onStop() {
        super.onStop();
        // 停用 Google API
        //Toast.makeText(MainActivity.this, "停用Google API", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.disconnect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Google API 連線成功時會執行這個方法
        //Toast.makeText(MainActivity.this, "Google API 連線成功", Toast.LENGTH_SHORT).show();
        // 啟動定位
        enableLocation(true);
    }
    @Override
    public void onConnectionSuspended(int i) {
        // Google API 無故斷線時，才會執行這個方法
        // 程式呼叫disconnect()時不會執行這個方法
        switch (i) {
            case CAUSE_NETWORK_LOST:
                //Toast.makeText(MainActivity.this, "網路斷線，無法定位",
                //        Toast.LENGTH_LONG).show();
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                //Toast.makeText(MainActivity.this, "Google API 異常，無法定位",
                //        Toast.LENGTH_LONG).show();
                break;
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // 和 Google API 連線失敗時會執行這個方法
        //Toast.makeText(MainActivity.this, "Google API 連線失敗", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 檢查收到的權限要求編號是否和我們送出的相同
        if (requestCode == REQUEST_PERMISSION_FOR_ACCESS_FINE_LOCATION) {
            if (grantResults.length != 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 再檢查一次，就會進入同意的狀態，並且順利啟動。
                enableLocation(true);
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onBackPressed(){
        //System.out.println("onBackPressed");
        System.out.print("page change : " + Data.Page_Order);
        System.out.print("->");
        if(Data.Page_Order.size()==1){
            goBackToDesktop();
        }
        else{
            Data.Page_Order.remove(Data.Page_Order.size()-1);
            System.out.println(Data.Page_Order);
            my_layout.Select_Page(my_map);
        }

    }
    public void goBackToDesktop(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("確定要退出?");
        builder.setPositiveButton("現在就去", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setNegativeButton("稍後", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            if(sensor_init){
                sensor_init = false;
                sensor_last_time = System.currentTimeMillis();
            }
            else {
                sensor_now_time = System.currentTimeMillis();
                sensor_pass_time = sensor_now_time - sensor_last_time;
                sensor_pass_time_ms = sensor_pass_time / 1000.0;
                my_layout.setdataviewNowSensorpasstime(Long.toString(sensor_pass_time));
                sensor_last_time = sensor_now_time;

                accCurrentValue = Math.sqrt(x*x + y*y + z*z);

                double accChange = Math.round((accCurrentValue - accPreviousValue)*10)/10.0;
                double VChange   = Math.round((accChange * sensor_pass_time_ms)*10)/10.0;
                accPreviousValue = accCurrentValue;
                //System.out.println(VChange);

//                if(change>0.1 || change<-0.1) {
//                    sensor_speed = sensor_speed + change;
//                }

                if(init_Corrected_Value){
                    init_Corrected_Value = false;
                    Corrected_Value = accCurrentValue;
                }else {
                    double test_Value = Math.abs(Math.round((accCurrentValue - Corrected_Value) * 10) / 10.0);
                    //int d = (int)(test_Value * sensor_pass_time_ms);
                    double d = Math.round((test_Value * sensor_pass_time_ms)*100);
                    //total_dis = total_dis + d;
                    //System.out.println(d);
                    //sensor_speed = sensor_speed + (int)test_Value;

                    //my_layout.setdataviewNowSensorSpeedtm(Double.toString(total_dis)+"cm");
                    my_layout.setdataviewNowSensorSpeedms(Double.toString(sensor_speed));
                    my_layout.setDataViewBearing(Double.toString(test_Value));
                }
                //System.out.println(1.0f);

                //Low-Pass Filter
                lowX = x * FILTERING_VALAUE + lowX * (1.0f - FILTERING_VALAUE);
                lowY = y * FILTERING_VALAUE + lowY * (1.0f - FILTERING_VALAUE);
                lowZ = z * FILTERING_VALAUE + lowZ * (1.0f - FILTERING_VALAUE);

                //High-pass filter
                float highX = x - lowX;
                float highY = y - lowY;
                float highZ = z - lowZ;

                float Vx = highX * sensor_pass_time;
                float Vy = highY * sensor_pass_time;
                float Vz = highZ * sensor_pass_time;

//                DisX = DisX + Vx * sensor_pass_time_ms;
//                DisY = Math.abs(DisY + Vy * sensor_pass_time);
//                DisZ = DisZ + Vz * sensor_pass_time;

                DisX = DisX + x * sensor_pass_time_ms;
                DisY = DisY + y * sensor_pass_time_ms;
                DisZ = DisZ + z * sensor_pass_time_ms;


                //output
                //为什么highX作为瞬时加速度显示？
//            TextView textX = (TextView)findViewById(R.id.x);
//            textX.setText("x:" + String.valueOf(highX));
//            TextView textY = (TextView)findViewById(R.id.y);
//            textY.setText("y:" + String.valueOf(highY));
//            TextView textZ = (TextView)findViewById(R.id.z);
//            textZ.setText("Z:" + String.valueOf(highZ));

//                my_layout.setdataviewNowSensorx(Double.toString(DisX));
//                my_layout.setdataviewNowSensory(Double.toString(DisY));
//                my_layout.setdataviewNowSensorz(Double.toString(DisZ));
                my_layout.setdataviewNowSensorx(Float.toString(x));
                my_layout.setdataviewNowSensory(Float.toString(y));
                my_layout.setdataviewNowSensorz(Float.toString(z));
            }
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}