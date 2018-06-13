package net.codecanyon.trimax.wordsearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;
import net.codecanyon.trimax.wordsearch.dialog.LanguageDialog;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.dialog.TimeLimitDialog;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;





public class DifficultyActivity extends Activity implements View.OnClickListener {

    public static final String SELECTED_DIFFICULTY = "selectedDifficulty";

    public static final int DIFFICULTY_EXTREMELY_EASY = 8;
    public static final int DIFFICULTY_VERY_EASY = 0;
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_DIFFICULT = 3;
    public static final int DIFFICULTY_VERY_DIFFICULT = 4;
    public static final int DIFFICULTY_EXCESSIVELY_DIFFICULT = 5;
    public static final int DIFFICULTY_EXTREMELY_DIFFICULT = 6;
    public static final int DIFFICULTY_SUPREMEMELY_DIFFICULT = 7;

    public static final int DIFFICULTY_EXTREMELY_EASY_SIZE = 5;
    public static final int DIFFICULTY_VERY_EASY_SIZE = 6;
    public static final int DIFFICULTY_EASY_SIZE = 8;
    public static final int DIFFICULTY_MEDIUM_SIZE = 10;
    public static final int DIFFICULTY_DIFFICULT_SIZE = 12;
    public static final int DIFFICULTY_VERY_DIFFICULT_SIZE = 14;
    public static final int DIFFICULTY_EXCESSIVELY_DIFFICULT_SIZE = 16;
    public static final int DIFFICULTY_EXTREMELY_DIFFICULT_SIZE = 18;
    public static final int DIFFICULTY_SUPREMELY_DIFFICULT_SIZE = 20;

