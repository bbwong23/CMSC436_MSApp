package com.example.bethanywong.msapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static com.example.bethanywong.msapp.TapTest.LEFT_FOOT_TRIALS;
import static com.example.bethanywong.msapp.TapTest.LEFT_HAND_TRIALS;
import static com.example.bethanywong.msapp.TapTest.RESULTS_KEY;
import static com.example.bethanywong.msapp.TapTest.RIGHT_FOOT_TRIALS;
import static com.example.bethanywong.msapp.TapTest.RIGHT_HAND_TRIALS;


public class TapScoreFragment extends Fragment {
    private int[] results;
    private TextView resultsView;
    private Button homeButton;
    private FinishTapTestListener callBack;

    public interface FinishTapTestListener {
        public void goHome();
    }

    public static TapScoreFragment newInstance(int[] results) {
        TapScoreFragment fragment = new TapScoreFragment();
        Bundle args = new Bundle();
        args.putIntArray(RESULTS_KEY, results);
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

        homeButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        resultString.append(constructResultTextHelper(RIGHT_HAND_TRIALS));
        resultString.append("\r\nLeft hand: ");
        resultString.append(constructResultTextHelper(LEFT_HAND_TRIALS));
        resultString.append("\r\nRight foot: ");
        resultString.append(constructResultTextHelper(RIGHT_FOOT_TRIALS));
        resultString.append("\r\nLeft foot: ");
        resultString.append(constructResultTextHelper(LEFT_FOOT_TRIALS));
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

}
