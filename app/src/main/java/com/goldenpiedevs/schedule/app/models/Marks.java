package com.goldenpiedevs.schedule.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Marks implements Parcelable {

    public static final Parcelable.Creator<Marks> CREATOR = new Parcelable.Creator<Marks>() {
        public Marks createFromParcel(Parcel in) {
            return new Marks(in);
        }

        @Override
        public Marks[] newArray(int size) {
            return new Marks[0];
        }
    };
    private int teacherId;
    private float objectKnow;
    private float exactness;
    private float relationToStudents;
    private float humorSense;
    private float bribery;
    private float teacherRate;


    public Marks() {
    }


    public Marks(Parcel in) {
        this.teacherId = in.readInt();
        this.objectKnow = in.readFloat();
        this.exactness = in.readFloat();
        this.relationToStudents = in.readFloat();
        this.humorSense = in.readFloat();
        this.bribery = in.readFloat();
        this.teacherRate = in.readFloat();
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public float getObjectKnow() {
        return objectKnow;
    }

    public void setObjectKnow(float objectKnow) {
        this.objectKnow = objectKnow;
    }

    public float getExactness() {
        return exactness;
    }

    public void setExactness(float exactness) {
        this.exactness = exactness;
    }

    public float getRelationToStudents() {
        return relationToStudents;
    }

    public void setRelationToStudents(float relationToStudents) {
        this.relationToStudents = relationToStudents;
    }

    public float getHumorSense() {
        return humorSense;
    }

    public void setHumorSense(float humorSense) {
        this.humorSense = humorSense;
    }

    public float getBribery() {
        return bribery;
    }

    public void setBribery(float bribery) {
        this.bribery = bribery;
    }

    public float getTeacherRate() {
        return teacherRate;
    }

    public void setTeacherRate(float teacherRate) {
        this.teacherRate = teacherRate;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(teacherId);
        dest.writeFloat(objectKnow);
        dest.writeFloat(exactness);
        dest.writeFloat(relationToStudents);
        dest.writeFloat(humorSense);
        dest.writeFloat(bribery);
        dest.writeFloat(teacherRate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}