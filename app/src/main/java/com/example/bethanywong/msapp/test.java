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

    private int count;
    Button tap, reset;
    TextView txtCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        startButton = (Button) findViewById(R.id.startButton);

    }

    public void runTimer(View view) {
        timer.start();
        txtCount = (TextView) findViewById(R.id.textView5);
        tap = (Button)findViewById(R.id.tapCount);
        reset = (Button)findViewById(R.id.reset);
        count = 0;
    }
    public void tapped(View view){
        count++;
        txtCount.setText(String.valueOf(count));
    }
    public void resetCount(View view){
        count = 0;
        txtCount.setText(R.string.maintxt);
    }
}
