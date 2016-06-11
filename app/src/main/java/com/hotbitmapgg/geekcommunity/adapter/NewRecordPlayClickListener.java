package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.View;
import android.widget.ImageView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobUtils;


public class NewRecordPlayClickListener implements View.OnClickListener
{

    BmobMsg message;

    ImageView iv_voice;

    private AnimationDrawable anim = null;

    Context context;

    String currentObjectId = "";

    MediaPlayer mediaPlayer = null;

    public static boolean isPlaying = false;

    public static NewRecordPlayClickListener currentPlayListener = null;

    // 用于区分两个不同语音的播放
    static BmobMsg currentMsg = null;

    BmobUserManager userManager;

    public NewRecordPlayClickListener(Context context, BmobMsg msg, ImageView voice)
    {

        this.iv_voice = voice;
        this.message = msg;
        this.context = context;
        currentMsg = msg;
        currentPlayListener = this;
        currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
        userManager = BmobUserManager.getInstance(context);
    }

    /**
     * 播放语音
     *
     * @param @param filePath
     * @param @param isUseSpeaker
     * @return void
     * @throws
     * @Title: playVoice
     * @Description: TODO
     */
    @SuppressWarnings("resource")
    public void startPlayRecord(String filePath, boolean isUseSpeaker)
    {

        if (!(new File(filePath).exists()))
        {
            return;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        if (isUseSpeaker)
        {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else
        {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }

        // while (true) {
        // try {
        // mediaPlayer.reset();
        // FileInputStream fis = new FileInputStream(new File(filePath));
        // mediaPlayer.setDataSource(fis.getFD());
        // mediaPlayer.prepare();
        // break;
        // } catch (IllegalArgumentException e) {
        // } catch (IllegalStateException e) {
        // } catch (IOException e) {
        // }
        // }
        //
        // isPlaying = true;
        // currentMsg = message;
        // mediaPlayer.start();
        // startRecordAnimation();
        // mediaPlayer.setOnCompletionListener(new
        // MediaPlayer.OnCompletionListener() {
        //
        // @Override
        // public void onCompletion(MediaPlayer mp) {
        // // TODO Auto-generated method stub
        // stopPlayRecord();
        // }
        //
        // });
        // currentPlayListener = this;

        try
        {
            mediaPlayer.reset();
            // 单独使用此方法会报错播放错误:setDataSourceFD failed.: status=0x80000000
            // mediaPlayer.setDataSource(filePath);
            // 因此采用此方式会避免这种错误
            FileInputStream fis = new FileInputStream(new File(filePath));
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new OnPreparedListener()
            {

                @Override
                public void onPrepared(MediaPlayer mediaPlayer)
                {
                    // TODO Auto-generated method stub
                    isPlaying = true;
                    currentMsg = message;
                    mediaPlayer.start();
                    startRecordAnimation();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    // TODO Auto-generated method stub
                    stopPlayRecord();
                }
            });
            currentPlayListener = this;
            // isPlaying = true;
            // currentMsg = message;
            // mediaPlayer.start();
            // startRecordAnimation();
        } catch (Exception e)
        {
            LogUtil.lsw(e.getMessage());
        }
    }

    /**
     * 停止播放
     *
     * @param
     * @return void
     * @throws
     * @Title: stopPlayRecord
     * @Description: TODO
     */
    public void stopPlayRecord()
    {

        stopRecordAnimation();
        if (mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
    }

    /**
     * 开启播放动画
     *
     * @param
     * @return void
     * @throws
     * @Title: startRecordAnimation
     * @Description: TODO
     */
    private void startRecordAnimation()
    {

        if (message.getBelongId().equals(currentObjectId))
        {
            iv_voice.setImageResource(R.drawable.anim_chat_voice_right);
        } else
        {
            iv_voice.setImageResource(R.drawable.anim_chat_voice_left);
        }
        anim = (AnimationDrawable) iv_voice.getDrawable();
        anim.start();
    }

    /**
     * 停止播放动画
     *
     * @param
     * @return void
     * @throws
     * @Title: stopRecordAnimation
     * @Description: TODO
     */
    private void stopRecordAnimation()
    {

        if (message.getBelongId().equals(currentObjectId))
        {
            iv_voice.setImageResource(R.drawable.chat_voice_to_3);
        } else
        {
            iv_voice.setImageResource(R.drawable.chat_voice_from_3);
        }
        if (anim != null)
        {
            anim.stop();
        }
    }

    @Override
    public void onClick(View view)
    {

        if (isPlaying)
        {
            currentPlayListener.stopPlayRecord();
            if (currentMsg != null && currentMsg.hashCode() == message.hashCode())
            {
                currentMsg = null;
                return;
            }
        }

        if (message.getBelongId().equals(currentObjectId))
        {
            // 如果是自己发送的语音消息，则播放本地地址
            String localPath = message.getContent().split("&")[0];
            startPlayRecord(localPath, true);
        } else
        {
            // 如果是收到的消息，则需要先下载后播放
            String localPath = getDownLoadFilePath(message);
            LogUtil.lsw("语音存放路径 : " + localPath);
            startPlayRecord(localPath, true);
        }
    }

    public String getDownLoadFilePath(BmobMsg msg)
    {

        String accountDir = BmobUtils.string2MD5(userManager.getCurrentUserObjectId());
        File dir = new File(BmobConfig.BMOB_VOICE_DIR + File.separator + accountDir + File.separator + msg.getBelongId());
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        // 在当前用户的目录下面存放录音文件
        File audioFile = new File(dir.getAbsolutePath() + File.separator + msg.getMsgTime() + ".amr");
        try
        {
            if (!audioFile.exists())
            {
                audioFile.createNewFile();
            }
        } catch (IOException e)
        {
        }
        return audioFile.getAbsolutePath();
    }
}