package com.hotbitmapgg.geekcommunity.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.bean.ImagePackage;
import com.hotbitmapgg.geekcommunity.fragment.SlidePreviewImageFragment;

import java.util.ArrayList;
import java.util.List;


public class SlidePreviewImageActivity extends FragmentActivity
{

    private TextView textView;

    private ViewPager viewPager;

    private List<Fragment> photoFragments;

    private ImagePackage imagePackage;

    private int pos = 0;

    private List<String> urls;

    /**
     * On touch listener for zoom view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_preview_image_activity);
        imagePackage = (ImagePackage) getIntent().getSerializableExtra("imagePackage");
        pos = getIntent().getIntExtra("index", 0);

        viewPager = (ViewPager) findViewById(R.id.viewpage);
        textView = (TextView) findViewById(R.id.textView1);
        photoFragments = new ArrayList<Fragment>();

        urls = imagePackage.getUrls();
        for (int i = 0; i < urls.size(); i++)
        {
            photoFragments.add(SlidePreviewImageFragment.newInstance(urls.get(i)));
        }

        viewPager.setAdapter(new PhotoViewAdapter(getSupportFragmentManager(), photoFragments));

        viewPager.setOnPageChangeListener(new OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int position)
            {

                pos = position;
                textView.setText(getString(R.string.photo_size, position + 1, urls.size()));
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
        viewPager.setCurrentItem(pos);
        textView.setText(getString(R.string.photo_size, pos + 1, urls.size()));
    }

    class PhotoViewAdapter extends FragmentPagerAdapter
    {

        private List<Fragment> fragments;

        public PhotoViewAdapter(FragmentManager fm, List<Fragment> fragments)
        {

            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position)
        {

            return fragments.get(position);
        }

        @Override
        public int getCount()
        {

            return fragments.size();
        }
    }
}
