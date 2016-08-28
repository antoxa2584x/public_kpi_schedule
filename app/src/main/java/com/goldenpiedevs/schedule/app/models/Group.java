package com.goldenpiedevs.schedule.app.models;

import java.io.Serializable;


public class Group implements Serializable {

    private String groupName;
    private int groupId;

    public String getGroupName() {
        return groupName;
    }

    public Group setGroupName(String groupName) {
        this.groupName = groupName.toUpperCase();
        return this;
    }

    public int getGroupId() {
        return groupId;
    }

    public Group setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }
}
