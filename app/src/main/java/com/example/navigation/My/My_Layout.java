package com.example.navigation.My;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.menu.ActionMenuItemView;

import com.example.navigation.MainActivity;
import com.example.navigation.R;

import java.util.ArrayList;

public class My_Layout extends RelativeLayout {

    //activity_main image button
    ImageButton btnDirections;
    ImageButton btnFocusUser;
    ImageButton btnNavigation;
    LinearLayout llMode;
    ImageButton btnDrivingMode;
    ImageButton btnBicyclingMode;
    ImageButton btnWalkingMode;
    ImageButton btnViewSearch;
    ImageButton btnCarMode;


    //activity_navigation
    LinearLayout llNext_Turn;
    LinearLayout llUserArrow;
    ImageView ic_Next_Turn;
    TextView tv_Next_Road;
    TextView tv_Next_Road_Detail;
    TextView tv_Now_Position;
    TextView tv_Next_Dis;
    TextView tv_to_Destination_dis;
    //TextView tv_Last_Dis;
    //TextView tv_Now_Bearing;

    //activity_data_view
    LinearLayout lldata_view;
    TextView data_view_Now_Page;
    TextView data_view_Bearing;

    //activity_search
    RelativeLayout rlSearch;
    EditText et_search;
    ScrollView sv_search_result;
    LinearLayout llsearch_result;
    ProgressBar SearchProgressBar;

    public ArrayList<Button> Search_Button_Group = new ArrayList<Button>();


