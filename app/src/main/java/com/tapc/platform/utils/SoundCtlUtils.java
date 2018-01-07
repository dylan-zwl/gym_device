package com.tapc.platform.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SoundEffectConstants;

/**
 * Created by Administrator on 2016/11/13.
 */
public class SoundCtlUtils {
    private static SoundCtlUtils sSoundCtlUtils;
    private AudioManager mAudioManager;
    private int mSaveVolume;

    public static SoundCtlUtils getInstance() {
        if (sSoundCtlUtils == null) {
            synchronized (SoundCtlUtils.class) {
                if (sSoundCtlUtils == null) {
                    sSoundCtlUtils = new SoundCtlUtils();
                }
            }
        }
        return sSoundCtlUtils;
    }

    public void init(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public AudioManager getAudioManager() {
        return mAudioManager;
    }

    public int getMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public int getVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void openVolume() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager
                .FLAG_SHOW_UI);
    }

    public void setVolume(int volume) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_VIBRATE);
    }

    public void addVolume() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager
                .FLAG_SHOW_UI);
    }

    public void subVolume() {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager
                .FLAG_SHOW_UI);
    }

    public boolean isEnble() {
        if (mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            return false;
        } else {
            return true;
        }
    }

    public void setSoundEnble(boolean enble) {
        if (enble) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mSaveVolume, AudioManager.FLAG_SHOW_UI);
        } else {
            mSaveVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
        openVolume();
    }

    public void clickSound(Context context) {
        mAudioManager.playSoundEffect(SoundEffectConstants.CLICK);
    }

    private void releasePlayer(MediaPlayer players) {
        if (players != null) {
            players.release();
        }
    }

    public void playBeep(final Context context, final int rid) {
        new Thread(new Runnable() {
            public void run() {
                MediaPlayer mediaPlayer = null;
                try {
                    if (rid != 0) {
                        mediaPlayer = MediaPlayer.create(context.getApplicationContext(), rid);
                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer players) {
                                    releasePlayer(players);
                                }
                            });
                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer players, int arg1, int arg2) {
                                    releasePlayer(players);
                                    return false;
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    releasePlayer(mediaPlayer);
                }
            }
        }).start();
    }
}
