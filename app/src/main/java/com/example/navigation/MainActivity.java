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
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import com.example.navigation.My.Data;
import com.example.navigation.My.My_Event;
import com.example.navigation.My.My_Layout;
import com.example.navigation.My.My_Map;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        my_layout = new My_Layout(this);
        setContentView(my_layout);


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
                if (locationResult == null)
                    return;

                Location location = locationResult.getLastLocation();
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());

                Data.now_position = point;
                my_map.initCamera(Data.now_position);
                my_map.moveCamera(Data.now_position);
                Toast.makeText(MainActivity.this, "更新位置", Toast.LENGTH_SHORT).show();
            }
        };
        mLocationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        //mLocationMgr.
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        my_map = new My_Map(this, mMap);
        my_map.init();
        my_event = new My_Event(my_layout, my_map);
        my_event.setEvent();
        Data.Page_Order.add(Data.Main_Page);
        my_layout.Select_Page(my_map);
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
            mFusedLocationClient.requestLocationUpdates(
                    locationRequest, mLocationCallback, Looper.myLooper());
        } else {
            // 停止定位功能
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Toast.makeText(MainActivity.this, "停止定位", Toast.LENGTH_LONG)
                    .show();
        }
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
        Toast.makeText(MainActivity.this, "停用Google API", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.disconnect();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Google API 連線成功時會執行這個方法
        Toast.makeText(MainActivity.this, "Google API 連線成功", Toast.LENGTH_SHORT).show();
        // 啟動定位
        enableLocation(true);
    }
    @Override
    public void onConnectionSuspended(int i) {
        // Google API 無故斷線時，才會執行這個方法
        // 程式呼叫disconnect()時不會執行這個方法
        switch (i) {
            case CAUSE_NETWORK_LOST:
                Toast.makeText(MainActivity.this, "網路斷線，無法定位",
                        Toast.LENGTH_LONG).show();
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Toast.makeText(MainActivity.this, "Google API 異常，無法定位",
                        Toast.LENGTH_LONG).show();
                break;
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // 和 Google API 連線失敗時會執行這個方法
        Toast.makeText(MainActivity.this, "Google API 連線失敗", Toast.LENGTH_SHORT).show();
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
        if(Data.Page_Order.size()==1){
            goBackToDesktop();
        }
        else{
            Data.Page_Order.remove(Data.Page_Order.size()-1);
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
}