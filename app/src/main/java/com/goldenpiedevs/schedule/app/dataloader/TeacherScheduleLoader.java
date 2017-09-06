package com.goldenpiedevs.schedule.app.dataloader;

import android.content.Context;
import android.os.AsyncTask;

import com.goldenpiedevs.schedule.app.dataloader.io.TeacherIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Парсер расписания с сервера
 */
public class TeacherScheduleLoader extends AsyncTask<String, Integer, String> {
    private Context mContext;
    private String teacherId;
    private DownloadStatusListener statusListener;

    public TeacherScheduleLoader(Context context) {
        mContext = context;
    }

    public TeacherScheduleLoader setStatusListener(DownloadStatusListener downloadStatusListener) {
        statusListener = downloadStatusListener;
        return this;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return postData(params[0]);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод выполняется когда данные полностью загружены
     *
     * @param result возвращает аолученные данные
     */
    protected void onPostExecute(String result) {
        int code = Const.STATUS_CODE_NOT_FOUND;
        try {
            code = new JSONObject(result).getInt("statusCode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (code == Const.STATUS_CODE_OK) {
            Parser parser = new Parser(result, true);

            Weeks teacherSchedule = parser.getWeek();

            assert teacherSchedule != null;
            new TeacherIO().writeTeacherToFile(teacherSchedule, teacherId, mContext);
            statusListener.onComplete(true);
        } else {
            statusListener.onComplete(false);
        }
    }


    /**
     * Показывает прогресс загрузки.
     * Учитывая размер данных, не используется.
     *
     * @param progress //
     */
    protected void onProgressUpdate(Integer... progress) {

    }

    /**
     * Метод загрузки расписания группы с сервера
     *
     * @param teacherId задается группа которую надо скачать
     * @throws java.net.URISyntaxException
     */
    @SuppressWarnings("deprecation")
    public String postData(String teacherId) throws URISyntaxException {
        this.teacherId = teacherId;

        //Log.v("TeacherScheduleLoader", "Start execute");
        HttpResponse response;
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, Const.CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, Const.SOCKET_TIMEOUT);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

        StringBuilder builder = new StringBuilder();

        try {
            HttpGet httpget = new HttpGet(Const.API_URL + "teachers/" + URLEncoder.encode(teacherId, "UTF-8") + "/lessons");
            response = httpClient.execute(httpget);

            HttpEntity entity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
