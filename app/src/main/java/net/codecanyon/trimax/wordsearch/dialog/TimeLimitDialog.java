package net.codecanyon.trimax.wordsearch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;



public class TimeLimitDialog extends Dialog implements View.OnClickListener {


    private Context context;
    private LanguageDialog.LanguageSelectionListener listener;
    public static final String DEFAULT_TIME_LIMIT = "no_time_limit";


    public TimeLimitDialog(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.time_limit_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.TimeLimitDialogAnimation;

        Resources resources = ContextUtil.getCustomResources(context);

        final TextView title = findViewById(R.id.lang_title);
        title.setText(resources.getString(R.string.time_mode_title));


        ((TextView)findViewById(R.id.time_limited_txt)).setText(resources.getString(R.string.time_limited_option));
        ((TextView)findViewById(R.id.no_time_limit_txt)).setText(resources.getString(R.string.no_time_limit_option));

        boolean night = Settings.getBooleanValue(context, Constants.NIGHT_MODE_ON, false);

        if(night){
            findViewById(R.id.dialog_inner).setBackgroundResource(R.drawable.dialog_bg_n);
            ((TextView)findViewById(R.id.lang_title)).setTextColor(ContextCompat.getColor(context, R.color.text_color_n));
        }else{
            findViewById(R.id.dialog_inner).setBackgroundResource(R.drawable.dialog_bg);
            ((TextView)findViewById(R.id.lang_title)).setTextColor(ContextCompat.getColor(context, R.color.button_text));
        }

        LinearLayout list = findViewById(R.id.list);

        for(int i=0;i<list.getChildCount();i++){
            View v = list.getChildAt(i);
            if(v instanceof RelativeLayout){
                v.setOnClickListener(this);
                RelativeLayout rl = (RelativeLayout)v;
                TextView tv = ((TextView)rl.getChildAt(0));

                if(night){
                    v.setBackgroundResource(R.drawable.btn_lang_item_selector_n);
                    tv.setTextColor(ContextCompat.getColor(context, R.color.text_color_n));
                }else{
                    v.setBackgroundResource(R.drawable.btn_lang_item_selector);
                    tv.setTextColor(ContextCompat.getColor(context, R.color.app_bg));
                }
            }
        }
    }



    @Override
    public void onClick(final View view) {
        SoundPlayer.playSound(context, SoundPlayer.CLICK);
        listener.selected(context.getResources().getResourceEntryName(view.getId()));
    }



    public void setLangugeSelectionListener(LanguageDialog.LanguageSelectionListener listener){
        this.listener = listener;
    }



    @Override
    public void onBackPressed() {
        dismiss();
    }


}
