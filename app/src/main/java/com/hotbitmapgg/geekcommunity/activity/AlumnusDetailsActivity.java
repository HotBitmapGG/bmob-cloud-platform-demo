package com.hotbitmapgg.geekcommunity.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.AlumnusDetailsAdapter;
import com.hotbitmapgg.geekcommunity.alumnus.utils.ImgView;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.bean.AlummusBean;
import com.hotbitmapgg.geekcommunity.bean.AlumnusContentBean;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.config.Config;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.db.DatabaseUtil;
import com.hotbitmapgg.geekcommunity.widget.emojicon.EmojiconTextView;
import com.hotbitmapgg.geekcommunity.widget.CircleProgressView;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

@SuppressLint("InflateParams")
public class AlumnusDetailsActivity extends ParentActivity implements OnClickListener
{

    private ListView mContentBeanList;

    private EditText mContentBeanContent;

    private Button mContentBeanCommit;

    private TextView userName;

    private EmojiconTextView mContentBeanItemContent;

    private ImageView userLogo;

    private ImageView myFav;

    private LinearLayout love;

    private AlummusBean mAlumnusBean;

    private String mContentBeanEdit = "";

    private AlumnusDetailsAdapter mAdapter;

    private List<AlumnusContentBean> mContentBeans = new ArrayList<AlumnusContentBean>();

    private int pageNum = 0;

    public enum RefreshType
    {
        REFRESH, LOAD_MORE
    }

    private RefreshType mRefreshType = RefreshType.LOAD_MORE;

