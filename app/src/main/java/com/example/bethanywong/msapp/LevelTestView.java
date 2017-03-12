package com.example.bethanywong.msapp;

/**
 * Created by Emmz on 2/25/2017.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class LevelTestView extends View{
    private final static String TAG = "LevelTestView";

    private float bearing;
    float pitch = 0;
    float roll = 0;

    private Paint paintOuter;
    private Paint paintMiddle;
    private Paint paintInner;
    private Paint paintDot;

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
    }

    public LevelTestView(Context context) {
        super(context);
        initCompassView();
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

    }

    void update(float z, float yy, float xx) {


        dotY = pointY + 50 * yy;
        if (xx > 0) {
            dotX = pointX + 10*((1 - xx) * z);
        } else {
            dotX = pointX - 10*((1 - xx) * z);
        }
        invalidate();
    }
}
