package com.hotbitmapgg.geekcommunity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.UserAlumnusContentAdapter;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.bean.AlummusBean;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.db.DatabaseUtil;
import com.hotbitmapgg.geekcommunity.widget.pullzoomlistview.PullZoomListView;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;


public class UserAlumnusActivity extends ParentActivity implements OnScrollListener, OnClickListener
{

    private PullZoomListView mPullToRefreshListView;

    private ArrayList<AlummusBean> mAlumnusBeans = new ArrayList<AlummusBean>();

    private UserAlumnusContentAdapter mAdapter;

    private User mUser;

    private int pageNum;

    public enum RefreshType
    {
        REFRESH, LOAD_MORE
    }

    private RefreshType mRefreshType = RefreshType.LOAD_MORE;

    private TextView mTitle;

    private LinearLayout mRunningLayout;

    private ImageView mIvRunning;

    private AnimationDrawable mAnimationDrawable;

    private FrameLayout mPullLayout;

    private int headerHeight;

    private RelativeLayout mTitleBar;

    private LinearLayout mEmptyView;

    private TextView mErrorText;

    private Button mErrorButton;

    private ImageView mErrorImage;

    private Handler mHandler = new Handler()
    {

        public void handleMessage(android.os.Message msg)
        {

            if (msg.what == 0)
            {
                mPullLayout.setVisibility(View.VISIBLE);
                mRunningLayout.setVisibility(View.GONE);
                mAnimationDrawable.stop();

                mAdapter = new UserAlumnusContentAdapter(UserAlumnusActivity.this, mAlumnusBeans);
                mPullToRefreshListView.setAdapter(mAdapter);
            } else if (msg.what == 1)
            {
                loadMoreUserAlumnus();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_alumnus);

        int parseColor = Color.parseColor("#2DBBEB");
        StatusBarCompat.compat(this, parseColor);

        mUser = HomeMsgApplication.getInstance().getmAlummusBean().getAuthor();

        initTitle();
        initView();
        initMyPublish();
        loadUserAlumnus();
    }

    private void refreshUI()
    {

        mPullLayout.setVisibility(View.GONE);
        mRunningLayout.setVisibility(View.VISIBLE);
        hideEmptyView();
        loadUserAlumnus();
    }


    private void initTitle()
    {

        ImageView mleftBack = (ImageView) findViewById(R.id.alumnus_left_btn);
        mTitle = (TextView) findViewById(R.id.alumnus_top_title);
        mleftBack.setVisibility(View.VISIBLE);
        mleftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
            }
        });
    }

    private void initView()
    {

        mTitleBar = (RelativeLayout) findViewById(R.id.top_rl);
        mTitleBar.getBackground().setAlpha(0);
        mPullToRefreshListView = (PullZoomListView) findViewById(R.id.user_alumnus_pull_list);
        mPullLayout = (FrameLayout) findViewById(R.id.user_alumnus_pull_layout);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mErrorText = (TextView) findViewById(R.id.error_tv);
        mErrorButton = (Button) findViewById(R.id.error_btn);
        mErrorImage = (ImageView) findViewById(R.id.error_iv);
        mErrorButton.setOnClickListener(this);
        mPullToRefreshListView.getHeaderImageView().setBackgroundResource(R.drawable.ciyuan_topic_cover_background);
        mPullToRefreshListView.setOnScrollListener(this);
        mPullToRefreshListView.setOnRefreshListener(new PullZoomListView.OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {

            }

            @Override
            public void onLoadMore()
            {

                mRefreshType = RefreshType.LOAD_MORE;
                mHandler.sendEmptyMessageDelayed(1, 2000);
            }
        });
        String photoUrl = mUser.getAvatar();
        if (!TextUtils.isEmpty(photoUrl))
        {
            mPullToRefreshListView.setPullImageByUrl(photoUrl);
        } else
        {
            mPullToRefreshListView.setPullImageByRes(R.drawable.ico_user_default);
        }

        String nickName = mUser.getNick();
        mPullToRefreshListView.setPullText(nickName);

        Boolean sex = mUser.getSex();
        if (sex)
        {
            mPullToRefreshListView.setPullTagByRes(R.drawable.userinfo_icon_male);
        } else
        {
            mPullToRefreshListView.setPullTagByRes(R.drawable.userinfo_icon_female);
        }

        updatePersonalInfo(mUser);
    }

    private void initMyPublish()
    {

        if (isCurrentUser(mUser))
        {

            User user = BmobUser.getCurrentUser(UserAlumnusActivity.this, User.class);
            updatePersonalInfo(user);
        } else
        {
            if (mUser != null)
            {
                String nick = mUser.getNick();
                if (!TextUtils.isEmpty(nick))
                {
                    mTitle.setText(nick);
                } else
                {
                    String username = mUser.getUsername();
                    if (!TextUtils.isEmpty(username))
                    {
                        mTitle.setText(username);
                    }
                }
            }
        }
        mPullToRefreshListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                if (position == 0)
                {
                    return;
                }
                AlummusBean alummusBean = mAdapter.getItem(position - 1);
                Intent intent = new Intent();
                intent.setClass(UserAlumnusActivity.this, AlumnusDetailsActivity.class);
                intent.putExtra("data", alummusBean);
                intent.putExtra("flag", "0");
                startActivity(intent);
            }
        });
    }

    private void updatePersonalInfo(User user)
    {

        String username = user.getUsername();
        String nick = user.getNick();
        if (!TextUtils.isEmpty(nick))
        {

            mTitle.setText(nick);
        } else
        {
            if (!TextUtils.isEmpty(username))
            {

                mTitle.setText(username);
            }
        }
    }

    private boolean isCurrentUser(User user)
    {

        if (null != user)
        {
            User cUser = BmobUser.getCurrentUser(UserAlumnusActivity.this, User.class);
            if (cUser != null && cUser.getObjectId().equals(user.getObjectId()))
            {
                return true;
            }
        }
        return false;
    }

    private void loadUserAlumnus()
    {

        BmobQuery<AlummusBean> query = new BmobQuery<AlummusBean>();
        query.setLimit(RestUtils.NUMBERS_PER_PAGE);
        query.setSkip(RestUtils.NUMBERS_PER_PAGE * (pageNum++));
        query.order("-createdAt");
        query.include("author");
        query.addWhereEqualTo("author", mUser);
        query.findObjects(UserAlumnusActivity.this, new FindListener<AlummusBean>()
        {

            @Override
            public void onSuccess(List<AlummusBean> data)
            {

                if (data.size() != 0 && data.get(data.size() - 1) != null)
                {
                    hideEmptyView();
                    if (mRefreshType == RefreshType.REFRESH)
                    {
                        mAlumnusBeans.clear();
                    }
                    if (HomeMsgApplication.getInstance().getCurrentUser() != null)
                    {
                        data = DatabaseUtil.getInstance(UserAlumnusActivity.this).setFav(data);
                    }
                    if (data.size() < RestUtils.NUMBERS_PER_PAGE)
                    {
                        mPullToRefreshListView.allDataLoadingComplete();
                    }
                    mAlumnusBeans.addAll(data);
                    mHandler.sendEmptyMessageDelayed(0, 3000);
                } else
                {
                    showEmptyViewByNoData();
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg)
            {

                showEmptyViewByNoNetwork();
            }
        });
    }

    private void loadMoreUserAlumnus()
    {

        BmobQuery<AlummusBean> query = new BmobQuery<AlummusBean>();
        query.setLimit(RestUtils.NUMBERS_PER_PAGE);
        query.setSkip(RestUtils.NUMBERS_PER_PAGE * (pageNum++));
        query.order("-createdAt");
        query.include("author");
        query.addWhereEqualTo("author", mUser);
        query.findObjects(UserAlumnusActivity.this, new FindListener<AlummusBean>()
        {

            @Override
            public void onSuccess(List<AlummusBean> data)
            {

                if (data.size() != 0 && data.get(data.size() - 1) != null)
                {
                    if (mRefreshType == RefreshType.REFRESH)
                    {
                        mAlumnusBeans.clear();
                    }

                    if (data.size() < RestUtils.NUMBERS_PER_PAGE)
                    {
                        mPullToRefreshListView.allDataLoadingComplete();
                    }
                    if (HomeMsgApplication.getInstance().getCurrentUser() != null)
                    {
                        data = DatabaseUtil.getInstance(UserAlumnusActivity.this).setFav(data);
                    }
                    mAlumnusBeans.addAll(data);
                    mAdapter.notifyDataSetChanged();
                    mPullToRefreshListView.loadingComplete();
                } else
                {
                    pageNum--;
                    mPullToRefreshListView.loadingComplete();
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg)
            {

                pageNum--;
                mPullToRefreshListView.loadingComplete();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public void showEmptyViewByNoData()
    {

        mRunningLayout.setVisibility(View.GONE);
        mPullLayout.setVisibility(View.GONE);
        mAnimationDrawable.stop();
        mEmptyView.setVisibility(View.VISIBLE);
       // mErrorText.setText(mUser.getNick() + "还没有发表动态!");
        mErrorImage.setImageResource(R.drawable.ic_bottomtabbar_notification);
        mErrorButton.setVisibility(View.GONE);
    }

    public void showEmptyViewByNoNetwork()
    {

        mRunningLayout.setVisibility(View.GONE);
        mPullLayout.setVisibility(View.GONE);
        mAnimationDrawable.stop();
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorText.setText("网络连接超时!");
       // mErrorImage.setImageResource(R.drawable.ic_bottomtabbar_notification);
        mErrorButton.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView()
    {

        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {

        if (firstVisibleItem == 0)
        {
            View child = view.getChildAt(0);
            if (child != null)
            {
                int top = -child.getTop();
                headerHeight = child.getHeight();
                if (top <= headerHeight && top >= 0)
                {
                    float f = (float) top / (float) headerHeight;
                    mTitleBar.getBackground().setAlpha((int) (f * 255));
                    mTitleBar.invalidate();
                }
            }
        } else if (firstVisibleItem > 0)
        {
            mTitleBar.getBackground().setAlpha(255);
        } else
        {
            mTitleBar.getBackground().setAlpha(0);
        }
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.error_btn:

                refreshUI();
                break;

            default:
                break;
        }
    }
}
