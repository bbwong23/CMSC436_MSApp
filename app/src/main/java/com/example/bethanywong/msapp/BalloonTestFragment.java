package com.example.bethanywong.msapp;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import static com.example.bethanywong.msapp.SpiralTestFragment.HAND_KEY;

public class BalloonTestFragment extends Fragment {

    public static String ROUND_KEY = "ROUND_KEY";
    private Button button;
    private TextView roundText;
    private String hand;
    private int roundNumber;
    private int rounds;
    private Long startTime;
    private long totalTime;
    private View mainView;
    private Button nextButton;
    private FinishBalloonTrialListener callBack;


    public static BalloonTestFragment newInstance(String hand, int roundNumber) {
        BalloonTestFragment fragment = new BalloonTestFragment();
        Bundle args = new Bundle();
        args.putString(HAND_KEY, hand);
        args.putInt(ROUND_KEY, roundNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public interface FinishBalloonTrialListener {
        public void completeRound(long totalTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("tag", "creating balloon test");
        View view = inflater.inflate(R.layout.fragment_balloon_test, container, false);
        mainView = view.findViewById(R.id.fragment_balloon_test);
        button = (Button)view.findViewById(R.id.balloon);
        roundText = (TextView)view.findViewById(R.id.roundText);
        hand = getArguments().getString(HAND_KEY);
        roundNumber = getArguments().getInt(ROUND_KEY);
        rounds = 0;
        startTime = null;
        nextButton = (Button)view.findViewById(R.id.nextButton);

        // set round text
        roundText.setText("Round " + roundNumber + ": " + hand);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rounds++;
                resetButton();

                if (startTime != null) {
                    totalTime += System.nanoTime()-startTime;
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.completeRound(totalTime);
            }
        });

        CountDownTimer coolDown = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                button.setVisibility(View.VISIBLE);
            }
        };
        coolDown.start();
        return view;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            callBack = (FinishBalloonTrialListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // grabs a new location for the balloon and makes it visible once location is set
    public void moveButton() {
        Random r = new Random();

        // accounts for the size of the button so that new position doesn't cut off circle near edges
        int randomX = r.nextInt(mainView.getWidth()-button.getWidth()-button.getWidth()/2) + button.getWidth()/2;
        int randomY = r.nextInt(mainView.getHeight()-button.getHeight()-button.getHeight()/2) + button.getHeight()/2;

        button.setLayoutParams(new AbsoluteLayout.LayoutParams(button.getWidth(),button.getHeight(),randomX,randomY));
        button.setVisibility(View.VISIBLE);
        startTime = System.nanoTime();
    }

    // makes the balloon vanish and wait 2 seconds before reappearing
    public void resetButton() {
        button.setVisibility(View.GONE);
        CountDownTimer coolDown = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                if (rounds >= 10) {
                    nextButton.setVisibility(View.VISIBLE);
                    return;
                }
                moveButton();
            }
        };
        coolDown.start();
    }


}
