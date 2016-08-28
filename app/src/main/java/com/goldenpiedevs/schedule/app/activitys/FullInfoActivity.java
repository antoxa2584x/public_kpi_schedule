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
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.ScheduleApplication;
import com.goldenpiedevs.schedule.app.dataloader.io.GroupIO;
import com.goldenpiedevs.schedule.app.events.OnDataChangedEvent;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;
import com.goldenpiedevs.schedule.app.modules.CustomViews.Adapters.PhotoListAdapter;
import com.goldenpiedevs.schedule.app.modules.CustomViews.HorizontalListView;
import com.goldenpiedevs.schedule.app.modules.ImageFilePath;
import com.goldenpiedevs.schedule.app.modules.Location;
import com.goldenpiedevs.schedule.app.modules.Loger;
import com.goldenpiedevs.schedule.app.modules.NetworkCheck;
import com.goldenpiedevs.schedule.app.modules.ViewUtils;
import com.goldenpiedevs.schedule.app.widget.WidgetUtility;
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
import com.melnykov.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("ConstantConditions")
public class FullInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static ArrayList<String> photoList = new ArrayList<>();
    public Weeks weeks;
    @BindView(R.id.location_layout)
    RelativeLayout locationLayout;
    @BindView(R.id.lesson_title)
    TextView lessonTitle;
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
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.note_layout)
    RelativeLayout noteLayout;
    @BindView(R.id.HorizontalListView)
    HorizontalListView list;
    @BindView(R.id.editnote)
    MaterialEditText noteEdit;
    @BindView(R.id.textView6)
    TextView noteTitle;
    @BindView(R.id.editedtext)
    TextView editedText;
    @BindView(R.id.complete)
    ImageView complete;
    @BindView(R.id.cancel)
    ImageView cancel;
    @BindView(R.id.takephoto)
    ImageView photo;
    @BindView(R.id.photo_list_layout)
    RelativeLayout photoListLayout;
    @BindView(R.id.note_buttons_bar)
    RelativeLayout noteButtonsBar;
    @BindView(R.id.lesson_type_layout)
    RelativeLayout lessonTypeLayout;
    @BindView(R.id.teacher_layout)
    RelativeLayout teacherLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private int TAKE_PHOTO_CODE = 0;
    private int IMAGE_PICKER_SELECT = 2;
    private GoogleMap googleMap;
    private Location location;
    private CameraPosition cameraPosition;
    private AdView mAdView;

    @NonNull
    private Bundle b = new Bundle();

    private String file;
    private PhotoListAdapter adapter;

    public FullInfoActivity() {
    }

    private static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {

        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if (inputChannel != null) {
                inputChannel.close();
            }
            assert outputChannel != null;
            outputChannel.close();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.fab)
    protected void onFabClicked() {
        createNote(null);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.teacher_layout)
    protected void onTeacherLayoutClicked() {
        if (!b.getString(Const.LESSON_TEACHER).equals("не вказано")) {
            String id = b.getString(Const.TEACHER_ID);
            if (!id.contains(",")) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("teacherId", Integer.valueOf(id));
                i.putExtra("teacherName", b.getString(Const.LESSON_TEACHER));
                startActivity(i);

            } else {
                onTeacherFieldClick(id);
            }

        } else {
            teacherLayout.setBackgroundResource(0);
        }
    }

    @OnClick(R.id.location_layout)
    protected void onLocationLayoutClick() {
        if (googleMap != null)
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        b = getIntent().getExtras();

        location = new Location();

        boolean hasMenuKey = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setTaskDescription(new ActivityManager.TaskDescription(b.getString(Const.LESSON_TITLE), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), ContextCompat.getColor(this, R.color.primary_dark)));


        try {
            initActivity();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (getPrefs().getBoolean("show_map", Boolean.parseBoolean(null))) {
            if (new NetworkCheck(FullInfoActivity.this).isNetworkOnline()) {
                if (android.os.Build.VERSION.SDK_INT < 16) {
                    locationLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_selector));
                } else {
                    locationLayout.setBackgroundResource((R.drawable.card_selector));
                }
                initMap();
            } else {
                mapFragmentLayout.setVisibility(View.GONE);
            }
        } else {
            mapFragmentLayout.setVisibility(View.GONE);
        }

        configureToolbar(hasMenuKey, hasBackKey, hasHomeKey);

        if (getPrefs().getBoolean(Const.WRITE_LOG, Boolean.parseBoolean(null))) {
            writeSomeLog(hasMenuKey, hasBackKey, hasHomeKey);
        }

        loadAdView(getPrefs().getBoolean(Const.SHOW_ADS, Boolean.parseBoolean(null)));
    }


    public SharedPreferences getPrefs() {
        return ((ScheduleApplication) getApplication()).getsPref();
    }

    public Weeks getWeeks() {
        return ((ScheduleApplication) getApplication()).getWeeks();
    }

    private void configureFab(int left, int top, int right, int bottom) {
        RelativeLayout.LayoutParams fabLayoutParams = (RelativeLayout.LayoutParams) fab.getLayoutParams();
        fabLayoutParams.setMargins(left, top, right, bottom);
        fab.setLayoutParams(fabLayoutParams);
    }


    private void configureToolbar(boolean hasMenuKey, boolean hasBackKey, boolean hasHomeKey) {

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            configureFab(16, 16, 16, 16);
        }

        if ((hasMenuKey && hasBackKey) || (hasHomeKey && hasBackKey)) {
            findViewById(R.id.blank_navbar).setVisibility(View.GONE);
            configureFab(16, 16, 16, 16);

        }
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            float scale = getResources().getDisplayMetrics().density;
            int left = (int) (15 * scale + 0.5f);
            int top = (int) (56 * scale + 0.5f);
            int right = (int) (8 * scale + 0.5f);
            int bottom = (int) (14 * scale + 0.5f);
            lessonTitle.setPadding(left, top, right, bottom);
            configureFab(16, -26, 16, 16);
        }


    }

    private void writeSomeLog(boolean hasMenuKey, boolean hasBackKey, boolean hasHomeKey) {
        Log.v("Tablet", String.valueOf(isTablet()));
        Log.v("hasMenuKey", String.valueOf(hasMenuKey));
        Log.v("hasBackKey", String.valueOf(hasBackKey));
        Log.v("hasHomeKey", String.valueOf(hasHomeKey));
        Log.v("densityDpi", String.valueOf(getResources().getDisplayMetrics().densityDpi));
        Log.v("Android version", String.valueOf(android.os.Build.VERSION.SDK_INT));

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
        noteLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onNoteLongClick();
                return false;
            }
        });

        fab.setColorNormal(ContextCompat.getColor(this, R.color.pink));
        fab.setColorPressed(ContextCompat.getColor(this, R.color.pink_dark));
        noteEdit.setPrimaryColor(ContextCompat.getColor(this, R.color.pink));


        if (android.os.Build.VERSION.SDK_INT < 21) {
            scrollView.setVisibility(View.VISIBLE);
        }

        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (b.getInt(Const.WEEK_NUM)) {
                                case 0:
                                    for (int i = 0; i < getWeeks().getSizeofFirstWeek(); i++) {
                                        if (getWeeks().getDayofFirstWeek(i).getDayNumb() == b.getInt(Const.DAY_NUM)) {
                                            photoList = getWeeks().getDayofFirstWeek(i).get(b.getInt(Const.LESSON_NUM) - 1).getNote_photo();
                                        }
                                    }
                                    break;
                                case 1:
                                    for (int i = 0; i < getWeeks().getSizeofSecondWeek(); i++) {
                                        if (getWeeks().getDayofSecondWeek(i).getDayNumb() == b.getInt(Const.DAY_NUM)) {
                                            photoList = getWeeks().getDayofSecondWeek(i).get(b.getInt(Const.LESSON_NUM) - 1).getNote_photo();
                                        }
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (b.getString(Const.NOTE, "").length() >= 1)
                            onNoteExist(b.getString(Const.NOTE, ""));


                        location.geolocation(b.getString(Const.CAMPUS_NUM));
                        location.run();

                        lessonTimeEnd.setText(b.getString(Const.LESSON_TIME));

                        if (b.getString(Const.LESSON_TYPE).contains("Лек")) {
                            lessonType.setText(getString(R.string.lesson_type_lecture));
                        } else if (b.getString(Const.LESSON_TYPE).contains("Прак")) {
                            lessonType.setText(getString(R.string.lesson_type_practice));
                        } else if (b.getString(Const.LESSON_TYPE).contains("Лаб")) {
                            lessonType.setText(getString(R.string.lesson_type_labwork));
                        }
                        teacher.setText(b.getString(Const.LESSON_TEACHER));
                        lessonTitle.setText(b.getString(Const.LESSON_TITLE));

                        if (!location.getRoomNum().equals("0") && !location.getCampusNum().contains("Пол.") && !location.getRoomNum().equals("ін-т ім. Амосова") && !location.getRoomNum().equals("ін-т РАКА")) {
                            locationRoom.setText(String.format("%s %s, %s %s", getString(R.string.lesson_campus), location.getCampusNum(), getString(R.string.lesson_room), location.getRoomNum()));
                        } else if (location.getRoomNum().equals("0") && location.getCampusNum().equals("24") && !location.getRoomNum().contains("ін-т ім. Амосова") && !location.getRoomNum().equals("ін-т РАКА")) {
                            lessonTitle.setText(getString(R.string.lesson_title_case_fp));
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
                            locationRoom.setText(String.format("%s %s", getString(R.string.lesson_campus), location.getCampusNum()));
                        }

                        if (lessonType.getText().equals("Medium Text")) {
                            lessonTypeLayout.setVisibility(View.GONE);
                        }
                        if (teacher.getText().toString().contains("не указано")) {
                            teacher.setText(getString(R.string.not_set));
                        }
                        if (locationRoom.getText().toString().contains(location.getCampusNum())) {
                            locationRoom.setText(locationRoom.getText().toString().replace(location.getCampusNum() + "-", ""));
                        }

                    }
                });
            }
        }).start();

        mToolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                titleRevels(mToolbar);
            }
        }, 200);
    }

    private void titleRevels(final Toolbar revelView) {
        fab.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceIn).duration(400).playOn(fab);

        revelView.postDelayed(new Runnable() {
            @Override
            public void run() {
//                final ImageView img = (ImageView) findViewById(R.id.imageView8);
//                final ImageView img1 = (ImageView) findViewById(R.id.imageView12);

                if (android.os.Build.VERSION.SDK_INT >= 21) {
                    configureFab(16, -60, 16, 16);
                    int cx = (fab.getLeft() + fab.getRight()) / 2;
                    int cy = (fab.getTop() + fab.getBottom()) / 2;

                    int finalRadius = Math.max(revelView.getWidth(), revelView.getHeight());

                    Animator anim =
                            ViewAnimationUtils.createCircularReveal(revelView, cx, cy, 0, finalRadius);
                    Animation show = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fullinfo_appear_animation);
                    revelView.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    scrollView.startAnimation(show);
                    anim.start();
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (b.getString(Const.NOTE, "").equals("")) {
                                fab.setVisibility(View.VISIBLE);
                                fab.show(false);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });

                } else {
                    mToolbar.setVisibility(View.VISIBLE);
                    if (b.getString(Const.NOTE, "").equals("")) {
                        fab.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInDown).duration(500).playOn(revelView);
                        fab.show(false);
                    }
                }
            }
        }, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            try {
                photoList.add(file);
                updateList(photoList, true);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            photoListLayout.setVisibility(View.VISIBLE);
        } else if (requestCode == IMAGE_PICKER_SELECT && resultCode == RESULT_OK) {

            photoListLayout.setVisibility(View.VISIBLE);
            Log.i("Image picker", "picked");
            Uri selectedImageURI = data.getData();
            File inputFile = new File(ImageFilePath.getPath(getApplicationContext(), selectedImageURI));

            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/.KPI/";
            File newDir = new File(dir);

            if (!newDir.exists())
                newDir.mkdirs();

            DateFormat df = new SimpleDateFormat(Const.DATE_FORMAT);
            String sdt = df.format(new Date(System.currentTimeMillis()));
            String file_str = dir + sdt;
            File outputFile = new File(file_str + ".png");

            try {
                Log.i("Input file", inputFile.getAbsolutePath());
                Log.i("Output file", outputFile.getAbsolutePath());
                photoList.add(file_str + ".png");
                updateList(photoList, true);
                copyFileUsingFileChannels(inputFile, outputFile);
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPhotoAddClick() {
        String[] strings = {getString(R.string.pick_from_gallery), getString(R.string.take_photo)};
        new MaterialDialog.Builder(this)
                .title(getString(R.string.photo))
                .items(strings)
                .positiveText(R.string.cancel)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, IMAGE_PICKER_SELECT);
                                break;
                            case 1:
                                String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/.KPI/";
                                File newDir = new File(dir);
                                newDir.mkdirs();

                                DateFormat df = new SimpleDateFormat(Const.DATE_FORMAT);
                                String sdt = df.format(new Date(System.currentTimeMillis()));
                                file = dir + sdt + ".jpg";
                                File newFile = new File(file);
                                try {
                                    newFile.createNewFile();
                                } catch (IOException ignored) {
                                }

                                Uri outputFileUri = Uri.fromFile(newFile);
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
                                break;
                        }
                    }
                }).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                dialog.dismiss();
            }
        })
                .build()
                .show();
    }

    public void onTeacherFieldClick(final String id) {
        final String[] strings = {b.getString(Const.LESSON_TEACHER).substring(0, b.getString(Const.LESSON_TEACHER).indexOf(","))
                , b.getString(Const.LESSON_TEACHER).substring(b.getString(Const.LESSON_TEACHER).indexOf(",") + 2, b.getString(Const.LESSON_TEACHER).length())};
        new MaterialDialog.Builder(this)
                .title(getString(R.string.teacher))
                .items(strings)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        switch (which) {
                            case 0:
                                i.putExtra("teacherId", Integer.valueOf(id.substring(0, id.indexOf(","))));
                                i.putExtra("teacherName", strings[0]);
                                startActivity(i);
                                break;
                            case 1:
                                i.putExtra("teacherId", Integer.valueOf(id.substring(id.indexOf(",") + 1, id.length())));
                                i.putExtra("teacherName", strings[1]);
                                startActivity(i);
                                break;
                        }
                    }
                }).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                dialog.dismiss();
            }
        })
                .positiveText(R.string.cancel)
                .build()
                .show();
    }

    /**
     * Метод обновления списка фото
     *
     * @param arrayList Список фотографий для просмотра
     * @param delete    Нужно ли показівать значек удаления фото
     */
    public void updateList(final ArrayList<String> arrayList, boolean delete) {
        if (adapter == null) {
            adapter = new
                    PhotoListAdapter(FullInfoActivity.this, arrayList, delete, new PhotoListAdapter.AdapterChangeListener() {
                @Override
                public void onDataChanged(int arraySize) {
                    if (arrayList.size() == 0) {
                        list.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }

        list.setAdapter(adapter);
    }

    public void onNoteExist(String note) {
        final Animation hide = AnimationUtils.loadAnimation(this, R.anim.hide_animation);
        final Animation show = AnimationUtils.loadAnimation(this, R.anim.appear_animation);
        final Animation click = AnimationUtils.loadAnimation(this, R.anim.image_click);

        try {
            if (photoList.size() >= 1) {
                updateList(photoList, false);
                photoListLayout.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        fab.setVisibility(View.GONE);
        noteLayout.setVisibility(View.VISIBLE);

        noteEdit.setVisibility(View.GONE);

        editedText.setVisibility(View.VISIBLE);
        editedText.setText(note);
        noteTitle.setVisibility(View.VISIBLE);
        noteButtonsBar.setVisibility(View.GONE);
        noteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) FullInfoActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(noteEdit, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    InputMethodManager imm = (InputMethodManager) FullInfoActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(noteEdit.getWindowToken(), 0);
                }
            }
        });
        noteEdit.requestFocus();
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete.startAnimation(click);
                if (noteEdit.getText().length() != 0) {
                    noteEdit.clearFocus();
                    noteEdit.startAnimation(hide);
                    noteEdit.setVisibility(View.GONE);
                    editedText.setText(noteEdit.getText());
                    noteEdit.setText("");
                    editedText.startAnimation(show);
                    editedText.setVisibility(View.VISIBLE);
                    editedText.startAnimation(show);
                    noteTitle.setVisibility(View.VISIBLE);
                    noteButtonsBar.startAnimation(hide);
                    noteButtonsBar.setVisibility(View.GONE);

                    writeNoteToWeek(b, editedText);

                    if (photoList != null)
                        updateList(photoList, false);

                    new WidgetUtility(FullInfoActivity.this).updateWidgets();

                    EventBus.getDefault().post(new OnDataChangedEvent(getWeeks()));

                } else {
                    noteEdit.setError(getString(R.string.null_note));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel.startAnimation(click);
                completeNote();

            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.startAnimation(click);
                onPhotoAddClick();
            }
        });
    }

    private void initMap() {
        if (googleMap == null) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMapAsync(this);
        }
    }

    private void editNote() {
        createNote(editedText.getText().toString());
    }

    private void onNoteLongClick() {
        String[] strings = {getString(R.string.edit), getString(R.string.delete)};
        new MaterialDialog.Builder(this)
                .title(getString(R.string.note))
                .items(strings)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                editNote();
                                break;
                            case 1:
                                completeNote();
                                break;
                        }
                    }
                }).callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                dialog.dismiss();
            }
        })
                .positiveText(R.string.cancel).build()
                .show();
    }

    private void completeNote() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.delete_note))
                .content(getString(R.string.confirm_delete_note))
                .positiveText(getString(R.string.yes))
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        noteEdit.setText("");
                        editedText.setText("");
                        noteLayout.setVisibility(View.GONE);
                        photoListLayout.setVisibility(View.GONE);
                        removeNoteFromWeek(b);
                        for (String aPhotoList : photoList) {
                            File f = new File(aPhotoList);
                            f.delete();
                        }
                        photoList = new ArrayList<>();
                        new WidgetUtility(FullInfoActivity.this).updateWidgets();

                        EventBus.getDefault().post(new OnDataChangedEvent(getWeeks()));

                        Toast.makeText(FullInfoActivity.this, getString(R.string.note_felete_sucsses_dialog), Toast.LENGTH_SHORT).show();
                        fab.setVisibility(View.VISIBLE);
                        fab.show(false);
                        YoYo.with(Techniques.BounceIn).duration(500).playOn(fab);
                    }
                }).build()
                .show();
    }


    /**
     * Метод для создания новой заметки если его параметр null
     *
     * @param note Заметка
     */
    public void createNote(String note) {
        final Animation hide = AnimationUtils.loadAnimation(this, R.anim.hide_animation);
        final Animation show = AnimationUtils.loadAnimation(this, R.anim.appear_animation);
        final Animation click = AnimationUtils.loadAnimation(this, R.anim.image_click);

        YoYo.with(Techniques.ZoomOut).duration(500).playOn(fab);
        fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.setVisibility(View.GONE);
                fab.hide(false);
            }
        }, 500);


        noteLayout.setVisibility(View.VISIBLE);
        noteLayout.startAnimation(show);
        noteEdit.setVisibility(View.VISIBLE);
        editedText.setVisibility(View.GONE);
        noteTitle.setVisibility(View.GONE);
        noteButtonsBar.setVisibility(View.VISIBLE);

        noteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ViewUtils.showKeyboard(FullInfoActivity.this);
                } else {
                    ViewUtils.hideKeyboard(FullInfoActivity.this);
                }
            }
        });
        noteEdit.requestFocus();

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complete.startAnimation(click);
                if (noteEdit.getText().length() != 0) {
                    noteEdit.clearFocus();
                    noteEdit.startAnimation(hide);
                    noteEdit.setVisibility(View.GONE);
                    editedText.setText(noteEdit.getText());
                    noteEdit.setText("");
                    editedText.startAnimation(show);
                    editedText.setVisibility(View.VISIBLE);

                    noteTitle.startAnimation(show);
                    noteTitle.setVisibility(View.VISIBLE);

                    noteButtonsBar.startAnimation(hide);
                    noteButtonsBar.setVisibility(View.GONE);

                    writeNoteToWeek(b, editedText);

                    if (photoList != null)
                        updateList(photoList, false);

                    new WidgetUtility(FullInfoActivity.this).updateWidgets();

                    EventBus.getDefault().post(new OnDataChangedEvent(getWeeks()));

                    YoYo.with(Techniques.BounceIn).duration(400).playOn(fab);
                    fab.setVisibility(View.VISIBLE);
                    fab.show(false);

                } else {
                    noteEdit.setError(getString(R.string.null_note));
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel.startAnimation(click);
                completeNote();

            }
        });
        if (note != null) {
            noteEdit.setText(editedText.getText());
            editedText.setVisibility(View.GONE);
            noteTitle.setVisibility(View.GONE);
            noteButtonsBar.setVisibility(View.VISIBLE);
            noteEdit.setVisibility(View.VISIBLE);
            noteEdit.requestFocus();

            if (photoList.size() > 0) {
                photoListLayout.setVisibility(View.VISIBLE);
                updateList(photoList, true);
            }
        }


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo.startAnimation(click);
                onPhotoAddClick();
            }


        });

    }

    private void removeNoteFromWeek(Bundle b) {
        switch (b.getInt(Const.WEEK_NUM)) {
            case 0:
                for (int i = 0; i < getWeeks().getSizeofFirstWeek(); i++) {
                    if (getWeeks().getDayofFirstWeek(i).getDayNumb() == b.getInt(Const.DAY_NUM)) {
                        getWeeks().getDayofFirstWeek(i).get(b.getInt(Const.LESSON_NUM) - (1 - b.getInt(Const.EXTRA_LESSON_NUM))).setNote("");
                        getWeeks().getDayofFirstWeek(i).get(b.getInt(Const.LESSON_NUM) - (1 - b.getInt(Const.EXTRA_LESSON_NUM))).setNote_photo(new ArrayList<String>());
                    }
                }
                new GroupIO().writeGroupToFile(getWeeks(), getPrefs().getString(Const.GROUP, ""), getApplicationContext());
                break;
            case 1:
                for (int i = 0; i < getWeeks().getSizeofSecondWeek(); i++) {
                    if (getWeeks().getDayofSecondWeek(i).getDayNumb() == b.getInt(Const.DAY_NUM)) {
                        getWeeks().getDayofSecondWeek(i).get(b.getInt(Const.LESSON_NUM) - (1 - b.getInt(Const.EXTRA_LESSON_NUM))).setNote("");
                        getWeeks().getDayofSecondWeek(i).get(b.getInt(Const.LESSON_NUM) - (1 - b.getInt(Const.EXTRA_LESSON_NUM))).setNote_photo(new ArrayList<String>());
                    }
                }
                new GroupIO().writeGroupToFile(getWeeks(), getPrefs().getString(Const.GROUP, ""), getApplicationContext());
                break;
        }

    }

    private void writeNoteToWeek(Bundle b, TextView edited_text) {
        switch (b.getInt(Const.WEEK_NUM)) {
            case 0:
                for (int i = 0; i < getWeeks().getSizeofFirstWeek(); i++) {
                    if (getWeeks().getDayofFirstWeek(i).getDayNumb() == b.getInt(Const.DAY_NUM)) {
                        getWeeks().getDayofFirstWeek(i).get(b.getInt(Const.LESSON_NUM) - (1 - b.getInt(Const.EXTRA_LESSON_NUM))).setNote(edited_text.getText().toString());
                        getWeeks().getDayofFirstWeek(i).get(b.getInt(Const.LESSON_NUM) - (1 - b.getInt(Const.EXTRA_LESSON_NUM))).setNote_photo(photoList);
                    }
                }
                new GroupIO().writeGroupToFile(getWeeks(), getPrefs().getString(Const.GROUP, ""), getApplicationContext());
                break;

            case 1:
                for (int i = 0; i < getWeeks().getSizeofSecondWeek(); i++) {
                    if (getWeeks().getDayofSecondWeek(i).getDayNumb() == b.getInt(Const.DAY_NUM)) {
                        getWeeks().getDayofSecondWeek(i).get(b.getInt(Const.LESSON_NUM) - (1 - b.getInt(Const.EXTRA_LESSON_NUM))).setNote(edited_text.getText().toString());
                        getWeeks().getDayofSecondWeek(i).get(b.getInt(Const.LESSON_NUM) - (1 - b.getInt(Const.EXTRA_LESSON_NUM))).setNote_photo(photoList);
                    }
                }
                new GroupIO().writeGroupToFile(getWeeks(), getPrefs().getString(Const.GROUP, ""), getApplicationContext());
                break;
        }
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

        try {
            if (b.getDouble(Const.LATITUDE) != 0 && b.getDouble(Const.LONGITUDE) != 0)
                latLng = new LatLng(b.getDouble(Const.LATITUDE), b.getDouble(Const.LONGITUDE));
            else
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (NullPointerException e) {
            e.printStackTrace();
            latLng = new LatLng(50.449309, 30.460717);
        }

        cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();

        MarkerOptions marker = new MarkerOptions().position(latLng).title(location.getCampusNum() + getString(R.string.map_fragment_camapus_end));

        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker_l));

        googleMap.addMarker(marker);

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
}
