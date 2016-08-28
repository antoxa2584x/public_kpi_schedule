package com.goldenpiedevs.schedule.app.modules.CustomViews;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.goldenpiedevs.schedule.app.R;


public class ControllableNestedScrollView extends NestedScrollView {

    private int mLastScrollY;
    private ScrollDirectionListener directionListener;

    public ControllableNestedScrollView(Context context) {
        super(context);
    }

    public ControllableNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControllableNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollDirectionListener(ScrollDirectionListener scrollDirectionListener) {
        this.directionListener = scrollDirectionListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (directionListener!=null) {
            boolean isSignificantDelta = Math.abs(t - mLastScrollY) > getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);
            if (isSignificantDelta) {
                if (t > mLastScrollY) {
                    directionListener.onScrollUp();
                } else {
                    directionListener.onScrollDown();
                }
            }
            mLastScrollY = t;
        }
    }


    public interface ScrollDirectionListener {
        void onScrollDown();

        void onScrollUp();
    }
}
