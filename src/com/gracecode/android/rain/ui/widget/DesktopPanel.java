package com.gracecode.android.rain.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.helper.SendBroadcastHelper;
import com.gracecode.android.rain.helper.TypefaceHelper;
import com.gracecode.android.rain.serivce.PlayService;
import com.gracecode.android.rain.ui.MainActivity;

public class DesktopPanel extends AppWidgetProvider {
    private static final String ACTION_STOP = "com.gracecode.android.rain.intent.action.STOP";
    private static final String ACTION_PLAY = "com.gracecode.android.rain.intent.action.PLAY";
    private Intent mServiceIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(ACTION_STOP)) {
            onStop(context);
        }

        if (intent.getAction().equals(ACTION_PLAY)) {
            onPlay(context);
        }
    }

    private void onStop(Context context) {
        SendBroadcastHelper.sendStopBroadcast(context);
        if (mServiceIntent != null) {
            try {
                context.stopService(mServiceIntent);
            } catch (RuntimeException e) {
                // ...
            }
        }
    }


    private void onPlay(Context context) {
        mServiceIntent = new Intent(context, PlayService.class);
        context.startService(mServiceIntent);

        try {
            // @todo Send broadcast when service is started.
            Thread.sleep(500);
            SendBroadcastHelper.sendPlayBroadcast(context);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        remoteViews.setImageViewBitmap(R.id.launch_activity, TypefaceHelper.getStringBitmapFromTypeface(
                context.getString(R.string.icon_rain), TypefaceHelper.getTypefaceWeather(context), 80, 80
        ));


        bindLaunchMainActivity(context, remoteViews);
        bindLaunchCommand(context, remoteViews);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    private void bindLaunchCommand(Context context, RemoteViews remoteViews) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, new Intent().setAction(ACTION_PLAY),
                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(
                context,
                0, new Intent().setAction(ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.launch_play, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.launch_stop, pendingIntent2);
    }


    public void bindLaunchMainActivity(Context context, RemoteViews remoteViews) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.launch_activity, pendingIntent);
    }
}
