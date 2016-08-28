package com.goldenpiedevs.schedule.app.modules;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NextDay {
    public static String nextDayOfWeek(int dow, int week_num) {
        Calendar date = Calendar.getInstance();
        int y = date.get(Calendar.DAY_OF_WEEK);
        if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            if (date.get(Calendar.WEEK_OF_YEAR) % 2 != week_num - 1) {
                if ((y - dow) > 0) {
                    y = (14 - (y - dow));
                } else {
                    y = dow - y;
                }

            } else {
                y = 7 + (dow - y);
            }
        } else {
            if (date.get(Calendar.WEEK_OF_YEAR) % 2 == week_num - 1) {
                if ((y - dow) > 0) {
                    y = (14 - (y - dow));
                } else {
                    y = dow - y;
                }

            } else {
                y = 7 + (dow - y);
            }
        }

        date.add(Calendar.DAY_OF_MONTH, y);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
        return sdf.format(date.getTime());
    }
}