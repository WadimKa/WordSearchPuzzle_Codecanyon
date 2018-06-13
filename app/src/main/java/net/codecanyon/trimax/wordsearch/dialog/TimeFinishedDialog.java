package net.codecanyon.trimax.wordsearch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;



public class TimeFinishedDialog extends Dialog implements View.OnClickListener {


    private Context context;
    private LanguageDialog.LanguageSelectionListener listener;
    private boolean videoLoaded;


    public TimeFinishedDialog(Context context, boolean videoLoaded) {
        super(context);
        this.context = context;
        this.videoLoaded = videoLoaded;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.time_finished_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.TimeLimitDialogAnimation;

        Resources resources = ContextUtil.getCustomResources(context);

        final TextView title = findViewById(R.id.lang_title);
        title.setText(resources.getString(R.string.time_is_up));



        View center = findViewById(R.id.center);

        TextView play_video_label = findViewById(R.id.play_video_label);
        TextView play_video_label_n = findViewById(R.id.play_video_label_n);

        View play_video = findViewById(R.id.play_video);
        View play_video_n = findViewById(R.id.play_video_n);


        if(!videoLoaded) {
            play_video.setAlpha(0.3f);
            play_video.setEnabled(false);
            play_video.setClickable(false);

            play_video_n.setAlpha(0.3f);
            play_video_n.setEnabled(false);
            play_video_n.setClickable(false);
        }

        play_video_label.setText(resources.getString(R.string.continue_playing));


        play_video_label_n.setText(resources.getString(R.string.continue_playing));
        TextView retry_label = findViewById(R.id.retry_label);

        retry_label.setText(resources.getString(R.string.retry));

        TextView home_label = findViewById(R.id.home_label);
        home_label.setText(resources.getString(R.string.home));

        TextView home_label_n = findViewById(R.id.home_label_n);
        home_label_n.setText(resources.getString(R.string.home));

        TextView retry_label_n = findViewById(R.id.retry_label_n);
        retry_label_n.setText(resources.getString(R.string.retry));

        View retry = findViewById(R.id.retry);
        View retry_n = findViewById(R.id.retry_n);

        View home = findViewById(R.id.home);
        View home_n = findViewById(R.id.home_n);

        ImageView hourglass = findViewById(R.id.hourglass);

        boolean night = Settings.getBooleanValue(context, Constants.NIGHT_MODE_ON, false);

        if(night){
            findViewById(R.id.dialog_inner).setBackgroundResource(R.drawable.dialog_bg_n);
            center.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.diff_btn_up_n));
            int textColor = ContextCompat.getColor(context, R.color.text_color_n);
            title.setTextColor(textColor);
            play_video_n.setVisibility(View.VISIBLE);
            retry_n.setVisibility(View.VISIBLE);
            home_n.setVisibility(View.VISIBLE);
            hourglass.setImageResource(R.drawable.hourglass_n);
            int color = ContextCompat.getColor(context, R.color.text_color_n);
            play_video_label_n.setTextColor(color);
            retry_label_n.setTextColor(color);
            home_label_n.setTextColor(color);
        }else{
            findViewById(R.id.dialog_inner).setBackgroundResource(R.drawable.dialog_bg);
            center.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondary_color_dark));
            int textColor = ContextCompat.getColor(context, R.color.button_text);
            title.setTextColor(textColor);
            play_video.setVisibility(View.VISIBLE);
            retry.setVisibility(View.VISIBLE);
            home.setVisibility(View.VISIBLE);
            hourglass.setImageResource(R.drawable.hourglass);
            int color = ContextCompat.getColor(context, R.color.app_bg);
            play_video_label.setTextColor(color);
            retry_label.setTextColor(color);
            home_label.setTextColor(color);
        }

        play_video.setOnClickListener(this);
        retry.setOnClickListener(this);
        play_video_n.setOnClickListener(this);
        retry_n.setOnClickListener(this);
        home_n.setOnClickListener(this);
        home.setOnClickListener(this);

        setCancelable(false);
    }



    public void enableWatchButton(){
        View play_video = findViewById(R.id.play_video);
        View play_video_n = findViewById(R.id.play_video_n);

        play_video.setAlpha(1f);
        play_video.setEnabled(true);
        play_video.setClickable(true);

        play_video_n.setAlpha(1f);
        play_video_n.setEnabled(true);
        play_video_n.setClickable(true);
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
        listener.selected("finish");
    }


}
