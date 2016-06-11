package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;


public class MenuAdapter extends BaseAdapter
{

    private Context mContext;

    int[] icon = {R.drawable.icon_noticy, R.drawable.icon_add_friend};

    String[] name = {"好友通知", "邀请好友"};

    public MenuAdapter(Context context)
    {

        mContext = context;
    }

    @Override
    public int getCount()
    {

        return name.length;
    }

    @Override
    public Object getItem(int position)
    {

        return name[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder = null;
        if (convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.pop_adapter, null);
            holder.photoView = (ImageView) convertView.findViewById(R.id.btnchat);
            holder.senderuser = (TextView) convertView.findViewById(R.id.btnname);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.photoView.setImageResource(icon[position]);
        holder.senderuser.setText(name[position]);
        return convertView;
    }

    static class ViewHolder
    {

        public ImageView photoView; // 头像

        public TextView senderuser; // 发送人名
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
