package com.hotbitmapgg.geekcommunity.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义ListView
 *
 * @author Administrator
 * @hcc
 */
public class CustomListView extends ListView
{

    public CustomListView(Context context)
    {

        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs)
    {

        super(context, attrs);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
