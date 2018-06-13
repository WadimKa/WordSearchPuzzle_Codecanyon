package net.codecanyon.trimax.wordsearch.game;

import android.content.Context;
import android.view.View;

import net.codecanyon.trimax.wordsearch.R;
import net.codecanyon.trimax.wordsearch.data.Constants;
import net.codecanyon.trimax.wordsearch.data.Settings;

import java.text.Collator;
import java.util.Locale;


public class Word implements Comparable<Word> {


    private int color;
    private String text;
    private int y, x;
    private Direction direction;
    private Context context;
    private boolean solved;
    private boolean revealed;
    private boolean jumping;
    public View firstLetter;
    public boolean striked;

    public Word(String word, int row, int col, Direction dir, Context context) {
        super();
        text = word;
        y = row;
        x = col;
        direction = dir;
        this.context = context;
        solved = false;
        revealed = false;
        jumping = false;
    }



    public int compareTo(Word another) {
        Locale locale;
        try {
            locale = new Locale(Settings.getStringValue(context, context.getResources().getString(R.string.pref_key_language), Constants.DEFAULT_LANGUAGE));
        }catch (Exception e){
            locale = Locale.ENGLISH;
        }


        Collator collator = Collator.getInstance(locale);
        return collator.compare(text.toLowerCase(locale), another.getText().toLowerCase(locale));
    }




    public void setDirection(Direction direction){
        this.direction = direction;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int col) {
        color = col;
    }


    public String getText() {
        return text;
    }


    public int getY() {
        return y;
    }


    public int getX() {
        return x;
    }


    public Direction getDirection() {
        return direction;
    }


    public boolean isSolved(){
        return solved;
    }

    public void setSolved(boolean solved){
        this.solved = solved;
    }


    public boolean isRevealed(){
        return revealed;
    }

    public void setRevealed(boolean revealed){
        this.revealed = revealed;
    }

    public boolean isJumping(){
        return jumping;
    }

    public void setJumping(boolean jumping){
        this.jumping = jumping;
    }

	@Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return getText()+", x:"+getX()+", y:"+getY();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Word other = (Word) obj;

        String otherAsReverse = new StringBuilder(other.text).reverse().toString();


        if (this.text.equals(otherAsReverse))
            return true;

        if(this.text.contains(other.text))
            return true;

        if(other.text.contains(this.text))
            return true;

        if(this.text.contains(otherAsReverse))
            return true;

        if(other.text.contains(new StringBuilder(this.text).reverse().toString()))
            return true;


        if (x != other.x)
            return false;
        if (direction != other.direction)
            return false;
        if (y != other.y)
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }

}
