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
import com.hotbitmapgg.geekcommunity.utils.InputMethodUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

public class ResetPasswordActivity extends BaseActivity
{

    private EditText mNewPwd;

    private EditText mConfirmNewPwd;

    private Button mOk;

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        StatusBarCompat.compat(this);

        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        initTitle();
        initView();
        InputMethodUtil.showInputMethod(this, mNewPwd, 500);
    }

    private void initView()
    {

        mNewPwd = (EditText) findViewById(R.id.et_new_pwd);
        mConfirmNewPwd = (EditText) findViewById(R.id.et_confirm_new_pwd);
        mOk = (Button) findViewById(R.id.btn_ok);
        mOk.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                String newPwd = mNewPwd.getText().toString();
                String confirmNewPwd = mConfirmNewPwd.getText().toString();
                if (TextUtils.isEmpty(newPwd))
                {
                    ToastUtil.ShortToast("新密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(confirmNewPwd))
                {
                    ToastUtil.ShortToast("确认密码不能为空");
                    return;
                }
                if (!TextUtils.equals(newPwd, confirmNewPwd))
                {
                    ToastUtil.ShortToast("两次输入的密码不一致");
                    return;
                }

                submitNewPwd(newPwd);
            }
        });
    }

    protected void submitNewPwd(String newPwd)
    {

        BmobUser.resetPasswordBySMSCode(this, code, newPwd, new ResetPasswordByCodeListener()
        {

            @Override
            public void done(BmobException ex)
            {

                if (ex == null)
                {
                    ToastUtil.ShortToast("密码重置成功");
                }
            }
        });
    }

    private void initTitle()
    {

        View view = findViewById(R.id.reset_pwd_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBack.setVisibility(View.VISIBLE);
        mTitle.setText("密码重置");
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
            }
        });
    }
}
