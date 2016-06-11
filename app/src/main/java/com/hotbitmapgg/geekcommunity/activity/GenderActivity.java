package com.hotbitmapgg.geekcommunity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;

public class GenderActivity extends BaseActivity implements OnClickListener
{

    private RelativeLayout mMan;

    private RelativeLayout mWoman;

    private ImageView mCheckMan;

    private ImageView mCheckWoman;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        StatusBarCompat.compat(this);

        initView();
    }

    private void initView()
    {

        initTitle();
        mMan = (RelativeLayout) findViewById(R.id.man);
        mWoman = (RelativeLayout) findViewById(R.id.woman);
        mCheckMan = (ImageView) findViewById(R.id.man_check);
        mCheckWoman = (ImageView) findViewById(R.id.check_woman);

        mMan.setOnClickListener(this);
        mWoman.setOnClickListener(this);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.choose_sex_top);
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
        mTitle.setText("选择性别");
    }

    @Override
    public void onClick(View v)
    {

        Intent mIntent = new Intent();
        switch (v.getId())
        {

            case R.id.man:
                mCheckMan.setVisibility(View.VISIBLE);
                mCheckWoman.setVisibility(View.GONE);

                mIntent.putExtra("sex", "男");
                setResult(Activity.RESULT_OK, mIntent);
                finish();

                break;

            case R.id.woman:
                mCheckMan.setVisibility(View.GONE);
                mCheckWoman.setVisibility(View.VISIBLE);

                mIntent.putExtra("sex", "女");
                setResult(Activity.RESULT_OK, mIntent);
                finish();

                break;

            default:
                break;
        }
    }
}
