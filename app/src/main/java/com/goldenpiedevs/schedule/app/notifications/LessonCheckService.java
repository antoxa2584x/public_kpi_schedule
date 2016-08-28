package com.goldenpiedevs.schedule.app.notifications;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.dataloader.io.GroupIO;
import com.goldenpiedevs.schedule.app.models.Lesson;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LessonCheckService extends IntentService {

    private static final IntentFilter s_intentFilter;

    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
    }

    private Weeks weeks;
    private int dayOfWeek;
    private int week;
    private String[] times;
    private int lessonNumber;
    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                Calendar c = Calendar.getInstance();
                dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 2;
                week = ((c.get(Calendar.WEEK_OF_YEAR) % 2));

                int offset = Integer.valueOf(getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE).getString("notification_delay", ""));
                c.add(Calendar.MINUTE, offset);

                try {
                    lessonNumber = lessonNumber(c);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                checkTime(c);
            }
        }
    };
    private Calendar minDate;
    private Calendar maxDate;

    public LessonCheckService() {
        super("NotificationService");
    }

    private void checkTime(Calendar date) {
//        Date currentTime = date.getTime();
//        if (currentTime.after(minDate.getTime()) && currentTime.before(maxDate.getTime())) {
        checkLesson(date);
//        }

    }

    private void checkLesson(Calendar date) {
        Lesson lesson = null;

        if (lessonNumber != -1) {
            if (week == 0) {
                if (weeks.getSizeofFirstWeek() >= dayOfWeek
                        && weeks.getDayofFirstWeek(dayOfWeek).getDayNumb() == dayOfWeek + 1
                        && weeks.getDayofFirstWeek(dayOfWeek).size() >= lessonNumber
                        && weeks.getDayofFirstWeek(dayOfWeek).isLessonExist(lessonNumber))
                    lesson = weeks.getDayofFirstWeek(dayOfWeek).getLessonByNumber(lessonNumber);

            } else if (week == 1) {
                if (weeks.getSizeofSecondWeek() >= dayOfWeek
                        && weeks.getDayofSecondWeek(dayOfWeek).getDayNumb() == dayOfWeek + 1
                        && weeks.getDayofSecondWeek(dayOfWeek).size() >= lessonNumber
                        && weeks.getDayofSecondWeek(dayOfWeek).isLessonExist(lessonNumber))
                    lesson = weeks.getDayofSecondWeek(dayOfWeek).getLessonByNumber(lessonNumber);
            }
        }

        if (lesson != null) {
            if (needNotification(date)) {
                Intent i = new Intent(getApplicationContext(), NotificationReceiver.class);
                i.putExtra("lesson", lesson);
                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                Calendar c = Calendar.getInstance();
                c.add(Calendar.SECOND, 10);
                long daley = c.getTimeInMillis();
                alarmManager.set(AlarmManager.RTC, daley, PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0));
            }
        }
    }

    private boolean needNotification(Calendar date) {
        @SuppressLint("SimpleDateFormat") String currentTime = new SimpleDateFormat("HH:mm").format(date.getTime());
        return times[lessonNumber].substring(0, times[lessonNumber].indexOf("–")).equals(currentTime);
    }


    @SuppressLint("SimpleDateFormat")
    private int lessonNumber(Calendar date) throws ParseException {
        int lessonNumber = -1;

        String currentTime = new SimpleDateFormat("HH:mm").format(date.getTime());

        for (int i = 0; i < times.length; i++) {
            if (times[i].substring(0, times[i].indexOf("–")).equals(currentTime)) {
                return i;
            }
        }

        return lessonNumber;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void startThisSheet() {
        if (getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE).contains(Const.GROUP)) {
            weeks = new GroupIO().getGroupFromFile(getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE).getString(Const.GROUP, ""), getApplicationContext());
            times = getApplicationContext().getResources().getStringArray(R.array.Times);
            registerReceiver(m_timeChangedReceiver, s_intentFilter);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE).getBoolean("show_notification", Boolean.parseBoolean(null))) {
            startThisSheet();
            return START_STICKY;
        } else {
            stopSelf();
            return START_NOT_STICKY;
        }

    }
}
