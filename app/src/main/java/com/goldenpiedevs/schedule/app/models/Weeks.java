package com.goldenpiedevs.schedule.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Класс - обект содержащий в себе две недели с днями
 */
public class Weeks implements Serializable {
    private ArrayList<Day> weekOne = new ArrayList<>();
    private ArrayList<Day> weekTwo = new ArrayList<>();

    public static boolean isWeekEven() {
        Calendar cal1 = Calendar.getInstance();
        int a = cal1.get(Calendar.WEEK_OF_YEAR);
        return (a % 2) == 0;
    }

    /**
     * Добавление дня в первую неделю
     *
     * @param a день
     */
    public void addtoFirstWeek(Day a) {
        weekOne.add(a);
    }

    /**
     * Добавление дня во вторую неделю
     *
     * @param a день
     */
    public void addtoSecondWeek(Day a) {
        weekTwo.add(a);
    }

    /**
     * Получение дня из списка первой недели
     *
     * @param a номер дня в масиве
     * @return день
     */
    public Day getDayofFirstWeek(int a) {
        if (weekOne.size() >= a)
            return weekOne.get(a);
        return new Day();
    }

    /**
     * Получение дня из списка второй недели
     *
     * @param a номер дня в масиве
     * @return день
     */
    public Day getDayofSecondWeek(int a) {
        if (weekTwo.size() >= a)
            return weekTwo.get(a);
        return new Day();
    }

    /**
     * Получение размера первой недели
     *
     * @return размер недлеи в зависимости от количества учебных дней в ней
     */
    public int getSizeofFirstWeek() {
        return weekOne.size();
    }

    /**
     * Получение размера второй недели
     *
     * @return размер недлеи в зависимости от количества учебных дней в ней
     */
    public int getSizeofSecondWeek() {
        return weekTwo.size();
    }
}
