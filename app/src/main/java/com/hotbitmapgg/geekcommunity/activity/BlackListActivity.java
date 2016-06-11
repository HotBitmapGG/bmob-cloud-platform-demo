package com.hotbitmapgg.geekcommunity.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.BlackListAdapter;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.utils.CollectionUtils;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.hotbitmapgg.geekcommunity.widget.DialogTips;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;


public class BlackListActivity extends ParentActivity implements OnItemClickListener
{

    private ListView listview;

    private BlackListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        StatusBarCompat.compat(this);

        initView();
    }

    private void initView()
    {

        View view = findViewById(R.id.black_list_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        mLeftBack.setVisibility(View.VISIBLE);
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
            }
        });
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mTitle.setText("黑名单");
        adapter = new BlackListAdapter(this, BmobDB.create(this).getBlackList());
        listview = (ListView) findViewById(R.id.list_blacklist);
        listview.setOnItemClickListener(this);
        listview.setAdapter(adapter);
    }

    public void showRemoveBlackDialog(final int position, final BmobChatUser user)
    {

        DialogTips dialog = new DialogTips(this, "移出黑名单", "你确定将" + user.getUsername() + "移出黑名单嘛?", "确定", true, true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialogInterface, int userId)
            {

                adapter.remove(position);
                userManager.removeBlack(user.getUsername(), new UpdateListener()
                {

                    @Override
                    public void onSuccess()
                    {

                        ToastUtil.ShortToast("已移出黑名单");
                        HomeMsgApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList()));
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMsg)
                    {

                        LogUtil.lsw(errorMsg);
                    }
                });
            }
        });
        dialog.show();
        dialog = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // TODO Auto-generated method stub
        BmobChatUser invite = (BmobChatUser) adapter.getItem(position);
        showRemoveBlackDialog(position, invite);
    }
}
