package com.goldenpiedevs.schedule.app.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.activitys.MainActivity;
import com.goldenpiedevs.schedule.app.activitys.SettingActivity;
import com.goldenpiedevs.schedule.app.dataloader.io.TeacherIO;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.InitCards;

import java.util.Locale;

import butterknife.ButterKnife;

public class TeacherScheduleFragment extends Fragment {

    private static final int RESULT_SETTINGS = 1;
    public static Weeks weeks;
    public static Activity activity;
    private final BroadcastReceiver ONTIMEBRODCAST = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_DATE_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                initCard();
            }
        }
    };
    public Context context;
    private boolean hasMenuKey;
    private boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    private boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shedule_fragment_layaout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        ButterKnife.bind(this,view);

        if ((hasMenuKey && hasBackKey) || (hasHomeKey && hasBackKey))
            (getActivity().findViewById(R.id.blank_navbar)).setVisibility(View.GONE);

        weeks = new TeacherIO().getTeacherFromFile(String.valueOf(bundle.getInt("teacherId")), getActivity().getApplicationContext());

        hasMenuKey = ViewConfiguration.get(getActivity().getApplicationContext()).hasPermanentMenuKey();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().setTaskDescription(new ActivityManager.TaskDescription(bundle.getString("teacherName"), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), getResources().getColor(R.color.primary_dark)));

        ((MainActivity) getActivity()).initHeaderPic(bundle.getString("teacherName"));

        addSomeUkrainianLocale();
        initFilters();
        initCard();
    }


    private void addSomeUkrainianLocale() {
        Locale locale = new Locale("uk");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
    }



    public void initFilters() {
        IntentFilter s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getActivity().registerReceiver(ONTIMEBRODCAST, s_intentFilter);
    }

    /**
     * Инициализация карточек с расписанием
     */

    public void initCard() {
        new InitCards(weeks, getActivity(), true, true).run();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_teachers_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(getActivity().getApplicationContext(), SettingActivity.class);
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SETTINGS:
                /**
                 ──────█▀▄─▄▀▄─▀█▀─█─█─▀─█▀▄─▄▀▀▀─────
                 ──────█─█─█─█──█──█▀█─█─█─█─█─▀█─────
                 ──────▀─▀──▀───▀──▀─▀─▀─▀─▀──▀▀──────
                 ─────────────────────────────────────
                 ───────────────▀█▀─▄▀▄───────────────
                 ────────────────█──█─█───────────────
                 ────────────────▀───▀────────────────
                 ─────────────────────────────────────
                 ─────█▀▀▄─█▀▀█───█──█─█▀▀─█▀▀█─█▀▀───
                 ─────█──█─█──█───█▀▀█─█▀▀─█▄▄▀─█▀▀───
                 ─────▀▀▀──▀▀▀▀───▀──▀─▀▀▀─▀─▀▀─▀▀▀───
                 ─────────────────────────────────────
                 ─────────▄███████████▄▄──────────────
                 ──────▄██▀──────────▀▀██▄────────────
                 ────▄█▀────────────────▀██───────────
                 ──▄█▀────────────────────▀█▄─────────
                 ─█▀──██──────────────██───▀██────────
                 █▀──────────────────────────██───────
                 █──███████████████████───────█───────
                 █────────────────────────────█───────
                 █────────────────────────────█───────
                 █────────────────────────────█───────
                 █────────────────────────────█───────
                 █────────────────────────────█───────
                 █▄───────────────────────────█───────
                 ▀█▄─────────────────────────██───────
                 ─▀█▄───────────────────────██────────
                 ──▀█▄────────────────────▄█▀─────────
                 ───▀█▄──────────────────██───────────
                 ─────▀█▄──────────────▄█▀────────────
                 ───────▀█▄▄▄──────▄▄▄███████▄▄───────
                 ────────███████████████───▀██████▄───
                 ─────▄███▀▀────────▀███▄──────█─███──
                 ───▄███▄─────▄▄▄▄────███────▄▄████▀──
                 ─▄███▓▓█─────█▓▓█───████████████▀────
                 ─▀▀██▀▀▀▀▀▀▀▀▀▀███████████────█──────
                 ────█─▄▄▄▄▄▄▄▄█▀█▓▓─────██────█──────
                 ────█─█───────█─█─▓▓────██────█──────
                 ────█▄█───────█▄█──▓▓▓▓▓███▄▄▄█──────
                 ────────────────────────██──────────
                 ────────────────────────██───▄███▄───
                 ────────────────────────██─▄██▓▓▓██──
                 ───────────────▄██████████─█▓▓▓█▓▓██▄
                 ─────────────▄██▀───▀▀███──█▓▓▓██▓▓▓█
                 ─▄███████▄──███───▄▄████───██▓▓████▓█
                 ▄██▀──▀▀█████████████▀▀─────██▓▓▓▓███
                 ██▀─────────██──────────────██▓██▓███
                 ██──────────███──────────────█████─██
                 ██───────────███──────────────█─██──█
                 ██────────────██─────────────────█───
                 ██─────────────██────────────────────
                 ██─────────────███───────────────────
                 ██──────────────███▄▄────────────────
                 ███──────────────▀▀███───────────────
                 ─███─────────────────────────────────
                 ──███────────────────────────────────
                 */
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(ONTIMEBRODCAST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
