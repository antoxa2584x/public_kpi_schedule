package com.goldenpiedevs.schedule.app.dataloader.listeners;

import com.goldenpiedevs.schedule.app.models.Marks;

public interface TeacherSenderInterface {
    void onTaskCompleted(Marks marks);

    void onTaskFailed();

    void onTimeoutException();
}
