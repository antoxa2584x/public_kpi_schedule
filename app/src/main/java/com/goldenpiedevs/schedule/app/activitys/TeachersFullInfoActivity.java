package com.goldenpiedevs.schedule.app.activitys;

import android.Manifest;
import android.animation.Animator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.modules.Const;
import com.goldenpiedevs.schedule.app.modules.Location;
import com.goldenpiedevs.schedule.app.modules.Loger;
import com.goldenpiedevs.schedule.app.modules.NetworkCheck;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TeachersFullInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.location_layout)
    RelativeLayout locationLayout;
    @BindView(R.id.lesson_title)
    TextView lessonTextView;
    @BindView(R.id.adLayout)
    RelativeLayout adLayout;
    @BindView(R.id.teacher)
    TextView teacher;
    @BindView(R.id.lesson_type)
    TextView lessonType;
    @BindView(R.id.location_room)
    TextView locationRoom;
    @BindView(R.id.lesson_time_end)
    TextView lessonTimeEnd;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.map_fragment_layout)
    RelativeLayout mapFragmentLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private GoogleMap googleMap;
    private CameraPosition cameraPosition;
    private AdView mAdView;
    private Bundle b;
    private int color_scheme;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences sPref = getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE);
        b = getIntent().getExtras();
        location = new Location();

        boolean hasMenuKey = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        color_scheme = Color.parseColor(sPref.getString(Const.COLOR_SCHEME, "1"));

        try {
            initActivity();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (sPref.getBoolean("show_map", Boolean.parseBoolean(null))) {
            if (new NetworkCheck(TeachersFullInfoActivity.this).isNetworkOnline()) {
                if (Build.VERSION.SDK_INT < 16) {
                    //noinspection deprecation
                    locationLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_selector));
                } else {
                    locationLayout.setBackgroundResource(R.drawable.card_selector);
                }
                try {
                    initMap();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                mapFragmentLayout.setVisibility(View.GONE);

        } else
            mapFragmentLayout.setVisibility(View.GONE);


        configureToolbar(hasMenuKey, hasBackKey, hasHomeKey);

        if (sPref.getBoolean(Const.WRITE_LOG, Boolean.parseBoolean(null))) {
            writeSomeLog(hasMenuKey, hasBackKey, hasHomeKey);
        }

        loadAdView(sPref.getBoolean(Const.SHOW_ADS, Boolean.parseBoolean(null)));

    }

    private void configureToolbar(boolean hasMenuKey, boolean hasBackKey, boolean hasHomeKey) {
        if ((hasMenuKey && hasBackKey) || (hasHomeKey && hasBackKey)) {
            findViewById(R.id.blank_navbar).setVisibility(View.GONE);

            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                float scale = getResources().getDisplayMetrics().density;
                int left = (int) (15 * scale + 0.5f);
                int top = (int) (56 * scale + 0.5f);
                int right = (int) (8 * scale + 0.5f);
                int bottom = (int) (14 * scale + 0.5f);
                lessonTextView.setPadding(left, top, right, bottom);
            }
        }
    }


    private void writeSomeLog(boolean hasMenuKey, boolean hasBackKey, boolean hasHomeKey) {
        Log.v("Tablet", String.valueOf(isTablet()));
        Log.v("hasMenuKey", String.valueOf(hasMenuKey));
        Log.v("hasBackKey", String.valueOf(hasBackKey));
        Log.v("hasHomeKey", String.valueOf(hasHomeKey));
        Log.v("densityDpi", String.valueOf(getResources().getDisplayMetrics().densityDpi));
        Log.v("Android version", String.valueOf(Build.VERSION.SDK_INT));
        try {
            String log = "isTablet: " + String.valueOf(isTablet()) + "\nhasMenuKey: " + String.valueOf(hasMenuKey) + "\nhasBackKey: " + String.valueOf(hasBackKey) + "\nhasHomeKey: " + String.valueOf(hasHomeKey) + "\ndensityDpi: " + String.valueOf(getResources().getDisplayMetrics().densityDpi) + "\nAndroid version: " + String.valueOf(Build.VERSION.SDK_INT) + " " + String.valueOf(Build.VERSION.CODENAME);
            Loger.writeLogToFile("Log", log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAdView(boolean show_ads) {
        Log.e(Const.SHOW_ADS, String.valueOf(show_ads));
        if (show_ads) {
            adLayout.setVisibility(View.VISIBLE);
            mAdView = new AdView(this);
            mAdView.setAdUnitId(getResources().getString(R.string.ad_unit_id));
            mAdView.setAdSize(AdSize.BANNER);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            adLayout.addView(mAdView, params);
            mAdView.loadAd(new AdRequest.Builder().build());
        } else adLayout.setVisibility(View.GONE);
    }

    private void initActivity() throws InterruptedException {
        if (Build.VERSION.SDK_INT < 21) {
            scrollView.setVisibility(View.VISIBLE);
        }

        Thread thr = new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            setTaskDescription(new ActivityManager.TaskDescription(b.getString(Const.LESSON_TEACHER), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), getResources().getColor(R.color.primary_dark)));

                        lessonTimeEnd.setText(b.getString(Const.LESSON_TIME));

                        location.geolocation(b.getString(Const.CAMPUS_NUM));
                        location.run();

                        lessonTimeEnd.setText(b.getString(Const.LESSON_TIME));

                        if (b.getString(Const.GROUP_LIST) != null) {
                            RelativeLayout groupLayout = (RelativeLayout) findViewById(R.id.groups_layout);
                            groupLayout.setVisibility(View.VISIBLE);
                            TextView groupLine = (TextView) findViewById(R.id.groups_list);
                            groupLine.setText(b.getString(Const.GROUP_LIST));
                        }
                        if (b.getString(Const.LESSON_TYPE).contains("Лек")) {
                            lessonType.setText(getString(R.string.lesson_type_lecture));
                        } else if (b.getString(Const.LESSON_TYPE).contains("Прак")) {
                            lessonType.setText(getString(R.string.lesson_type_practice));
                        } else if (b.getString(Const.LESSON_TYPE).contains("Лаб")) {
                            lessonType.setText(getString(R.string.lesson_type_labwork));
                        }
                        teacher.setText(b.getString(Const.LESSON_TEACHER));
                        lessonTextView.setText(b.getString(Const.LESSON_TITLE));

                        if (!location.getRoomNum().equals("0") && !location.getCampusNum().contains("Пол.") && !location.getRoomNum().equals("ін-т ім. Амосова") && !location.getRoomNum().equals("ін-т РАКА")) {
                            locationRoom.setText(String.format("%s %s, %s %s", getString(R.string.lesson_campus), location.getCampusNum(), getString(R.string.lesson_room), location.getRoomNum()));
                        } else if (location.getRoomNum().equals("0") && location.getCampusNum().equals("24") && !location.getRoomNum().contains("ін-т ім. Амосова") && !location.getRoomNum().equals("ін-т РАКА")) {
                            lessonTextView.setText(getString(R.string.lesson_title_case_fp));
                            lessonType.setText(getString(R.string.lesson_type_practice));
                            teacher.setText(getString(R.string.lesson_teacher_case_fp));
                            locationRoom.setText(String.format("%s %s, %s", getString(R.string.lesson_campus), location.getCampusNum(), getString(R.string.lesson_room_case_fp)));
                        } else if (location.getCampusNum().contains("Пол.") && location.getCampusNum().contains("янг") && !location.getRoomNum().equals("ін-т ім. Амосова") && !location.getRoomNum().equals("ін-т РАКА")) {
                            locationRoom.setText(String.format("%s 5 (Поліклініка на Янгеля), %s %s", getString(R.string.lesson_campus), getString(R.string.lesson_room), location.getRoomNum().substring(0, location.getRoomNum().length() - 3)));
                        } else if (location.getCampusNum().contains("Пол.") && !location.getCampusNum().contains("янг") && !location.getRoomNum().equals("ін-т ім. Амосова") && !location.getRoomNum().equals("ін-т РАКА")) {
                            locationRoom.setText(String.format("%s %s, %s %s", getString(R.string.lesson_campus), getString(R.string.yangelya), getString(R.string.lesson_room), location.getRoomNum()));
                        } else if (location.getCampusNum().equals("Пол.") && location.getRoomNum().equals("ін-т ім. Амосова")) {
                            locationRoom.setText(getString(R.string.amosova));
                        } else if (location.getCampusNum().equals("Пол.") && location.getRoomNum().equals("ін-т РАКА")) {
                            locationRoom.setText(getString(R.string.rack));
                        } else if (location.getCampusNum().equals("не вказано")) {
                            mapFragmentLayout.setVisibility(View.GONE);
                            locationLayout.setBackgroundResource(0);
                            locationRoom.setText(getString(R.string.lesson_campus) + " " + location.getCampusNum());
                        }

                        if (lessonType.getText().equals("Medium Text")) {
                            RelativeLayout r = (RelativeLayout) findViewById(R.id.lesson_type_layout);
                            r.setVisibility(View.GONE);
                        }
                        if (teacher.getText().toString().contains("не указано")) {
                            teacher.setText(getString(R.string.not_set));
                        }
                        if (locationRoom.getText().toString().contains(location.getCampusNum())) {
                            locationRoom.setText(locationRoom.getText().toString().replace(location.getCampusNum() + "-", ""));
                        }

                        final RelativeLayout teacher_layout = (RelativeLayout) findViewById(R.id.teacher_layout);
                        teacher_layout.setBackgroundResource(0);
                    }
                });
            }
        });

        thr.start();

        mToolbar.post(new Runnable() {
            @Override
            public void run() {
                titleRevels(mToolbar);
            }
        });
    }

    private void titleRevels(final Toolbar revelView) {
        revelView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                final ImageView img = (ImageView) findViewById(R.id.imageView8);
//                final ImageView img1 = (ImageView) findViewById(R.id.imageView12);

                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    int cx = (revelView.getLeft() + revelView.getRight()) / 2;
                    int cy = (revelView.getTop() + revelView.getBottom()) / 2;

                    int finalRadius = Math.max(revelView.getWidth(), revelView.getHeight());

                    Animator anim =
                            ViewAnimationUtils.createCircularReveal(revelView, cx, cy, 0, finalRadius);
                    Animation show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fullinfo_appear_animation);
                    revelView.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    scrollView.startAnimation(show);
                    anim.start();

                } else {
                    revelView.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeInDown).duration(500).playOn(revelView);
                }
            }
        }, 200);
    }

    private void initMap() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map_fragment)).getMapAsync(this);
    }


    @Override
    protected void onPause() {
        if (mAdView != null)
            mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null)
            mAdView.resume();
        initMap();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null)
            mAdView.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingActivity.class);
                int RESULT_SETTINGS = 1;
                startActivityForResult(i, RESULT_SETTINGS);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    public boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        LatLng latLng;
        if (b.getDouble(Const.LATITUDE) != 0 && b.getDouble(Const.LONGITUDE) != 0)
            latLng = new LatLng(b.getDouble(Const.LATITUDE), b.getDouble(Const.LONGITUDE));
        else
            latLng = new LatLng(location.getLatitude(), location.getLongitude());

        cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();

        MarkerOptions marker = new MarkerOptions().position(latLng).title(location.getCampusNum() + getString(R.string.map_fragment_camapus_end));

        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_l));

        googleMap.addMarker(marker);

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            googleMap.setMyLocationEnabled(true);

        Display display = getWindowManager().getDefaultDisplay();
        SupportMapFragment mMapFragment = (SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment));

        ViewGroup.LayoutParams params;
        if (mMapFragment.getView() != null) {
            Point size = new Point();
            display.getSize(size);

            params = mMapFragment.getView().getLayoutParams();
            params.height = (size.x - 24) / 2;
            mMapFragment.getView().setLayoutParams(params);
        }
    }

    @OnClick(R.id.location_layout)
    protected void onLocationClick() {
        if (googleMap != null)
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
