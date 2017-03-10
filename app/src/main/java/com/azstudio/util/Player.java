package com.azstudio.util;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener {

    public MediaPlayer mediaPlayer; // 媒体播放器
    public long pos;
    public int Percent;
    public String Playtime;
    private SeekBar seekBar; // 拖动条
    //public int itemIndex;
    //public ListView mListView;
    private ProgressBar progressBar, progressBar2; // 进度条
    private TextView text_time; // 进度条
    private Timer mTimer = new Timer(); // 计时器

    public Player() {
        super();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 初始化播放器
    public Player(SeekBar seekBar) {
        super();
        this.seekBar = seekBar;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次
        mTimer.schedule(timerTask, 0, 1000);
    }

    public Player(ProgressBar progressBar, TextView time) {
        super();
        this.progressBar = progressBar;
        this.text_time = time;
        //this.progressBar2.setTag(MainActivity.current_playposition);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次
        mTimer.schedule(timerTask, 0, 1000);
    }

    // 计时器
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            //if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
            if (mediaPlayer.isPlaying()){//&& view.getTag().equals(MainActivity.current_playposition) ) {
                handler.sendEmptyMessage(0); // 发送消息
            }
        }
    };

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (duration > 0) {
                pos = progressBar.getMax() * position / duration;
                progressBar.setProgress((int) pos);
                String current_time = s2t(position);
                text_time.setText( current_time +" / " + Playtime);
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                //long pos = seekBar.getMax() * position / duration;
                //seekBar.setProgress((int) pos);
                //int firstVisiblePosition = mListView.getFirstVisiblePosition();
                //int lastVisiblePosition = mListView.getLastVisiblePosition();
                /*
                if(itemIndex>=firstVisiblePosition && itemIndex<=lastVisiblePosition){
                    //得到你需要更新item的View
                    View view = mListView.getChildAt(itemIndex - firstVisiblePosition);
                    progressBar = (ProgressBar) findViewById(R.id.music_progress_bar);
                    //TextView d = (TextView) view.findViewById(R.id.btn_download);
                    //d.setText("暂停");
                    pos = progressBar.getMax() * position / duration;
                    progressBar.setProgress((int) pos);
                }*/


		        /*
				pos = progressBar.getMax() * position / duration;
				if ( progressBar.getTag().equals(MainActivity.current_playposition) ){
					progressBar.setProgress((int) pos);
				}
				else {
					progressBar.setProgress(0);
				}
				*/

            }
        };
    };

    public void refreshbar( ProgressBar bar) {
        this.progressBar = bar;
        this.progressBar.setProgress(0);
        this.progressBar.setSecondaryProgress(0);
    }

    public void play() {
        mediaPlayer.start();
    }

    public String getPlaytime() {
        return Playtime;
    }

    /**
     *
     * @param url
     *            url地址
     */
    public void playUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 暂停
    public void pause() {
        mediaPlayer.pause();
    }

    // 停止
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Playtime = s2t(mp.getDuration()) ;
        //text_time.setText( "00:00:00 / " + Playtime);
        mp.start();
        Log.e("mediaPlayer", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
    }

    public String s2t(int ms){
        int second =  ms / 1000;
        int hh = second/3600;
        int mm = (second%3600) / 60;
        int ss = second - hh * 3600 - mm * 60;
        String t = String.format( "%02d", hh) + ':' +  String.format( "%02d", mm)  + ':' +  String.format( "%02d", ss) ;
        return t;
    }

    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //seekBar.setSecondaryProgress(percent);
        //int currentProgress = seekBar.getMax()
        Percent = percent;
        //progressBar.setSecondaryProgress(percent);
        //int currentProgress = progressBar.getMax()
        //        * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        //Log.e(currentProgress + "% play", percent + " buffer");


    }

    //public void setListView(ListView listView) {    this.mListView = listView;}

}
