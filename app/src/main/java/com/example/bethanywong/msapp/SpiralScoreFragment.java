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

//all of the sheets stuff
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.sheets.v4.SheetsScopes;

import com.google.api.services.sheets.v4.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

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
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String BUTTON_TEXT = "Submit Results";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };
    private List<Object> rowToAdd;
    //mode should be depending on test type("Tap", "Level", "Spiral",etc)
    private String mode;


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

        row.add("T8P" + userID);//Name
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
        mode = response.get(2);
        response.remove(2);
        rowToAdd = new ArrayList<>();

        Intent sheets = new Intent(getActivity(), Sheets.class);
        sheets.putStringArrayListExtra(Sheets.EXTRA_SHEETS, response);
        startActivity(sheets);
    }

}
