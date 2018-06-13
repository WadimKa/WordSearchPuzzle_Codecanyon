package net.codecanyon.trimax.wordsearch.game;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import net.codecanyon.trimax.wordsearch.activity.GameActivity;


public class WordView extends android.support.v7.widget.AppCompatTextView {


    private Word word;
    private boolean stillAlpha;


    public WordView(Context context) {
        super(context);
    }

    public WordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setWord(Word word){
        this.word = word;

        if(word != null) {
            setText(word.getText());

            if (word.isSolved() && !word.striked) {
                strike();
            } else {
                unstrike();
            }

        }
    }

    public void strike(){

        if(word != null && word.isSolved() && word.striked){
            stillAlpha = false;

            invalidate();
            return;
        }

        if(word != null) {
            stillAlpha = false;
            word.striked = true;
            animate().alpha(0.2f).setDuration(300).start();
        }
    }



    public void unstrike(){
        stillAlpha = true;
        setPaintFlags(getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {

        if(word != null && word.isSolved()) {
            setPaintFlags(getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            super.onDraw(canvas);
            if(stillAlpha && word.striked && word.isSolved()){
                 animate().alpha(0.2f).setDuration(0).start();
                stillAlpha = false;
            }
        }else{
            stillAlpha = false;
            animate().alpha(1f).start();
            super.onDraw(canvas);
        }
    }

}