    public My_Layout(Context context) {
        super(context);
        //---------------------------------------------------------------------------------------------------------------------------------
        //Here to add the maps to view
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.activity_maps, null);
        this.addView(view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //---------------------------------------------------------------------------------------------------------------------------------
        //Here to add the button layout
        //LayoutInflater layout = ((Activity) context).getLayoutInflater();
        View layout_view = layoutInflater.inflate(R.layout.activity_main, null);
        this.addView(layout_view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //---------------------------------------------------------------------------------------------------------------------------------
        //Here to add the Navigation layout
        //LayoutInflater layout = ((Activity) context).getLayoutInflater();
        View navigation_view = layoutInflater.inflate(R.layout.activity_navigation, null);
        this.addView(navigation_view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //---------------------------------------------------------------------------------------------------------------------------------
        //Here to add the Text Data View layout
        //LayoutInflater layout = ((Activity) context).getLayoutInflater();
        View data_view = layoutInflater.inflate(R.layout.activity_data_view, null);
        this.addView(data_view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //---------------------------------------------------------------------------------------------------------------------------------
        //Here to add the Search layout
        //LayoutInflater layout = ((Activity) context).getLayoutInflater();
        View search_view = layoutInflater.inflate(R.layout.activity_search, null);
        this.addView(search_view, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //---------------------------------------------------------------------------------------------------------------------------------
        //取得acitvity_main的元件
        btnDirections = (ImageButton) layout_view.findViewById(R.id.btnDirections);
        btnFocusUser  = (ImageButton) layout_view.findViewById(R.id.btnFocusUser);
        btnNavigation = (ImageButton) layout_view.findViewById(R.id.btnNavigation);
        btnCarMode    = (ImageButton) layout_view.findViewById(R.id.btnCarMode);

        //取得activity_navigation的元件
        llNext_Turn   = (LinearLayout) navigation_view.findViewById(R.id.llNext_Turn);
        llUserArrow   = (LinearLayout) navigation_view.findViewById(R.id.llUserArrow);
        ic_Next_Turn          = (ImageView) llNext_Turn.findViewById(R.id.ic_Next_Turn);
        tv_Next_Road          = (TextView) llNext_Turn.findViewById(R.id.tv_Next_Road);
        tv_Next_Road_Detail   = (TextView) llNext_Turn.findViewById(R.id.tv_Next_Road_Detail);
        tv_Now_Position       = (TextView) llNext_Turn.findViewById(R.id.tv_Now_Position);
        tv_Next_Dis           = (TextView) llNext_Turn.findViewById(R.id.tv_Next_Dis);
        tv_to_Destination_dis = (TextView) llNext_Turn.findViewById(R.id.tv_to_Destination_dis);
        //tv_Last_Dis         = (TextView) llNext_Turn.findViewById(R.id.tv_Last_Dis);
        //tv_Now_Bearing      = (TextView) llNext_Turn.findViewById(R.id.tv_Now_Bearing);
        //取得activity_data_view
        lldata_view = (LinearLayout) data_view.findViewById(R.id.lldata_view);
        data_view_Now_Page = (TextView) lldata_view.findViewById(R.id.data_view_Now_Page);
        data_view_Bearing      = (TextView) lldata_view.findViewById(R.id.data_view_Bearing);
        //取得Search Layout的元件
        rlSearch = (RelativeLayout) search_view.findViewById(R.id.rlSearch);
        et_search = (EditText) rlSearch.findViewById(R.id.et_search);
        btnViewSearch = (ImageButton) rlSearch.findViewById(R.id.btnViewSearch);
        sv_search_result = (ScrollView) rlSearch.findViewById(R.id.sv_search_result);
        llsearch_result = (LinearLayout) rlSearch.findViewById(R.id.llsearch_result);
        SearchProgressBar = (ProgressBar) rlSearch.findViewById(R.id.SearchProgressBar);
        //取得mode選擇
        llMode           = (LinearLayout) layout_view.findViewById(R.id.llMode);
        btnDrivingMode   = (ImageButton) layout_view.findViewById(R.id.btnDrivingMode);
        btnBicyclingMode = (ImageButton) layout_view.findViewById(R.id.btnBicyclingMode);
        btnWalkingMode   = (ImageButton) layout_view.findViewById(R.id.btnWalkingMode);
    }
    public void Select_Page(My_Map my_map){
        if(Data.Page_Order.get(Data.Page_Order.size()-1).equals(Data.Main_Page)){ Main_Page(my_map); }
        if(Data.Page_Order.get(Data.Page_Order.size()-1).equals(Data.Search_Page)){ Search_Page(); }
        if(Data.Page_Order.get(Data.Page_Order.size()-1).equals(Data.Direction_Page)){ Direction_Page(my_map); }
//        if(Data.Page_Order.get(Data.Page_Order.size()-1).equals(Data.Navigation_Page)){ Navigation_Page(my_map); }
    }
    public void Main_Page(My_Map my_map){
        data_view_Now_Page.setText("現在頁面: " + Data.Main_Page);
        //隱藏元件
        rlSearch.setBackgroundColor(getResources().getColor(R.color.transparent));
        my_map.Remove_Direction();
        my_map.Remove_Destination();
        btnNavigation.setVisibility(View.GONE);
        sv_search_result.setVisibility(View.GONE);
        llNext_Turn.setVisibility(View.GONE);

        //關閉元件
        btnViewSearch.setEnabled(false);

        //顯示元件
        rlSearch.setVisibility(View.VISIBLE);
        my_map.setMyLocationEnabled(true);
        llMode.setVisibility(View.VISIBLE);
        btnDirections.setVisibility(View.VISIBLE);

        //刪除元件
        Remove_Result();
        my_map.Remove_Navigation_MK();
        Data.Destination = null;

        //移動相機
        my_map.moveCamera(Data.now_position, 15,  0);

        //狀態解除
        Data.CarMode_Status = false;
    }
    public void Search_Page(){
        if(Data.Page_Order.get(Data.Page_Order.size()-1)!=Data.Search_Page)   Data.Page_Order.add(Data.Search_Page);
        data_view_Now_Page.setText("現在頁面: " + Data.Search_Page);
        rlSearch.setBackgroundColor(getResources().getColor(R.color.white));
        btnViewSearch.setEnabled(true);

        //顯示元件
        sv_search_result.setVisibility(View.VISIBLE);
        btnCarMode.setVisibility(View.VISIBLE);
        llMode.setVisibility(View.VISIBLE);
    }

    public void Direction_Page(My_Map my_map){
        //返回路徑頁面取消GPS刷新
        Data.Navigation_Status = false;

        if(Data.Page_Order.get(Data.Page_Order.size()-1)!=Data.Direction_Page)   Data.Page_Order.add(Data.Direction_Page);
        //Data.Page_Order.add(Data.Direction_Page);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                my_map.set_Direction_Camera();
                data_view_Now_Page.setText("現在頁面: " + Data.Direction_Page);
                //隱藏元件
                btnDirections.setVisibility(View.GONE);
                my_map.Remove_Navigation_MK();
                llNext_Turn.setVisibility(View.GONE);
                rlSearch.setBackgroundColor(getResources().getColor(R.color.transparent));
                sv_search_result.setVisibility(View.GONE);

                btnCarMode.setVisibility(View.GONE);
                llMode.setVisibility(View.GONE);

                //顯示元件
                rlSearch.setVisibility(View.VISIBLE);
                btnNavigation.setVisibility(View.VISIBLE);
                my_map.setMyLocationEnabled(true);
                Data.Navigation_Status = false;
                //移動相機
                my_map.set_Direction_Camera();
            }
        });
    }
    public void Navigation_Page(My_Map my_map){
        //設定GPS刷新之後是否進入導航模式
        Data.Navigation_Status = true;


        if(Data.Page_Order.get(Data.Page_Order.size()-1)!=Data.Navigation_Page)   Data.Page_Order.add(Data.Navigation_Page);
        //Data.Page_Order.add(Data.Navigation_Page);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                data_view_Now_Page.setText("現在頁面: " + Data.Navigation_Page);
                //隱藏元件
                rlSearch.setVisibility(View.GONE);
                btnNavigation.setVisibility(View.GONE);
                llNext_Turn.setVisibility(View.VISIBLE);
                my_map.setMyLocationEnabled(false);
//                Data.Navigation_Status = true;
//                System.out.println(Data.Navigation_Status);
                //llUserArrow.setVisibility(View.VISIBLE);

            }
        });
    }
    public void CarMode_Page(){
        Data.Page_Order.add(Data.CarMode_Page);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                data_view_Now_Page.setText("現在頁面: " + Data.CarMode_Page);
                //隱藏元件
                llMode.setVisibility(View.GONE);
                rlSearch.setVisibility(View.GONE);
                btnNavigation.setVisibility(View.GONE);
                btnDirections.setVisibility(View.GONE);

                llNext_Turn.setVisibility(View.VISIBLE);
//                Data.Navigation_Status = true;
//                System.out.println(Data.Navigation_Status);
                //llUserArrow.setVisibility(View.VISIBLE);

            }
        });
    }
    public void Set_Turn_Pic(String turn){
        if(turn.contains("繼續直行")){ic_Next_Turn.setImageResource(R.drawable.dir_continue);}
        if(turn.contains("向右轉")){ic_Next_Turn.setImageResource(R.drawable.dir_turnright);}
        if(turn.contains("向左轉")){ic_Next_Turn.setImageResource(R.drawable.dir_turnleft);}
    }
    //Set Text
    public void setNextRoadText(String text){
        tv_Next_Road.setText(text);
    }
    public void setNextRoadDetailText(String text){
        tv_Next_Road_Detail.setText(text);
    }
    public void setNowPosition(String text){
        tv_Now_Position.setText(text);
    }
    public void setNextRoadDistance(String text) {
        tv_Next_Dis.setText("距離前方路口" + text + "公尺");
    }
    public void setDisToDestination(String text) {tv_to_Destination_dis.setText("距離目的地還有" + text + "公尺");}
