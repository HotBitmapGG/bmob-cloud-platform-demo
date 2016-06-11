package com.hotbitmapgg.geekcommunity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.config.Config;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.utils.HomeMessageTools;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.bean.BmobSmsState;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.QuerySMSStateListener;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;


public class VeirfyAuthCodeActivity extends BaseActivity implements OnClickListener
{

    private TextView tv_tips;

    private TextView tv_phone;

    private EditText et_code;

    private Button btn_code;

    private Button btn_validata;

    private String phone;

    private int smsId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        HomeMessageTools.bindActivitys.add(this);
        setContentView(R.layout.activity_veirfy_code);

        StatusBarCompat.compat(this);
        BmobSMS.initialize(this, Config.applicationId);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        smsId = intent.getIntExtra("smsId", 0);
        initTitle();
        initView();
        setView();
        new CountDownTimer(90 * 1000, 1000)
        {

            public void onTick(long millisUntilFinished)
            {

                btn_code.setText(millisUntilFinished / 1000 + "s再获取");
                btn_code.setClickable(false);
            }

            public void onFinish()
            {

                btn_code.setClickable(true);
                btn_code.setText("重新获取");
            }
        }.start();
    }

    private void initView()
    {

        tv_tips = (TextView) findViewById(R.id.tv_code_tips);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_code = (Button) findViewById(R.id.btn_code);
        btn_validata = (Button) findViewById(R.id.btn_validate);
        btn_code.setOnClickListener(this);
        btn_validata.setOnClickListener(this);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.veirfy_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mLeftBack.setVisibility(View.VISIBLE);
        mTitle.setText("验证");
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

    private void setView()
    {

        tv_tips.setText(Html.fromHtml(getString(R.string.settings_phone_tips, "<font color='#ff45c3f5'>" + getString(R.string.settings_phone_code) + "</font>")));
        tv_phone.setText("+86" + phone);
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.btn_code:
                queryAuthCode();
                break;

            case R.id.btn_validate:
                querySendCode(smsId);
                break;

            default:
                break;
        }
    }

    protected void querySendCode(Integer smsId)
    {

        final String code = et_code.getText().toString();
        LogUtil.lsw(code);
        if (TextUtils.isEmpty(code))
        {
            ToastUtil.ShortToast("验证码不能为空");
            return;
        }

        BmobSMS.querySmsState(this, smsId, new QuerySMSStateListener()
        {

            @Override
            public void done(BmobSmsState state, BmobException ex)
            {

                if (ex == null)
                {
                    String smsState = state.getSmsState();
                    LogUtil.lsw(smsState);
                    if (smsState.equals("SUCCESS"))
                    {
                        bingPhone(code);
                    } else if (smsState.equals("FAIL"))
                    {

                        ToastUtil.ShortToast("验证码发送失败");
                    } else if (smsState.equals("SENDING"))
                    {
                        ToastUtil.ShortToast("验证码正在发送,请稍候");
                    }
                }
            }
        });
    }

    private void bingPhone(String code)
    {

        BmobSMS.verifySmsCode(this, phone, code, new VerifySMSCodeListener()
        {

            @Override
            public void done(BmobException ex)
            {

                if (ex == null)
                {
                    ToastUtil.ShortToast("验证成功");
                    Intent mIntent = new Intent(VeirfyAuthCodeActivity.this, RegisterActivity.class);
                    mIntent.putExtra("phone", phone);
                    startActivity(mIntent);
                    finish();
                } else
                {
                    ToastUtil.ShortToast("验证失败 =" + ex.getErrorCode() + ",msg = " + ex.getLocalizedMessage());
                }
            }
        });
    }

    protected void savePhoneToBmob()
    {

        if (!TextUtils.isEmpty(phone))
        {
            User mUser = new User();
            mUser.setMobilePhoneNumber(phone);
            User currentUser = BmobUser.getCurrentUser(VeirfyAuthCodeActivity.this, User.class);
            mUser.update(VeirfyAuthCodeActivity.this, currentUser.getObjectId(), new UpdateListener()
            {

                @Override
                public void onSuccess()
                {

                    LogUtil.lsw("更新成功");
                }

                @Override
                public void onFailure(int errorCode, String errorMsg)
                {

                    LogUtil.lsw("更新失败 :" + errorMsg);
                }
            });
        }
    }

    private void queryAuthCode()
    {

        BmobSMS.requestSMSCode(this, phone, RestUtils.SMSName, new RequestSMSCodeListener()
        {

            @Override
            public void done(Integer smsId, BmobException ex)
            {

                if (ex == null)
                {
                    ToastUtil.ShortToast("验证码已发送至您的手机,请查收");
                    new CountDownTimer(90 * 1000, 1000)
                    {

                        public void onTick(long millisUntilFinished)
                        {

                            btn_code.setText(millisUntilFinished / 1000 + "s再获取");
                            btn_code.setClickable(false);
                        }

                        public void onFinish()
                        {

                            btn_code.setClickable(true);
                            btn_code.setText("重新获取");
                        }
                    }.start();
                } else
                {

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
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
