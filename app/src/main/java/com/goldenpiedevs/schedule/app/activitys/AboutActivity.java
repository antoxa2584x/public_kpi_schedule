package com.goldenpiedevs.schedule.app.activitys;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenpiedevs.schedule.app.BuildConfig;
import com.goldenpiedevs.schedule.app.R;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), getResources().getColor(R.color.primary_dark)));

        TextView code = (TextView) findViewById(R.id.version);
        code.setText(getString(R.string.version_code) + BuildConfig.VERSION_NAME);
        addSomeGlitch();
    }

    private void addSomeGlitch() {
        final int[] logoClick = {0};
        ImageView logo = (ImageView) findViewById(R.id.logo);
        final RelativeLayout glitch = (RelativeLayout) findViewById(R.id.glitch);
        final TextView logoTextView = (TextView) findViewById(R.id.logo_text);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoClick[0]++;
                if (logoClick[0] >= 3) {
                    if (getActionBar() != null)
                        getActionBar().setTitle("П̸̹̮̣̯ͮ͆ͯͥр̛̫̟̬̬̞̌̐ͤӧ̧̯̺̩́ ͕̹͉̻̫ͪͮ̌ͅд̙͚͜о̼͗д̰̜̻̜̗̥͐͑ͣ͌̀̔͡а̜̗͑̕т̮̯̠̩̤̻ͯ̂ͨ̚о̘͉̦ͦ̃ͭ̾к̍̅ͧ̔ͬ͏͎̦͚͓̹̯̮");
                    logoTextView.setText("Р̵̼͚̬͚̙̯о̠̮̅͊̎̎ͥ͒̓з̸͎͉̭̭̥ͥ̽ͮ̂к̡̤̙̺̬̠̲̿л͓̥̬͈̪̫ͫ̊ͤͤ̌̕а̬̗̗̘̟̣͗ͪ́д̗͉̬͚̤ͯͫ͝ ͕̼̻̉̇̈́̽̾ͩ́К͕͚͈̜̻̝П̱͖̜̘̅̂̏ͩͭ͑ͤІ̤̿̀͑̌ͯ́");
                }
                if (logoClick[0] >= 6) {
                    if (getActionBar() != null)
                        getActionBar().setTitle("Н̒͒̍̋ѐ̢͚̋ͣ̌̈ͫ̒ ̲̗͓̬ͅд̶̻̺̺͎͕͖̬ͧе̪̰ͯͣл̹̼͇̭̙̦̞͋ͮ͆̎̊͑́а̝̯̯̯̂̒͝й͎̖̠͔̣̹̘̉́ͯ̏ ̣͖̺̗ͦ̃ͦͭͪ̂є͚̺͙͔̙͎т̺̟̼͖̖͈̬̎̏̌о̪͇̫́̊̎ͮ̃ͭͅг̭̙͔̖͖͗ͬͥ̒͆о͙̖̍̾͂͌ͭ̈͟");
                }
                if (logoClick[0] == 10) {
                    logoClick[0] = 0;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        glitch.setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                    Toast.makeText(getApplicationContext(), "Самое время зачистить boot раздел телефона", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Ух ты. Сколько Вконтакте переписок", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "А истории в Chrome", Toast.LENGTH_LONG).show();

                    glitch.setVisibility(View.VISIBLE);
                }

            }
        });
        glitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
