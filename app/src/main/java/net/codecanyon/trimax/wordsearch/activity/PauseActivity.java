package net.codecanyon.trimax.wordsearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;

public class PauseActivity extends Activity implements View.OnClickListener{

    public static final int RESUME = 0;
    public static final int RESTART_GAME = 1;
    public static final int GO_HOME = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);

        View back = findViewById(R.id.back);
        back.setOnClickListener(this);

        findViewById(R.id.resume_btn).setOnClickListener(this);
        findViewById(R.id.restart_btn).setOnClickListener(this);
        findViewById(R.id.home_btn).setOnClickListener(this);

        findViewById(R.id.settings_btn).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SoundPlayer.playSound(PauseActivity.this, SoundPlayer.CLICK);
                int action = motionEvent.getAction();

                if (action == MotionEvent.ACTION_DOWN)
                    ContextUtil.buttonDown(view);
                if(action == MotionEvent.ACTION_UP)
                    ContextUtil.buttonUp(view);
                return false;
            }
        });

        findViewById(R.id.settings_btn).setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        SoundPlayer.playSound(PauseActivity.this, SoundPlayer.CLICK);
        switch (view.getId()){
            case R.id.settings_btn:{
                Intent intent = new Intent(PauseActivity.this, ConfigActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_to_left_1, R.anim.slide_right_to_left_2);
                break;
            }
            case R.id.restart_btn:{
                setResult(RESTART_GAME);
                finish();
                overridePendingTransition(R.anim.slide_left_to_right_2, R.anim.slide_left_to_right_1);
                break;
            }
            case R.id.resume_btn:
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.slide_left_to_right_2, R.anim.slide_left_to_right_1);
                break;

            case R.id.home_btn:{
                setResult(GO_HOME);
                finish();
                break;
            }

        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        setLabels();
        toggleMode();
        setLabels();
    }



    public void setLabels() {
        Resources r = ContextUtil.getCustomResources(this);

        ((TextView)findViewById(R.id.title)).setText(r.getString(R.string.app_name));
        ((TextView)findViewById(R.id.title_2)).setText(r.getString(R.string.paused));
        ((TextView)findViewById(R.id.resume)).setText(r.getString(R.string.resume));
        ((TextView)findViewById(R.id.restart)).setText(r.getString(R.string.restart));
        ((TextView)findViewById(R.id.home)).setText(r.getString(R.string.home));
    }



    public void toggleMode() {
        boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);
        View window = this.getWindow().getDecorView();

        View title_2_container = findViewById(R.id.title_2_container);
        TextView title = findViewById(R.id.title);
        TextView title_2 = findViewById(R.id.title_2);
        View separator = findViewById(R.id.separator);
        ImageButton back = findViewById(R.id.back);
        View resume_btn = findViewById(R.id.resume_btn);
        View restart_btn = findViewById(R.id.restart_btn);
        View home_btn = findViewById(R.id.home_btn);
        TextView resume = findViewById(R.id.resume);
        TextView restart = findViewById(R.id.restart);
        TextView home = findViewById(R.id.home);
        ImageView ic1 = findViewById(R.id.ic1);
        ImageView ic2 = findViewById(R.id.ic2);
        ImageView ic3 = findViewById(R.id.ic3);
        ImageButton settings_btn = findViewById(R.id.settings_btn);

        if(night) {
            window.setBackgroundColor(ContextCompat.getColor(this, R.color.app_bg_n));
            title_2_container.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_color_n));

            int text_color_n = ContextCompat.getColor(this, R.color.text_color_n);
            title.setTextColor(text_color_n);
            title_2.setTextColor(text_color_n);
            separator.setBackgroundColor(ContextCompat.getColor(this, R.color.stripe_n));
            back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_left_n));
            resume_btn.setBackgroundResource(R.drawable.btn_difficulty_selector_n);
            restart_btn.setBackgroundResource(R.drawable.btn_difficulty_selector_n);
            home_btn.setBackgroundResource(R.drawable.btn_difficulty_selector_n);
            resume.setTextColor(text_color_n);
            restart.setTextColor(text_color_n);
            home.setTextColor(text_color_n);

            int diff_icon_bg_n = R.drawable.toolbar_btn_bg_n;
            ic1.setBackgroundResource(diff_icon_bg_n);
            ic2.setBackgroundResource(diff_icon_bg_n);
            ic3.setBackgroundResource(diff_icon_bg_n);
            ic1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.resume_icon_n));
            ic2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.restart_icon_n));
            ic3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.home_icon_n));
            settings_btn.setBackgroundResource(R.drawable.toolbar_icon_bg_n);
            settings_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.settings_icon_n));
        }else{
            window.setBackgroundColor(ContextCompat.getColor(this, R.color.app_bg));
            title_2_container.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_color));
            title.setTextColor(ContextCompat.getColor(this, R.color.diff_title));
            int white = ContextCompat.getColor(this, R.color.button_text);
            title_2.setTextColor(white);
            separator.setBackgroundColor(white);
            back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_left));

            int btn_difficulty_selector = R.drawable.btn_difficulty_selector;
            resume_btn.setBackgroundResource(btn_difficulty_selector);
            restart_btn.setBackgroundResource(btn_difficulty_selector);
            home_btn.setBackgroundResource(btn_difficulty_selector);
            resume.setTextColor(ContextCompat.getColor(this, R.color.pause_btn_color));
            restart.setTextColor(ContextCompat.getColor(this, R.color.pause_btn_color));
            home.setTextColor(ContextCompat.getColor(this, R.color.pause_btn_color));

            int diff_icon_bg = R.drawable.diff_icon_bg;
            ic1.setBackgroundResource(diff_icon_bg);
            ic2.setBackgroundResource(diff_icon_bg);
            ic3.setBackgroundResource(diff_icon_bg);
            ic1.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.resume_icon));
            ic2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.restart_icon));
            ic3.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.home_icon));
            settings_btn.setBackgroundResource(R.drawable.toolbar_icon_bg);
            settings_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.settings_icon));
        }
    }



    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_left_to_right_2, R.anim.slide_left_to_right_1);
    }


}
