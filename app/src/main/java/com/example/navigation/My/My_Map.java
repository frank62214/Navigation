package com.example.navigation.My;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.navigation.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private MarkerOptions Navigation_MK_Opt = new MarkerOptions();
    private Marker Navigation_MK;
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
        //mMap.getMyLocation();
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
        if(Data.Init_Camera) {
            Data.Init_Camera = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        }
    }
    public void moveCamera(LatLng point){
        if(Data.Init_Camera){ initCamera(point); }
        //System.out.println(cameraPosition.target);
        cameraPosition = new CameraPosition.Builder()
                .target(point)
                .zoom(mMap.getCameraPosition().zoom)
                .bearing(mMap.getCameraPosition().bearing)
                .tilt(mMap.getCameraPosition().tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void moveCamera(LatLng point, float bearing){
        cameraPosition = new CameraPosition.Builder()
                .target(point)
                .zoom(mMap.getCameraPosition().zoom)
                .bearing(bearing)
                .tilt(mMap.getCameraPosition().tilt)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void addMark(LatLng point){
        if(destination != null){ destination.remove(); }
        destination_mark.position(point);
        destination = mMap.addMarker(destination_mark);
    }
    public void Add_Destination_Mark(LatLng point){
        moveCamera(point);
        addMark(point);
    }
    public void Draw_Direction(ArrayList<LatLng> Points){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Remove_Direction();
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
    public void initUserMK(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Navigation_MK_Opt.position(Data.now_position);
                Navigation_MK_Opt.icon(BitmapFromVector(R.drawable.mk_user_arrow));
                Navigation_MK = mMap.addMarker(Navigation_MK_Opt);
            }
        });
    }
    public void set_Navigation_Camera(LatLng point, float bearing){
        //LatLng camera_position = Cal_Camera_Position(Data.now_position);
        //System.out.println(Data.now_position);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Navigation_MK.setPosition(point);
                cameraPosition = new CameraPosition.Builder()
                        .target(point)
                        .zoom(20)
                        .bearing(bearing)
                        .tilt(65)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //mMap.animateCamera(CameraUpdateFactory.scrollBy(10,0));
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
    public float Cal_Bearing(LatLng Start, LatLng End){
        double degress = Math.PI / 180.0;
        double phi1 = Start.latitude * degress;
        double phi2 = End.latitude * degress;
        double lam1 = Start.longitude * degress;
        double lam2 = End.longitude * degress;

        double y = Math.sin(lam2 - lam1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(lam2 - lam1);
        float bearing = (float)(((Math.atan2(y, x) * 180) / Math.PI) + 360) % 360;
        System.out.println(bearing);
        if (bearing < 0) {
            bearing = bearing + 360;
        }
        return bearing;
    }
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

}
