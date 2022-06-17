package com.example.navigation.My;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class My_Json {
    public void get_json(String text, ArrayList<String> arraylist, String tag){
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
    public void get_json(ArrayList<String> text, ArrayList<String> arraylist, String tag){
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
    public void show(ArrayList arrayList){
        for(int i=0; i<arrayList.size();i++){
            System.out.println(arrayList.get(i));
        }
    }


}
