package com.example.bethanywong.msapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.example.bethanywong.msapp.SpiralScoreFragment.TRIAL_KEY;
import static com.example.bethanywong.msapp.TapScoreFragment.LEFT_HAND_KEY;
import static com.example.bethanywong.msapp.TapScoreFragment.RIGHT_HAND_KEY;


public class CurlScoreFragment extends Fragment {
    private static final String RESULTS_KEY = "RESULTS_KEY";
    private String[] trialOrder;
    private int[] rTrials;
    private int[] lTrials;
    private float[] results;
    private TextView resultsText;
    private Button homeButton;
    private FinishCurlTestListener callBack;

    public interface FinishCurlTestListener {
        public void goHome();
    }
    public static CurlScoreFragment newInstance(String[] trialOrder, int[] rTrials, int[] lTrials, float[] results) {
        CurlScoreFragment fragment = new CurlScoreFragment();
        Bundle args = new Bundle();
        args.putFloatArray(RESULTS_KEY, results);
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
        View view = inflater.inflate(R.layout.fragment_curl_score, container, false);
        trialOrder = getArguments().getStringArray(RESULTS_KEY);
        rTrials = getArguments().getIntArray(RIGHT_HAND_KEY);
        lTrials = getArguments().getIntArray(LEFT_HAND_KEY);
        results = getArguments().getFloatArray(RESULTS_KEY);
        resultsText = (TextView)view.findViewById(R.id.resultsText);
        homeButton = (Button)view.findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.goHome();
            }
        });

        String resultString = getResultString();
        resultsText.setText(resultString);
        return view;
    }

    public String getResultString() {
        StringBuffer resultString = new StringBuffer();
        NumberFormat formatter = new DecimalFormat("#0.00");
        float rAvg = computeAverageScore(rTrials);
        float lAvg = computeAverageScore(lTrials);
        resultString.append("Right Hand Score: " + formatter.format(rAvg));
        resultString.append(getResultStringHelper(rTrials));
        resultString.append("\n");
        resultString.append("Left Hand Score: " + formatter.format(lAvg));
        resultString.append(getResultStringHelper(lTrials));
        return resultString.toString();
    }

    public String getResultStringHelper(int[] indexArray) {
        StringBuffer str = new StringBuffer();
        NumberFormat formatter = new DecimalFormat("#0.00");
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            str.append("\n\rTrial " + (i+1) + ": " + formatter.format(results[index]));
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
            callBack = (FinishCurlTestListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
