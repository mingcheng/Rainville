package com.gracecode.android.rain.helper;

import android.content.Context;
import android.graphics.*;

public class TypefaceHelper {

    private static Typeface mTypefaceMusket2;
    private static Typeface mTypefaceWeather;
    private static Typeface mTypefaceElegant;
    private static Typeface mTypefaceRoboto;

    public static Typeface getTypefaceMusket2(Context context) {
        if (mTypefaceMusket2 == null) {
            // http://bybu.es/portfolio/musket/
            mTypefaceMusket2 = Typeface.createFromAsset(context.getAssets(), "musket2.otf");
        }

        return mTypefaceMusket2;
    }


    public static Typeface getTypefaceWeather(Context context) {
        if (mTypefaceWeather == null) {
            mTypefaceWeather = Typeface.createFromAsset(context.getAssets(), "weather.otf");
        }

        return mTypefaceWeather;
    }


    public static Typeface getTypefaceElegant(Context context) {
        if (mTypefaceElegant == null) {
            mTypefaceElegant = Typeface.createFromAsset(context.getAssets(), "elegant.ttf");
        }

        return mTypefaceElegant;
    }


    public static Bitmap getStringBitmapFromTypeface(String string, Typeface typeface, int width, int height) {
        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        float textSize = width * .7f;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.LEFT);
        myCanvas.drawText(string, width * .1f, (height - textSize) / 2 + height / 2,
                paint);
        return myBitmap;
    }
}
