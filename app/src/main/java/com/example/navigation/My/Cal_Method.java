package com.example.navigation.My;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
    public static double reverse(double bearing){
        //System.out.println("Before:" + bearing);
        if(bearing >= 180){ bearing = bearing - 180; }
        else{ bearing = bearing + 180; }
        //System.out.println("After: " + bearing);
        return bearing;
    }
    public static boolean Navigation_Drift(LatLng API, LatLng Cal){
        //回傳true代表有觸發飄移
        //LatLng select = API;
        if(Cal_Distance(API, Cal)>2){
            Navigation_Catch_Log("API飄移，使用計算後的",API,Cal);
            //select = Cal;
            return true;
        }
        else {
            Navigation_Catch_Log("使用API", API, Cal);
            return false;
        }
    }
    public static LatLng Drift(LatLng GPS, LatLng API, LatLng Cal){
        LatLng select = API;
        if(Cal_Distance(GPS, API)>1){
            Catch_Log("GPS飄移，使用API",GPS,API,Cal);
        }
        if(Cal_Distance(API, Cal)>1){
            Catch_Log("API飄移，使用計算後的",GPS,API,Cal);
            select = Cal;
        }
        return select;
    }

    //取得自製圖片
    public static BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
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
    //polyline解碼
    public static ArrayList<LatLng> PolyLine_Decoder(ArrayList<String> points){
        ArrayList<LatLng> polyline = new ArrayList<LatLng>();
        for (int i = 0; i < points.size(); i++) {
            String encoded = points.get(i);
            int index = 0, len = encoded.length();
            int decoded_lat = 0;
            int decoded_lng = 0;
            //get one char in loop
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    //get on char to calculate decoder
                    b = encoded.charAt(index++);
                    //step 1: number reduce 63
                    b = b - 63;
                    //step 2: number logic operation(AND) 0x1f and then left shift one bit
                    result |= (b & 0x1f) << shift;
                    //step 3: five bit for one block
                    shift += 5;
                } while (b >= 0x20);
                //step 4: if first bit is one need to bit upside down, and do shift on right one bit.
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                decoded_lat += dlat;
                shift = 0;
                result = 0;
                //do the same thing with lng
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                decoded_lng += dlng;
                LatLng p = new LatLng((((double) decoded_lat / 1E5)), (((double) decoded_lng / 1E5)));
                polyline.add(p);
            }
        }
        return  polyline;
    }

    public static void Catch_Error_Log(String function_name, String error){
        String date = "yyyyMMdd";
        SimpleDateFormat date_df = new SimpleDateFormat(date);
        date_df.applyPattern(date);
        String filename = date_df.format(new Date()) + "-errorlog" + ".txt";
        // 存放檔案位置在 內部空間/Download/
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        try
        {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output = new FileOutputStream(file, true);
            String string = function_name + "," + error + "\n";
            Output.write(string.getBytes());
            Output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void Navigation_Catch_Log(String s, LatLng API, LatLng Cal){
        String date = "yyyyMMdd";
        SimpleDateFormat date_df = new SimpleDateFormat(date);
        date_df.applyPattern(date);
        String filename = date_df.format(new Date()) + "Navigation-log" + ".txt";
        //String filename = type + ".txt";
        // 存放檔案位置在 內部空間/Download/
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        try
        {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output = new FileOutputStream(file, true);

            String dateformat = "kk:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            df.applyPattern(dateformat);
            //String string =  df.format(new Date()) + " : " + type + "\n";
            //String string =  df.format(new Date()) + " : " + data.get(0)  + "\n";
            String string = "";
            //string = string + df.format(new Date()) + "," + data.get(i) + "\n";
            string = string + df.format(new Date()) + ",";
            string = string + s + ",";
            string = string + "API" + "," + API   + ",";
            string = string + "Cal" + "," + Cal + "\n";

            Output.write(string.getBytes());
            Output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void Catch_Log(String s, LatLng GPS, LatLng API, LatLng Cal){
        String date = "yyyyMMdd";
        SimpleDateFormat date_df = new SimpleDateFormat(date);
        date_df.applyPattern(date);
        String filename = date_df.format(new Date()) + "-log" + ".txt";
        //String filename = type + ".txt";
        // 存放檔案位置在 內部空間/Download/
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        try
        {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output = new FileOutputStream(file, true);

            String dateformat = "kk:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            df.applyPattern(dateformat);
            //String string =  df.format(new Date()) + " : " + type + "\n";
            //String string =  df.format(new Date()) + " : " + data.get(0)  + "\n";
            String string = "";
            //string = string + df.format(new Date()) + "," + data.get(i) + "\n";
            string = string + df.format(new Date()) + ",";
            string = string + s + ",";
            string = string + "GPS" + "," + GPS + ",";
            string = string + "API" + "," + API   + ",";
            string = string + "Cal" + "," + Cal + "\n";

            Output.write(string.getBytes());
            Output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static void Store_Dis(double dis, int step){
        String date = "yyyyMMdd";
        SimpleDateFormat date_df = new SimpleDateFormat(date);
        date_df.applyPattern(date);
        String filename = date_df.format(new Date()) + "-dis" + ".txt";
        //String filename = type + ".txt";
        // 存放檔案位置在 內部空間/Download/
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, filename);
        try
        {
            // 第二個參數為是否 append
            // 若為 true，則新加入的文字會接續寫在文字檔的最後
            FileOutputStream Output = new FileOutputStream(file, true);

            String dateformat = "kk:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(dateformat);
            df.applyPattern(dateformat);
            //String string =  df.format(new Date()) + " : " + type + "\n";
            //String string =  df.format(new Date()) + " : " + data.get(0)  + "\n";
            String string = "";
            //string = string + df.format(new Date()) + "," + data.get(i) + "\n";
            string = string + df.format(new Date()) + ",";
            string = string + dis + ", " + step + "\n";

            Output.write(string.getBytes());
            Output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
