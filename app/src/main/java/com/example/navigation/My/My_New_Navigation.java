package com.example.navigation.My;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 獨立的Class  相機移動 路口判斷全在這
 */
public class My_New_Navigation {
    private My_Layout my_layout;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private final MarkerOptions Navigation_MK_Opt = new MarkerOptions();
    private final MarkerOptions Navigation_test = new MarkerOptions();
    private Marker Navigation_MK;
    private Context context;
    private Polyline Direction;

    private LatLng Navigation_MK_Start = new LatLng(0,0);
    private LatLng Navigation_MK_End   = new LatLng(0,0);

    //private LatLng Now_Position  = new LatLng(0,0);
    //private LatLng Last_Position = new LatLng(0,0);

    private boolean initMK = true;
    private My_Direction my_direction;

    private double Now_Bearing;
    private double Last_Bearing;

    private boolean initData = true;

    private int distance;

    private int now_step = 0;
    private int next_step =0;

    private My_Map my_map;

    private double time = 0.0;
    private Timer timer;
    private TimerTask timerTask;

    private LatLng last_position;
    private LatLng now_API_position;
    private LatLng last_API_position;


    private long start_time = 0;
    private long end_time = 0;

    public My_New_Navigation(Context cont, GoogleMap map, My_Layout layout, My_Map mymap){
        context = cont;
        mMap = map;
        my_layout = layout;
        my_map = mymap;
        my_direction = new My_Direction();
        timer = new Timer();
    }
    public void Navigation(){

        //now_API_position = Data.now_position;
        //Cal_GPS_Speed();
        //Cal_API_Speed();
//        if(now_step < Data.Decoder_Steps.size()) {
//            next_step = now_step + 1;
//            Draw_Direction(Data.now_position, next_step, Data.Decoder_Steps);
//            Now_Bearing = Cal_Bearing(Data.now_position, Data.Decoder_Steps.get(next_step));
//            NavigationCamera();
//            set_Navigation_Text();
//            double now_dis = Cal_Distance(Data.now_position, Data.Decoder_Steps.get(next_step));
//            Toast("現在距離" + Double.toString(now_dis));
//            if(now_dis<20){
//                now_step++;
//            }
//        }
//
//            System.out.println("FYBR");
//            next_step = now_step + 1;
//            double now_dis = Cal_Distance(Data.now_position, Data.Decoder_Steps.get(next_step));
//            Toast("現在距離" + Double.toString(now_dis));
//            Draw_Direction(Data.now_position, now_step, Data.Steps);
//            Now_Bearing = Cal_Bearing(Data.now_position, Data.Decoder_Steps.get(next_step));
//            NavigationCamera();
//            set_Navigation_Text();
//            if(now_dis<20){
//                now_step++;
//                //Draw_Direction(Data.now_position, now_step, Data.Steps);
//            }
//            else{
//                //Draw_Direction(Data.now_position, now_step, Data.Steps);
//            }
//        }
//        else if(now_step == Data.Steps.size()){
//            double now_dis = Cal_Distance(Data.Destination, Data.Steps.get(now_step));
//            Toast("距離目的地" + Double.toString(now_dis));
//            if(now_dis<10){
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//// Add the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        my_layout.Direction_Page(my_map);
//                    }
//                });
//            }
//        }
        try {
            my_direction.searchDirection();
            my_direction.SearchNavigationData(new My_Direction.onDataReadyCallback() {
                @Override
                public void onDataReady(ArrayList<LatLng> data) {
                    now_API_position = data.get(0);
                    if (initData) {
                        //第一次執行
                        initData = false;
                        Last_Bearing = Cal_Bearing(data.get(0), data.get(1));
                        //Last_Bearing = Cal_Bearing(Data.now_position, data.get(0));
                    } else {
                        Now_Bearing = Cal_Bearing(data.get(0), data.get(1));
                        //Now_Bearing = Cal_Bearing(Data.now_position, data.get(0));
                    }
                    Navigation_MK_Start = data.get(0);
                    Navigation_MK_End = data.get(1);
                    if (!Check_Drift()) {
                        Last_Bearing = Now_Bearing;
                        distance = (int) Cal_Distance(Navigation_MK_Start, Navigation_MK_End);
                        Draw_Direction(data);
                        NavigationCamera();
                        set_Navigation_Text();
                    }
                }

                @Override
                public void onDisReady(int dis) {
                    distance = dis;
                }

                @Override
                public void onStartLocationReady(LatLng start, LatLng end) {
                }
            });
        }
        catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
    public void NavigationCamera(){
        cameraPosition = new CameraPosition.Builder()
                .target(Cal_LatLng(Navigation_MK_Start, Now_Bearing, 0.055))
                .zoom(20)
                .bearing(Cal_Bearing(Navigation_MK_Start, Navigation_MK_End))
                .tilt(65)
                .build();
        Change_camera();
//        cameraPosition = new CameraPosition.Builder()
//                .target(Cal_LatLng(Data.now_position, Now_Bearing, 0.055))
//                .zoom(20)
//                .bearing(Cal_Bearing(Data.now_position, Data.Decoder_Steps.get(next_step)))
//                .tilt(65)
//                .build();
//        Change_camera();
    }
    private void Change_camera(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Navigation_MK.setPosition(Cal_LatLng(Navigation_MK_Start, reverse(Now_Bearing), 0.010));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            public void run() {
//                Navigation_MK.setPosition(Cal_LatLng(Data.now_position, reverse(Now_Bearing), 0.010));
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }
//        });
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
    public void set_Navigation_Text(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                int select = 0;
                if(distance<100 && Data.Steps.size()<2){
                    select = 1;
                }
                my_layout.setNextRoadText(Data.Road.get(select));
                my_layout.setNextRoadDetailText(Data.Road_Detail.get(select));
                my_layout.Set_Turn_Pic(Data.Road_Detail.get(select));
                my_layout.setNowPosition(Data.now_position.toString());
                my_layout.setNextRoadDistance(Integer.toString(distance));
                my_layout.setDisToDestination(Double.toString(Cal_Distance(Data.now_position , Data.Destination)));

                //測試用顯示文字

                //System.out.println("distance: " + distance);
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
    public boolean Check_Drift(){
        //回傳true，有飄移，回傳false，無飄移
        boolean value = false;
        long pass_time = 0;
        if(start_time==0){
            start_time = System.currentTimeMillis();
        }
        else{
            end_time = System.currentTimeMillis();
            pass_time = end_time - start_time;
            start_time = System.currentTimeMillis();
            long tmp = pass_time;
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    my_layout.setdataviewRecordTimer("經過"+ tmp + "ms");
//                }
//            });
        }
        double drift_speed = Cal_API_Speed(pass_time) - Cal_GPS_Speed(pass_time);
        time = 0;
        if(drift_speed>10){
            value = true;
            Toast("飄移觸發");
        }
        return value;
        //角度判斷-失敗
//        boolean value = false;
//        double drift_angle = Last_Bearing - Now_Bearing;
//        if(drift_angle < 0) { drift_angle = drift_angle + 360; }
//        if(drift_angle>45){ value = true;Toast("飄移觸發");}
//        return value;
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
    private double Cal_Distance(LatLng Start, LatLng End){
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
    public double reverse(double bearing){
        //System.out.println("Before:" + bearing);
        if(bearing >= 180){ bearing = bearing - 180; }
        else{ bearing = bearing + 180; }
        //System.out.println("After: " + bearing);
        return bearing;
    }
    public void Toast(String text){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() { ;
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void Draw_Direction(ArrayList<LatLng> Points){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Remove_Direction();
                PolylineOptions polylineOptions = new PolylineOptions();

                //mMap.addMarker(Navigation_test);
                for(int i=0; i< Points.size();i++){
                    //System.out.println(Points.get(i));
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
    public void Draw_Direction(LatLng point, int start, ArrayList<LatLng> Points){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                Remove_Direction();
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.add(point);
                Navigation_test.position(point);
                mMap.addMarker(Navigation_test);
                for(int i=start; i< Points.size();i++){
                    //System.out.println(Points.get(i));
                    polylineOptions.add(Points.get(i));
                    Navigation_test.position(Points.get(i));
                    mMap.addMarker(Navigation_test);
                }
                polylineOptions.color(context.getResources().getColor(R.color.route_color));
                polylineOptions.width(20f);
                Direction = mMap.addPolyline(polylineOptions);
            }
        });
    }
    public int Cal_GPS_Speed(long pass_time){

        //long pass_time = 0;
        double dis = 0;
        if(last_position==null){
            last_position = Data.now_position;
            //start_Timer();
            //start_time = System.currentTimeMillis();
        }
        else{
            dis = Cal_Method.Cal_Distance(Data.now_position, last_position);
            last_position = Data.now_position;
            //end_time = System.currentTimeMillis();
            //pass_time = end_time-start_time;
            //start_time = System.currentTimeMillis();
            //timer.cancel();

        }
        //int kmh = 0;

//        if(pass_time!=0) {
//            kmh = (((int) dis / (int)(pass_time)) * 1000) * 3600 / 1000;
//        }
        //int kmh = Cal_Speed(dis, pass_time);
//        if(minutes!=0 || seconds!=0) {
//            kmh = ((int) dis / (minutes * 60 + seconds)) * 3600 / 1000;
//            int tmp = kmh;
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    my_layout.setdataviewNowGPSSpeed(Integer.toString(tmp));
//                }
//            });
//
//        }
        //int tmp = kmh;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                //my_layout.setdataviewNowGPSSpeed(Integer.toString(tmp));
//            }
//        });
        return 0;
    }
    public int Cal_API_Speed(long pass_time){

        double dis = 0;
        if(last_API_position==null){
            last_API_position = now_API_position;
        }
        else{
            dis = Cal_Method.Cal_Distance(now_API_position, last_API_position);
            last_API_position = now_API_position;
        }
//        int kmh = 0;
//        if(minutes!=0 || seconds!=0) {
//           kmh = ((int) dis / (minutes * 60 + seconds)) * 3600 / 1000;
//           int tmp = kmh;
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    my_layout.setdataviewNowAPISpeed(Integer.toString(tmp));
//                }
//            });
//        }
        int kmh = Cal_Speed(dis, pass_time);

        int tmp = kmh;
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                my_layout.setdataviewNowAPISpeed(Integer.toString(tmp));
//            }
//        });

        return kmh;
    }
    public int Cal_Speed(double dis, long pass_time){
        int value = 0;
        if(pass_time!=0) {
            Toast("計算速度");
            value = (((int) dis / (int) (pass_time)) * 1000) * 3600 / 1000;
        }
        return value;
    }
    public void Remove_Navigation_MK(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if(Navigation_MK!=null){ Navigation_MK.remove(); }
            }});
    }
    public void Remove_Direction(){
        //System.out.println(Direction.getPoints().size());
        if(Direction!=null) {
            Direction.remove();
        }
    }
    public void Final_Remove_Direction(){
        //System.out.println(Direction.getPoints().size());
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if(Direction!=null) {
                    Direction.remove();
                }
            }});
    }
    public void start_Timer(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        my_layout.setdataviewRecordTimer(getTimerText()+"經過");
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }
    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int milliseconds = rounded;
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours, milliseconds);
    }

    private String formatTime(int seconds, int minutes, int hours, int milliseconds)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds) + " : "+ String.format("%02d",milliseconds);
    }
    public void set_Direction_Camera(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                if (Data.now_position != null && Data.Destination != null) {
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

    //取得值
    public boolean get_initMK() {return initMK;}

    //設定值
    public void set_initMK(boolean value){ initMK = value;}
}
