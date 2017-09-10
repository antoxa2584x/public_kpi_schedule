package com.goldenpiedevs.schedule.app.dataloader;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.goldenpiedevs.schedule.app.dataloader.io.GroupIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Парсер расписания с сервера
 */
public class ScheduleLoader {
    private DownloadStatusListener statusListener;
    private Context mContext;
    private SharedPreferences sPref;

    public ScheduleLoader(Context context) {
        mContext = context;
        sPref = context.getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE);
    }

    public ScheduleLoader setStatusListener(DownloadStatusListener downloadStatusListener) {
        statusListener = downloadStatusListener;
        return this;
    }

    public void execute(final String groupName) {
        try {
            AndroidNetworking.get(Const.API_URL + "groups/" + URLEncoder.encode(groupName, "UTF-8") + "/lessons")
                    .setTag("lessons")
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
                                Parser parser;
                                parser = new Parser(response, false);

                                Weeks weeks = parser.getWeek();
                                assert weeks != null;
                                new GroupIO().writeGroupToFile(weeks, groupName, mContext);
                                sPref.edit().putInt(sPref.getString(Const.GROUP, "") + ":1", weeks.getSizeofFirstWeek()).apply();
                                sPref.edit().putInt(sPref.getString(Const.GROUP, "") + ":2", weeks.getSizeofSecondWeek()).apply();
                                statusListener.onComplete(true);
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
