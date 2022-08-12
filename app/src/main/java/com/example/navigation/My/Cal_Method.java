package com.example.navigation.My;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.LatLng;

public class Cal_Method {
    public static double Cal_Distance(LatLng Start, LatLng End){
        //計算兩點之間的距離，單位m。
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
    public static float Cal_Bearing(LatLng Start, LatLng End){
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
    public static LatLng Cal_LatLng(LatLng Start, double bearing){
        LatLng camera = new LatLng(0,0);
        //距離單位為km
        double distance = 0.05;
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
    public static LatLng Cal_LatLng(LatLng Start, double bearing, double dis){
        LatLng camera = new LatLng(0,0);
        //距離單位為km
        //double distance = 0.05;
        double distance = dis / 1000;
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
    public static boolean Cal_Check_Drift(){
        //回傳true，有飄移，回傳false，無飄移
        boolean value = false;
        long pass_time = 0;
        return value;
        //角度判斷-失敗
//        boolean value = false;
//        double drift_angle = Last_Bearing - Now_Bearing;
//        if(drift_angle < 0) { drift_angle = drift_angle + 360; }
//        if(drift_angle>45){ value = true;Toast("飄移觸發");}
//        return value;
    }

}
