package com.example.navigation.My;

public class Orientation {
    public float azimuth;
    public float pitch;
    public float roll;
    public void setOrientation(float mazimuth, float mpitch, float mroll){
        azimuth = mazimuth;
        pitch = mpitch;
        roll= mroll;
    }
    public void show(){
        System.out.print("azimuth:" + azimuth + " ");
        System.out.print("pitch:" + pitch + " ");
        System.out.println("roll:" + roll);
    }
}
