package com.example.bethanywong.msapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DeveloperInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);
    }

    public void getUser(View v){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("PrefsFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int userId = 0;
        switch(v.getId()){
            case R.id.user_bethany:
                userId = 1;
                break;
            case R.id.user_charles:
                userId = 2;
                break;
            case R.id.user_judy:
                userId = 3;
                break;
            case R.id.user_emma:
                userId = 4;
                break;
            case R.id.user_samantha:
                userId = 5;
                break;
        }

        editor.putInt("user",userId);
        editor.commit();
        Toast userToast = Toast.makeText(getApplicationContext(),
                "Hi " + ((TextView) v).getText().toString() + ", your id is: "
                 + userId, Toast.LENGTH_SHORT);
        userToast.show();
    }
}
