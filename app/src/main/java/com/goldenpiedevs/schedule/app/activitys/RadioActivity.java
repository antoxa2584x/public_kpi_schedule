package com.goldenpiedevs.schedule.app.activitys;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.dataloader.SongDataLoader;
import com.goldenpiedevs.schedule.app.events.SongInfoLoaded;
import com.goldenpiedevs.schedule.app.modules.CustomViews.VisualizerView;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class RadioActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.visualizer)
    VisualizerView mVisualizerView;

    @BindView(R.id.play_pause)
    FloatingActionButton playPause;
    @BindView(R.id.stream_progress)
    ProgressBar streamProgress;

    @BindView(R.id.song_name)
    TextView songName;
    @BindView(R.id.singer)
    TextView singer;
    String url = "http://77.47.130.190:8000/radiokpi";

    private MediaPlayer mPlayer;
    private boolean playing = false;
    private Handler mHandler;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            new SongDataLoader().execute();
            long mInterval = 15000;
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mHandler = new Handler();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.material_grey_850), PorterDuff.Mode.SRC_ATOP);

        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        playPause.setClickable(false);
        new SongDataLoader().execute();
        mStatusChecker.run();

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(url);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(RadioActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.play_pause)
    protected void onPlayClick() {
        if (isPlaying()) {
            if(mPlayer.isPlaying())
                mPlayer.pause();
            playPause.setImageResource(R.drawable.ic_av_play_arrow);

            playPause.setColorNormal(Color.parseColor("#554411"));
            playPause.setColorPressed(Color.parseColor("#221100"));
            playPause.setColorPressed(Color.parseColor("#665544"));
        } else {
            if(!mPlayer.isPlaying())
                mPlayer.start();
            playPause.setImageResource(R.drawable.ic_av_pause);

            playPause.setColorNormal(getResources().getColor(R.color.pink));
            playPause.setColorPressed(getResources().getColor(R.color.pink_dark));
            playPause.setColorPressed(getResources().getColor(R.color.pink_dark));
        }
        setPlaying(!isPlaying());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(SongInfoLoaded event) {
        songName.setText(event.getName());
        singer.setText(event.getArtist());
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    @Override
    public void onBackPressed() {
        mPlayer.stop();
        mPlayer.release();
        super.onBackPressed();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        streamProgress.setVisibility(View.GONE);
        playPause.setClickable(true);

        Visualizer mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
    }
}
