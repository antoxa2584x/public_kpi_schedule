package com.goldenpiedevs.schedule.app.fragments;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.goldenpiedevs.schedule.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment {

    @BindView(R.id.view)
    protected SubsamplingScaleImageView subsamplingScaleImageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_map_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), getResources().getColor(R.color.primary_dark)));

        subsamplingScaleImageView.setImage(ImageSource.resource(R.drawable.map));
        subsamplingScaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        collapseView();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.view)
    protected void onMapClick() {
        collapseView();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void collapseView() {
        try {
            getView().post(new Runnable() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void run() {
                    getView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


}
