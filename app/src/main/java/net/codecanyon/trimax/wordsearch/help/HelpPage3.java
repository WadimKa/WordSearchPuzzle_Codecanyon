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


public class HelpPage3 extends Help {


    public HelpPage3(Context context) {
        super(context);
        this.context = context;
    }


    public HelpPage3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    public HelpPage3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        boolean night = Settings.getBooleanValue(context, Constants.NIGHT_MODE_ON, false);

        ImageView hint = findViewById(R.id.hint);
        TextView hints_left = findViewById(R.id.hints_left);

        if(night) {
            hint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.reward_icon_n));
            hints_left.setTextColor(ContextCompat.getColor(context, R.color.button_text_n));
        }else{
            hint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.reward_icon));
            hints_left.setTextColor(ContextCompat.getColor(context, R.color.button_text));
        }

        ((TextView)findViewById(R.id.title)).setText(resources.getString(R.string.hint_reward_title));
        ((TextView)findViewById(R.id.text)).setText(resources.getString(R.string.hint_reward_text));
    }

}
