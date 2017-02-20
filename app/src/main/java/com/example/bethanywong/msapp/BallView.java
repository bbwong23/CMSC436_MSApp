package com.example.bethanywong.msapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

public class BallView extends View implements SensorEventListener2{
    private float xPos, xAccel, xVel = 0.0f;
    private float yPos, yAccel, yVel = 0.0f;
    private float xMax, yMax;
    private Bitmap ball;
    private SensorManager sensorManager;

    public BallView(Context context,AttributeSet attrs) {
        super(context,attrs);
        //create ball
        Bitmap ballSrc = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        final int dstWidth = 50;
        final int dstHeight = 50;
        ball = Bitmap.createScaledBitmap(ballSrc, dstWidth, dstHeight, true);
        //starting point
        getStartPoint();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(ball, xPos, yPos,null);
        invalidate();
    }

    @Override
    public void onFlushCompleted(Sensor sensor) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAccel = event.values[0];
            yAccel = -event.values[1];
            updateBall();
        }
    }

    private void getStartPoint(){
        //because the size of the imageview is not known at runtime, i'm running into problems
        //where I won't be able to just say the endpoints are the size of the thing
        //basically means you need to hardcode it.....
        int ySize = 450;
        int xSize = 450;

        xMax = (float) xSize;
        yMax = (float) ySize;
        xPos = (float) xSize / 2;
        yPos = (float) ySize / 2;
    }
    private void updateBall() {
        float frameTime = 0.3f;
        xVel += (xAccel * frameTime);
        yVel += (yAccel * frameTime);

        float xS = (xVel / 2) * frameTime;
        float yS = (yVel / 2) * frameTime;

        xPos -= xS;
        yPos -= yS;

        if (xPos > xMax) {
            xPos = xMax;
        } else if (xPos < 0) {
            xPos = 0;
        }

        if (yPos > yMax) {
            yPos = yMax;
        } else if (yPos < 0) {
            yPos = 0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}