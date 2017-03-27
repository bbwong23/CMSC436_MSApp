package com.example.bethanywong.msapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static android.R.attr.button;
import static com.example.bethanywong.msapp.SpiralTest.SCORE_KEY;

/**
 * Created by Samantha on 3/12/17.
 */

public class SpiralScoreFragment extends Fragment {
    private Button button;
    private FinishSpiralTestListener callBack;
    private TextView scores;
    public interface FinishSpiralTestListener {
        public void goHome();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spiral_score, container, false);
        button = (Button)view.findViewById(R.id.homeButton);
        scores = (TextView)view.findViewById(R.id.finalScore);
        int[] allScores = getArguments().getIntArray(SCORE_KEY);
        int rHandScore = (allScores[0] + allScores[1] + allScores[2]) / 3;
        int lHandScore = (allScores[3] + allScores[4] + allScores[5]) / 3;
        scores.setText("Right Hand Score: " + rHandScore + "\nLeft Hand Score: " + lHandScore
                + "\r\n" + "Right Trials: " + "\r\n" + "R1: " + allScores[0]
                + "\r\n" + "R2: " + allScores[1]
                + "\r\n" + "R3: " + allScores[2]
                + "\r\n" + "Left Trials: " + "\r\n" + "L1: " + allScores[3]
                + "\r\n" + "L2: " + allScores[4]
                + "\r\n" + "L3: " + allScores[5]);
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.goHome();
                }
            }
        );
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            callBack = (FinishSpiralTestListener)activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
