package com.hotbitmapgg.geekcommunity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.AIContentAdapter;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.bean.AlummusBean;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.config.Config;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.db.DatabaseUtil;
import com.hotbitmapgg.geekcommunity.widget.CircleProgressView;
import com.hotbitmapgg.geekcommunity.widget.pullzoomlistview.PullZoomListView;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobQuery.CachePolicy;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

@SuppressLint({"HandlerLeak", "InflateParams", "NewApi"})
public class AlumnusCircleActivity extends ParentActivity implements OnClickListener, OnScrollListener
{

    public enum RefreshType
    {
        REFRESH, LOAD_MORE
    }

    private int pageNum;

    private List<AlummusBean> datas = new ArrayList<AlummusBean>();

    private PullZoomListView mPullRefreshListView;

    private AIContentAdapter mAdapter;

    private RefreshType mRefreshType = RefreshType.LOAD_MORE;

    private RelativeLayout mTitleBar;

    private LinearLayout mRunningLayout;

    private FrameLayout mPullLayout;

    private ImageButton mAddPost;

    private LinearLayout mEmptyView;

    private TextView mErrorText;

    private Button mErrorButton;

    private ImageView mErrorImage;

    private ImageButton mCancleButton;

    private int headerHeight;

    private CircleProgressView mPullProgress;


    private Queue<String> images = new LinkedList<String>();

