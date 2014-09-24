package info.zhegui.disconalert;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

public class MyService extends Service implements MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener  {

    MediaPlayer mMediaPlayer = null;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("onStartCommand("+intent+","+flags+","+startId+")");

        if (intent != null && TextUtils.equals(intent.getAction(), "play_alert_sound")) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // could not get audio focus.
            }else {

                initMediaPlayer(); // initialize it here
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.prepareAsync(); // prepare async to not block main thread
            }
        }

        return Service.START_NOT_STICKY;
    }

    private void initMediaPlayer(){
        mMediaPlayer=new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(getApplicationContext(),
                    Uri.parse("android.resource://"+this.getApplication().getPackageName() +"/" + R.raw.sound_alert));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        log("onPrepared("+mp+")");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy()");
        if (mMediaPlayer != null) mMediaPlayer.release();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
//        log("onAudioFocusChange("+focusChange+")");
//        switch (focusChange) {
//            case AudioManager.AUDIOFOCUS_GAIN:
//                // resume playback
//                if (mMediaPlayer == null) {
//                    initMediaPlayer();
//                    mMediaPlayer.setOnPreparedListener(this);
//                    mMediaPlayer.prepareAsync(); // prepare async to not block main thread
//                } else if (!mMediaPlayer.isPlaying()){
//                    mMediaPlayer.start();
//                }
//
//                mMediaPlayer.setVolume(1.0f, 1.0f);
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS:
//                // Lost focus for an unbounded amount of time: stop playback and release media player
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
//                mMediaPlayer.release();
//                mMediaPlayer = null;
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                // Lost focus for a short time, but we have to stop
//                // playback. We don't release the media player because playback
//                // is likely to resume
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
//                break;
//
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                // Lost focus for a short time, but it's ok to keep playing
//                // at an attenuated level
//                if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
//                break;
//        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        log("onError("+mp+","+what+","+extra+")");
        mp.reset();

        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        log("onCompletion("+mp+")");
        stopSelf();
    }

    private void log(String text) {
        Log.d("MyService", "Mobile  ...." + text);
    }
}
