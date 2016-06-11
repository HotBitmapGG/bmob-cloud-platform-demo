package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;
import com.hotbitmapgg.geekcommunity.widget.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

public class AddFriendAdapter extends BaseListAdapter<BmobChatUser>
{

    public AddFriendAdapter(Context context, List<BmobChatUser> list)
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
            convertView = mInflater.inflate(R.layout.item_add_friend, null);
        }
        final BmobChatUser contract = getList().get(position);
        TextView name = ViewHolder.get(convertView, R.id.name);
        RoundedImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);

        Button btn_add = ViewHolder.get(convertView, R.id.btn_add);

        String avatar = contract.getAvatar();

        if (avatar != null && !avatar.equals(""))
        {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadUtil.defaultOptions());
        } else
        {
            iv_avatar.setImageResource(R.drawable.ico_user_default);
        }

        name.setText(contract.getUsername());
        btn_add.setText("添加");
        btn_add.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                final LoadingDialog mLoadingDialog = new LoadingDialog(mContext, R.style.Loading);
                mLoadingDialog.setMessage("发送中...");
                mLoadingDialog.show();
                // 发送tag请求
                BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, contract.getObjectId(), new PushListener()
                {

                    @Override
                    public void onSuccess()
                    {

                        mLoadingDialog.dismiss();
                        ShowToast("发送成功,等待对方验证~");
                    }

                    @Override
                    public void onFailure(int errorCode, final String errorMsg)
                    {

                        mLoadingDialog.dismiss();
                        ShowToast("发送失败,请重新添加~");
                        LogUtil.lsw("发送添加好友请求失败 = " + errorMsg);
                    }
                });
            }
        });
        return convertView;
    }
}
