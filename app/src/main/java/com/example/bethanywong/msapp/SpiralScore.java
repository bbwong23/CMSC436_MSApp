package com.example.bethanywong.msapp;

import com.example.bethanywong.msapp.SpiralTest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SpiralScore extends AppCompatActivity {

    private TextView scoreTextView;
    private int lScore, rScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spiral_score);
        scoreTextView = (TextView)findViewById(R.id.finalScore);
        lScore = getIntent().getExtras().getInt(SpiralTest.L_SCORE_KEY);
        rScore = getIntent().getExtras().getInt(SpiralTest.R_SCORE_KEY);
        scoreTextView.setText("Left Hand Score: " + lScore + "%\nRight Hand Score: " + rScore + "%");

    }
}
