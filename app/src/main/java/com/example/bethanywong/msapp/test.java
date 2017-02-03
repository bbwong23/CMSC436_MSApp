package com.example.bethanywong.msapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class test extends AppCompatActivity {
    private int count;
    Button tap, reset;
    TextView txtCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
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
