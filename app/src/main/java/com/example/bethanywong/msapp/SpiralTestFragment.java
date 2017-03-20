package com.example.bethanywong.msapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.UUID;

import static com.example.bethanywong.msapp.SpiralTest.HAND_KEY;
import static com.example.bethanywong.msapp.SpiralTest.R_HAND;


/**
 * Created by Samantha on 3/11/17.
 */

public class SpiralTestFragment extends Fragment {
    private OnFinishListener callback;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Activity activity;
    private Button button;
    private DrawingView drawView;
    private ImageView original;
    private TextView instructions;
    private View view;
    private String hand;
    private CountDownTimer timer;
    private long duration;
    private boolean started;

    public interface OnFinishListener {
        public void onFinish(String hand, int score, long time);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        view = inflater.inflate(R.layout.fragment_spiral_test, container, false);
        button = (Button)view.findViewById(R.id.finish);
        drawView = (DrawingView)view.findViewById(R.id.drawing);
        original = (ImageView)view.findViewById(R.id.spiral);
        instructions = (TextView)view.findViewById(R.id.instructions);
        started = false;
        duration = 0;
        timer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                instructions.setText("Timer: " + millisUntilFinished/1000);
                duration = millisUntilFinished;
            }

            // once timer is completed, user should not be able to draw anymore
            @Override
            public void onFinish() {
                instructions.setText("Times up! Please click Next.");
                drawView.pause();
            }
        };

        // set text for appropriate hand
        hand = getArguments().getString(HAND_KEY);
        instructions.setText("Trace the spiral with your " + hand + " hand");
        button.setText("Next");

        // starts timer when user begins drawing
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!started) {
                    started = true;
                    timer.start();
                }
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int score = computeScore();
                saveDrawing(v);
                drawView.setEnabled(true);
                started = false;
                callback.onFinish(hand, score, 10000-duration);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callback = (OnFinishListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int computeScore() {

        // convert original ImageView into a Bitmap
        original.setDrawingCacheEnabled(true);
        Bitmap origbit = original.getDrawingCache();
        drawView.setDrawingCacheEnabled(true);
        Bitmap drawbit = drawView.getDrawingCache();

        // Retrieve pixel data in the form of an array for the original spiral and drawn spiral
        int width = origbit.getWidth();
        int height = origbit.getHeight();
        int[] origpixels = new int[width*height];
        int[] drawpixels = new int[width*height];
        origbit.getPixels(origpixels,0,width,1,1,width-1,height-1);
        drawbit.getPixels(drawpixels,0,width,1,1,width-1,height-1);

        int totalDrawn = 0;
        int totalAccurate = 0;
        int missed = 0;
        int totalOrig = 0;
        int score;

        // calculates accuracy and penalizes for any non-traced parts of original
        for(int i = 0; i < width*height; i++) {
            if(drawpixels[i] != 0) {
                if(origpixels[i] != 0) {
                    totalOrig++;
                    totalAccurate++;
                }
                totalDrawn++;
            } else if (origpixels[i] != 0) {
                missed++;
                totalOrig++;
            }
        }

        // Final score: 80% = (accuracy - missed/2) && 20% = remaining time
        if (totalDrawn == 0) {
            score = 0;
        } else {
            score = totalAccurate*100/totalDrawn;
            score -= missed*50/totalOrig;
            score =(int)(score * .8 + (duration/10000)*.2);
        }

        return score < 0 ? 0:score;
    }

    public void saveDrawing(View v){
        view = v;
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        } else {
            String imgSaved = MediaStore.Images.Media.insertImage(
                    activity.getContentResolver(), screenShot(view),
                    UUID.randomUUID().toString() + ".png", "drawing");
            if (imgSaved != null) {
                Toast savedToast = Toast.makeText(activity.getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            } else {
                Toast unsavedToast = Toast.makeText(activity.getApplicationContext(),
                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
            drawView.destroyDrawingCache();
        }

        drawView.clear();
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    }
