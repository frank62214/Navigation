package com.example.navigation.My;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.navigation.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.function.LongToIntFunction;

import javax.net.ssl.HttpsURLConnection;
import javax.sql.StatementEvent;

public class My_Snap_Road {
    private String key = "AIzaSyD24I4V9f9ojF9xOe_Oil4upRIoYPGLyeI";
    private String Snap_Road_url_1 = "https://roads.googleapis.com/v1/snapToRoads?path=";
    private String Snap_Road_url_2 = "&key=";

    private String url="";
    private LatLng cal_position;

    public My_Snap_Road(){

    }

    public void setSnapRoadUrl(){
        url = Snap_Road_url_1 + Data.now_position.latitude + "," + Data.now_position.longitude + Snap_Road_url_2 + key;
    }
    public void SearchLocation(final My_Snap_Road.onDataReadyCallback callback) {
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String web_text = run_content(url);
                    get_Location(web_text);
                    callback.onDataReady(cal_position);
//                //callback.onDataReady("New Data");
                }
            };
            //runnable.run();
            Thread t1 = new Thread(runnable);
            t1.start();
        } catch (Exception e) {
            e.printStackTrace();
            //callback.onDataReady();
        }
    }
    private void get_Location(String text){
        try {
            My_Json my_json = new My_Json();
            ArrayList<String> snappedPoints = new ArrayList<String>();
            ArrayList<String> location = new ArrayList<String>();
            ArrayList<String> latitude = new ArrayList<String>();
            ArrayList<String> longitude = new ArrayList<String>();
            my_json.get_json(text, snappedPoints, "snappedPoints");
            my_json.get_json(snappedPoints, location, "location");
            my_json.get_json(location, latitude, "latitude");
            my_json.get_json(location, longitude, "longitude");
            cal_position = new LatLng(Double.parseDouble(latitude.get(0)), Double.parseDouble(longitude.get(0)));
            //my_json.show(latitude);
            //my_json.show(longitude);
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
    private String run_content(String api_url){
        String text = "";
        HttpURLConnection connection = null;
        try {
            System.out.println(api_url);
            // 初始化 URL
            URL url = new URL(api_url);
            // 取得連線物件
            connection = (HttpURLConnection) url.openConnection();
            // 設定 request timeout
            connection.setReadTimeout(1500);
            connection.setConnectTimeout(1500);
            // 模擬 Chrome 的 user agent, 因為手機的網頁內容較不完整
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
            // 設定開啟自動轉址
            connection.setInstanceFollowRedirects(true);

            // 若要求回傳 200 OK 表示成功取得網頁內容
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                // 讀取網頁內容
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String tempStr;
                StringBuffer stringBuffer = new StringBuffer();

                while ((tempStr = bufferedReader.readLine()) != null) {
                    stringBuffer.append(tempStr);
                }

                bufferedReader.close();
                inputStream.close();

                // 取得網頁內容類型
                String mime = connection.getContentType();
                boolean isMediaStream = false;

                // 判斷是否為串流檔案
                if (mime.indexOf("audio") == 0 || mime.indexOf("video") == 0) {
                    isMediaStream = true;
                }

                // 網頁內容字串
                String responseString = stringBuffer.toString();

                text = responseString;
                //System.out.println(web_text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 中斷連線
            if (connection != null) {
                connection.disconnect();
            }
        }
        return text;
    }

    public interface onDataReadyCallback{
        void onDataReady(LatLng data);
    }
}
