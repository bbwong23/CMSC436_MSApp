package com.example.bethanywong.msapp;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class BalloonTest extends AppCompatActivity {
    private static Button btn;
    private static int rounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloon_test);
        rounds = 0;
        OnClickButtonListener();
    }

    public void OnClickButtonListener() {
        // Every time the balloon gets tapped on, it should disappear and move
        btn = (Button)findViewById(R.id.balloon);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rounds++;
                resetButton();
            }
        });
    }

    // makes the balloon vanish and wait 2 seconds before reappearing
    public void resetButton() {
        btn.setVisibility(View.GONE);
        btn.setText("");
        CountDownTimer coolDown = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                moveButton();
            }
        };
        coolDown.start();
    }

    // grabs a new location for the balloon and makes it visible once location is set
    public void moveButton() {
        View main = findViewById(R.id.activity_balloon_test);
        Random r = new Random();

        // accounts for the size of the button so that new position doesn't cut off circle near edges
        int randomX = r.nextInt(main.getWidth()-btn.getWidth()-btn.getWidth()/2) + btn.getWidth()/2;
        int randomY = r.nextInt(main.getHeight()-btn.getHeight()-btn.getHeight()/2) + btn.getHeight()/2;

        btn.setLayoutParams(new AbsoluteLayout.LayoutParams(btn.getWidth(),btn.getHeight(),randomX,randomY));
        btn.setVisibility(View.VISIBLE);
    }
}