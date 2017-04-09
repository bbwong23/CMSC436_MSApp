package com.example.bethanywong.msapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SwayTestInstructionFragment extends Fragment {
    private Button startButton;
    private SwayTestInstructionFragment.StartTestListener callback;

    public interface StartTestListener {
        public void startTest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tap_test_instruction, container, false);
        startButton = (Button)view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.startTest();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callback = (SwayTestInstructionFragment.StartTestListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
