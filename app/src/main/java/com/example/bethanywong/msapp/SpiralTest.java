package com.example.bethanywong.msapp;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Config;
import android.view.View;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class SpiralTest extends AppCompatActivity {

    private View view;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private DrawingView drawView;
    private ImageView original;
    private String hand;
    private int trials;
    private int lScore, rScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test);
        drawView = (DrawingView)findViewById(R.id.drawing);
        original = (ImageView)findViewById(R.id.spiral);
        hand = "right";
        trials = 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
      if (requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //if permission wasn't granted the first time, call save drawing again.
                //reason we call again is because the permission request is async, pls help with the callback pls
                saveDrawing(view);
            } else {
                //some error handling lolz
            }
        }
    }

    public void onFinish(View v){
//        TextView output = (TextView)findViewById(R.id.score);
        Button finish = (Button)findViewById(R.id.finish);

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

        // Compute score
        int totalDrawn = 0;
        int totalAccurate = 0;
        int score;
        for(int i = 0; i < width*height; i++) {
            if(drawpixels[i] != 0) {
                if(origpixels[i] != 0) {
                    totalAccurate++;
                }
                totalDrawn++;
            }
        }

        if (totalDrawn == 0) {
            score = 0;
        } else {
            score = Math.round(totalAccurate*100/totalDrawn);
        }


        if (trials == 0) {
            lScore = score;
            System.out.println("l trial: " + trials);
        } else if (trials == 1) {
            rScore = score;
            System.out.println("r trial: " + trials);
        }
        saveDrawing(v);

    }

    public void displayScores(View v) {
        Button finish = (Button)findViewById(R.id.finish);
        ViewGroup layout = (ViewGroup) finish.getParent();
        if (null != layout) {
            layout.removeView(finish);
        }

        TextView scoreTextView = new TextView(this);
        ImageView spiralView = (ImageView)findViewById(R.id.spiral);
        DrawingView drawingView = (DrawingView)findViewById(R.id.drawing);
        TextView instructions = (TextView) findViewById(R.id.instructions);
        scoreTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        scoreTextView.setText("Left Hand Score: " + lScore + "%\nRight Hand Score: " + rScore + "%");
        if (null != layout) {
            layout.removeView(finish);
            layout.removeView(spiralView);
            layout.removeView(instructions);
            layout.removeView(drawingView);
        }
        Button doneButton = new Button(this);
        doneButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        doneButton.setText("Done");
        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.example.bethanywong.msapp.MainActivity");
                        startActivity(intent);
                    }
                }
        );

        layout.addView(scoreTextView);
        layout.addView(doneButton);
    }

    public void saveDrawing(View v){
        view = v;
        View screen = findViewById(R.id.activity_spiral_test);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        } else {
            String imgSaved = MediaStore.Images.Media.insertImage(
                    getContentResolver(), screenShot(screen),
                    UUID.randomUUID().toString() + ".png", "drawing");
            if (imgSaved != null) {
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                savedToast.show();
            } else {
                Toast unsavedToast = Toast.makeText(getApplicationContext(),
                        "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                unsavedToast.show();
            }
            drawView.destroyDrawingCache();
        }

        drawView.clear();

        Button finish = (Button)findViewById(R.id.finish);
        if (trials < 1) {
            if (trials == 0) {
                hand = "left";
                finish.setText("View Results");
            }
            TextView instructions = (TextView) findViewById(R.id.instructions);
            instructions.setText("Trace the spiral with your " + hand + " hand.");
            trials++;
        } else {
            displayScores(v);
        }
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
