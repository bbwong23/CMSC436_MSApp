package com.example.bethanywong.msapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
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
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

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
        //setup pathing canvas
        setupDrawing();
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(0xFFFF0000);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        // dont really know what the size should be...
        canvasBitmap = Bitmap.createBitmap(650,650, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(ball, xPos, yPos,null);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
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
        int ySize = 650;
        int xSize = 650;

        xMax = (float) xSize-50;
        yMax = (float) ySize-50;
        xPos = (float) xSize / 2;
        yPos = (float) ySize / 2;
    }

    private void updateBall() {
        // compute movement of ball based on sensor data
        float frameTime = 0.3f;
        xVel += (xAccel * frameTime);
        yVel += (yAccel * frameTime);

        float xS = (xVel / 2) * frameTime;
        float yS = (yVel / 2) * frameTime;

        // for drawing the path
        float tempX = xPos;
        float tempY = yPos;

        // update ball position
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

        // draw path of ball movement
        drawPath.moveTo(tempX, tempY);
        drawPath.lineTo(xPos, yPos);
        drawCanvas.drawPath(drawPath, drawPaint);
        drawPath.reset();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}