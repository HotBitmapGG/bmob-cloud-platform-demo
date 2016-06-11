package com.hotbitmapgg.geekcommunity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.widget.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.im.bean.BmobChatUser;

public class UserInfoDetailsActivity extends ParentActivity implements OnClickListener
{

    private RelativeLayout mUserDetailsLayout;

    private TextView mNickName;

    private RoundedImageView mUserPhoto;

    private RelativeLayout mMessageRecord;

    private RelativeLayout mUserMessageBg;

    private BmobChatUser mBmobChatUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_details);

        StatusBarCompat.compat(this);

        Intent intent = getIntent();
        mBmobChatUser = (BmobChatUser) intent.getSerializableExtra("user");

        initTitle();
        initView();
    }

    private void initView()
    {

        mUserDetailsLayout = (RelativeLayout) findViewById(R.id.user_details_layout);
        mNickName = (TextView) findViewById(R.id.user_details_name);
        mUserPhoto = (RoundedImageView) findViewById(R.id.user_details_photo);
        mMessageRecord = (RelativeLayout) findViewById(R.id.user_msg_record);
        mUserMessageBg = (RelativeLayout) findViewById(R.id.user_msg_bg);

        String nick = mBmobChatUser.getNick();
        String username = mBmobChatUser.getUsername();
        String avatar = mBmobChatUser.getAvatar();
        if (!TextUtils.isEmpty(nick))
        {
            mNickName.setText(nick);
        } else
        {
            mNickName.setText(username);
        }

        if (!TextUtils.isEmpty(avatar))
        {
            ImageLoader.getInstance().displayImage(avatar, mUserPhoto, ImageLoadUtil.defaultOptions());
        } else
        {
            mUserPhoto.setImageResource(R.drawable.ico_user_default);
        }

        mUserDetailsLayout.setOnClickListener(this);
        mMessageRecord.setOnClickListener(this);
        mUserMessageBg.setOnClickListener(this);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.user_details_top);
        ImageView mLeftBtn = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBtn.setVisibility(View.VISIBLE);
        mTitle.setText("聊天设置");
        mLeftBtn.setOnClickListener(new OnClickListener()
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
            case R.id.user_details_layout:
                Intent intent = new Intent(UserInfoDetailsActivity.this, SetMyInfoActivity.class);
                intent.putExtra("from", "other");
                intent.putExtra("username", mBmobChatUser.getUsername());
                startActivity(intent);
                break;

            case R.id.user_msg_record:

                break;

            case R.id.user_msg_bg:

                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == 100)
            {
                int resId = data.getIntExtra("resId", 0);
                Bundle mBundle = new Bundle();
                mBundle.putInt("resId", resId);
                Intent mIntent = new Intent();
                mIntent.setAction("android.homemessage.resid");
                mIntent.putExtras(mBundle);
                UserInfoDetailsActivity.this.sendBroadcast(mIntent);
            }
        }
    }
}
