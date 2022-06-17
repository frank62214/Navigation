package com.example.navigation.My;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.navigation.R;

public class My_Sensor extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;

    private Sensor mAccelerometers;
    private Sensor mGyroscope;
    private Sensor mMagnetometer;
    private Sensor vectorSensor;
    private Sensor orientation;

    float[] accelerometerValues = new float[3];
    float[] magneticValues = new float[3];

    public float bearing = 0;


    Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    //public My_Sensor(Context cont, LinearLayout ll){
    public My_Sensor(Context cont){
        context = cont;
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometers = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //vectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        orientation  = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }
    public void registerListener(){
        mSensorManager.registerListener(this, mAccelerometers, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(this, vectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void unregisterListener(){
        mSensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //bearing = sensorEvent.values[0]-42;
        bearing = sensorEvent.values[0];
        Data.now_bearing = bearing;

        String values = "X-axis=" + String.valueOf(sensorEvent.values[0]) + "\n" +
                "Y-axis=" + String.valueOf(sensorEvent.values[1]) + "\n" +
                "Z-axis=" + String.valueOf(sensorEvent.values[2]) + "\n";


        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            accelerometerValues[0] = sensorEvent.values[0];
//            accelerometerValues[1] = sensorEvent.values[1];
//            accelerometerValues[2] = sensorEvent.values[2];
            accelerometerValues = sensorEvent.values;
            //System.out.println(accelerometerValues[0]);
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            magneticValues[0] = sensorEvent.values[0];
//            magneticValues[1] = sensorEvent.values[1];
//            magneticValues[2] = sensorEvent.values[2];
            magneticValues = sensorEvent.values;
            calculate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private void calculate(){
        float[] R = new float[9];
        float[] value = new float[3];

        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
        mSensorManager.getOrientation(R, value);

        value[0] = (float) Math.toDegrees(value[0]);
        if(value[0]<0){
            value[0] = value[0] + 360;
        }
        //System.out.println(value[0]);
        //bearing = value[0];
        String bearing_s = "Bearing=" + value[0];
//        String values = "X-axis=" + String.valueOf(sensorEvent.values[0]) + "\n" +
//                         "Y-axis=" + String.valueOf(sensorEvent.values[1]) + "\n" +
//                         "Z-axis=" + String.valueOf(sensorEvent.values[2]) + "\n";

//        ori_c.setText(bearing_s);

//        if(sensorEvent.sensor.equals(mAccelerometers)){
//            acce.setText(values);
//        }
//        if(sensorEvent.sensor.equals(mGyroscope)){
//            gyro.setText(values);
//        }
//        if(sensorEvent.sensor.equals(mMagnetometer)){
//            mag.setText(values);
//        }
//        if(sensorEvent.sensor.equals(vectorSensor)){
//            rot.setText(values);
//        }
    }
}
