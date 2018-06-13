package net.codecanyon.trimax.wordsearch.activity;


import net.codecanyon.trimax.wordsearch.Config;
import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;
import net.codecanyon.trimax.wordsearch.game.Direction;
import net.codecanyon.trimax.wordsearch.game.WSLayout;
import net.codecanyon.trimax.wordsearch.game.Word;
import net.codecanyon.trimax.wordsearch.game.WordTableAdapter;
import net.codecanyon.trimax.wordsearch.dialog.HelpDialog;
import net.codecanyon.trimax.wordsearch.dialog.LanguageDialog;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.dialog.TimeFinishedDialog;
import net.codecanyon.trimax.wordsearch.dialog.TimeLimitDialog;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;
import net.codecanyon.trimax.wordsearch.data.WSDatabase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static net.codecanyon.trimax.wordsearch.game.ContextUtil.buttonDown;
import static net.codecanyon.trimax.wordsearch.game.ContextUtil.buttonUp;


public class GameActivity extends Activity implements
        WSLayout.OnWordHighlightedListener,
        View.OnTouchListener,
        View.OnClickListener,
        RewardedVideoAdListener{



    private RewardedVideoAd googleRewardedAd;
    private InterstitialAd googleInterstitialAd;
    private AdView mAdView;

    private boolean userRewarded;
    private boolean giveExtraHints;
    private boolean timerMode;
    private Dialog rewardDialog;

    private int cols;
    private String[] wordList;
    public WordTableAdapter wordListAdapter;
    private WSLayout grid;

    private char[][] board;
    private List<Word> solution = new ArrayList<>();
    private final Direction[] directions = Direction.values();
    private boolean[][] lock;
    private int[] randIndices;
    private int foundCount;
    public GridView grd_word_list;

    private TimeFinishedDialog timeFinishDialog;
    private AVLoadingIndicatorView avi;
    private TextView preview;
    private int givenHintCount;

    public static final String HINTS_LEFT = "hintsLeft";

    private Chronometer chrono;
    private long timeWhenStopped = 0;
    private CountDownTimer timer;
    private long timerTime;
    private boolean backFromSettings;
    private Intent intent;
    private boolean giveExtraTime;
    private boolean fade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


		/*
		//Uncomment the following to skip the game and open result activity
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra(ResultActivity.TIME, "15:30");
		intent.putExtra(ResultActivity.HINTS_USED, 5);
		intent.putExtra(ResultActivity.WORD_COUNT, 30);
		intent.putExtra(ResultActivity.ACHIEVEMENT, R.string.achievement_solve_a_very_easy_puzzle);
        intent.putExtra(ResultActivity.LEADERBOARDS, R.string.leaderboard_number_of_revealed_words_6x6);
		startActivityForResult(intent, 1);

		finish();
		if(true)return;*/

        setContentView(R.layout.activity_game);
        boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);
        avi = findViewById(R.id.avi);
        avi.setIndicatorColor(ContextCompat.getColor(this, night ? R.color.text_color_n : R.color.secondary_color_dark));

        View window = this.getWindow().getDecorView();
        window.setBackgroundColor(ContextCompat.getColor(this, night ? R.color.app_bg_n : R.color.app_bg));

        grid = findViewById(R.id.game_board);
        cols = grid.cols;
        chrono = findViewById(R.id.chrono);
        String timeMode = Settings.getStringValue(this, Constants.TIME_MODE, TimeLimitDialog.DEFAULT_TIME_LIMIT);
        timerMode = !timeMode.equals(TimeLimitDialog.DEFAULT_TIME_LIMIT);
        if(timerMode)timerTime = getCountDownOffset();

        startNewGame();
    }



    private void postCreate(){
        grid.setOnWordHighlightedListener(GameActivity.this);
        setPreview();
        View hint_container = findViewById(R.id.hint_container);
        hint_container.setOnTouchListener(this);
        hint_container.setOnClickListener(this);

        View settings_btn = findViewById(R.id.settings_btn);
        settings_btn.setOnTouchListener(this);
        settings_btn.setOnClickListener(this);

        View pause_btn = findViewById(R.id.pause_btn);
        pause_btn.setOnTouchListener(this);
        pause_btn.setOnClickListener(this);

        View help_btn = findViewById(R.id.help_btn);
        help_btn.setOnTouchListener(this);
        help_btn.setOnClickListener(this);

        if(!timerMode) {
            chrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    CharSequence text = chronometer.getText();
                    if (text.length() == 4) {
                        chrono.setText("0" + text);
                        chrono.invalidate();
                    }
                }
            });
        }

        googleRewardedAd = MobileAds.getRewardedVideoAdInstance(this);
        googleRewardedAd.setRewardedVideoAdListener(this);

        if(Config.enableInterstitialAd) {
            googleInterstitialAd = new InterstitialAd(this);
            googleInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));
            googleInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    Log.d("wordsearch", "interstitial closed");
                    startActivityForResult(intent, 1);
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onAdOpened() {}

                @Override
                public void onAdFailedToLoad(int i) {
                    Log.d("wordsearch", "Interstitial failed to load");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        requestNewInterstitial();
                                    }
                                });
                            } catch (InterruptedException e) {
                            }
                        }
                    }).start();
                }
            });
            requestNewInterstitial();
        }

        loadRewardedVideoAd();
        grid.setEnabled(true);
        findViewById(R.id.bottom_panel).setEnabled(true);
        findViewById(R.id.toolbar).setEnabled(true);
    }



    private void countDown(long millis){
        if(millis == 0)return;

        timer = new CountDownTimer(millis, 1000) {

            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished < 1000 * 15){
                    int red = ContextCompat.getColor(GameActivity.this, R.color.red);
                    if(chrono.getCurrentTextColor() != red){
                        chrono.setTextColor(red);
                        SoundPlayer.playSound(GameActivity.this, SoundPlayer.TICTAC);
                    }

                    if(millisUntilFinished > 1000) {
                        if (fade) {
                            chrono.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    chrono.setVisibility(View.INVISIBLE);
                                }
                            }, 500);
                        } else {
                            chrono.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    chrono.setVisibility(View.INVISIBLE);
                                }
                            }, 500);
                        }
                    }else{
                        chrono.setVisibility(View.VISIBLE);
                    }

                    fade = !fade;
                }

                chrono.setText(""+String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))

                );
                timerTime = millisUntilFinished;

            }

            public void onFinish() {
                showTimeFinishDialog();
            }
        };
        timer.start();
    }




    private void showTimeFinishDialog(){
        chrono.setText("00:00");
        chrono.setVisibility(View.VISIBLE);
        timerTime = 0;
        SoundPlayer.playSound(GameActivity.this, SoundPlayer.TIME_IS_UP);

        timeFinishDialog = new TimeFinishedDialog(GameActivity.this, isRewardedVideoLoaded());
        timeFinishDialog.setLangugeSelectionListener(new LanguageDialog.LanguageSelectionListener() {
            @Override
            public void selected(String code) {
                boolean night = Settings.getBooleanValue(GameActivity.this, Constants.NIGHT_MODE_ON, false);
                chrono.setTextColor(ContextCompat.getColor(GameActivity.this, night ? R.color.text_color_n : R.color.secondary_color_dark));
                timeFinishDialog.dismiss();
                timeFinishDialog = null;

                if(code.equals("retry") || code.equals("retry_n")){
                    startNewGame();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(timer != null)timer.cancel();
                            countDown(timerTime);
                        }
                    }, 1200);
                }else if(code.equals("play_video") || code.equals("play_video_n")){
                    giveExtraTime = true;
                    googleRewardedAd.show();
                }else if(code.equals("home") || code.equals("home_n") || code.equals("finish")){
                    finish();
                }
            }
        });
        if(!isFinishing())timeFinishDialog.show();
    }



    private long getCountDownOffset(){
        switch (cols){
            case DifficultyActivity.DIFFICULTY_EXTREMELY_EASY_SIZE:
                return 1000 * 20;
            case DifficultyActivity.DIFFICULTY_VERY_EASY_SIZE:
                return 1000 * 30;
            case DifficultyActivity.DIFFICULTY_EASY_SIZE:
                return 1000 * 60;
            case DifficultyActivity.DIFFICULTY_MEDIUM_SIZE:
                return 1000 * 60 * 3;
            case DifficultyActivity.DIFFICULTY_DIFFICULT_SIZE:
                return 1000 * 60 * 5;
            case DifficultyActivity.DIFFICULTY_VERY_DIFFICULT_SIZE:
                return 1000 * 60 * 10;
            case DifficultyActivity.DIFFICULTY_EXCESSIVELY_DIFFICULT_SIZE:
                return 1000 * 60 * 15;
            case DifficultyActivity.DIFFICULTY_EXTREMELY_DIFFICULT_SIZE:
                return 1000 * 60 * 20;
            case DifficultyActivity.DIFFICULTY_SUPREMELY_DIFFICULT_SIZE:
                return 1000 * 60 * 30;
            default:
                return 1000 * 60;

        }
    }


    private boolean isInterstitialAdLoaded(){
        if(googleInterstitialAd != null)
            return googleInterstitialAd.isLoaded();

        return false;
    }


    private boolean isRewardedVideoLoaded(){
        if(googleRewardedAd != null)
            return googleRewardedAd.isLoaded();
        else
            return false;
    }



    @Override
    public void onClick(View view) {
        SoundPlayer.playSound(GameActivity.this, SoundPlayer.CLICK);

        switch(view.getId()){
            case R.id.play:
            case R.id.play_n:{
                if (isRewardedVideoLoaded()) {
                    giveExtraHints = true;
                    googleRewardedAd.show();
                    if(rewardDialog != null)rewardDialog.dismiss();
                }else{
                    setBulbIcon();
                }

                break;
            }
            case R.id.pause_btn:{
                Intent intent = new Intent(GameActivity.this, PauseActivity.class);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.slide_right_to_left_1, R.anim.slide_right_to_left_2);
                break;
            }
            case R.id.settings_btn:{
                Intent intent = new Intent(GameActivity.this, ConfigActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(R.anim.slide_right_to_left_1, R.anim.slide_right_to_left_2);
                break;
            }
            case R.id.hint_container:{
                giveHint();
                break;
            }
            case R.id.help_btn:{
                HelpDialog helpDialog = new HelpDialog(this);
                if(!isFinishing())helpDialog.show();
                break;
            }
        }
    }

    private void startNewGame() {

        avi.show();

        final View grid_container = findViewById(R.id.grid_container);
        grid_container.animate().alpha(0f).start();

        grid.setVisibility(View.INVISIBLE);
        grid.setEnabled(false);

        final View toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.INVISIBLE);
        toolbar.setEnabled(false);

        final RelativeLayout bottom_panel = (RelativeLayout)findViewById(R.id.bottom_panel);
        bottom_panel.setVisibility(View.INVISIBLE);
        bottom_panel.setEnabled(false);
        timeWhenStopped = 0;
        timerTime = getCountDownOffset();

        if(givenHintCount > 0) {
            for (Word word : solution) {
                if (word.isJumping()) {
                    word.setJumping(false);
                    word.firstLetter.clearAnimation();
                }
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                givenHintCount = 0;
                foundCount = 0;
                cols = grid.cols;
                //rows = grid.rows;
                board = new char[cols][cols];
                lock = new boolean[cols][cols];
                clearBoard();
                randomizeWords();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        grid.populateBoard(board);
                        avi.hide();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                printWordsLeft();


                                int hintsLeft = Settings.getIntValue(GameActivity.this, HINTS_LEFT, Config.defaultHintCount);
                                ((TextView) findViewById(R.id.hints_left)).setText(Integer.toString(hintsLeft));

                                if(grd_word_list == null) {
                                    grd_word_list = findViewById(R.id.grd_word_list);
                                }

                                if (wordListAdapter == null) {
                                    wordListAdapter = new WordTableAdapter(GameActivity.this, solution);
                                    grd_word_list.setAdapter(wordListAdapter);
                                }else {
                                    wordListAdapter.setWords(solution);
                                    wordListAdapter.notifyDataSetChanged();
                                }

                                float wordSize = findWordTextSize();
                                wordListAdapter.textSize = wordSize;

                                grd_word_list.setNumColumns(Settings.getIntValue(GameActivity.this, Constants.NUM_COLS, 2));
                                grd_word_list.setVerticalSpacing(getGridVerticalSpacing((int)wordSize));

                                grid.gridAppearAnimCallBack = gridAppearAnimCallBack;
                                grid.setVisibility(View.VISIBLE);

                                grid_container.animate().setDuration(200).alpha(1f).start();

                                grid.appearAnim();
                                topBottomAnim();
                            }
                        }, 30);

                    }
                });
            }
        }).start();
    }



    private int getGridVerticalSpacing(int wordSize){
        int colCount = Settings.getIntValue(GameActivity.this, Constants.NUM_COLS, 2);

        switch(colCount) {
            case 1:
            case 2:
                return 1;
            case 3:
            case 4:
                return wordSize + 1;
        }

        return 2;
    }


    private void topBottomAnim(){

        final Animation topAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_panel_slide_down);
        final Animation bottomAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_panel_slide_up);

        View toolbar = findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.startAnimation(topAnim);

        View bottom_panel = findViewById(R.id.bottom_panel);
        bottom_panel.setVisibility(View.VISIBLE);
        bottom_panel.startAnimation(bottomAnim);
    }


    private WSLayout.GridAppearAnimCallBack gridAppearAnimCallBack = new WSLayout.GridAppearAnimCallBack() {
        @Override
        public void animComplete() {
            postCreate();
        }
    };



    private void selectWordsFromDB(){
        WSDatabase wsd = new WSDatabase(GameActivity.this);
        wsd.open();
        wordList = wsd.getRandomWords();
        wsd.close();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == PauseActivity.GO_HOME) {
                finish();
            }else if(resultCode == PauseActivity.RESTART_GAME) {
                startNewGame();
            }
        }

        if(requestCode == 100){
            backFromSettings = true;
        }
    }

    private int getHintedAndFoundCount(){
        int count = 0;

        if(solution != null) {
            for (int i = 0; i < solution.size(); i++) {
                Word word = solution.get(i);
                if (word != null && word.isSolved() || word.isRevealed()) {
                    count++;
                }
            }
        }
        return count;
    }


    private void giveHint(){

        int hintedAndFoundCount = getHintedAndFoundCount();

        if(hintedAndFoundCount == solution.size())
            return;

        int hintsLeft = Settings.getIntValue(this, HINTS_LEFT, Config.defaultHintCount);
        if(hintsLeft == 0){
            SoundPlayer.playSound(this, SoundPlayer.HINT_FINISHED);
            if(isRewardedVideoLoaded())
                showOfferDialog();
            else
                setBulbIcon();

            return;
        }
        SoundPlayer.playSound(this, SoundPlayer.HINT);
        hintsLeft--;
        givenHintCount++;
        Settings.saveIntValue(this, HINTS_LEFT, hintsLeft);
        ((TextView)findViewById(R.id.hints_left)).setText(Integer.toString(hintsLeft));

        for(int i=0;i<solution.size();i++){
            Word word = solution.get(i);
            if(word != null && !word.isSolved() && !word.isRevealed()){
                word.setRevealed(true);

                word.firstLetter = grid.getLetterByXY(word.getX(), word.getY());
                word.setJumping(true);

                Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Roboto-Black.ttf");
                ((TextView)word.firstLetter.findViewById(R.id.letter)).setTypeface(custom_font);

                Integer tag = (Integer)word.firstLetter.getTag();

                if(tag == null || tag == 0){
                    tag = 1;
                }else{
                    tag++;

                }

                word.firstLetter.setTag(new Integer(tag));
                animateHintStep1(word, word.firstLetter, 0);
                break;
            }
        }
        setVideoIcon();
    }



    private void animateHintStep1(final Word word, final View view, int delay){
        if(!word.isJumping()) {
            return;
        }

        TranslateAnimation jump = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                0.0f,
                Animation.RELATIVE_TO_PARENT,
                -0.3f);

        jump.setDuration(100);
        jump.setStartOffset(delay);
        jump.setInterpolator(new DecelerateInterpolator());

        jump.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                animateHintStep2(word, view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.findViewById(R.id.letter).startAnimation(jump);

    }



    private void animateHintStep2(final Word word, final View view){
        if(!word.isJumping())
            return;

        TranslateAnimation bounce = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_PARENT,
                -0.3f,
                Animation.RELATIVE_TO_PARENT,
                0.0f);

        bounce.setDuration(600);
        bounce.setInterpolator(new BounceInterpolator());
        bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                animateHintStep1(word, view, 300);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view.findViewById(R.id.letter).startAnimation(bounce);
    }


    @Override
    protected void onResume() {
        super.onResume();

        int delay = backFromSettings ? 0 : 100;
        backFromSettings = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(googleRewardedAd != null) {
                    googleRewardedAd.resume(GameActivity.this);
                    loadRewardedVideoAd();
                    requestNewInterstitial();
                }

                mAdView = findViewById(R.id.adView);
                if(Config.enableBannerAd){
                   mAdView.setVisibility(View.VISIBLE);
                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice("1F474434B1229D7A3963E1B2DEB4E609")
                            .build();

                    mAdView.loadAd(adRequest);
                }else{
                    mAdView.setVisibility(View.GONE);
                }

                setVideoIcon();

                boolean gridOn = Settings.getBooleanValue(GameActivity.this, Constants.GRID_ON, false);
                grid.showGrid(gridOn);
                grid.enableSelectionStroke = Settings.getBooleanValue(GameActivity.this, Constants.MARQUEE_ON, true);
                if(grd_word_list != null) {
                    grd_word_list.setNumColumns(Settings.getIntValue(GameActivity.this, Constants.NUM_COLS, 2));
                    float wordSize = findWordTextSize();
                    wordListAdapter.textSize = wordSize;
                    grd_word_list.setVerticalSpacing(getGridVerticalSpacing((int)wordSize));
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!timerMode)
                            resumeChrono();
                        else {
                            if(timer != null)timer.cancel();
                            countDown(timerTime);
                        }
                    }
                }, 1200);

                toggleMode();
            }
        }, delay);


        if(grid != null && delay == 0 && board != null && board.length > 0) {
            grid.update(board);
        }
        setVideoIcon();
    }



    private void setVideoIcon(){
        int hintsLeft = Settings.getIntValue(this, HINTS_LEFT, Config.defaultHintCount);

        if(hintsLeft == 0 && isRewardedVideoLoaded()){
            ImageView iv = findViewById(R.id.hint);
            if(iv != null) {
                boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);
                iv.setImageDrawable(ContextCompat.getDrawable(this, night ? R.drawable.reward_icon_n : R.drawable.reward_icon));
            }
        }
    }



    private void setBulbIcon(){
        ImageView iv = findViewById(R.id.hint);
        if(iv != null){
            boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);
            iv.setImageDrawable(ContextCompat.getDrawable(this, night ? R.drawable.hint_icon_n : R.drawable.hint_icon));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(googleRewardedAd != null)
            googleRewardedAd.destroy(this);

        if(chrono != null && !timerMode)
            stopChrono();
    }



    @Override
    protected void onPause() {
        super.onPause();

        if(googleRewardedAd != null)
            googleRewardedAd.pause(this);

        if (chrono != null ){

            if(!timerMode)
                pauseChrono();
            else {
                if(timer != null){
                    timer.cancel();
                    timer = null;
                }
            }
        }
    }


    private void pauseChrono(){
        timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
        chrono.stop();
    }



    private void resumeChrono(){
        chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        chrono.start();
    }



    private void stopChrono(){
        chrono.stop();
    }



    private void setPreview(){

        preview = findViewById(R.id.preview);

        WSLayout.TouchListener listener = new WSLayout.TouchListener() {
            @Override
            public void touchStarted() {
                Animation a = AnimationUtils.loadAnimation(GameActivity.this, R.anim.preview_cover_up);
                findViewById(R.id.cover).startAnimation(a);
                preview.setText("");
            }

            @Override
            public void dragging(String partialWord) {
                setPreviewText(partialWord);
            }

            @Override
            public void touchEnded() {
                Animation a = AnimationUtils.loadAnimation(GameActivity.this, R.anim.preview_cover_down);
                findViewById(R.id.cover).startAnimation(a);
            }
        };
        grid.setTouchListener(listener);
    }



    private void setPreviewText(String partialWord){
        if(partialWord != null && partialWord.length() > 0) {
            preview.setText(partialWord);

            int size = partialWord.length();

            if (size > 9)
                size = 9;

            String resName = "s_" + size;

            int resId = getResources().getIdentifier(resName, "raw", getPackageName());
            SoundPlayer.playSound(GameActivity.this, resId);
        }
    }


    private void clearBoard() {
        solution.clear();
        grid.clear();
        String langCode = Settings.getStringValue(this, getResources().getString(R.string.pref_key_language), null);

        for (int i = 0; i < lock.length; i++) {
            for (int j = 0; j < lock[i].length; j++) {
                lock[i][j] = false;
            }
        }

        char c;
        if(wordList != null && wordList.length > 0)
            c = wordList[0].charAt(0);
        else {
            if(langCode.equals("ja")){
                c = '蒲';
            }else if(langCode.equals("ko")) {
                c = '에';
            }else if(langCode.equals("ru")){
                c = 'Б';
            }else{
                c = 'D';
            }

        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = c;
            }
        }

    }




    private void requestNewInterstitial() {
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("1F474434B1229D7A3963E1B2DEB4E609")
                .build();

        googleInterstitialAd.loadAd(request);
    }





    public boolean wordHighlighted(List<Integer> positions, Direction userDirection, int startX, int startY) {
        boolean foundIt = false;
        StringBuilder forwardWord = new StringBuilder();

        for (Integer position : positions) {
            int row = position / cols;
            int col = position % cols;
            char c = board[row][col];
            forwardWord.append(c);
        }

        for (Word word : solution) {
            if(word == null || word.isSolved())
                continue;

            if(word.getText().equals(forwardWord.toString()))
                foundIt = true;

            if (foundIt) {
                foundCount++;
                word.setSolved(true);

                word.setDirection(userDirection);
                word.setX(startX);
                word.setY(startY);
                grid.goal(word);
                wordListAdapter.setWordFound();
                printWordsLeft();

                if(word.isJumping()){
                    Integer tag = (Integer)word.firstLetter.getTag();
                    int i = tag == null ? 0 :tag.intValue();
                    i--;
                    word.firstLetter.setTag(i);
                    if(i == 0) {
                        word.setJumping(false);
                        word.firstLetter.clearAnimation();
                        ((TextView)word.firstLetter.findViewById(R.id.letter)).setTypeface(Typeface.DEFAULT_BOLD);
                     }
                }

                String resName = "w_" + ((foundCount-1) % 9);
                int resId = getResources().getIdentifier(resName, "raw", getPackageName());
                SoundPlayer.playSound(GameActivity.this, resId);
                break;
            }
        }

        if (foundCount == solution.size()) {
            Boolean volumeOn = Settings.getBooleanValue(this, ConfigActivity.SOUND, true);
            SoundPlayer.playSound(this, SoundPlayer.VICTORY);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    puzzleFinished();
                }
            }, volumeOn ? 500 : 300);

        }

        return foundIt;
    }



    private int findAchievementId(){

        int achievementId = -1;

        if(cols == DifficultyActivity.DIFFICULTY_EXTREMELY_EASY_SIZE) {
            achievementId = timerMode ?
                    R.string.achievement_solve_a_time_limited_extremely_easy_puzzle:
                    R.string.achievement_solve_an_extremely_easy_puzzle;
        }else if(cols == DifficultyActivity.DIFFICULTY_VERY_EASY_SIZE){
            achievementId = timerMode ?
                    R.string.achievement_solve_a_time_limited_very_easy_puzzle:
                    R.string.achievement_solve_a_very_easy_puzzle;
        }else if(cols == DifficultyActivity.DIFFICULTY_EASY_SIZE){
            achievementId = timerMode ?
                    R.string.achievement_solve_a_time_limited_easy_puzzle:
                    R.string.achievement_solve_an_easy_puzzle;
        }else if(cols == DifficultyActivity.DIFFICULTY_MEDIUM_SIZE){
            achievementId = timerMode ?
                    R.string.achievement_solve_a_time_limited_medium_puzzle:
                    R.string.achievement_solve_a_medium_puzzle;
        }else if(cols == DifficultyActivity.DIFFICULTY_DIFFICULT_SIZE){
            achievementId = timerMode ?
                    R.string.achievement_solve_a_time_limited_difficult_puzzle:
                    R.string.achievement_solve_a_difficult_puzzle;
        }else if(cols == DifficultyActivity.DIFFICULTY_VERY_DIFFICULT_SIZE){
            achievementId = timerMode ?
                    R.string.achievement_solve_a_time_limited_very_difficult_puzzle:
                    R.string.achievement_solve_a_very_difficult_puzzle;
        }else if(cols == DifficultyActivity.DIFFICULTY_EXCESSIVELY_DIFFICULT_SIZE){
            achievementId = timerMode ?
                    R.string.achievement_solve_a_time_limited_excessively_difficult_puzzle:
                    R.string.achievement_solve_an_excessively_difficult_puzzle;
        }else if(cols == DifficultyActivity.DIFFICULTY_EXTREMELY_DIFFICULT_SIZE){
            achievementId = timerMode ?
                    R.string.achievement_solve_a_title_limited_extremely_difficult_puzzle:
                    R.string.achievement_solve_an_extremely_difficult_puzzle;
        }else if(cols == DifficultyActivity.DIFFICULTY_SUPREMELY_DIFFICULT_SIZE){
            achievementId = timerMode ?
                    R.string.achievement_solve_a_time_limited_supremely_difficult_puzzle:
                    R.string.achievement_solve_a_supremely_difficult_puzzle;
        }

        return achievementId;
    }


    private int findLeaderBoardId(){

        if(cols == DifficultyActivity.DIFFICULTY_EXTREMELY_EASY_SIZE) {
            return R.string.leaderboard_revealed_words_5x5;
        }else if(cols == DifficultyActivity.DIFFICULTY_VERY_EASY_SIZE){
            return R.string.leaderboard_revealed_words_6x6;
        }else if(cols == DifficultyActivity.DIFFICULTY_EASY_SIZE){
            return R.string.leaderboard_revealed_words_8x8;
        }else if(cols == DifficultyActivity.DIFFICULTY_MEDIUM_SIZE){
            return R.string.leaderboard_revealed_words_10x10;
        }else if(cols == DifficultyActivity.DIFFICULTY_DIFFICULT_SIZE){
            return R.string.leaderboard_revealed_words_12x12;
        }else if(cols == DifficultyActivity.DIFFICULTY_VERY_DIFFICULT_SIZE){
            return R.string.leaderboard_revealed_words_14x14;
        }else if(cols == DifficultyActivity.DIFFICULTY_EXCESSIVELY_DIFFICULT_SIZE){
            return R.string.leaderboard_revealed_words_16x16;
        }else if(cols == DifficultyActivity.DIFFICULTY_EXTREMELY_DIFFICULT_SIZE){
            return R.string.leaderboard_revealed_words_18x18;
        }else if(cols == DifficultyActivity.DIFFICULTY_SUPREMELY_DIFFICULT_SIZE){
            return R.string.leaderboard_revealed_words_20x20;
        }

        return -1;
    }



    private void printWordsLeft(){
        TextView tv = findViewById(R.id.words_left);
        tv.setText(new StringBuilder().append(solution.size()).append("/").append(foundCount).toString());
    }



    private void puzzleFinished() {

        if(!timerMode)
            stopChrono();
        else{
            if(timer!=null)timer.cancel();}

        intent = new Intent(GameActivity.this, ResultActivity.class);
        intent.putExtra(ResultActivity.GRID_SIZE, cols);
        intent.putExtra(ResultActivity.TIME, chrono.getText().toString());
        intent.putExtra(ResultActivity.HINTS_USED, givenHintCount);
        intent.putExtra(ResultActivity.WORD_COUNT, solution.size());
        if(Config.enableLeaderboardsAndAchievements) {
            intent.putExtra(ResultActivity.ACHIEVEMENT, findAchievementId());
            intent.putExtra(ResultActivity.LEADERBOARDS, findLeaderBoardId());
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Config.enableInterstitialAd && isInterstitialAdLoaded()){
                    googleInterstitialAd.show();

                }else{
                    startActivityForResult(intent, 1);
                    overridePendingTransition(0,0);
                }
            }
        }, 200);
    }



    private float findWordTextSize(){
        float size = 18;
        Resources resources = getResources();

        switch (cols){
            case DifficultyActivity.DIFFICULTY_EXTREMELY_EASY_SIZE:
                size = resources.getDimension(R.dimen.word_size_5);
                break;
            case DifficultyActivity.DIFFICULTY_VERY_EASY_SIZE:
            case DifficultyActivity.DIFFICULTY_EASY_SIZE:
                size = resources.getDimension(R.dimen.word_size_a);
                break;
            case DifficultyActivity.DIFFICULTY_MEDIUM_SIZE:
            case DifficultyActivity.DIFFICULTY_DIFFICULT_SIZE:
                size = resources.getDimension(R.dimen.word_size_b);
                break;
            case DifficultyActivity.DIFFICULTY_VERY_DIFFICULT_SIZE:
            case DifficultyActivity.DIFFICULTY_EXCESSIVELY_DIFFICULT_SIZE:
                size = resources.getDimension(R.dimen.word_size_c);
                break;
            case DifficultyActivity.DIFFICULTY_EXTREMELY_DIFFICULT_SIZE:
                size = resources.getDimension(R.dimen.word_size_d);
                break;
            case DifficultyActivity.DIFFICULTY_SUPREMELY_DIFFICULT_SIZE:
                size = resources.getDimension(R.dimen.word_size_e);
                break;
        }

        size *= 1.5f;

        int numWordCols = Settings.getIntValue(GameActivity.this, Constants.NUM_COLS, 2);
        switch (numWordCols){
            case 1:
                size *= 1;
                break;
            case 2:
                size *= 0.7f;
                break;
            case 3:
                size *= 0.6f;
                break;
            case 4:
                size *= 0.55f;
                break;
        }

        return size;
    }



    private void randomizeWords() {
        randIndices = new int[cols * cols];

        Random rand = new Random(System.currentTimeMillis());
        for (int i = 0; i < randIndices.length; i++) {
            randIndices[i] = i;
        }

        for (int i = randIndices.length - 1; i >= 1; i--) {
            int randIndex = rand.nextInt(i);
            int realIndex = randIndices[i];
            randIndices[i] = randIndices[randIndex];
            randIndices[randIndex] = realIndex;
        }

        selectWordsFromDB();

        for (String word : wordList) {
            addWord(word);
        }
    }



    private void addWord(String word) {
        if (word.length() > cols) {
            return;
        }

        Random rand = new Random();

        for (int i = directions.length - 1; i >= 1; i--) {
            int randIndex = rand.nextInt(i);
            Direction direction = directions[i];
            directions[i] = directions[randIndex];
            directions[randIndex] = direction;
        }

        Direction bestDirection = null;
        int bestRow = -1;
        int bestCol = -1;
        int bestScore = -1;

        for (int index : randIndices) {
            int row = index / cols;
            int col = index % cols;
            for (Direction direction : directions) {
                int score = isEmbeddable(word, direction, row, col);
                if (score > bestScore) {
                    bestRow = row;
                    bestCol = col;
                    bestDirection = direction;
                    bestScore = score;
                }
            }
        }
        if (bestScore >= 0) {
            Word result = new Word(word, bestRow, bestCol, bestDirection, this);
            if(solution.contains(result)) {
                return;
            }
            embedWord(result);
        }
    }



    private void embedWord(Word word) {
        int curRow = word.getY();
        int curCol = word.getX();
        final String wordStr = word.getText();
        final Direction direction = word.getDirection();

        for (int i = 0; i < wordStr.length(); i++) {
            char c = wordStr.charAt(i);

            board[curRow][curCol] = c;
            lock[curRow][curCol] = true;

            if (direction.isUp()) {
                curRow -= 1;
            } else if (direction.isDown()) {
                curRow += 1;
            }

            if (direction.isLeft()) {
                curCol -= 1;
            } else if (direction.isRight()) {
                curCol += 1;
            }

        }
        solution.add(word);
    }



    private int isEmbeddable(String word, Direction direction, int row, int col) {
        if (getEmptySpace(direction, row, col) < word.length()) {
            return -1;
        }

        int score = 0;
        int curRow = row;
        int curCol = col;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);

            if (lock[curRow][curCol] && board[curRow][curCol] != c) {
                return -1;
            } else if (lock[curRow][curCol]) {
                score++;
            }

            if (direction.isUp()) {
                curRow -= 1;
            } else if (direction.isDown()) {
                curRow += 1;
            }

            if (direction.isLeft()) {
                curCol -= 1;
            } else if (direction.isRight()) {
                curCol += 1;
            }
        }
        return score;
    }



    private int getEmptySpace(Direction direction, int row, int col) {
        switch (direction) {
            case SOUTH:
                return col - row;
            case SOUTH_WEST:
                return Math.min(col - row, col);
            case SOUTH_EAST:
                return Math.min(col - row, cols - col);
            case WEST:
                return col;
            case EAST:
                return cols - col;
            case NORTH:
                return row;
            case NORTH_WEST:
                return Math.min(row, col);
            case NORTH_EAST:
                return Math.min(row, cols - col);

        }

        return 0;
    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();

        switch(view.getId()){
            case R.id.hint_container:
            case R.id.settings_btn:
            case R.id.pause_btn:
            case R.id.help_btn:
                if (action == MotionEvent.ACTION_DOWN)
                    buttonDown(view);
                if(action == MotionEvent.ACTION_UP)
                    buttonUp(view);
                break;
        }
        return false;
    }



    public void toggleMode() {
        boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);

        View cover = findViewById(R.id.cover);
        View bottom_panel = findViewById(R.id.bottom_panel);
        View preview_bottom = findViewById(R.id.preview_bottom);
        View preview_container = findViewById(R.id.preview_container);
        TextView words_left = findViewById(R.id.words_left);
        TextView chrono = findViewById(R.id.chrono);
        ImageButton settings_btn = findViewById(R.id.settings_btn);
        ImageButton pause_btn = findViewById(R.id.pause_btn);
        ImageButton help_btn = findViewById(R.id.help_btn);
        TextView hints_left = findViewById(R.id.hints_left);
        TextView preview = findViewById(R.id.preview);
        View grid_container = findViewById(R.id.grid_container);
        View sep = findViewById(R.id.sep);

        if(night) {
            int app_bg_n = ContextCompat.getColor(this, R.color.app_bg_n);
            cover.setBackgroundColor(app_bg_n);
            bottom_panel.setBackgroundColor(app_bg_n);
            grid_container.setBackgroundColor(ContextCompat.getColor(this, R.color.diff_btn_up_n));

            int toolbar_icon_bg_n = R.drawable.toolbar_icon_bg_n;
            pause_btn.setBackgroundResource(toolbar_icon_bg_n);
            settings_btn.setBackgroundResource(toolbar_icon_bg_n);
            help_btn.setBackgroundResource(toolbar_icon_bg_n);
            int stripe_n = ContextCompat.getColor(this, R.color.stripe_n);
            preview_bottom.setBackgroundColor(stripe_n);
            preview_container.setBackgroundColor(stripe_n);
            words_left.setBackgroundResource(R.drawable.words_left_bg_n);
            sep.setBackgroundColor(stripe_n);

            int text_color_n = ContextCompat.getColor(this, R.color.text_color_n);
            words_left.setTextColor(text_color_n);
            chrono.setTextColor(text_color_n);
            settings_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.settings_icon_n));
            help_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.help_icon_small_n));
            pause_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause_icon_n));

            hints_left.setTextColor(ContextCompat.getColor(this, R.color.button_text_n));
            preview.setTextColor(text_color_n);
        }else{
            int app_bg = ContextCompat.getColor(this, R.color.app_bg);
            //window.setBackgroundColor(app_bg);

            int preview_bg = ContextCompat.getColor(this, R.color.preview_bg);
            preview_bottom.setBackgroundColor(preview_bg);
            preview_container.setBackgroundColor(preview_bg);
            sep.setBackgroundColor(preview_bg);
            cover.setBackgroundColor(app_bg);
            grid_container.setBackgroundColor(ContextCompat.getColor(this, R.color.diff_btn_up));
            words_left.setBackgroundResource(R.drawable.words_left_bg);
            words_left.setTextColor(app_bg);

            settings_btn.setBackgroundResource(R.drawable.toolbar_icon_bg);
            settings_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.settings_icon));
            pause_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause_icon));
            pause_btn.setBackgroundResource(R.drawable.toolbar_icon_bg);
            help_btn.setBackgroundResource(R.drawable.toolbar_icon_bg);
            help_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.help_icon_small));
            int secondary_color_dark = ContextCompat.getColor(this, R.color.secondary_color_dark);
            chrono.setTextColor(secondary_color_dark);

            bottom_panel.setBackgroundColor(app_bg);

            hints_left.setTextColor(ContextCompat.getColor(this, R.color.button_text));
            preview.setTextColor(ContextCompat.getColor(this, R.color.stripe));
        }

        setBulbIcon();
        if(wordListAdapter != null)
            wordListAdapter.update();
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GameActivity.this, PauseActivity.class);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.slide_right_to_left_1, R.anim.slide_right_to_left_2);
    }



    protected void loadRewardedVideoAd() {
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("1F474434B1229D7A3963E1B2DEB4E609")
                .build();
        googleRewardedAd.loadAd(getString(R.string.admob_rewarded), request);
    }



    private void showOfferDialog(){

        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialog = inflater.inflate(R.layout.reward_question_dialog, null);

        Resources r = ContextUtil.getCustomResources(this);

        RelativeLayout play = dialog.findViewById(R.id.play);
        play.setOnClickListener(this);
        play.animate().alpha(isRewardedVideoLoaded() ? 1f : 0.3f).start();

        RelativeLayout play_n = dialog.findViewById(R.id.play_n);
        play_n.setOnClickListener(this);
        play_n.animate().alpha(isRewardedVideoLoaded() ? 1f : 0.3f).start();

        TextView reward_title = dialog.findViewById(R.id.reward_title);
        reward_title.setText(r.getString(R.string.reward_text));

        ImageView rays = (ImageView)dialog.findViewById(R.id.rays);

        boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);

        if(night){
            TextView play_video_label_n = dialog.findViewById(R.id.play_video_label_n);
            play_video_label_n.setText(r.getString(R.string.watch_video));
            int color = ContextCompat.getColor(this, R.color.text_color_n);
            play_video_label_n.setTextColor(color);
            dialog.findViewById(R.id.play_n).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.reward_container).setBackgroundResource(R.drawable.dialog_bg_n);
            reward_title.setTextColor(ContextCompat.getColor(this, R.color.button_text_n));
            rays.setImageResource(R.drawable.rays_n);
        }else{
            TextView play_video_label = dialog.findViewById(R.id.play_video_label);
            play_video_label.setText(r.getString(R.string.watch_video));
            int color = ContextCompat.getColor(this, R.color.app_bg);
            play_video_label.setTextColor(color);
            dialog.findViewById(R.id.play).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.reward_container).setBackgroundResource(R.drawable.dialog_bg);
            reward_title.setTextColor(ContextCompat.getColor(this, R.color.button_text));
            rays.setImageResource(R.drawable.rays);
            rays.setAlpha(0.1f);
        }

        rewardDialog = new Dialog(this);
        rewardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        rewardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rewardDialog.setContentView(dialog);
        if(!isFinishing())rewardDialog.show();
    }



    private void coinAnimStep1(){
        final View hint_container = findViewById(R.id.hint_container);
        hint_container.setEnabled(false);
        hint_container.setClickable(false);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int[] location = new int[2];

        hint_container.getLocationOnScreen(location);
        final View coin = findViewById(R.id.coin);

        final Path path = new Path();

        path.moveTo(width, 0); // Starting point
        // Create a cubic Bezier cubic left path
        path.cubicTo(
                width/2.5f,
                height/5,
                width/4,
                4*height/5,
                location[0],
                location[1]+(hint_container.getHeight()/2)-coin.getWidth()/2);

        ValueAnimator pathAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);

        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            float[] point = new float[2];

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Gets the animated float fraction
                float val = animation.getAnimatedFraction();

                // Gets the point at the fractional path length
                PathMeasure pathMeasure = new PathMeasure(path, false);
                pathMeasure.getPosTan(pathMeasure.getLength() * val, point, null);

                // Sets view location to the above point
                coin.setX(point[0]);
                coin.setY(point[1]);
            }
        });


        pathAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                coinAnimStep2();
            }
        });

        pathAnimator.setDuration(1200);
        pathAnimator.setInterpolator(new AccelerateInterpolator());
        pathAnimator.start();
        coin.setVisibility(View.VISIBLE);
    }



    private void coinAnimStep2(){
        findViewById(R.id.coin).setVisibility(View.GONE);
        SoundPlayer.playSound(GameActivity.this, SoundPlayer.EARNED_HINTS);
        ScaleAnimation sa = new ScaleAnimation(1f,1.2f,1f,1.2f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setInterpolator(new DecelerateInterpolator());
        sa.setDuration(200);
        sa.setFillAfter(true);
        final View hint_container = findViewById(R.id.hint_container);
        sa.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                coinAnimStep3();
                int hintsLeft = Settings.getIntValue(GameActivity.this, HINTS_LEFT, Config.defaultHintCount);
                TextView hintsLeftTxt = ((TextView) findViewById(R.id.hints_left));
                if (hintsLeftTxt != null) {
                    hintsLeftTxt.setText(hintsLeft + "");
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        hint_container.startAnimation(sa);
    }



    private void coinAnimStep3(){
        ScaleAnimation sa2 = new ScaleAnimation(1.2f, 1f,1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa2.setDuration(400);
        sa2.setInterpolator(new BounceInterpolator());
        final View hint_container = findViewById(R.id.hint_container);
        sa2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                coinAnimStep4();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        hint_container.startAnimation(sa2);
    }



    private void coinAnimStep4(){
        final View plus = findViewById(R.id.plus);
        AlphaAnimation a = new AlphaAnimation(0f, 1);
        a.setDuration(200);
        a.setFillAfter(true);
        plus.setVisibility(View.VISIBLE);
        plus.startAnimation(a);

        Animation b =  AnimationUtils.loadAnimation(GameActivity.this, R.anim.checkbox_slide_up);
        b.setFillAfter(true);
        b.setDuration(1000);
        plus.startAnimation(b);

        View hint_container = findViewById(R.id.hint_container);
        hint_container.setEnabled(true);
        hint_container.setClickable(true);
    }



    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d("wordsearch", "onRewardedVideoAdLoaded");

        setVideoIcon();

        View resultVideo = findViewById(R.id.video);
        if(resultVideo != null){
            resultVideo.setVisibility(View.VISIBLE);
        }

        if(timeFinishDialog != null)
            timeFinishDialog.enableWatchButton();
    }



    @Override
    public void onRewardedVideoAdOpened() {
        Log.d("wordsearch", "onRewardedVideoAdOpened");
    }


    @Override
    public void onRewardedVideoStarted() {
        Log.d("wordsearch", "onRewardedVideoStarted");
    }


    @Override
    public void onRewardedVideoAdClosed() {
        Log.d("wordsearch", "onRewardedVideoAdClosed ");

        if(giveExtraHints && userRewarded)
            coinAnimStep1();

        if(giveExtraTime){
            if(userRewarded) {
                giveExtraTime = false;
                final ImageView extra_time = new ImageView(this);
                extra_time.setImageResource(R.drawable.extra_time);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 100, 0, 0);
                extra_time.setLayoutParams(layoutParams);

                final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.checkbox_slide_up);
                anim.setFillAfter(true);
                final RelativeLayout root = findViewById(R.id.root);
                root.addView(extra_time);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        root.removeView(extra_time);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                extra_time.startAnimation(anim);
                timerTime = 1000 * 30;
            }else{
                showTimeFinishDialog();
            }
        }

        userRewarded = false;
        giveExtraTime = false;
        giveExtraHints = false;

        loadRewardedVideoAd();
    }



    @Override
    public void onRewarded(RewardItem reward) {
        Log.d("wordsearch", "onRewarded");

        // reward the user only if they have completely watched the video
        if(giveExtraHints) {
            int hintsLeft = Settings.getIntValue(this, HINTS_LEFT, Config.defaultHintCount);
            hintsLeft += Config.hintRewardCount;
            Settings.saveIntValue(this, HINTS_LEFT, hintsLeft);
            setBulbIcon();
        }
        userRewarded = true;
    }


    @Override
    public void onRewardedVideoAdLeftApplication() {Log.d("wordsearch", "onRewardedVideoAdLeftApplication");}


    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d("wordsearch", "onRewardedVideoAdFailedToLoad:"+i);

        if(giveExtraHints) {
            setBulbIcon();
            View resultVideo = findViewById(R.id.video);
            if (resultVideo != null) {
                resultVideo.setVisibility(View.GONE);
            }
        }
        giveExtraTime = false;
        giveExtraHints = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(5000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadRewardedVideoAd();
                        }
                    });
                }catch(InterruptedException e){}
            }
        }).start();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

}
