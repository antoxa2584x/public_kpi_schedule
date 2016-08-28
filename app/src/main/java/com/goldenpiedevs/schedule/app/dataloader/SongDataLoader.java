package com.goldenpiedevs.schedule.app.dataloader;

import android.os.AsyncTask;

import com.goldenpiedevs.schedule.app.events.SongInfoLoaded;
import com.goldenpiedevs.schedule.app.modules.Const;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
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

import org.greenrobot.eventbus.EventBus;

/**
 * Парсер расписания с сервера
 */
public class
        SongDataLoader extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... params) {
        try {
            return postData();
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
        String song = "";
        try {
            song = new JSONObject(result).getString("song");
        } catch (JSONException | NullPointerException e1) {
            e1.printStackTrace();
        }
        EventBus.getDefault().post(new SongInfoLoaded(song));
    }


    @SuppressWarnings("deprecation")
    public String postData() throws URISyntaxException {
        HttpResponse response;
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, Const.CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, Const.SOCKET_TIMEOUT);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        try {

            String url = "http://77.47.130.190:7000/getmeta/";
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
