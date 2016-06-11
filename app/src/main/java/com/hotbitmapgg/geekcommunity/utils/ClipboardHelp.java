package com.hotbitmapgg.geekcommunity.utils;

import android.content.Context;
import android.text.ClipboardManager;

public class ClipboardHelp
{

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public static void copy(Context context, String content)
    {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能
     *
     * @param context
     * @return
     */
    public static String paste(Context context)
    {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }
}
