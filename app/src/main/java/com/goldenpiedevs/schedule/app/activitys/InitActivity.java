package com.goldenpiedevs.schedule.app.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.goldenpiedevs.schedule.app.ScheduleApplication;


public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((ScheduleApplication) getApplication()).isRunSchedule()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), FirstRunActivity.class));
        }

        finish();
    }
}
