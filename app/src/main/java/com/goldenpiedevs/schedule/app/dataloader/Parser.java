package com.goldenpiedevs.schedule.app.dataloader;

import android.text.TextUtils;
import android.util.Log;

import com.goldenpiedevs.schedule.app.models.Day;
import com.goldenpiedevs.schedule.app.models.Group;
import com.goldenpiedevs.schedule.app.models.Lesson;
import com.goldenpiedevs.schedule.app.models.Weeks;
import com.goldenpiedevs.schedule.app.models.jsonobjects.GroupModel;
import com.goldenpiedevs.schedule.app.models.jsonobjects.LessonData;
import com.goldenpiedevs.schedule.app.models.jsonobjects.LessonResponseModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final LessonResponseModel lesson;

    private boolean teacherLoader;

    public Parser(String lessonJson, boolean teacherLoader) {

        Gson gson = new Gson();
        lesson = gson.fromJson(lessonJson, LessonResponseModel.class);
        this.teacherLoader = teacherLoader;
    }

    public boolean isDayExist(int dayNumb, boolean week) {
        return getDay(dayNumb, week).size() != 0;
    }

    public Weeks getWeek() {
        Weeks st = new Weeks();
        for (int i = 1; i < 7; i++) {
            Day day = getDay(i, true);
            if (day != null)
                st.addtoFirstWeek(day);
            day = getDay(i, false);
            if (day != null) {
                st.addtoSecondWeek(day);
            }
        }
        return st;

    }

    public Day getDay(int dayNumb, boolean week) {
        Day out = new Day();
        out.setDayNumb(dayNumb);
        int weekNum = week ? 1 : 2;

        List<LessonData> lessonDataList = new ArrayList<>();
        for (int i = 0; i < lesson.getData().size(); i++) {
            LessonData lessonData = lesson.getData().get(i);
            if (Integer.parseInt(lessonData.getDayNumber()) == dayNumb
                    && Integer.parseInt(lessonData.getLessonWeek()) == weekNum)
                lessonDataList.add(lessonData);
        }

        if (lessonDataList.isEmpty())
            return null;

        for (int i = 0; i < lessonDataList.size(); i++) {
            LessonData lessonData = lessonDataList.get(i);
            Lesson someLesson = new Lesson();

            someLesson.setFullName(lessonData.getLessonFullName());
            someLesson.setItemNumber(Integer.parseInt(lessonData.getLessonNumber()));
            someLesson.setClassesType(lessonData.getLessonType());

            String lessonRoom = lessonData.getLessonRoom();

            if (!TextUtils.isEmpty(lessonRoom)) {
                String room = "";
                String campus = "";
                if (lessonRoom.contains(",")) {
                    if (!lessonRoom.contains("а,б")) {
                        if ((!lessonRoom.contains("Прак")) && (!lessonRoom.contains("Лек")) && (!lessonRoom.contains("Лаб"))) {
                            String cured = lessonRoom.substring(0, lessonRoom.indexOf(","));
                            Log.i("Cured", cured);
                            room = cured.substring(0, cured.lastIndexOf("-"));
                            campus = cured.substring(cured.lastIndexOf("-") + 1, cured.length());
                        } else {
                            if (lessonRoom.substring(0, lessonRoom.indexOf(",")).contains("Прак") ||
                                    lessonRoom.substring(0, lessonRoom.indexOf(",")).contains("Лек") ||
                                    lessonRoom.substring(0, lessonRoom.indexOf(",")).contains("Лаб")) {

                                String cured = lessonRoom.substring(lessonRoom.indexOf(","), lessonRoom.length());
                                room = cured.substring(1, cured.lastIndexOf("-"));
                                campus = cured.substring(cured.lastIndexOf("-") + 1, cured.length());

                                if (lessonRoom.contains("Прак") || lessonRoom.contains("Лек") || lessonRoom.contains("Лаб")) {
                                    if (someLesson.getClassesType().equals("не вказано")) {
                                        someLesson.setClassesType(lessonRoom.substring(0, lessonRoom.indexOf(",")));
                                    }
                                }
                            }
                            if (lessonRoom.substring(lessonRoom.indexOf(","), lessonRoom.length()).contains("Прак") ||
                                    lessonRoom.substring(lessonRoom.indexOf(","), lessonRoom.length()).contains("Лек") ||
                                    lessonRoom.substring(lessonRoom.indexOf(","), lessonRoom.length()).contains("Лаб")) {

                                String cured = lessonRoom.substring(0, lessonRoom.indexOf(","));
                                room = cured.substring(0, cured.lastIndexOf("-"));
                                campus = cured.substring(cured.lastIndexOf("-") + 1, cured.length());

                                if (lessonRoom.contains("Прак") || lessonRoom.contains("Лек") || lessonRoom.contains("Лаб")) {
                                    if (someLesson.getClassesType().equals("не вказано")) {
                                        someLesson.setClassesType(lessonRoom.substring(lessonRoom.indexOf(","), lessonRoom.length()));
                                    }
                                }
                            }
                        }
                    } else if (lessonRoom.contains("а,б")) {
                        room = lessonRoom.substring(0, lessonRoom.lastIndexOf("-"));
                        campus = lessonRoom.substring(lessonRoom.lastIndexOf("-") + 1, lessonRoom.length());
                    }
                } else {
                    room = lessonRoom.substring(0, lessonRoom.lastIndexOf("-"));
                    campus = lessonRoom.substring(lessonRoom.lastIndexOf("-") + 1, lessonRoom.length());
                }

                someLesson.setRoomLocation(campus + "-" + room);
            } else
                someLesson.setRoomLocation("не вказано");

            if (!teacherLoader) {
                if (!lessonData.getTeachers().isEmpty()) {
                    List<String[]> teachersData = new ArrayList<>();
                    for (int j = 0; j < lessonData.getTeachers().size(); j++) {
                        String temp_id = lessonData.getTeachers().get(j).getTeacherId();
                        String temp_name = lessonData.getTeachers().get(j).getTeacherName();
                        String[] strings = {temp_id, temp_name};
                        teachersData.add(j, strings);

                    }
                    someLesson.setTeachers(teachersData);
                } else {
                    String teacherName = lessonData.getTeacherName();
                    if (!TextUtils.isEmpty(teacherName)) {
                        List<String[]> teachersData = new ArrayList<>();
                        if (!teacherName.contains("(") || !teacherName.contains("#")) {
                            String[] strings = {"", teacherName};
                            teachersData.add(0, strings);
                            someLesson.setTeachers(teachersData);
                        } else {
                            if (teacherName.contains("(")) {
                                String[] strings = {"", teacherName.substring(0, teacherName.indexOf("("))};
                                teachersData.add(0, strings);
                                someLesson.setTeachers(teachersData);
                            } else {
                                String[] strings = {"", teacherName.substring(0, teacherName.indexOf("#"))};
                                teachersData.add(0, strings);
                                someLesson.setTeachers(teachersData);
                            }
                        }
                    } else {
                        List<String[]> teachersData = new ArrayList<>();
                        String[] strings = {"не вказано", "не вказано"};
                        teachersData.add(0, strings);
                        someLesson.setTeachers(teachersData);
                    }
                }
            } else {
                String teacherName = lessonData.getTeacherName();
                if (!TextUtils.isEmpty(teacherName)) {
                    List<String[]> teachersData = new ArrayList<>();
                    if (!teacherName.contains("(") || !teacherName.contains("#")) {
                        String[] strings = {"", teacherName};
                        teachersData.add(0, strings);
                        someLesson.setTeachers(teachersData);
                    } else {
                        if (teacherName.contains("(")) {
                            String[] strings = {"", teacherName.substring(0, teacherName.indexOf("("))};
                            teachersData.add(0, strings);
                            someLesson.setTeachers(teachersData);
                        } else {
                            String[] strings = {"", teacherName.substring(0, teacherName.indexOf("#"))};
                            teachersData.add(0, strings);
                            someLesson.setTeachers(teachersData);
                        }
                    }

                } else {
                    List<String[]> teachersData = new ArrayList<>();
                    String[] strings = {"", "не вказано"};
                    teachersData.add(0, strings);
                    someLesson.setTeachers(teachersData);
                }
            }

            if (teacherLoader) {
                try {
                    if (!lessonData.getGroups().isEmpty()) {
                        for (int j = 0; j < lessonData.getGroups().size(); j++) {
                            GroupModel groupModel = lessonData.getGroups().get(j);
                            Group group = new Group();
                            group.setGroupId(Integer.parseInt(String.valueOf(groupModel.getGroupId())))
                                    .setGroupName(groupModel.getGroupFullName());
                            someLesson.getGroupArrayList().add(group);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!lessonData.getRooms().isEmpty()) {
                someLesson.setLatitude(Double.parseDouble(lessonData.getRooms().get(0).getRoomLatitude()));
                someLesson.setLongitude(Double.parseDouble(lessonData.getRooms().get(0).getRoomLongitude()));
            } else {
                someLesson.setLatitude(0);
                someLesson.setLongitude(0);
            }

            if (someLesson.getFullName().equals("Фізичне виховання")) {
                someLesson.setRoomLocation("24");
                someLesson.setClassesType("Прак");
            }

            someLesson.setNote("");
            someLesson.setNote_photo(new ArrayList<String>());

            out.add(someLesson);
        }

        return out;
    }

}
