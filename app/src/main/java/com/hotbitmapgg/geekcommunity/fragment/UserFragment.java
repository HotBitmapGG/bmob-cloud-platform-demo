package com.hotbitmapgg.geekcommunity.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.activity.BlackListActivity;
import com.hotbitmapgg.geekcommunity.activity.FragmentBase;
import com.hotbitmapgg.geekcommunity.activity.SetMyInfoActivity;
import com.hotbitmapgg.geekcommunity.activity.SettingActivity;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.utils.DisplayUtil;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.widget.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ypy.eventbus.EventBus;

import cn.bmob.im.BmobUserManager;

@SuppressLint("SimpleDateFormat")
public class UserFragment extends FragmentBase implements OnClickListener
{

    private TextView tv_set_name;

    private RelativeLayout layout_info;

    private RoundedImageView mUserPic;

    private TextView mUserName;

    private RelativeLayout mSetting;

    // 黑名单
    private RelativeLayout layout_blacklist;

    private TextView mUserInfo;

    private ImageView mUserSex;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_set, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        // 注册EventBus
        EventBus.getDefault().register(this);

        initView();
        initData();
    }

    private void initView()
    {

        layout_blacklist = (RelativeLayout) findViewById(R.id.layout_blacklist);
        layout_blacklist.setOnClickListener(this);

        layout_info = (RelativeLayout) findViewById(R.id.layout_info);
        mUserInfo = (TextView) findViewById(R.id.tv_user_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //如果是5.0以上系统 设置高度为250dp
            LayoutParams layoutParams = layout_info.getLayoutParams();
            int height = DisplayUtil.dip2px(getActivity(), 250);
            layoutParams.height = height;
            layout_info.setLayoutParams(layoutParams);
            android.widget.RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) mUserInfo.getLayoutParams();
            int topMargin = DisplayUtil.dip2px(getActivity(), 40);
            lp.topMargin = topMargin;
            mUserInfo.setLayoutParams(lp);
        }
        tv_set_name = (TextView) findViewById(R.id.tv_set_name);

        mUserPic = (RoundedImageView) findViewById(R.id.user_photo);
        mUserName = (TextView) findViewById(R.id.user_name);

        mSetting = (RelativeLayout) findViewById(R.id.rl_setting);
        mUserSex = (ImageView) findViewById(R.id.user_sex);

        mUserInfo.setOnClickListener(this);
        mSetting.setOnClickListener(this);
    }

    private void initData()
    {

        tv_set_name.setText("帐号:" + BmobUserManager.getInstance(getActivity()).getCurrentUser().getUsername());
        String nick = BmobUserManager.getInstance(getActivity()).getCurrentUser().getNick();
        if (nick != null)
        {
            mUserName.setText(BmobUserManager.getInstance(getActivity()).getCurrentUser().getNick());
        } else
        {
            mUserName.setText("您还没有设置昵称");
        }

        String avatar = BmobUserManager.getInstance(getActivity()).getCurrentUser().getAvatar();
        if (avatar != null)
        {
            ImageLoader.getInstance().displayImage(avatar, mUserPic, ImageLoadUtil.defaultOptions());
        } else
        {
            mUserPic.setImageResource(R.drawable.ico_user_default);
        }
        Boolean sex = HomeMsgApplication.getInstance().getCurrentUser().getSex();
        if (sex)
        {
            mUserSex.setImageResource(R.drawable.userinfo_icon_male);
        } else
        {
            mUserSex.setImageResource(R.drawable.userinfo_icon_female);
        }
    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {

            case R.id.tv_user_info:
                // 跳转到个人信息设置界面
                Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
                intent.putExtra("from", "me");
                startAnimActivity(intent);
                break;

            case R.id.rl_setting:
                // 进入设置界面
                startAnimActivity(new Intent(getActivity(), SettingActivity.class));
                break;


            case R.id.layout_blacklist:
                // 启动到黑名单页面
                startAnimActivity(new Intent(getActivity(), BlackListActivity.class));
                break;

            default:
                break;
        }
    }

    public void onEventMainThread(Bundle bundle)
    {

        if (bundle != null)
        {
            String url = bundle.getString("head_photo");
            if (url != null)
            {
                refreshAvatar(url);
            }

            String nickName = bundle.getString("name");
            if (nickName != null)
            {
                mUserName.setText(nickName);
            }
        }
    }

    /**
     * 更新头像 refreshAvatar
     *
     * @return void
     * @throws
     */
    private void refreshAvatar(String avatar)
    {

        if (avatar != null && !avatar.equals(""))
        {
            ImageLoader.getInstance().displayImage(avatar, mUserPic, ImageLoadUtil.defaultOptions());
        } else
        {
            mUserPic.setImageResource(R.drawable.ico_user_default);
        }
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}