    private Handler mHandler = new Handler()
    {

        public void handleMessage(android.os.Message msg)
        {

            if (msg.what == 0)
            {
                mContentBeanList.setVisibility(View.VISIBLE);
                mAdapter = new AlumnusDetailsAdapter(AlumnusDetailsActivity.this, mContentBeans);
                mContentBeanList.setAdapter(mAdapter);
                mCircleProgressView.setVisibility(View.GONE);
                mCircleProgressView.stopSpinning();
            } else if (msg.what == 1)
            {
                mAdapter.notifyDataSetChanged();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnus_details);
        StatusBarCompat.compat(this);
        Bmob.initialize(this, Config.applicationId);
        EventBus.getDefault().register(this);
        initTitle();
        initView();

        loadContentData(true);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.alumnus_details_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mTitle.setText("详情");
        mLeftBack.setVisibility(View.VISIBLE);
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
                Bundle mBundle = new Bundle();
                mBundle.putString("msg", "finish");
                EventBus.getDefault().post(mBundle);
            }
        });
    }

    @SuppressLint("InflateParams")
    private void initView()
    {

        mContentBeanList = (ListView) findViewById(R.id.comment_list);
        mCircleProgressView = (CircleProgressView) findViewById(R.id.comment_circle_progress);
        View head = LayoutInflater.from(this).inflate(R.layout.ai_details, null);
        mContentBeanList.addHeaderView(head);
        mContentBeanContent = (EditText) findViewById(R.id.comment_content);
        mContentBeanCommit = (Button) findViewById(R.id.comment_commit);
        commentLayout = (LinearLayout) findViewById(R.id.area_commit);
        userName = (TextView) head.findViewById(R.id.user_name);
        mSex = (ImageView) head.findViewById(R.id.user_sex);
        mContentBeanItemContent = (EmojiconTextView) head.findViewById(R.id.content_text);
        mImageLayout = (LinearLayout) head.findViewById(R.id.img_lyt);
        userLogo = (ImageView) head.findViewById(R.id.user_logo);
        love = (LinearLayout) head.findViewById(R.id.item_action_love);
        mDate = (TextView) head.findViewById(R.id.item_action_date);
        mLoveNum = (TextView) findViewById(R.id.tv_love);
        mLovePic = (ImageView) findViewById(R.id.iv_love);
        mBtnComment = (LinearLayout) findViewById(R.id.alumnus_detail_comment_btn);
        mBtnLove = (LinearLayout) findViewById(R.id.alumnus_detail_love_btn);
        mBtnComment.setOnClickListener(this);
        mBtnLove.setOnClickListener(this);
        mPraiseNmaes = (TextView) findViewById(R.id.tv_praise_name);
        mContentBeanCommit.setOnClickListener(this);
        userLogo.setOnClickListener(this);
        mAlumnusBean = (AlummusBean) getIntent().getSerializableExtra("data");
        flag = getIntent().getStringExtra("flag");
        if (flag.equals("0"))
        {
            commentLayout.setVisibility(View.VISIBLE);
        } else
        {
            mBtnComment.setVisibility(View.GONE);
            mBtnLove.setVisibility(View.GONE);
            mContentBeanContent.setVisibility(View.VISIBLE);
            mContentBeanCommit.setVisibility(View.VISIBLE);
        }

//		mContentBeanList.setOnAcFunRefreshListener(new OnAcFunRefreshListener()
//		{
//
//			@Override
//			public void onRefresh()
//			{
////				mRefreshType = RefreshType.REFRESH;
////				pageNum = 0;
////				loadContentData();
//
//			}
//
//			@Override
//			public void onLoadMore()
//			{
//				mRefreshType = RefreshType.LOAD_MORE;
//				loadContentData(false);
//
//			}
//		});

        //mContentBeanList.hideHeadView();

        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();

//		mContentBeanList.setOnRefreshListener(new OnRefreshListener2<ListView>()
//		{
//
//			@Override
//			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
//			{
//				mContentBeanList.setMode(Mode.BOTH);
//				mRefreshType = RefreshType.REFRESH;
//				pageNum = 0;
//				loadContentData();
//
//			}
//
//			@Override
//			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
//			{
//
//				mRefreshType = RefreshType.LOAD_MORE;
//				//pageNum++;
//				loadContentData();
//
//			}
//		});

        initMoodView(mAlumnusBean);
    }

    private void initMoodView(AlummusBean bean)
    {
        // TODO Auto-generated method stub
        if (bean == null)
        {
            return;
        }
        String account = mAlumnusBean.getAuthor().getUsername();
        String nick = mAlumnusBean.getAuthor().getNick();
        if (!TextUtils.isEmpty(nick))
        {
            userName.setText(nick);
        } else
        {
            userName.setText(account);
        }
        Boolean sex = mAlumnusBean.getAuthor().getSex();
        if (sex)
        {
            mSex.setImageResource(R.drawable.userinfo_icon_male);
        } else
        {
            mSex.setImageResource(R.drawable.userinfo_icon_female);
        }
        mContentBeanItemContent.setText(mAlumnusBean.getContent());
        mImageLayout.removeAllViews();
        List<BmobFile> bmobFiles = mAlumnusBean.getContentfigureurls();
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
                View view = ImgView.backview(AlumnusDetailsActivity.this, urls, urls, urls);
                if (view != null)
                {
                    mImageLayout.setVisibility(View.VISIBLE);
                    mImageLayout.addView(view);
                }
            }
        } else
        {
            mImageLayout.setVisibility(View.GONE);
        }

        int love = mAlumnusBean.getLove();
        if (love == 0)
        {
            mLoveNum.setText("喜欢");
        } else
        {
            mLoveNum.setText(love + "");
        }
        if (mAlumnusBean.getMyLove())
        {
            mLoveNum.setTextColor(getResources().getColor(R.color.blue_main));
            mLovePic.setImageResource(R.drawable.ic_a_praise_full);
        } else
        {
            mLoveNum.setTextColor(getResources().getColor(R.color.gray_80));
            mLovePic.setImageResource(R.drawable.ic_a_praise);
        }

        User user = mAlumnusBean.getAuthor();
        BmobFile avatar = user.getAvatars();
        if (null != avatar)
        {
            ImageLoader.getInstance().displayImage(avatar.getFileUrl(AlumnusDetailsActivity.this), userLogo, ImageLoadUtil.defaultOptions(), new SimpleImageLoadingListener()
            {

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                {
                    // TODO Auto-generated method stub
                    super.onLoadingComplete(imageUri, view, loadedImage);
                }
            });
        }
        String createdAt = mAlumnusBean.getCreatedAt();
        if (!TextUtils.isEmpty(createdAt))
        {
            mDate.setText(createdAt);
        }

        List<String> names = mAlumnusBean.getNames();
        StringBuilder mBuilder = new StringBuilder();
        if (names != null && names.size() > 0)
        {
            mPraiseNmaes.setVisibility(View.VISIBLE);
            for (String name : names)
            {
                mBuilder.append(name);
                mBuilder.append(",");
            }
            mPraiseNmaes.setText(mBuilder.toString());
        }
    }

    private void loadContentData(final boolean flag)
    {

        BmobQuery<AlumnusContentBean> query = new BmobQuery<AlumnusContentBean>();
        query.addWhereRelatedTo("relation", new BmobPointer(mAlumnusBean));
        query.include("user");
        query.order("createdAt");
        query.setLimit(RestUtils.NUMBERS_PER_PAGE);
        query.setSkip(RestUtils.NUMBERS_PER_PAGE * (pageNum++));
        query.findObjects(this, new FindListener<AlumnusContentBean>()
        {

            @Override
            public void onSuccess(List<AlumnusContentBean> data)
            {

                if (data.size() != 0 && data.get(data.size() - 1) != null)
                {

                    if (mRefreshType == RefreshType.REFRESH)
                    {
                        mAdapter.getDataList().clear();
                    }
                    if (data.size() < RestUtils.NUMBERS_PER_PAGE)
                    {
                        //mContentBeanList.allDataLoadingComplete();
                    }

                    mContentBeans.addAll(data);
                    if (flag)
                    {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    } else
                    {
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                } else
                {
                    ToastUtil.ShortToast("没有更多评论了");
                    pageNum--;
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg)
            {

                ToastUtil.ShortToast("获取评论失败,请检查网络");
                pageNum--;
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.user_logo:
                onClickUserLogo();
                break;

            case R.id.comment_commit:
                onClickCommit();
                break;

            case R.id.alumnus_detail_comment_btn:

                mBtnComment.setVisibility(View.GONE);
                mBtnLove.setVisibility(View.GONE);
                mContentBeanContent.setVisibility(View.VISIBLE);
                mContentBeanCommit.setVisibility(View.VISIBLE);

                break;

            case R.id.alumnus_detail_love_btn:
                onClickLove();
                break;

            default:
                break;
        }
    }

    private void onClickUserLogo()
    {

        Intent intent = new Intent();
        intent.setClass(AlumnusDetailsActivity.this, UserAlumnusActivity.class);
        startActivity(intent);
    }


    private void onClickCommit()
    {

        User currentUser = BmobUser.getCurrentUser(this, User.class);
        if (currentUser != null)
        {
            mContentBeanEdit = mContentBeanContent.getText().toString().trim();
            if (TextUtils.isEmpty(mContentBeanEdit))
            {
                ToastUtil.ShortToast("评论内容不能为空");
                return;
            }
            publishmContentBean(currentUser, mContentBeanEdit);
        }
    }

    private void publishmContentBean(User user, String content)
    {

        final AlumnusContentBean mContentBean = new AlumnusContentBean();
        mContentBean.setUser(user);
        mContentBean.setCommentContent(content);
        mContentBean.save(this, new SaveListener()
        {

            @Override
            public void onSuccess()
            {

                ToastUtil.ShortToast("评论成功");
                mAdapter.getDataList().add(mContentBean);
                mAdapter.notifyDataSetChanged();
                mContentBeanList.setSelection(mAdapter.getCount() - 1);
                mContentBeanContent.setText("");
                hideSoftInput();

                BmobRelation relation = new BmobRelation();
                relation.add(mContentBean);
                mAlumnusBean.setRelation(relation);
                int size = mContentBeans.size();
                mAlumnusBean.setCommentNum(size);
                mAlumnusBean.update(AlumnusDetailsActivity.this, new UpdateListener()
                {

                    @Override
                    public void onSuccess()
                    {

                        LogUtil.lsw("评论更新成功");
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg)
                    {

                        LogUtil.lsw("评论更新失败 = " + errorMsg);
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                ToastUtil.ShortToast("请检查网络是否连接");
            }
        });
    }

    boolean isFav = false;

    private LinearLayout mImageLayout;

    private TextView mLoveNum;

    private ImageView mLovePic;

    private TextView mDate;

    private LinearLayout commentLayout;

    private String flag;

    private LinearLayout mBtnComment;

    private LinearLayout mBtnLove;

    private TextView mPraiseNmaes;

    private ImageView mSex;

    private CircleProgressView mCircleProgressView;

    private void onClickLove()
    {
        // TODO Auto-generated method stub
        User user = BmobUser.getCurrentUser(this, User.class);
        if (mAlumnusBean.getMyLove())
        {
            ToastUtil.ShortToast("您已经赞过了");
            return;
        }
        isFav = mAlumnusBean.getMyFav();
        if (isFav)
        {
            mAlumnusBean.setMyFav(false);
        }
        mAlumnusBean.setLove(mAlumnusBean.getLove() + 1);
        mLoveNum.setTextColor(getResources().getColor(R.color.blue_main));
        mLoveNum.setText(mAlumnusBean.getLove() + "");
        mLovePic.setImageResource(R.drawable.ic_a_praise_full);
        mAlumnusBean.increment("love", 1);
        User currentUser = HomeMsgApplication.getInstance().getCurrentUser();
        String name = currentUser.getNick();
        List<String> names = mAlumnusBean.getNames();
        names.add(name);
        mAlumnusBean.setNames(names);
        mAlumnusBean.update(AlumnusDetailsActivity.this, new UpdateListener()
        {

            @Override
            public void onSuccess()
            {
                // TODO Auto-generated method stub
                mAlumnusBean.setMyLove(true);
                mAlumnusBean.setMyFav(isFav);
                DatabaseUtil.getInstance(AlumnusDetailsActivity.this).insertFav(mAlumnusBean);
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                LogUtil.lsw("点赞失败 = " + errorMsg);
            }
        });
    }

    private void onClickmContentBean()
    {

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(mContentBeanContent, 0);
    }

    private void hideSoftInput()
    {

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mContentBeanContent.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case RestUtils.PUBLISH_COMMENT:
                    mContentBeanCommit.performClick();
                    break;
                case RestUtils.SAVE_FAVOURITE:
                    myFav.performClick();
                    break;
                case RestUtils.GET_FAVOURITE:

                    break;
                case RestUtils.GO_SETTINGS:
                    userLogo.performClick();
                    break;
                default:
                    break;
            }
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView)
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
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 15;
        listView.setLayoutParams(params);
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
            if (msg.equals("nofity"))
            {
                int size = mContentBeans.size();
                mAlumnusBean.setCommentNum(size);
                mAlumnusBean.update(AlumnusDetailsActivity.this, new UpdateListener()
                {

                    @Override
                    public void onSuccess()
                    {

                        LogUtil.lsw("评论更新成功");
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg)
                    {

                        LogUtil.lsw("评论更新失败 = " + errorMsg);
                    }
                });
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return true;
    }
}
