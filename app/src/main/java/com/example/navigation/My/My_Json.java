package com.example.navigation.My;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class My_Json {


    //取得API回應到下個轉彎處距離值
    public static int Get_Next_Step_Distance(String text){
        int dis = 0;
        try {
            ArrayList<String> routes = new ArrayList<String>();
            ArrayList<String> legs = new ArrayList<String>();
            ArrayList<String> steps = new ArrayList<String>();
            ArrayList<String> distance = new ArrayList<String>();
            ArrayList<String> value = new ArrayList<String>();
            get_json(text, routes, "routes");
            get_json(routes, legs, "legs");
            get_json(legs, steps, "steps");
            get_json(steps, distance, "distance");
            //System.out.println("Direction Dis: " + distance);
            get_json(distance, value, "value");
            dis = Integer.parseInt(value.get(0));
        }
        catch (Exception e){
            Cal_Method.Catch_Error_Log("Get_Next_Step_Distance", e.toString());
        }
        return dis;
    }
    //取得導航用的overview_polyline
    public static ArrayList<LatLng> Get_Navigation_OverView_PolyLine(String text) {
        ArrayList<LatLng> Direction = new ArrayList<LatLng>();
        try {
            //取得OverView PolyLine
            ArrayList<String> routes = new ArrayList<String>();
            ArrayList<String> polyline_overview = new ArrayList<String>();
            ArrayList<String> points = new ArrayList<String>();
            get_json(text, routes, "routes");
            get_json(routes, polyline_overview, "overview_polyline");
            get_json(polyline_overview, points, "points");
            Direction = Cal_Method.PolyLine_Decoder(points);
        } catch (Exception e) {
            Cal_Method.Catch_Error_Log("Get_Navigation_OverView_PolyLine", e.toString());
        }
        return Direction;
    }
    public static ArrayList<LatLng> Get_Navigation_OverView_PolyLine_1(ArrayList<ArrayList<LatLng>> steps){
        ArrayList<LatLng> polyline = new ArrayList<LatLng>();

        for(int i=0; i<steps.size(); i++){
            polyline.addAll(steps.get(i));
        }
        return polyline;
    }
    public static ArrayList<String> Get_Navigation_Turn(String text){
        ArrayList<String> Turn = new ArrayList<String>();
        //取得Direction 細節
        ArrayList<String> routes = new ArrayList<String>();
        ArrayList<String> legs = new ArrayList<String>();
        ArrayList<String> steps = new ArrayList<String>();
        ArrayList<String> end_location_s = new ArrayList<String>();
        ArrayList<String> start_location_s = new ArrayList<String>();
        ArrayList<String> html_instrustions = new ArrayList<String>();
        ArrayList<String> html_instrustions_tmp = new ArrayList<String>();
        //get every point
        get_json(text, routes, "routes");
        get_json(routes, legs, "legs");
        get_json(legs, steps, "steps");
        get_json(steps, start_location_s, "start_location");
        get_json(steps, end_location_s, "end_location");
        get_json(steps, html_instrustions_tmp, "html_instructions");
        Turn = Get_Turn(html_instrustions_tmp);
        return Turn;
    }
    //取得導航用的路名
    public static ArrayList<String> Get_Navigation_Road(String text){
        ArrayList<String> Road = new ArrayList<String>();
        //取得Direction 細節
        ArrayList<String> routes = new ArrayList<String>();
        ArrayList<String> legs = new ArrayList<String>();
        ArrayList<String> steps = new ArrayList<String>();
        ArrayList<String> end_location_s = new ArrayList<String>();
        ArrayList<String> start_location_s = new ArrayList<String>();
        ArrayList<String> html_instrustions = new ArrayList<String>();
        ArrayList<String> html_instrustions_tmp = new ArrayList<String>();
        //get every point
        get_json(text, routes, "routes");
        get_json(routes, legs, "legs");
        get_json(legs, steps, "steps");
        get_json(steps, start_location_s, "start_location");
        get_json(steps, end_location_s, "end_location");
        get_json(steps, html_instrustions_tmp, "html_instructions");

        Road = Get_Road(html_instrustions_tmp);
        return Road;
    }
    //取得導航用的路名細節
    public static ArrayList<String> Get_Navigation_Road_Detail(String text){
        ArrayList<String> Road_Detail = new ArrayList<String>();
        //取得Direction 細節
        ArrayList<String> routes = new ArrayList<String>();
        ArrayList<String> legs = new ArrayList<String>();
        ArrayList<String> steps = new ArrayList<String>();
        ArrayList<String> end_location_s = new ArrayList<String>();
        ArrayList<String> start_location_s = new ArrayList<String>();
        ArrayList<String> html_instrustions = new ArrayList<String>();
        ArrayList<String> html_instrustions_tmp = new ArrayList<String>();
        //get every point
        get_json(text, routes, "routes");
        get_json(routes, legs, "legs");
        get_json(legs, steps, "steps");
        get_json(steps, start_location_s, "start_location");
        get_json(steps, end_location_s, "end_location");
        get_json(steps, html_instrustions_tmp, "html_instructions");

        Road_Detail = Get_Road_Detail(html_instrustions_tmp);
        return Road_Detail;
    }
    public static ArrayList<LatLng> Get_Steps(String text){
        ArrayList<LatLng> Steps = new ArrayList<LatLng>();

        ArrayList<String> routes = new ArrayList<String>();
        ArrayList<String> legs = new ArrayList<String>();
        ArrayList<String> steps = new ArrayList<String>();
        ArrayList<String> end_location_s = new ArrayList<String>();
        ArrayList<String> start_location_s = new ArrayList<String>();
        ArrayList<String> lat = new ArrayList<String>();
        ArrayList<String> lng = new ArrayList<String>();
        //get every point
        get_json(text, routes, "routes");
        get_json(routes, legs, "legs");
        get_json(legs, steps, "steps");
        get_json(steps, start_location_s, "start_location");
        get_json(steps, end_location_s, "end_location");
        //取得Lat Lng
        //my_json.get_json(Start_location_s, lat, "lat");
        //my_json.get_json(end_location_s, lat, "lat");
        //my_json.get_json(end_location_s, lng, "lng");
        //取得Lat Lng
        //my_json.get_json(Start_location_s, lat, "lat");
        //get_json(start_location_s, lat, "lat");
        //get_json(start_location_s, lng, "lng");
        get_json(end_location_s, lat, "lat");
        get_json(end_location_s, lng, "lng");

        Steps = Store_Step(lat, lng);
        return Steps;
    }
    public static ArrayList<ArrayList<LatLng>> Get_Navigation_PolyLine_Step(String text){
        ArrayList<ArrayList<LatLng>> polylinesteps = new ArrayList<>();


        ArrayList<String> routes = new ArrayList<String>();
        ArrayList<String> legs = new ArrayList<String>();
        ArrayList<String> steps = new ArrayList<String>();
        ArrayList<String> polyline = new ArrayList<>();
        ArrayList<String> points = new ArrayList<String>();


        //get every point
        get_json(text, routes, "routes");
        get_json(routes, legs, "legs");
        get_json(legs, steps, "steps");
        get_json(steps, polyline, "polyline");
        get_json(polyline, points, "points");
        for(int i=0; i< points.size(); i++){
            ArrayList<LatLng> Steps = new ArrayList<LatLng>();
            Steps = Cal_Method.PolyLine_Decoder(points.get(i));
            polylinesteps.add(Steps);
        }
        //show(points);
        return polylinesteps;
    }
    //取得路名
    public static ArrayList<String> Get_Road(ArrayList<String> text){
        ArrayList<String> new_text = new ArrayList<String>();
        ArrayList<String> road = new ArrayList<String>();
        ArrayList<String> road_detail = new ArrayList<String>();
        String text_tmp = "";

        for(int i=0; i<text.size(); i++){
            //System.out.println(text.get(i));
            boolean once = true;
            text_tmp = text.get(i).replace("/", "");
            String[] tmp = text_tmp.split("<b>");
            String merge = "";
            for(int j=0; j<tmp.length; j++){
                //System.out.println(tmp[j]);
                merge = merge + tmp[j];
                if(!tmp[j].contains("十字路")){
                    if (tmp[j].contains("路") || tmp[j].contains("街")) {
                        if (once) {
                            //System.out.println(tmp[j]);
                            once = false;
                            road.add(tmp[j]);
                        }
                    }
                }
            }
            if(once){
                once = false;
                if(text_tmp.contains("左")){
                    road.add("向左轉");
                }
                if(text_tmp.contains("右")){
                    road.add("向右轉");
                }
            }
            System.out.println("-------------------");

            road_detail.add(merge);
        }
        return road;
        //Data.Road        = delete_english(delete_more(road));
        //Data.Road_Detail = delete_english(delete_more(road_detail));
    }
    //取得路名細節
    public static ArrayList<String> Get_Road_Detail(ArrayList<String> text){
        ArrayList<String> new_text = new ArrayList<String>();
        ArrayList<String> road = new ArrayList<String>();
        ArrayList<String> road_detail = new ArrayList<String>();
        String text_tmp = "";
        for(int i=0; i<text.size(); i++){

            text_tmp = text.get(i).replace("<b>", "");
            text_tmp = text_tmp.replace("</b>", "");
            text_tmp = text_tmp.replace("/<wbr/>", "");
            text_tmp = text_tmp.replace("</div>", "");
            text_tmp = text_tmp.replace("<div style=\"font-size:0.9em\">", "");
//            System.out.println(text_tmp);
//            String[] tmp = text_tmp.split("<b>");
//            String merge = "";
//            for(int j=0; j<tmp.length; j++){
//                //System.out.println(tmp[j]);
//                merge = merge + tmp[j];
//                if(tmp[j].contains("路") || tmp[j].contains("街")){
//                    road.add(tmp[j]);
//                }
//            }
//            road_detail.add(merge);
            road_detail.add(text_tmp);
        }
        return road_detail;
    }
    //取得路名
    public static ArrayList<String> Get_Turn(ArrayList<String> text){
        ArrayList<String> turn = new ArrayList<String>();
        String text_tmp = "";
        for(int i=0; i<text.size(); i++){

            text_tmp = text.get(i).replace("<b>", "");
            text_tmp = text_tmp.replace("</b>", "");
            //System.out.println(text_tmp);
            boolean once = true;
            if(once) {
                String tmp = "";
                once = false;
                if(text_tmp.contains("向右轉")){
                    tmp = "向右轉";
                }
                if(text_tmp.contains("向右急轉")){
                    tmp = "向右急轉";
                }
                if(text_tmp.contains("靠右")){
                    tmp = "靠右";
                }
                if(text_tmp.contains("向左轉")){
                    tmp = "向左轉";
                }
                if(text_tmp.contains("向左急轉")){
                    tmp = "向左急轉";
                }
                if(text_tmp.contains("靠左")){
                    tmp = "靠左";
                }
                if(text_tmp.contains("前進")){
                    tmp = "前進";
                }
                if(text_tmp.contains("迴轉")){
                    tmp = "迴轉";
                }
//                if(text_tmp.contains("目的地在右邊")){
//                    tmp = "目的地在右邊";
//                }
//                if(text_tmp.contains("目的地在左邊")){
//                    tmp = "目的地在左邊";
//                }
                //System.out.println(tmp);
                turn.add(tmp);
            }
        }
        //System.out.println(turn);
        return turn;
        //Data.Road        = delete_english(delete_more(road));
        //Data.Road_Detail = delete_english(delete_more(road_detail));
    }
    public static ArrayList<LatLng> Store_Step(ArrayList<String> lat, ArrayList<String> lng){
        ArrayList<LatLng> steps = new ArrayList<LatLng>();
        for(int i=0; i<lat.size(); i++){
            double Lat = Double.parseDouble(lat.get(i));
            double Lng = Double.parseDouble(lng.get(i));
            LatLng tmp = new LatLng(Lat, Lng);
            steps.add(tmp);
        }
        return steps;
    }
    //通用方法
    public static void get_json(String text, ArrayList<String> arraylist, String tag){
        //JsonConfig conf = new JsonConfig();
        //往JSONArray中新增JSONObject物件。
        //發現JSONArray跟JSONObject的區別就是JSONArray比JSONObject多中括號[]
        if (text.charAt(0) != '['){
            text = "[" + text + "]";
        }

        try {
            //建立一個JSONArray並帶入JSON格式文字，getString(String key)取出欄位的數值
            JSONArray array = new JSONArray(text);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                arraylist.add(json.getString(tag));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }
    public static void get_json(ArrayList<String> text, ArrayList<String> arraylist, String tag){
        //JsonConfig conf = new JsonConfig();
        //往JSONArray中新增JSONObject物件。
        //發現JSONArray跟JSONObject的區別就是JSONArray比JSONObject多中括號[]
        for(int i=0 ;i <text.size();i++) {
            String tmp = text.get(i);
            if (tmp.charAt(0) != '[') {
                tmp= "[" + tmp + "]";
            }
            try {
                //建立一個JSONArray並帶入JSON格式文字，getString(String key)取出欄位的數值
                JSONArray array = new JSONArray(tmp);
                for (int j = 0; j < array.length(); j++) {
                    JSONObject json = array.getJSONObject(j);
                    //String ch = delete_english(json.getString(tag));
                    //arraylist.add(ch);
                    arraylist.add(json.getString(tag));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public static void show(ArrayList arrayList){
        for(int i=0; i<arrayList.size();i++){
            System.out.println(i + " : " + arrayList.get(i));
        }
    }
    public static void show1(ArrayList<ArrayList<LatLng>> arrayList){
        for(int i=0; i<arrayList.size();i++){
            for(int j=0; j < arrayList.get(i).size(); j++){
                System.out.println(i + " : " + arrayList.get(i).get(j));
            }
        }
    }
    public static void show_detail(ArrayList<String> a, ArrayList<String> b, ArrayList<String> c){
        for(int i=0; i<a.size();i++){
            System.out.println(i + " : " + a.get(i) + " " + b.get(i) + " " + c.get(i));
        }
    }

}
