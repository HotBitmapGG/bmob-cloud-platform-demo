package com.hotbitmapgg.geekcommunity.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;

public class AppIntroduceActivity extends ParentActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_introduce);
		StatusBarCompat.compat(this);

		initTitle();
	}

	private void initTitle()
	{
		View view = findViewById(R.id.app_top);
		ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
		TextView mTitle = (TextView) view.findViewById(R.id.top_title);
		mLeftBack.setVisibility(View.VISIBLE);
		mTitle.setText("关于极客社区");
		mLeftBack.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();

			}
		});

	}
}
