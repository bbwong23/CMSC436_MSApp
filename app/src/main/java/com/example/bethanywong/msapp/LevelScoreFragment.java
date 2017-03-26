package com.example.bethanywong.msapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.bethanywong.msapp.LevelTest.LEFT_HAND_TRIALS;
import static com.example.bethanywong.msapp.LevelTest.RIGHT_HAND_TRIALS;

public class LevelScoreFragment extends Fragment {
    public static final String RESULTS_KEY = "RESULTS_KEY";
    private int[] results;
    private TextView resultText;
    private Button homeButton;
    private FinishLevelTestListener callBack;

    public interface FinishLevelTestListener {
        public void goHome();
    }
    public static LevelScoreFragment newInstance(int[] results) {
        LevelScoreFragment fragment = new LevelScoreFragment();
        Bundle args = new Bundle();
        args.putIntArray(RESULTS_KEY, results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_level_score, container, false);
        resultText = (TextView)view.findViewById(R.id.results);
        homeButton = (Button)view.findViewById(R.id.homeButton);
        results = getArguments().getIntArray(RESULTS_KEY);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.goHome();
            }
        });
        String resultString = getResultString();
        resultText.setText(resultString);

        return view;
    }

    public String getResultString() {
        StringBuffer resultString = new StringBuffer();
        double rAvg = computeAverageScore(RIGHT_HAND_TRIALS);
        double lAvg = computeAverageScore(LEFT_HAND_TRIALS);
        resultString.append("Right Hand Score: " + rAvg);
        resultString.append(getResultStringHelper(RIGHT_HAND_TRIALS));
        resultString.append("\n");
        resultString.append("Left Hand Score: " + lAvg);
        resultString.append(getResultStringHelper(LEFT_HAND_TRIALS));
        return resultString.toString();
    }

    public String getResultStringHelper(int[] indexArray) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            str.append("\n\rTrial " + (i+1) + ": " + results[index]);
        }
        return str.toString();
    }

    public double computeAverageScore(int[] indexArray) {
        double score = 0.0;
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            score = score + results[index];
        }
        return score / indexArray.length;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callBack = (FinishLevelTestListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
