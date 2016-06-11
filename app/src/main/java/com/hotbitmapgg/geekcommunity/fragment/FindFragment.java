package com.hotbitmapgg.geekcommunity.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.activity.AlumnusCircleActivity;
import com.hotbitmapgg.geekcommunity.activity.FragmentBase;
import com.hotbitmapgg.geekcommunity.bean.AlummusBean;
import com.hotbitmapgg.geekcommunity.utils.DisplayUtil;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;

import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;


/**
 * 发现
 *
 * @author Administrator
 * @hcc
 */
public class FindFragment extends FragmentBase implements OnClickListener
{

    // 朋友圈
    private RelativeLayout mFriendsCircle;

    // 是否隐藏
    private boolean hidden;

    private ImageView mDataUpdateShow;

    private int updateNum = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_find, container, false);

        View topView = view.findViewById(R.id.find_top);
        TextView mTextView = (TextView) topView.findViewById(R.id.find_top_title);
        RelativeLayout mLayout = (RelativeLayout) topView.findViewById(R.id.find_top_rl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            android.view.ViewGroup.LayoutParams layoutParams = mLayout.getLayoutParams();
            int height = DisplayUtil.dip2px(getActivity(), 54);
            int topMargin = DisplayUtil.dip2px(getActivity(), 20);
            layoutParams.height = height;
            mLayout.setLayoutParams(layoutParams);
            android.widget.RelativeLayout.LayoutParams mTitleLayoutParams = (android.widget.RelativeLayout.LayoutParams) mTextView.getLayoutParams();
            mTitleLayoutParams.topMargin = topMargin;
            mTextView.setLayoutParams(mTitleLayoutParams);
        }

        // 初始化控件
        mFriendsCircle = (RelativeLayout) view.findViewById(R.id.rl_circle);

        mDataUpdateShow = (ImageView) view.findViewById(R.id.alumnus_update_show);

        mFriendsCircle.setOnClickListener(this);


        queryDataUpdate();

        return view;
    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
    }

    /**
     * 查询是否有新动态生成
     */
    public void queryDataUpdate()
    {

        BmobQuery<AlummusBean> query = new BmobQuery<AlummusBean>();
        query.order("-createdAt");
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        query.include("author");
        query.findObjects(getActivity(), new FindListener<AlummusBean>()
        {

            @Override
            public void onSuccess(List<AlummusBean> list)
            {

                if (list != null && list.size() > 0)
                {

                    updateNum = list.size();
                    LogUtil.lsw(updateNum + "");
                    mDataUpdateShow.setVisibility(View.VISIBLE);
                } else
                {
                    // 数据为空 显示错误界面
                    LogUtil.lsw("数据为空");
                    mDataUpdateShow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg)
            {

                // 加载数据失败
                LogUtil.lsw("数据加载失败");
                mDataUpdateShow.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {


            case R.id.rl_circle:
                // 进入圈子界面
                mDataUpdateShow.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), AlumnusCircleActivity.class));

                break;


            default:
                break;
        }
    }
}
