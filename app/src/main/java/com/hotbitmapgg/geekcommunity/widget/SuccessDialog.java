package com.hotbitmapgg.geekcommunity.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.DisplayUtil;


public class SuccessDialog extends Dialog
{

	private Context context = null;

	private TextView tv_msg;

	private ImageView img_success;

	public SuccessDialog(Context context)
	{
		super(context);
		this.context = context;
		init();
	}

	public SuccessDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
	{

		super(context, cancelable, cancelListener);
		this.context = context;
		init();
	}

	public SuccessDialog(Context context, int theme)
	{

		super(context, theme);
		this.context = context;

		init();

	}

	private void init()
	{
		setCanceledOnTouchOutside(false);
		// 加载自己定义的布局
		View view = LayoutInflater.from(context).inflate(R.layout.success_dialog, null);
		img_success = (ImageView) view.findViewById(R.id.successImg);
		tv_msg = (TextView) view.findViewById(R.id.successTv);

		// 为Dialoge设置自己定义的布局
		setContentView(view);

		WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); // 获取对话框当前的参数
		Display d = getWindow().getWindowManager().getDefaultDisplay(); // 获取屏幕宽高
		layoutParams.width = DisplayUtil.dip2px(getContext(), 110);
		layoutParams.height = DisplayUtil.dip2px(getContext(), 110);

		getWindow().setAttributes(layoutParams);
	}
	
	public void setImageRes(int res)
	{
		img_success.setImageResource(res);
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
