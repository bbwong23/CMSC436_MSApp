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

import static com.example.bethanywong.msapp.SpiralScoreFragment.LEFT_KEY;
import static com.example.bethanywong.msapp.SpiralScoreFragment.RIGHT_KEY;
import static com.example.bethanywong.msapp.SpiralScoreFragment.TRIAL_KEY;
import static com.example.bethanywong.msapp.SpiralTest.RESULT_KEY;

public class BalloonScoreFragment extends Fragment {

    private String[] trialOrder;
    private int[] rTrials;
    private int[] lTrials;
    private double[] scores;
    private TextView resultsText;
    private FinishBalloonTestListener callBack;
    private Button finishButton;

    public static BalloonScoreFragment newInstance(String[] trialOrder, int[] rTrials, int[] lTrials, double[] results) {
        BalloonScoreFragment fragment = new BalloonScoreFragment();
        Bundle args = new Bundle();
        args.putStringArray(TRIAL_KEY, trialOrder);
        args.putIntArray(RIGHT_KEY, rTrials);
        args.putIntArray(LEFT_KEY, lTrials);
        args.putDoubleArray(RESULT_KEY, results);
        fragment.setArguments(args);
        return fragment;
    }

    public interface FinishBalloonTestListener {
        public void goHome();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_balloon_score, container, false);
        trialOrder = getArguments().getStringArray(TRIAL_KEY);
        rTrials = getArguments().getIntArray(RIGHT_KEY);
        lTrials = getArguments().getIntArray(LEFT_KEY);
        scores = getArguments().getDoubleArray(RESULT_KEY);
        resultsText = (TextView)view.findViewById(R.id.resultsText);
        finishButton = (Button)view.findViewById(R.id.homeButton);

        resultsText.setText(getResultString());
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.goHome();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            callBack = (FinishBalloonTestListener)activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getAvgScore(int[] indexArray) {
        double sum = 0.0;
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            sum += scores[index];
        }
        return sum / (double)indexArray.length;
    }

    public String getResultStringHelper(int[] indexArray) {
        StringBuffer str = new StringBuffer();
        NumberFormat formatter = new DecimalFormat("#0.00");
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            str.append("\n\rTrial " + (i+1) + ": " + formatter.format(scores[index]));
        }
        return str.toString();
    }

    public String getResultString() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        StringBuffer resultStr = new StringBuffer();
        double rAvg = getAvgScore(rTrials);
        double lAvg = getAvgScore(lTrials);
        resultStr.append("Right hand score: " + formatter.format(rAvg));
        resultStr.append(getResultStringHelper(rTrials));
        resultStr.append("\nLeft hand score: " + formatter.format(lAvg));
        resultStr.append(getResultStringHelper(lTrials));
        return resultStr.toString();


    }



}
