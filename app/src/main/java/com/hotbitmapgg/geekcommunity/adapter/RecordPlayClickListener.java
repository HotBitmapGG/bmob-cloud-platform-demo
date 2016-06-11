package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;

import cn.bmob.im.BmobPlayManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.OnPlayChangeListener;



public class RecordPlayClickListener implements View.OnClickListener
{

	BmobMsg message;

	ImageView iv_voice;

	private AnimationDrawable anim = null;

	public static RecordPlayClickListener currentPlayListener = null;

	BmobPlayManager playMananger;

	Context context;

	String currentObjectId = "";

	// 用于区分两个不同语音的播放
	static BmobMsg currentMsg = null;

	public RecordPlayClickListener(Context context, BmobMsg msg, ImageView voice)
	{
		this.iv_voice = voice;
		this.message = msg;
		this.context = context;
		currentMsg = msg;
		currentPlayListener = this;
		currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
		playMananger = BmobPlayManager.getInstance(context);
		playMananger.setOnPlayChangeListener(new OnPlayChangeListener()
		{

			@Override
			public void onPlayStop()
			{
				// TODO Auto-generated method stub
				currentPlayListener.stopRecordAnimation();
			}

			@Override
			public void onPlayStart()
			{
				// TODO Auto-generated method stub
				currentPlayListener.startRecordAnimation();
			}
		});
	}

	/**
	 * 开启播放动画
	 *
	 * @Title: startRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void startRecordAnimation()
	{
		if (message.getBelongId().equals(currentObjectId))
		{
			iv_voice.setImageResource(R.drawable.anim_chat_voice_right);
		}
		else
		{
			iv_voice.setImageResource(R.drawable.anim_chat_voice_left);
		}
		anim = (AnimationDrawable) iv_voice.getDrawable();
		anim.start();
	}

	/**
	 * 停止播放动画
	 *
	 * @Title: stopRecordAnimation
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void stopRecordAnimation()
	{
		if (message.getBelongId().equals(currentObjectId))
		{
			iv_voice.setImageResource(R.drawable.chat_voice_to_3);
		}
		else
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
		if (playMananger.isPlaying())
		{
			playMananger.stopPlayback();
			if (currentMsg != null && currentMsg.hashCode() == message.hashCode())
			{// 是否是同条语音消息
				currentMsg = null;
				return;
			}
		}
		else
		{
			String localPath = message.getContent().split("&")[0];
			LogUtil.lsw("本地地址 :" + localPath);
			if (message.getBelongId().equals(currentObjectId))
			{
				// 如果是自己发送的语音消息，则播放本地地址
				playMananger.playRecording(localPath, true);
			}
			else
			{
				// 如果是收到的消息，则需要先下载后播放

			}
		}

	}

}