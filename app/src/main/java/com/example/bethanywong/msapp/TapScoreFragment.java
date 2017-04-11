package com.example.bethanywong.msapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.bethanywong.msapp.SpiralScoreFragment.TRIAL_KEY;
import static com.example.bethanywong.msapp.TapTest.RESULTS_KEY;


public class TapScoreFragment extends Fragment {
    public static final String RIGHT_HAND_KEY = "RIGHT_HAND_KEY";
    public static final String LEFT_HAND_KEY = "LEFT_HAND_KEY";
    public static final String RIGHT_FOOT_KEY = "RIGHT_FOOT_KEY";
    public static final String LEFT_FOOT_KEY = "LEFT_FOOT_KEY";
    private int[] results;
    private TextView resultsView;
    private Button homeButton;
    private FinishTapTestListener callBack;
    private String[] trialOrder;
    private int[] rHandTrials;
    private int[] lHandTrials;
    private int[] rFootTrials;
    private int[] lFootTrials;

    public interface FinishTapTestListener {
        public void goHome();
    }

    public static TapScoreFragment newInstance(String[] trialOrder, int[] rHand, int[] lHand, int[] rFoot, int[] lFoot, int[] results) {
        TapScoreFragment fragment = new TapScoreFragment();
        Bundle args = new Bundle();
        args.putIntArray(RESULTS_KEY, results);
        args.putStringArray(TRIAL_KEY, trialOrder);
        args.putIntArray(RIGHT_HAND_KEY, rHand);
        args.putIntArray(LEFT_HAND_KEY, lHand);
        args.putIntArray(RIGHT_FOOT_KEY, rFoot);
        args.putIntArray(LEFT_FOOT_KEY, lFoot);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tap_score, container, false);
        resultsView = (TextView)view.findViewById(R.id.results);
        results = getArguments().getIntArray(RESULTS_KEY);
        homeButton = (Button)view.findViewById(R.id.homeButton);
        trialOrder = getArguments().getStringArray(TRIAL_KEY);
        rHandTrials = getArguments().getIntArray(RIGHT_HAND_KEY);
        lHandTrials = getArguments().getIntArray(LEFT_HAND_KEY);
        rFootTrials = getArguments().getIntArray(RIGHT_FOOT_KEY);
        lFootTrials = getArguments().getIntArray(LEFT_FOOT_KEY);

        homeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beginSheetResponse();
                        callBack.goHome();
                    }
                }
        );

        // display results
        String resultString = constructResultText();
        resultsView.setText(resultString);

        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            callBack = (FinishTapTestListener)activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String constructResultText() {
        StringBuffer resultString = new StringBuffer("Right hand: ");
        resultString.append(constructResultTextHelper(rHandTrials));
        resultString.append("\r\nLeft hand: ");
        resultString.append(constructResultTextHelper(lHandTrials));
        resultString.append("\r\nRight foot: ");
        resultString.append(constructResultTextHelper(rFootTrials));
        resultString.append("\r\nLeft foot: ");
        resultString.append(constructResultTextHelper(lFootTrials));
        return resultString.toString();
    }

    private String constructResultTextHelper(int[] indexArray) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            str.append(results[index]);

            if (i < indexArray.length-1) {
                str.append(" || ");
            }
        }
        return str.toString();
    }

    private float getAvgScore(int[] trials) {
        float sum = 0;

        for(int i = 0;i < trials.length;i++) {
            sum += results[trials[i]];
        }

        return sum / 3.0f;
    }

    private float[] toFloatArray(int[] data) {
        float[] floatData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            floatData[i] = (float)results[data[i]];
        }
        return floatData;
    }

    private void beginSheetResponse() {
        SharedPreferences prefs = this.getActivity().getSharedPreferences("PrefsFile", Context.MODE_PRIVATE);
        int userID = prefs.getInt("user",0);
        if (userID == 0) {
            Log.d("Tag","Missing userID!");
        }
        ((TapTest)getActivity()).sendToClassSheet("t8p0" + userID, getAvgScore(rHandTrials),
                getAvgScore(lHandTrials),getAvgScore(rFootTrials), getAvgScore(lFootTrials));
        ((TapTest)getActivity()).sendToTrialSheet("t8p0" + userID, toFloatArray(rHandTrials),
                toFloatArray(lHandTrials),toFloatArray(rFootTrials), toFloatArray(lFootTrials));
    }

}
