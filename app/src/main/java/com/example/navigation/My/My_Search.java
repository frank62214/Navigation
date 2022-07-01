package com.example.navigation.My;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class My_Search {
    private String key = "AIzaSyBm6kC5U0Y_k3lfmggPRurC0C3o3wiUlA0";

    private String Search_url_1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?location=";   //+lat
    private String Search_url_2 = ",";                                                                      //+lng
    private String Search_url_3 = "&language=zh-TW&query=";                                                                //+text
    private String Search_url_4 = "&radius=10000&key=";

    private String url="";

    private ArrayList<String> Places = new ArrayList<String>();
    private ArrayList<LatLng> Places_location = new ArrayList<LatLng>();
    public My_Search(){

    }
    public void SearchDestination(String dest){
        url = Search_url_1 + Data.now_position.latitude + Search_url_2 + Data.now_position.longitude + Search_url_3;
        url = url + dest + Search_url_4 + key;
    }
    public void SearchData(final onDataReadyCallback callback){
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String web_text = run_content(url);
                    get_Places(web_text);
                    callback.onDataNameReady(Places, Places_location);
//                //callback.onDataReady("New Data");
                }
            };
            //runnable.run();
            Thread t1 = new Thread(runnable);
            t1.start();
        }
        catch(Exception e) {
            e.printStackTrace();
            //callback.onDataReady();
        }
    }


    private String run_content(String api_url) {
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

    private void get_Places(String text){
        My_Json my_json = new My_Json();
        ArrayList<String> results  = new ArrayList<String>();
        ArrayList<String> geometry = new ArrayList<String>();
        ArrayList<String> name     = new ArrayList<String>();
        ArrayList<String> location = new ArrayList<String>();
        ArrayList<String> lat      = new ArrayList<String>();
        ArrayList<String> lng      = new ArrayList<String>();
        my_json.get_json(text, results, "results");
        my_json.get_json(results, geometry, "geometry");
        my_json.get_json(geometry, location, "location");
        my_json.get_json(location, lat, "lat");
        my_json.get_json(location, lng, "lng");

        my_json.get_json(results, name, "name");


        Places = name;
        Places_location = Store_Location(lat, lng);
        //my_json.show(Places_location);
    }
    private ArrayList<LatLng> Store_Location(ArrayList<String> lat, ArrayList<String> lng){
        ArrayList<LatLng> tmp = new ArrayList<LatLng>();
        for(int i=0; i<lat.size();i++) {
            LatLng location = new LatLng(Double.parseDouble(lat.get(i)), Double.parseDouble(lng.get(i)));
            tmp.add(location);
        }
        return tmp;
    }
    interface onDataReadyCallback{
        void onDataNameReady(ArrayList<String> data, ArrayList<LatLng>location);

    }
}
