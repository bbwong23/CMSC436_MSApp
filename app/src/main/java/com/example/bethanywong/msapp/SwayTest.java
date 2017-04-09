package com.example.bethanywong.msapp;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SwayTest extends AppCompatActivity {
    android.support.v4.app.FragmentManager fragmentManager;
    FragmentTransaction transaction;
    int roundNumber;
    boolean hasBeenResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sway_test);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = -1;
        hasBeenResumed = false;
        SwayTestInstructionFragment fragment = new SwayTestInstructionFragment();

        // place instructions in view automatically
        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    public void startTest() {
        SwayTestFragment fragment = newInstance(TRIAL_ORDER[++roundNumber], roundNumber+1);
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
