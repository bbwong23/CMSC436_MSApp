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

import static com.example.bethanywong.msapp.TapTest.RESULTS_KEY;


public class SwayScoreFragment extends Fragment {
    private float[] results;
    private TextView resultsText;
    private Button homeButton;
    private OnFinishSwayTestListener callBack;

    public static SwayScoreFragment newInstance(float[] results) {
        SwayScoreFragment fragment = new SwayScoreFragment();
        Bundle args = new Bundle();
        args.putFloatArray(RESULTS_KEY, results);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnFinishSwayTestListener {
        public void finishTest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sway_score, container, false);
        results = getArguments().getFloatArray(RESULTS_KEY);
        resultsText = (TextView)view.findViewById(R.id.resultsText);
        homeButton = (Button)view.findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginSheetResponse();
                callBack.finishTest();
            }
        });

        resultsText.setText(getResultText());

        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callBack = (OnFinishSwayTestListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getAvg() {
        double avg = 0;
        for (int i = 0; i < results.length; i++) {
            avg = avg + results[i];
        }
        return avg/results.length;
    }

    public String getResultText() {
        StringBuffer resultStr = new StringBuffer();
        resultStr.append("\tScore: " + getAvg() + "\n");
        for (int i = 0; i < results.length; i++) {
            resultStr.append("\t\tTrial " + (i+1) + ": " + results[i] + "\n");
        }
        return resultStr.toString();
    }

    private void beginSheetResponse(){
        SharedPreferences prefs = this.getActivity().getSharedPreferences("PrefsFile", Context.MODE_PRIVATE);
        int userID = prefs.getInt("user",0);
        if (userID == 0) {
            Log.d("Tag","Missing userID!");
        }
        ((SwayTest)getActivity()).sendToClassSheet("t8p0" + userID,(float)getAvg());
        ((SwayTest)getActivity()).sendToGroupSheet("t8p0" + userID,results);
    }


}
