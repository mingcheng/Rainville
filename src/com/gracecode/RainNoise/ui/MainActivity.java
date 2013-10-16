package com.gracecode.RainNoise.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.gracecode.RainNoise.R;
import com.gracecode.RainNoise.serivce.PlayerService;
import com.gracecode.RainNoise.ui.fragment.FrontPanelFragment;
import com.gracecode.RainNoise.ui.widget.SimplePanel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private List<SeekBar> seekBars = new ArrayList<SeekBar>();
    private SimplePanel mFrontPanel;
    private AudioManager mAudioManager;
    private LinearLayout mMixer;
    private int mScreenHeight;
    private FrontPanelFragment mFrontPanelFragmentFragment;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        this.mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//        findViewById(R.id.play).setOnClickListener(this);
//        findViewById(R.id.stop).setOnClickListener(this);
//        findViewById(R.id.reset).setOnClickListener(this);
//
//        startService(new Intent(this, PlayerService.class));

//        initMixer();

        mFrontPanel = (SimplePanel) findViewById(R.id.front_panel);
        mFrontPanelFragmentFragment = new FrontPanelFragment();

        mMixer = (LinearLayout) findViewById(R.id.mixer);
        mFrontPanelFragmentFragment.setFrontPanel(mFrontPanel);
        mFrontPanel.addSimplePanelListener(mFrontPanelFragmentFragment);

        getFragmentManager()
                .beginTransaction()
                .add(R.id.front_panel, mFrontPanelFragmentFragment)
                .commit();
    }



    @Override
    protected void onResume() {
        super.onResume();
//        bindService(new Intent(this, PlayerService.class), mConnection, Context.BIND_AUTO_CREATE);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mScreenHeight = displaymetrics.heightPixels;

        float v = mScreenHeight * (1 - mFrontPanel.getSlideRatio());

        mMixer.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) v));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        reSetMixer();

//                      getWindowManager();
//        mBound = (int) (mScreenHeight * 0.6);
//        Log.e(TAG, "Screen size is " + width + ", " + height);
//        mMask.setLayoutParams(new FrameLayout.LayoutParams(width, height));


    }

    @Override
    protected void onPause() {
        super.onPause();
//        unbindService(mConnection);
    }

    private PlayerService.MyBinder mBinder;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mBinder = (PlayerService.MyBinder) binder;
        }

        public void onServiceDisconnected(ComponentName className) {
            mBinder = null;
        }
    };

}
