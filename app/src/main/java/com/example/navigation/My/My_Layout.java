package com.example.navigation.My;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.navigation.R;

public class My_Layout extends RelativeLayout {

    //activity_main image button
    ImageButton btnDirections;
    ImageButton btnFocusUser;
    ImageButton btnNavigation;

    //activity_navigation
    LinearLayout llNext_Turn;
    LinearLayout llUserArrow;
    ImageView ic_Next_Turn;
    TextView tv_Next_Road;
    TextView tv_Next_Road_Detail;
    TextView tv_Now_Position;

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
        //取得acitvity_main的元件
        btnDirections = (ImageButton) layout_view.findViewById(R.id.btnDirections);
        btnFocusUser  = (ImageButton) layout_view.findViewById(R.id.btnFocusUser);
        btnNavigation = (ImageButton) layout_view.findViewById(R.id.btnNavigation);
        //取得activity_navigation的元件
        llNext_Turn   = (LinearLayout) navigation_view.findViewById(R.id.llNext_Turn);
        llUserArrow   = (LinearLayout) navigation_view.findViewById(R.id.llUserArrow);
        ic_Next_Turn        = (ImageView) llNext_Turn.findViewById(R.id.ic_Next_Turn);
        tv_Next_Road        = (TextView) llNext_Turn.findViewById(R.id.tv_Next_Road);
        tv_Next_Road_Detail = (TextView) llNext_Turn.findViewById(R.id.tv_Next_Road_Detail);
        tv_Now_Position     = (TextView) llNext_Turn.findViewById(R.id.tv_Now_Position);
        Main_Page();
    }

    public void Main_Page(){
        btnDirections.setVisibility(View.VISIBLE);
        btnNavigation.setVisibility(View.GONE);
    }
    public void Direction_Page(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                btnDirections.setVisibility(View.GONE);
                btnNavigation.setVisibility(View.VISIBLE);
            }
        });
    }
    public void Navigation_Page(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() { ;
                btnNavigation.setVisibility(View.GONE);
                llNext_Turn.setVisibility(View.VISIBLE);
                llUserArrow.setVisibility(View.VISIBLE);
            }
        });
    }
    public void Set_Turn_Pic(String turn){
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
}
