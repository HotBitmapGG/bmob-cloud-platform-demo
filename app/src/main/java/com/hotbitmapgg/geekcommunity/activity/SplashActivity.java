package com.hotbitmapgg.geekcommunity.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.config.Config;

import cn.bmob.im.BmobChat;


public class SplashActivity extends BaseActivity
{

    private static final int GO_HOME = 100;

    private static final int GO_LOGIN = 200;

    private static final int ANIMATION_DURATION = 3000;

    private static final float SCALE_END = 1.13F;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        BmobChat.DEBUG_MODE = true;
        BmobChat.getInstance(this).init(Config.applicationId);

        mSplashImage = (ImageView) findViewById(R.id.splash_iv);
    }

    private void animateImage(final int flag)
    {

        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mSplashImage, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mSplashImage, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter()
        {

            @Override
            public void onAnimationEnd(Animator animation)
            {

                if (flag == 0)
                {
                    startAnimActivity(MainActivity.class);
                    finish();
                } else
                {
                    startAnimActivity(LoginActivity.class);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        if (userManager.getCurrentUser() != null)
        {
            updateUserInfos();
            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
        } else
        {
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case GO_HOME:
                    animateImage(0);
                    break;
                case GO_LOGIN:
                    animateImage(1);
                    break;
            }
        }
    };

    private ImageView mSplashImage;
}
