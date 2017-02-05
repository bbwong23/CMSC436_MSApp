package com.example.bethanywong.msapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class test extends AppCompatActivity {
    Button startButton;
    TextView timeTextView;
    CountDownTimer timer = new CountDownTimer(30000, 1000) {
        public void onTick(long millisUntilFinished) {
            timeTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
        }

        public void onFinish() {
            timeTextView.setText("TIME'S UP");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        startButton = (Button) findViewById(R.id.startButton);

    }

    public void runTimer(View view) {
        timer.start();
    }
}
