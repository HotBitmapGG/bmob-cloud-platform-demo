package com.hotbitmapgg.geekcommunity.widget;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 解决ListView嵌套ScrollView数据显示不全的问题
 *
 * @author Administrator
 * @hcc
 */
public class ListViewHeightUtil
{

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        ((MarginLayoutParams) params).setMargins(15, 15, 15, 15);
        listView.setLayoutParams(params);
    }
}
