package com.example.bethanywong.msapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static com.example.bethanywong.msapp.SpiralTest.HAND_KEY;


public class LevelTestInstructionFragment extends Fragment {

    private Button startButton;
    private StartLevelTestListener callBack;

    public interface StartLevelTestListener {
        public void startLevelTest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_level_test_instruction, container, false);
        startButton = (Button)view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.startLevelTest();
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callBack = (StartLevelTestListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
