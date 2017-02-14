package com.example.bethanywong.msapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.R.attr.start;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnClickButtonListener();
    }

    public void OnClickButtonListener() {
        Button test1_button = (Button)findViewById(R.id.tap_button);
        Button test2_button = (Button)findViewById(R.id.spiral_button);
        Button devinfo_button = (Button)findViewById(R.id.devinfo_button);
        test1_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.example.bethanywong.msapp.TapTest");
                        startActivity(intent);
                    }
                }
        );
        test2_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.example.bethanywong.msapp.SpiralTest");
                        startActivity(intent);
                    }
                }
        );

        devinfo_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("com.example.bethanywong.msapp.DeveloperInfo");
                        startActivity(intent);
                    }
                }
        );
    }

}
