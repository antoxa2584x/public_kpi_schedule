package com.goldenpiedevs.schedule.app.modules;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class ViewUtils {

    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static int navigationBarHeight = -1;

    
    public static void hideKeyboard(Context context, View view) {
        if (context == null) {
            return;
        }
        if (view == null) {
            view = new View(context);
        }
        InputMethodManager inputManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(@Nullable Activity activity) {
        if (activity == null) {
            return;
        }
        hideKeyboard(activity, activity.getCurrentFocus());
    }

    public static void showKeyboard(Context context, View view) {
        if (context == null) {
            return;
        }
        if (view == null) {
            view = new View(context);
        }
        InputMethodManager inputManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInputFromInputMethod(view.getWindowToken(), 0);
    }

    public static void showKeyboard(@Nullable Activity activity) {
        if (activity == null) {
            return;
        }
        showKeyboard(activity, activity.getCurrentFocus());
    }

    public static int getNavigationBarHeight(@NonNull Context context) {
        if (navigationBarHeight < 0) {
            boolean hasMenuKey =
                    ViewConfiguration.get(context.getApplicationContext()).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

            navigationBarHeight = 0;

            if (!hasBackKey || (!hasHomeKey && !hasMenuKey)) {
                // then has UI system navigation buttons (back/home/recent)
                Resources resources = context.getResources();
                int resourceId =
                        resources.getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    navigationBarHeight = resources.getDimensionPixelSize(resourceId);
                }
            }
        }
        return navigationBarHeight;
    }

    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            if (Build.VERSION.SDK_INT >= 13) {
                Point size = new Point();
                display.getSize(size);
                screenWidth = size.x;
            } else {
                screenWidth = display.getWidth();
            }
        }
        return screenWidth;
    }

    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            if (Build.VERSION.SDK_INT >= 13) {
                Point size = new Point();
                display.getSize(size);
                screenHeight = size.y;
            } else {
                screenHeight = display.getHeight();
            }
        }
        return screenHeight;
    }
}
