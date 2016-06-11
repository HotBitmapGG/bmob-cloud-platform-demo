package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.utils.CollectionUtils;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

public class NewFriendAdapter extends BaseListAdapter<BmobInvitation>
{

    public NewFriendAdapter(Context context, List<BmobInvitation> list)
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
        final BmobInvitation msg = getList().get(position);
        TextView name = ViewHolder.get(convertView, R.id.name);
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);

        final Button btn_add = ViewHolder.get(convertView, R.id.btn_add);

        String avatar = msg.getAvatar();

        if (avatar != null && !avatar.equals(""))
        {
            ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadUtil.defaultOptions());
        } else
        {
            iv_avatar.setImageResource(R.drawable.ico_user_default);
        }

        int status = msg.getStatus();
        if (status == BmobConfig.INVITE_ADD_NO_VALIDATION || status == BmobConfig.INVITE_ADD_NO_VALI_RECEIVED)
        {
            // btn_add.setText("同意");
            // btn_add.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_login_selector));
            // btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_white));
            btn_add.setOnClickListener(new OnClickListener()
            {

                @Override
                public void onClick(View view)
                {

                    agressAdd(btn_add, msg);
                }
            });
        } else if (status == BmobConfig.INVITE_ADD_AGREE)
        {
            btn_add.setText("已同意");
            btn_add.setBackgroundDrawable(null);
            btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
            btn_add.setEnabled(false);
        }
        name.setText(msg.getFromname());

        return convertView;
    }

    /**
     * 添加好友 agressAdd
     *
     * @param @param btn_add
     * @param @param msg
     * @return void
     * @throws
     * @Title: agressAdd
     * @Description: TODO
     */
    private void agressAdd(final Button btn_add, final BmobInvitation msg)
    {

        final LoadingDialog mLoadingDialog = new LoadingDialog(mContext, R.style.Loading);
        mLoadingDialog.setMessage("正在添加,请稍后...");
        mLoadingDialog.show();
        try
        {
            // 同意添加好友
            BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener()
            {

                @Override
                public void onSuccess()
                {

                    mLoadingDialog.dismiss();
                    btn_add.setText("已同意");
                    btn_add.setBackgroundDrawable(null);
                    btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
                    btn_add.setEnabled(false);
                    // 保存到application中方便比较
                    HomeMsgApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));
                }

                @Override
                public void onFailure(int errorCode, final String errorMsg)
                {

                    mLoadingDialog.dismiss();
                    ShowToast("添加失败");
                }
            });
        } catch (final Exception e)
        {
            mLoadingDialog.dismiss();
            ShowToast("添加失败");
        }
    }
}
