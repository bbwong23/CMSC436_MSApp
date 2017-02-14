package com.example.bethanywong.msapp;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Config;
import android.view.View;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class SpiralTest extends AppCompatActivity {

    private View view;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private DrawingView drawView;
    private String hand;
    private int trials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_test);
        drawView = (DrawingView)findViewById(R.id.drawing);
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

        if (hand == "right") {
            hand = "left";
        } else {
            trials++;
            hand = "right";
        }

        if (trials >= 2) {
            // display ending screen
        }

        TextView instructions = (TextView) findViewById(R.id.instructions);
        instructions.setText("Trace the spiral with your " + hand + " hand.");
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
