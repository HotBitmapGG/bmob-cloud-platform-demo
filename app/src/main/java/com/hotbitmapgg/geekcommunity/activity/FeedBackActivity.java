package com.hotbitmapgg.geekcommunity.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.bean.FeedBackBean;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;

import cn.bmob.v3.listener.SaveListener;

public class FeedBackActivity extends ParentActivity implements OnClickListener
{
	private EditText mFeedBack;

	private TextView mTip;

	private LoadingDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		StatusBarCompat.compat(this);
		
		initTitle();
		initView();
	}

	private void initView()
	{
		mFeedBack = (EditText) findViewById(R.id.feed_edit);
		mTip = (TextView) findViewById(R.id.tip);

		mFeedBack.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				mTip.setText((160 - s.length()) + "");

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub

			}
		});

	}

	private void initTitle()
	{
		View view = findViewById(R.id.feed_back_top);
		ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
		TextView mTitlte = (TextView) view.findViewById(R.id.top_title);
		mLeftBack.setVisibility(View.VISIBLE);
		mTitlte.setText("意见反馈");
		mLeftBack.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();

			}
		});
		TextView mRightTv = (TextView) view.findViewById(R.id.right_tv);
		mRightTv.setVisibility(View.VISIBLE);
		mRightTv.setText("发送");
		mRightTv.setOnClickListener(this);

	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.right_tv)
		{
			String text = mFeedBack.getText().toString().trim();
			if (TextUtils.isEmpty(text))
			{
				ToastUtil.ShortToast("输入的内容不能为空");
				return;
			}

			mDialog = new LoadingDialog(FeedBackActivity.this, R.style.Loading);
			mDialog.setMessage("提交中...");
			mDialog.show();

			sendFeedBackText(text);
		}

	}

	private void sendFeedBackText(String text)
	{
		FeedBackBean mFeedBackBean = new FeedBackBean();
		mFeedBackBean.setContent(text);
		mFeedBackBean.save(FeedBackActivity.this, new SaveListener()
		{

			@Override
			public void onSuccess()
			{
				// TODO Auto-generated method stub
				mDialog.dismiss();
				ToastUtil.ShortToast("提交成功");
				mFeedBack.setText("");

			}

			@Override
			public void onFailure(int errorCode, String errorMsg)
			{
				// TODO Auto-generated method stub
				mDialog.dismiss();
				ToastUtil.ShortToast("提交失败");

			}
		});
	}

}
