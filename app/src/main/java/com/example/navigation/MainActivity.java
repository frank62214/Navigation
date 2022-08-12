package com.example.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigation.My.Cal_Method;
import com.example.navigation.My.Data;
import com.example.navigation.My.My_Event;
import com.example.navigation.My.My_Layout;
import com.example.navigation.My.My_Map;
import com.example.navigation.My.My_New_Navigation;
import com.example.navigation.My.My_Sensor;
import com.example.navigation.My.My_Snap_Road;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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
    private My_Sensor my_sensor;

    private SensorManager sm;
    private int Linear_Acceleration_Count = 0;
    private ArrayList<Float> Speed_Count = new ArrayList<Float>();
    private float Speed = 0;

    private LatLng last_GPS_position;
    private boolean init_API_position = true;
    private LatLng now_API_position;
    private LatLng last_API_position;
    private LatLng cal_position = new LatLng(0,0);

    boolean init_time = true;
    long start_time = 0;
    long end_time = 0;
    long API_start_time = 0;
    long API_end_time = 0;
    long API_pass_time = 0;
    long pass_time = 0;
    double GPS_dis = 0;
    double API_dis = 0;
    double GPS_ms  = 0;
    double GPS_kmh = 0;
    double API_kmh = 0;
    double API_ms  = 0;
    int ms = 0;

    My_Snap_Road my_snap_road;

    //模擬GPS刷新
    private final Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        my_layout = new My_Layout(this);
        setContentView(my_layout);

        sm = (SensorManager) getSystemService(this.SENSOR_SERVICE);

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
                //刷新相機
                my_map.initCamera(Data.now_position);

                //my_map.moveCamera(Data.now_position);
                Toast.makeText(MainActivity.this, "更新位置", Toast.LENGTH_SHORT).show();





                //Cal_Speed();
                //導航的Function
                Data.GPS_Status = true;
                Navigation(Data.now_position, "GPS");
            }
        };
        mLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    //模擬GPS刷新--------------------------------------------
    final Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            // 需要背景作的事
            while(true) {
                if(!Data.GPS_Status) {
                    if(Data.now_position!=null) {
                        Navigation(Data.now_position, "Thread");
                    }
                }
                else{
                    Data.GPS_Status = false;
                }
                SystemClock.sleep(1000);
            }
        }
    };
    //------------------------------------------------------
    public void Navigation(LatLng point, String type){
        try {
            GPS_kmh = 0;
            API_kmh = 0;
            API_ms = 0;
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
            Data.GPS_Record.add(point);
            //紀錄API定位
            if (type.equals("GPS") && Data.SnapRoad_Status) {
                my_snap_road.setSnapRoadUrl();
                my_snap_road.SearchLocation(new My_Snap_Road.onDataReadyCallback() {
                    @Override
                    public void onDataReady(LatLng data) {
                        Data.API_Record.add(data);

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
                            last_API_position = now_API_position;
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
                                Data.Cal_Record.add(cal_position);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        my_layout.setdataviewNowAPISpeedms(API_ms + "ms");
                                        my_layout.setdataviewNowAPISpeedkmh(API_kmh + "km/h");
                                        Save_Position("Cal" , Data.Cal_Record , API_pass_time);
                                        Save_Position("GPS" , Data.GPS_Record, pass_time);
                                        Save_Position("API" , Data.API_Record, API_pass_time);
                                    }
                                });
                            }
                            //--------------------------------------------------------
                        }
                    }
                });
            }
            //--------------------------------------------------------
            //顯示資訊
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    my_layout.Set_GPS_Status(type);
                    my_layout.setdataviewRecordTimer("更新間格時間:" + pass_time + "ms");
                    my_layout.setdataviewNowAPIPassTime(API_pass_time + "ms");
                    my_layout.setdataviewRefreshGPSdis(GPS_dis + "m");
                    my_layout.setdataviewRefreshAPIdis(API_dis + "m");
//                    my_layout.setdataviewNowAPISpeedms(API_ms + "ms");
//                    my_layout.setdataviewNowAPISpeedkmh(API_kmh + "km/h");
                    my_layout.setdataviewNowGPSSpeedms(GPS_ms + "ms");
                    my_layout.setdataviewNowGPSSpeedkmh(GPS_kmh + "km/h");
                    my_layout.setdataviewCalPosition(cal_position.toString());


                }
            });


            if (Data.Navigation_Status) {
                if (my_new_navigation.get_initMK()) {
                    my_new_navigation.set_initMK(false);
                    my_new_navigation.initUserMK();
                } else {
                    my_new_navigation.Navigation();
                }
            } else {
                my_new_navigation.set_initMK(true);
                my_new_navigation.Remove_Navigation_MK();
                my_new_navigation.Final_Remove_Direction();
            }
            if (Data.GPS_Record.size() > 1000) {
                Data.GPS_Record.removeAll(Data.GPS_Record);
                Data.API_Record.removeAll(Data.API_Record);
                Data.Cal_Record.removeAll(Data.Cal_Record);
            }
        }
        catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(e.toString()).setTitle("Error").setIcon(R.drawable.warning);
            builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }
    }
    public void Save_Position(String type, ArrayList<LatLng> data, long time){
        String filename = type + ".txt";
        // 存放檔案位置在 內部空間/Download/
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        try
        {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output = new FileOutputStream(file, false);

            String dateformat = "yyyyMMdd kk:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            df.applyPattern(dateformat);
            String string =  df.format(new Date()) + " : " + type + "\n";
            //String string =  df.format(new Date()) + " : " + data.get(0)  + "\n";
            for(int i=0; i<data.size() ;i++){
                string = string + time+ " : " + data.get(i) + "\n";
            }
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
        my_new_navigation = new My_New_Navigation(this, mMap, my_layout, my_map);
        //my_sensor = new My_Sensor(this);
        //my_sensor.registerListener();
        //-------------------------------------------------
        //模擬GPS刷新
        Thread t = new Thread(runnable);
        t.start();
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
            locationRequest.setInterval(5000);
            // 二次定位之間的最大距離，單位是公尺。
            locationRequest.setSmallestDisplacement(5);

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
        sm.registerListener((SensorEventListener) this, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
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
        int sensorType = sensorEvent.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

//                if(Linear_Acceleration_Count<5) {
//                    Speed = Speed + y;
//                    Linear_Acceleration_Count++;
//                }
//                else{
//                    Speed = Speed / 5;
//                    Linear_Acceleration_Count = 0;
//                }

                my_layout.setdataviewNowSensor(Float.toString(y));
                //Speed_Count.add(y);
                //System.out.println("x= " + Float.toString(x));
                //System.out.println("y= " + Float.toString(y));
                //System.out.println("z= " + Float.toString(z));
//                if(Linear_Acceleration_Count >10) {
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        public void run() {
//                            my_layout.setdataviewNowSensor(Float.toString(y));
//                            Linear_Acceleration_Count = 0;
//                        }
//                    });
//                }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}