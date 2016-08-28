package com.goldenpiedevs.schedule.app.activitys;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.ScheduleApplication;
import com.goldenpiedevs.schedule.app.dataloader.GlobalLoader;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.modules.Const;
import com.goldenpiedevs.schedule.app.modules.NetworkCheck;
import com.goldenpiedevs.schedule.app.widget.WidgetUtility;
import com.melnykov.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.goldenpiedevs.schedule.app.modules.Utils.checkError;


public class FirstRunActivity extends Activity {

    @BindView(R.id.coose_group_text_view)
    MaterialAutoCompleteTextView autoCompleteTextView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    private boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
    private MaterialDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);
        ButterKnife.bind(this);
        boolean hasMenuKey = ViewConfiguration.get(getApplicationContext()).hasPermanentMenuKey();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), getResources().getColor(R.color.primary_dark)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(FirstRunActivity.this,
                R.layout.select_dialog_item, getResources().getStringArray(R.array.Groups)));

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    hideKeyboard();
                    onGroupChosen();
                    fab.hide();
                }
                return false;
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (autoCompleteTextView.getText().toString().contains("И") || autoCompleteTextView.getText().toString().contains("и")) {
                    String str = null;
                    if (autoCompleteTextView.getText().toString().contains("И")) {
                        str = autoCompleteTextView.getText().toString().replace('И', 'І');
                    }
                    if (autoCompleteTextView.getText().toString().contains("и")) {
                        str = autoCompleteTextView.getText().toString().replace('и', 'і');
                    }

                    autoCompleteTextView.setText(str);

                    assert str != null;
                    autoCompleteTextView.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                onGroupChosen();
                fab.hide();
            }
        });

        if ((hasMenuKey || hasHomeKey) && hasBackKey) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fab.getLayoutParams();
            layoutParams.setMargins(16, 16, 16, 16);
            fab.setLayoutParams(layoutParams);
        }

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onGroupChosen() {
        if (new NetworkCheck(FirstRunActivity.this).isNetworkOnline()) {
            if (progressDialog == null)
                progressDialog = new MaterialDialog.Builder(FirstRunActivity.this)
                        .title("Зачекайте")
                        .autoDismiss(false)
                        .cancelable(false)
                        .content(getResources().getString(R.string.loading_chedule))
                        .progress(true, 0).build();

            progressDialog.show();

            String groupName = autoCompleteTextView.getText().toString();

            if (!TextUtils.isEmpty(groupName)) {
                getPrefs().edit().putString(Const.GROUP, groupName).apply();
                new GlobalLoader(groupName, this)
                        .setStatusListener(new DownloadStatusListener() {
                            @Override
                            public void onComplete(boolean complete) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();

                                getPrefs().edit().putBoolean("my_first_time", true).apply();
                                new WidgetUtility(FirstRunActivity.this).updateWidgets();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();

                            }

                            @Override
                            public void onFailed(int status) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Toast.makeText(FirstRunActivity.this, String.format(getString(R.string.loading_error), checkError(status, getApplicationContext())), Toast.LENGTH_SHORT).show();
                                fab.show();
                            }
                        }).execute();

            } else {
                autoCompleteTextView.setError(getResources().getString(R.string.group_dialog_incorrect_enter));
            }
        } else {
            Toast.makeText(FirstRunActivity.this, R.string.no_internet_access, Toast.LENGTH_SHORT).show();
        }
    }

    private SharedPreferences getPrefs() {
        return ((ScheduleApplication) getApplication()).getsPref();
    }

}
