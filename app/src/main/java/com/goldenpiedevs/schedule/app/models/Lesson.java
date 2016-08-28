package com.goldenpiedevs.schedule.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс - обект содержащий в себе расписание
 */


public class Lesson implements Serializable {
    private int itemNumber;
    private String TeacherID;
    private String roomLocation;
    private String classesType;
    private String fullName;
    private String teacher;
    private String note;
    private double latitude;
    private double longitude;
    private List<String[]> teachers;
    private ArrayList<String> note_photo;
    private ArrayList<Group> groupArrayList = new ArrayList<>();

    public Lesson() {
    }

    public List<String[]> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<String[]> teachers) {
        this.teachers = teachers;
    }

    public ArrayList<String> getNote_photo() {
        return note_photo;
    }

    public void setNote_photo(ArrayList<String> note_photo) {
        this.note_photo = note_photo;
    }

    public ArrayList<Group> getGroupArrayList() {
        return groupArrayList;
    }

    public void setGroupArrayList(ArrayList<Group> groupArrayList) {
        this.groupArrayList = groupArrayList;
    }

    public int getItemNumber() {

        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getTeacherID() {
        return TeacherID;
    }

    public void setTeacherID(String teacherID) {
        TeacherID = teacherID;
    }

    public String getRoomLocation() {
        return roomLocation;
    }

    public void setRoomLocation(String roomLocation) {
        this.roomLocation = roomLocation;
    }

    public String getClassesType() {
        return classesType;
    }

    public void setClassesType(String classesType) {
        this.classesType = classesType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
