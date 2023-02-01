package com.example.navigation.My;

import com.example.navigation.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class My_Direction {
    private String key = "AIzaSyBiflQ57CcbDWXozIQdCLT8Ue5xMBwe8FY";

    private ArrayList<LatLng> direction = new ArrayList<LatLng>();

    private String Direction_url_1 = "https://maps.googleapis.com/maps/api/directions/json?destination=";
    private String Direction_url_2 = "&mode=";
    private String Direction_url_3 = "&origin=";
    private String Direction_url_4 = "&language=zh-TW&key=";

    private String url = "";
    private int dis;
    private LatLng callback_point;

    //private String web_text = "";
    public My_Direction(){

    }
    public void searchDirection(){
        String origin      = Data.now_position.latitude + "," + Data.now_position.longitude;
        String destination = Data.Destination.latitude + "," + Data.Destination.longitude;
        //System.out.println(origin);
        //System.out.println(destination);
        String mode = Data.Mode;
        url = Direction_url_1 + destination + Direction_url_2 + mode + Direction_url_3 + origin;
        url = url + Direction_url_4 + key;
        direction.remove(direction);
        //if(Data.Decoder_Steps!=null){Data.Decoder_Steps.removeAll(Data.Decoder_Steps);}
    }
    public void searchDirection(LatLng point){
        String origin      = point.latitude + "," + point.longitude;
        String destination = Data.Destination.latitude + "," + Data.Destination.longitude;
        //System.out.println(origin);
        //System.out.println(destination);
        String mode = Data.Mode;
        url = Direction_url_1 + destination + Direction_url_2 + mode + Direction_url_3 + origin;
        url = url + Direction_url_4 + key;
        direction.remove(direction);
        //if(Data.Decoder_Steps!=null){Data.Decoder_Steps.removeAll(Data.Decoder_Steps);}
    }
    public void setDistanceUrl(LatLng start, LatLng end){
        //int ans = 0;
        String origin      = start.latitude+ "," + start.longitude;
        String destination = end.latitude + "," + end.longitude;
        String mode = Data.Mode;
        url = Direction_url_1 + destination + Direction_url_2 + mode + Direction_url_3 + origin;
        url = url + Direction_url_4 + key;

        //return ans;
    }

    public void SearchDistance(final onDataReadyCallback callback){
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String web_text = run_content(url);
                    get_Distance(web_text);
                    callback.onDisReady(dis);
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
    public void SearchNavigationData(final onNavigationDataReadyCallBack callBack){
        try{
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String web_text = run_content(url);
                    callBack.onDataReady(web_text);
                }
            };
            //runnable.run();
            Thread t1 = new Thread(runnable);
            t1.start();
        }catch (Exception e){
            Cal_Method.Catch_Error_Log("SearchNavigationDataNew", e.toString());
        }
    }
    public void SearchNavigationData(final onDataReadyCallback callback){
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String web_text = run_content(url);
                    get_Navigation(web_text);
                    callback.onDataReady(direction);
                    LatLng start = direction.get(0);
                    LatLng end = direction.get(1);
                    callback.onStartLocationReady(start, end);
                    get_Distance(web_text);
                    callback.onDisReady(dis);
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
    public void SearchData(final onDataReadyCallback callback){
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String web_text = run_content(url);
                    get_Direction(web_text);
                    callback.onDataReady(direction);
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
    private void get_Distance(String text){
        try {
            My_Json my_json = new My_Json();

            //取得距離
            ArrayList<String> routes = new ArrayList<String>();
            ArrayList<String> legs = new ArrayList<String>();
            ArrayList<String> steps = new ArrayList<String>();
            ArrayList<String> distance = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
            my_json.get_json(text, routes, "routes");
            my_json.get_json(routes, legs, "legs");
            my_json.get_json(legs, steps, "steps");
            my_json.get_json(steps, distance, "distance");
            //System.out.println("Direction Dis: " + distance);
            my_json.get_json(distance, value, "value");
            dis = Integer.parseInt(value.get(0));
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
    private void get_Direction(String text){
        try {
            My_Json my_json = new My_Json();
            //取得OverView PolyLine
            ArrayList<String> routes = new ArrayList<String>();
            ArrayList<String> polyline_overview = new ArrayList<String>();
            ArrayList<String> points = new ArrayList<String>();
            my_json.get_json(text, routes, "routes");
            my_json.get_json(routes, polyline_overview, "overview_polyline");
            my_json.get_json(polyline_overview, points, "points");
            Polyline_decoder(points, direction);
            if(Data.Decoder_Steps.size()!=0){
                Data.Decoder_Steps.removeAll(Data.Decoder_Steps);
            }
            Polyline_decoder(points, Data.Decoder_Steps);
            //取得Direction 細節
            ArrayList<String> legs = new ArrayList<String>();
            ArrayList<String> steps = new ArrayList<String>();
            ArrayList<String> end_location_s = new ArrayList<String>();
            ArrayList<String> html_instrustions = new ArrayList<String>();
            ArrayList<String> html_instrustions_tmp = new ArrayList<String>();
            ArrayList<String> maneuver = new ArrayList<String>();
            ArrayList<String> lat = new ArrayList<String>();
            ArrayList<String> lng = new ArrayList<String>();
            //get every point
            my_json.get_json(routes, legs, "legs");
            my_json.get_json(legs, steps, "steps");
            my_json.get_json(steps, end_location_s, "end_location");
            my_json.get_json(steps, html_instrustions_tmp, "html_instructions");
            //取得Lat Lng
            //my_json.get_json(Start_location_s, lat, "lat");
            my_json.get_json(end_location_s, lat, "lat");
            my_json.get_json(end_location_s, lng, "lng");


            Store_Step(lat, lng);
            Store_Road(html_instrustions_tmp);
            //my_json.show(html_instrustions_tmp);
        }
        catch (Exception e){
            System.out.println(e.toString());
        }

    }
    public void get_Navigation(String text){
        try {
            My_Json my_json = new My_Json();
            //取得OverView PolyLine
            ArrayList<String> routes = new ArrayList<String>();
            ArrayList<String> polyline_overview = new ArrayList<String>();
            ArrayList<String> points = new ArrayList<String>();
            my_json.get_json(text, routes, "routes");
            my_json.get_json(routes, polyline_overview, "overview_polyline");
            my_json.get_json(polyline_overview, points, "points");
            Polyline_decoder(points, direction);
            System.out.println(direction.size());

            //Data.Decoder_Steps = direction;
            //Polyline_decoder(points, Data.Decoder_Steps);

            //取得Direction 細節
            ArrayList<String> legs = new ArrayList<String>();
            ArrayList<String> steps = new ArrayList<String>();
            ArrayList<String> end_location_s = new ArrayList<String>();
            ArrayList<String> start_location_s = new ArrayList<String>();
            ArrayList<String> html_instrustions = new ArrayList<String>();
            ArrayList<String> html_instrustions_tmp = new ArrayList<String>();
            ArrayList<String> lat = new ArrayList<String>();
            ArrayList<String> lng = new ArrayList<String>();
            //get every point
            my_json.get_json(routes, legs, "legs");
            my_json.get_json(legs, steps, "steps");
            my_json.get_json(steps, start_location_s, "start_location");
            my_json.get_json(steps, end_location_s, "end_location");
            my_json.get_json(steps, html_instrustions_tmp, "html_instructions");
            //取得Lat Lng
            //my_json.get_json(Start_location_s, lat, "lat");
            //my_json.get_json(end_location_s, lat, "lat");
            //my_json.get_json(end_location_s, lng, "lng");
            //取得Lat Lng
            //my_json.get_json(Start_location_s, lat, "lat");
            my_json.get_json(start_location_s, lat, "lat");
            my_json.get_json(start_location_s, lng, "lng");


            Store_Step(lat, lng);
            Store_Road(html_instrustions_tmp);
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
    }
    public void Store_Road(ArrayList<String> text){
        if(Data.Road!=null){ Data.Road.removeAll(Data.Road); }
        if(Data.Road_Detail!=null){ Data.Road_Detail.removeAll(Data.Road_Detail); }
        ArrayList<String> new_text = new ArrayList<String>();
        ArrayList<String> road = new ArrayList<String>();
        ArrayList<String> road_detail = new ArrayList<String>();
        String text_tmp = "";
        for(int i=0; i<text.size(); i++){
            //System.out.println(text.get(i));
            text_tmp = text.get(i).replace("/", "");
            String[] tmp = text_tmp.split("<b>");
            String merge = "";
            for(int j=0; j<tmp.length; j++){
                //System.out.println(tmp[j]);
                merge = merge + tmp[j];
                if(tmp[j].contains("路") || tmp[j].contains("街")){
                    road.add(tmp[j]);
                }
            }
            road_detail.add(merge);
        }
        Data.Road        = delete_english(delete_more(road));
        Data.Road_Detail = delete_english(delete_more(road_detail));
    }
    public void Store_Step(ArrayList<String> lat, ArrayList<String> lng){
        if(Data.Steps!=null){ Data.Steps.removeAll(Data.Steps); }
        ArrayList<LatLng> ans = new ArrayList<LatLng>();
        for(int i=0; i<lat.size(); i++){
            double Lat = Double.parseDouble(lat.get(i));
            double Lng = Double.parseDouble(lng.get(i));
            LatLng tmp = new LatLng(Lat, Lng);
            ans.add(tmp);
        }
        Data.Steps = ans;
    }
    private ArrayList<String> delete_more(ArrayList<String> list){
        ArrayList<String> ans = new ArrayList<String>();
        for(int i=0; i<list.size(); i++) {
            String text = list.get(i);
            text = text.replace("/<wbr/>", "");
            text = text.replace("<", "");
            text = text.replace(">", "");
            text = text.replace("=", "");
            text = text.replace("-", "");
            text = text.replace(":", "");
            text = text.replace(".", "");
            text = text.replace("\"", "");
            text = text.replace("(", "");
            text = text.replace(")", "");
            text = text.replace("/", "");

            ans.add(text);
        }
        return ans;
    }

    private ArrayList<String> delete_english(ArrayList<String> list){
        ArrayList<String> ans = new ArrayList<String >();
        for(int i=0; i<list.size();i++) {
            //ans = text.replace("2", "");
            String text = list.get(i);
            text = text.replace(" ", "");
            //ans = ans.replace(direction, direction+" ");
//            for (int j = 0; j < 10; j++) {
//                //System.out.println(i);
//                String test = String.valueOf(j);
//                text = text.replace(test, "");
//            }
            //System.out.println(ans);
            for (int j = 0; j < 26; j++) {
                int num = j + 65; //A
                String test = Character.toString((char) num);
                text = text.replace(test, "");
            }
            for (int j = 0; j < 26; j++) {
                int num = j + 97; //a
                String test = Character.toString((char) num);
                text = text.replace(test, "");
            }
            ans.add(text);
        }
        return ans;
    }
    public static void Polyline_decoder(ArrayList<String> list, ArrayList<LatLng> Poly_List) {
        //get all the polylines point
        for (int i = 0; i < list.size(); i++) {
            String encoded = list.get(i);
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
                Poly_List.add(p);
            }
        }
    }
    interface onDataReadyCallback{
        void onDataReady(ArrayList<LatLng> data);
        void onDisReady(int dis);
        void onStartLocationReady(LatLng start, LatLng end);
    }
    interface onNavigationDataReadyCallBack{
        void onDataReady(String text);
    }

}
