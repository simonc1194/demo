package com.example.simon.androidsimongame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // creates the main page of the game
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // function that goes to settings page
    public void goToSettings(View view) {
        Intent nextPage = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(nextPage);
    }

    // function that goes to game page
    public void goToStart(View view) {
        Intent nPage = new Intent(MainActivity.this, StartActivity.class);
        startActivity(nPage);
    }
}
