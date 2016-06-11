package com.hotbitmapgg.geekcommunity.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.DisplayUtil;


public class LoadingDialog extends Dialog
{

	private Context context = null;

	private TextView tv_msg;

	public LoadingDialog(Context context)
	{
		super(context);
		this.context = context;
		init();
	}

	public LoadingDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
	{

		super(context, cancelable, cancelListener);
		this.context = context;
		init();
	}

	public LoadingDialog(Context context, int theme)
	{

		super(context, theme);
		this.context = context;

		init();

	}

	private void init()
	{
		setCanceledOnTouchOutside(false);
		// 加载自己定义的布局
		View view = LayoutInflater.from(context).inflate(R.layout.loading, null);
		ImageView img_loading = (ImageView) view.findViewById(R.id.loadImg);
		tv_msg = (TextView) view.findViewById(R.id.loadTv);

		// 加载XML文件中定义的动画
		Drawable drawable = img_loading.getDrawable();
		if (drawable != null && drawable instanceof AnimationDrawable)
		{
			AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
			animationDrawable.start();
		}

		// 为Dialoge设置自己定义的局
		setContentView(view);

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); // 获取对话框当前的参数
		Display d = getWindow().getWindowManager().getDefaultDisplay(); // 获取屏幕宽高
		layoutParams.width = DisplayUtil.dip2px(getContext(), 110);
		layoutParams.height = DisplayUtil.dip2px(getContext(), 110);

		// layoutParams.y = d.getHeight() / 4;
		getWindow().setAttributes(layoutParams);
	}

	public void setMessage(String msg)
	{

		if (null != tv_msg)
		{
			tv_msg.setText(msg);
		}
	}

	public void setMessage(int resId)
	{

		if (null != tv_msg)
		{
			tv_msg.setText(context.getString(resId));
		}
	}

}
