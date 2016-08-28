package com.goldenpiedevs.schedule.app.dataloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.goldenpiedevs.schedule.app.dataloader.io.GroupIO;
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
public class ScheduleLoader extends AsyncTask<String, Integer, String> {
    private String group;
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
     * @param result возвращает полученные данные
     */
    protected void onPostExecute(String result) {
        int code = Const.STATUS_CODE_NOT_FOUND;
        try {
            code = new JSONObject(result).getInt("statusCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (code == Const.STATUS_CODE_OK) {
            Parser parser;
            parser = new Parser(result, false);

            Weeks weeks = parser.getWeek();
            assert weeks != null;
            new GroupIO().writeGroupToFile(weeks, group, mContext);
            sPref.edit().putInt(sPref.getString(Const.GROUP, "") + ":1", weeks.getSizeofFirstWeek()).apply();
            sPref.edit().putInt(sPref.getString(Const.GROUP, "") + ":2", weeks.getSizeofSecondWeek()).apply();
            statusListener.onComplete(true);
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
     * @param group задается группа которую надо скачать
     * @throws URISyntaxException
     */
    public String postData(String group) throws URISyntaxException {
        this.group = group;
        HttpResponse response;
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, Const.CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, Const.SOCKET_TIMEOUT);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {

            String url = Const.API_URL + "groups/" + URLEncoder.encode(group, "UTF-8") + "/lessons";
            HttpGet httpget = new HttpGet(url);
            response = httpClient.execute(httpget);

            HttpEntity entity = response.getEntity();
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

            String line;
            StringBuilder builder = new StringBuilder();

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
