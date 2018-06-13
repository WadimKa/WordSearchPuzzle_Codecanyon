package net.codecanyon.trimax.wordsearch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.rd.PageIndicatorView;
import com.rd.animation.type.AnimationType;

import net.codecanyon.trimax.wordsearch.Config;
import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.help.HelpAdapter;
import net.codecanyon.trimax.wordsearch.game.SoundPlayer;
import net.codecanyon.trimax.wordsearch.help.HelpPage1;
import net.codecanyon.trimax.wordsearch.help.HelpPage2;
import net.codecanyon.trimax.wordsearch.help.HelpPage3;
import net.codecanyon.trimax.wordsearch.help.HelpPage4;
import net.codecanyon.trimax.wordsearch.help.HelpPage5;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;


import java.util.ArrayList;
import java.util.List;





public class HelpDialog extends Dialog implements View.OnClickListener {


    private Context context;
    private List<View> pagerContent = new ArrayList<>();

    public HelpDialog(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.help_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().getAttributes().windowAnimations = R.style.LanguageDialogAnimation;
        initViews();
    }


    private void initViews() {

        final HelpAdapter adapter = new HelpAdapter();
        adapter.setData(createPageList());

        ViewPager pager = findViewById(R.id.viewPager);
        pager.setAdapter(adapter);


        PageIndicatorView pageIndicatorView = findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setAnimationType(AnimationType.FILL);
        pageIndicatorView.setStrokeWidth(1);

        boolean night = Settings.getBooleanValue(context, Constants.NIGHT_MODE_ON, false);
        if(night){
            pageIndicatorView.setSelectedColor(ContextCompat.getColor(context, R.color.text_color_n));
            pageIndicatorView.setUnselectedColor(ContextCompat.getColor(context, R.color.text_color_n));
            findViewById(R.id.dialog_inner).setBackgroundResource(R.drawable.dialog_bg_n);
        }else{
            pageIndicatorView.setSelectedColor(ContextCompat.getColor(context, R.color.light_brown_text));
            pageIndicatorView.setUnselectedColor(ContextCompat.getColor(context, R.color.light_brown_text));
            findViewById(R.id.dialog_inner).setBackgroundResource(R.drawable.dialog_bg);
        }


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    ((HelpPage1)pagerContent.get(0)).start();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        pageIndicatorView.setViewPager(pager);
        ((HelpPage1)pagerContent.get(0)).start();
    }



    private List<View> createPageList() {

        LayoutInflater inflter = getLayoutInflater();
        HelpPage1 gav = (HelpPage1)inflter.inflate(R.layout.help_page_1,null);

        pagerContent.add(gav);
        HelpPage2 hints = (HelpPage2)inflter.inflate(R.layout.help_page_2, null);

        pagerContent.add(hints);
        HelpPage3 watch = (HelpPage3)inflter.inflate(R.layout.help_page_3, null);
        pagerContent.add(watch);

        if(Config.enableLeaderboardsAndAchievements) {
            HelpPage4 google = (HelpPage4) inflter.inflate(R.layout.help_page_4, null);
            pagerContent.add(google);

            HelpPage5 page5 = (HelpPage5) inflter.inflate(R.layout.help_page_5, null);
            pagerContent.add(page5);
        }

        return pagerContent;
    }




    @Override
    public void onClick(final View view) {
        SoundPlayer.playSound(context, SoundPlayer.CLICK);

    }



    @Override
    public void onBackPressed() {
        dismiss();
    }


}
