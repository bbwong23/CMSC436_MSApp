package com.example.bethanywong.msapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class SpiralTest extends AppCompatActivity {
    protected static final String L_SCORE_KEY = "L_SCORE_KEY";
    protected static final String R_SCORE_KEY = "R_SCORE_KEY";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private DrawingView drawView;
    private ImageView original;
    private String hand;
    private int trials;
    private int lScore, rScore;
    private Button finishButton;
    private View screen;
    private TextView instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test);
        drawView = (DrawingView)findViewById(R.id.drawing);
        original = (ImageView)findViewById(R.id.spiral);
        hand = "right";
        trials = 0;
        finishButton = (Button)findViewById(R.id.finish);
        screen = findViewById(R.id.activity_spiral_test);
        instructions = (TextView) findViewById(R.id.instructions);
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

        return score;
    }

    public void onFinish(View v){
        if (trials == 0) {
            lScore = computeScore();
        }
        saveDrawing(v);
    }

    public void saveDrawing(View v){
        view = v;
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

        if (trials < 1) {
            if (trials == 0) {
                hand = "left";
                finishButton.setText("View Results");
                finishButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rScore = computeScore();
                                Intent intent = new Intent("com.example.bethanywong.msapp.SpiralScore");
                                intent.putExtra(L_SCORE_KEY, lScore);
                                intent.putExtra(R_SCORE_KEY, rScore);
                                startActivity(intent);
                            }
                        }
                );
            }

            instructions.setText("Trace the spiral with your " + hand + " hand.");
            trials++;
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
