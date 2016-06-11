package com.hotbitmapgg.geekcommunity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.config.Config;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.utils.InputMethodUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;

public class FindPwdPhoneActivity extends BaseActivity
{

    private EditText mPhone;

    private Button mSubmit;

    private EditText mFindCode;

    private Button mFindCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        StatusBarCompat.compat(this);

        BmobSMS.initialize(this, Config.applicationId);
        initTitle();
        initView();
        InputMethodUtil.showInputMethod(this, mPhone, 500);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.phone_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBack.setVisibility(View.VISIBLE);
        mTitle.setText("手机找回");
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

        mPhone = (EditText) findViewById(R.id.et_find_phone);
        mSubmit = (Button) findViewById(R.id.btn_submit);
        mFindCode = (EditText) findViewById(R.id.et_find_code);
        mFindCodeBtn = (Button) findViewById(R.id.btn_find_code);
        mFindCodeBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                String phone = mPhone.getText().toString();
                queryAuthCode(phone);
            }
        });
        mSubmit.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                String code = mFindCode.getText().toString();
                String phone = mPhone.getText().toString();
                if (TextUtils.isEmpty(phone))
                {
                    ToastUtil.ShortToast("手机号码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(code))
                {
                    ToastUtil.ShortToast("请输入正确的手机号");
                    return;
                }

                findPasswordByPhone(code);
            }
        });
    }

    protected void findPasswordByPhone(String code)
    {

        Intent mIntent = new Intent(FindPwdPhoneActivity.this, ResetPasswordActivity.class);
        mIntent.putExtra("code", code);
        startActivity(mIntent);
    }

    private void queryAuthCode(String phone)
    {

        BmobSMS.requestSMSCode(this, phone, RestUtils.SMSName, new RequestSMSCodeListener()
        {

            @Override
            public void done(Integer smsId, BmobException ex)
            {

                if (ex == null)
                {
                    ToastUtil.ShortToast("验证码已发送您的手机,请查收");
                    new CountDownTimer(90 * 1000, 1000)
                    {

                        public void onTick(long millisUntilFinished)
                        {

                            mFindCodeBtn.setText(millisUntilFinished / 1000 + "s再获取");
                            mFindCodeBtn.setClickable(false);
                        }

                        public void onFinish()
                        {

                            mFindCodeBtn.setClickable(true);
                            mFindCodeBtn.setText("重新获取");
                        }
                    }.start();
                } else
                {

                    ToastUtil.ShortToast("发送失败");
                }
            }
        });
    }
}
