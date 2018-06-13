package net.codecanyon.trimax.wordsearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.example.games.basegameutils.GameHelper;


import net.codecanyon.trimax.wordsearch.Config;
import net.codecanyon.trimax.wordsearch.R;

import net.codecanyon.trimax.wordsearch.game.ContextUtil;
import net.codecanyon.trimax.wordsearch.game.WSLayout;
import net.codecanyon.trimax.wordsearch.dialog.LanguageDialog;
import net.codecanyon.trimax.wordsearch.dialog.NumColsDialog;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;

import java.util.Locale;

import cn.refactor.library.SmoothCheckBox;



public class ConfigActivity extends Activity implements View.OnClickListener{

    private GameHelper gameHelper;


    private Resources resources;
    public static final String SOUND = "sound";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);

        TextView title = findViewById(R.id.title);
        TextView title_n = findViewById(R.id.title_n);
        TextView title_2 = findViewById(R.id.title_2);
        TextView title_2_n = findViewById(R.id.title_2_n);
        View language = findViewById(R.id.language);
        View sound = findViewById(R.id.sound);
        View mode = findViewById(R.id.mode);
        View grid = findViewById(R.id.grid);
        View marquee = findViewById(R.id.marquee);
        View letter_anim = findViewById(R.id.letter_anim);
        View num_cols = findViewById(R.id.num_cols);
        final View sign_out = findViewById(R.id.sign_out);


        View language_n = findViewById(R.id.language_n);
        View sound_n = findViewById(R.id.sound_n);
        View mode_n = findViewById(R.id.mode_n);
        View grid_n = findViewById(R.id.grid_n);
        View marquee_n = findViewById(R.id.marquee_n);
        View letter_anim_n = findViewById(R.id.letter_anim_n);
        View num_cols_n = findViewById(R.id.num_cols_n);
        final View sign_out_n = findViewById(R.id.sign_out_n);


        if(night) {
            findViewById(R.id.night).setVisibility(View.VISIBLE);
            title_n.setVisibility(View.VISIBLE);
            findViewById(R.id.separator_n).setVisibility(View.VISIBLE);
            findViewById(R.id.title_2_cont_n).setVisibility(View.VISIBLE);
            findViewById(R.id.back_n).setVisibility(View.VISIBLE);
            title_2_n.setVisibility(View.VISIBLE);

            language_n.setVisibility(View.VISIBLE);
            sound_n.setVisibility(View.VISIBLE);
            mode_n.setVisibility(View.VISIBLE);
            grid_n.setVisibility(View.VISIBLE);
            marquee_n.setVisibility(View.VISIBLE);
            letter_anim_n.setVisibility(View.VISIBLE);
            num_cols_n.setVisibility(View.VISIBLE);
            if(Config.enableLeaderboardsAndAchievements)sign_out_n.setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.day).setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            findViewById(R.id.separator).setVisibility(View.VISIBLE);
            findViewById(R.id.title_2_cont).setVisibility(View.VISIBLE);
            findViewById(R.id.back).setVisibility(View.VISIBLE);
            title_2.setVisibility(View.VISIBLE);

            language.setVisibility(View.VISIBLE);
            sound.setVisibility(View.VISIBLE);
            mode.setVisibility(View.VISIBLE);
            grid.setVisibility(View.VISIBLE);
            marquee.setVisibility(View.VISIBLE);
            letter_anim.setVisibility(View.VISIBLE);
            num_cols.setVisibility(View.VISIBLE);
            if(Config.enableLeaderboardsAndAchievements)sign_out.setVisibility(View.VISIBLE);

        }



        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.back_n).setOnClickListener(this);

        language.setOnClickListener(this);
        sound.setOnClickListener(this);
        mode.setOnClickListener(this);
        grid.setOnClickListener(this);
        marquee.setOnClickListener(this);
        letter_anim.setOnClickListener(this);
        num_cols.setOnClickListener(this);
        sign_out.setOnClickListener(this);

        language_n.setOnClickListener(this);
        sound_n.setOnClickListener(this);
        mode_n.setOnClickListener(this);
        grid_n.setOnClickListener(this);
        marquee_n.setOnClickListener(this);
        letter_anim_n.setOnClickListener(this);
        num_cols_n.setOnClickListener(this);
        sign_out_n.setOnClickListener(this);

        resources = ContextUtil.getCustomResources(this);

        if(Config.enableLeaderboardsAndAchievements) {
            gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
            gameHelper.enableDebugLog(false);
            GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
                @Override
                public void onSignInFailed() {
                    Toast.makeText(ConfigActivity.this, resources.getString(R.string.not_signed_in), Toast.LENGTH_LONG).show();
                    setSignOutCheckbox(false, false);
                    setSignLabel();

                    sign_out.setAlpha(1f);
                    sign_out_n.setAlpha(1f);
                    sign_out.setClickable(true);
                    sign_out_n.setClickable(true);
                }

                @Override
                public void onSignInSucceeded() {
                    setSignOutCheckbox(true, false);
                    setSignLabel();
                    sign_out.setAlpha(1f);
                    sign_out_n.setAlpha(1f);
                    sign_out.setClickable(true);
                    sign_out_n.setClickable(true);
                }
            };

            boolean declined = Settings.getBooleanValue(this, Constants.DECLINED_TO_SIGN_IN, false);

            if (!declined) {
                sign_out.setAlpha(0.3f);
                sign_out_n.setAlpha(0.3f);
                sign_out.setClickable(false);
                sign_out_n.setClickable(false);
            }

            gameHelper.setConnectOnStart(!declined);
            gameHelper.setup(gameHelperListener);
        }
    }




    @Override
    protected void onStop() {
        super.onStop();
        if(Config.enableLeaderboardsAndAchievements && gameHelper != null)
            gameHelper.onStop();
    }




    @Override
    protected void onStart() {
        super.onStart();

        if(Config.enableLeaderboardsAndAchievements && gameHelper != null){
            gameHelper.onStart(this);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setSoundCheckbox(Settings.getBooleanValue(ConfigActivity.this, SOUND, true), false);
                setModeCheckbox(Settings.getBooleanValue(ConfigActivity.this, Constants.NIGHT_MODE_ON, false), false);
                setGridCheckbox(Settings.getBooleanValue(ConfigActivity.this, Constants.GRID_ON, false), false);
                setMarqueeCheckbox(Settings.getBooleanValue(ConfigActivity.this, Constants.MARQUEE_ON, true), false);
                setLetterAnimCheckbox(Settings.getBooleanValue(ConfigActivity.this, Constants.LETTER_ANIMATION, true), false);
                setNumColsCount();
                setLanguageCode();

                if(Config.enableLeaderboardsAndAchievements)
                    setSignOutCheckbox(IntroActivity.isSignedIn(gameHelper, ConfigActivity.this), false);
            }
        }, 50);


    }




    @Override
    public void onClick(View view) {
        SoundPlayer.playSound(this, SoundPlayer.CLICK);


        switch(view.getId()){

            case R.id.language:
            case R.id.language_n:
                openLangDialog();
                break;
            case R.id.sound:
            case R.id.sound_n:
                toogleSound();
                break;
            case R.id.mode:
            case R.id.mode_n:
                toogleMode();
                break;
            case R.id.grid:
            case R.id.grid_n:
                toogleGrid();
                break;
            case R.id.marquee:
            case R.id.marquee_n:
                toogleMarquee();
                break;
            case R.id.back:
            case R.id.back_n:
                setResult(Activity.RESULT_OK, null);
                finish();
                overridePendingTransition(R.anim.slide_left_to_right_2, R.anim.slide_left_to_right_1);
                break;
            case R.id.letter_anim:
            case R.id.letter_anim_n:
                toogleLetterAnim();
                break;
            case R.id.num_cols:
            case R.id.num_cols_n:
                openNumColsDialog();
                break;
            case R.id.sign_out:
            case R.id.sign_out_n:

                if(!IntroActivity.isNetworkConnected(this)){
                    IntroActivity.showNoConnection(this);
                    return;
                }

                toggleSignOut();
                break;

        }
    }





    private void toogleMarquee(){
        boolean marqueeOn = !Settings.getBooleanValue(this, Constants.MARQUEE_ON, true);
        Settings.saveBooleanValue(this, Constants.MARQUEE_ON, marqueeOn);
        setMarqueeCheckbox(marqueeOn, true);
    }


    private void setSignOutCheckbox(boolean visible, boolean animate){
        SmoothCheckBox sign_out_dot = (SmoothCheckBox)findViewById(R.id.sign_out_dot);
        sign_out_dot.setChecked(visible, animate);
        SmoothCheckBox sign_out_dot_n = (SmoothCheckBox)findViewById(R.id.sign_out_dot_n);
        sign_out_dot_n.setChecked(visible, animate);
    }

    private void toggleSignOut(){
        boolean action = !IntroActivity.isSignedIn(gameHelper, this);

        if(!action) {
            Settings.saveBooleanValue(ConfigActivity.this, Constants.DECLINED_TO_SIGN_IN, true);
            gameHelper.signOut();
            setSignOutCheckbox(false, true);

        }else {
            Settings.saveBooleanValue(ConfigActivity.this, Constants.DECLINED_TO_SIGN_IN, false);
            gameHelper.beginUserInitiatedSignIn();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(gameHelper != null)gameHelper.onActivityResult(requestCode, resultCode, data);
    }


    private void setNumColsCount(){
        int count = Settings.getIntValue(this, Constants.NUM_COLS, 2);
        ((TextView)findViewById(R.id.num_cols_value)).setText(String.valueOf(count));
        ((TextView)findViewById(R.id.num_cols_value_n)).setText(String.valueOf(count));
    }


    private void setLanguageCode(){
        String langCode = Settings.getStringValue(this, getResources().getString(R.string.pref_key_language), null);
        ((TextView)findViewById(R.id.lang_value)).setText(langCode.toUpperCase(new Locale(langCode)));
        ((TextView)findViewById(R.id.lang_value_n)).setText(langCode.toUpperCase(new Locale(langCode)));
    }


    private void setMarqueeCheckbox(boolean visible, boolean animate){
        SmoothCheckBox marqueeDot = findViewById(R.id.marqueeDot);
        marqueeDot.setChecked(visible, animate);
        SmoothCheckBox marqueeDot_n = findViewById(R.id.marqueeDot_n);
        marqueeDot_n.setChecked(visible, animate);
    }


    private void toogleGrid(){
        boolean gridOn = !Settings.getBooleanValue(this, Constants.GRID_ON, false);
        Settings.saveBooleanValue(this, Constants.GRID_ON, gridOn);
        setGridCheckbox(gridOn, true);
    }

    private void setGridCheckbox(boolean visible, boolean animate){
        SmoothCheckBox gridDot = findViewById(R.id.gridDot);
        gridDot.setChecked(visible, animate);
        SmoothCheckBox gridDot_n = findViewById(R.id.gridDot_n);
        gridDot_n.setChecked(visible, animate);
    }


    private void toogleMode(){
        boolean nightOn = !Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);
        Settings.saveBooleanValue(this, Constants.NIGHT_MODE_ON, nightOn);
        setModeCheckbox(nightOn, true);
        crossfade();
    }


    private void setModeCheckbox(boolean night, boolean animate){
        SmoothCheckBox modeDot = findViewById(R.id.modeDot);
        modeDot.setChecked(night, animate);
        SmoothCheckBox modeDot_n = findViewById(R.id.modeDot_n);
        modeDot_n.setChecked(night, animate);
    }


    private void toogleSound(){
        boolean soundOn = !Settings.getBooleanValue(this, SOUND, true);
        Settings.saveBooleanValue(this, SOUND, soundOn);
        setSoundCheckbox(soundOn, true);
        SoundPlayer.volumeOn = soundOn;
    }

    private void setSoundCheckbox(boolean on, boolean animate){
        SmoothCheckBox soundDot = findViewById(R.id.soundDot);
        soundDot.setChecked(on, animate);
        SmoothCheckBox soundDot_n = findViewById(R.id.soundDot_n);
        soundDot_n.setChecked(on, animate);
    }


    private void toogleLetterAnim(){
        boolean showAnim = !Settings.getBooleanValue(this, Constants.LETTER_ANIMATION, true);
        Settings.saveBooleanValue(this, Constants.LETTER_ANIMATION, showAnim);
        setLetterAnimCheckbox(showAnim, true);
        WSLayout.enableLetterAnim = showAnim;
    }


    private void setLetterAnimCheckbox(boolean on, boolean animate){
        SmoothCheckBox letterAnimDot = findViewById(R.id.letterAnimDot);
        letterAnimDot.setChecked(on, animate);
        SmoothCheckBox letterAnimDot_n = findViewById(R.id.letterAnimDot_n);
        letterAnimDot_n.setChecked(on, animate);
    }


    private void crossfade(){

        boolean n = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);

        final View[] fadeIn, fadeOut;

        View[] a = new View[]{
                findViewById(R.id.night),
                findViewById(R.id.title_n),
                findViewById(R.id.separator_n),
                findViewById(R.id.title_2_cont_n),
                findViewById(R.id.back_n),
                findViewById(R.id.title_2_n),
                findViewById(R.id.language_n),
                findViewById(R.id.sound_n),
                findViewById(R.id.mode_n),
                findViewById(R.id.grid_n),
                findViewById(R.id.marquee_n),
                findViewById(R.id.letter_anim_n),
                findViewById(R.id.num_cols_n),
                Config.enableLeaderboardsAndAchievements ? findViewById(R.id.sign_out_n) : null
        };

        View[] b = new View[]{
                findViewById(R.id.day),
                findViewById(R.id.title),
                findViewById(R.id.separator),
                findViewById(R.id.title_2_cont),
                findViewById(R.id.back),
                findViewById(R.id.title_2),
                findViewById(R.id.language),
                findViewById(R.id.sound),
                findViewById(R.id.mode),
                findViewById(R.id.grid),
                findViewById(R.id.marquee),
                findViewById(R.id.letter_anim),
                findViewById(R.id.num_cols),
                Config.enableLeaderboardsAndAchievements ? findViewById(R.id.sign_out) : null
        };

        if(n){
            fadeIn = a;
            fadeOut = b;
        }else{
            fadeIn = b;
            fadeOut = a;
        }

        fadeOut(fadeOut);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeIn(fadeIn);
            }
        }, 300);
    }


    private void fadeIn(View... views){
        for(int i=0;i<views.length;i++){
            if(views[i] == null)continue;
            AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setDuration(600);
            fadeIn.setFillAfter(true);
            views[i].setClickable(true);
            views[i].setEnabled(true);
            views[i].setVisibility(View.VISIBLE);
            views[i].startAnimation(fadeIn);
        }
    }


    private void fadeOut(View... views){

        for(int i=0;i<views.length;i++){
            if(views[i] == null)continue;
            AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
            fadeOut.setDuration(600);
            fadeOut.setFillAfter(true);
            final View view = views[i];
            view.setVisibility(View.VISIBLE);
            view.startAnimation(fadeOut);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setClickable(false);
                    view.setEnabled(false);
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }


    private void openNumColsDialog(){
        final NumColsDialog ncd = new NumColsDialog(this, resources);
        ncd.setLangugeSelectionListener(new NumColsDialog.LanguageSelectionListener() {
            @Override
            public void selected(String code) {
                int count = Integer.valueOf(code);
                Settings.saveIntValue(ConfigActivity.this, Constants.NUM_COLS, count);
                setNumColsCount();
            }
        });
        ncd.show();
    }


    private void openLangDialog(){

        final LanguageDialog ld = new LanguageDialog(this, resources);
        ld.setLangugeSelectionListener(new LanguageDialog.LanguageSelectionListener() {
            @Override
            public void selected(final String code) {
                boolean night = Settings.getBooleanValue(ConfigActivity.this, Constants.NIGHT_MODE_ON, false);
                final RelativeLayout root = findViewById(R.id.root);
                LayoutInflater inflater = LayoutInflater.from(ConfigActivity.this);
                final View lang_confirm = inflater.inflate(R.layout.confirm_layout, null);

                root.addView(lang_confirm);
                TextView chg_lang_txt = lang_confirm.findViewById(R.id.chg_lang_txt);

                chg_lang_txt.setText(resources.getString(R.string.change_language));

                Button yes = lang_confirm.findViewById(R.id.yes);
                Button no = lang_confirm.findViewById(R.id.no);
                yes.setText(resources.getString(R.string.yes));
                no.setText(resources.getString(R.string.no));

                if(night){
                    lang_confirm.findViewById(R.id.confirm_bg).setBackgroundResource(R.drawable.dialog_bg_n);
                    chg_lang_txt.setTextColor(ContextCompat.getColor(ConfigActivity.this, R.color.home_title_n));
                    int btn_confirm_selector_n = R.drawable.btn_confirm_selector_n;
                    yes.setBackgroundResource(btn_confirm_selector_n);
                    int color = ContextCompat.getColor(ConfigActivity.this, R.color.confirm_btn_text);
                    yes.setTextColor(color);
                    no.setBackgroundResource(btn_confirm_selector_n);
                    no.setTextColor(color);
                }else{
                    lang_confirm.findViewById(R.id.confirm_bg).setBackgroundResource(R.drawable.toast);
                    int color = ContextCompat.getColor(ConfigActivity.this, R.color.app_bg);
                    chg_lang_txt.setTextColor(color);
                    int btn_confirm_selector = R.drawable.btn_confirm_selector;
                    yes.setBackgroundResource(btn_confirm_selector);
                    int btnColor = ContextCompat.getColor(ConfigActivity.this, R.color.secondary_color_darker);
                    yes.setTextColor(btnColor);
                    no.setBackgroundResource(btn_confirm_selector);
                    no.setTextColor(btnColor);
                }

                AlphaAnimation a = new AlphaAnimation(0f, 1f);
                a.setDuration(300);
                lang_confirm.startAnimation(a);

                findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SoundPlayer.playSound(ConfigActivity.this, SoundPlayer.CLICK);
                        Settings.saveStringValue(ConfigActivity.this, ConfigActivity.this.getResources().getString(R.string.pref_key_language), code);
                        setLanguageCode();
                        Intent intent = new Intent(ConfigActivity.this, IntroActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SoundPlayer.playSound(ConfigActivity.this, SoundPlayer.CLICK);
                        lang_confirm.clearAnimation();

                        AlphaAnimation a = new AlphaAnimation(1f, 0f);
                        a.setDuration(300);
                        a.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                lang_confirm.clearAnimation();
                                root.removeView(lang_confirm);

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}

                        });
                        lang_confirm.startAnimation(a);

                    }
                });
            }
        });

        ld.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setLabels();
    }


    private String getDefaultSelectedLanguageLabel(){
        String langCode = Settings.getStringValue(this, getResources().getString(R.string.pref_key_language), null);
        String name = "English";
        switch (langCode){
            case "en":
                name = "English";
                break;
            case "tr":
                name = "Türkçe";
                break;
            case "de":
                name = "Deutsch";
                break;
            case "es":
                name = "Español";
                break;
            case "pt":
                name = "Português";
                break;
            case "ja":
                name = "日本語";
                break;
            case "ko":
                name = "한국어";
                break;
            case "fr":
                name = "Français";
                break;
            case "it":
                name = "Italiano";
                break;
            case "nl":
                name = "Nederlands";
                break;
            case "cs":
                name = "Čeština";
                break;
            case "da":
                name = "Dansk";
                break;
            case "el":
                name = "Ελληνικά";
                break;
            case "fi":
                name = "Suomi";
                break;
            case "hu":
                name = "Magyar";
                break;
            case "no":
                name = "Norsk";
                break;
            case "pl":
                name = "Polski";
                break;
            case "ro":
                name = "Română";
                break;
            case "ru":
                name = "Pусский";
                break;
            case "sv":
                name = "Svenska";
                break;
            case "sw":
                name = "Swahili";
                break;
        }
        return name;
    }


    private void setSignLabel(){
        String sign_out_config_label;

        if(IntroActivity.isSignedIn(gameHelper, this)){
            sign_out_config_label = resources.getString(R.string.gpg_sign_out_label);
        }else{
            sign_out_config_label = resources.getString(R.string.gpg_sign_in_label);
        }

        ((TextView)findViewById(R.id.sign_out_txt)).setText(sign_out_config_label);
        ((TextView)findViewById(R.id.sign_out_txt_n)).setText(sign_out_config_label);
    }



    public void setLabels() {
        String app_name = resources.getString(R.string.app_name);
        String settings = resources.getString(R.string.settings);
        String pref_title_language = resources.getString(R.string.pref_title_language) + " ("+getDefaultSelectedLanguageLabel() +")";
        String sound = resources.getString(R.string.sound);
        String night_mode = resources.getString(R.string.night_mode);
        String grid = resources.getString(R.string.grid);
        String marquee = resources.getString(R.string.marquee);
        String letter_anim = resources.getString(R.string.letter_anim);
        String num_cols_label = resources.getString(R.string.num_cols_label);

        ((TextView)findViewById(R.id.title)).setText(app_name);
        ((TextView)findViewById(R.id.title_2)).setText(settings);
        ((TextView)findViewById(R.id.lang_title)).setText(pref_title_language);
        ((TextView)findViewById(R.id.sound_txt)).setText(sound);
        ((TextView)findViewById(R.id.mode_txt)).setText(night_mode);
        ((TextView)findViewById(R.id.grid_txt)).setText(grid);
        ((TextView)findViewById(R.id.marquee_txt)).setText(marquee);
        ((TextView)findViewById(R.id.letter_anim_txt)).setText(letter_anim);
        ((TextView)findViewById(R.id.num_cols_txt)).setText(num_cols_label);

        ((TextView)findViewById(R.id.title_n)).setText(app_name);
        ((TextView)findViewById(R.id.title_2_n)).setText(settings);
        ((TextView)findViewById(R.id.lang_title_n)).setText(pref_title_language);
        ((TextView)findViewById(R.id.sound_txt_n)).setText(sound);
        ((TextView)findViewById(R.id.mode_txt_n)).setText(night_mode);
        ((TextView)findViewById(R.id.grid_txt_n)).setText(grid);
        ((TextView)findViewById(R.id.marquee_txt_n)).setText(marquee);
        ((TextView)findViewById(R.id.letter_anim_txt_n)).setText(letter_anim);
        ((TextView)findViewById(R.id.num_cols_txt_n)).setText(num_cols_label);
        setSignLabel();
    }



    @Override
    public void onBackPressed() {
        final View confirm = findViewById(R.id.lang_confirm);
        if(confirm != null){
            confirm.clearAnimation();
            AlphaAnimation a = new AlphaAnimation(1f, 0f);
            a.setDuration(300);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((RelativeLayout)findViewById(R.id.root)).removeView(confirm);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            confirm.startAnimation(a);
        }else{
            setResult(Activity.RESULT_OK, null);
            finish();
            overridePendingTransition(R.anim.slide_left_to_right_2, R.anim.slide_left_to_right_1);
        }
    }

}
