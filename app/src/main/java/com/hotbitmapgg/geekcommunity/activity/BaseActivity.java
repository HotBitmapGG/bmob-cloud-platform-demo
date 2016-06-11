package com.hotbitmapgg.geekcommunity.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.utils.CollectionUtils;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.widget.DialogTips;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;


public class BaseActivity extends FragmentActivity
{

    BmobUserManager userManager;

    BmobChatManager manager;

    HomeMsgApplication mApplication;

    protected int mScreenWidth;

    protected int mScreenHeight;

    private LoadingDialog mDialog;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        userManager = BmobUserManager.getInstance(this);
        manager = BmobChatManager.getInstance(this);
        mApplication = HomeMsgApplication.getInstance();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
    }


    public void ShowToast(final String text)
    {

        if (!TextUtils.isEmpty(text))
        {
            runOnUiThread(new Runnable()
            {

                @Override
                public void run()
                {
                    // TODO Auto-generated method stub
                    if (mToast == null)
                    {
                        mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                    } else
                    {
                        mToast.setText(text);
                        mToast.setDuration(Toast.LENGTH_SHORT);
                    }
                    mToast.show();
                }
            });
        }
    }

    public void ShowToast(final int resId)
    {

        runOnUiThread(new Runnable()
        {

            @Override
            public void run()
            {
                // TODO Auto-generated method stub
                if (mToast == null)
                {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId, Toast.LENGTH_LONG);
                } else
                {
                    mToast.setText(resId);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
        });
    }


    public void ShowLog(String msg)
    {

        LogUtil.lsw(msg);
    }


    public void showOfflineDialog(final Context context)
    {

        DialogTips dialog = new DialogTips(this, "您的账号已在其他设备上登录!", "重新登录");
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialogInterface, int userId)
            {

                HomeMsgApplication.getInstance().logout();
                startActivity(new Intent(context, LoginActivity.class));
                finish();
                dialogInterface.dismiss();
            }
        });
        dialog.show();
        dialog = null;
    }

    public void startAnimActivity(Class<?> cla)
    {

        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent)
    {

        this.startActivity(intent);
    }

    public void updateUserInfos()
    {

        userManager.queryCurrentContactList(new FindListener<BmobChatUser>()
        {

            @Override
            public void onError(int errorCode, String errorMsg)
            {
                // TODO Auto-generated method stub
                if (errorCode == BmobConfig.CODE_COMMON_NONE)
                {
                    ShowLog(errorMsg);
                } else
                {
                    ShowLog("查询好友列表失败" + errorMsg);
                }
            }

            @Override
            public void onSuccess(List<BmobChatUser> bmobChatUsers)
            {

                HomeMsgApplication.getInstance().setContactList(CollectionUtils.list2map(bmobChatUsers));
            }
        });
    }


    public void showLoadingDialog(String msg)
    {

        if (this.isFinishing())
        {
            return;
        }

        if (mDialog == null)
        {
            mDialog = new LoadingDialog(this, R.style.Loading);
            mDialog.setMessage(msg);
            if (!mDialog.isShowing())
            {
                mDialog.isShowing();
            }
        }
    }

    protected void setDialogMesssage(String msg)
    {

        if (mDialog == null)
        {
            mDialog = new LoadingDialog(this, R.style.Loading);
        }

        if (mDialog != null)
        {
            if (!mDialog.isShowing())
            {
                mDialog.show();
            }
            mDialog.setMessage(msg);
        }
    }

    public void showDialog()
    {

        showLoadingDialog("加载中...");
    }

    public void dismissDialog()
    {

        if (mDialog.isShowing())
        {
            mDialog.dismiss();
        }
    }
}
