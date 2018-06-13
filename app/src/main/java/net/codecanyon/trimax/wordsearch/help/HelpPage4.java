package net.codecanyon.trimax.wordsearch.help;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;


public class HelpPage4 extends Help {


    public HelpPage4(Context context) {
        super(context);
        this.context = context;
    }


    public HelpPage4(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    public HelpPage4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        boolean night = Settings.getBooleanValue(context, Constants.NIGHT_MODE_ON, false);

        ImageView achievements = findViewById(R.id.achievements);
        ImageView leaderboards = findViewById(R.id.leaderboards);

        if(night) {
            achievements.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.achievement_help_n));
            leaderboards.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.leaderboards_help_n));
        }else{
            achievements.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.achievement_help));
            leaderboards.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.leaderboards_help));
        }

        ((TextView)findViewById(R.id.title)).setText(resources.getString(R.string.leaderboards_achievements_title));
        ((TextView)findViewById(R.id.text)).setText(resources.getString(R.string.leaderboards_achievements_text));
    }


}
