package com.goldenpiedevs.schedule.app.dataloader;

import android.content.Context;
import android.os.AsyncTask;

import com.goldenpiedevs.schedule.app.dataloader.io.TeacherIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.models.Teacher;
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
import java.util.List;

/**
 * Парсер расписания с сервера
 */
public class TeachersListLoader extends AsyncTask<String, Integer, String> {
    public static DefaultHttpClient httpClient;

    StringBuilder builder = new StringBuilder();
    private List<Teacher> teachersList = null;
    private String groupName;
    private Context mContext;
    private DownloadStatusListener statusListener;

    public TeachersListLoader(Context context) {
        mContext = context;
    }

    public TeachersListLoader setStatusListener(DownloadStatusListener downloadStatusListener) {
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
        //Log.v("TeachersListLoader", "Exetuded");
        //Log.v("TeachersListLoader", builder.toString());

        int code = Const.STATUS_CODE_NOT_FOUND;
        try {
            code = new JSONObject(result).getInt("statusCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (code == Const.STATUS_CODE_OK) {
            TeacherListParser parser = new TeacherListParser(result);
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
            statusListener.onFailed(code);
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
     * @param groupName задается группа которую надо скачать
     * @throws java.net.URISyntaxException
     */
    @SuppressWarnings("deprecation")
    public String postData(String groupName) throws URISyntaxException {
        //err = false;
        this.groupName = groupName;
        //Log.v("TeachersListLoader", "Start execute");
        HttpResponse response;
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, Const.CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, Const.SOCKET_TIMEOUT);

        httpClient = new DefaultHttpClient(httpParameters);

        try {
            HttpGet httpget = new HttpGet(Const.API_URL + "groups/" + URLEncoder.encode(groupName, "UTF-8") + "/teachers");
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
