package com.example.simon.androidsimongame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

// game page
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    // function that goes back to main page
    public void goToMain_Start(View view) {
        Intent nextPage = new Intent(StartActivity.this, MainActivity.class);
        startActivity(nextPage);
    }
}
