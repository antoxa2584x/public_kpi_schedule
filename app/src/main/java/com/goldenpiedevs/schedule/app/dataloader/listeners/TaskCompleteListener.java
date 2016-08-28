package com.goldenpiedevs.schedule.app.dataloader.listeners;

public interface TaskCompleteListener {
    void onTaskCompleted();

    void onTaskFailed();
}
