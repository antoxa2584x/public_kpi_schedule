package com.goldenpiedevs.schedule.app.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.activitys.MainActivity;
import com.goldenpiedevs.schedule.app.dataloader.GlobalLoader;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.CustomViews.Adapters.CardAdapter;
import com.goldenpiedevs.schedule.app.modules.CustomViews.ControllableAppBarLayout;
import com.goldenpiedevs.schedule.app.modules.CustomViews.ControllableNestedScrollView;

import it.gmariotti.cardslib.library.view.CardViewNative;

public class InitCards implements Runnable {
    public SharedPreferences sPref;
    public TextView text;
    public boolean needRefresh;
    public boolean teacherActivity;
    private Activity activity;
    private Weeks weeks;
    private CardViewNative cardView;
    private int card_day = -1;
    private LinearLayout linearLayout;
    private Animation show;
    private CardAdapter card;
    private boolean isNeedAnimation;


    /**
     * Инициализация класса обновления карточек
     *
     * @param weeks       Обект класса @Weeks
     * @param activity    Activity приложения
     * @param needRefresh Нужно ли анимация карточек
     */

    public InitCards(Weeks weeks, Activity activity, boolean needRefresh, boolean teacherActivity) {
        this.activity = activity;
        this.weeks = weeks;
        this.needRefresh = needRefresh;
        this.teacherActivity = teacherActivity;
        this.sPref = activity.getApplicationContext().getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE);
        this.isNeedAnimation = ((MainActivity) activity).isNeedAnimation;
    }

    /**
     * Метод обновления карточек
     */
    public void start() {
        cardView = new CardViewNative(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                        show = AnimationUtils.loadAnimation(activity, R.anim.google_like);

                        if (isNeedAnimation)
                            activity.findViewById(R.id.mainLayout).scrollTo(0, 0);

                        for (int i = 0; i < weeks.getSizeofFirstWeek(); i++) {
                            card = new CardAdapter(activity, weeks.getDayofFirstWeek(i), 1, teacherActivity);
                            card.init();

                            int resID = activity.getResources().getIdentifier("lessoncard" + i, "id", "com.goldenpiedevs.schedule.app");
                            cardView = (CardViewNative) activity.findViewById(resID);

                            if (cardView.getCard() == null) {
                                cardView.setCard(card);
                            } else {
                                cardView.replaceCard(card);
                            }

                            if (isNeedAnimation && needRefresh && i < 4) {
                                if (!isTablet()) {
                                    cardView.startAnimation(show);
                                }
                            }
                            text = (TextView) cardView.findViewById(R.id.card_header_subtitle);
                            if (text.getText().toString().contains(activity.getString(R.string.today))) {
                                card_day = resID;
                            }
                            cardView.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < weeks.getSizeofSecondWeek(); i++) {
                            card = new CardAdapter(activity, weeks.getDayofSecondWeek(i), 2, teacherActivity);
                            card.init();

                            int resID = activity.getResources().getIdentifier("lessoncard" + (i + 6), "id", "com.goldenpiedevs.schedule.app");
                            cardView = (CardViewNative) activity.findViewById(resID);
                            if (cardView.getCard() == null) {
                                cardView.setCard(card);
                            } else {
                                cardView.replaceCard(card);
                            }
                            text = (TextView) cardView.findViewById(R.id.card_header_subtitle);
                            if (text.getText().toString().contains(activity.getString(R.string.today))) {
                                card_day = resID;
                            }
                            cardView.setVisibility(View.VISIBLE);
                        }

                        if (needRefresh) {
                            linearLayout = (LinearLayout) activity.findViewById(R.id.contentlayout);
                            linearLayout.setVisibility(View.VISIBLE);
                        }

                        if (sPref.getBoolean("today", Boolean.parseBoolean(null))) {
                            if (needRefresh && card_day != -1) {

                                cardView = (CardViewNative) activity.findViewById(card_day);

                                final ControllableNestedScrollView observableScrollView = (ControllableNestedScrollView) activity.findViewById(R.id.mainLayout);
                                final ControllableAppBarLayout controllableAppBarLayout = (ControllableAppBarLayout) activity.findViewById(R.id.appbar);
                                controllableAppBarLayout.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        controllableAppBarLayout.collapseToolbar(true);
                                        if (isNeedAnimation)
                                            observableScrollView.smoothScrollTo(0, (((cardView.getBottom() + cardView.getTop()) / 2) - activity.getResources().getDimensionPixelSize(R.dimen.header_size)));
                                        else
                                            observableScrollView.scrollTo(0, (((cardView.getBottom() + cardView.getTop()) / 2) - activity.getResources().getDimensionPixelSize(R.dimen.header_size)));

                                        ((MainActivity) activity).isNeedAnimation = false;
                                    }
                                }, isNeedAnimation ? 1500 : 0);


                            }
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                    onError();
                }
            }
        });
    }

    private void onError() {
        new MaterialDialog.Builder(activity)
                .title("Помилка")
                .content(activity.getString(R.string.seems_lie_error))
                .positiveText(activity.getString(R.string.yes))
                .negativeText(activity.getString(R.string.cancel))
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        final MaterialDialog progressDialog = new MaterialDialog.Builder(activity)
                                .title("Зачекайте")
                                .autoDismiss(false)
                                .cancelable(false)
                                .content(activity.getResources().getString(R.string.loading_chedule))
                                .progress(true, 0)
                                .show();

                        new GlobalLoader(sPref.getString(Const.GROUP, ""), activity.getApplicationContext()).setStatusListener(new DownloadStatusListener() {
                            @Override
                            public void onComplete(boolean complete) {
                                progressDialog.dismiss();
                                Intent intent = activity.getIntent();
                                activity.finish();
                                activity.startActivity(intent);
                            }

                            @Override
                            public void onFailed(int status) {
                                new Thread() {
                                    public void run() {
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                onError();
                                            }
                                        });
                                    }
                                }.start();
                            }
                        }).execute();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        activity.finish();
                    }
                }).build()
                .show();
    }

    /**
     * Вызов метода обновления карточек в отдельном потоке
     */
    @Override
    public void run() {
        try {
            start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Проверка на планшет
     *
     * @return true Если планшет
     */
    public boolean isTablet() {
        return (activity.getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public float density() {
        return activity.getResources().getDisplayMetrics().density;
    }

}
