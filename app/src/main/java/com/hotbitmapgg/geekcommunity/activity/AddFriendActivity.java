package com.hotbitmapgg.geekcommunity.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.AddFriendAdapter;
import com.hotbitmapgg.geekcommunity.utils.CollectionUtils;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.hotbitmapgg.geekcommunity.widget.ClearEditText;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class AddFriendActivity extends ParentActivity implements OnClickListener, OnItemClickListener
{

    private ClearEditText et_find_name;

    private List<BmobChatUser> users = new ArrayList<BmobChatUser>();

    private ListView mListView;

    private AddFriendAdapter adapter;

    private int curPage = 0;

    private LoadingDialog mLoadingDialog;

    private String searchName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        StatusBarCompat.compat(this);

        initView();
    }

    private void initView()
    {

        View view = findViewById(R.id.add_friend_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        mLeftBack.setVisibility(View.VISIBLE);
        mLeftBack.setOnClickListener(this);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mTitle.setText("添加好友");
        et_find_name = (ClearEditText) findViewById(R.id.et_find_name);
        et_find_name.setOnEditorActionListener(new OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {

                users.clear();
                searchName = et_find_name.getText().toString();
                if (!TextUtils.isEmpty(searchName))
                {
                    initSearchList(false);
                } else
                {
                    ToastUtil.ShortToast("请输入用户名");
                }
                return false;
            }
        });
        mLoadingDialog = new LoadingDialog(this, R.style.Loading);
        initListView();
    }

    private void initListView()
    {

        mListView = (ListView) findViewById(R.id.list_search);
        adapter = new AddFriendAdapter(this, users);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }

    private void initSearchList(final boolean isUpdate)
    {

        if (!isUpdate)
        {

            mLoadingDialog.setMessage("查找中...");
            mLoadingDialog.show();
        }
        userManager.queryUserByPage(isUpdate, 0, searchName, new FindListener<BmobChatUser>()
        {

            @Override
            public void onError(int errorCode, String errorMsg)
            {

                if (users != null)
                {
                    users.clear();
                }
                ShowToast("用户不存在");

                curPage = 0;
            }

            @Override
            public void onSuccess(List<BmobChatUser> bmobChatUsers)
            {

                if (CollectionUtils.isNotNull(bmobChatUsers))
                {
                    if (isUpdate)
                    {
                        users.clear();
                    }
                    adapter.addAll(bmobChatUsers);
                    if (bmobChatUsers.size() < BRequest.QUERY_LIMIT_COUNT)
                    {
                        LogUtil.lsw("查询好友失败");
                    } else
                    {
                        LogUtil.lsw("查询好友成功");
                    }
                } else
                {

                    if (users != null)
                    {
                        users.clear();
                    }
                    ToastUtil.ShortToast("用户不存在");
                    mLoadingDialog.dismiss();
                }
                if (!isUpdate)
                {

                    mLoadingDialog.dismiss();
                }

                curPage = 0;
            }
        });
    }

    private void queryMoreSearchList(int page)
    {

        userManager.queryUserByPage(true, page, searchName, new FindListener<BmobChatUser>()
        {

            @Override
            public void onSuccess(List<BmobChatUser> bmobChatUsers)
            {
                // TODO Auto-generated method stub
                if (CollectionUtils.isNotNull(bmobChatUsers))
                {
                    adapter.addAll(bmobChatUsers);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg)
            {

                LogUtil.lsw(errorMsg);
            }
        });
    }

    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.left_btn:
                finish();

                break;

            default:
                break;
        }
    }

    public void queryAllUsers()
    {
        // TODO Auto-generated method stub
        userManager.querySearchTotalCount(searchName, new CountListener()
        {

            @Override
            public void onSuccess(int count)
            {
                // TODO Auto-generated method stub
                if (count > users.size())
                {
                    curPage++;
                    queryMoreSearchList(curPage);
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                LogUtil.lsw(errorMsg);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        BmobChatUser user = (BmobChatUser) adapter.getItem(position - 1);
        Intent intent = new Intent(this, SetMyInfoActivity.class);
        intent.putExtra("from", "add");
        intent.putExtra("username", user.getUsername());
        startAnimActivity(intent);
    }
}
