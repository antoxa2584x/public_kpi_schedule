package com.goldenpiedevs.schedule.app.fragments;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.ScheduleApplication;
import com.goldenpiedevs.schedule.app.activitys.MainActivity;
import com.goldenpiedevs.schedule.app.dataloader.io.TeacherIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.TaskCompleteListener;
import com.goldenpiedevs.schedule.app.dataloader.listeners.TeacherSenderInterface;
import com.goldenpiedevs.schedule.app.models.Marks;
import com.goldenpiedevs.schedule.app.models.Teacher;
import com.goldenpiedevs.schedule.app.modules.Const;
import com.goldenpiedevs.schedule.app.modules.CustomViews.Adapters.TeacherSearchAdapter;
import com.goldenpiedevs.schedule.app.modules.NetworkCheck;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TeachersListFragment extends android.support.v4.app.Fragment {
    @BindView(R.id.listView)
    ListView listView;
    private ArrayList<Teacher> list;
    private ArrayList<Marks> markList;
    private MaterialDialog progressDialog;
    private String TEACHERS_MARKS_LIST = "TeachersListFragment.MarksList";
    private Handler uiHandler = new Handler();
    private ListAdapter defaultAdapter;
    private TeacherSearchAdapter searchAdapter;
    private SwingBottomInAnimationAdapter mAnimAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_teachers_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            markList = savedInstanceState.getParcelableArrayList(TEACHERS_MARKS_LIST);
        } else {
            markList = new ArrayList<>();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().setTaskDescription(new ActivityManager.TaskDescription("Список викладачів",
                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher),
                    getResources().getColor(R.color.primary_dark)));

        getPrefs();
        list = new TeacherIO().getTeacherListFromFile(getPrefs().getString(Const.GROUP, ""),
                getActivity().getApplicationContext());

        defaultAdapter = new ListAdapter(list);
        searchAdapter = new TeacherSearchAdapter(getActivity(), 0,
                Arrays.asList(getResources().getStringArray(R.array.teachers_name)));

        mAnimAdapter = new SwingBottomInAnimationAdapter(defaultAdapter);
        mAnimAdapter.setAbsListView(listView);

        listView.setAdapter(mAnimAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onClickListItem(position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onLongClickListItem(position);
                return true;
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    private SharedPreferences getPrefs() {
        return ((ScheduleApplication) getActivity().getApplication()).getsPref();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TEACHERS_MARKS_LIST, markList);
    }


    public void onClickListItem(int position) {
        ArrayList<String> params = new ArrayList<>();
        params.add(String.valueOf(list.get(position).getTeacherID()));
        params.add(list.get(position).getTeacherName());

        ((MainActivity) getActivity()).changeActivityFragment(MainActivity.TEACHERS_FRAGMENT, params);
    }

    public void onLongClickListItem(final int position) {
        final String[] strings = {"Додати оцінку", "Переглянути рейтинг"};
        new MaterialDialog.Builder(getActivity())
                .title("Рейтинг викладача")
                .items(strings)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                onRateAddChoose(position, new TeacherIO().getTeacherListFromFile(
                                        getPrefs().getString(Const.GROUP, ""),
                                        getActivity().getApplicationContext()).get(position).getTeacherID());

                                break;
                            case 1:
                                progressDialog = new MaterialDialog.Builder(getActivity())
                                        .title("Зачекайте")
                                        .autoDismiss(false)
                                        .cancelable(false)
                                        .content(getResources().getString(R.string.load_in_progress))
                                        .progress(true, 0)
                                        .show();

                                final Timer myTimer = new Timer();
                                final Handler uiHandler = new Handler();
                                myTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        uiHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (getMarkList().size() > 0) {
                                                    if (getMarkList().size() > position) {
                                                        myTimer.cancel();
                                                        progressDialog.dismiss();
                                                        onRateViewChoose(position);
                                                    }
                                                }
                                            }
                                        });
                                    }

                                }, 100, 100);
                                break;
                        }
                    }
                })
                .positiveText(R.string.cancel)
                .build()
                .show();
    }

    public void onRateViewChoose(int position) {
        @SuppressLint("InflateParams") View localView = getActivity().getLayoutInflater()
                .inflate(R.layout.teacher_rating, null);

        RatingBar ratingBar1 = (RatingBar) localView.findViewById(R.id.ratingBar1);
        RatingBar ratingBar2 = (RatingBar) localView.findViewById(R.id.ratingBar2);
        RatingBar ratingBar3 = (RatingBar) localView.findViewById(R.id.ratingBar3);
        RatingBar ratingBar4 = (RatingBar) localView.findViewById(R.id.ratingBar4);

        ratingBar1.setIsIndicator(true);
        ratingBar2.setIsIndicator(true);
        ratingBar3.setIsIndicator(true);
        ratingBar4.setIsIndicator(true);

        final int finalPosition = position;
        if (getMarkList().get(position).getTeacherRate() != 0) {
            localView.findViewById(R.id.list).setVisibility(View.VISIBLE);
            ratingBar1.setRating(getMarkList().get(position).getObjectKnow());
            ratingBar2.setRating(getMarkList().get(position).getExactness());
            ratingBar3.setRating(getMarkList().get(position).getRelationToStudents());
            ratingBar4.setRating(getMarkList().get(position).getHumorSense());

            new MaterialDialog.Builder(getActivity())
                    .title("Рейтинг викладача")
                    .customView(localView, true)
                    .negativeText("Сховати")
                    .build()
                    .show();
        } else {
            localView.findViewById(R.id.error_view).setVisibility(View.VISIBLE);

            new MaterialDialog.Builder(getActivity())
                    .title("Рейтинг викладача")
                    .customView(localView, false)
                    .negativeText("Сховати")
                    .positiveText("Додати відгук")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            onRateAddChoose(finalPosition, getMarkList().get(finalPosition).getTeacherId());
                        }
                    })
                    .build()
                    .show();
        }
    }

    public void onRateAddChoose(final int position, final int id) {
        if (new NetworkCheck(getActivity()).isNetworkOnline()) {
            @SuppressLint("InflateParams") View localView = getActivity().getLayoutInflater().inflate(R.layout.teacher_rating, null);
            localView.findViewById(R.id.list).setVisibility(View.VISIBLE);

            final RatingBar ratingBar1 = (RatingBar) localView.findViewById(R.id.ratingBar1);
            final RatingBar ratingBar2 = (RatingBar) localView.findViewById(R.id.ratingBar2);
            final RatingBar ratingBar3 = (RatingBar) localView.findViewById(R.id.ratingBar3);
            final RatingBar ratingBar4 = (RatingBar) localView.findViewById(R.id.ratingBar4);
//            final RatingBar ratingBar5 = (RatingBar) localView.findViewById(R.id.ratingBar5);

            new MaterialDialog.Builder(getActivity())
                    .title("Рейтинг викладача")
                    .customView(localView, true)
                    .positiveText("Відправити")
                    .negativeText("Сховати")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            if (ratingBar1.getRating() != 0 && ratingBar2.getRating() != 0 && ratingBar3.getRating() != 0 && ratingBar4.getRating() != 0) {
                                progressDialog = new MaterialDialog.Builder(getActivity())
                                        .title("Зачекайте")
                                        .autoDismiss(false)
                                        .cancelable(false)
                                        .content(getResources().getString(R.string.load_in_progress))
                                        .progress(true, 0)
                                        .show();

//                                final RatingGetter ratingGetter = new RatingGetter(position, true);
                                RatingSender ratingSender = new RatingSender(new TeacherSenderInterface() {
                                    @Override
                                    public void onTaskCompleted(Marks marks) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity().getApplicationContext(), "Ваш відгук відправлено", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        progressDialog.dismiss();
                                        getMarkList().set(position, marks);
                                        defaultAdapter.notifyDataSetChanged();
//                                        ratingGetter.execute(String.valueOf(id));
                                    }

                                    @Override
                                    public void onTaskFailed() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "Ви вже проголосували за цього викладача", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        progressDialog.dismiss();
                                    }

                                    @Override
                                    public void onTimeoutException() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), "Помилка при відправленні відгуку", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        progressDialog.dismiss();
                                    }
                                });

                                ratingSender.execute(String.valueOf(id), String.valueOf(ratingBar1.getRating()), String.valueOf(ratingBar2.getRating())
                                        , String.valueOf(ratingBar3.getRating()), String.valueOf(ratingBar4.getRating()));
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Помилка - мінімальна оцінка 0.5", Toast.LENGTH_SHORT).show();
                            }
                        }

                    })
                    .build()
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.app_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                ((MainActivity) getActivity()).collapsingToolbar.setCollapsedTitleTextColor(Color.TRANSPARENT);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ((MainActivity) getActivity()).collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
                return true;
            }
        });

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 0) {
                    if (listView.getAdapter() != searchAdapter)
                        listView.setAdapter(searchAdapter);

                    searchAdapter.getFilter().filter(s);
                } else {
                    if (listView.getAdapter() == searchAdapter)
                        listView.setAdapter(mAnimAdapter);
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    public ArrayList<Marks> getMarkList() {
        return markList;
    }


    static class ViewHolder {
        @BindView(R.id.teacher_list_row_title)
        TextView name;
        @BindView(R.id.image_view)
        ImageView image;
        @BindView(R.id.teacher_list_row_rating)
        TextView rate;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ListAdapter extends ArrayAdapter<Teacher> {

        public ListAdapter(ArrayList<Teacher> teacherList) {
            super(getActivity(),0,teacherList);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = TeachersListFragment.this.getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.teacheras_listview_row, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.rate.setText("Рейтинг: -");

            if (!getMarkList().isEmpty() && getMarkList().size() > position) {
                if (getMarkList().get(position).getTeacherRate() == 0)
                    viewHolder.rate.setText("Рейтинг: ще не вказано");
                else
                    viewHolder.rate.setText(String.format("Рейтинг: %.02f", getMarkList().get(position).getTeacherRate()).replace(',', '.'));
            } else {
                final RatingGetter scheduleLoader = new RatingGetter(position);
                scheduleLoader.setListener(new TaskCompleteListener() {
                    @Override
                    public void onTaskCompleted() {
                        scheduleLoader.removeListenr();
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (getMarkList().get(position).getTeacherRate() == 0)
                                    viewHolder.rate.setText("Рейтинг: ще не вказано");
                                else if (getMarkList().get(position).getTeacherRate() != 0)
                                    viewHolder.rate.setText(String.format("Рейтинг: %.02f", getMarkList().get(position).getTeacherRate()).replace(',', '.'));
                            }
                        });

                    }

                    @Override
                    public void onTaskFailed() {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.rate.setText("Рейтинг: -");
                            }
                        });

                    }

                });
                scheduleLoader.execute(String.valueOf(getItem(position).getTeacherID()));
            }

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(String.valueOf(getItem(position).getTeacherName().charAt(0)));

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(String.valueOf(getItem(position).getTeacherName().charAt(0)), color);
            viewHolder.image.setImageDrawable(drawable);
            viewHolder.name.setText(getItem(position).getTeacherName());
            viewHolder.rate.setText(viewHolder.rate.getText().toString());

            return convertView;
        }
    }

    @SuppressWarnings({"deprecated", "deprecation"})
    class RatingGetter extends AsyncTask<String, Integer, String> {

        private Marks mark = new Marks();
        private int position;
        private TaskCompleteListener listener;

        public RatingGetter(int position) {
            this.position = position;
        }

        public void setListener(TaskCompleteListener listener){
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            return postData(params[0]);
        }

        public String postData(String id) {
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(httpParameters, Const.CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Const.SOCKET_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpParameters);

            try {
                String respond;
                String url = String.format("http://api.rozklad.org.ua/v2/teachers/%s/rating", id);
                HttpGet httpget = new HttpGet(url);
                response = client.execute(httpget);

                HttpEntity entity = response.getEntity();
                StringBuilder builder = new StringBuilder();

                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                while ((respond = reader.readLine()) != null) {
                    builder.append(respond);
                }
                mark.setTeacherId(Integer.parseInt(id));
                respond = builder.toString();
                return respond;
            } catch (IOException e) {
                if(listener!=null)
                listener.onTaskFailed();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String respond) {
            JSONObject respondData;

            JSONObject teacherData = null;
            int statusCode = Const.STATUS_CODE_NOT_FOUND;
            try {
                respondData = new JSONObject(respond);
                statusCode = respondData.getInt("statusCode");
                teacherData = respondData.getJSONObject("data");
            } catch (Exception e) {
                e.printStackTrace();

                if(listener!=null)
                listener.onTaskFailed();
            }

            if (statusCode == Const.STATUS_CODE_OK) {
                if (teacherData != null) {
                    try {
                        mark.setExactness((float) teacherData.getDouble("mark_avg_exactingness"));
                        mark.setRelationToStudents((float) teacherData.getDouble("mark_avg_relation_to_the_student"));
                        mark.setHumorSense((float) teacherData.getDouble("mark_avg_sense_of_humor"));
                        mark.setObjectKnow((float) teacherData.getDouble("mark_avg_knowledge_subject"));

                        mark.setTeacherRate((float) ((mark.getExactness() + mark.getRelationToStudents() + mark.getObjectKnow() + mark.getHumorSense()) / (4 * 1.0)));

                        if (Float.isNaN(mark.getTeacherRate())) {
                            setNanData();
                            if(listener!=null)
                            listener.onTaskFailed();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(listener!=null)
                    listener.onTaskCompleted();
                } else {
                    setNanData();
                    if(listener!=null)
                    listener.onTaskFailed();
                }
            } else {
                setNanData();
                if(listener!=null)
                listener.onTaskCompleted();
            }

            getMarkList().add(position, mark);
        }

        void setNanData() {
            mark.setTeacherRate(0);
            mark.setExactness(0);
            mark.setRelationToStudents(0);
            mark.setHumorSense(0);
            mark.setObjectKnow(0);
        }

        public void removeListenr() {
            this.listener = null;
        }
    }

    @SuppressWarnings("deprecation")
    class RatingSender extends AsyncTask<String, Integer, String> {
        private TeacherSenderInterface listener;

        public RatingSender(TeacherSenderInterface listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return postData(params[0], params[1], params[2], params[3], params[4]);
            } catch (URISyntaxException e) {
                listener.onTimeoutException();
                e.printStackTrace();
                return null;
            }

        }

        public String postData(String id, String rate1, String rate2, String rate3, String rate4) throws URISyntaxException {
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(httpParameters, Const.CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParameters, Const.SOCKET_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpParameters);

            String canVoteUrl = String.format("http://api.rozklad.org.ua/v2/teachers/%s/canvote", id);
            String postVoteUrl = String.format("http://api.rozklad.org.ua/v2/teachers/%s/vote/", id);
            HttpGet httpget = new HttpGet(canVoteUrl);
            HttpPost httpPost = new HttpPost(postVoteUrl);

            try {
                response = client.execute(httpget);
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                String canVoteRespond;
                StringBuilder builder = new StringBuilder();
                while ((canVoteRespond = reader.readLine()) != null) {
                    builder.append(canVoteRespond);
                }
                JSONObject respond = new JSONObject(builder.toString());

                if (respond.getBoolean("data")) {
                    String voteRespond;
                    List<NameValuePair> nameValuePairs = new ArrayList<>(4);
                    nameValuePairs.add(new BasicNameValuePair("mark_knowledge_subject", rate1));
                    nameValuePairs.add(new BasicNameValuePair("mark_exactingness", rate2));
                    nameValuePairs.add(new BasicNameValuePair("mark_relation_to_the_student", rate3));
                    nameValuePairs.add(new BasicNameValuePair("mark_sense_of_humor", rate4));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    response = client.execute(httpPost);

                    entity = response.getEntity();
                    builder = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    while ((voteRespond = reader.readLine()) != null) {
                        builder.append(voteRespond);
                    }
                    voteRespond = builder.toString();

                    return voteRespond;
                } else {
                    listener.onTaskFailed();
                    return null;
                }
            } catch (Exception e) {
                listener.onTaskFailed();
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Log.i("PostRating", s);
                try {
                    JSONObject jsonObject = new JSONObject(s);

                    if (jsonObject.getInt("statusCode") == 201) {

                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONObject teacherData = data.getJSONObject("rating");
                        Marks marks = new Marks();

                        marks.setTeacherId(data.getJSONObject("teacher").getInt("teacher_id"));

                        marks.setExactness((float) teacherData.getDouble("mark_avg_exactingness"));
                        marks.setRelationToStudents((float) teacherData.getDouble("mark_avg_relation_to_the_student"));
                        marks.setHumorSense((float) teacherData.getDouble("mark_avg_sense_of_humor"));
                        marks.setObjectKnow((float) teacherData.getDouble("mark_avg_knowledge_subject"));

                        marks.setTeacherRate((float) ((marks.getExactness() + marks.getRelationToStudents() + marks.getObjectKnow() + marks.getHumorSense()) / (4 * 1.0)));
                        listener.onTaskCompleted(marks);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
