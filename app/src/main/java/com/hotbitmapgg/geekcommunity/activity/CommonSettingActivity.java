package com.hotbitmapgg.geekcommunity.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.SharePreferenceUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommonSettingActivity extends ParentActivity implements OnClickListener
{

	private RelativeLayout rl_switch_notification;

	private RelativeLayout rl_switch_voice;

	private RelativeLayout rl_switch_vibrate;

	private ImageView iv_open_notification;

	private ImageView iv_close_notification;

	private ImageView iv_open_voice;

	private ImageView iv_close_voice;

	private ImageView iv_open_vibrate;

	private ImageView iv_close_vibrate;

	private View view1;

	private String path;

	private SharePreferenceUtil mSharedUtil;

	private RelativeLayout mCleanCache;

	private LoadingDialog mDialog;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{

			if (msg.what == 0)
			{
				mDialog.dismiss();
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_setting);

		StatusBarCompat.compat(this);
		
		mSharedUtil = mApplication.getSpUtil();
		initView();
	}

	private void initView()
	{

		initTitle();
		rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
		rl_switch_voice = (RelativeLayout) findViewById(R.id.rl_switch_voice);
		rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_voice.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);
		mCleanCache = (RelativeLayout) findViewById(R.id.rl_clean_cache);
		mCleanCache.setOnClickListener(this);
		iv_open_notification = (ImageView) findViewById(R.id.iv_open_notification);
		iv_close_notification = (ImageView) findViewById(R.id.iv_close_notification);
		iv_open_voice = (ImageView) findViewById(R.id.iv_open_voice);
		iv_close_voice = (ImageView) findViewById(R.id.iv_close_voice);
		iv_open_vibrate = (ImageView) findViewById(R.id.iv_open_vibrate);
		iv_close_vibrate = (ImageView) findViewById(R.id.iv_close_vibrate);
		view1 = (View) findViewById(R.id.view1);

		boolean isAllowNotify = mSharedUtil.isAllowPushNotify();

		if (isAllowNotify)
		{
			iv_open_notification.setVisibility(View.VISIBLE);
			iv_close_notification.setVisibility(View.INVISIBLE);
		}
		else
		{
			iv_open_notification.setVisibility(View.INVISIBLE);
			iv_close_notification.setVisibility(View.VISIBLE);
		}
		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		if (isAllowVoice)
		{
			iv_open_voice.setVisibility(View.VISIBLE);
			iv_close_voice.setVisibility(View.INVISIBLE);
		}
		else
		{
			iv_open_voice.setVisibility(View.INVISIBLE);
			iv_close_voice.setVisibility(View.VISIBLE);
		}
		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		if (isAllowVibrate)
		{
			iv_open_vibrate.setVisibility(View.VISIBLE);
			iv_close_vibrate.setVisibility(View.INVISIBLE);
		}
		else
		{
			iv_open_vibrate.setVisibility(View.INVISIBLE);
			iv_close_vibrate.setVisibility(View.VISIBLE);
		}

	}

	private void initTitle()
	{
		View view = findViewById(R.id.common_setting_top);
		ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
		mLeftBack.setVisibility(View.VISIBLE);
		mLeftBack.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();

			}
		});
		TextView mTitle = (TextView) view.findViewById(R.id.top_title);
		mTitle.setText("通用设置");

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{

		case R.id.rl_switch_notification:
			if (iv_open_notification.getVisibility() == View.VISIBLE)
			{
				iv_open_notification.setVisibility(View.INVISIBLE);
				iv_close_notification.setVisibility(View.VISIBLE);
				mSharedUtil.setPushNotifyEnable(false);
				rl_switch_vibrate.setVisibility(View.GONE);
				rl_switch_voice.setVisibility(View.GONE);
				view1.setVisibility(View.GONE);

			}
			else
			{
				iv_open_notification.setVisibility(View.VISIBLE);
				iv_close_notification.setVisibility(View.INVISIBLE);
				mSharedUtil.setPushNotifyEnable(true);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				rl_switch_voice.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);

			}

			break;
		case R.id.rl_switch_voice:
			if (iv_open_voice.getVisibility() == View.VISIBLE)
			{
				iv_open_voice.setVisibility(View.INVISIBLE);
				iv_close_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVoiceEnable(false);
			}
			else
			{
				iv_open_voice.setVisibility(View.VISIBLE);
				iv_close_voice.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVoiceEnable(true);
			}

			break;
		case R.id.rl_switch_vibrate:
			if (iv_open_vibrate.getVisibility() == View.VISIBLE)
			{
				iv_open_vibrate.setVisibility(View.INVISIBLE);
				iv_close_vibrate.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVibrateEnable(false);
			}
			else
			{
				iv_open_vibrate.setVisibility(View.VISIBLE);
				iv_close_vibrate.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVibrateEnable(true);
			}
			break;

		case R.id.rl_clean_cache:
			mDialog = new LoadingDialog(CommonSettingActivity.this, R.style.Loading);
			mDialog.setMessage("正在清理...");
			mDialog.show();
			ImageLoader.getInstance().clearDiskCache();
			mHandler.sendEmptyMessageDelayed(0, 2000);

			break;

		default:
			break;
		}

	}
}
