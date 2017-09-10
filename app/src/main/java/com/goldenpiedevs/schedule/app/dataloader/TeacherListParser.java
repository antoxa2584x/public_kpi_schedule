package com.goldenpiedevs.schedule.app.dataloader;

import com.goldenpiedevs.schedule.app.models.Teacher;
import com.goldenpiedevs.schedule.app.models.jsonobjects.TeacherResponseModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TeacherListParser {
    TeacherResponseModel teacherResponseModel;

    public TeacherListParser(String teacherJson) {
//        String a = new String(teacherJson.getBytes(), "utf-8");
        Gson gson = new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create();
        teacherResponseModel = gson.fromJson(teacherJson, TeacherResponseModel.class);
    }

    public List<Teacher> getTeachersList() {
        List<Teacher> out = new ArrayList<>();
        Teacher teacher;
        if (teacherResponseModel == null)
            return new ArrayList<>();

        for (com.goldenpiedevs.schedule.app.models.jsonobjects.Teacher teacherModel : teacherResponseModel.getData()) {
            teacher = new Teacher();
            teacher.setTeacherID(Integer.valueOf(teacherModel.getTeacherId()));

            if (!teacherModel.getTeacherName().contains("(") || !teacherModel.getTeacherName().contains("#"))
                teacher.setTeacherName(teacherModel.getTeacherName());
            else {
                teacher.setTeacherName(teacherModel.getTeacherName().substring(0, teacherModel.getTeacherName()
                        .indexOf(teacherModel.getTeacherName().contains("(") ? "(" : "#")));
            }

            out.add(teacher);
        }
        return out;

    }
}
