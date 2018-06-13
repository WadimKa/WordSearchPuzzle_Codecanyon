package net.codecanyon.trimax.wordsearch.game;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;


import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.activity.ConfigActivity;
import net.codecanyon.trimax.wordsearch.data.Settings;

import java.util.HashMap;


public class SoundPlayer {


    public static final int CLICK = R.raw.click;
    public static final int BOARD_FINISHED = R.raw.victory;
    public static final int HINT = R.raw.hint;
    public static final int EARNED_HINTS = R.raw.earned_hints;
    public static final int VICTORY = R.raw.victory;
    public static final int HINT_FINISHED = R.raw.hint_finished;
    public static final int STAMP = R.raw.stamp;
    public static final int TICTAC = R.raw.tictac;
    public static final int TIME_IS_UP = R.raw.time_is_up;

    public static final int s_1 = R.raw.s_1;
    public static final int s_2 = R.raw.s_2;
    public static final int s_3 = R.raw.s_3;
    public static final int s_4 = R.raw.s_4;
    public static final int s_5 = R.raw.s_5;
    public static final int s_6 = R.raw.s_6;
    public static final int s_7 = R.raw.s_7;
    public static final int s_8 = R.raw.s_8;
    public static final int s_9 = R.raw.s_9;

    public static final int w_0 = R.raw.w_0;
    public static final int w_1 = R.raw.w_1;
    public static final int w_2 = R.raw.w_2;
    public static final int w_3 = R.raw.w_3;
    public static final int w_4 = R.raw.w_4;
    public static final int w_5 = R.raw.w_5;
    public static final int w_6 = R.raw.w_6;
    public static final int w_7 = R.raw.w_7;
    public static final int w_8 = R.raw.w_8;

    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;
    public static boolean volumeOn = true;

    public static void initSounds(final Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    createNewSoundPool();
                }else{
                    createOldSoundPool();
                }

                if(soundPool != null) {
                    soundPoolMap = new HashMap<Integer, Integer>();
                    soundPoolMap.put(BOARD_FINISHED, soundPool.load(context, BOARD_FINISHED, 1));

                    soundPoolMap.put(CLICK, soundPool.load(context, CLICK, 1));
                    soundPoolMap.put(HINT, soundPool.load(context, HINT, 1));
                    soundPoolMap.put(EARNED_HINTS, soundPool.load(context, EARNED_HINTS, 1));
                    soundPoolMap.put(VICTORY, soundPool.load(context, VICTORY, 1));
                    soundPoolMap.put(HINT_FINISHED, soundPool.load(context, HINT_FINISHED, 1));
                    soundPoolMap.put(STAMP, soundPool.load(context, STAMP, 1));
                    soundPoolMap.put(TICTAC, soundPool.load(context, TICTAC, 1));
                    soundPoolMap.put(TIME_IS_UP, soundPool.load(context, TIME_IS_UP, 1));

                    soundPoolMap.put(s_1, soundPool.load(context, s_1, 1));
                    soundPoolMap.put(s_2, soundPool.load(context, s_2, 1));
                    soundPoolMap.put(s_3, soundPool.load(context, s_3, 1));
                    soundPoolMap.put(s_4, soundPool.load(context, s_4, 1));
                    soundPoolMap.put(s_5, soundPool.load(context, s_5, 1));
                    soundPoolMap.put(s_6, soundPool.load(context, s_6, 1));
                    soundPoolMap.put(s_7, soundPool.load(context, s_7, 1));
                    soundPoolMap.put(s_8, soundPool.load(context, s_8, 1));
                    soundPoolMap.put(s_9, soundPool.load(context, s_9, 1));

                    soundPoolMap.put(w_0, soundPool.load(context, w_0, 1));
                    soundPoolMap.put(w_1, soundPool.load(context, w_1, 1));
                    soundPoolMap.put(w_2, soundPool.load(context, w_2, 1));
                    soundPoolMap.put(w_3, soundPool.load(context, w_3, 1));
                    soundPoolMap.put(w_4, soundPool.load(context, w_4, 1));
                    soundPoolMap.put(w_5, soundPool.load(context, w_5, 1));
                    soundPoolMap.put(w_6, soundPool.load(context, w_6, 1));
                    soundPoolMap.put(w_7, soundPool.load(context, w_7, 1));
                    soundPoolMap.put(w_8, soundPool.load(context, w_8, 1));
                }
                volumeOn = Settings.getBooleanValue(context, ConfigActivity.SOUND, true);



            }
        }).start();


    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void createNewSoundPool(){

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();

    }


    @SuppressWarnings("deprecation")
    private static void createOldSoundPool(){
        soundPool = new SoundPool(9, AudioManager.STREAM_MUSIC, 0);
    }


    public static void playSound(final Context context, final int soundID) {


        if(soundPool == null || soundPoolMap == null){
            initSounds(context);
        }

        if(volumeOn && soundPool != null && soundPoolMap != null && soundPoolMap.get(soundID) != null && soundPoolMap.get(soundID) > 0) {
            try {
                soundPool.play(soundPoolMap.get(soundID), 1, 1, 1, 0, 1f);
            }catch (Exception e){
                e.printStackTrace();
                Log.d("wordsearch", "Error playing audio: "+e.getMessage());
            }
        }


    }






}
