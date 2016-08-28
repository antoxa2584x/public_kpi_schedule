package com.goldenpiedevs.schedule.app.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.activitys.FullInfoActivity;
import com.goldenpiedevs.schedule.app.models.Lesson;
import com.goldenpiedevs.schedule.app.modules.Const;

import java.util.Calendar;

/**
 * Helper class for showing and canceling new message
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class NextLessonNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Rozklad_KPI_Notification";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * TODO: Customize this method's arguments to present relevant content in
     * the notification.
     * <p>
     * TODO: Customize the contents of this method to tweak the behavior and
     * presentation of new message notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context,
                              Lesson lesson) {
        final Resources res = context.getResources();


        Intent intent = new Intent(context, FullInfoActivity.class);
        intent = addIntentData(intent, lesson, context);


        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);

        final SpannableStringBuilder teacherItem = new SpannableStringBuilder();
        teacherItem.append("Викладач: ").setSpan(new ForegroundColorSpan(Color.BLACK), 0, teacherItem.length(), 0);
        if (lesson.getTeachers().size() > 1) {
            teacherItem.append(lesson.getTeachers().get(0)[1]).append(", ").append(lesson.getTeachers().get(1)[1]);
        } else
            teacherItem.append(lesson.getTeachers().get(0)[1]);

        final SpannableStringBuilder typeItem = new SpannableStringBuilder();
        typeItem.append("Тип: ").setSpan(new ForegroundColorSpan(Color.BLACK), 0, typeItem.length(), 0);

        switch (lesson.getClassesType()) {
            case "Лек":
                typeItem.append(context.getString(R.string.lesson_type_lecture));
                break;
            case "Прак":
                typeItem.append(context.getString(R.string.lesson_type_practice));
                break;
            case "Лаб":
                typeItem.append(context.getString(R.string.lesson_type_labwork));
                break;
            default:
                typeItem.append(lesson.getClassesType());
                break;
        }

        final SpannableStringBuilder locationItem = new SpannableStringBuilder();
        locationItem.append("Розташування: ").setSpan(new ForegroundColorSpan(Color.BLACK), 0, locationItem.length(), 0);
        locationItem.append(lesson.getRoomLocation());

        final SpannableStringBuilder timeItem = new SpannableStringBuilder();
        timeItem.append("Час: ").setSpan(new ForegroundColorSpan(Color.BLACK), 0, timeItem.length(), 0);
        timeItem.append(context.getResources().getStringArray(R.array.Times)[lesson.getItemNumber()]);

        final String title = String.format("Наступна пара: %s", lesson.getFullName());

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText("Розгорнiть, для бiльш детальниої інформації")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setTicker(lesson.getFullName())
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(typeItem)
                        .addLine(teacherItem)
                        .addLine(locationItem)
                        .addLine(timeItem))
                .addAction(
                        R.drawable.ic_action_stat_reply,
                        "Детальніше",
                        PendingIntent.getActivity(context, 0, intent, 0))
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    private static Intent addIntentData(Intent intent, Lesson lesson, Context context) {
        int lessonNumber = lesson.getItemNumber();
        intent.putExtra(Const.LESSON_TITLE, lesson.getFullName());
        intent.putExtra(Const.CAMPUS_NUM, lesson.getRoomLocation());
        intent.putExtra(Const.LESSON_TYPE, lesson.getClassesType());
        intent.putExtra(Const.LESSON_TIME, context.getResources().getStringArray(R.array.Times)[lesson.getItemNumber()]);
        intent.putExtra(Const.LESSON_NUM, lessonNumber);
        intent.putExtra(Const.NOTE, lesson.getNote());
        intent.putExtra(Const.EXTRA_LESSON_NUM, 0);
        intent.putExtra(Const.WEEK_NUM, Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) % 2);
        intent.putExtra(Const.DAY_NUM, Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
        intent.putExtra(Const.LATITUDE, lesson.getLatitude());
        intent.putExtra(Const.LONGITUDE, lesson.getLongitude());


        String teacher = null;
        String teacher_id = null;
        for (int i = 0; i < lesson.getTeachers().size(); i++) {
            if (i >= 1) {
                teacher = lesson.getTeacher() + ", " + lesson.getTeachers().get(i)[1];
                teacher_id = lesson.getTeacherID() + "," + lesson.getTeachers().get(i)[0];
            } else {
                teacher = lesson.getTeachers().get(i)[1];
                teacher_id = lesson.getTeachers().get(i)[0];
            }
        }

        intent.putExtra(Const.LESSON_TEACHER, teacher);
        intent.putExtra(Const.TEACHER_ID, teacher_id);

        return intent;
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, Lesson)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}