package com.hotbitmapgg.geekcommunity.activity;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;


public abstract class FragmentBase extends Fragment
{

    public BmobUserManager userManager;

    public BmobChatManager manager;

    protected View contentView;

    public LayoutInflater mInflater;

    Toast mToast;

    private Handler handler = new Handler();

    public void runOnWorkThread(Runnable action)
    {

        new Thread(action).start();
    }

    public void runOnUiThread(Runnable action)
    {

        handler.post(action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mApplication = HomeMsgApplication.getInstance();
        userManager = BmobUserManager.getInstance(getActivity());
        manager = BmobChatManager.getInstance(getActivity());
        mInflater = LayoutInflater.from(getActivity());
    }

    public FragmentBase()
    {

    }

    public void ShowToast(String text)
    {

        if (mToast == null)
        {
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        } else
        {
            mToast.setText(text);
        }
        mToast.show();
    }

    public void ShowToast(int text)
    {

        if (mToast == null)
        {
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
        } else
        {
            mToast.setText(text);
        }
        mToast.show();
    }

    public void ShowLog(String msg)
    {

        LogUtil.lsw(msg);
    }

    public View findViewById(int paramInt)
    {

        return getView().findViewById(paramInt);
    }

    public HomeMsgApplication mApplication;

    public void startAnimActivity(Intent intent)
    {

        this.startActivity(intent);
    }

    public void startAnimActivity(Class<?> cla)
    {

        getActivity().startActivity(new Intent(getActivity(), cla));
    }
}
