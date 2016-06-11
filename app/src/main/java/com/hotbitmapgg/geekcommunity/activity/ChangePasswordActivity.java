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
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;


public class ChangePasswordActivity extends ParentActivity
{

    private EditText mPwdOld;

    private EditText mPwdNew;

    private Button mChangePassword;

    private LoadingDialog mDialog;

    private String oldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        StatusBarCompat.compat(this);

        Intent intent = getIntent();
        oldPassword = intent.getStringExtra("oldPwd");
        initTitle();
        initView();
    }

    private void initView()
    {

        mPwdOld = (EditText) findViewById(R.id.et_pwd);
        mPwdNew = (EditText) findViewById(R.id.et_pwd1);
        mChangePassword = (Button) findViewById(R.id.btn_change_password);
        InputMethodUtil.showInputMethod(ChangePasswordActivity.this, mPwdOld, 200);
        mChangePassword.setOnClickListener(new OnClickListener()
        {


            @Override
            public void onClick(View v)
            {

                mDialog = new LoadingDialog(ChangePasswordActivity.this, R.style.Loading);
                mDialog.setMessage("修改中...");
                mDialog.show();
                changePassword();
            }
        });
    }

    private void changePassword()
    {

        String oldPwd = mPwdOld.getText().toString().trim();
        String newPwd = mPwdNew.getText().toString().trim();
        if (TextUtils.isEmpty(oldPwd))
        {
            mDialog.dismiss();
            ToastUtil.ShortToast("旧密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(newPwd))
        {
            mDialog.dismiss();
            ToastUtil.ShortToast("新密码不能为空");
            return;
        }
        if (!TextUtils.equals(oldPwd, newPwd))
        {
            mDialog.dismiss();
            ToastUtil.ShortToast("两次输入的密码不一致");
            return;
        }

        BmobUser.updateCurrentUserPassword(ChangePasswordActivity.this, oldPassword, newPwd, new UpdateListener()
        {

            @Override
            public void onSuccess()
            {

                mDialog.dismiss();
                ToastUtil.ShortToast("修改成功");
                ChangePasswordActivity.this.finish();
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                mDialog.dismiss();
                ToastUtil.ShortToast("修改失败");
            }
        });
    }

    private void initTitle()
    {

        View view = findViewById(R.id.change_password_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mTitle.setText("修改密码");
        mLeftBack.setVisibility(View.VISIBLE);
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
