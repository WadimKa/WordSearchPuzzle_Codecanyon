package net.codecanyon.trimax.wordsearch.activity;

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

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.example.games.basegameutils.GameHelper;

import net.codecanyon.trimax.wordsearch.Config;
import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;



public class ResultActivity extends Activity implements View.OnTouchListener, View.OnClickListener, RewardedVideoAdListener {

    public static final String GRID_SIZE = "gridSize";
    public static final String TIME = "time";
    public static final String HINTS_USED = "hintsUsed";
    public static final String WORD_COUNT = "wordCount";
    public static final String ACHIEVEMENT = "achievement";
    public static final String LEADERBOARDS = "leaderboards";

    public static final int REQUEST_CONFIG = 999;

    private boolean scoreSubmitted;

    protected RewardedVideoAd mAd;
    private boolean userRewarded;
    private Resources resources;

    private Dialog rewardDialog;
    private GameHelper gameHelper;

    int[] smokeBitmaps = {
            R.drawable.smoke_0,
            R.drawable.smoke_1,
            R.drawable.smoke_2,
            R.drawable.smoke_3,
            R.drawable.smoke_4,
            R.drawable.smoke_5,
            R.drawable.smoke_6,
            R.drawable.smoke_7
    };


    int[] checkmarkBitmaps = {
            R.drawable.checkmark0001,
            R.drawable.checkmark0002,
            R.drawable.checkmark0003,
            R.drawable.checkmark0004,
            R.drawable.checkmark0005,
            R.drawable.checkmark0006,
            R.drawable.checkmark0007,
            R.drawable.checkmark0008,
            R.drawable.checkmark0009,
            R.drawable.checkmark0010,
            R.drawable.checkmark0011,
            R.drawable.checkmark0012,
            R.drawable.checkmark0013,
            R.drawable.checkmark0014,
            R.drawable.checkmark0015,
            R.drawable.checkmark0016,
            R.drawable.checkmark0017,
            R.drawable.checkmark0018,
            R.drawable.checkmark0019,
            R.drawable.checkmark0020,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        TextView duration = findViewById(R.id.duration);
        String time = getIntent().getStringExtra(TIME);
        duration.setText(time);

        TextView grid_size = findViewById(R.id.grid_size);
        String grid = String.valueOf(getIntent().getIntExtra(GRID_SIZE, 0));
        grid_size.setText(grid + "x" + grid);

        TextView words = findViewById(R.id.words);
        String wordCount = String.valueOf(getIntent().getIntExtra(WORD_COUNT, 0));
        words.setText(wordCount);

        TextView hints_used = findViewById(R.id.hints_used);
        String hintsUsed = String.valueOf(getIntent().getIntExtra(HINTS_USED, 0));
        hints_used.setText(hintsUsed);

        View settings_btn = findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(this);
        settings_btn.setOnTouchListener(this);

        View home = findViewById(R.id.home);
        home.setOnClickListener(this);
        home.setOnTouchListener(this);

        final View sign_in = findViewById(R.id.sign_in);
        sign_in.setOnClickListener(this);
        sign_in.setOnTouchListener(this);
        sign_in.setClickable(false);
        sign_in.setEnabled(false);
        sign_in.setAlpha(0.3f);
        if(!Config.enableLeaderboardsAndAchievements)
            sign_in.setVisibility(View.GONE);

        Button playgame = findViewById(R.id.playgame);
        playgame.setOnClickListener(this);
        playgame.setOnTouchListener(this);

        disableVideoButton(0);

       mAd = MobileAds.getRewardedVideoAdInstance(this);
       mAd.setRewardedVideoAdListener(this);

        int hintsLeft = Settings.getIntValue(ResultActivity.this, GameActivity.HINTS_LEFT, Config.defaultHintCount);
        ((TextView)findViewById(R.id.hints_left)).setText(hintsLeft+"");

        if(Config.enableLeaderboardsAndAchievements) {
            gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
            gameHelper.enableDebugLog(false);
        }

        GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener(){
            @Override
            public void onSignInFailed(){
                Toast.makeText(ResultActivity.this, resources.getString(R.string.not_signed_in), Toast.LENGTH_LONG).show();
                sign_in.setClickable(true);
                sign_in.setEnabled(true);
                sign_in.setAlpha(1f);
                TextView not_signed_in = (TextView)findViewById(R.id.not_signed_in);
                not_signed_in.setText(resources.getString(R.string.sign_in_to_submit));
                not_signed_in.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSignInSucceeded(){
                if(IntroActivity.isSignedIn(gameHelper, ResultActivity.this)) {
                    sign_in.setClickable(false);
                    sign_in.setEnabled(false);
                    sign_in.setAlpha(0.3f);
                    if (!scoreSubmitted) {
                        saveAchievements();
                        saveLeaderBoards();
                    }
                    findViewById(R.id.not_signed_in).setVisibility(View.GONE);
                }else{
                    IntroActivity.showNoConnection(ResultActivity.this);
                    sign_in.setClickable(true);
                    sign_in.setEnabled(true);
                    sign_in.setAlpha(1f);
                    TextView not_signed_in = (TextView)findViewById(R.id.not_signed_in);
                    not_signed_in.setText(resources.getString(R.string.sign_in_to_submit));
                    not_signed_in.setVisibility(View.VISIBLE);
                }
            }
        };

        boolean declined = Settings.getBooleanValue(this, Constants.DECLINED_TO_SIGN_IN, false);

        resources = ContextUtil.getCustomResources(this);

        if(Config.enableLeaderboardsAndAchievements) {
            gameHelper.setConnectOnStart(!declined);
            gameHelper.setup(gameHelperListener);
            if (declined) {
                gameHelper.signOut();
                sign_in.setClickable(true);
                sign_in.setEnabled(true);
                sign_in.setAlpha(1f);
                TextView not_signed_in = (TextView) findViewById(R.id.not_signed_in);
                not_signed_in.setText(resources.getString(R.string.sign_in_to_submit));
                not_signed_in.setVisibility(View.VISIBLE);
            }
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSmokedView(findViewById(R.id.grid_data), (ImageView)findViewById(R.id.smoke_0));
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSmokedView(findViewById(R.id.word_data), (ImageView)findViewById(R.id.smoke_1));
            }
        }, 900);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSmokedView(findViewById(R.id.hint_data), (ImageView)findViewById(R.id.smoke_2));
            }
        }, 1300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateSmokedView(findViewById(R.id.time_data), (ImageView)findViewById(R.id.smoke_3));
            }
        }, 1700);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkmark();
            }
        }, 2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadRewardedVideoAd();
            }
        }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!Config.enableLeaderboardsAndAchievements)
            return;

        final View sign_in = findViewById(R.id.sign_in);

        if(gameHelper != null)gameHelper.onActivityResult(requestCode, resultCode, data);

        if(gameHelper != null){
            boolean declined = Settings.getBooleanValue(this, Constants.DECLINED_TO_SIGN_IN, false);
            if(declined){
                gameHelper.signOut();
                if(scoreSubmitted){
                    sign_in.setClickable(false);
                    sign_in.setEnabled(false);
                    sign_in.setAlpha(0.3f);
                }else{
                    sign_in.setClickable(true);
                    sign_in.setEnabled(true);
                    sign_in.setAlpha(1f);
                    TextView not_signed_in = (TextView) findViewById(R.id.not_signed_in);
                    not_signed_in.setText(resources.getString(R.string.sign_in_to_submit));
                    not_signed_in.setVisibility(View.VISIBLE);
                }
            }

        }

        if(requestCode == REQUEST_CONFIG) {
            if (!scoreSubmitted) {
                gameHelper.reconnectClient();
            }
        }else {
            if (IntroActivity.isSignedIn(gameHelper, this)) {
                sign_in.setClickable(false);
                sign_in.setEnabled(false);
                sign_in.setAlpha(0.3f);
                if (!scoreSubmitted) {
                    saveAchievements();
                    saveLeaderBoards();
                }
            } else {
                sign_in.setClickable(true);
                sign_in.setEnabled(true);
                sign_in.setAlpha(1f);
                TextView not_signed_in = (TextView) findViewById(R.id.not_signed_in);
                not_signed_in.setText(resources.getString(R.string.sign_in_to_submit));
                not_signed_in.setVisibility(View.VISIBLE);
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(Config.enableLeaderboardsAndAchievements && gameHelper != null){
            gameHelper.onStart(this);
        }

    }



    @Override
    protected void onStop() {
        super.onStop();
        if(Config.enableLeaderboardsAndAchievements && gameHelper != null)
            gameHelper.onStop();
    }



    private void checkmark(){

        final ImageView checkmark_holder = findViewById(R.id.checkmark_holder);

        for(int i=0;i<checkmarkBitmaps.length;i++){
            final int res = checkmarkBitmaps[i];
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkmark_holder.setImageDrawable(ContextCompat.getDrawable(ResultActivity.this, res));
                }
            }, i * 40);
        }
    }



    private void disableVideoButton(long delay){
        RelativeLayout video_btn = findViewById(R.id.video_btn);
        video_btn.setEnabled(false);
        video_btn.setClickable(false);
        Animation a = new AlphaAnimation(video_btn.getAlpha(), 0.3f);
        a.setDuration(delay);
        a.setFillAfter(true);
        video_btn.startAnimation(a);
        video_btn.setOnClickListener(null);
        video_btn.setOnTouchListener(null);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.playgame:{
                setResult(PauseActivity.RESTART_GAME);
                finish();
                break;
            }
            case R.id.home:{
                setResult(PauseActivity.GO_HOME);
                finish();
                break;
            }
            case R.id.video_btn:{
                if(isRewardedVideoLoaded())
                    showOfferDialog();
                else{
                    disableVideoButton(300);
                }
                break;
            }
            case R.id.play:
            case R.id.play_n:{
                mAd.show();

                if(rewardDialog != null)rewardDialog.dismiss();
                break;
            }
            case R.id.settings_btn:{
                Intent intent = new Intent(ResultActivity.this, ConfigActivity.class);
                startActivityForResult(intent, REQUEST_CONFIG);
                overridePendingTransition(R.anim.slide_right_to_left_1, R.anim.slide_right_to_left_2);
                break;
            }
            case R.id.sign_in:
                if(!IntroActivity.isNetworkConnected(this)){
                    IntroActivity.showNoConnection(this);
                    return;
                }

                Settings.saveBooleanValue(this, Constants.DECLINED_TO_SIGN_IN, false);
                if(gameHelper != null)
                    gameHelper.beginUserInitiatedSignIn();
                break;
        }
    }



    private boolean isRewardedVideoLoaded(){
        if(mAd != null)
            return mAd.isLoaded();
        else
            return false;
    }




    @Override
    protected void onResume() {
        super.onResume();

        mAd.resume(this);
        toggleMode();
        setLabels();
    }



    @Override
    protected void onPause() {
        super.onPause();
        mAd.pause(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAd.destroy(this);
    }



    public void setLabels() {
        ((TextView)findViewById(R.id.well_done)).setText(resources.getString(R.string.well_done));
        ((TextView)findViewById(R.id.title)).setText(resources.getString(R.string.app_name));
        ((Button)findViewById(R.id.playgame)).setText(resources.getString(R.string.play_again));
    }



    public void toggleMode() {
        boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);
        View window = this.getWindow().getDecorView();
        TextView title = findViewById(R.id.title);
        View separator = findViewById(R.id.separator);
        TextView well_done = findViewById(R.id.well_done);
        TextView duration = findViewById(R.id.duration);
        TextView grid_size = findViewById(R.id.grid_size);
        TextView words = findViewById(R.id.words);
        TextView hints_used = findViewById(R.id.hints_used);
        ImageView video = findViewById(R.id.video);
        ImageButton home = findViewById(R.id.home);

        ImageView img_grid_size = findViewById(R.id.img_grid_size);
        ImageView img_word_count = findViewById(R.id.img_word_count);
        ImageView img_hints_used = findViewById(R.id.img_hints_used);
        ImageView img_time = findViewById(R.id.img_time);

        Button playgame = findViewById(R.id.playgame);
        ImageButton settings_btn = findViewById(R.id.settings_btn);
        ImageButton sign_in = findViewById(R.id.sign_in);

        if(night) {
            window.setBackgroundColor(ContextCompat.getColor(this, R.color.app_bg_n));
            int color = ContextCompat.getColor(this, R.color.text_color_n);
            title.setTextColor(color);
            separator.setBackgroundColor(ContextCompat.getColor(this, R.color.stripe_n));
            well_done.setTextColor(color);
            duration.setTextColor(color);
            grid_size.setTextColor(color);
            words.setTextColor(color);
            hints_used.setTextColor(color);
            video.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.reward_icon_n));
            home.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.home_icon_big_n));
            playgame.setTextColor(ContextCompat.getColor(this, R.color.button_text_n));
            playgame.setBackgroundResource(R.drawable.play_btn_rect_n);
            settings_btn.setBackgroundResource(R.drawable.toolbar_icon_bg_n);
            settings_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.settings_icon_n));
            ((TextView)findViewById(R.id.hints_left)).setTextColor(color);

            img_grid_size.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_grid_size_n));
            img_word_count.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_word_count_n));
            img_hints_used.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_hints_used_n));
            img_time.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_time_n));

            sign_in.setBackgroundResource(R.drawable.grey_circle);
        }else{
            int windowColor = ContextCompat.getColor(this, R.color.app_bg);
            window.setBackgroundColor(windowColor);
            int color = ContextCompat.getColor(this, R.color.diff_title);
            title.setTextColor(color);
            separator.setBackgroundColor(ContextCompat.getColor(this, R.color.secondary_color));
            well_done.setTextColor(color);
            duration.setTextColor(windowColor);
            grid_size.setTextColor(windowColor);
            words.setTextColor(windowColor);
            hints_used.setTextColor(windowColor);
            video.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.reward_icon));
            home.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.home_icon_big));
            int white = ContextCompat.getColor(this, R.color.button_text);
            playgame.setTextColor(ContextCompat.getColor(this, R.color.button_text));
            playgame.setBackgroundResource(R.drawable.play_btn_rect);
            settings_btn.setBackgroundResource(R.drawable.toolbar_icon_bg);
            settings_btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.settings_icon));
            ((TextView)findViewById(R.id.hints_left)).setTextColor(white);

            img_grid_size.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_grid_size));
            img_word_count.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_word_count));
            img_hints_used.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_hints_used));
            img_time.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bg_time));

            sign_in.setBackgroundResource(R.drawable.white_circle);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN)
            ContextUtil.buttonDown(view);
        if(action == MotionEvent.ACTION_UP)
            ContextUtil.buttonUp(view);
        return false;
    }



    @Override
    public void onBackPressed() {
        setResult(PauseActivity.GO_HOME);
        finish();
    }



    protected void loadRewardedVideoAd() {
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("1F474434B1229D7A3963E1B2DEB4E609")
                .build();
        mAd.loadAd(getString(R.string.admob_rewarded), request);

    }



    private void saveAchievements(){
        if(!Config.enableBannerAd)return;
        int achievementId = getIntent().getIntExtra(ACHIEVEMENT, -1);

        if(gameHelper.isSignedIn()){
            Games.Achievements.unlockImmediate(gameHelper.getApiClient(), getString(achievementId));
            scoreSubmitted = true;
            findViewById(R.id.not_signed_in).setVisibility(View.GONE);
        }

    }



    private void saveLeaderBoards(){
        if(!Config.enableBannerAd)return;
        final int leaderboardId = getIntent().getIntExtra(LEADERBOARDS, -1);

        if(gameHelper.isSignedIn()){
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(), getString(leaderboardId), LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                @Override
                public void onResult(final Leaderboards.LoadPlayerScoreResult scoreResult) {
                    long mPoints = 0;
                    if (isScoreResultValid(scoreResult)) {
                        mPoints = scoreResult.getScore().getRawScore();
                    }
                    Log.d("wordsearch", "prev score:"+mPoints);
                    mPoints += getIntent().getIntExtra(WORD_COUNT, 0);

                    Games.Leaderboards.submitScoreImmediate(gameHelper.getApiClient(), getString(leaderboardId), mPoints);
                    scoreSubmitted = true;
                    findViewById(R.id.not_signed_in).setVisibility(View.GONE);
                }
            });
        }
    }



    private boolean isScoreResultValid(final Leaderboards.LoadPlayerScoreResult scoreResult) {
        return scoreResult != null && GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null;
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

        ImageView rays = dialog.findViewById(R.id.rays);

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



    private void enableVideoButton(){
        View video_btn = findViewById(R.id.video_btn);
        video_btn.setOnClickListener(this);
        video_btn.setOnTouchListener(this);

        AlphaAnimation a = new AlphaAnimation(0.3f, 1f);
        a.setDuration(300);
        a.setFillAfter(true);
        video_btn.clearAnimation();
        video_btn.setClickable(true);
        video_btn.setEnabled(true);
        video_btn.startAnimation(a);
    }



    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d("wordsearch", "onRewardedVideoAdLoaded ResultActivity");
        enableVideoButton();
    }

    @Override
    public void onRewardedVideoAdOpened() {}

    @Override
    public void onRewardedVideoStarted() {}

    @Override
    public void onRewardedVideoAdClosed() {
        if(userRewarded) {
            coinAnimStep1();
        }
        userRewarded = false;
    }



    @Override
    public void onRewarded(RewardItem reward) {
        Log.d("wordsearch", "onRewarded ResultActivity");

        // reward the user only if they have completely watched the video

        int hintsLeft = Settings.getIntValue(this, GameActivity.HINTS_LEFT, Config.defaultHintCount);
        hintsLeft += Config.hintRewardCount;
        Settings.saveIntValue(this, GameActivity.HINTS_LEFT, hintsLeft);
        userRewarded = true;
    }


    @Override
    public void onRewardedVideoAdLeftApplication() {
        Log.d("wordsearch", "onRewardedVideoAdLeftApplication ResultActivity");
    }



    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        Log.d("wordsearch", "onRewardedVideoAdFailedToLoad ResultActivity");

        View video_btn = findViewById(R.id.video_btn);
        if(video_btn != null){
            disableVideoButton(0);
        }

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


    private void coinAnimStep1(){

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int[] location = new int[2];
        View video_btn = findViewById(R.id.video_btn);
        video_btn.getLocationOnScreen(location);
        final View coin = findViewById(R.id.coin);



        final Path path = new Path();
        path.moveTo(width, 0); // Starting point
        // Create a cubic Bezier cubic left path
        path.cubicTo(
                width*0.2f,
                height/5,
                width*0.1f,
                4*height/5,
                location[0]+(video_btn.getWidth()/2)-coin.getWidth()/2,
                location[1]+(video_btn.getHeight()/2)-coin.getWidth()/2);

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
        SoundPlayer.playSound(this, SoundPlayer.EARNED_HINTS);
        ScaleAnimation sa = new ScaleAnimation(1f,1.2f,1f,1.2f,Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setInterpolator(new DecelerateInterpolator());
        sa.setDuration(200);
        sa.setFillAfter(true);
        final View video_btn = findViewById(R.id.video_btn);
        sa.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                int hintsLeft = Settings.getIntValue(ResultActivity.this, GameActivity.HINTS_LEFT, Config.defaultHintCount);
                TextView hintsLeftTxt = findViewById(R.id.hints_left);
                if (hintsLeftTxt != null) {
                    hintsLeftTxt.setText(hintsLeft + "");
                }
                coinAnimStep3();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        video_btn.startAnimation(sa);
    }



    private void coinAnimStep3(){
        ScaleAnimation sa2 = new ScaleAnimation(1.2f, 1f,1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa2.setDuration(400);
        sa2.setInterpolator(new BounceInterpolator());
        final View video_btn = findViewById(R.id.video_btn);
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
        video_btn.startAnimation(sa2);
    }



    private void coinAnimStep4(){
        final View plus = findViewById(R.id.plus);
        AlphaAnimation a = new AlphaAnimation(0f, 1);
        a.setDuration(200);
        a.setFillAfter(true);
        plus.setVisibility(View.VISIBLE);
        plus.startAnimation(a);

        Animation b =  AnimationUtils.loadAnimation(this, R.anim.checkbox_slide_up);
        b.setFillAfter(true);
        b.setDuration(1000);

        b.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                loadRewardedVideoAd();
                if(isRewardedVideoLoaded()) {
                    enableVideoButton();
                }else{
                    disableVideoButton(100);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        plus.startAnimation(b);
    }




    private void animateSmokedView(View container, final ImageView smokeHolder){

        ScaleAnimation sa = new ScaleAnimation(3f, 1f, 3f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation a = new AlphaAnimation(0f, 1f);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(sa);
        set.addAnimation(a);
        set.setInterpolator(new AccelerateInterpolator());
        set.setDuration(300);
        set.setFillAfter(true);

        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                SoundPlayer.playSound(ResultActivity.this, SoundPlayer.STAMP);
                playSmoke(smokeHolder);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        container.setVisibility(View.VISIBLE);
        container.startAnimation(set);
    }



    private void playSmoke(final ImageView smokeHolder){
        for(int i=0;i<smokeBitmaps.length;i++){
            final int res = smokeBitmaps[i];
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    smokeHolder.setImageDrawable(ContextCompat.getDrawable(ResultActivity.this, res));
                }
            }, i * 40);
        }
    }


}
