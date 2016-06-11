package com.hotbitmapgg.geekcommunity.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author Administrator
 * @hcc
 */
public class StringUtils
{

    // 手机号正则
    private static final Pattern PHONE_NO = Pattern.compile("^[1]\\d{10}$");

    // 邮箱正则
    private static final Pattern EMALI_NO = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");

    /**
     * 验证手机号是否合法
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneValid(final String phone)
    {

        if (TextUtils.isEmpty(phone))
        {
            return false;
        }
        final Matcher matcher = PHONE_NO.matcher(phone);
        return matcher.matches();
    }

    /**
     * 验证邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(final String email)
    {

        if (TextUtils.isEmpty(email))
        {
            return false;
        }

        final Matcher matcher = EMALI_NO.matcher(email);

        return matcher.matches();
    }
}
