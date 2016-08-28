package com.goldenpiedevs.schedule.app.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.ScheduleApplication;
import com.goldenpiedevs.schedule.app.activitys.MainActivity;
import com.goldenpiedevs.schedule.app.dataloader.GlobalLoader;
import com.goldenpiedevs.schedule.app.dataloader.io.GroupIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.events.FabClickedEvent;
import com.goldenpiedevs.schedule.app.events.OnDataChangedEvent;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;
import com.goldenpiedevs.schedule.app.modules.CustomViews.ControllableNestedScrollView;
import com.goldenpiedevs.schedule.app.modules.InitCards;
import com.goldenpiedevs.schedule.app.modules.NetworkCheck;
import com.goldenpiedevs.schedule.app.widget.WidgetUtility;
import com.melnykov.fab.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import rate.AppRate;

import static com.goldenpiedevs.schedule.app.modules.Utils.checkError;

public class ScheduleFragment extends Fragment {

    @BindView(R.id.mainLayout)
    ControllableNestedScrollView scrollView;

    private boolean hasMenuKey;
    private boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    private boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
    private MaterialAutoCompleteTextView autoCompleteTextView;
    private MaterialDialog progressDialog = null;
    private Weeks weeks;

    private final BroadcastReceiver ONTIMEBRODCAST = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_DATE_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                initCard(true);
            }
        }
    };
    private FloatingActionButton fab;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.shedule_fragment_layaout, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = ((MainActivity) getActivity()).getFab();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), getResources().getColor(R.color.primary_dark)));

        ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);

        if ((hasMenuKey && hasBackKey) || (hasHomeKey && hasBackKey))
            (getActivity().findViewById(R.id.blank_navbar)).setVisibility(View.GONE);

        hasMenuKey = ViewConfiguration.get(getActivity().getApplicationContext()).hasPermanentMenuKey();


        setCards();
        initCard(true);
        actions();
        initFilters();

        ((MainActivity) getActivity()).initHeaderPic();

        AppRate.showRateDialogIfMeetsConditions(getActivity());

    }

    private SharedPreferences getPrefs() {
        return ((ScheduleApplication) getActivity().getApplication()).getsPref();
    }

    public void actions() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        if ((hasMenuKey && hasBackKey) || (hasHomeKey && hasBackKey)) {
            layoutParams.setMargins(16, 16, 16, 16);
        }
        layoutParams.setAnchorId(R.id.mainLayout);
        fab.setLayoutParams(layoutParams);
        fab.hide(false);
        fab.setColorNormal(getResources().getColor(R.color.pink));
        fab.setColorPressed(getResources().getColor(R.color.pink_dark));

        scrollView.setOnScrollDirectionListener(new ControllableNestedScrollView.ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fab.show();
            }

            @Override
            public void onScrollUp() {
                fab.hide();
            }
        });

        fab.postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.setVisibility(View.VISIBLE);
                fab.show();
            }
        }, ((MainActivity) getActivity()).isNeedAnimation ? 2000 : 500);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(FabClickedEvent event) {
        swapGroup();
    }

    public void initFilters() {
        IntentFilter s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getActivity().registerReceiver(ONTIMEBRODCAST, s_intentFilter);
    }

    public void reloadData() {
        fab.hide();
        new MaterialDialog.Builder(getActivity())
                .title("Оновити розклад")
                .content("Ви дійсно хочете оновити файл розкладу? Якщо ви маете активні нотатки, їх буду видалено.")
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.cancel))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        reloadSchedule();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        fab.show();
                    }
                })
                .build()
                .show();
    }

    public void reloadSchedule() {
        if (new NetworkCheck(getActivity()).isNetworkOnline()) {
            if (progressDialog == null)
                buildErrorDialog();

            progressDialog.show();

            new GlobalLoader(getPrefs().getString(Const.GROUP, ""), getActivity().getApplicationContext()).setStatusListener(new DownloadStatusListener() {
                @Override
                public void onComplete(boolean complete) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    new WidgetUtility(getActivity()).updateWidgets();
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }

                @Override
                public void onFailed(int status) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    Toast.makeText(getActivity().getApplicationContext(), String.format(getString(R.string.loading_error), checkError(status, getActivity().getApplicationContext())), Toast.LENGTH_SHORT).show();
                }
            }).execute();
        }
    }


    public void setCards() {
        if (!GroupIO.isGroupDownloaded(getPrefs().getString(Const.GROUP, ""), getActivity().getApplicationContext())) {
            if (new NetworkCheck(getActivity()).isNetworkOnline()) {
                if (progressDialog == null)
                    buildErrorDialog();

                progressDialog.show();

                new GlobalLoader(getPrefs().getString(Const.GROUP, ""), getActivity().getApplicationContext()).setStatusListener(new DownloadStatusListener() {
                    @Override
                    public void onComplete(boolean result) {
                        if (result) {
                            initCard(true);

                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(getActivity().getApplication(), getString(R.string.download_sucsses), Toast.LENGTH_SHORT).show();

                            new WidgetUtility(getActivity()).updateWidgets();
                        }
                    }

                    @Override
                    public void onFailed(int status) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        Toast.makeText(getActivity().getApplicationContext(), String.format(getString(R.string.loading_error), checkError(status, getActivity().getApplicationContext())), Toast.LENGTH_SHORT).show();
                    }
                }).execute();

            } else {
                Toast.makeText(getActivity(), R.string.no_internet_access, Toast.LENGTH_SHORT).show();
            }
        } else {
            weeks = new GroupIO().getGroupFromFile(getPrefs().getString(Const.GROUP, ""), getActivity().getApplicationContext());
        }
    }

    public void swapGroup() {
        fab.hide();
        View localView = getActivity().getLayoutInflater().inflate(R.layout.groupe_choose, null);
        autoCompleteTextView = (MaterialAutoCompleteTextView) localView.findViewById(R.id.coose_group_text_view);

        autoCompleteTextView.setPrimaryColor(getResources().getColor(R.color.pink));

        String[] array = getResources().getStringArray(R.array.Groups);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.select_dialog_item, array);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (autoCompleteTextView.getText().toString().contains("И")) {
                    String str = autoCompleteTextView.getText().toString().replace('И', 'І');
                    autoCompleteTextView.setText(str);
                    autoCompleteTextView.setSelection(str.length());
                }
                if (autoCompleteTextView.getText().toString().contains("и")) {
                    String str = autoCompleteTextView.getText().toString().replace('и', 'і');
                    autoCompleteTextView.setText(str);
                    autoCompleteTextView.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.choose_group_title))
                .customView(localView, false)
                .positiveText("Ок")
                .autoDismiss(false)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        fab.show();
                    }
                })
                .negativeText("Сховати")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        final String groupName = autoCompleteTextView.getText().toString();
                        if (groupName.length() != 0) {
                            dialog.dismiss();
                            if (!GroupIO.isGroupDownloaded(groupName, getActivity().getApplicationContext())) {
                                if (new NetworkCheck(getActivity()).isNetworkOnline()) {
                                    if (progressDialog == null)
                                        buildErrorDialog();

                                    progressDialog.show();

                                    new GlobalLoader(groupName, getActivity().getApplicationContext())
                                            .setStatusListener(new DownloadStatusListener() {
                                                @Override
                                                public void onComplete(boolean result) {
                                                    getPrefs().edit().putString(Const.GROUP, groupName).apply();

                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();

                                                    Intent intent = getActivity().getIntent();
                                                    getActivity().finish();
                                                    startActivity(intent);
                                                    new WidgetUtility(getActivity()).updateWidgets();
                                                }

                                                @Override
                                                public void onFailed(int status) {
                                                    if (progressDialog.isShowing())
                                                        progressDialog.dismiss();
                                                    Toast.makeText(getActivity(), String.format(getString(R.string.loading_error), checkError(status, getActivity().getApplicationContext())), Toast.LENGTH_SHORT).show();
                                                    dialog.show();
                                                }
                                            }).execute();


                                } else {
                                    Toast.makeText(getActivity(), R.string.no_internet_access, Toast.LENGTH_SHORT).show();
                                    dialog.show();
                                }
                            } else {
                                getPrefs().edit().putString(Const.GROUP, groupName).apply();
                                new WidgetUtility(getActivity()).updateWidgets();

                                Intent intent = getActivity().getIntent();
                                getActivity().finish();
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.group_dialog_incorrect_enter, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        fab.show();
                    }
                })
                .build()
                .show();

    }

    /**
     * Инициализация карточек с расписанием
     */

    private void buildErrorDialog() {
        progressDialog = new MaterialDialog.Builder(getActivity())
                .title("Зачекайте")
                .autoDismiss(false)
                .cancelable(false)
                .content(getResources().getString(R.string.loading_chedule))
                .progress(true, 0).build();
    }

    private void initCard(boolean updateAnim) {
        new InitCards(weeks, getActivity(), updateAnim, false).run();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(OnDataChangedEvent event) {
        weeks = event.getWeeks();
        initCard(false);
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(ONTIMEBRODCAST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
