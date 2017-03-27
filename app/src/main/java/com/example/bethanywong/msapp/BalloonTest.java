package com.example.bethanywong.msapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.w3c.dom.Text;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class BalloonTest extends AppCompatActivity {
    private static Button btn;
    private static int rounds;
    private static int trialNumber;
    private TextView t;
    Long startTime;
    long totalTime;
    double[] averages;
    TextView sd;
    TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balloon_test);
        rounds = 0;
        trialNumber = 0;
        OnClickButtonListener();
        t = (TextView) findViewById(R.id.test);
        sd = (TextView) findViewById(R.id.scoreDescribe);
        score = (TextView) findViewById(R.id.score);
        averages = new double[2];
        startTime = null;
    }

    public void OnClickButtonListener() {
        // Every time the balloon gets tapped on, it should disappear and move
        btn = (Button)findViewById(R.id.balloon);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rounds++;
                resetButton();
                t.setVisibility(View.GONE);
                sd.setVisibility(View.GONE);
                score.setVisibility(View.GONE);

                if (startTime != null) {
                    totalTime += System.nanoTime()-startTime;
                }
            }
        });
    }

    // makes the balloon vanish and wait 2 seconds before reappearing
    public void resetButton() {
        btn.setVisibility(View.GONE);
        btn.setText("");
        CountDownTimer coolDown = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                if (rounds > 10) {
                    complete();
                    return;
                }
                moveButton();
            }
        };
        coolDown.start();
    }

    // grabs a new location for the balloon and makes it visible once location is set
    public void moveButton() {
        View main = findViewById(R.id.activity_balloon_test);
        Random r = new Random();

        // accounts for the size of the button so that new position doesn't cut off circle near edges
        int randomX = r.nextInt(main.getWidth()-btn.getWidth()-btn.getWidth()/2) + btn.getWidth()/2;
        int randomY = r.nextInt(main.getHeight()-btn.getHeight()-btn.getHeight()/2) + btn.getHeight()/2;

        btn.setLayoutParams(new AbsoluteLayout.LayoutParams(btn.getWidth(),btn.getHeight(),randomX,randomY));
        btn.setVisibility(View.VISIBLE);
        startTime = System.nanoTime();
    }

    public void complete() {
        averages[trialNumber] = totalTime / 10000000000.0;
        totalTime = 0;
        trialNumber++;
        if (trialNumber == 1) {
            t.setText("Now use your left hand!");
        } else {
            t.setText("You're done! Go back to the menu.");
            completeAll();

            return;
        }

        View main = findViewById(R.id.activity_balloon_test);
        t.setVisibility(View.VISIBLE);

        btn.setLayoutParams(new  AbsoluteLayout.LayoutParams(btn.getWidth(),btn.getHeight(),(main.getWidth()-btn.getWidth())/2,(main.getHeight()-btn.getHeight())/2));
        btn.setText("Tap to Start");
        btn.setVisibility(View.VISIBLE);

        rounds = 0;
    }

    public float averageTimes() {
        return 0;
    }

    public void completeAll() {
        NumberFormat formatter = new DecimalFormat("#0.00");

        t.setVisibility(View.VISIBLE);
        t.setText("Test Completed!");

        score.setText("Right Hand Score: " + formatter.format(averages[0]) + " seconds"
                + "\nLeft Hand Score: " + formatter.format(averages[1]) + " seconds");

        score.setVisibility(View.VISIBLE);
        sendResponse(formatter.format(averages[0]),formatter.format(averages[1]));
    }
    public void sendResponse(String rightAvg, String leftAvg){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss");
        String currentDateandTime = sdf.format(new Date());
        //send data to sheets
        SharedPreferences prefs = getSharedPreferences("PrefsFile", MODE_PRIVATE);
        int userID = prefs.getInt("user",0);
        if (userID == 0) {
            Log.d("Tag","Missing userID!");
        }

        //determine test day
        String day = prefs.getString("balloonDay","");
        //uncomment to reset day
//        SharedPreferences.Editor editor1 = getSharedPreferences("PrefsFile", MODE_PRIVATE).edit();
//        editor1.remove("balloonDay");
//        editor1.commit();
        if (day.isEmpty()){
            Log.d("Tag","First Day");
            SharedPreferences.Editor editor = getSharedPreferences("PrefsFile", MODE_PRIVATE).edit();
            editor.putString("balloonDay", currentDateandTime + "#1");
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
                    SharedPreferences.Editor editor = getSharedPreferences("PrefsFile", MODE_PRIVATE).edit();
                    editor.putString("balloonDay", currentDateandTime + "#" + day);
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
        Intent sheets = new Intent(this, Sheets.class);
        ArrayList<String> row = new ArrayList<>();

        row.add("T8P" + userID);//Name
        row.add(currentDateandTime);//datetime
        row.add("Balloon");//mode
        row.add(day);//day
        //right hand
        row.add(rightAvg);
        //left hand
        row.add(leftAvg);

        sheets.putStringArrayListExtra(Sheets.EXTRA_SHEETS, row);
        startActivity(sheets);
    }
}
