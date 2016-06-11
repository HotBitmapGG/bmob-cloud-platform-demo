package com.hotbitmapgg.geekcommunity.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.config.Config;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.utils.CommonUtils;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity implements OnClickListener
{

    private Button btn_register;

    private TextView et_username;

    private EditText et_password;

    private EditText et_email;

    BmobChatUser currentUser;

    private EditText et_user_email;

    private String phone;

    private EditText et_user_nice;

    private EditText et_user_school;

    private RelativeLayout sex;

    private TextView mSexInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarCompat.compat(this);

        Bmob.initialize(this, Config.applicationId);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        View view = findViewById(R.id.register_top);
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
        mTitle.setText("注册");
        et_username = (TextView) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_email = (EditText) findViewById(R.id.et_email);
        et_user_email = (EditText) findViewById(R.id.et_user_email);
        sex = (RelativeLayout) findViewById(R.id.rl_sex);
        mSexInfo = (TextView) findViewById(R.id.sex_info);
        et_user_nice = (EditText) findViewById(R.id.et_user_nice);
        et_user_school = (EditText) findViewById(R.id.et_user_school);
        et_username.setText(phone);
        sex.setOnClickListener(this);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                // TODO Auto-generated method stub
                register();
            }
        });
    }

    private void register()
    {

        String password = et_password.getText().toString();
        String pwd_again = et_email.getText().toString();
        String email = et_user_email.getText().toString();
        String nice = et_user_nice.getText().toString();
        String school = et_user_school.getText().toString();
        String sex = mSexInfo.getText().toString();

        if (TextUtils.isEmpty(password))
        {
            ShowToast(R.string.toast_error_password_null);
            return;
        }
        if (!pwd_again.equals(password))
        {
            ShowToast(R.string.toast_error_comfirm_password);
            return;
        }
        if (TextUtils.isEmpty(nice))
        {
            ToastUtil.ShortToast("昵称不能为空");
            return;
        }

        if (TextUtils.isEmpty(school))
        {
            ToastUtil.ShortToast("签名不能为空");
            return;
        }
        if (TextUtils.isEmpty(email))
        {
            ShowToast(R.string.toast_error_email);
            return;
        }
        if (TextUtils.isEmpty(sex))
        {
            ToastUtil.ShortToast("性别不能为空");
            return;
        }
        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if (!isNetConnected)
        {
            ShowToast(R.string.network_tips);
            return;
        }

        final LoadingDialog mLoadingDialog = new LoadingDialog(this, R.style.Loading);
        mLoadingDialog.setMessage("注册中...");
        mLoadingDialog.show();

        final User bu = new User();
        bu.setUsername(phone);
        bu.setPassword(password);
        if (sex.equals("男"))
        {
            bu.setSex(true);
        } else
        {
            bu.setSex(false);
        }
        bu.setNick(nice);
        bu.setDeviceType("android");
        bu.setEmail(email);
        bu.setGerder(RestUtils.SEX_FEMALE);
        bu.setSignature(school);
        bu.setMobilePhoneNumber(phone);
        bu.setInstallId(BmobInstallation.getInstallationId(this));
        bu.signUp(RegisterActivity.this, new SaveListener()
        {

            @Override
            public void onSuccess()
            {
                // TODO Auto-generated method stub
                mLoadingDialog.dismiss();
                ShowToast("注册成功");
                userManager.bindInstallationForRegister(bu.getUsername());
                sendBroadcast(new Intent(RestUtils.ACTION_REGISTER_SUCCESS_FINISH));
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                mLoadingDialog.dismiss();
            }
        });
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
                String sex = data.getStringExtra("sex");
                mSexInfo.setText(sex);
            }
        }
    }

    @Override
    public void onClick(View v)
    {

        if (v.getId() == R.id.rl_sex)
        {
            Intent mIntent = new Intent(RegisterActivity.this, GenderActivity.class);
            startActivityForResult(mIntent, 100);
        }
    }
}
