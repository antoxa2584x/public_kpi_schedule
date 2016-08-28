package com.goldenpiedevs.schedule.app.modules;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Loger {
    public static void writeLogToFile(String logFileName, String log) throws IOException {
        File logDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.rozklad-kpi/log");
        logDir.mkdirs();
        File logFile = new File(logDir, logFileName + ".txt");
        BufferedWriter b = new BufferedWriter(new FileWriter(logFile));
        b.write(log);
        b.flush();
        b.close();
    }
}

