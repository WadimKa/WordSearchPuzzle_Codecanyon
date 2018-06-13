package net.codecanyon.trimax.wordsearch.help;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;


public class Help extends RelativeLayout {

    protected Context context;
    protected Resources resources;

    public Help(Context context) {
        super(context);
        this.context = context;
    }

    public Help(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public Help(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        resources = ContextUtil.getCustomResources(context);

        boolean night = Settings.getBooleanValue(context, Constants.NIGHT_MODE_ON, false);
        TextView title = findViewById(R.id.title);
        TextView text = findViewById(R.id.text);

        View content = findViewById(R.id.content);
        View line0 = findViewById(R.id.line0);
        View line1 = findViewById(R.id.line);
        if(night) {
            int app_bg_n = ContextCompat.getColor(context, R.color.button_text_n);
            int line = ContextCompat.getColor(context, R.color.black);
            int textColor = ContextCompat.getColor(context, R.color.text_color_n);
            content.setBackgroundColor(app_bg_n);
            line0.setBackgroundColor(line);
            line1.setBackgroundColor(line);
            title.setTextColor(textColor);
            text.setTextColor(textColor);
        }else{
            int mid = ContextCompat.getColor(context, R.color.light_brown_text);
            int line = ContextCompat.getColor(context, R.color.secondary_color);
            content.setBackgroundColor(mid);
            line0.setBackgroundColor(line);
            line1.setBackgroundColor(line);
            title.setTextColor(mid);
            text.setTextColor(mid);
        }
    }
}
