package net.codecanyon.trimax.wordsearch.game;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.util.ButtonBounceInterpolator;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;

import java.util.Locale;


public class ContextUtil {


    public static Resources getCustomResources(Context ctx){

        Resources standardResources = ctx.getResources();
        AssetManager assets = standardResources.getAssets();
        DisplayMetrics metrics = standardResources.getDisplayMetrics();
        Configuration config = new Configuration(standardResources.getConfiguration());
        String code = Settings.getStringValue(ctx, ctx.getResources().getString(R.string.pref_key_language), Constants.DEFAULT_LANGUAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(new Locale(code));
        } else {
            config.locale = new Locale(code);
        }

        Resources resources;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Context context  = ctx.createConfigurationContext(config);//BURASI TEST EDİLMELİ
            resources = context.getResources();
        } else {
            resources = new Resources(assets, metrics, config);
        }

        return resources;

    }



    public static void buttonDown(View v){
        Animation scale = new ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(850);
        ButtonBounceInterpolator interpolator = new ButtonBounceInterpolator(0.2, 20);
        scale.setInterpolator(interpolator);
        scale.setFillAfter(true);
        v.startAnimation(scale);
    }



    public static void buttonUp(View v){
        Animation scale = new ScaleAnimation(.9f, 1f, .9f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(850);
        ButtonBounceInterpolator interpolator = new ButtonBounceInterpolator(0.2, 20);
        scale.setInterpolator(interpolator);
        scale.setFillAfter(true);
        v.startAnimation(scale);
    }
}
