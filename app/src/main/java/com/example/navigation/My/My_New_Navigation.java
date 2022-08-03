package com.example.navigation.My;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.example.navigation.MainActivity;
import com.example.navigation.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * 獨立的Class  相機移動 路口判斷全在這
 */
public class My_New_Navigation {
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private final MarkerOptions Navigation_MK_Opt = new MarkerOptions();
    private Marker Navigation_MK;
    private Context context;


    private boolean initMK = true;


    public My_New_Navigation(Context cont, GoogleMap map){
        context = cont;
        mMap = map;
    }
    public void Navigation(){
        NavigationCamera();
    }
    public void NavigationCamera(){
        cameraPosition = new CameraPosition.Builder()
                .target(Data.now_position)
                .zoom(20)
                .bearing(Cal_Bearing(start, end))
                .tilt(65)
                .build();
        Change_camera();
    }
    private void Change_camera(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });

    }
    public void initUserMK(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Navigation_MK_Opt.position(Data.now_position);
                Navigation_MK_Opt.icon(BitmapFromVector(R.drawable.mk_user_arrow));
                Navigation_MK = mMap.addMarker(Navigation_MK_Opt);
            }
        });
    }
    //計算式  有機會要整理
    public BitmapDescriptor BitmapFromVector(int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    public float Cal_Bearing(LatLng Start, LatLng End){
        //System.out.println("Start: "+ Start);
        //System.out.println("End: "+ End);
        double degress = Math.PI / 180.0;
        //System.out.println("Degress: " + degress);
        double phi1 = Start.latitude * degress;
        //System.out.println("phi1: " + phi1);
        double phi2 = End.latitude * degress;
        //System.out.println("phi2: " + phi2);
        double lam1 = Start.longitude * degress;
        //System.out.println("lam1: " + lam1);
        double lam2 = End.longitude * degress;
        //System.out.println("lam2: " + lam2);

        double y = Math.sin(lam2 - lam1) * Math.cos(phi2);
        //System.out.println("y: " + y);
        double x = Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1);
        //System.out.println("x: " + x);
        float bearing = (float)(((Math.atan2(y, x) * 180) / Math.PI) + 360) % 360;
        //System.out.println("bearing1: " + bearing);
        //System.out.println(bearing);
        if (bearing < 0) {
            bearing = bearing + 360;
        }
        //System.out.println("bearing2: " + bearing);
        return bearing;
    }
    public LatLng Cal_LatLng(LatLng Start, double bearing, double distance){
        LatLng camera = new LatLng(0,0);
        //距離單位為km
        //double distance = 0.055;
        //地球每度的弧長(km)
        double EARTH_ARC = 111.199;
        //將方向角轉成弧度
        bearing = Math.toRadians(bearing);
        // 將距離轉換成經度的計算公式
        double lon = Start.longitude + (distance * Math.sin(bearing))
                / (EARTH_ARC * Math.cos(Math.toRadians(Start.latitude)));
        // 將距離轉換成緯度的計算公式
        double lat = Start.latitude + (distance * Math.cos(bearing)) / EARTH_ARC;

        camera = new LatLng(lat, lon);
        return camera;
    }
    //取得值
    public boolean get_initMK() {return initMK;}

    //設定值
    public void set_initMK(boolean value){ initMK = value;}
}
