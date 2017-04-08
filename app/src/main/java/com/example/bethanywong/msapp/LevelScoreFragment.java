package com.example.bethanywong.msapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.umd.cmsc436.sheets.Sheets;

import static com.example.bethanywong.msapp.SpiralScoreFragment.TRIAL_KEY;
import static com.example.bethanywong.msapp.TapScoreFragment.LEFT_HAND_KEY;
import static com.example.bethanywong.msapp.TapScoreFragment.RIGHT_HAND_KEY;

public class LevelScoreFragment extends Fragment {
    public static final String RESULTS_KEY = "RESULTS_KEY";
    private int[] results;
    private TextView resultText;
    private Button homeButton;
    private FinishLevelTestListener callBack;
    private String[] trialOrder;
    private int[] rTrials;
    private int[] lTrials;
    private Sheets sheet;
    private String spreadsheetId;

    public interface FinishLevelTestListener {
        public void goHome();
    }

    public static LevelScoreFragment newInstance(String[] trialOrder, int[] rTrials, int[] lTrials, int[] results) {
        LevelScoreFragment fragment = new LevelScoreFragment();
        Bundle args = new Bundle();
        args.putIntArray(RESULTS_KEY, results);
        args.putStringArray(TRIAL_KEY, trialOrder);
        args.putIntArray(RIGHT_HAND_KEY, rTrials);
        args.putIntArray(LEFT_HAND_KEY, lTrials);
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
        trialOrder = getArguments().getStringArray(TRIAL_KEY);
        rTrials = getArguments().getIntArray(RIGHT_HAND_KEY);
        lTrials = getArguments().getIntArray(LEFT_HAND_KEY);

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
        float rAvg = computeAverageScore(rTrials);
        float lAvg = computeAverageScore(lTrials);
        resultString.append("Right Hand Score: " + rAvg);
        resultString.append(getResultStringHelper(rTrials));
        resultString.append("\n");
        resultString.append("Left Hand Score: " + lAvg);
        resultString.append(getResultStringHelper(lTrials));

        ((LevelTest)getActivity()).sendToSheets("t8p03",rAvg, lAvg);
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

    public float computeAverageScore(int[] indexArray) {
        float score = 0;
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