    public static final int DIFFICULTY_DEFAULT = DIFFICULTY_MEDIUM;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_left_to_right_2, R.anim.slide_left_to_right_1);
            }
        });

        findViewById(R.id.extremely_easy).setOnClickListener(this);
        findViewById(R.id.very_easy).setOnClickListener(this);
        findViewById(R.id.easy).setOnClickListener(this);
        findViewById(R.id.medium).setOnClickListener(this);
        findViewById(R.id.difficult).setOnClickListener(this);
        findViewById(R.id.very_difficult).setOnClickListener(this);
        findViewById(R.id.pret_difficult).setOnClickListener(this);
        findViewById(R.id.ext_difficult).setOnClickListener(this);
        findViewById(R.id.sup_difficult).setOnClickListener(this);

    }




    @Override
    protected void onResume() {
        super.onResume();
        setLabels();
        toggleMode();
    }



    @Override
    public void onClick(View view) {
        int difficulty = DIFFICULTY_MEDIUM;
        SoundPlayer.playSound(this, SoundPlayer.CLICK);

        switch (view.getId()){
            case R.id.extremely_easy:
                difficulty = DIFFICULTY_EXTREMELY_EASY;
                break;
            case R.id.very_easy:
                difficulty = DIFFICULTY_VERY_EASY;
                break;
            case R.id.easy:
                difficulty = DIFFICULTY_EASY;
                break;
            case R.id.medium:
                difficulty = DIFFICULTY_MEDIUM;
                break;
            case R.id.difficult:
                difficulty = DIFFICULTY_DIFFICULT;
                break;
            case R.id.very_difficult:
                difficulty = DIFFICULTY_VERY_DIFFICULT;
                break;
            case R.id.pret_difficult:
                difficulty = DIFFICULTY_EXCESSIVELY_DIFFICULT;
                break;
            case R.id.ext_difficult:
                difficulty = DIFFICULTY_EXTREMELY_DIFFICULT;
                break;
            case R.id.sup_difficult:
                difficulty = DIFFICULTY_SUPREMEMELY_DIFFICULT;
                break;

        }

        Settings.saveIntValue(this, SELECTED_DIFFICULTY, difficulty);
        showTimeDialog();
    }



    private void showTimeDialog(){
        final TimeLimitDialog tld = new TimeLimitDialog(this);
        tld.setLangugeSelectionListener(new LanguageDialog.LanguageSelectionListener(){

            @Override
            public void selected(String code) {
                Settings.saveStringValue(DifficultyActivity.this, Constants.TIME_MODE, code);
                tld.dismiss();
                Intent intent = new Intent(DifficultyActivity.this, GameActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();

            }
        });
        if(!isFinishing())tld.show();
    }


    public void setLabels() {
        Resources resources = ContextUtil.getCustomResources(this);
        ((TextView)findViewById(R.id.title)).setText(resources.getString(R.string.app_name));
        ((TextView)findViewById(R.id.title_2)).setText(resources.getString(R.string.difficulty));
        ((TextView)findViewById(R.id.extremely_easy_txt)).setText(resources.getString(R.string.extremely_easy));
        ((TextView)findViewById(R.id.very_easy_txt)).setText(resources.getString(R.string.very_easy));
        ((TextView)findViewById(R.id.easy_txt)).setText(resources.getString(R.string.easy));
        ((TextView)findViewById(R.id.medium_txt)).setText(resources.getString(R.string.medium));
        ((TextView)findViewById(R.id.difficult_txt)).setText(resources.getString(R.string.difficult));
        ((TextView)findViewById(R.id.very_difficult_txt)).setText(resources.getString(R.string.very_difficult));
        ((TextView)findViewById(R.id.pret_difficult_txt)).setText(resources.getString(R.string.pret_difficult));
        ((TextView)findViewById(R.id.ext_difficult_txt)).setText(resources.getString(R.string.ext_difficult));
        ((TextView)findViewById(R.id.sup_difficult_txt)).setText(resources.getString(R.string.sup_difficult));
    }



    public void toggleMode() {
        boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);
        View window = this.getWindow().getDecorView();
        TextView title = findViewById(R.id.title);
        TextView title_2 = findViewById(R.id.title_2);
        View separator = findViewById(R.id.separator);
        ImageButton back = findViewById(R.id.back);
        View title_2_container = findViewById(R.id.title_2_container);
        View extremely_easy = findViewById(R.id.extremely_easy);
        View very_easy = findViewById(R.id.very_easy);
        View easy = findViewById(R.id.easy);
        View medium = findViewById(R.id.medium);
        View difficult = findViewById(R.id.difficult);
        View very_difficult = findViewById(R.id.very_difficult);
        View pret_difficult = findViewById(R.id.pret_difficult);
        View ext_difficult = findViewById(R.id.ext_difficult);
        View sup_difficult = findViewById(R.id.sup_difficult);

        TextView g0 = findViewById(R.id.g0);
        TextView g1 = findViewById(R.id.g1);
        TextView g2 = findViewById(R.id.g2);
        TextView g3 = findViewById(R.id.g3);
        TextView g4 = findViewById(R.id.g4);
        TextView g5 = findViewById(R.id.g5);
        TextView g6 = findViewById(R.id.g6);
        TextView g7 = findViewById(R.id.g7);
        TextView g8 = findViewById(R.id.g8);

        TextView extremely_ease_txt = findViewById(R.id.extremely_easy_txt);
        TextView very_easy_txt = findViewById(R.id.very_easy_txt);
        TextView easy_txt = findViewById(R.id.easy_txt);
        TextView medium_txt = findViewById(R.id.medium_txt);
        TextView difficult_txt = findViewById(R.id.difficult_txt);
        TextView very_difficult_txt = findViewById(R.id.very_difficult_txt);
        TextView pret_difficult_txt = findViewById(R.id.pret_difficult_txt);
        TextView ext_difficult_txt = findViewById(R.id.ext_difficult_txt);
        TextView sup_difficult_txt = findViewById(R.id.sup_difficult_txt);

        int text_color_n = ContextCompat.getColor(this, R.color.text_color_n);

        if(night) {
            window.setBackgroundColor(ContextCompat.getColor(this, R.color.app_bg_n));
            title.setTextColor(text_color_n);
            title_2.setTextColor(text_color_n);
            separator.setBackgroundColor(ContextCompat.getColor(this, R.color.stripe_n));
            back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_left_n));
            title_2_container.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_color_n));
            int btn_difficulty_selector_n = R.drawable.btn_difficulty_selector_n;
            very_easy.setBackgroundResource(btn_difficulty_selector_n);
            extremely_easy.setBackgroundResource(btn_difficulty_selector_n);
            easy.setBackgroundResource(btn_difficulty_selector_n);
            medium.setBackgroundResource(btn_difficulty_selector_n);
            difficult.setBackgroundResource(btn_difficulty_selector_n);
            very_difficult.setBackgroundResource(btn_difficulty_selector_n);
            pret_difficult.setBackgroundResource(btn_difficulty_selector_n);
            ext_difficult.setBackgroundResource(btn_difficulty_selector_n);
            sup_difficult.setBackgroundResource(btn_difficulty_selector_n);

            int diff_icon_bg_n = R.drawable.diff_icon_bg_n;
            g0.setBackgroundResource(diff_icon_bg_n);
            g1.setBackgroundResource(diff_icon_bg_n);
            g2.setBackgroundResource(diff_icon_bg_n);
            g3.setBackgroundResource(diff_icon_bg_n);
            g4.setBackgroundResource(diff_icon_bg_n);
            g5.setBackgroundResource(diff_icon_bg_n);
            g6.setBackgroundResource(diff_icon_bg_n);
            g7.setBackgroundResource(diff_icon_bg_n);
            g8.setBackgroundResource(diff_icon_bg_n);

            int diff_btn_text_n = ContextCompat.getColor(this, R.color.diff_btn_text_n);
            g0.setTextColor(diff_btn_text_n);
            g1.setTextColor(diff_btn_text_n);
            g2.setTextColor(diff_btn_text_n);
            g3.setTextColor(diff_btn_text_n);
            g4.setTextColor(diff_btn_text_n);
            g5.setTextColor(diff_btn_text_n);
            g6.setTextColor(diff_btn_text_n);
            g7.setTextColor(diff_btn_text_n);
            g8.setTextColor(diff_btn_text_n);

            extremely_ease_txt.setTextColor(diff_btn_text_n);
            very_easy_txt.setTextColor(diff_btn_text_n);
            easy_txt.setTextColor(diff_btn_text_n);
            medium_txt.setTextColor(diff_btn_text_n);
            difficult_txt.setTextColor(diff_btn_text_n);
            very_difficult_txt.setTextColor(diff_btn_text_n);
            pret_difficult_txt.setTextColor(diff_btn_text_n);
            ext_difficult_txt.setTextColor(diff_btn_text_n);
            sup_difficult_txt.setTextColor(diff_btn_text_n);
        }else{
            int app_bg = ContextCompat.getColor(this, R.color.app_bg);
            window.setBackgroundColor(app_bg);

            int white = ContextCompat.getColor(this, R.color.stripe);
            title.setTextColor(ContextCompat.getColor(this, R.color.diff_title));
            title_2.setTextColor(white);
            separator.setBackgroundColor(white);

            back.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.arrow_left));

            title_2_container.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_color));

            int btn_difficulty_selector = R.drawable.btn_difficulty_selector;
            extremely_easy.setBackgroundResource(btn_difficulty_selector);
            very_easy.setBackgroundResource(btn_difficulty_selector);
            easy.setBackgroundResource(btn_difficulty_selector);
            medium.setBackgroundResource(btn_difficulty_selector);
            difficult.setBackgroundResource(btn_difficulty_selector);
            very_difficult.setBackgroundResource(btn_difficulty_selector);
            pret_difficult.setBackgroundResource(btn_difficulty_selector);
            ext_difficult.setBackgroundResource(btn_difficulty_selector);
            sup_difficult.setBackgroundResource(btn_difficulty_selector);

            int diff_icon_bg = R.drawable.diff_icon_bg;
            g0.setBackgroundResource(diff_icon_bg);
            g1.setBackgroundResource(diff_icon_bg);
            g2.setBackgroundResource(diff_icon_bg);
            g3.setBackgroundResource(diff_icon_bg);
            g4.setBackgroundResource(diff_icon_bg);
            g5.setBackgroundResource(diff_icon_bg);
            g6.setBackgroundResource(diff_icon_bg);
            g7.setBackgroundResource(diff_icon_bg);
            g8.setBackgroundResource(diff_icon_bg);

            g0.setTextColor(app_bg);
            g1.setTextColor(app_bg);
            g2.setTextColor(app_bg);
            g3.setTextColor(app_bg);
            g4.setTextColor(app_bg);
            g5.setTextColor(app_bg);
            g6.setTextColor(app_bg);
            g7.setTextColor(app_bg);
            g8.setTextColor(app_bg);

            int diff_btn_text = ContextCompat.getColor(this, R.color.pause_btn_color);
            extremely_ease_txt.setTextColor(diff_btn_text);
            very_easy_txt.setTextColor(diff_btn_text);
            easy_txt.setTextColor(diff_btn_text);
            medium_txt.setTextColor(diff_btn_text);
            difficult_txt.setTextColor(diff_btn_text);
            very_difficult_txt.setTextColor(diff_btn_text);
            pret_difficult_txt.setTextColor(diff_btn_text);
            ext_difficult_txt.setTextColor(diff_btn_text);
            sup_difficult_txt.setTextColor(diff_btn_text);
        }
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_left_to_right_2, R.anim.slide_left_to_right_1);
    }
}
