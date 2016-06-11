package com.hotbitmapgg.geekcommunity.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.activity.AlumnusDetailsActivity;
import com.hotbitmapgg.geekcommunity.alumnus.utils.ImgView;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.bean.AlummusBean;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.db.DatabaseUtil;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.widget.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;

public class UserAlumnusContentAdapter extends BaseContentAdapter<AlummusBean>
{

    public static final String TAG = "AIContentAdapter";

    public static final int SAVE_FAVOURITE = 2;

    public UserAlumnusContentAdapter(Context context, List<AlummusBean> list)
    {

        super(context, list);
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent)
    {

        final ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.ai_item, null);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.user_name);
            viewHolder.userLogo = (RoundedImageView) convertView.findViewById(R.id.user_logo);
            viewHolder.date = (TextView) convertView.findViewById(R.id.item_action_date);
            viewHolder.contentText = (TextView) convertView.findViewById(R.id.content_text);
            viewHolder.imageLayout = (LinearLayout) convertView.findViewById(R.id.img_lyt);
            viewHolder.love = (LinearLayout) convertView.findViewById(R.id.item_action_love);
            viewHolder.LoveNum = (TextView) convertView.findViewById(R.id.tv_love);
            viewHolder.loveImage = (ImageView) convertView.findViewById(R.id.iv_love);
            viewHolder.comment = (LinearLayout) convertView.findViewById(R.id.item_action_comment);
            viewHolder.fromPhone = (TextView) convertView.findViewById(R.id.tv_from_lbs);
            viewHolder.sex = (ImageView) convertView.findViewById(R.id.user_sex);
            viewHolder.commentTv = (TextView) convertView.findViewById(R.id.tv_comment);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AlummusBean entity = dataList.get(position);

        final User user = entity.getAuthor();
        if (user == null)
        {
            LogUtil.lsw("USER IS NULL");
        }
        if (user.getAvatar() == null)
        {
            LogUtil.lsw("USER avatar IS NULL");
        }
        String avatarUrl = null;
        if (user.getAvatars() != null)
        {
            avatarUrl = user.getAvatars().getFileUrl(mContext);
        }
        if (avatarUrl != null)
        {
            ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.userLogo, ImageLoadUtil.defaultOptions());
        } else
        {
            viewHolder.userLogo.setImageResource(R.drawable.ico_user_default);
        }
        User author = entity.getAuthor();
        String nick = author.getNick();
        String username = author.getUsername();
        if (!TextUtils.isEmpty(nick))
        {
            viewHolder.userName.setText(nick);
        } else
        {
            if (!TextUtils.isEmpty(username))
            {
                viewHolder.userName.setText(username);
            }
        }

        viewHolder.contentText.setText(entity.getContent());
        // 添加图片
        viewHolder.imageLayout.removeAllViews();
        List<BmobFile> bmobFiles = entity.getContentfigureurls();
        if (bmobFiles != null && bmobFiles.size() > 0)
        {
            List<String> urls = new ArrayList<String>();
            for (int i = 0; i < bmobFiles.size(); i++)
            {
                BmobFile bmobFile = bmobFiles.get(i);

                String fileUrl = bmobFile.getUrl();
                urls.add(fileUrl);
            }

            if (!urls.isEmpty())
            {

                View view = ImgView.backview(mContext, urls, urls, urls);
                if (view != null)
                {
                    viewHolder.imageLayout.setVisibility(View.VISIBLE);
                    viewHolder.imageLayout.addView(view);
                }
            } else
            {
                viewHolder.imageLayout.setVisibility(View.GONE);
            }
        } else
        {
            viewHolder.imageLayout.setVisibility(View.GONE);
        }

        viewHolder.LoveNum.setText(entity.getLove() + "");
        if (entity.getMyLove())
        {
            viewHolder.loveImage.setImageResource(R.drawable.ic_a_praise_full);
            viewHolder.LoveNum.setTextColor(mContext.getResources().getColor(R.color.blue_main));
        } else
        {
            viewHolder.loveImage.setImageResource(R.drawable.ic_a_praise);
            viewHolder.LoveNum.setTextColor(mContext.getResources().getColor(R.color.black_alpha));
        }
        viewHolder.love.setOnClickListener(new OnClickListener()
        {

            boolean oldFav = entity.getMyFav();

            @Override
            public void onClick(View v)
            {

                if (entity.getMyLove())
                {

                    return;
                }

                if (DatabaseUtil.getInstance(mContext).isLoved(entity))
                {

                    entity.setMyLove(true);
                    entity.setLove(entity.getLove() + 1);
                    viewHolder.LoveNum.setTextColor(mContext.getResources().getColor(R.color.blue_main));
                    viewHolder.LoveNum.setText(entity.getLove() + "");
                    viewHolder.loveImage.setImageResource(R.drawable.ic_a_praise_full);
                    viewHolder.loveImage.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_small));

                    return;
                }

                entity.setLove(entity.getLove() + 1);
                viewHolder.LoveNum.setTextColor(mContext.getResources().getColor(R.color.blue_main));
                viewHolder.LoveNum.setText(entity.getLove() + "");
                viewHolder.loveImage.setImageResource(R.drawable.ic_a_praise_full);
                viewHolder.loveImage.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_small));
                User currentUser = HomeMsgApplication.getInstance().getCurrentUser();
                String name = currentUser.getNick();
                List<String> names = entity.getNames();
                names.add(name);
                entity.setNames(names);

                if (entity.getMyFav())
                {
                    entity.setMyFav(false);
                }
                entity.update(mContext, new UpdateListener()
                {

                    @Override
                    public void onSuccess()
                    {

                        entity.setMyLove(true);
                        entity.setMyFav(oldFav);
                        DatabaseUtil.getInstance(mContext).insertFav(entity);
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg)
                    {

                        entity.setMyLove(true);
                        entity.setMyFav(oldFav);
                    }
                });
            }
        });

        viewHolder.comment.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                // 圈子详情界面
                Intent intent = new Intent();
                intent.setClass(mContext, AlumnusDetailsActivity.class);
                intent.putExtra("data", entity);
                intent.putExtra("flag", "1");
                mContext.startActivity(intent);
            }
        });

        // 设置发布时间
        String date = entity.getCreatedAt();
        if (!TextUtils.isEmpty(date))
        {
            viewHolder.date.setText(date);
        } else
        {
            viewHolder.date.setText("");
        }

        // 设置手机型号
        String formPhone = entity.getFormPhone();
        if (!TextUtils.isEmpty(formPhone))
        {
            viewHolder.fromPhone.setText(formPhone);
        }

        // 设置性别显示
        Boolean sex = user.getSex();
        if (sex)
        {
            viewHolder.sex.setImageResource(R.drawable.userinfo_icon_male);
        } else
        {
            viewHolder.sex.setImageResource(R.drawable.userinfo_icon_female);
        }

        int commentNum = entity.getCommentNum();
        if (commentNum != 0)
        {
            viewHolder.commentTv.setText(commentNum + "");
        } else
        {
            viewHolder.commentTv.setText("评论");
        }

        List<String> names = entity.getNames();
        if (names != null && names.size() > 0)
        {
            for (String string : names)
            {
                LogUtil.lsw(string);
            }
        }

        return convertView;
    }

    public static class ViewHolder
    {

        public RoundedImageView userLogo;

        public TextView userName;

        public TextView contentText;

        public LinearLayout imageLayout;

        public LinearLayout love;

        public TextView LoveNum;

        public ImageView loveImage;

        public TextView date;

        public LinearLayout comment;

        public TextView fromPhone;

        public ImageView sex;

        public TextView commentTv;
    }
}