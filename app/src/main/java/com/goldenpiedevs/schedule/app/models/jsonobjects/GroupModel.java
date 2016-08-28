package com.goldenpiedevs.schedule.app.models.jsonobjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class GroupModel {

    @SerializedName("group_id")
    @Expose
    private Integer groupId;
    @SerializedName("group_full_name")
    @Expose
    private String groupFullName;
    @SerializedName("group_prefix")
    @Expose
    private String groupPrefix;
    @SerializedName("group_okr")
    @Expose
    private String groupOkr;
    @SerializedName("group_type")
    @Expose
    private String groupType;
    @SerializedName("group_url")
    @Expose
    private String groupUrl;
}