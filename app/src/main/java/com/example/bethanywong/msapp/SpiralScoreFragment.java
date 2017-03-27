package com.example.bethanywong.msapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.bethanywong.msapp.SpiralTest.RESULT_KEY;


public class SpiralScoreFragment extends Fragment {
    public static final String TRIAL_KEY = "TRIAL_KEY";
    public static final String RIGHT_KEY = "RIGHT_KEY";
    public static final String LEFT_KEY = "LEFT_KEY";
    private Button button;
    private FinishSpiralTestListener callBack;
    private TextView scoresText;
    private int[] scores;
    private String[] trialOrder;
    private int[] rTrials;
    private int[] lTrials;

    public interface FinishSpiralTestListener {
        public void goHome();
    }

    public static SpiralScoreFragment newInstance(String[] trialOrder, int[] rTrials, int[] lTrials, int[] result) {
        SpiralScoreFragment fragment = new SpiralScoreFragment();
        Bundle args = new Bundle();
        args.putIntArray(RESULT_KEY, result);
        args.putStringArray(TRIAL_KEY, trialOrder);
        args.putIntArray(RIGHT_KEY, rTrials);
        args.putIntArray(LEFT_KEY, lTrials);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spiral_score, container, false);
        button = (Button)view.findViewById(R.id.homeButton);
        scoresText = (TextView)view.findViewById(R.id.finalScore);
        scores = getArguments().getIntArray(RESULT_KEY);
        trialOrder = getArguments().getStringArray(TRIAL_KEY);
        rTrials = getArguments().getIntArray(RIGHT_KEY);
        lTrials = getArguments().getIntArray(LEFT_KEY);
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.goHome();
                }
            }
        );

        String resultString = getResultString();
        scoresText.setText(resultString);

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

    public double getAvgScore(int[] indexArray) {
        int sum = 0;
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            sum += scores[index];
        }
        return sum / (double)indexArray.length;
    }

    public String getResultStringHelper(int[] indexArray) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            str.append("\n\rTrial " + (i+1) + ": " + scores[index]);
        }
        return str.toString();
    }

    public String getResultString() {
        StringBuffer resultStr = new StringBuffer();
        double rAvg = getAvgScore(rTrials);
        double lAvg = getAvgScore(lTrials);
        resultStr.append("Right hand score: " + rAvg);
        resultStr.append(getResultStringHelper(rTrials));
        resultStr.append("\nLeft hand score: " + lAvg);
        resultStr.append(getResultStringHelper(lTrials));
        return resultStr.toString();

    }


}
