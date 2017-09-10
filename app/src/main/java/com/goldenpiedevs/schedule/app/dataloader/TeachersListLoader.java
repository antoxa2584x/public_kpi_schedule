package com.goldenpiedevs.schedule.app.dataloader;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.goldenpiedevs.schedule.app.dataloader.io.TeacherIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.models.Teacher;
import com.goldenpiedevs.schedule.app.modules.Const;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Парсер расписания с сервера
 */
public class TeachersListLoader{

    private List<Teacher> teachersList = null;
    private Context mContext;
    private DownloadStatusListener statusListener;

    public TeachersListLoader(Context context) {
        mContext = context;
    }

    public TeachersListLoader setStatusListener(DownloadStatusListener downloadStatusListener) {
        statusListener = downloadStatusListener;
        return this;
    }

    public void execute(final String groupName) {
        try {
            AndroidNetworking.get(Const.API_URL + "groups/" + URLEncoder.encode(groupName, "UTF-8") + "/teachers")
                    .setTag("teachers")
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
                                TeacherListParser parser = new TeacherListParser(response);
                                try {
                                    teachersList = parser.getTeachersList();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                assert teachersList != null;
                                if (!teachersList.isEmpty()) {
                                    new TeacherIO().writeTeacherListToFile(teachersList, groupName, mContext);
                                    statusListener.onComplete(true);
                                } else {
                                    statusListener.onFailed(Const.CODE_FAILED);
                                }

                            } else {
                                statusListener.onFailed(Const.STATUS_CODE_NOT_FOUND);
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
