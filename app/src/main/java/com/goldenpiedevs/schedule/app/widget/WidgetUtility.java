package com.goldenpiedevs.schedule.app.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;

public class WidgetUtility {
    private static Activity mActivity;

    public WidgetUtility(Activity activity) {
        mActivity = activity;
    }

    /**
     * Шлет уведомление виджету о том что надо обновить содержимое
     */
    public void updateWidgets() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppWidgetManager widgetManager = AppWidgetManager.getInstance(mActivity.getApplicationContext());
                ComponentName widgetComponent = new ComponentName(mActivity.getApplicationContext(), WidgetProvider.class);
                int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
                Intent update = new Intent();
                update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
                update.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                mActivity.sendBroadcast(update);
            }
        }, 1000);
    }
}
