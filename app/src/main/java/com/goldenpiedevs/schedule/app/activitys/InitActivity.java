package com.goldenpiedevs.schedule.app.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.goldenpiedevs.schedule.app.BuildConfig;
import com.goldenpiedevs.schedule.app.ScheduleApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((ScheduleApplication) getApplication()).isRunSchedule()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), FirstRunActivity.class));
        }

        if (BuildConfig.DEBUG)
            checkCrash();

        finish();
    }

    private void checkCrash() {
        String trace = "";
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(openFileInput("stack.trace")));
            String line;
            while ((line = reader.readLine()) != null) {
                trace += line + "\n";
            }
        } catch (IOException ioe) {
        }

        if (trace.isEmpty())
            return;

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Error report";
        String body = "Mail this to antoxa2584@gmail: " + "\n" + trace + "\n";

        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"antoxa2584@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        startActivity(Intent.createChooser(sendIntent, "Title:"));

        deleteFile("stack.trace");
    }
}
