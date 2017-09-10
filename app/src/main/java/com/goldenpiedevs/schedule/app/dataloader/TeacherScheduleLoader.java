package com.goldenpiedevs.schedule.app.dataloader;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.goldenpiedevs.schedule.app.dataloader.io.TeacherIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Парсер расписания с сервера
 */
public class TeacherScheduleLoader {
    private Context mContext;
    private DownloadStatusListener statusListener;

    public TeacherScheduleLoader(Context context) {
        mContext = context;
    }

    public TeacherScheduleLoader setStatusListener(DownloadStatusListener downloadStatusListener) {
        statusListener = downloadStatusListener;
        return this;
    }

    public void execute(final String teacherId) {
        try {
            AndroidNetworking.get(Const.API_URL + "teachers/" + URLEncoder.encode(teacherId, "UTF-8") + "/lessons")
                    .setTag("teachers_lessons")
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            String message = "";
                            try {
                                message = new JSONObject(response).getString("message");
                            } catch (Exception ignored) {
                            }
                            if (message.equals("Ok")) {
                                Parser parser = new Parser(response, true);

                                Weeks teacherSchedule = parser.getWeek();

                                assert teacherSchedule != null;
                                new TeacherIO().writeTeacherToFile(teacherSchedule, teacherId, mContext);
                                statusListener.onComplete(true);
                            } else {
                                statusListener.onComplete(false);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            statusListener.onFailed(666);
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
