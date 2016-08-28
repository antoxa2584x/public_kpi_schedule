package com.goldenpiedevs.schedule.app.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.goldenpiedevs.schedule.app.models.Lesson;

/**
 * Created by Anton on 20.05.2015.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Lesson lesson = (Lesson) intent.getExtras().getSerializable("lesson");
        NextLessonNotification.notify(context, lesson);
    }
}
