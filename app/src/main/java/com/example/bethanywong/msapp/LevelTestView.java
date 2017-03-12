package com.example.bethanywong.msapp;

/**
 * Created by Emmz on 2/25/2017.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class LevelTestView extends View{
    private final static String TAG = "LevelTestView";

    private float bearing;
    float pitch = 0;
    float roll = 0;

    private Paint paintOuter;
    private Paint paintMiddle;
    private Paint paintInner;
    private Paint paintDot;

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private boolean timerStarted;
    ArrayList<Pair<Float, Float>> positions = new ArrayList<Pair<Float,Float>>();

    float pointX, pointY;
    float dotX, dotY;
    int radiusInner, radiusMiddle, radiusOuter;

    public LevelTestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initCompassView();
    }

    public LevelTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompassView();
        setupDrawing();
        timerStarted = false;
    }

    public LevelTestView(Context context) {
        super(context);
        initCompassView();
        setupDrawing();
        timerStarted = false;
    }

    protected void initCompassView() {
        setFocusable(true);

        Resources r = this.getResources();

        paintOuter = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintOuter.setColor(Color.RED);
        paintOuter.setStrokeWidth(1);
        paintOuter.setStyle(Paint.Style.FILL_AND_STROKE);

        paintMiddle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMiddle.setColor(Color.YELLOW);
        paintMiddle.setStrokeWidth(1);
        paintMiddle.setStyle(Paint.Style.FILL_AND_STROKE);

        paintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintInner.setColor(Color.GREEN);
        paintInner.setStrokeWidth(1);
        paintInner.setStyle(Paint.Style.FILL_AND_STROKE);

        paintDot = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDot.setColor(Color.BLACK);
        paintDot.setStrokeWidth(1);
        paintDot.setStyle(Paint.Style.FILL_AND_STROKE);

    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        // TEMP FIX: hardcoded size of bitmap.
        // originally used getMeasuredWidth() and getMeasuredHeight(), but they returned 0
        canvasBitmap = Bitmap.createBitmap(1000,1000, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int px = getMeasuredWidth() / 2;
        int py = getMeasuredHeight() / 2;
        radiusOuter = Math.min(px, py);
        radiusMiddle = radiusOuter/2;
        radiusInner = radiusMiddle/2;

        pointX = px;
        pointY = py;

        canvas.drawCircle(pointX, pointY, radiusOuter, paintOuter);
        canvas.drawCircle(pointX, pointY, radiusMiddle, paintMiddle);
        canvas.drawCircle(pointX, pointY, radiusInner, paintInner);
        canvas.drawCircle(dotX, dotY, 20, paintDot);

        if (timerStarted) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            canvas.drawPath(drawPath, drawPaint);
        }
    }

    void clearPathTrace() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        timerStarted = false;
    }

    void startTrace() {
        positions = new ArrayList<Pair<Float, Float>>();
        timerStarted = true;
    }

    void update(float z, float yy, float xx) {
        float oldX = dotX;
        float oldY = dotY;

        dotY = pointY + 50 * yy;
        if (xx > 0) {
            dotX = pointX + 10*((1 - xx) * z);
        } else {
            dotX = pointX - 10*((1 - xx) * z);
        }

        if (timerStarted) {
            drawPath.moveTo(oldX, oldY);
            drawPath.lineTo(dotX, dotY);
            drawCanvas.drawPath(drawPath, drawPaint);
            drawPath.reset();
        }
        positions.add(new Pair<Float,Float>(dotX,dotY));
        invalidate();
    }

    protected int computeResults() {

        double testedRed = 0;
        double testedYellow = 0;
        double testedGreen = 0;
        double totalDrawn = 0;

        int width = canvasBitmap.getWidth();
        int height = canvasBitmap.getHeight();
        int[] canvasPixels = new int[width*height];
        double score = 0;

        canvasBitmap.getPixels(canvasPixels,0,width,1,1,width-1,height-1);

        //count the number of pixels of each color after the test.
        for(Pair<Float,Float> p: positions) {
            double xSquare = (p.first - pointX);
            double ySquare = (p.second-pointY);
            double distance = xSquare * xSquare + ySquare * ySquare;
            distance = Math.sqrt(distance);
            if (distance < radiusInner) { //green
                score += 3;
            } else if (distance < radiusMiddle) { //yellow
                score += 2;
            } else if (distance < radiusOuter) { //red
                score += 1;
            }

            totalDrawn++;
        }
        score /= (totalDrawn * 3);

        return (int) Math.ceil(score*100);
    }
}
