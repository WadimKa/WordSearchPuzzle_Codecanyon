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



public class HelpPage5 extends Help {


    public HelpPage5(Context context) {
        super(context);
        this.context = context;

    }


    public HelpPage5(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    public HelpPage5(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        boolean night = Settings.getBooleanValue(context, Constants.NIGHT_MODE_ON, false);
        ImageView img = findViewById(R.id.img);

        if(night) {
            img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.sign_out_n));
        }else{
            img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.sign_out));
        }

        ((TextView)findViewById(R.id.title)).setText(resources.getString(R.string.gpg_sign_in_out_title));
        ((TextView)findViewById(R.id.text)).setText(resources.getString(R.string.gpg_sing_in_out_text));
    }


}
