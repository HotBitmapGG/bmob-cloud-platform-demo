package com.hotbitmapgg.geekcommunity.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.config.Config;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.utils.CommonUtils;
import com.hotbitmapgg.geekcommunity.utils.PreferencesUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.SaveListener;


public class LoginActivity extends BaseActivity implements OnClickListener
{


    private EditText et_username;


    private EditText et_password;

    private Button btn_login;

    private ImageView btn_register;

    private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

    private ImageView mDeleteUserName;

    private CheckBox mSaveCheckBox;

    private LinearLayout mSave;

    private int flag = 0;

    private LinearLayout mFindPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StatusBarCompat.compat(this);

        Bmob.initialize(this, Config.applicationId);
        init();
        initTitle();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RestUtils.ACTION_REGISTER_SUCCESS_FINISH);
        registerReceiver(receiver, filter);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.login_top);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mTitle.setText("登录");
    }

    private void init()
    {

        String username = PreferencesUtil.getStringData(LoginActivity.this, RestUtils.USERNAME_KEY);
        String password = PreferencesUtil.getStringData(LoginActivity.this, RestUtils.PASSWORD_KEY);

        mDeleteUserName = (ImageView) findViewById(R.id.delete_username);
        mSaveCheckBox = (CheckBox) findViewById(R.id.save_pwd_check);
        mSave = (LinearLayout) findViewById(R.id.save_password);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (ImageView) findViewById(R.id.btn_register);
        mFindPassword = (LinearLayout) findViewById(R.id.find_password);
        mFindPassword.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                Intent mIntent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(mIntent);
            }
        });

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        mSave.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                if (flag == 0)
                {
                    mSaveCheckBox.setChecked(true);
                    flag = 1;
                } else if (flag == 1)
                {
                    mSaveCheckBox.setChecked(false);
                    flag = 0;
                }
            }
        });
        mDeleteUserName.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                et_username.setText("");
                et_password.setText("");
                mSaveCheckBox.setChecked(false);
                mDeleteUserName.setVisibility(View.GONE);
                et_username.setFocusable(true);
                et_username.setFocusableInTouchMode(true);
                et_username.requestFocus();
                PreferencesUtil.setStringData(LoginActivity.this, RestUtils.USERNAME_KEY, "");
            }
        });

        if (username != null && username != "")
        {
            et_username.setText(username);
            et_username.setSelection(username.length());
            mDeleteUserName.setVisibility(View.VISIBLE);
        } else
        {
            mDeleteUserName.setVisibility(View.GONE);
        }

        if (username != null && password != null && username != "" && password != "")
        {
            et_username.setText(username);
            et_password.setText(password);
            et_username.setSelection(username.length());
            et_password.setSelection(password.length());
            mSaveCheckBox.setChecked(true);
        } else
        {
            mSaveCheckBox.setChecked(false);
        }

        et_username.setOnFocusChangeListener(new OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {

                if (hasFocus && et_username.getText().length() > 0)
                {
                    mDeleteUserName.setVisibility(View.VISIBLE);
                } else
                {
                    mDeleteUserName.setVisibility(View.GONE);
                }
            }
        });


        et_username.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
                et_password.setText("");
                mSaveCheckBox.setChecked(false);
                if (s.length() > 0)
                {
                    mDeleteUserName.setVisibility(View.VISIBLE);
                } else
                {
                    mDeleteUserName.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub

            }
        });

        mSaveCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                // TODO Auto-generated method stub
                if (isChecked)
                {
                    String username = et_username.getText().toString().trim();
                    String password = et_password.getText().toString().trim();
                    if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password))
                    {

                        PreferencesUtil.setStringData(LoginActivity.this, RestUtils.USERNAME_KEY, username);
                        PreferencesUtil.setStringData(LoginActivity.this, RestUtils.PASSWORD_KEY, password);
                    }
                } else
                {

                    PreferencesUtil.setStringData(LoginActivity.this, RestUtils.PASSWORD_KEY, "");
                }
            }
        });
    }

    public class MyBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            if (intent != null && RestUtils.ACTION_REGISTER_SUCCESS_FINISH.equals(intent.getAction()))
            {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.btn_login:

                boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
                if (!isNetConnected)
                {
                    ShowToast(R.string.network_tips);
                    return;
                }
                login();
                break;

            case R.id.btn_register:

                Intent intent = new Intent(LoginActivity.this, GetAuthCodeActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void login()
    {

        String name = et_username.getText().toString();
        String password = et_password.getText().toString();

        if (TextUtils.isEmpty(name))
        {
            ShowToast(R.string.toast_error_username_null);
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            ShowToast(R.string.toast_error_password_null);
            return;
        }

        PreferencesUtil.setStringData(LoginActivity.this, RestUtils.USERNAME_KEY, name);
        setDialogMesssage("登录中...");
        User user = new User();
        user.setUsername(name);
        user.setPassword(password);

        userManager.login(user, new SaveListener()
        {

            @Override
            public void onSuccess()
            {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable()
                {

                    @Override
                    public void run()
                    {

                        showDialog();
                    }
                });
                updateUserInfos();
                dismissDialog();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int errorcode, String error)
            {
                // TODO Auto-generated method stub
                dismissDialog();
                ToastUtil.ShortToast(error);
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
