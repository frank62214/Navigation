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
import com.google.android.gms.maps.model.LatLngBounds;
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
    private MarkerOptions GPS_Opt = new MarkerOptions();
    private MarkerOptions API_Opt = new MarkerOptions();
    private MarkerOptions Cal_Opt = new MarkerOptions();
    private Marker Navigation_MK;
    private Polyline Direction;
    private Polyline Record_Route;
    private Polyline GPS_Line;
    private Polyline API_Line;
    private Polyline Cal_Line;

    private ArrayList<Marker> rec_gps = new ArrayList<Marker>();
    private ArrayList<Marker> rec_api = new ArrayList<Marker>();
    private ArrayList<Marker> rec_cal = new ArrayList<Marker>();

    private LocationCallback locationCallback;

    public My_Map(Context cont, GoogleMap map) {
        context = cont;
        mMap = map;
    }

    public void init() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //mMap.setMyLocationEnabled(true);
        //mMap.getMyLocation();
        UiSettings UI = mMap.getUiSettings();
        UI.setMyLocationButtonEnabled(false);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng point) {
                if(!Data.Page_Order.get(Data.Page_Order.size()-1).equals(Data.Direction_Page)) {
                    Data.Destination = point;
                    Add_Destination_Mark(point);
                }
            }
        });

    }

    public void setMyLocationEnabled(boolean select) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(select);
    }
    public void initCamera(LatLng point){
        if(Data.Init_Camera) {
            Data.Init_Camera = false;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        }
        if(Data.Lock_User){
            cameraPosition = new CameraPosition.Builder()
                    .target(point)
                    .zoom(mMap.getCameraPosition().zoom)
                    .bearing(mMap.getCameraPosition().bearing)
                    .tilt(mMap.getCameraPosition().tilt)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    public void initCarCamera(LatLng point, float bearing){
        if(point==null) return;
        Navigation_MK.setPosition(Cal_LatLng(point, reverse(Data.now_bearing), 0.005));

        cameraPosition = new CameraPosition.Builder()
                .target(Cal_LatLng(point,Data.now_bearing, 0.055))
                .zoom(20)
                .bearing(Data.now_bearing)
                .tilt(65)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void moveCamera(LatLng point){
        if(point==null) { return; }
        //if(!Data.Lock_User) { return; }
        if (Data.Init_Camera) { initCamera(point); }
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
        if(point!=null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(point)
                    .zoom(mMap.getCameraPosition().zoom)
                    .bearing(bearing)
                    .tilt(mMap.getCameraPosition().tilt)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    public void moveCamera(LatLng point,float zoom, float bearing){
        if(point!=null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(point)
                    .zoom(zoom)
                    .bearing(bearing)
                    .tilt(mMap.getCameraPosition().tilt)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    public void moveCamera(LatLng point, float zoom, float bearing, float tilt){
        if(point!=null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(point)
                    .zoom(zoom)
                    .bearing(bearing)
                    .tilt(tilt)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
    public void moveCamera(LatLng point, float zoom, float bearing, float tilt, int Ms){
        if(point!=null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(point)
                    .zoom(zoom)
                    .bearing(bearing)
                    .tilt(tilt)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),Ms, null);
            //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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
    public void set_Direction_Camera(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (Data.Destination != null) {
                    double S = 0;
                    double W = 0;
                    double N = 0;
                    double E = 0;
                    if (Data.now_position.latitude > Data.Destination.latitude) {
                        N = Data.now_position.latitude;
                        S = Data.Destination.latitude;
                    } else {
                        N = Data.Destination.latitude;
                        S = Data.now_position.latitude;
                    }
                    if (Data.now_position.longitude > Data.Destination.longitude) {
                        E = Data.now_position.longitude;
                        W = Data.Destination.longitude;
                    } else {
                        E = Data.Destination.longitude;
                        W = Data.now_position.longitude;
                    }
                    LatLngBounds DestinationBounds = new LatLngBounds(
                            new LatLng(S, W), // SW bounds
                            new LatLng(N, E)  // NE bounds
                    );
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(DestinationBounds, 300));
                    //System.out.println(mMap.getCameraPosition().zoom);
                }
            }
        });
    }
    public void Draw_Direction(ArrayList<LatLng> Points){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Remove_Direction();
                PolylineOptions polylineOptions = new PolylineOptions();
                //mMap.addMarker(Navigation_test)
                for (int i = 0; i < Points.size(); i++) {
                    polylineOptions.add(Points.get(i));
                    //Navigation_test.position(Points.get(i));
                    //mMap.addMarker(Navigation_test);
                }
                polylineOptions.color(context.getResources().getColor(R.color.route_color));
                polylineOptions.width(20f);
                Direction = mMap.addPolyline(polylineOptions);
            }
        });
    }
    public Polyline Draw_PolyLine(LatLng Start, LatLng End){
        PolylineOptions polylineOptions = new PolylineOptions();
        //mMap.addMarker(Navigation_test)
        polylineOptions.add(Start);
        polylineOptions.add(End);
//        for(int i=1; i< Points.size();i++){
//            polylineOptions.add(Points.get(i));
//            //Navigation_test.position(Points.get(i));
//            //mMap.addMarker(Navigation_test);
//        }
        polylineOptions.color(context.getResources().getColor(R.color.route_color));
        polylineOptions.width(40f);
        Polyline navigation = mMap.addPolyline(polylineOptions);
        return navigation;
    }
    public Polyline Draw_PolyLine(ArrayList<LatLng> Points){
        PolylineOptions polylineOptions = new PolylineOptions();
        //mMap.addMarker(Navigation_test)
        //polylineOptions.add(point);
        for(int i=1; i< Points.size();i++){
            polylineOptions.add(Points.get(i));
            //Navigation_test.position(Points.get(i));
            //mMap.addMarker(Navigation_test);
        }
        polylineOptions.color(context.getResources().getColor(R.color.route_color));
        polylineOptions.width(30f);
        Polyline navigation = mMap.addPolyline(polylineOptions);
        return navigation;
    }
    public Polyline Draw_PolyLine(LatLng point, ArrayList<LatLng> Points, int step){
        PolylineOptions polylineOptions = new PolylineOptions();
        //mMap.addMarker(Navigation_test)
        polylineOptions.add(point);
        for(int i=step; i< Points.size();i++){
            polylineOptions.add(Points.get(i));
            //Navigation_test.position(Points.get(i));
            //mMap.addMarker(Navigation_test);
        }
        polylineOptions.color(context.getResources().getColor(R.color.route_color));
        polylineOptions.width(50f);
        Polyline navigation = mMap.addPolyline(polylineOptions);
        return navigation;
    }
    public Polyline Draw_PolyLine(LatLng point, ArrayList<LatLng> Points){
        PolylineOptions polylineOptions = new PolylineOptions();
        //mMap.addMarker(Navigation_test)
        polylineOptions.add(point);
        for(int i=1; i< Points.size();i++){
            polylineOptions.add(Points.get(i));
            //Navigation_test.position(Points.get(i));
            //mMap.addMarker(Navigation_test);
        }
        polylineOptions.color(context.getResources().getColor(R.color.route_color));
        polylineOptions.width(30f);
        Polyline navigation = mMap.addPolyline(polylineOptions);
        return navigation;
    }
    public void Remove_PolyLine(Polyline polyline){
        if(polyline != null) polyline.remove();
    }
    public void Draw_Record_Route(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Remove_Record_Route();
                PolylineOptions polylineOptions = new PolylineOptions();
                for(int i=0; i< Data.API_Record.size();i++){
                    //System.out.println(Points.get(i));
                    polylineOptions.add(Data.API_Record.get(i));
                }
                polylineOptions.color(context.getResources().getColor(R.color.record_route_color));
                polylineOptions.width(20f);
                Record_Route = mMap.addPolyline(polylineOptions);
            }
        });
    }
    public void Draw_Record_Marker(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if(Data.Dot_Items[0]) {
                    //測試中查看點
                    System.out.println(rec_gps.size());
                    //先移除原先的點，當作更新
                    if(rec_gps.size()!=0) {
                        for (int i = 0; i < rec_gps.size(); i++) {
                            rec_gps.get(i).remove();
                        }
                        //移除ArrayList
                        rec_gps.removeAll(rec_gps);
                    }
                    //將新的點畫上去
                    for (int i = 0; i < Data.GPS_Record.size(); i++) {
                        GPS_Opt.position(Data.GPS_Record.get(i));
                        GPS_Opt.icon(BitmapFromVector(R.drawable.rec_gps));
                        GPS_Opt.title("GPS:" + Integer.toString(i));
                        rec_gps.add(mMap.addMarker(GPS_Opt));
                    }
                }
                else{
                    //將點從地圖上移除
                    for (int i = 0; i < rec_gps.size(); i++) {
                        rec_gps.get(i).remove();
                    }
                    //移除ArrayList
                    rec_gps.removeAll(rec_gps);
                }
                if(Data.Dot_Items[1] ) {
                    //測試中查看點
                    System.out.println(rec_api.size());
                    //先移除原先的點，當作更新
                    if(rec_api.size()!=0) {
                        for (int i = 0; i < rec_api.size(); i++) {
                            rec_api.get(i).remove();
                        }
                        //移除ArrayList
                        rec_api.removeAll(rec_api);
                    }
                    //將新的點畫上去
                    for (int i = 0; i < Data.API_Record.size(); i++) {
                        API_Opt.position(Data.API_Record.get(i));
                        API_Opt.icon(BitmapFromVector(R.drawable.rec_api));
                        API_Opt.title("API:" + Integer.toString(i));
                        rec_api.add(mMap.addMarker(API_Opt));
                    }
                }
                else{
                    //將點從地圖上移除
                    for(int i=0; i<rec_api.size();i++){
                        rec_api.get(i).remove();
                    }
                    //移除ArrayList
                    rec_api.removeAll(rec_api);
                }
                if(Data.Dot_Items[2]) {
                    //測試中查看點
                    System.out.println(rec_cal.size());
                    //先移除原先的點，當作更新
                    if(rec_cal.size()!=0) {
                        for (int i = 0; i < rec_cal.size(); i++) {
                            rec_cal.get(i).remove();
                        }
                        //移除ArrayList
                        rec_cal.removeAll(rec_cal);
                    }
                    System.out.println(rec_cal.size());
                    //將新的點畫上去
                    for (int i = 0; i < Data.Cal_Record.size(); i++) {
                        Cal_Opt.position(Data.Cal_Record.get(i));
                        Cal_Opt.icon(BitmapFromVector(R.drawable.rec_cal));
                        Cal_Opt.title("Cal:" + Integer.toString(i));
                        rec_cal.add(mMap.addMarker(Cal_Opt));
                    }
                }
                else{
                    //將點從地圖上移除
                    for(int i=0; i<rec_cal.size();i++){
                        rec_cal.get(i).remove();
                    }
                    rec_cal.removeAll(rec_cal);
                }
            }
        });
    }
    public void Remove_Record_Marker(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                for(int i=0; i<rec_gps.size();i++){
                    rec_gps.get(i).remove();
                }
                for(int i=0; i<rec_api.size();i++){
                    rec_api.get(i).remove();
                }
                for(int i=0; i<rec_cal.size();i++){
                    rec_cal.get(i).remove();
                }
            }});
    }
    public void Draw_Record_Marker_PolyLine(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                //PolylineOptions polylineOptions = new PolylineOptions();
                //                for(int i=0; i< Data.API_Record.size();i++){
                //                    //System.out.println(Points.get(i));
                //                    polylineOptions.add(Data.API_Record.get(i));
                //                }
                //                polylineOptions.color(context.getResources().getColor(R.color.record_route_color));
                //                polylineOptions.width(20f);
                //                Record_Route = mMap.addPolyline(polylineOptions);
                //GPS顯示與否
                if(Data.Line_Items[0]) {
                    if(GPS_Line!=null) {
                        Remove_PolyLine(GPS_Line);
                    }
                    PolylineOptions polylineOptions_GPS = new PolylineOptions();
                    for(int i=0 ; i<Data.GPS_Record.size(); i++){
                        polylineOptions_GPS.add(Data.GPS_Record.get(i));
                    }
                    polylineOptions_GPS.color(context.getResources().getColor(R.color.record_GPS_route_color));
                    polylineOptions_GPS.width(20f);
                    GPS_Line = mMap.addPolyline(polylineOptions_GPS);
                }
                else{
                    Remove_PolyLine(GPS_Line);
                }
                //API顯示與否
                if(Data.Line_Items[1]){
                    if(API_Line!=null) {
                        Remove_PolyLine(API_Line);
                    }
                    PolylineOptions polylineOptions_API = new PolylineOptions();
                    for(int i=0 ; i<Data.API_Record.size(); i++){
                        polylineOptions_API.add(Data.API_Record.get(i));
                    }
                    polylineOptions_API.color(context.getResources().getColor(R.color.record_API_route_color));
                    polylineOptions_API.width(20f);
                    API_Line = mMap.addPolyline(polylineOptions_API);
                }
                else{
                    Remove_PolyLine(API_Line);
                }
                //計算點顯示與否
                if(Data.Line_Items[2]){
                    if(Cal_Line!=null) {
                        Remove_PolyLine(Cal_Line);
                    }
                    PolylineOptions polylineOptions_Cal = new PolylineOptions();
                    for(int i=0 ; i<Data.Cal_Record.size(); i++){
                        polylineOptions_Cal.add(Data.Cal_Record.get(i));
                    }
                    polylineOptions_Cal.color(context.getResources().getColor(R.color.record_Cal_route_color));
                    polylineOptions_Cal.width(20f);
                    Cal_Line = mMap.addPolyline(polylineOptions_Cal);
                }
                else{
                    Remove_PolyLine(Cal_Line);
                }
            }
        });
    }
    public void Remove_Record_Marker_PolyLine(){
        //if(polyline != null) polyline.remove();
        //if(GPS_Line!=null) GPS_Line.remove();
        Remove_PolyLine(GPS_Line);
        Remove_PolyLine(API_Line);
        Remove_PolyLine(Cal_Line);
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
                Navigation_MK.setPosition(Cal_LatLng(point, reverse(bearing), 0.010));
                //reverse(bearing );
                //Navigation_MK.setPosition(point);
                cameraPosition = new CameraPosition.Builder()
                        .target(Cal_LatLng(point, bearing, 0.055))
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
        //禁止使用Thread 會導致畫面畫不出來
        //System.out.println(Direction.getPoints().size());
        if (Direction != null) {
            Direction.remove();
        }

    }
    public void Remove_Record_Route(){
        if(Record_Route!=null){
            Record_Route.remove();
        }
    }
    public void Remove_Destination(){
        if(destination!=null){
            destination.remove();
        }
    }
    public void Remove_Navigation_MK(){
        if(Navigation_MK!=null){ Navigation_MK.remove(); }
    }
    public void Remove_Marker(Marker marker){
        marker.remove();
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
    public double reverse(double bearing){
        //System.out.println("Before:" + bearing);
        if(bearing >= 180){ bearing = bearing - 180; }
        else{ bearing = bearing + 180; }
        //System.out.println("After: " + bearing);
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
    public Marker Add_Marker(MarkerOptions MKopt){
        return mMap.addMarker(MKopt);
    }
    public void Nav(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                for(int i=0; i<Data.Nav_Record.size(); i++){
                    Data.Nav_Opt.position(Data.Nav_Record.get(i));
                    Data.Nav_Opt.icon(Cal_Method.BitmapFromVector(context, R.drawable.history_point));
                    Data.Nav_marker.add(Add_Marker(Data.Nav_Opt));
                }
                Data.Nav_History = true;
            }
        });
    }
    public void removeNav(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                for(int i=0; i<Data.Nav_marker.size(); i++){
                    Remove_Marker(Data.Nav_marker.get(i));
                }
                Data.Nav_History = false;
            }
        });
    }
}