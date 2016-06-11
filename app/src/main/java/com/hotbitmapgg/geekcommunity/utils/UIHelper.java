package com.hotbitmapgg.geekcommunity.utils;

import android.app.Activity;
import android.content.Context;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;


public class UIHelper
{

    private static LoadingDialog dialog;

    public static void showDialog(Context context, String msg)
    {

        if (((Activity) context).isFinishing())
        {
            return;
        }
        dialog = new LoadingDialog(context, R.style.Loading);
        dialog.setMessage(msg);
        dialog.show();
    }

    public static void showDialog(Context context)
    {

        showDialog(context, "正在加载");
    }

    public static void dismissdialog()
    {

        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