//    public void setLastDistance(String text){  tv_Last_Dis.setText("距離前方路口" + text);}
//    public void setNow_Bearing(String text){
//        tv_Now_Bearing.setText(text);
//    }
    //public void setDataViewNowPosition(String text){
    //    data_view_Now_Position.setText(text);
    //}
    public void setDataViewBearing(String text){
        data_view_Bearing.setText(text);
    }

    public void Select_Mode(String mode){
        if(Data.Select_mode){
            Data.Select_mode = false;
            btnDrivingMode.setVisibility(View.VISIBLE);
            btnBicyclingMode.setVisibility(View.VISIBLE);
            btnWalkingMode.setVisibility(View.VISIBLE);
        }
        else{
            Data.Select_mode = true;
            if(mode.equals(Data.Driving)){
                Data.Mode = Data.Driving;
                btnBicyclingMode.setVisibility(View.GONE);
                btnWalkingMode.setVisibility(View.GONE);
            }
            if(mode.equals(Data.Bicycling)){
                Data.Mode = Data.Bicycling;
                btnDrivingMode.setVisibility(View.GONE);
                btnWalkingMode.setVisibility(View.GONE);
            }
            if(mode.equals(Data.Walking)){
                Data.Mode = Data.Walking;
                btnDrivingMode.setVisibility(View.GONE);
                btnBicyclingMode.setVisibility(View.GONE);
            }
        }
    }
    public void Set_Search_Place_Result(ArrayList<String> data){
        if(data.size()!=0) {
            for (int i = 0; i < data.size(); i++) {
                Button bt = new Button(getContext());
                bt.setText(data.get(i));
                bt.setTextColor(getResources().getColor(R.color.black));
                bt.setBackgroundColor(getResources().getColor(R.color.white));
                bt.setHeight(dp2px(50));
                bt.setTextSize(20);
                bt.setId(i);
                //ll.addView(bt);
                llsearch_result.addView(bt);
                Search_Button_Group.add(bt);
            }
        }
        else{
            Toast("查無資料，請重新搜尋");
        }
    }

    public void Toast(String text){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() { ;
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private int dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public void Remove_Result(){
        llsearch_result.removeAllViews();
    }
}
