package net.codecanyon.trimax.wordsearch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;





public class LanguageDialog extends Dialog implements View.OnClickListener {


    private Context context;
    private LanguageSelectionListener listener;
    private Resources resources;


    public LanguageDialog(Context context, Resources resources) {
        super(context);
        this.context = context;
        this.resources = resources;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.language_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.LanguageDialogAnimation;

        final TextView title = findViewById(R.id.lang_title);
        title.setText(resources.getString(R.string.pref_title_language));



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
            v.setVisibility(View.VISIBLE);

            if(v instanceof RelativeLayout){
                v.setOnClickListener(this);
                RelativeLayout rl = (RelativeLayout)v;
                TextView tv = ((TextView)rl.getChildAt(0));

                RelativeLayout inner = (RelativeLayout)rl.getChildAt(1);
                ImageView dot = (ImageView)inner.getChildAt(0);

                if(night){
                    v.setBackgroundResource(R.drawable.btn_lang_item_selector_n);
                    tv.setTextColor(ContextCompat.getColor(context, R.color.text_color_n));
                    inner.setBackgroundResource(R.drawable.lang_check_box_bg_n);
                    dot.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lang_check_box_dot_n));
                }else{
                    v.setBackgroundResource(R.drawable.btn_lang_item_selector);
                    tv.setTextColor(ContextCompat.getColor(context, R.color.app_bg));
                    inner.setBackgroundResource(R.drawable.lang_check_box_bg);
                    dot.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.lang_check_box_dot));
                }
            }
        }

        setSelectedLanguage();
    }



    @Override
    public void onClick(final View view) {
        SoundPlayer.playSound(context, SoundPlayer.CLICK);
        String code = Settings.getStringValue(context, context.getResources().getString(R.string.pref_key_language), Constants.DEFAULT_LANGUAGE);

        int resID = context.getResources().getIdentifier(code, "id", context.getPackageName());

        RelativeLayout rl = findViewById(resID);
        final View old = ((RelativeLayout)rl.getChildAt(1)).getChildAt(0);

        ScaleAnimation s = new ScaleAnimation(1f,0f,1f,0f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        s.setDuration(200);
        s.setInterpolator(new AccelerateInterpolator());
        s.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                old.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        old.startAnimation(s);

        RelativeLayout rl2 = (RelativeLayout)view;
        View circle = ((RelativeLayout)rl2.getChildAt(1)).getChildAt(0);

        ScaleAnimation sa = new ScaleAnimation(0f,1f,0f,1f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(200);
        sa.setInterpolator(new AccelerateInterpolator());
        sa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if(listener != null){
                    listener.selected(context.getResources().getResourceEntryName(view.getId()));
                }

                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        circle.setVisibility(View.VISIBLE);
        circle.startAnimation(sa);
    }



    public void setLangugeSelectionListener(LanguageSelectionListener listener){
        this.listener = listener;
    }



    private void setSelectedLanguage(){
        String code = Settings.getStringValue(context, context.getResources().getString(R.string.pref_key_language), Constants.DEFAULT_LANGUAGE);

        int resID = context.getResources().getIdentifier(code, "id", context.getPackageName());
        final RelativeLayout rl = findViewById(resID);
        ((RelativeLayout)rl.getChildAt(1)).getChildAt(0).setVisibility(View.VISIBLE);

        final ScrollView scroller = findViewById(R.id.scroller);

        scroller.postDelayed(new Runnable() {
            public void run() {
                scroller.smoothScrollTo(0, rl.getTop());
            }
        }, 500);

    }



    @Override
    public void onBackPressed() {
        dismiss();
    }



    public interface LanguageSelectionListener{
        void selected(String code);
    }
}
