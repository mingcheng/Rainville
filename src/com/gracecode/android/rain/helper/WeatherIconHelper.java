package com.gracecode.android.rain.helper;

import android.animation.ObjectAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * User: feelinglucky
 * Date: 15/1/6
 */
public class WeatherIconHelper {
    private static final long DURATION = 10 * 1000;
    private final TextView mWeatherTextView;
    private String[] mWeatherTitle = new String[]{"晴", "云", "阴", "雨", "雷", "雪", "雾", "冻", "尘", "霾"};
    private String[] mWeatherCharacter = new String[]{"\uf00d", "\uf002", "\uf013", "\uf017",
            "\uf016", "\uf01b", "\uf063", "\uf076", "\uf062", "\uf014"};

    private String mCondition;

    public WeatherIconHelper(TextView weatherTextView) {
        mWeatherTextView = weatherTextView;
    }

    public void hide() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mWeatherTextView, "alpha", 1f, 0f);
        anim.setDuration(DURATION);
        anim.setInterpolator(new BounceInterpolator());
        anim.start();
    }

    public void show() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mWeatherTextView, "alpha", 0f, 1f);
        anim.setDuration(DURATION);
        anim.setInterpolator(new BounceInterpolator());
        anim.start();
    }

    public void setWeather(String condition) {
        mCondition = condition;
        for (int i = mWeatherTitle.length - 1; i >= 0; i--) {
            if (condition.contains(mWeatherTitle[i])) {
                mWeatherTextView.setText(mWeatherCharacter[i]);
                return;
            }
        }
    }
}
