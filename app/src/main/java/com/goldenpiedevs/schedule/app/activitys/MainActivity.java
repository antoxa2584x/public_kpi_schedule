package com.goldenpiedevs.schedule.app.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.ScheduleApplication;
import com.goldenpiedevs.schedule.app.dataloader.SongDataLoader;
import com.goldenpiedevs.schedule.app.events.FabClickedEvent;
import com.goldenpiedevs.schedule.app.events.SongInfoLoaded;
import com.goldenpiedevs.schedule.app.fragments.MapFragment;
import com.goldenpiedevs.schedule.app.fragments.ScheduleFragment;
import com.goldenpiedevs.schedule.app.fragments.TeacherScheduleFragment;
import com.goldenpiedevs.schedule.app.fragments.TeachersListFragment;
import com.goldenpiedevs.schedule.app.modules.Const;
import com.goldenpiedevs.schedule.app.modules.CustomViews.ControllableAppBarLayout;
import com.goldenpiedevs.schedule.app.modules.NetworkCheck;
import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    public static final String MAIN_FRAGMENT = "Main_activity";
    public static final String MAP_FRAGMENT = "Map_activity";
    public static final String TEACHERS_LIST_FRAGMENT = "teachers_list_activity";
    public static final String RADIO_ACTIVITY = "radio";
    public static final String TEACHERS_FRAGMENT = "teachers_activity";
    public static final String GROUPS_LIST_FRAGMENT = "groups_activity";
    public static final String SETTINGS_ACTIVITY = "settings_activity";
    public boolean isNeedAnimation = true;
    public CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.main_activity)
    RelativeLayout mainLayout;
    @BindView(R.id.teachers_list_activity)
    RelativeLayout teachersListLayout;
    @BindView(R.id.map_activity)
    RelativeLayout mapLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.subtitle)
    TextView subTitle;
    @BindView(R.id.gruopTitle)
    TextView groupName;
    @BindView(R.id.image_header)
    ImageView headerImage;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.appbar)
    ControllableAppBarLayout appbar;
//    @BindView(R.id.radio_desciption)
//    TextView radioDescription;

    private String neededFragment = "";
    private String currentFragment = "";

    private Fragment fragment;
    private DrawerLayout mDrawerLayout;
    private ArrayList<RelativeLayout> layoutArrayList = new ArrayList<>();
    private Handler mHandler;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean fromFullInfo = false;

//    Runnable mStatusChecker = new Runnable() {
//        @Override
//        public void run() {
//            if (new NetworkCheck(MainActivity.this).isNetworkOnline()) {
//                new SongDataLoader().execute();
//            }
//            long mInterval = 15000;
//            mHandler.postDelayed(mStatusChecker, mInterval);
//        }
//    };

    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    protected void onFabClick() {
        EventBus.getDefault().post(new FabClickedEvent());
    }

    private void setCollapsingToolbarLayoutTitle(String title) {
        collapsingToolbar.setTitle(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_layout);
        ButterKnife.bind(this);

//        EventBus.getDefault().register(this);
//        mHandler = new Handler();
//        if (new NetworkCheck(MainActivity.this).isNetworkOnline()) {
//            new SongDataLoader().execute();
//        }
//        mStatusChecker.run();

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initHeaderPic();
        collectLayouts();

        if (getIntent().getExtras() == null || !getIntent().getExtras().containsKey("teacherName"))
            changeActivityFragment(MAIN_FRAGMENT);
        else {
            fromFullInfo = true;
            ArrayList<String> params = new ArrayList<>();
            params.add(String.valueOf(getIntent().getIntExtra("teacherId", 0)));
            params.add(getIntent().getStringExtra("teacherName"));
            changeActivityFragment(TEACHERS_FRAGMENT, params);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable
            Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mDrawerToggle.setHomeAsUpIndicator(upArrow);
            mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            mDrawerToggle.syncState();
        }
    }

