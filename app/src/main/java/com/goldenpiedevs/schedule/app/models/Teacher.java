package com.goldenpiedevs.schedule.app.models;

import java.io.Serializable;

public class Teacher implements Serializable {
    private int teacherID;
    private String teacherName;

    public Teacher() {

    }

    public int getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(int teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}

