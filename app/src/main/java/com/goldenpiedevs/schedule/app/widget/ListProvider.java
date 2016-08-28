package com.goldenpiedevs.schedule.app.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.activitys.FullInfoActivity;
import com.goldenpiedevs.schedule.app.dataloader.io.GroupIO;
import com.goldenpiedevs.schedule.app.models.Day;
import com.goldenpiedevs.schedule.app.models.Lesson;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.modules.Const;

public class ListProvider implements RemoteViewsFactory { //:TODO: Fix Data get
    public static SharedPreferences sPref;
    private Day day = new Day();
    private Context mContext = null;
    private Lesson tempLesson;

    public ListProvider(Context context) {
        this.mContext = context;
        sPref = context.getSharedPreferences(Const.SCHEDULE, Context.MODE_PRIVATE);
        tempLesson = null;
        populateListItem();
    }

    private void populateListItem() {
        Weeks weeks;
        Day listItemList = new Day();
        day = new Day();
        weeks = new GroupIO().getGroupFromFile(sPref.getString(Const.GROUP, ""), mContext);
        if (sPref.getInt("widget_week", 0) == 1) {
            listItemList = weeks.getDayofFirstWeek(sPref.getInt("widget_day", 0) - 2);
        } else if (sPref.getInt("widget_week", 0) == 2) {
            listItemList = weeks.getDayofSecondWeek(sPref.getInt("widget_day", 0) - 2);
        }
        checkDay(listItemList);
    }

    private void checkDay(Day listItemList) {
        for (int i = 0; i < listItemList.size(); i++) {
            Lesson lesson = listItemList.get(i);
            Lesson final_lesson = new Lesson();
            final_lesson.setFullName(lesson.getFullName());
            final_lesson.setItemNumber(lesson.getItemNumber());
            final_lesson.setNote_photo(lesson.getNote_photo());
            final_lesson.setRoomLocation(lesson.getRoomLocation());
            final_lesson.setClassesType(lesson.getClassesType());
            final_lesson.setTeachers(lesson.getTeachers());
            final_lesson.setNote(lesson.getNote());
            final_lesson.setTeacherID((lesson.getTeacherID()));
            final_lesson.setLatitude(lesson.getLatitude());
            final_lesson.setLongitude((lesson.getLongitude()));

            if ((i < listItemList.size() - 1) && (listItemList.get(i).getItemNumber() == listItemList.get(i + 1).getItemNumber())) {
                final_lesson.setRoomLocation(lesson.getRoomLocation() + "\n" + listItemList.get(i + 1).getRoomLocation());

                if (final_lesson.getTeachers().get(i)[0].equals(listItemList.get(i + 1).getTeachers().get(i + 1)[0])) {
                    final_lesson.getTeachers().add(1, listItemList.get(i + 1).getTeachers().get(0));
                } else {
                    final_lesson.setTeachers(lesson.getTeachers());
                }
                //final_lesson.setTeacher(lesson.getWeek() + "\n" + listItemList.get(i + 1).getWeek());
                //final_lesson.setTeacherID(lesson.getTeacherId() + "," + listItemList.get(i + 1).getTeacherId());
                i++;
            }
            day.add(final_lesson);
        }
    }

    @Override
    public int getCount() {
        return day.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                mContext.getPackageName(), R.layout.widget_list_row);
        Lesson lesson;
        lesson = day.get(position);

        String room = lesson.getRoomLocation();
        String[] times = mContext.getResources().getStringArray(R.array.Times);
        String time;
        time = times[lesson.getItemNumber()];

        Intent intent = new Intent(mContext, FullInfoActivity.class);
        intent.putExtra(Const.LESSON_TITLE, lesson.getFullName());
        intent.putExtra(Const.CAMPUS_NUM, lesson.getRoomLocation());
        intent.putExtra(Const.LESSON_TYPE, lesson.getClassesType());
        intent.putExtra(Const.LATITUDE, lesson.getLatitude());
        intent.putExtra(Const.LONGITUDE, lesson.getLongitude());
        intent.putExtra(Const.LESSON_TIME, time);
        intent.putExtra(Const.DAY_NUM, sPref.getInt("widget_day", 0) - 1);
        intent.putExtra(Const.WEEK_NUM, sPref.getInt("widget_week", 0) - 1);

        intent.putExtra(Const.EXTRA_LESSON_NUM, 0);

        for (int i = 0; i < lesson.getTeachers().size(); i++) {
            if (i >= 1) {
                lesson.setTeacher(lesson.getTeacher() + ", " + lesson.getTeachers().get(i)[1]);
                lesson.setTeacherID(lesson.getTeacherID() + ", " + lesson.getTeachers().get(i)[0]);
            } else {
                lesson.setTeacher(lesson.getTeachers().get(i)[1]);
                lesson.setTeacherID(lesson.getTeachers().get(i)[0]);
            }
        }

        intent.putExtra(Const.LESSON_TEACHER, lesson.getTeacher());
        intent.putExtra(Const.TEACHER_ID, lesson.getTeacherID());


        if (tempLesson != null) {
            if (tempLesson.getTeacher().contains(",")) {
                intent.putExtra(Const.EXTRA_LESSON_NUM, 1);
            }
        }


        tempLesson = lesson;

        intent.putExtra(Const.LESSON_NUM, position + 1);
        intent.putExtra(Const.NOTE, lesson.getNote());

        switch (lesson.getRoomLocation()) {
            case "Пол.-ін-т РАКА":
                remoteView.setTextViewText(R.id.widget_locate, lesson.getClassesType() + " " + "ін-т РАКА");
                break;
            case "Пол.-ін-т ім. Амосова":
                remoteView.setTextViewText(R.id.widget_locate, lesson.getClassesType() + " " + "ін-т ім. Амосова");
                break;
            default:
                remoteView.setTextViewText(R.id.widget_locate, lesson.getClassesType() + " " + room);
                break;
        }

        remoteView.setTextViewText(R.id.lesson_num, String.valueOf(lesson.getItemNumber()));
        remoteView.setTextViewText(R.id.content, lesson.getFullName());
        remoteView.setTextViewText(R.id.time, time);

        if (!lesson.getNote().equals("")) {
            remoteView.setViewVisibility(R.id.imageView, View.VISIBLE);
            remoteView.setTextColor(R.id.lesson_num, Color.WHITE);
        } else {
            remoteView.setViewVisibility(R.id.imageView, View.INVISIBLE);
            remoteView.setTextColor(R.id.lesson_num, Color.parseColor("#707070"));
        }

        remoteView.setOnClickFillInIntent(R.id.widger_reletive_layout, intent);

        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        populateListItem();
    }

    @Override
    public void onDestroy() {
    }

}