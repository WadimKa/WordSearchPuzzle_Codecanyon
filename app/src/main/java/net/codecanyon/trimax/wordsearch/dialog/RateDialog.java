package net.codecanyon.trimax.wordsearch.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;

import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;




public class RateDialog extends Dialog implements View.OnClickListener {


    private Activity context;

    public RateDialog(Activity context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rate_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.TimeLimitDialogAnimation;

        Resources resources = ContextUtil.getCustomResources(getContext());

        final TextView title = findViewById(R.id.lang_title);
        title.setText(resources.getString(R.string.rta_dialog_title));

        final TextView text = findViewById(R.id.text);
        text.setText(resources.getString(R.string.rta_dialog_message));

        Button no_thanks = findViewById(R.id.no_thanks);
        no_thanks.setText(resources.getString(R.string.rta_dialog_no));
        no_thanks.setOnClickListener(this);

        Button no_thanks_n = findViewById(R.id.no_thanks_n);
        no_thanks_n.setText(resources.getString(R.string.rta_dialog_no));
        no_thanks_n.setOnClickListener(this);

        Button rate = findViewById(R.id.rate);
        rate.setText(resources.getString(R.string.rta_dialog_ok));
        rate.setOnClickListener(this);

        Button rate_n = findViewById(R.id.rate_n);
        rate_n.setText(resources.getString(R.string.rta_dialog_ok));
        rate_n.setOnClickListener(this);


        Button later = findViewById(R.id.later);
        later.setText(resources.getString(R.string.rta_dialog_cancel));
        later.setOnClickListener(this);

        Button later_n = findViewById(R.id.later_n);
        later_n.setText(resources.getString(R.string.rta_dialog_cancel));
        later_n.setOnClickListener(this);

        boolean night = Settings.getBooleanValue(context, Constants.NIGHT_MODE_ON, false);

        if(night){
            findViewById(R.id.dialog_inner).setBackgroundResource(R.drawable.dialog_bg_n);
            int textColor = ContextCompat.getColor(context, R.color.text_color_n);
            title.setTextColor(textColor);
            text.setTextColor(textColor);
            no_thanks_n.setVisibility(View.VISIBLE);
            rate_n.setVisibility(View.VISIBLE);
            later_n.setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.dialog_inner).setBackgroundResource(R.drawable.dialog_bg);
            int textColor = ContextCompat.getColor(context, R.color.button_text);
            title.setTextColor(textColor);
            text.setTextColor(textColor);
            no_thanks.setVisibility(View.VISIBLE);
            rate.setVisibility(View.VISIBLE);
            later.setVisibility(View.VISIBLE);
        }
        setCancelable(false);
    }


    @Override
    public void onClick(final View view) {
        SoundPlayer.playSound(context, SoundPlayer.CLICK);

        switch (view.getId()){
            case R.id.no_thanks:
            case R.id.no_thanks_n:
                Settings.saveBooleanValue(getContext(), Constants.DONT_SHOW_RATE_DIALOG_AGAIN, true);
                dismiss();
                break;
            case R.id.later:
            case R.id.later_n:
                dismiss();
                break;
            case R.id.rate:
            case R.id.rate_n:
                final String appPackageName = context.getPackageName();

                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                Settings.saveBooleanValue(getContext(), Constants.DONT_SHOW_RATE_DIALOG_AGAIN, true);
                dismiss();
        }
    }


    @Override
    public void onBackPressed() {
        dismiss();
    }


}
