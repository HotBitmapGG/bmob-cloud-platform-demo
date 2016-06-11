package com.hotbitmapgg.geekcommunity.activity;

import android.content.Intent;
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
import com.hotbitmapgg.geekcommunity.utils.InputMethodUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;

import cn.bmob.v3.listener.UpdateListener;


public class SetSignTextActivity extends ParentActivity
{

    private EditText mEditText;

    private Button mOk;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setsign);

        StatusBarCompat.compat(this);

        initTitle();
        initView();
        InputMethodUtil.showInputMethod(this, mEditText, 500);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.set_sign_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBack.setVisibility(View.VISIBLE);
        mTitle.setText("设置签名");
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
            }
        });
    }

    private void initView()
    {

        mEditText = (EditText) findViewById(R.id.et_sign);
        mOk = (Button) findViewById(R.id.set_sign_ok);

        mOk.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                String text = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(text))
                {
                    ToastUtil.ShortToast("签名不能为空");
                    return;
                }

                updateInfo(text);
            }
        });
    }


    private void updateInfo(final String sign)
    {

        final User user = userManager.getCurrentUser(User.class);
        User u = new User();
        u.setSignature(sign);
        u.setHight(110);
        u.setObjectId(user.getObjectId());
        u.update(this, new UpdateListener()
        {

            @Override
            public void onSuccess()
            {

                Intent mIntent = new Intent();
                Bundle mBundle = new Bundle();
                mBundle.putString("sign", sign);
                mIntent.putExtras(mBundle);
                SetSignTextActivity.this.setResult(400, mIntent);
                SetSignTextActivity.this.finish();
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

            }
        });
    }
}
