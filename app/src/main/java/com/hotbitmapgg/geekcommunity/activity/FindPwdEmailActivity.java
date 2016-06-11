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
import com.hotbitmapgg.geekcommunity.utils.InputMethodUtil;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.StringUtils;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;

public class FindPwdEmailActivity extends BaseActivity
{

    private EditText mEmail;

    private Button mOK;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        StatusBarCompat.compat(this);

        initTitle();
        initView();
        InputMethodUtil.showInputMethod(this, mEmail, 500);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.email_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBack.setVisibility(View.VISIBLE);
        mTitle.setText("邮箱找回");
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

        mEmail = (EditText) findViewById(R.id.et_find_email);
        mOK = (Button) findViewById(R.id.btn_verify);
        mOK.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                String email = mEmail.getText().toString();
                if (TextUtils.isEmpty(email))
                {
                    ToastUtil.ShortToast("邮箱地址不能为空");
                    return;
                }
                boolean isEmail = StringUtils.isEmail(email);
                if (!isEmail)
                {
                    ToastUtil.ShortToast("请输入正确的邮箱地址");
                    return;
                }
                findPasswordByEmail(email);
            }
        });
    }

    protected void findPasswordByEmail(String email)
    {

        BmobUser.resetPasswordByEmail(this, email, new ResetPasswordByEmailListener()
        {

            @Override
            public void onSuccess()
            {

                ToastUtil.ShortToast("密码找回成功,请登录邮箱进行验证");
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                ToastUtil.ShortToast("密码找回失败");
                LogUtil.lsw(errorMsg);
            }
        });
    }
}
