package com.goldenpiedevs.schedule.app.dataloader;

import android.content.Context;

import com.goldenpiedevs.schedule.app.dataloader.io.TeacherIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;
import com.goldenpiedevs.schedule.app.models.Teacher;

import java.util.List;

public class GlobalLoader {
    private String groupName;
    private Context context;
    private List<Teacher> teacherList = null;
    private DownloadStatusListener statusListener;

    public GlobalLoader(String groupName, Context context) {
        this.groupName = groupName;
        this.context = context;
    }

    public GlobalLoader setStatusListener(DownloadStatusListener downloadStatusListener) {
        statusListener = downloadStatusListener;
        return this;
    }

    public void execute() {
        loadSchedule();
    }

    public void loadSchedule() {
        new ScheduleLoader(context)
                .setStatusListener(new DownloadStatusListener() {
                    @Override
                    public void onComplete(boolean complete) {
                        loadTeachersList();
                    }

                    @Override
                    public void onFailed(int status) {
                        statusListener.onFailed(status);
                    }
                })
                .execute(groupName);

    }


    public void loadTeachersList() {
        new TeachersListLoader(context)
                .setStatusListener(new DownloadStatusListener() {
                    @Override
                    public void onComplete(boolean complete) {
                        loadTeacherSchedule();
                    }

                    @Override
                    public void onFailed(int status) {
                        statusListener.onFailed(status);
                    }
                })
                .execute(groupName);

    }


    public void loadTeacherSchedule() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                teacherList = new TeacherIO().getTeacherListFromFile(groupName, context);
                if (teacherList != null) {
                    for (int i = 0; i < teacherList.size(); i++) {
                        final int iteration = i;
                        new TeacherScheduleLoader(context)
                                .setStatusListener(new DownloadStatusListener() {
                                    @Override
                                    public void onComplete(boolean complete) {
                                        downloadComplete(iteration);
                                    }

                                    @Override
                                    public void onFailed(int status) {
                                        statusListener.onFailed(status);
                                    }
                                })
                                .execute(String.valueOf(teacherList.get(i).getTeacherID()));

                    }
                }

            }
        }).start();
    }


    private void downloadComplete(int iteration) {
        if (teacherList != null && iteration == teacherList.size() - 1) {
            statusListener.onComplete(true);
        }
    }
}
