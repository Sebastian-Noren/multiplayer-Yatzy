package software.engineering.yatzy.game;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;


import software.engineering.yatzy.R;

/**
 * @author Sebastian Norén <s.norén@gmail.com>
 */

class SoundEngine {
    private String tag = "Info";
    private Context context;
    private static int menuClick;
    private SoundPool soundPool;
    private AudioAttributes audio;
    private MediaPlayer bgSound;
    private int playLength;

    SoundEngine(Context context) {
        this.context = context;
    }

    void buttonClick() {
        if (soundPool != null) {
            soundPool.play(menuClick, 1, 1, 0, 0, 1);
        }
    }

    void createApplicationSound() {
        if (soundPool == null) {
            audio = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder().setAudioAttributes(audio).setMaxStreams(10).build();
            menuClick = soundPool.load(context, R.raw.click, 1);
        }
    }

    void stopApplicationEffectSound() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            audio = null;
        }
    }

    void createGameBgSound() {
        try {
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    if (bgSound == null) {
                        bgSound = MediaPlayer.create(context, R.raw.game_music);
                        bgSound.start();
                        bgSound.setLooping(true);
                    }
                } // Exit Run
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(tag, "createGameBgSound: Thread fail" );
        }
    }

    void stopGameBgSound() {
        if (bgSound != null) {
            bgSound.stop();
            bgSound.release();
            bgSound = null;
        }
    }

   boolean isSoundOn(){
        if (bgSound !=null) {
           return bgSound.isPlaying();
        }
        return false;
    }

    void resumeGameBgSound() {
        if (bgSound != null) {
            bgSound.seekTo(playLength);
            bgSound.start();
        }
    }

    void pauseGameBgSound() {
        if (bgSound != null) {
            bgSound.pause();
            playLength = bgSound.getCurrentPosition();
        }
    }


}
