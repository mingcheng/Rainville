package com.gracecode.android.rain.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TypefaceHelper {

    private static Typeface mTypefaceMusket2;
    private static Typeface mTypefaceWeather;
    private static Typeface mTypefaceElegant;

    public static void setAllTypeface(ViewGroup view, final Typeface typeface) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View v = view.getChildAt(i);
            if (v instanceof ViewGroup) {
                setAllTypeface((ViewGroup) v, typeface);
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(typeface);
            }
        }
    }

    public static Typeface getTypefaceMusket2(Context context) {
        if (mTypefaceMusket2 == null) {
            // http://bybu.es/portfolio/musket/
            mTypefaceMusket2 = Typeface.createFromAsset(context.getAssets(), "musket2.otf");
        }

        return mTypefaceMusket2;
    }


    public static Typeface getTypefaceWeather(Context context) {
        if (mTypefaceWeather == null) {
            mTypefaceWeather = Typeface.createFromAsset(context.getAssets(), "weather.ttf");
        }

        return mTypefaceWeather;
    }


    public static Typeface getTypefaceElegant(Context context) {
        if (mTypefaceElegant == null) {
            mTypefaceElegant = Typeface.createFromAsset(context.getAssets(), "elegant.ttf");
        }

        return mTypefaceElegant;
    }
}
