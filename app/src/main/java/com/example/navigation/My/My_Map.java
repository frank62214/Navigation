package com.example.navigation.My;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class My_Map {
    private Context context;
    private GoogleMap mMap;
    private LatLng now_position;
    private CameraPosition cameraPosition;

    public My_Map(Context cont, GoogleMap map){
        context = cont;
        mMap = map;
    }
    public void init(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        UiSettings UI  = mMap.getUiSettings();
        UI.setMyLocationButtonEnabled(false);
    }
    public void initCamera(LatLng point){
        cameraPosition = new CameraPosition.Builder()
                .target(point)
                .zoom(15)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void moveCamera(Location location){
        
    }
    private void updateMapLocation(Location location) {
        // location物件中包含定位的經緯度資料
        // 移動地圖到新位置
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
        //        new LatLng(location.getLatitude(), location.getLongitude()), 15));
        now_position = new LatLng(location.getLatitude(), location.getLongitude());
        cameraPosition = new CameraPosition.Builder()
                .target(now_position)
                .zoom(mMap.getCameraPosition().zoom)
                .bearing(mMap.getCameraPosition().bearing)
                .tilt(mMap.getCameraPosition().tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
