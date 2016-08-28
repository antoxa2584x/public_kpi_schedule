package com.goldenpiedevs.schedule.app.modules;

import com.goldenpiedevs.schedule.app.models.Day;
import com.goldenpiedevs.schedule.app.models.Weeks;

import java.util.Calendar;
import java.util.Date;


public class NextLessonDateGetter {
    private final static int[][] lessonsTime = {{8, 30, 10, 5}, {10, 25, 12, 0}, {12, 20, 13, 55}, {14, 15, 15, 50}, {16, 10, 17, 45}};
    private final Weeks Schedule;

    public NextLessonDateGetter(Weeks schedule) {
        Schedule = schedule;
    }

    public long getNextLessonDate() {
        if (NextParaExist(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1, Weeks.isWeekEven())) {
            Calendar cal = Calendar.getInstance();
            int paraNumb;
            for (paraNumb = 0; paraNumb < lessonsTime.length; paraNumb++) {
                if (Weeks.isWeekEven()) {
                    if (
                            (lessonsTime[paraNumb][0] * 60 + lessonsTime[paraNumb][1] > cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR_OF_DAY) * 60) &&
                                    (Schedule.getDayofFirstWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).isLessonExist(paraNumb))
                            ) break;
                } else {
                    if (
                            (lessonsTime[paraNumb][0] * 60 + lessonsTime[paraNumb][1] > cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR_OF_DAY) * 60) &&
                                    (Schedule.getDayofSecondWeek(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).isLessonExist(paraNumb))
                            ) break;
                }
            }
            cal = Calendar.getInstance();
            cal.set(Calendar.MINUTE, lessonsTime[paraNumb][1]);
            cal.set(Calendar.HOUR_OF_DAY, lessonsTime[paraNumb][0]);
            return cal.getTime().getTime();
        } else {
            int dayNumb = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (Weeks.isWeekEven()) {
                do {
                    if (dayNumb != 7) ++dayNumb;
                    else dayNumb = 1;
                }
                while (Schedule.getDayofFirstWeek(dayNumb).LessonsToday() == 0);
            } else {
                do {
                    if (dayNumb != 7) ++dayNumb;
                    else dayNumb = 1;
                }
                while (Schedule.getDayofSecondWeek(dayNumb).LessonsToday() == 0);
            }
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, dayNumb);
            cal.set(Calendar.MINUTE, lessonsTime[0][1]);
            cal.set(Calendar.HOUR_OF_DAY, lessonsTime[0][0]);
            return cal.getTime().getTime();
        }

    }

    public boolean NextParaExist(int day, boolean week) {
        Day dayinwhichlucking = null;
        if (week)
            if (Schedule.getSizeofFirstWeek() > day)
                dayinwhichlucking = Schedule.getDayofFirstWeek(day);
            else if (Schedule.getSizeofSecondWeek() > day)
                dayinwhichlucking = Schedule.getDayofSecondWeek(day);
        if (dayinwhichlucking != null) {
            int[] timesofluckingless = lessonsTime[(dayinwhichlucking.get(dayinwhichlucking.size() - 1).getItemNumber() - 1)];
            Date now = new Date(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            return timesofluckingless[0] * 60 + timesofluckingless[1] > cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR_OF_DAY) * 60;
        } else {
            return false;
        }
    }

}
