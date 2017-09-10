package com.goldenpiedevs.schedule.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.goldenpiedevs.schedule.app.dataloader.io.GroupIO;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;

import java.util.Locale;
import java.util.UUID;

import io.fabric.sdk.android.Fabric;
import rate.AppRate;

public class ScheduleApplication extends Application {

    private SharedPreferences sPref;
    private Weeks weeks;

    public Weeks getWeeks() {
        return weeks == null ? new GroupIO().getGroupFromFile(sPref.getString(Const.GROUP, ""), this) : weeks;
    }

    public void setWeeks(Weeks weeks) {
        this.weeks = weeks;
    }

    @Override
    public void onCreate() {
        sPref = getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE);
        AndroidNetworking.initialize(getApplicationContext());

        initRateDialog();
        addSomeUkrainianLocale();

        if (BuildConfig.DEBUG)
            Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        else
            Fabric.with(this, new Answers(), new Crashlytics());

        if (sPref.getInt("version_code", 0) < BuildConfig.VERSION_CODE)
            sPref.edit().putInt("version_code", BuildConfig.VERSION_CODE).apply();

        if (!sPref.contains("show_notification")) {
            sPref.edit().putBoolean("show_notification", false).apply();
            sPref.edit().putString("notification_delay", "15").apply();
        }

        if (!sPref.getBoolean("my_first_time", Boolean.parseBoolean(null))) {
            sPref.edit().putBoolean("show_map", true).apply();
            sPref.edit().putString(Const.COLOR_SCHEME, "#2196F3").apply();
            sPref.edit().putBoolean("today", true).apply();
            sPref.edit().putBoolean("material_header", false).apply();
            sPref.edit().putBoolean(Const.WRITE_LOG, false).apply();
            sPref.edit().putInt("version_code", BuildConfig.VERSION_CODE).apply();
            sPref.edit().putString(Const.UNIQ_ID, UUID.randomUUID().toString()).apply();

        } else {
            weeks = new GroupIO().getGroupFromFile(sPref.getString(Const.GROUP, ""), this);
        }

        super.onCreate();
    }

    public boolean isRunSchedule() {
        return sPref.getBoolean("my_first_time", Boolean.parseBoolean(null));
    }

    private void addSomeUkrainianLocale() {
        Locale locale = new Locale("uk");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void initRateDialog() {
        AppRate.with(this)
                .setInstallDays(4) // default 10, 0 means install day.
                .setLaunchTimes(10) // default 10
                .setRemindInterval(2)
                .setDebug(false)
                .monitor();
    }

    public SharedPreferences getsPref() {
        return sPref;
    }
}