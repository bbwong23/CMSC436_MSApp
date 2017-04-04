package com.example.bethanywong.msapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


import static com.example.bethanywong.msapp.SpiralTest.SCORE_KEY;

/**
 * Created by Samantha on 3/12/17.
 */

public class SpiralScoreFragment extends Fragment{
    private Button button;
    private FinishSpiralTestListener callBack;
    public interface FinishSpiralTestListener {
        public void goHome();
    }
    private TextView scores;
    int[] allScores;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spiral_score, container, false);
        button = (Button)view.findViewById(R.id.homeButton);
        scores = (TextView)view.findViewById(R.id.finalScore);
        allScores = getArguments().getIntArray(SCORE_KEY);
        int rHandScore = (allScores[0] + allScores[1] + allScores[2]) / 3;
        int lHandScore = (allScores[3] + allScores[4] + allScores[5]) / 3;
        scores.setText("Right Hand Score: " + rHandScore + "\nLeft Hand Score: " + lHandScore
                + "\r\n" + "Right Trials: " + "\r\n" + "R1: " + allScores[0]
                + "\r\n" + "R2: " + allScores[1]
                + "\r\n" + "R3: " + allScores[2]
                + "\r\n" + "Left Trials: " + "\r\n" + "L1: " + allScores[3]
                + "\r\n" + "L2: " + allScores[4]
                + "\r\n" + "L3: " + allScores[5]);
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSheet();
                    callBack.goHome();
                }
            }
        );
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


    //for sending stuff to sheets
    //creates a row to be added
    public ArrayList<String> createResponse(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        //send data to sheets
        SharedPreferences prefs = getActivity().getSharedPreferences("PrefsFile", Context.MODE_PRIVATE);
        int userID = prefs.getInt("user",0);
        if (userID == 0) {
            Log.d("Tag","Missing userID!");
        }
        //determine test day
        String day = prefs.getString("spiralDay","");
        //uncomment to reset day
//        SharedPreferences.Editor editor1 = getSharedPreferences("PrefsFile", MODE_PRIVATE).edit();
//        editor1.remove("spiralDay");
//        editor1.commit();
        if (day.isEmpty()){
            Log.d("Tag","First Day");
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("PrefsFile", Context.MODE_PRIVATE).edit();
            editor.putString("spiralDay", currentDateandTime + "#1");
            editor.commit();
            day = "1";
        } else {
            Log.d("Tag","Already has day: " + day);
            //structure is: MM/DD/YYYY, HH:mm:ss#<day>
            String metaDay = day.substring(day.length() - 1);
            SimpleDateFormat diffsdf = new SimpleDateFormat("MM/dd/yyyy");
            try{
                Date date1 = diffsdf.parse(day);
                Date date2 = diffsdf.parse(currentDateandTime);
                long diff = TimeUnit.DAYS.convert((date1.getTime() - date2.getTime())
                        , TimeUnit.MILLISECONDS);
                if (diff == 0){
                    //same day
                    day = "1";
                } else if (diff <= -1) {
                    //in the future
                    //increment metaDay and store to day
                    day = String.valueOf(Integer.parseInt(metaDay) + 1);
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("PrefsFile", Context.MODE_PRIVATE).edit();
                    editor.putString("spiralDay", currentDateandTime + "#" + day);
                    editor.commit();
                } else {
                    //shouldn't go into past
                    day = "-1";
                }
            } catch (ParseException e){
                Log.d("Tag","date is screwed up: " + day);
                e.printStackTrace();
            }
        }
        ArrayList<String> row = new ArrayList<>();

        row.add("t8p0" + userID);//Name
        row.add(currentDateandTime);//datetime
        row.add("Spiral");//mode
        row.add(day);//day
        //add score
        //spiral right hand
        row.add(String.valueOf(allScores[0]));
        row.add(String.valueOf(allScores[1]));
        row.add(String.valueOf(allScores[2]));
        //spiral left
        row.add(String.valueOf(allScores[3]));
        row.add(String.valueOf(allScores[4]));
        row.add(String.valueOf(allScores[5]));
        return row;
    }
    private void sendSheet(){
        ArrayList<String> response = createResponse();
        Intent sheets = new Intent(getActivity(), Sheets.class);
        sheets.putStringArrayListExtra(Sheets.EXTRA_SHEETS, response);
        startActivity(sheets);
    }

}
