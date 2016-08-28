package com.goldenpiedevs.schedule.app.dataloader.listeners;

public interface DownloadStatusListener {
    void onComplete(boolean complete);

    void onFailed(int status);
}
