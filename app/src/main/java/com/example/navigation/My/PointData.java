package com.example.navigation.My;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class PointData {
    public LatLng now;
    public LatLng last;
    public ArrayList<LatLng> history = new ArrayList<LatLng>();
    public int count = 0;


    public double Dis = 0;
    public double Bearing = 0;


}
