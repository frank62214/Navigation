package com.example.navigation.My;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RouteData {
    public ArrayList<LatLng> Routes = new ArrayList<LatLng>();
    public ArrayList<ArrayList<LatLng>> PolyLineSteps = new ArrayList<ArrayList<LatLng>>();
    public ArrayList<LatLng> firstSteps = new ArrayList<LatLng>();
    public float Bearing = 0;
    public LatLng Position;
    public double firstStepsDis = 0;
    public LatLng nowDestination;
    public double count;

    public void StoreData(String text, int num){
        Routes = My_Json.Get_Steps(text);
        PolyLineSteps = My_Json.Get_Navigation_PolyLine_Step(text);
        nowDestination = Routes.get(0);
        System.out.println("nowDestination1" + nowDestination);
//        if(PolyLineSteps.size()<15) {
//            nowDestination = Routes.get(2);
//        }
//        else{
//            nowDestination = Routes.get(1);
//        }
        firstSteps = PolyLineSteps.get(num);
        Bearing = calBearing(firstSteps.get(num), firstSteps.get(num+1));
        Position = firstSteps.get(num);
        firstStepsDis = calDistance(firstSteps.get(num), firstSteps.get(num+1));
    }
    private float calBearing(LatLng p1, LatLng p2){
        return Cal_Method.Cal_Bearing(p1, p2);
    }
    private double calDistance(LatLng last, LatLng now){
        return Cal_Method.Cal_Distance(last, now);
    }
}
