package com.hotbitmapgg.geekcommunity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.config.Config;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.utils.HomeMessageTools;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.StringUtils;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;


public class GetAuthCodeActivity extends BaseActivity implements OnClickListener
{

    private EditText mEdPhone;

    private Button mGetCode;

    private LoadingDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        HomeMessageTools.bindActivitys.add(this);
        setContentView(R.layout.activity_auth_code);
        StatusBarCompat.compat(this);

        BmobSMS.initialize(this, Config.applicationId);

        initTitle();
        initView();
    }

    private void initView()
    {

        mEdPhone = (EditText) findViewById(R.id.et_phone);
        mEdPhone.requestFocus();
        mGetCode = (Button) findViewById(R.id.btn_get_code);
        mGetCode.setOnClickListener(this);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.auth_code_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBack.setVisibility(View.VISIBLE);
        mTitle.setText("获取验证码");
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                if (null != HomeMessageTools.bindActivitys)
                {
                    for (int i = 0; i < HomeMessageTools.bindActivitys.size(); i++)
                    {
                        Activity activity = HomeMessageTools.bindActivitys.get(i);
                        if (null != activity)
                        {
                            activity.finish();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.btn_get_code:
                String phone = mEdPhone.getText().toString();
                if (TextUtils.isEmpty(phone))
                {
                    ToastUtil.ShortToast("手机号码为空");
                    return;
                } else
                {
                    if (!StringUtils.isPhoneValid(phone))
                    {
                        ToastUtil.ShortToast("手机号码不合法");
                        return;
                    }

                    queryAuthCode(phone);
                }

                break;

            default:
                break;
        }
    }

    private void queryAuthCode(final String phone)
    {

        mDialog = new LoadingDialog(GetAuthCodeActivity.this, R.style.Loading);
        mDialog.setMessage("获取中...");
        mDialog.show();

        BmobSMS.requestSMSCode(this, phone, RestUtils.SMSName, new RequestSMSCodeListener()
        {

            @Override
            public void done(Integer smsId, BmobException ex)
            {

                if (ex == null)
                {
                    mDialog.dismiss();
                    ToastUtil.ShortToast("验证码已发送至您的手机,请查收");
                    Intent mIntent = new Intent(GetAuthCodeActivity.this, VeirfyAuthCodeActivity.class);
                    mIntent.putExtra("phone", phone);
                    mIntent.putExtra("smsId", smsId);
                    startActivity(mIntent);
                } else
                {
                    mDialog.dismiss();
                    ToastUtil.ShortToast("发送失败");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (null != HomeMessageTools.unBindActivitys)
            {
                for (int i = 0; i < HomeMessageTools.unBindActivitys.size(); i++)
                {
                    Activity activity = HomeMessageTools.unBindActivitys.get(i);
                    if (null != activity)
                    {
                        activity.finish();
                    }
                }
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
