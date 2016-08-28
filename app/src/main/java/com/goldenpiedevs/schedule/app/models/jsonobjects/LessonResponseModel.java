
package com.goldenpiedevs.schedule.app.models.jsonobjects;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class LessonResponseModel {

    @SerializedName("statusCode")
    @Expose
    public Integer statusCode;
    @SerializedName("timeStamp")
    @Expose
    public Integer timeStamp;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("debugInfo")
    @Expose
    public String debugInfo;
    @SerializedName("meta")
    @Expose
    public Object meta;
    @SerializedName("data")
    @Expose
    public List<LessonData> data = new ArrayList<>();

}
