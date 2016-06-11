package com.hotbitmapgg.geekcommunity.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.fragment.ContactFragment;
import com.hotbitmapgg.geekcommunity.fragment.FindFragment;
import com.hotbitmapgg.geekcommunity.fragment.MessageFragment;
import com.hotbitmapgg.geekcommunity.fragment.UserFragment;
import com.hotbitmapgg.geekcommunity.receive.HomeMessageReceiver;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

public class MainActivity extends ParentActivity implements EventListener
{

    // private Button[] mTabs;


    private MessageFragment recentFragment;

    private UserFragment settingFragment;

    private FindFragment findFragment;

    private Fragment[] fragments;

    private int index;

    private int currentTabIndex;

    private ImageView iv_recent_tips;

    //private HomeGankFragment mHomeGankFragment;

    private ContactFragment contactFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNewMessageBroadCast();
        initTagMessageBroadCast();
        initView();
        initTab();
    }

    private void initView()
    {

//        mTabs = new Button[4];
//        mTabs[0] = (Button) findViewById(R.id.btn_home);
//        mTabs[1] = (Button) findViewById(R.id.btn_message);
//        mTabs[2] = (Button) findViewById(R.id.btn_find);
//        mTabs[3] = (Button) findViewById(R.id.btn_set);
//        iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
        //mTabs[0].setSelected(true);


        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("消息", R.drawable.ic_bottomtabbar_message, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("通讯录", R.drawable.ic_bottomtabbar_feed, R.color.colorPrimary);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("发现", R.drawable.ic_bottomtabbar_discover, R.color.colorPrimary);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("更多", R.drawable.ic_bottomtabbar_more, R.color.colorPrimary);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        bottomNavigation.setBehaviorTranslationEnabled(true);
        bottomNavigation.setAccentColor(Color.parseColor("#1E8AE8"));
        bottomNavigation.setInactiveColor(Color.parseColor("#999999"));
        bottomNavigation.setCurrentItem(0);


        // Customize notification (title, background, typeface)
        // bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

        // Add or remove notification for each item
        //bottomNavigation.setNotification("4", 1);
        // bottomNavigation.setNotification("", 1);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener()
        {

            @Override
            public void onTabSelected(int position, boolean wasSelected)
            {
                // Do something cool here...
                if (currentTabIndex != position)
                {
                    FragmentTransaction trx = getFragmentManager().beginTransaction();
                    trx.hide(fragments[currentTabIndex]);
                    if (!fragments[position].isAdded())
                    {
                        trx.add(R.id.fragment_container, fragments[position]);
                    }
                    trx.show(fragments[position]).commit();
                }
                currentTabIndex = position;
            }
        });
    }

    private void initTab()
    {

        //mHomeGankFragment = new HomeGankFragment();
        recentFragment = new MessageFragment();
        contactFragment = new ContactFragment();
        findFragment = new FindFragment();
        settingFragment = new UserFragment();


        fragments = new Fragment[]{recentFragment, contactFragment, findFragment, settingFragment};
        getFragmentManager().beginTransaction().add(R.id.fragment_container, recentFragment).show(recentFragment).commit();
    }


//    public void onTabSelect(View view)
//    {
//
////        switch (view.getId())
////        {
////            case R.id.btn_home:
////                index = 0;
////                break;
////            case R.id.btn_message:
////                index = 1;
////                break;
////            case R.id.btn_find:
////                index = 2;
////                break;
////            case R.id.btn_set:
////                index = 3;
////                break;
////        }
//        if (currentTabIndex != index)
//        {
//            FragmentTransaction trx = getFragmentManager().beginTransaction();
//            trx.hide(fragments[currentTabIndex]);
//            if (!fragments[index].isAdded())
//            {
//                trx.add(R.id.fragment_container, fragments[index]);
//            }
//            trx.show(fragments[index]).commit();
//        }
////        mTabs[currentTabIndex].setSelected(false);
////        mTabs[index].setSelected(true);
//        currentTabIndex = index;
//    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        if (BmobDB.create(this).hasUnReadMsg())
        {
            // iv_recent_tips.setVisibility(View.VISIBLE);
        } else
        {
            //iv_recent_tips.setVisibility(View.GONE);
        }
        HomeMessageReceiver.ehList.add(this);
        HomeMessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        HomeMessageReceiver.ehList.remove(this);
    }

    @Override
    public void onMessage(BmobMsg message)
    {
        // TODO Auto-generated method stub
        refreshNewMsg(message);
    }

    private void refreshNewMsg(BmobMsg message)
    {

        boolean isAllow = HomeMsgApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllow)
        {
            HomeMsgApplication.getInstance().getMediaPlayer().start();
        }
        iv_recent_tips.setVisibility(View.VISIBLE);

        if (message != null)
        {
            BmobChatManager.getInstance(MainActivity.this).saveReceiveMessage(true, message);
        }
        if (currentTabIndex == 1)
        {
            if (recentFragment != null)
            {
                recentFragment.refresh();
            }
        }
    }

    NewBroadcastReceiver newReceiver;

    private void initNewMessageBroadCast()
    {

        newReceiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        intentFilter.setPriority(3);
        registerReceiver(newReceiver, intentFilter);
    }

    private class NewBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            refreshNewMsg(null);
            abortBroadcast();
        }
    }

    TagBroadcastReceiver userReceiver;

    private void initTagMessageBroadCast()
    {

        userReceiver = new TagBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
        intentFilter.setPriority(3);
        registerReceiver(userReceiver, intentFilter);
    }

    private class TagBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            BmobInvitation message = (BmobInvitation) intent.getSerializableExtra("invite");
            refreshInvite(message);
            abortBroadcast();
        }
    }

    @Override
    public void onNetChange(boolean isNetConnected)
    {
        // TODO Auto-generated method stub
        if (isNetConnected)
        {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message)
    {
        // TODO Auto-generated method stub
        refreshInvite(message);
    }

    private void refreshInvite(BmobInvitation message)
    {

        boolean isAllow = HomeMsgApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllow)
        {
            HomeMsgApplication.getInstance().getMediaPlayer().start();
        }
    }

    @Override
    public void onOffline()
    {
        // TODO Auto-generated method stub
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime)
    {
        // TODO Auto-generated method stub
    }

    private static long firstTime;

    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        if (firstTime + 2000 > System.currentTimeMillis())
        {
            super.onBackPressed();
        } else
        {
            ShowToast("再按一次退出");
        }
        firstTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        try
        {
            unregisterReceiver(newReceiver);
        } catch (Exception e)
        {
        }
        try
        {
            unregisterReceiver(userReceiver);
        } catch (Exception e)
        {
        }

        BmobChat.getInstance(this).stopPollService();
    }
}
