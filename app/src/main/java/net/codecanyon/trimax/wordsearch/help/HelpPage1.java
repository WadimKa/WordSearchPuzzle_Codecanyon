package net.codecanyon.trimax.wordsearch.help;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import net.codecanyon.trimax.wordsearch.R;



public class HelpPage1 extends Help {


    private int[] frames = {
                R.drawable.grid_anim_0001,
                R.drawable.grid_anim_0002,
                R.drawable.grid_anim_0003,
                R.drawable.grid_anim_0004,
                R.drawable.grid_anim_0005,
                R.drawable.grid_anim_0006,
                R.drawable.grid_anim_0007,
                R.drawable.grid_anim_0008,
                R.drawable.grid_anim_0009,
                R.drawable.grid_anim_0010,
                R.drawable.grid_anim_0011,
                R.drawable.grid_anim_0012,
                R.drawable.grid_anim_0013,
                R.drawable.grid_anim_0014,
                R.drawable.grid_anim_0015,
                R.drawable.grid_anim_0016,
                R.drawable.grid_anim_0017,
                R.drawable.grid_anim_0018,
                R.drawable.grid_anim_0019,
                R.drawable.grid_anim_0020,
                R.drawable.grid_anim_0021,
                R.drawable.grid_anim_0022,
                R.drawable.grid_anim_0023,
                R.drawable.grid_anim_0024,
                R.drawable.grid_anim_0025,
                R.drawable.grid_anim_0026,
                R.drawable.grid_anim_0027,
                R.drawable.grid_anim_0028,
                R.drawable.grid_anim_0029,
                R.drawable.grid_anim_0030,
                R.drawable.grid_anim_0031,
                R.drawable.grid_anim_0032,
                R.drawable.grid_anim_0033,
                R.drawable.grid_anim_0034,
                R.drawable.grid_anim_0035,
                R.drawable.grid_anim_0036,
                R.drawable.grid_anim_0037,
                R.drawable.grid_anim_0038,
                R.drawable.grid_anim_0039,
                R.drawable.grid_anim_0040
    };

    public HelpPage1(Context context) {
        super(context);
    }

    public HelpPage1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HelpPage1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ((TextView)findViewById(R.id.title)).setText(resources.getString(R.string.how_to_play_title));
        ((TextView)findViewById(R.id.text)).setText(resources.getString(R.string.how_to_play_text));
    }



    public void start(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final ImageView iv = findViewById(R.id.anim_view);

                for(int i=0;i<frames.length;i++){
                    final int res = frames[i];
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageDrawable(ContextCompat.getDrawable(context, res));
                        }
                    }, i * 50);
                }
            }
        }, 300);
    }

}
