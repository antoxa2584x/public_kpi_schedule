
package com.goldenpiedevs.schedule.app.models.jsonobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Teacher {

    @SerializedName("teacher_id")
    @Expose
    public String teacherId;
    @SerializedName("teacher_name")
    @Expose
    public String teacherName;
    @SerializedName("teacher_full_name")
    @Expose
    public String teacherFullName;
    @SerializedName("teacher_short_name")
    @Expose
    public String teacherShortName;
    @SerializedName("teacher_url")
    @Expose
    public String teacherUrl;
    @SerializedName("teacher_rating")
    @Expose
    public String teacherRating;

}
