package com.goldenpiedevs.schedule.app.activitys;

import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.modules.Const;

public class SettingActivity extends PreferenceActivity { //TODO: Update Preference Screen

    private ListPreference listPreference;
    private CheckBoxPreference checkBoxPreference;

    @Override
    @Deprecated
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(Const.SCHEDULE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), getResources().getColor(R.color.primary_dark)));
        prefMgr.setSharedPreferencesMode(MODE_PRIVATE);

        addPreferencesFromResource(com.goldenpiedevs.schedule.app.R.xml.settings);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setTitle(getResources().getString(R.string.action_settings));
        }
        checkBoxPreference = (CheckBoxPreference) findPreference("show_notification");
        listPreference = (ListPreference) findPreference("notification_delay");
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                listPreference.setEnabled(!checkBoxPreference.isChecked());
                return true;
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}