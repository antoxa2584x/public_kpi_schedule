package com.goldenpiedevs.schedule.app.dataloader.io;

import android.content.Context;

import com.goldenpiedevs.schedule.app.models.Teacher;
import com.goldenpiedevs.schedule.app.models.Weeks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для загрузки, сохранение, проверки расписания.
 */
public class TeacherIO {
    /**
     * Проверка наличия файла с расписанием
     *
     * @param teacherId id препода
     * @param context   котекст приложения (заморочки андроид)
     * @return true если сушествует
     */
    public static boolean isTeacherDownloaded(String teacherId, Context context) {
        File a = new File(context.getFilesDir().getPath() + "/db/teachers/" + teacherId);
        return a.exists();
    }

    /**
     * Проверка наличия файла с расписанием
     *
     * @param groupName номер группы
     * @param context   котекст приложения (заморочки андроид)
     * @return true если сушествует
     */
    public static boolean isTeacherListDownloaded(String groupName, Context context) {
        File a = new File(context.getFilesDir().getPath() + "/db/" + groupName + "_teachersList");
        return a.exists();
    }

    /**
     * Получения расписания с файла
     *
     * @param teacherId номер группы
     * @param context   котекст приложения (заморочки андроид)
     * @return обект класса @Weeks
     */
    public Weeks getTeacherFromFile(String teacherId, Context context) {
        File a = new File(context.getFilesDir().getPath() + "/db/teachers/" + teacherId);
        if (TeacherIO.isTeacherDownloaded(teacherId, context)) {
            Weeks d = null;
            try {
                ObjectInputStream b = new ObjectInputStream(new FileInputStream(a));
                d = (Weeks) b.readObject();
                b.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return d;
        } else {
            return null;
        }
    }

    /**
     * Запись расписания в файл
     *
     * @param teacherWeeks расписание группы
     * @param teacherId    номер группы
     * @param context      котекст приложения (заморочки андроид)
     * @throws IOException
     */
    public void writeTeacherToFile(final Weeks teacherWeeks, final String teacherId, final Context context) {
        new Thread() {
            @Override
            public void run() {
                try {
                    File dir = new File(context.getFilesDir().getPath() + "/db/teachers/");
                    if (!dir.exists())
                        dir.mkdir();

                    File a = new File(context.getFilesDir().getPath() + "/db/teachers/" + teacherId);
                    ObjectOutputStream b = new ObjectOutputStream(new FileOutputStream(a));
                    b.writeObject(teacherWeeks);
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * Получения расписания с файла
     *
     * @param groupName номер группы
     * @param context   котекст приложения (заморочки андроид)
     * @return обект класса @Weeks
     */
    public ArrayList<Teacher> getTeacherListFromFile(String groupName, Context context) {
        File a = new File(context.getFilesDir().getPath() + "/db/" + groupName + "_teachersList");
        if (TeacherIO.isTeacherListDownloaded(groupName, context)) {
            ArrayList<Teacher> d = null;
            try {
                ObjectInputStream b = new ObjectInputStream(new FileInputStream(a));
                //noinspection unchecked
                d = (ArrayList<Teacher>) b.readObject();
                b.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return d;
        } else {
            return null;
        }
    }

    /**
     * Запись расписания в файл
     *
     * @param teachersList расписание группы
     * @param groupName    номер группы
     * @param context      котекст приложения (заморочки андроид)
     * @throws IOException
     */
    public void writeTeacherListToFile(List<Teacher> teachersList, String groupName, Context context) {
        try {
            File dir = new File(context.getFilesDir().getPath() + "/db/");
            if (!dir.exists())
                dir.mkdir();

            File a = new File(context.getFilesDir().getPath() + "/db/" + groupName + "_teachersList");
            ObjectOutputStream b = new ObjectOutputStream(new FileOutputStream(a));
            b.writeObject(teachersList);
            b.flush();
            b.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

