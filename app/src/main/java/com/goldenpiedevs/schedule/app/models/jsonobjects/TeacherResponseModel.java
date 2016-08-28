package com.goldenpiedevs.schedule.app.models.jsonobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TeacherResponseModel {

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
public Object debugInfo;
@SerializedName("meta")
@Expose
public Object meta;
@SerializedName("data")
@Expose
public List<Teacher> data = new ArrayList<>();

}