package com.example.navigation.My;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.stream.Stream;

public class Data {

    public static boolean Init_Camera = true;

    //UI Control
    public static ArrayList<String> Page_Order = new ArrayList<String>();
    final public static String Main_Page = "Main";
    final public static String Search_Page = "Search";
    final public static String Direction_Page = "Direction";
    final public static String Navigation_Page = "Navigation";
    final public static String CarMode_Page = "CarMode";
    public static boolean Lock_User = false;
    public static boolean lldata_view_visible = true;

    //location data
    public static LatLng now_position;
    public static LatLng cal_position;
    public static float now_bearing;
    public static LatLng Destination;

    //Direction data
    public static ArrayList<LatLng> Steps         = new ArrayList<LatLng>();
    public static ArrayList<LatLng> Decoder_Steps = new ArrayList<LatLng>();
    public static ArrayList<String> Road          = new ArrayList<String>();
    public static ArrayList<String> Road_Detail   = new ArrayList<String>();

    //Mode data
    public static String Mode = "Driving";
    public static boolean Select_mode = true;
    public static String Driving   = "Driving";
    public static String Bicycling = "Bicycling";
    public static String Walking   = "Walking";

    //Navigation data
    public static boolean Navigation_Status = false;
    public static boolean GPS_Status = false;
    public static boolean Nav_History = false;
    public static ArrayList<LatLng> API_Record = new ArrayList<LatLng>();
    public static ArrayList<LatLng> GPS_Record = new ArrayList<LatLng>();
    public static ArrayList<LatLng> Cal_Record = new ArrayList<LatLng>();
    public static ArrayList<LatLng> Nav_Record = new ArrayList<LatLng>();
    public static MarkerOptions Nav_Opt = new MarkerOptions();
    public static ArrayList<Marker> Nav_marker = new ArrayList<Marker>();

    //CarMode data
    public static boolean CarMode_Status = false;

    //Record
    public static boolean SnapRoad_Status = false;
    public static boolean Record_Timer_Status = false;
    public static boolean History_Status = false;
    public static boolean HistoryLine_Status = false;

    public static boolean[] Dot_Items = {false, false, false};
    public static boolean[] Line_Items = {false, false, false};



//    public static boolean GPS_Dot = false;
//    public static boolean GPS_Line = false;
//    public static boolean API_Dot = false;
//    public static boolean API_Line = false;
//    public static boolean Cal_Dot = false;
//    public static boolean Cal_Line = false;

    public static boolean AutoPlay = false;
    public static int compensate_dis_test = 0;

    public static double Compass_Bearing = 0;

    public static boolean AccStatus = false;
    public static boolean CalStatus = false;

    //private static final int[] sides = {0, 45, 90, 135, 180, 225, 270, 315, 360};
    public static final LatLng[] gps_simulator_location = new LatLng[]{
            new LatLng(22.6511068, 120.3127655),
            new LatLng(22.651043, 120.313139),
            new LatLng(22.650969, 120.313340),
            new LatLng(22.650870, 120.313555),
            new LatLng(22.650848, 120.313683),
            new LatLng(22.650739, 120.313815),
            new LatLng(22.650745, 120.313978),
            new LatLng(22.650732, 120.314140),
            new LatLng(22.650771, 120.314282),
            new LatLng(22.650806, 120.314400),
            new LatLng(22.650835, 120.314608),
            new LatLng(22.650880, 120.314754)
    };
}
