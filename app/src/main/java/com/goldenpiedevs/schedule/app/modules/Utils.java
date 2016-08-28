package com.goldenpiedevs.schedule.app.modules;

import android.content.Context;

import com.goldenpiedevs.schedule.app.R;

public class Utils {
    public static String checkError(int code, Context context) {
        String str;
        switch (code) {
            case Const.STATUS_CODE_NOT_FOUND:
                str = "Інформацію не знайдено";
                break;
            case Const.STATUS_CODE_BAD_REQUEST:
                str = context.getString(R.string.request_error);
                break;
            case Const.STATUS_CODE_BAD_GATEWAY:
                str = context.getString(R.string.gateway_error);
                break;
            case Const.STATUS_CODE_NOT_ALLOWED:
                str = context.getString(R.string.not_allowed_error);
                break;
            case Const.STATUS_CODE_SERVER_ERROR:
                str = context.getString(R.string.server_error);
                break;
            case Const.STATUS_CODE_MOVING_DATA_ERROR:
                str = context.getString(R.string.moving_data_error);
                break;
            default:
                str = "Невідома помилка";
                break;
        }
        return str;
    }
}

