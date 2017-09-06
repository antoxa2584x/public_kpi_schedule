package com.goldenpiedevs.schedule.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.activitys.FullInfoActivity;
import com.goldenpiedevs.schedule.app.dataloader.io.GroupIO;
import com.goldenpiedevs.schedule.app.fragments.ScheduleFragment;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;

import java.util.Calendar;

public class WidgetProvider extends AppWidgetProvider {

    public static SharedPreferences sPref;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        if (GroupIO.isGroupDownloaded(context.getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE).getString(Const.GROUP, ""), context)) {
            for (int appWidgetId : appWidgetIds) {
                RemoteViews remoteViews = updateWidgetListView(context,
                        appWidgetId);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.cardListView);
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private RemoteViews updateWidgetListView(Context context, int appWidgetId) { //TODO: Fix Data get

        Calendar calendar = Calendar.getInstance();
        sPref = context.getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.app_widget);

        if (sPref.getString(Const.GROUP, "") != null && (!sPref.getString(Const.GROUP, "").equals(""))) {
            Intent clickIntent = new Intent(context, FullInfoActivity.class);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId).addFlags(
                    Intent.FLAG_ACTIVITY_TASK_ON_HOME).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                    clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.cardListView, clickPI);

            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(appWidgetId, R.id.cardListView,
                    svcIntent);

            String[] days = context.getResources().getStringArray(R.array.Days);
            Weeks weeks = new GroupIO().getGroupFromFile(sPref.getString(Const.GROUP, ""), context);

            if(weeks == null)
                return remoteViews;

            int dow = -666;

            int week = ((calendar.get(Calendar.WEEK_OF_YEAR) % 2) + 1);

            if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0) {
                for (int i = 0; i < weeks.getSizeofFirstWeek(); i++) {
                    if (weeks.getDayofFirstWeek(i).getDayNumb() == calendar.get(Calendar.DAY_OF_WEEK) - 1) {
                        dow = weeks.getDayofFirstWeek(i).getDayNumb() + 1;
                    }
                }
                if (dow != calendar.get(Calendar.DAY_OF_WEEK)) {
                    for (int i = 0; i < weeks.getSizeofFirstWeek(); i++) {
                        if (weeks.getDayofFirstWeek(i).getDayNumb() == calendar.get(Calendar.DAY_OF_WEEK)) {
                            dow = weeks.getDayofFirstWeek(i).getDayNumb() + 1;
                        }
                    }
                    if (dow != calendar.get(Calendar.DAY_OF_WEEK)) {
                        dow = weeks.getDayofFirstWeek(0).getDayNumb() + 1;
                        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            week = 2;
                        }
                    }
                }

            } else if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) {
                for (int i = 0; i < weeks.getSizeofSecondWeek(); i++) {
                    if (weeks.getDayofSecondWeek(i).getDayNumb() == calendar.get(Calendar.DAY_OF_WEEK) - 1) {
                        dow = weeks.getDayofSecondWeek(i).getDayNumb() + 1;
                    }
                }
                if (dow != calendar.get(Calendar.DAY_OF_WEEK)) {
                    for (int i = 0; i < weeks.getSizeofSecondWeek(); i++) {
                        if (weeks.getDayofSecondWeek(i).getDayNumb() == calendar.get(Calendar.DAY_OF_WEEK)) {
                            dow = weeks.getDayofSecondWeek(i).getDayNumb() + 1;
                        }
                    }
                    if (dow != calendar.get(Calendar.DAY_OF_WEEK)) {
                        dow = weeks.getDayofSecondWeek(0).getDayNumb() + 1;
                        if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                            week = 1;
                        }
                    }
                }
            }

            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 0) {
                    week = 2;

                } else if (calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1) {
                    week = 1;
                }
            }

            remoteViews.setTextViewText(R.id.widget_week, String.valueOf(week) + "-ий тиждень");

            sPref.edit().putInt("widget_day", dow).apply();
            sPref.edit().putInt("widget_week", week).apply();

            remoteViews.setTextViewText(R.id.widget_card_header, days[dow - 1]);

            remoteViews.setViewVisibility(R.id.widget_header, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.cardListView, View.VISIBLE);

            remoteViews.setViewVisibility(R.id.empty_layout, View.GONE);

            Intent intent = new Intent(context, ScheduleFragment.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_header, pendIntent);
            remoteViews.setOnClickPendingIntent(R.id.imageView, pendIntent);

        } else {
            remoteViews.setViewVisibility(R.id.widget_header, View.GONE);
            remoteViews.setViewVisibility(R.id.cardListView, View.GONE);
            remoteViews.setViewVisibility(R.id.empty_layout, View.VISIBLE);
        }

        return remoteViews;
    }


    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equalsIgnoreCase(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            updateWidget(context);
            Log.e("onReceive", "onReceive1");
        }

    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        onUpdate(context, appWidgetManager, appWidgetIds);
    }

}