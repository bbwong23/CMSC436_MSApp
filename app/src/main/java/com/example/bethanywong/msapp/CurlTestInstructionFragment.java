package com.example.bethanywong.msapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CurlTestInstructionFragment extends Fragment {

    private Button startButton;
    private StartCurlTestListener callBack;

    public interface StartCurlTestListener {
        public void startCurlTest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_curl_test_instruction, container, false);
        startButton = (Button)view.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.startCurlTest();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callBack = (StartCurlTestListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
