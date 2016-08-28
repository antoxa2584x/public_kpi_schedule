package com.goldenpiedevs.schedule.app.modules.CustomViews.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ParseException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.activitys.FullInfoActivity;
import com.goldenpiedevs.schedule.app.activitys.TeachersFullInfoActivity;
import com.goldenpiedevs.schedule.app.models.Day;
import com.goldenpiedevs.schedule.app.models.Lesson;
import com.goldenpiedevs.schedule.app.modules.Const;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;
import it.gmariotti.cardslib.library.prototypes.LinearListView;

import static com.goldenpiedevs.schedule.app.modules.NextDay.nextDayOfWeek;


public class CardAdapter extends CardWithList {

    private static SharedPreferences sPref;
    private final Context context;
    private final boolean teacherActivity;
    private final Day day;
    private final int dayOfTheWeek;
    private final int week;
    private List<ListObject> mObjects = new ArrayList<>();
    private StockObject tempStockObject;

    public CardAdapter(Context context, Day day, int week, boolean teacherActivity) {
        super(context);
        this.context = context;
        this.day = day;
        this.week = week;
        this.tempStockObject = null;
        this.teacherActivity = teacherActivity;
        sPref = context.getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE);
        this.dayOfTheWeek = day.getDayNumb();
    }

    @Override
    protected CardHeader initCardHeader() {
        final CardHeader header = new CardHeader(getContext(), R.layout.card_header) {
            @Override
            public void setupInnerViewElements(ViewGroup parent, View view) {
                super.setupInnerViewElements(parent, view);
                TextView subTitle = (TextView) view.findViewById(R.id.card_header_subtitle);
                TextView title = (TextView) view.findViewById(R.id.card_header_inner_simple_title);
                DateFormat dateFormat = new SimpleDateFormat("MMM dd", new Locale("uk"));
                Date date = new Date();
                Calendar c = Calendar.getInstance();

                try {
                    c.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                c.add(Calendar.DATE, 0);

                int color;
                if (sPref.getBoolean("material_header", Boolean.parseBoolean(null))) {
                    color = mContext.getResources().getColor(R.color.pink);
                } else {
                    color = mContext.getResources().getColor(R.color.primary);
                }

                if (subTitle != null) {
                    if (!dateFormat.format(c.getTime()).equals(nextDayOfWeek(dayOfTheWeek + 1, week))) {
                        subTitle.setText(mContext.getString(R.string.next_day) + nextDayOfWeek(dayOfTheWeek + 1, week));  //Should use strings.xml
                    } else {
                        subTitle.setText(mContext.getString(R.string.today));  //Should use strings.xml
                        subTitle.setTextColor(Color.WHITE);
                        title.setTextColor(Color.WHITE);
                        view.setBackgroundColor(color);
                    }

                }
            }

        };
        String[] days = context.getResources().getStringArray(R.array.Days);
//        Drawable circle = context.getResources().getDrawable(R.drawable.circle);
        header.setTitle(days[dayOfTheWeek]);

        return header;
    }

    @Override
    protected void initCard() {
        setSwipeable(false);
        setUseEmptyView(false);
    }


    @Override
    public List<ListObject> initChildren() {
        for (int i = 0; i < day.size(); i++) {
            StockObject s = new StockObject(this);
            Lesson lesson = day.get(i);
            s.code = (lesson.getFullName());
            s.num = String.valueOf(lesson.getItemNumber());
            String[] times = context.getResources().getStringArray(R.array.Times);
            s.time = times[lesson.getItemNumber()];
            s.value = lesson.getRoomLocation();
            s.type = lesson.getClassesType();
            s.teachers = lesson.getTeachers();

            s.note = lesson.getNote();
            s.day_num = this.dayOfTheWeek;
            s.need_replace = false;
            s.week_num = (this.week - 1);
            s.longitude = lesson.getLongitude();
            s.latitude = lesson.getLatitude();

            if (lesson.getGroupArrayList().size() != 0) {
                s.groupList = "";
                for (int j = 0; j < lesson.getGroupArrayList().size(); j++) {
                    if (j > 0) {
                        s.groupList += ", " + lesson.getGroupArrayList().get(j).getGroupName();
                    } else {
                        s.groupList += lesson.getGroupArrayList().get(j).getGroupName();
                    }
                }
            }
            if ((i < day.size() - 1) && (day.get(i).getItemNumber() == day.get(i + 1).getItemNumber())) {
                if (!lesson.getRoomLocation().equals(day.get(i + 1).getRoomLocation())) {
                    s.value = lesson.getRoomLocation() + "/" + day.get(i + 1).getRoomLocation();
                } else {
                    s.value = lesson.getRoomLocation();
                }

                if (lesson.getTeachers().get(i)[0].equals(day.get(i + 1).getTeachers().get(i + 1)[0])) {
                    s.teachers.add(1, day.get(i + 1).getTeachers().get(0));
                } else {
                    s.teachers = lesson.getTeachers();
                }

                s.need_replace = true;
            }

            mObjects.add(s);
        }
        return mObjects;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {
        TextView card_item_title = (TextView) convertView.findViewById(R.id.card_item_title);
        TextView card_item_location = (TextView) convertView.findViewById(R.id.card_item_location);
        TextView card_item_times = (TextView) convertView.findViewById(R.id.card_item_times);
        TextView card_item_number = (TextView) convertView.findViewById(R.id.card_item_number);

        StockObject stockObject = (StockObject) object;
        card_item_title.setText(stockObject.code);

        switch (stockObject.value) {
            case "Пол.-ін-т РАКА":
                card_item_location.setText(stockObject.type + " " + "ін-т РАКА");
                break;
            case "Пол.-ін-т ім. Амосова":
                card_item_location.setText(stockObject.type + " " + "ін-т ім. Амосова");
                break;
            default:
                card_item_location.setText(stockObject.type + " " + stockObject.value);
                break;
        }

        card_item_times.setText(stockObject.time);
        card_item_number.setText(stockObject.num);

        if (stockObject.note.length() > 1) {
            ImageView img = (ImageView) convertView.findViewById(R.id.imageView);
            img.setVisibility(View.VISIBLE);
            card_item_number.setTextColor(Color.WHITE);
            if (sPref.getString(Const.COLOR_SCHEME, "1").equals("#2196F3")) {
                img.setImageResource(R.drawable.circle);
            } else if (sPref.getString(Const.COLOR_SCHEME, "1").equals("#f50057")) {
                img.setImageResource(R.drawable.circle_l);
            }
        }

        if (tempStockObject != null) {
            if (tempStockObject.need_replace) {
                convertView.setVisibility(View.GONE);
            }
        }
        tempStockObject = stockObject;

        return convertView;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_content;
    }

    public class StockObject extends DefaultListObject {
        public String code;
        public String value;
        public String time;
        public String num;
        public String teacher;
        public String type;
        public String teacher_id;
        public String note;
        public int week_num;
        public int day_num;
        public boolean need_replace;
        public double latitude;
        public double longitude;
        private List<String[]> teachers;
        private String groupList;

        public StockObject(it.gmariotti.cardslib.library.internal.Card parentCard) {
            super(parentCard);
            init();
        }

        private void init() {
            Intent intent;
            if (!teacherActivity) {
                intent = new Intent(context, FullInfoActivity.class);
            } else {
                intent = new Intent(context, TeachersFullInfoActivity.class);
            }
            final Intent finalIntent = intent;
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, ListObject object) {
                    try {
                        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
                        if (resultCode == ConnectionResult.SUCCESS) {
                            StockObject stockObject = (StockObject) object;
                            if (getObjectId().equals(stockObject.code)) {
                                finalIntent.putExtra(Const.LESSON_TITLE, getObjectId());
                                finalIntent.putExtra(Const.CAMPUS_NUM, stockObject.value);
                                finalIntent.putExtra(Const.LESSON_TYPE, stockObject.type);
                                finalIntent.putExtra(Const.LESSON_TIME, stockObject.time);
                                finalIntent.putExtra(Const.LESSON_NUM, position + 1);
                                finalIntent.putExtra(Const.NOTE, stockObject.note);
                                finalIntent.putExtra(Const.EXTRA_LESSON_NUM, 0);
                                finalIntent.putExtra(Const.WEEK_NUM, stockObject.week_num);
                                finalIntent.putExtra(Const.DAY_NUM, stockObject.day_num);
                                finalIntent.putExtra(Const.LATITUDE, stockObject.latitude);
                                finalIntent.putExtra(Const.LONGITUDE, stockObject.longitude);
                                finalIntent.putExtra(Const.GROUP_LIST, stockObject.groupList);


                                for (int i = 0; i < stockObject.teachers.size(); i++) {
                                    if (i >= 1) {
                                        stockObject.teacher = stockObject.teacher + ", " + stockObject.teachers.get(i)[1];
                                        stockObject.teacher_id = stockObject.teacher_id + "," + stockObject.teachers.get(i)[0];

                                        Log.i("Teacher" + i, stockObject.teacher);
                                    } else {
                                        stockObject.teacher = stockObject.teachers.get(i)[1];
                                        stockObject.teacher_id = stockObject.teachers.get(i)[0];

                                        Log.i("Teacher" + i, stockObject.teacher);
                                    }
                                }

                                finalIntent.putExtra(Const.LESSON_TEACHER, stockObject.teacher);
                                finalIntent.putExtra(Const.TEACHER_ID, stockObject.teacher_id);

                            }
                            finalIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            context.startActivity(finalIntent);

                        } else if (resultCode == ConnectionResult.SERVICE_MISSING ||
                                resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                                resultCode == ConnectionResult.SERVICE_DISABLED) {
                            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, 1);
                            dialog.show();
                            //Log.e("g_play", String.valueOf(resultCode));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public String getObjectId() {
            return code;
        }

    }

}