    private Handler mHandler = new Handler()
    {

        public void handleMessage(android.os.Message msg)

        {

            if (msg.what == 1)
            {
                mPullLayout.setVisibility(View.VISIBLE);
                mRunningLayout.setVisibility(View.GONE);
                mAddPost.setVisibility(View.VISIBLE);

                mAdapter = new AIContentAdapter(AlumnusCircleActivity.this, datas);
                mPullRefreshListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else if (msg.what == 2)
            {
                LoadMoreAlumnusData();
            } else if (msg.what == 3)
            {
                mAdapter = new AIContentAdapter(AlumnusCircleActivity.this, datas);
                mPullRefreshListView.setAdapter(mAdapter);
                mPullProgress.setVisibility(View.GONE);
                mPullProgress.stopSpinning();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnus);

        int parseColor = Color.parseColor("#2DBBEB");
        StatusBarCompat.compat(this, parseColor);
        Bmob.initialize(this, Config.applicationId);
        EventBus.getDefault().register(this);

        initTitle();
        initView();
        loadAlumnusData(true);
    }

    private void initHead()
    {

        String photoUrl = BmobUserManager.getInstance(AlumnusCircleActivity.this).getCurrentUser().getAvatar();
        if (!TextUtils.isEmpty(photoUrl))
        {
            mPullRefreshListView.setPullImageByUrl(photoUrl);
        } else
        {
            mPullRefreshListView.setPullImageByRes(R.drawable.ico_user_default);
        }

        String nickName = BmobUserManager.getInstance(AlumnusCircleActivity.this).getCurrentUser().getNick();
        mPullRefreshListView.setPullText(nickName);

        Boolean sex = BmobUser.getCurrentUser(HomeMsgApplication.getInstance(), User.class).getSex();
        if (sex)
        {
            mPullRefreshListView.setPullTagByRes(R.drawable.userinfo_icon_male);
        } else
        {
            mPullRefreshListView.setPullTagByRes(R.drawable.userinfo_icon_female);
        }

        mPullProgress = mPullRefreshListView.getPullProgress();
    }

    private void initTitle()
    {

        ImageView mLeftBack = (ImageView) findViewById(R.id.alumnus_left_btn);
        mLeftBack.setOnClickListener(this);
    }

    private void refreshUI()
    {

        mPullLayout.setVisibility(View.GONE);
        mAddPost.setVisibility(View.GONE);
        mRunningLayout.setVisibility(View.VISIBLE);
        hideEmptyView();
        loadAlumnusData(true);
    }

    private void initView()
    {

        mTitleBar = (RelativeLayout) findViewById(R.id.top_rl);
        mTitleBar.getBackground().setAlpha(0);
        mPullRefreshListView = (PullZoomListView) findViewById(R.id.pull_refresh_list);
        mRunningLayout = (LinearLayout) findViewById(R.id.running_layout);
        mPullLayout = (FrameLayout) findViewById(R.id.pull_layout);
        mAddPost = (ImageButton) findViewById(R.id.add_post_btn);
        mEmptyView = (LinearLayout) findViewById(R.id.empty_view);
        mErrorText = (TextView) findViewById(R.id.error_tv);
        mErrorButton = (Button) findViewById(R.id.error_btn);
        mErrorImage = (ImageView) findViewById(R.id.error_iv);
        mCancleButton = (ImageButton) findViewById(R.id.cancle_btn);
        mCancleButton.setOnClickListener(this);
        mErrorButton.setOnClickListener(this);
        mAddPost.setOnClickListener(this);

        mPullRefreshListView.getHeaderImageView().setBackgroundResource(R.drawable.ciyuan_topic_cover_background);
        mPullRefreshListView.setOnScrollListener(this);
        mPullRefreshListView.setOnRefreshListener(new PullZoomListView.OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {

                mPullProgress.setVisibility(View.VISIBLE);
                mPullProgress.spin();
                mRefreshType = RefreshType.REFRESH;
                pageNum = 0;
                loadAlumnusData(false);
            }

            @Override
            public void onLoadMore()
            {

                mRefreshType = RefreshType.LOAD_MORE;
                mHandler.sendEmptyMessageDelayed(2, 2000);
            }
        });
        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                if (position == 0)
                {
                    return;
                }

                LogUtil.lsw(position + "");
                AlummusBean alummusBean = mAdapter.getItem(position - 1);
                Intent intent = new Intent();
                intent.setClass(AlumnusCircleActivity.this, AlumnusDetailsActivity.class);
                intent.putExtra("data", alummusBean);
                intent.putExtra("flag", "0");
                startActivity(intent);
            }
        });

        initHead();
    }

    public void loadAlumnusData(final boolean isFirstLoad)
    {

        mRefreshType = RefreshType.REFRESH;
        pageNum = 0;
        BmobQuery<AlummusBean> query = new BmobQuery<AlummusBean>();
        query.order("-createdAt");
        query.setCachePolicy(CachePolicy.NETWORK_ONLY);
        query.setLimit(RestUtils.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        query.setSkip(RestUtils.NUMBERS_PER_PAGE * (pageNum++));
        query.include("author");
        query.findObjects(AlumnusCircleActivity.this, new FindListener<AlummusBean>()
        {

            @Override
            public void onSuccess(List<AlummusBean> list)
            {

                if (list != null && list.size() > 0)
                {
                    hideEmptyView();
                    if (mRefreshType == RefreshType.REFRESH)
                    {
                        datas.clear();
                    }
                    if (HomeMsgApplication.getInstance().getCurrentUser() != null)
                    {
                        list = DatabaseUtil.getInstance(AlumnusCircleActivity.this).setFav(list);
                    }
                    if (list.size() < RestUtils.NUMBERS_PER_PAGE)
                    {
                        mPullRefreshListView.allDataLoadingComplete();
                    }
                    datas.addAll(list);
                    LogUtil.lsw(datas.size() + "");

                    if (isFirstLoad)
                    {
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    } else
                    {
                        mHandler.sendEmptyMessageDelayed(3, 2000);
                    }
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

    public void LoadMoreAlumnusData()
    {

        BmobQuery<AlummusBean> query = new BmobQuery<AlummusBean>();
        query.order("-createdAt");
        query.setCachePolicy(CachePolicy.NETWORK_ONLY);
        query.setLimit(RestUtils.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        LogUtil.lsw("SIZE:" + RestUtils.NUMBERS_PER_PAGE * pageNum);
        query.setSkip(RestUtils.NUMBERS_PER_PAGE * (pageNum++));
        query.include("author");
        query.findObjects(AlumnusCircleActivity.this, new FindListener<AlummusBean>()
        {

            @Override
            public void onSuccess(List<AlummusBean> list)
            {

                if (list.size() != 0 && list.get(list.size() - 1) != null)
                {

                    if (mRefreshType == RefreshType.REFRESH)
                    {
                        datas.clear();
                    }
                    if (HomeMsgApplication.getInstance().getCurrentUser() != null)
                    {
                        list = DatabaseUtil.getInstance(AlumnusCircleActivity.this).setFav(list);
                    }
                    if (list.size() < RestUtils.NUMBERS_PER_PAGE)
                    {
                        mPullRefreshListView.allDataLoadingComplete();
                    }
                    datas.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    mPullRefreshListView.loadingComplete();
                } else
                {

                    pageNum--;
                    mPullRefreshListView.loadingComplete();
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg)
            {

                pageNum--;
                mPullRefreshListView.loadingComplete();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Bundle bundle)
    {

        if (bundle != null)
        {
            String msg = bundle.getString("msg");
            if (msg.equals("success"))
            {
                refreshUI();
            } else if (msg.equals("error"))
            {

            }
        }
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {

            case R.id.add_post_btn:

                startActivity(new Intent(AlumnusCircleActivity.this, EditAlumnusActivity.class));
                break;

            case R.id.alumnus_left_btn:

                finish();
                break;

            case R.id.error_btn:
                refreshUI();
                break;

            case R.id.cancle_btn:
                AlumnusCircleActivity.this.finish();
                break;

            default:
                break;
        }
    }


    public void showEmptyViewByNoData()
    {

        mRunningLayout.setVisibility(View.GONE);
        mPullLayout.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorText.setText("点击加号发布你的动态!");
       // mErrorImage.setImageResource(R.drawable.icon_noticy);
        mErrorButton.setVisibility(View.GONE);
        mAddPost.setVisibility(View.VISIBLE);
        mCancleButton.setVisibility(View.VISIBLE);
    }

    public void showEmptyViewByNoNetwork()
    {

        mRunningLayout.setVisibility(View.GONE);
        mPullLayout.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mErrorText.setText("网络连接超时!");
       // mErrorImage.setImageResource(R.drawable.ic_bottomtabbar_notification);
        mErrorButton.setVisibility(View.VISIBLE);
        mAddPost.setVisibility(View.GONE);
        mCancleButton.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView()
    {

        mEmptyView.setVisibility(View.GONE);
        mCancleButton.setVisibility(View.GONE);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {

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
}