//    @SuppressWarnings("unused")
//    @Subscribe
//    public void onEvent(SongInfoLoaded event) {
//        radioDescription.setVisibility(View.VISIBLE);
//        radioDescription.setText(event.getSong());
//
//    }

    public FloatingActionButton getFab() {
        return fab;
    }

    private void collectLayouts() {
        layoutArrayList.add(mainLayout);
        layoutArrayList.add(mapLayout);
        layoutArrayList.add(teachersListLayout);
    }

    private SharedPreferences getPrefs() {
        return ((ScheduleApplication) getApplication()).getsPref();
    }

    public void changeActivityFragment(String tag) {
        changeActivityFragment(tag, null);
    }

    public void changeActivityFragment(String tag, ArrayList<String> params) {
        currentFragment = tag;
        Calendar calendar = Calendar.getInstance();
        String[] str = getResources().getStringArray(R.array.Days);

        if (!tag.equals(SETTINGS_ACTIVITY)) {
            for (int i = 0; i < layoutArrayList.size(); i++) {
                int[] attrs = new int[]{android.R.attr.selectableItemBackground};
                TypedArray ta = obtainStyledAttributes(attrs);
                Drawable drawableFromTheme = ta.getDrawable(0);
                ta.recycle();
                layoutArrayList.get(i).setBackgroundDrawable(drawableFromTheme);
                ((ImageView) layoutArrayList.get(i).getChildAt(0)).clearColorFilter();
                ((TextView) layoutArrayList.get(i).getChildAt(1)).setTextColor(Color.parseColor("#7E7E7F"));
            }
        }

        Intent i;

        switch (tag) {
            case MAIN_FRAGMENT:
                setCollapsingToolbarLayoutTitle((str[calendar.get(Calendar.DAY_OF_WEEK) - 1]));
                mainLayout.setBackgroundColor(Color.parseColor("#E0E0E0"));
                ((ImageView) mainLayout.getChildAt(0)).setColorFilter(getResources().getColor(R.color.primary_dark));
                ((TextView) mainLayout.getChildAt(1)).setTextColor(getResources().getColor(R.color.primary_dark));
                fragment = new ScheduleFragment();
                break;
            case MAP_FRAGMENT:
                setCollapsingToolbarLayoutTitle(getString(R.string.map));
                mapLayout.setBackgroundColor(Color.parseColor("#E0E0E0"));

                ((ImageView) mapLayout.getChildAt(0)).setColorFilter(getResources().getColor(R.color.primary_dark));
                ((TextView) mapLayout.getChildAt(1)).setTextColor(getResources().getColor(R.color.primary_dark));
                fragment = new MapFragment();
                break;
            case TEACHERS_FRAGMENT:
                isNeedAnimation = true;
                fragment = new TeacherScheduleFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("teacherId", Integer.parseInt(params.get(0)));
                bundle.putString("teacherName", params.get(1));

                fragment.setArguments(bundle);
                break;
            case TEACHERS_LIST_FRAGMENT:
                setCollapsingToolbarLayoutTitle(getString(R.string.title_activity_teachers_list));
                teachersListLayout.setBackgroundColor(Color.parseColor("#E0E0E0"));

                ((ImageView) teachersListLayout.getChildAt(0)).setColorFilter(getResources().getColor(R.color.primary_dark));
                ((TextView) teachersListLayout.getChildAt(1)).setTextColor(getResources().getColor(R.color.primary_dark));
                fragment = new TeachersListFragment();
                break;
//            case GROUPS_LIST_FRAGMENT:
//                groupsListLayout.setBackgroundColor(Color.parseColor("#E0E0E0"));
//
//                ((ImageView) groupsListLayout.getChildAt(0)).setColorFilter(getResources().getColor(R.color.primary_dark));
//                ((TextView) groupsListLayout.getChildAt(1)).setTextColor(getResources().getColor(R.color.primary_dark));
//                break;
            case RADIO_ACTIVITY:
                if (new NetworkCheck(MainActivity.this).isNetworkOnline()) {
                    i = new Intent(this, RadioActivity.class);
                    startActivity(i);
                    neededFragment = MAIN_FRAGMENT;
                    return;
                } else {
                    Toast.makeText(this, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
                }
            case SETTINGS_ACTIVITY:
                i = new Intent(this, SettingActivity.class);
                startActivity(i);
                neededFragment = MAIN_FRAGMENT;
                return;
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.content_frame, fragment, tag)
                .commit();

        getSupportFragmentManager().popBackStackImmediate();

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

        if (tag.equals(MAIN_FRAGMENT)) {
            if (!isNeedAnimation)
                appbar.collapseToolbar();
        } else {
            fab.setVisibility(View.GONE);
            fab.hide(false);

            if (tag.equals(TEACHERS_FRAGMENT))
                appbar.expandToolbar(true);
            else
                appbar.collapseToolbar(true);

            layoutParams.setAnchorId(View.NO_ID);
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
            neededFragment = null;
        }

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        menu.findItem(R.id.reload_data).setVisible(currentFragment.equals(MAIN_FRAGMENT));
        menu.findItem(R.id.action_search).setVisible(currentFragment.equals(TEACHERS_LIST_FRAGMENT));
        return true;
    }

    private void closeDrawer(String tag) {
        if (currentFragment.equals(tag)) {
            mDrawerLayout.closeDrawers();
            neededFragment = null;
            return;
        }
        neededFragment = tag;
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload_data:
                ((ScheduleFragment) fragment).reloadData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.main_activity)
    protected void onMainActivityClicked() {
        closeDrawer(MAIN_FRAGMENT);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.teachers_list_activity)
    protected void onTeachersListActivityClicked() {
        closeDrawer(TEACHERS_LIST_FRAGMENT);
    }

//    @OnClick(R.id.groups_list_activity)
//    protected void onGroupsListActivityClicked() {
//        Toast.makeText(this, "onGroupsListActivityClicked", Toast.LENGTH_SHORT).show();
//    }

    @SuppressWarnings("unused")
    @OnClick(R.id.settings_activity)
    protected void onSettingsActivityClicked() {
        mDrawerLayout.closeDrawers();

        Intent i = new Intent(this, SettingActivity.class);
        startActivity(i);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.map_activity)
    protected void onMapActivityClicked() {
        neededFragment = MAP_FRAGMENT;
        mDrawerLayout.closeDrawers();
    }
//
//    @SuppressWarnings("unused")
//    @OnClick(R.id.radio_activity)
//    protected void onRadioActivityClicked() {
//        mDrawerLayout.closeDrawers();
//
//        Intent i = new Intent(this, RadioActivity.class);
//        startActivity(i);
//    }


    @Override
    public void onBackPressed() {
        if (fromFullInfo) {
            finish();
            return;
        }

        ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT);
        TeacherScheduleFragment teacherScheduleFragment = (TeacherScheduleFragment) getSupportFragmentManager().findFragmentByTag(TEACHERS_FRAGMENT);
        if (scheduleFragment == null && teacherScheduleFragment == null) {
            fragment = new ScheduleFragment();
            changeActivityFragment(MAIN_FRAGMENT);
            return;
        } else if (teacherScheduleFragment != null) {
            fragment = new TeachersListFragment();
            changeActivityFragment(TEACHERS_LIST_FRAGMENT);
            return;
        }
        super.onBackPressed();

    }


    @SuppressWarnings("deprecation")
    public void initHeaderPic() {
        initHeaderPic(null);
    }

    @SuppressWarnings("deprecation")
    public void initHeaderPic(String title) {
        Random r = new Random();
        Drawable image = getResources().getDrawable(getResources().getIdentifier("drawable/img_" + String.valueOf(r.nextInt(15) + 1), null, getApplicationContext().getPackageName()));
        headerImage.setImageDrawable(image);
        initHeader(title);
    }

    @SuppressLint("SimpleDateFormat")
    private void initHeader(String title) {
        if (mDrawerLayout == null)
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (!fromFullInfo) {
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    if (neededFragment != null)
                        changeActivityFragment(neededFragment);

                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };

            mDrawerLayout.setDrawerListener(mDrawerToggle);

            mDrawerToggle.syncState();
        }
        Calendar calendar = Calendar.getInstance();
        String[] str = getResources().getStringArray(R.array.Days);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setTitle(title == null ? str[calendar.get(Calendar.DAY_OF_WEEK) - 1] : title);
        groupName.setVisibility(title == null ? View.VISIBLE : View.GONE);

        groupName.setText(getPrefs().getString(Const.GROUP, ""));
        subTitle.setText(String.format("%s , %d-ий тиждень",
                new SimpleDateFormat(getString(R.string.date_format)).format(new Date()),
                (calendar.get(Calendar.WEEK_OF_YEAR) % 2) + 1));
    }
}
