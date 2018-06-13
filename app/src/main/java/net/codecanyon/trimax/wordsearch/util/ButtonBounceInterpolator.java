package net.codecanyon.trimax.wordsearch.util;



public class ButtonBounceInterpolator implements android.view.animation.Interpolator {

    double mAmplitude = 1;
    double mFrequency = 10;


    public ButtonBounceInterpolator(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }


    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}