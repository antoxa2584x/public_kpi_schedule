package com.goldenpiedevs.schedule.app.models;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Класс - обект содержащий в себе расписание на день
 */
public class Day implements Serializable {
    private ArrayList<Lesson> day = new ArrayList<>();
    private int dayNumb;

    /**
     * Добавление пары в день
     *
     * @param a пара
     */
    public void add(Lesson a) {
        day.add(a);
    }

    /**
     * @return размер дня
     */
    public int size() {
        return day.size();
    }

    /**
     * Получения пары из дня
     *
     * @param a номер пары в списке
     * @return пару
     */
    public Lesson get(int a) {
        return day.get(a);
    }

    /**
     * @return номер дня в неделе
     */
    public int getDayNumb() {
        return dayNumb;
    }

    /**
     * Задания номера дня в неделе
     *
     * @param dayNumb номер дня в неделе
     */
    public void setDayNumb(int dayNumb) {
        this.dayNumb = dayNumb;
    }

    public Lesson getLessonByNumber(int lessonNumber) {
        for (int i = 0; i < day.size(); i++) {
            if (day.get(i).getItemNumber() == lessonNumber) {
                return day.get(i);
            }
        }
        return null;
    }

    public boolean isLessonExist(int a) {
        for (int i = 0; i < day.size(); i++) {
            if (day.get(i).getItemNumber() == a) return true;
        }
        return false;
    }

    public int LessonsToday() {
        return day.size();
    }
}
