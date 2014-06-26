package com.gracecode.android.rain.player;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import com.gracecode.android.rain.BuildConfig;
import com.gracecode.android.rain.helper.MixerPresetsHelper;
import org.xiph.vorbis.decoder.DecodeFeed;
import org.xiph.vorbis.decoder.DecodeStreamInfo;
import org.xiph.vorbis.decoder.VorbisDecoder;

import java.io.IOException;
import java.io.InputStream;

public final class BufferedPlayer implements DecodeFeed, Runnable {
    public static final String TAG = BufferedPlayer.class.getName();
    public static final float DEFAULT_VOLUME_PERCENT = MixerPresetsHelper.DEFAULT_PRESET[0];
    private static final long TOTAL_DELAY_TIME = 2000;
    private boolean looping = false;

    private final Context mContext;
    private InputStream mInputStream;

    private float leftVolume = DEFAULT_VOLUME_PERCENT;
    private float rightVolume = DEFAULT_VOLUME_PERCENT;

    private AudioTrack mAudioTrack;

    BufferedPlayer(Context context, int raw) {
        mContext = context.getApplicationContext();
//        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        mInputStream = mContext.getResources().openRawResource(raw);
        if (mInputStream.markSupported()) {
            mInputStream.mark(0);
        }
    }

    public void play() {
        new Thread(this).start();
    }

    public void setLooping(boolean flag) {
        looping = flag;
    }

    @Override
    public int readVorbisData(byte[] buffer, int amountToWrite) {
        try {
            int read = mInputStream.read(buffer, 0, amountToWrite);
            if (read == -1) {
                mInputStream.reset();
            }
        } catch (IOException e) {
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }

        return amountToWrite;
    }

    @Override
    public void writePCMData(short[] pcmData, int amountToRead) {
        try {
            if (mAudioTrack != null)
                mAudioTrack.write(pcmData, 0, amountToRead);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {
        if (mAudioTrack != null) {
            try {
                mAudioTrack.stop();
                mAudioTrack.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                mAudioTrack = null;
            }
        }
    }


    @Override
    public void startReadingHeader() {
//        try {
//            mAudioTrack.play();
//            mAudioTrack.setStereoVolume(leftVolume, rightVolume);
//        } catch (RuntimeException e) {
//            Log.e(TAG, e.getMessage());
//        }
    }

    @Override
    public void start(DecodeStreamInfo decodeStreamInfo) {
        int channelConfiguration = (decodeStreamInfo.getChannels() == 1)
                ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;


        int minSize = AudioTrack.getMinBufferSize((int) decodeStreamInfo.getSampleRate(),
                channelConfiguration, AudioFormat.ENCODING_PCM_16BIT);

        try {
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    (int) decodeStreamInfo.getSampleRate(),
                    channelConfiguration, AudioFormat.ENCODING_PCM_16BIT,
                    minSize,
                    AudioTrack.MODE_STREAM);
            setStereoVolume(leftVolume, rightVolume);

            mAudioTrack.play();
        } catch (RuntimeException e) {
            // ...
        }
    }

    public boolean isPlaying() {
        return (mAudioTrack != null) && (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING);
    }

    public synchronized int setStereoVolume(float a, float b) {
        try {
            leftVolume = a;
            rightVolume = b;
            return mAudioTrack.setStereoVolume(a, b);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    @Override
    public void run() {
        try {
            long delay = getRandomDelayTime();
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "Delay " + delay + "ms, from steam " + mInputStream.toString());
            }
            Thread.sleep(delay);

            // Decode and playing
            do {
                VorbisDecoder.startDecoding(this);
            } while (looping);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private long getRandomDelayTime() {
        return (long) (TOTAL_DELAY_TIME * Math.random());
    }

    public void shutdown() {
        try {
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        looping = false;
        stop();
    }
}
