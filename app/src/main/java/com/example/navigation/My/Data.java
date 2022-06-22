package com.example.navigation.My;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Data {

    public static boolean Init_Camera = true;

    //location data
    public static LatLng now_position;
    public static float now_bearing;
    public static LatLng Destination;

    //Direction data
    public static ArrayList<LatLng> Steps       = new ArrayList<LatLng>();
    public static ArrayList<String> Road        = new ArrayList<String>();
    public static ArrayList<String> Road_Detail = new ArrayList<String>();

    //Navigation data
    public static LatLng Navigation_Location;
}
