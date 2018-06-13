package net.codecanyon.trimax.wordsearch.activity;


import net.codecanyon.trimax.wordsearch.Config;
import net.codecanyon.trimax.wordsearch.R;

import net.codecanyon.trimax.wordsearch.dialog.HelpDialog;
import net.codecanyon.trimax.wordsearch.game.ContextUtil;

import net.codecanyon.trimax.wordsearch.util.AppRater;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;

import net.codecanyon.trimax.wordsearch.data.WSDatabase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.Locale;




public class IntroActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{


	private GameHelper gameHelper;
	private final static int requestCode = 1;
	private static int animTime = 200;
	private boolean forceIntoAchievements;
	private boolean forceIntoLeaderboard;
	private Resources resources;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_intro);
		boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);
		AVLoadingIndicatorView avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
		avi.setIndicatorColor(ContextCompat.getColor(this, night ? R.color.text_color_n : R.color.secondary_color_dark));

		if(!Config.enableLeaderboardsAndAchievements){
			findViewById(R.id.google_container).setVisibility(View.GONE);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {

				String langCode = Settings.getStringValue(IntroActivity.this, getResources().getString(R.string.pref_key_language), null);

				if(langCode == null){
					Configuration config = getResources().getConfiguration();
					Locale locale;

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						locale = getResources().getConfiguration().getLocales().get(0);
					} else {
						locale = config.locale;
					}

					langCode = locale.getLanguage();

					//Check if this language is within supported languages
					String[] builtInLanguages = getResources().getStringArray(R.array.language_codes);
					boolean found = false;

					for(String code : builtInLanguages){
						if(code.equals(langCode)){
							found = true;
							break;
						}
					}

					if(!found){//make en default
						langCode = Constants.DEFAULT_LANGUAGE;
					}

					Settings.saveStringValue(IntroActivity.this, getResources().getString(R.string.pref_key_language), langCode);
					setLabels();
				}

				select();
				SoundPlayer.initSounds(IntroActivity.this);
				initialized();

			}

		}).start();

	}




	private void initialized(){

		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				Button play = findViewById(R.id.play);
				play.setOnTouchListener(IntroActivity.this);
				play.setOnClickListener(IntroActivity.this);

				findViewById(R.id.config_btn).setOnClickListener(IntroActivity.this);
				findViewById(R.id.rate_btn).setOnClickListener(IntroActivity.this);
				findViewById(R.id.achievements_btn).setOnClickListener(IntroActivity.this);
				findViewById(R.id.leaderboards_btn).setOnClickListener(IntroActivity.this);
				findViewById(R.id.help_btn).setOnClickListener(IntroActivity.this);

				findViewById(R.id.avi).setVisibility(View.GONE);

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						animateUI_1();
					}
				}, 150);
				findViewById(R.id.ray_container).setVisibility(View.VISIBLE);
			}
		});
	}




    @Override
	public void onClick(View view) {
		SoundPlayer.playSound(IntroActivity.this, SoundPlayer.CLICK);
		switch (view.getId()){
			case R.id.config_btn: {
				Intent intent = new Intent(IntroActivity.this, ConfigActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_right_to_left_1, R.anim.slide_right_to_left_2);
				break;
			}
			case R.id.play: {
				Intent intent = new Intent(IntroActivity.this, DifficultyActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_right_to_left_1, R.anim.slide_right_to_left_2);
				break;
			}
			case R.id.rate_btn: {
				final String appPackageName = getPackageName();

				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}

				Settings.saveBooleanValue(this, Constants.DONT_SHOW_RATE_DIALOG_AGAIN, true);
				break;
			}
			case R.id.achievements_btn:{
				if(!isNetworkConnected(this)){
					showNoConnection(this);
					return;
				}

				Settings.saveBooleanValue(this, Constants.DECLINED_TO_SIGN_IN, false);
				if(isSignedIn(gameHelper, this)) {
					startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
				}else{
					forceIntoAchievements = true;
					gameHelper.beginUserInitiatedSignIn();
				}
				break;
			}
			case R.id.leaderboards_btn:{
				if(!isNetworkConnected(this)){
					showNoConnection(this);
					return;
				}

				Settings.saveBooleanValue(this, Constants.DECLINED_TO_SIGN_IN, false);
				if(isSignedIn(gameHelper, this))
					startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), requestCode);
				else {
					forceIntoLeaderboard = true;
					gameHelper.beginUserInitiatedSignIn();
				}
				break;
			}
			case R.id.help_btn:{
				HelpDialog hd = new HelpDialog(this);
				if(!isFinishing())hd.show();
				break;
			}
		}
	}



	private void select(){
		WSDatabase wsd = new WSDatabase(IntroActivity.this);
		wsd.open();
		wsd.close();
	}



	@Override
	protected void onStart() {
		super.onStart();
		if(gameHelper != null) {
			gameHelper.onStart(this);
		}
	}



	@Override
	protected void onStop() {
		super.onStop();
		if(gameHelper != null)
			gameHelper.onStop();
	}



	@Override
	protected void onResume() {
		super.onResume();
		setLabels();
		toggleMode();
	}



	public void setLabels() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final TextView title = (TextView)findViewById(R.id.title);
				final Button play = (Button)findViewById(R.id.play);

				resources = ContextUtil.getCustomResources(IntroActivity.this);
				title.setText(resources.getString(R.string.app_name));
				play.setText(resources.getString(R.string.play));
			}
		});
	}


	public void toggleMode() {
		boolean night = Settings.getBooleanValue(this, Constants.NIGHT_MODE_ON, false);

		View window = this.getWindow().getDecorView();
		ImageView rays = findViewById(R.id.rays);
		TextView title = findViewById(R.id.title);
		Button play = findViewById(R.id.play);

		ImageButton rate_btn = findViewById(R.id.rate_btn);
		ImageButton config_btn = findViewById(R.id.config_btn);
        ImageButton achievements_btn = findViewById(R.id.achievements_btn);
        ImageButton leaderboards_btn = findViewById(R.id.leaderboards_btn);
        ImageButton help_btn = findViewById(R.id.help_btn);

		int home_title_n = ContextCompat.getColor(this, R.color.home_title_n);
		int home_title = ContextCompat.getColor(this, R.color.home_title);

		if(night){
			window.setBackgroundColor(ContextCompat.getColor(this, R.color.app_bg_n));
			rays.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rays_n));
			title.setTextColor(home_title_n);
			play.setTextColor(ContextCompat.getColor(this, R.color.button_text_n));
            play.setBackgroundResource(R.drawable.play_btn_rect_n);
			rate_btn.setImageResource(R.drawable.btn_rate_selector_n);
			config_btn.setImageResource(R.drawable.btn_config_selector_n);
			leaderboards_btn.setImageResource(R.drawable.btn_leaderboards_selector_n);
			achievements_btn.setImageResource(R.drawable.btn_achievements_selector_n);
			help_btn.setImageResource(R.drawable.btn_help_home_selector_n);
		}else{
			window.setBackgroundColor(ContextCompat.getColor(this, R.color.app_bg));
			rays.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.rays));
			title.setTextColor(home_title);
			play.setTextColor(ContextCompat.getColor(this, R.color.button_text));
            play.setBackgroundResource(R.drawable.play_btn_rect);
            rate_btn.setImageResource(R.drawable.btn_rate_selector);
            config_btn.setImageResource(R.drawable.btn_config_selector);
            leaderboards_btn.setImageResource(R.drawable.btn_leaderboards_selector);
            achievements_btn.setImageResource(R.drawable.btn_achievements_selector);
			help_btn.setImageResource(R.drawable.btn_help_home_selector);
		}
	}




	@Override
	public boolean onTouch(View v, MotionEvent motionEvent) {
		int action = motionEvent.getAction();

		switch(v.getId()){
			case R.id.play:
				if (action == MotionEvent.ACTION_DOWN)
					ContextUtil.buttonDown(v);
				if(action == MotionEvent.ACTION_UP)
					ContextUtil.buttonUp(v);
				break;
		}

		return false;
	}



	private void animateUI_1(){
		final View title = findViewById(R.id.title);

		AlphaAnimation a = new AlphaAnimation(0f, 1f);
		a.setInterpolator(new AccelerateDecelerateInterpolator());
		a.setDuration(animTime);

		ScaleAnimation s =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f);
		s.setInterpolator(new AccelerateDecelerateInterpolator());
		s.setDuration(animTime);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				animateUI_2();
			}
		}, animTime );

		title.setVisibility(View.VISIBLE);
		title.startAnimation(a);
		title.startAnimation(s);
	}



	public AnimationSet getBtnAnims(float xAnchor){
		AnimationSet set = new AnimationSet(true);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		set.setDuration(animTime);

		AlphaAnimation a = new AlphaAnimation(0f, 1f);
		ScaleAnimation s =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, xAnchor, Animation.RELATIVE_TO_SELF, 0.0f);

		set.addAnimation(a);
		set.addAnimation(s);

		return set;
	}



	private void animateUI_2(){
		View play = findViewById(R.id.play);

		AlphaAnimation a = new AlphaAnimation(0f, 1f);
		a.setInterpolator(new AccelerateDecelerateInterpolator());
		a.setDuration(animTime);

		ScaleAnimation s =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.0f);
		s.setInterpolator(new AccelerateDecelerateInterpolator());
		s.setDuration(animTime);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				animateUI_3();
			}
		}, animTime );

		play.setVisibility(View.VISIBLE);
		play.startAnimation(a);
		play.startAnimation(s);
	}



	private void animateUI_3(){
		if(Config.enableLeaderboardsAndAchievements) {
			View achievements_btn = findViewById(R.id.achievements_btn);
			achievements_btn.setVisibility(View.VISIBLE);
			achievements_btn.startAnimation(getBtnAnims(1f));

			View leaderboards_btn = findViewById(R.id.leaderboards_btn);
			leaderboards_btn.setVisibility(View.VISIBLE);
			leaderboards_btn.startAnimation(getBtnAnims(0f));

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					animateUI_4();
				}
			}, animTime);
		}else{
			animateUI_4();
		}
	}



	private void animateUI_4(){

		View help_btn = findViewById(R.id.help_btn);
		help_btn.setVisibility(View.VISIBLE);
		help_btn.startAnimation(getBtnAnims(1f));

		View config_btn = findViewById(R.id.config_btn);
		config_btn.setVisibility(View.VISIBLE);
		config_btn.startAnimation(getBtnAnims(0.5f));

		View rate_btn = findViewById(R.id.rate_btn);
		Animation a = getBtnAnims(0f);
		a.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				animateUI_5();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
		rate_btn.setVisibility(View.VISIBLE);
		rate_btn.startAnimation(a);
	}



	private void animateUI_5(){
		final View rayView = findViewById(R.id.rays);
		final RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(50000);
		rotate.setInterpolator(new LinearInterpolator());
		rotate.setRepeatCount(Animation.INFINITE);
		rayView.startAnimation(rotate);

		if(Config.enableLeaderboardsAndAchievements)
			initGms();
		AppRater.app_launched(this);

	}



	private void initGms(){
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);

		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener(){
			@Override
			public void onSignInFailed(){}

			@Override
			public void onSignInSucceeded(){
				if(isSignedIn(gameHelper, IntroActivity.this)) {
					if (forceIntoAchievements) {
						forceIntoAchievements = false;
						startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
					}
					if (forceIntoLeaderboard) {
						forceIntoLeaderboard = false;
						startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), requestCode);
					}
				}
			}
		};

		boolean declined = Settings.getBooleanValue(this, Constants.DECLINED_TO_SIGN_IN, false);
		gameHelper.setConnectOnStart(!declined);
		gameHelper.setup(gameHelperListener);

		if (!declined) {
			if(isNetworkConnected(this)){
				gameHelper.beginUserInitiatedSignIn();
			}
		}
	}




	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}



	public static boolean isSignedIn(GameHelper gameHelper, Context context) {
		if(gameHelper == null)return false;
		return (gameHelper.isSignedIn() && isNetworkConnected(context));
	}



	public static void showNoConnection(final Activity activity){
		final RelativeLayout root = activity.findViewById(R.id.root);
		String nc = "no_connection";
		if(root.findViewWithTag(nc) != null)return;

		final TextView tv = new TextView(activity);
		tv.setTag(nc);
		tv.setTypeface(Typeface.DEFAULT_BOLD);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, activity.getResources().getDimension(R.dimen.no_connection_text_size));
		boolean night = Settings.getBooleanValue(activity, Constants.NIGHT_MODE_ON, false);

		int color = ContextCompat.getColor(activity, night ? R.color.text_color_n:R.color.stripe);
		tv.setTextColor(color);

		tv.setText(ContextUtil.getCustomResources(activity).getString(R.string.no_connection));
		tv.setBackgroundResource(R.drawable.no_connection);
		tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)activity.getResources().getDimension(R.dimen.no_connection_height));
		tv.setLayoutParams(params);

		tv.setVisibility(View.GONE);
		final Animation topAnim = AnimationUtils.loadAnimation(activity, R.anim.no_conn_down);
		root.addView(tv);
		tv.setVisibility(View.VISIBLE);
		tv.startAnimation(topAnim);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(activity != null){
					final Animation up = AnimationUtils.loadAnimation(activity, R.anim.no_conn_up);
					up.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {}

						@Override
						public void onAnimationEnd(Animation animation) {
							root.removeView(tv);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {}
					});
					tv.startAnimation(up);
				}
			}
		}, 2000);
	}




	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(gameHelper != null)gameHelper.onActivityResult(requestCode, resultCode, data);

	}


}



