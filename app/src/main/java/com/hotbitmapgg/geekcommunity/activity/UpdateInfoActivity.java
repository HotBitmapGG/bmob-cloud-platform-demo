package com.hotbitmapgg.geekcommunity.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.ypy.eventbus.EventBus;

import cn.bmob.v3.listener.UpdateListener;


public class UpdateInfoActivity extends ParentActivity implements OnClickListener
{

    private EditText edit_nick;

    private Button mOk;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_updateinfo);

        StatusBarCompat.compat(this);

        initView();
    }

    private void initView()
    {

        View view = findViewById(R.id.upload_info_top);
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
        mTitle.setText("修改昵称");
        edit_nick = (EditText) findViewById(R.id.edit_nick);
        mOk = (Button) findViewById(R.id.update_info_ok);
        mOk.setOnClickListener(this);
    }


    private void updateInfo(final String nick)
    {

        final User user = userManager.getCurrentUser(User.class);
        User u = new User();
        u.setNick(nick);
        u.setHight(110);
        u.setObjectId(user.getObjectId());
        u.update(this, new UpdateListener()
        {

            @Override
            public void onSuccess()
            {
                // TODO Auto-generated method stub
                final User c = userManager.getCurrentUser(User.class);
                ToastUtil.ShortToast("修改成功");
                Bundle mBundle = new Bundle();
                mBundle.putString("name", nick);
                EventBus.getDefault().post(mBundle);
                finish();
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public void onClick(View v)
    {

        if (v.getId() == R.id.update_info_ok)
        {
            String nick = edit_nick.getText().toString();
            if (TextUtils.isEmpty(nick))
            {
                ToastUtil.ShortToast("昵称不能为空");
                return;
            }
            updateInfo(nick);
        }
    }
}
