
package com.goldenpiedevs.schedule.app.models.jsonobjects;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class LessonData {

    @SerializedName("lesson_id")
    @Expose
    public String lessonId;
    @SerializedName("group_id")
    @Expose
    public String groupId;
    @SerializedName("day_number")
    @Expose
    public String dayNumber;
    @SerializedName("day_name")
    @Expose
    public String dayName;
    @SerializedName("lesson_name")
    @Expose
    public String lessonName;
    @SerializedName("lesson_full_name")
    @Expose
    public String lessonFullName = "не вказано";
    @SerializedName("lesson_number")
    @Expose
    public String lessonNumber = "-1";
    @SerializedName("lesson_room")
    @Expose
    public String lessonRoom;
    @SerializedName("lesson_type")
    @Expose
    public String lessonType = "не вказано";
    @SerializedName("teacher_name")
    @Expose
    public String teacherName;
    @SerializedName("lesson_week")
    @Expose
    public String lessonWeek;
    @SerializedName("time_start")
    @Expose
    public String timeStart;
    @SerializedName("time_end")
    @Expose
    public String timeEnd;
    @SerializedName("rate")
    @Expose
    public String rate;
    @SerializedName("teachers")
    @Expose
    public List<Teacher> teachers = new ArrayList<>();
    @SerializedName("rooms")
    @Expose
    public List<Room> rooms = new ArrayList<>();
    @SerializedName("groups")
    @Expose
    private List<GroupModel> groups = new ArrayList<>();

}
