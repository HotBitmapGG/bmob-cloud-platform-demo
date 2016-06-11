package com.hotbitmapgg.geekcommunity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.ImageViewAdapter;
import com.hotbitmapgg.geekcommunity.utils.CompressImage;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;

import java.io.File;
import java.util.ArrayList;

public class AlumunsViewImageActivity extends ParentActivity
{

    private TextView mTitle;

    private int page = 0;

    private ViewPager view_pager;

    private String path;

    private ArrayList<String> photos;

    private ImageViewAdapter adapter;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumuns_image_preview);
        StatusBarCompat.compat(this);
        path = getIntent().getStringExtra("path");
        photos = getIntent().getStringArrayListExtra("alls");
        index = getIntent().getIntExtra("index", 0);
        initView();
        initTitle();
        initData();
    }

    private void initData()
    {

        page = index;
        mTitle.setText((page + 1) + "/" + photos.size());
        adapter = new ImageViewAdapter(AlumunsViewImageActivity.this, photos);
        view_pager.setAdapter(adapter);
        view_pager.setOnPageChangeListener(new OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int position)
            {

                page = position;
                mTitle.setText((page + 1) + "/" + photos.size());
            }

            @Override
            public void onPageScrolled(int position, float offset, int state)
            {

            }

            @Override
            public void onPageScrollStateChanged(int position)
            {

            }
        });
        view_pager.setCurrentItem(page);
    }

    private void initView()
    {

        view_pager = (ViewPager) findViewById(R.id.view_pager);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.alumuns_image_perview_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        ImageView mRightBtn = (ImageView) view.findViewById(R.id.right_btn);
        mRightBtn.setImageResource(R.drawable.action_button_delete_pressed_light);
        mRightBtn.setVisibility(View.VISIBLE);
        mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBack.setVisibility(View.VISIBLE);
        mTitle.setText("查看图片");
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                onBackPressed();
            }
        });
        mRightBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                deletePic();
            }
        });
    }


    @Override
    public void onBackPressed()
    {

        finishActivity();
    }


    private void deletePic()
    {

        String path = photos.get(page);
        photos.remove(path);
        File file = new File(path);
        CompressImage.deleteFile(file);

        if (photos.size() == 0)
        {
            finishActivity();
        }
        adapter.notifyDataSetChanged();
        page = 0;
        for (int i = 0; i < photos.size(); i++)
        {
            if (path.equals(photos.get(i)))
            {
                page = i;
            }
        }
        mTitle.setText((page + 1) + "/" + photos.size());
        view_pager.setCurrentItem(page);
    }

    private void finishActivity()
    {

        Intent intent = new Intent();
        intent.putStringArrayListExtra("all_path", photos);
        setResult(RESULT_OK, intent);
        finish();
    }
}
