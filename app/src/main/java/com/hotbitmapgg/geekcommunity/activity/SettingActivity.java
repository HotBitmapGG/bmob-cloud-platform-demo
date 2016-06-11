package com.hotbitmapgg.geekcommunity.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.utils.HomeMessageTools;
import com.hotbitmapgg.geekcommunity.utils.InputMethodUtil;
import com.hotbitmapgg.geekcommunity.utils.PreferencesUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;


public class SettingActivity extends ParentActivity implements OnClickListener
{


    private Button btn_logout;


    private RelativeLayout mCommonSetting;


    private RelativeLayout mIdea;


    private RelativeLayout mUpdata;

    private RelativeLayout mAbout;


    private TextView mVersionCode;

    private RelativeLayout mUpdatePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        StatusBarCompat.compat(this);

        initView();
    }

    private void initView()
    {

        initTitle();

        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);

        mVersionCode = (TextView) findViewById(R.id.app_version_code);
        mCommonSetting = (RelativeLayout) findViewById(R.id.rl_common_setting);
        mCommonSetting.setOnClickListener(this);
        mIdea = (RelativeLayout) findViewById(R.id.rl_idea);
        mUpdata = (RelativeLayout) findViewById(R.id.rl_update);
        mAbout = (RelativeLayout) findViewById(R.id.rl_about);
        mIdea.setOnClickListener(this);
        mUpdata.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        String versionName = HomeMessageTools.getVersionName(SettingActivity.this);
        mVersionCode.setText("V" + versionName);
        mUpdatePassword = (RelativeLayout) findViewById(R.id.layout_update_password);
        mUpdatePassword.setOnClickListener(this);
    }


    private void initTitle()
    {

        View view = findViewById(R.id.setting_top);
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
        mTitle.setText("设置");
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {

            case R.id.btn_logout:
                HomeMsgApplication.getInstance().logout();
                SettingActivity.this.finish();
                Intent mIntent = new Intent(SettingActivity.this, LoginActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mIntent);
                break;

            case R.id.rl_common_setting:
                startAnimActivity(new Intent(SettingActivity.this, CommonSettingActivity.class));
                break;

            case R.id.rl_idea:
                startAnimActivity(new Intent(SettingActivity.this, FeedBackActivity.class));

                break;

            case R.id.rl_update:

                break;

            case R.id.rl_about:
                startAnimActivity(new Intent(SettingActivity.this, AppIntroduceActivity.class));
                break;

            case R.id.layout_update_password:

                changePassword();
                break;
            default:
                break;
        }
    }

    private void changePassword()
    {

        final AlertDialog changePwdDialog = new AlertDialog.Builder(SettingActivity.this).create();
        changePwdDialog.show();
        Window window = changePwdDialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_change_pwd);
        final EditText editText = (EditText) window.findViewById(R.id.et_pwd);
        window.findViewById(R.id.btn_cancel).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                changePwdDialog.dismiss();
            }
        });

        window.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                validatePassword(changePwdDialog, editText.getText().toString());
            }
        });

        Display d = getWindow().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = changePwdDialog.getWindow().getAttributes();
        layoutParams.width = (int) (d.getWidth() * 0.9);
        changePwdDialog.getWindow().setAttributes(layoutParams);
        InputMethodUtil.showInputMethod(this, editText, 200);
    }

    private void validatePassword(final AlertDialog dialog, String pwd)
    {

        if (TextUtils.isEmpty(pwd))
        {
            ToastUtil.ShortToast("旧密码不能为空");
            return;
        }
        // ��ȡ�û���ǰ����
        String oldPassword = PreferencesUtil.getStringData(SettingActivity.this, RestUtils.PASSWORD_KEY);
        if (oldPassword.equals(pwd))
        {
            dialog.dismiss();
            Intent mIntent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
            mIntent.putExtra("oldPwd", pwd);
            startActivity(mIntent);
        } else
        {
            ToastUtil.ShortToast("旧密码输入错误");
        }
    }
}
