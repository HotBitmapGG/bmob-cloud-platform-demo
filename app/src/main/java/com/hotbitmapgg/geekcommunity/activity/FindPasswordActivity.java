package com.hotbitmapgg.geekcommunity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;


public class FindPasswordActivity extends BaseActivity implements OnClickListener
{

    private RelativeLayout mEmailFind;

    private RelativeLayout mPhoneFind;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        StatusBarCompat.compat(this);

        initTitle();
        initView();
    }

    private void initView()
    {

        mEmailFind = (RelativeLayout) findViewById(R.id.find_email);
        mPhoneFind = (RelativeLayout) findViewById(R.id.find_phone);
        mEmailFind.setOnClickListener(this);
        mPhoneFind.setOnClickListener(this);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.find_password_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBack.setVisibility(View.VISIBLE);
        mTitle.setText("找回密码");
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.find_email:
                startActivity(new Intent(FindPasswordActivity.this, FindPwdEmailActivity.class));
                break;

            case R.id.find_phone:
                startActivity(new Intent(FindPasswordActivity.this, FindPwdPhoneActivity.class));
                break;

            default:
                break;
        }
    }
}
