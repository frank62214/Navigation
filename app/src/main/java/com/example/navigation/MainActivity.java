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
import com.example.navigation.My.My_CarMode;
import com.example.navigation.My.My_Event;
import com.example.navigation.My.My_Layout;
import com.example.navigation.My.My_Map;
import com.example.navigation.My.My_New_Navigation;
import com.example.navigation.My.My_Sensor;
import com.example.navigation.My.My_Snap_Road;
import com.example.navigation.My.Navigation;
import com.example.navigation.My.Orientation;
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
import java.util.Arrays;
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
    private My_CarMode carMode;
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

    float GPS_Bearing = 0;

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


    private Orientation value = new Orientation();
    private final float[] accelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] rotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];


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
        //TYPE_GEOMAGNETIC_ROTATION_VECTOR
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
                //取消，三秒沒動就刷新的Thread
                handler.removeCallbacks(initGPS);
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
                //導航的Function
                Data.GPS_Status = true;
                //將一切判斷與資料傳遞放在GPS_data()
                GPS_data();
                //設定三秒沒動就刷新
                handler.postDelayed(initGPS, 3000);
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
            Data.Cal_Record.add(Data.now_position);
        }
        else{
            //取距離
            GPS_dis = Cal_Method.Cal_Distance(last_GPS_position, Data.now_position);
            total_dis = total_dis + GPS_dis;
            //取方向角
            GPS_Bearing = Cal_Method.Cal_Bearing(last_GPS_position, Data.now_position);
            Data.Compass_Bearing = GPS_Bearing;
            //取時間
            now_time = System.currentTimeMillis();
            pass_time = now_time - last_time;
            last_time = now_time;
            //更新至導航
            navigation.passtime = pass_time;
            navigation.last_GPS_position = last_GPS_position;
            navigation.now_position = Data.now_position;
            navigation.now_bearing = GPS_Bearing;
            navigation.now_distance = GPS_dis;
            navigation.acc_dis = accCurrentValue;
            //更新位置
            last_GPS_position = Data.now_position;
            //更新至車用模式
            carMode.now_position = Data.now_position;
            //carMode.now_bearing = GPS_Bearing;
            carMode.gps_distance = GPS_dis;
            carMode.Call_API();
            //carMode.get_position = true;
            //carMode.now_distance = GPS_dis;

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
    int sim_count = 0;
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
                    if(Data.Navigation_Status || Data.CarMode_Status) {
                        //Random x = new Random();
                        //int a = 0 + x.nextInt(15);
                        int a = 17;
                        navigation.now_distance = a;
                        total_dis += a;
                        show_GPS();
                    }
                }
            }
            Data.GPS_Status = false;
            show_GPS_status();
            handler.postDelayed(GPS_Simulation, 1000);
        }
    };
    final Runnable Smooth = new Runnable() {
        @Override
        public void run() {
            if(Data.now_position != null) {
                if(Data.Navigation_Status || Data.CarMode_Status) {
                    navigation.Navigation_Process();
                }
            }
            handler.postDelayed(Smooth, 80);
        }
    };
    final Runnable initGPS = new Runnable() {
        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,
                            "啟動刷新",
                            Toast.LENGTH_SHORT).show();
                }
            });
            init = true;
        }
    };
    //------------------------------------------------------

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
        carMode    = new My_CarMode(this, my_layout, my_map);
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

    private float[] rotate = new float[9];
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    float[] values = new float[3];
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
        //TYPE_ORIENTATION
        //---------------------------------------------------------------------------
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = sensorEvent.values;
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = sensorEvent.values;
        }
        SensorManager.getRotationMatrix(rotate, null, accelerometerValues,magneticFieldValues);//通过磁力和加速度值计算旋转矩阵，赋值给rotate


        SensorManager.getOrientation(rotate, values);//最后通过矩阵数组计算x,y,z方向手机角度，目前我们需要第一个角度x轴的

        values[0] = (float) Math.toDegrees(values[0]);

        //System.out.println("定位角度是："+values[0]);//此结果为从北到南顺时针为0-180度，从南-北顺指针-180到0度，其实是弧度运算的，也就是从北顺指针是0-360度
        //value.show();
        //---------------------------------------------------------------------------
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

                accCurrentValue = Math.round(Math.sqrt(x*x + y*y + z*z) * 10000) / 10000.0;

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

                my_layout.setdataviewNowSensorSpeedm(Double.toString(accCurrentValue));
                my_layout.setdataviewNowSensorx(Float.toString(x));
                my_layout.setdataviewNowSensory(Float.toString(y));
                my_layout.setdataviewNowSensorz(Float.toString(z));
            }
        }

    }
    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null,
                accelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}