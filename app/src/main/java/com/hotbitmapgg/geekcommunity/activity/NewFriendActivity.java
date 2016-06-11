package com.hotbitmapgg.geekcommunity.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.NewFriendAdapter;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.widget.DialogTips;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

public class NewFriendActivity extends ParentActivity implements OnItemLongClickListener
{

    private ListView listview;

    private NewFriendAdapter adapter;

    private String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        StatusBarCompat.compat(this);

        from = getIntent().getStringExtra("from");
        initView();
    }

    private void initView()
    {

        View view = findViewById(R.id.new_friend_top);
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
        mTitle.setText("新好友");
        listview = (ListView) findViewById(R.id.list_newfriend);
        listview.setOnItemLongClickListener(this);
        adapter = new NewFriendAdapter(this, BmobDB.create(this).queryBmobInviteList());
        listview.setAdapter(adapter);
        if (from == null)
        {
            listview.setSelection(adapter.getCount());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        // TODO Auto-generated method stub
        BmobInvitation invite = (BmobInvitation) adapter.getItem(position);
        showDeleteDialog(position, invite);
        return true;
    }

    public void showDeleteDialog(final int position, final BmobInvitation invite)
    {

        DialogTips dialog = new DialogTips(this, invite.getFromname(), "删除添加好友请求", "确定", true, true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialogInterface, int userId)
            {

                deleteInvite(position, invite);
            }
        });
        dialog.show();
        dialog = null;
    }

    private void deleteInvite(int position, BmobInvitation invite)
    {

        adapter.remove(position);
        BmobDB.create(this).deleteInviteMsg(invite.getFromid(), Long.toString(invite.getTime()));
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (from == null)
        {
            startAnimActivity(MainActivity.class);
        }
    }
}
