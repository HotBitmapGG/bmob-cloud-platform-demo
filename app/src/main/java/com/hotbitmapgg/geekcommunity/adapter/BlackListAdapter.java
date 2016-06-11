package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;

public class BlackListAdapter extends BaseListAdapter<BmobChatUser>
{

	public BlackListAdapter(Context context, List<BmobChatUser> list)
	{
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.item_blacklist, null);
		}
		final BmobChatUser contract = getList().get(position);
		TextView tv_friend_name = ViewHolder.get(convertView, R.id.tv_friend_name);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.img_friend_avatar);
		String avatar = contract.getAvatar();
		if (avatar != null && !avatar.equals(""))
		{
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadUtil.defaultOptions());
		}
		else
		{
			iv_avatar.setImageResource(R.drawable.ico_user_default);
		}
		tv_friend_name.setText(contract.getUsername());
		return convertView;
	}

}
