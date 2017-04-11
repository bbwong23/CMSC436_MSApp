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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.example.bethanywong.msapp.SpiralTest.RESULT_KEY;

public class SpiralScoreFragment extends Fragment{
    public static final String TRIAL_KEY = "TRIAL_KEY";
    public static final String RIGHT_KEY = "RIGHT_KEY";
    public static final String LEFT_KEY = "LEFT_KEY";
    private Button button;
    private FinishSpiralTestListener callBack;
    private TextView scoresText;
    private float[] scores;
    private String[] trialOrder;
    private int[] rTrials;
    private int[] lTrials;

    public interface FinishSpiralTestListener {
        public void goHome();
    }

    public static SpiralScoreFragment newInstance(String[] trialOrder, int[] rTrials, int[] lTrials, float[] result) {
        SpiralScoreFragment fragment = new SpiralScoreFragment();
        Bundle args = new Bundle();
        args.putFloatArray(RESULT_KEY, result);
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
        scores = getArguments().getFloatArray(RESULT_KEY);
        trialOrder = getArguments().getStringArray(TRIAL_KEY);
        rTrials = getArguments().getIntArray(RIGHT_KEY);
        lTrials = getArguments().getIntArray(LEFT_KEY);
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginSheetResponse();
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

    public float getAvgScore(int[] indexArray) {
        float sum = 0;
        for (int i = 0; i < indexArray.length; i++) {
            int index = indexArray[i];
            sum += scores[index];
        }
        return sum / (float)indexArray.length;
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
        StringBuffer resultStr = new StringBuffer();
        NumberFormat formatter = new DecimalFormat("#0.00");
        float rAvg = getAvgScore(rTrials);
        float lAvg = getAvgScore(lTrials);
        resultStr.append("Right hand score: " + formatter.format(rAvg));
        resultStr.append(getResultStringHelper(rTrials));
        resultStr.append("\nLeft hand score: " + formatter.format(lAvg));
        resultStr.append(getResultStringHelper(lTrials));
        return resultStr.toString();

    }

    private void beginSheetResponse() {
        SharedPreferences prefs = this.getActivity().getSharedPreferences("PrefsFile", Context.MODE_PRIVATE);
        int userID = prefs.getInt("user",0);
        if (userID == 0) {
            Log.d("Tag","Missing userID!");
        }
        //"Do not call these two functions one after another" - Andrew Liu
        //Fuck it lol.
        ((SpiralTest)getActivity()).sendToClassSheet("t8p0" + userID,(float)getAvgScore(rTrials),(float)getAvgScore(lTrials));
        ((SpiralTest)getActivity()).sendToGroupSheet("t8p0" + userID, getTrials(rTrials), getTrials(lTrials));
    }

    //why is scores separate from rTrials and lTrials? rTrials should just be the trials,
    //not index of the right hand trials in scores..?
    private float[] getTrials(int[] input){
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; i++){
            output[i] = scores[input[i]];
        }
        return output;
    }


}
