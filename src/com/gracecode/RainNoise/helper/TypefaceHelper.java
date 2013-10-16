package com.gracecode.RainNoise.helper;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TypefaceHelper {

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
}
