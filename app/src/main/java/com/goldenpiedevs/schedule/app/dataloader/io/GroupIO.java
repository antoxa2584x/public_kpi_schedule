package com.goldenpiedevs.schedule.app.dataloader.io;

import android.content.Context;

import com.goldenpiedevs.schedule.app.models.Weeks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Класс для загрузки, сохранение, проверки расписания.
 */
public class GroupIO {
    /**
     * Проверка наличия файла с расписанием
     *
     * @param groupName номер группы
     * @param context   котекст приложения (заморочки андроид)
     * @return true если сушествует
     */
    public static boolean isGroupDownloaded(String groupName, Context context) {
        File a = new File(context.getFilesDir().getPath() + "/db/" + groupName);
        return a.exists();
    }

    /**
     * Получения расписания с файла
     *
     * @param groupName номер группы
     * @param context   котекст приложения (заморочки андроид)
     * @return обект класса @Weeks
     */
    public Weeks getGroupFromFile(String groupName, Context context) {
        File a = new File(context.getFilesDir().getPath() + "/db/" + groupName);
        if (GroupIO.isGroupDownloaded(groupName, context)) {
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
     * @param group     расписание группы
     * @param groupName номер группы
     * @param context   котекст приложения (заморочки андроид)
     * @throws IOException
     */
    public void writeGroupToFile(Weeks group, String groupName, Context context) {
        try {
            File dir = new File(context.getFilesDir().getPath() + "/db/");
            if (!dir.exists())
                dir.mkdir();

            File a = new File(context.getFilesDir().getPath() + "/db/" + groupName);
            ObjectOutputStream b = new ObjectOutputStream(new FileOutputStream(a));
            b.writeObject(group);
            b.flush();
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

