package com.example.navigation.My;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.navigation.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class My_Map {
    private Context context;
    private GoogleMap mMap;
    private LatLng now_position;

    private CameraPosition cameraPosition;
    private MarkerOptions destination_mark = new MarkerOptions();
    private Marker destination;
    private Polyline Direction;

    private LocationCallback locationCallback;

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
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng point) {
                Data.Destination = point;
                Add_Destination_Mark(point);
            }
        });
    }
    public void initCamera(LatLng point){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
    }
    public void moveCamera(LatLng point){
        cameraPosition = new CameraPosition.Builder()
                .target(point)
                .zoom(mMap.getCameraPosition().zoom)
                .bearing(mMap.getCameraPosition().bearing)
                .tilt(mMap.getCameraPosition().tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void addMark(LatLng point){
        destination_mark.position(point);
        destination = mMap.addMarker(destination_mark);
    }
    public void Add_Destination_Mark(LatLng point){
        moveCamera(point);
        addMark(point);
    }
    public void Draw_Direction(ArrayList<LatLng> Points){
        Remove_Direction();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
            PolylineOptions polylineOptions = new PolylineOptions();
            for(int i=0; i< Points.size();i++){
                //System.out.println(Points.get(i));
                polylineOptions.add(Points.get(i));
            }
            polylineOptions.color(context.getResources().getColor(R.color.route_color));
            polylineOptions.width(20f);
            Direction = mMap.addPolyline(polylineOptions);
            }
        });
    }
    public void set_Navigation_Camera(){
        //LatLng camera_position = Cal_Camera_Position(Data.now_position);
        System.out.println(Data.now_position);
        cameraPosition = new CameraPosition.Builder()
                .target(Data.now_position)
                .zoom(20)
                .bearing(Data.now_bearing )
                .tilt(65)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mMap.animateCamera(CameraUpdateFactory.scrollBy(10,0));

    }
    public double Camera_Dis_cal(LatLng Start, LatLng End){
        double EARTH_RADIUS = 6378137.0;
        //double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (Start.latitude * Math.PI / 180.0);
        double radLat2 = (End.latitude * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (Start.longitude - End.longitude) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2)
                        * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    public void Remove_Direction(){
        if(Direction!=null) {
            Direction.remove();
        }
    }

}
