package com.example.bethanywong.msapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class BalloonTestInstructionFragment extends Fragment {

    private Button startButton;
    private StartBalloonTestListener callBack;

    public interface StartBalloonTestListener {
        public void startBalloonTest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balloon_test_instruction, container, false);
        startButton = (Button)view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.startBalloonTest();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callBack = (StartBalloonTestListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
