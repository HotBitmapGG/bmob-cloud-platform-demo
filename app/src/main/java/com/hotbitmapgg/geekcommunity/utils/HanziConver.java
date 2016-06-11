package com.hotbitmapgg.geekcommunity.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

public class HanziConver
{

    private final String ZI_MU = "[abcdefghijklmnopqrstuvwxyz]";

    private String HAN_ZI;

    private static HanziConver inst;

    private HanziConver(Context context)
    {

        getHAN_ZI(context.getAssets());
    }

    public static HanziConver getInst(Context context)
    {

        if (inst == null)
        {
            inst = new HanziConver(context);
        }
        return inst;
    }

    /**
     * 返回声母 获取汉字编码表
     *
     * @return hanZi 汉字编码表
     */
    private final String getHAN_ZI(AssetManager am)
    {

        if (HAN_ZI == null)
        {
            try
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(am.open("hanzi.txt")));

                HAN_ZI = br.readLine();
                br.close();
            } catch (IOException e)
            {
                HAN_ZI = "";
            }
        }
        return HAN_ZI;
    }

    public String[] getPinYinFromFile(String zhongwen)
    {

        StringBuilder sb0 = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();

        for (int i = 0; i < zhongwen.length(); i++)
        {
            String str = zhongwen.substring(i, i + 1);
            if (ZI_MU.indexOf(str) > -1)
            {
                sb0.append(str);
                sb1.append(str);
                continue;
            }
            int index = HAN_ZI.indexOf(str);
            if (index > -1)
            {
                str = HAN_ZI.substring(HAN_ZI.indexOf("[", index) + 1, HAN_ZI.indexOf("]", index));
                sb0.append(str);
                sb1.append(str.substring(0, 1));
            } else if (!TextUtils.isEmpty(str))
            {
                sb0.append(str);
                sb1.append(str);
            }
        }

        String[] zhongWenPinYin = {sb0.toString(), sb1.toString()};
        return zhongWenPinYin;
    }

    private final Collator COLLATOR = Collator.getInstance();

    private String[] NORM_STRINGS = null;

    private String[] getNormStrings()
    {

        if (NORM_STRINGS == null)
        {
            NORM_STRINGS = new String['z' - 'a' + 1];
            COLLATOR.setStrength(Collator.PRIMARY);
            for (char b = 'a'; b <= 'z'; b++)
            {
                NORM_STRINGS[b - 'a'] = new String(new char[]{b});
            }
        }
        return NORM_STRINGS;
    }

    /**
     * @param 将拼音转换为T9编码 便于存储到数据库，供过滤查找
     * @return
     */
    public String toT9Code(String name)
    {

        int nameLen = name.length();
        if (nameLen == 0)
            return "";
        char[] src = name.toCharArray();

        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < nameLen; i++)
        {
            c = mapToPhone(src[i]);
            if (c != 0)
            {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * @param alpha 一个字母
     * @return char 返回值:T9键盘数字
     */
    private char mapToPhone(char alpha)
    {

        if (Character.isDigit(alpha) || alpha == '+')
            return alpha;

        if (alpha == ' ')
            return '#';

        char c = normalize(alpha);
        if (c < 'a' || c > 'z')
            return 0;
        if (c <= 'o')
        {
            int x = (c - 'a') / 3;
            return (char) ('2' + x);
        } else if (c >= 'p' && c <= 's')
        {
            return '7';
        } else if (c >= 't' && c <= 'v')
        {
            return '8';
        } else
        {
            return '9';
        }
    }

    /**
     * @param alpha
     * @return
     */
    private char normalize(final char alpha)
    {

        if (alpha == '\u00df')
        { // &szlig;
            return 's';
        }
        if (alpha == '\u00f8' || alpha == '\u00d8')
        { // &oslash; / &Oslash;
            return 'o';
        }
        final String source = new String(new char[]{alpha});
        final int compA = COLLATOR.compare(source, getNormStrings()[0]);
        if (compA == 0)
        {
            return 'a';
        }
        final int compZ = COLLATOR.compare(source, NORM_STRINGS[NORM_STRINGS.length - 1]);
        if (compZ == 0)
        {
            return 'z';
        }
        if (compA < 0 || compZ > 0)
        {
            return alpha;
        }
        for (char b = 'a' + 1; b < 'z'; b++)
        {
            if (COLLATOR.compare(source, NORM_STRINGS[b - 'a']) == 0)
            {
                return b;
            }
        }
        return alpha;
    }
}
