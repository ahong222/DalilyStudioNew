package com.syh.dalilystudio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

@TargetApi(21)
@SuppressLint("NewApi")
public class SoundPoolWrapper {

    private static int MAX_STREAMS = 5;
    
    private static SoundPool mSoundPool;
    private static SoundPoolWrapper instance;
 
    public static SoundPoolWrapper getInstance() {
        if (instance == null) {
            synchronized (SoundPoolWrapper.class) {
                if (instance == null) {
                    instance = new SoundPoolWrapper();
                }
            }
        }
        return instance;
    }

    private SoundPoolWrapper() {
        if(Build.VERSION.SDK_INT>=21){
            AudioAttributes attributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            mSoundPool = new SoundPool.Builder().setMaxStreams(MAX_STREAMS).setAudioAttributes(attributes).build();
        } else {
            mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_NOTIFICATION, 0);
        }
    }

    public void loadRing(Context context,int resId){
        mSoundPool.load(context, resId, 1);
    }
}
