package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.bean.AlumnusContentBean;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ypy.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.listener.DeleteListener;

public class AlumnusDetailsAdapter extends BaseContentAdapter<AlumnusContentBean>
{

	public AlumnusDetailsAdapter(Context context, List<AlumnusContentBean> list)
	{
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getConvertView(final int position, View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_alumnus_details_xml, null);
			viewHolder.userName = (TextView) convertView.findViewById(R.id.userName_comment);
			viewHolder.commentContent = (TextView) convertView.findViewById(R.id.content_comment);
			viewHolder.index = (TextView) convertView.findViewById(R.id.index_comment);
			viewHolder.userPhoto = (ImageView) convertView.findViewById(R.id.photo_comment);
			viewHolder.mDeleteMsg = (LinearLayout) convertView.findViewById(R.id.btn_del);
			viewHolder.mSexImage = (ImageView) convertView.findViewById(R.id.user_sex);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final AlumnusContentBean comment = dataList.get(position);
		User user = comment.getUser();
		if (user != null)
		{
			String nick = user.getNick();
			if (!TextUtils.isEmpty(nick))
			{
				viewHolder.userName.setText(nick);
			}
			else
			{
				String username = user.getUsername();
				if (!TextUtils.isEmpty(username))
				{
					viewHolder.userName.setText(username);
				}
			}

		}
		else
		{
			viewHolder.userName.setText("");
		}
		String avatar = user.getAvatar();
		if(!TextUtils.isEmpty(avatar))
		{
			ImageLoader.getInstance().displayImage(avatar, viewHolder.userPhoto, ImageLoadUtil.defaultOptions());
		}
		else
		{
			viewHolder.userPhoto.setImageResource(R.drawable.ico_user_default);
		}
		//设置发布评论时间
        long stringToLong = TimeUtil.stringToLong(comment.getCreatedAt(), "yyyy-MM-dd HH:mm:ss");
        String timestamp = TimeUtil.getDescriptionTimeFromTimestamp(stringToLong);
		viewHolder.index.setText(timestamp);
		viewHolder.commentContent.setText(comment.getCommentContent());
		User currentUser = HomeMsgApplication.getInstance().getCurrentUser();
		String username = currentUser.getUsername();
		if(username.equals(user.getUsername()))
		{
			// 显示删除
			viewHolder.mDeleteMsg.setVisibility(View.VISIBLE);
		}
		else
		{
			//隐藏删除
			viewHolder.mDeleteMsg.setVisibility(View.GONE);
		}
		viewHolder.mDeleteMsg.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				String objectId = comment.getObjectId();
				comment.setObjectId(objectId);
				comment.delete(mContext, new DeleteListener()
				{
					
					@Override
					public void onSuccess()
					{
						dataList.remove(position);
						notifyDataSetChanged();
						LogUtil.lsw("删除成功");
						//更新评论数量
						Bundle mBundle = new Bundle();
						mBundle.putString("msg", "nofity");
						EventBus.getDefault().post(mBundle);
						
						
					}
					
					@Override
					public void onFailure(int arg0, String arg1)
					{
						LogUtil.lsw("删除失败");
						
					}
				});
				
			}
		});
		
		Boolean sex = user.getSex();
		if (sex)
		{
			viewHolder.mSexImage.setImageResource(R.drawable.userinfo_icon_male);
		}
		else
		{
			viewHolder.mSexImage.setImageResource(R.drawable.userinfo_icon_female);
		}

		return convertView;
	}

	public static class ViewHolder
	{
		public TextView userName;
		
		public TextView commentContent;
		
		public TextView index;
		
		public ImageView userPhoto;
		
		public LinearLayout mDeleteMsg;
		
		public ImageView mSexImage;
	}
}
