package com.example.simon.androidsimongame;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

// settings page
public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    // function that goes back to main page
    public void goToMain(View view) {
        Intent nextPage = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(nextPage);
    }
}
