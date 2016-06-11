package com.hotbitmapgg.geekcommunity.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.activity.AddFriendActivity;
import com.hotbitmapgg.geekcommunity.activity.ChatActivity;
import com.hotbitmapgg.geekcommunity.activity.FragmentBase;
import com.hotbitmapgg.geekcommunity.adapter.MessageRecentAdapter;
import com.hotbitmapgg.geekcommunity.adapter.PopAdapter;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.widget.swipemenulistview.SwipeMenu;
import com.hotbitmapgg.geekcommunity.widget.swipemenulistview.SwipeMenuCreator;
import com.hotbitmapgg.geekcommunity.widget.swipemenulistview.SwipeMenuItem;
import com.hotbitmapgg.geekcommunity.widget.swipemenulistview.SwipeMenuListView;
import com.hotbitmapgg.geekcommunity.utils.DisplayUtil;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;



/**
 * 消息界面
 *
 * @author Administrator
 * @hcc
 */
public class MessageFragment extends FragmentBase implements OnItemClickListener, SwipeMenuListView.OnMenuItemClickListener
{

	// 消息列表
	private SwipeMenuListView mListView;

	// 消息列表适配器
	private MessageRecentAdapter mAdapter;

	// 是否隐藏
	private boolean hidden;

	// 提示
	private LinearLayout mHintLayout;

	// 泡泡窗体
	private PopupWindow mFilterPopupWindow;

	// 下拉展示List
	private ListView expandListView;

	private ImageView mRightBtn;

	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_recent, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView()
	{
		view = findViewById(R.id.msg_top);
		RelativeLayout mLayout = (RelativeLayout) view.findViewById(R.id.message_top_rl);
		mRightBtn = (ImageView) view.findViewById(R.id.message_right_btn);
		TextView mTitle = (TextView) view.findViewById(R.id.message_top_title);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{

			android.view.ViewGroup.LayoutParams layoutParams = mLayout.getLayoutParams();
			int height = DisplayUtil.dip2px(getActivity(), 54);
			int topMargin = DisplayUtil.dip2px(getActivity(), 20);
			int rightBtnTopMargin = DisplayUtil.dip2px(getActivity(), 5);
			layoutParams.height = height;
			mLayout.setLayoutParams(layoutParams);
			android.widget.RelativeLayout.LayoutParams mTitleLayoutParams = (android.widget.RelativeLayout.LayoutParams) mTitle.getLayoutParams();
			mTitleLayoutParams.topMargin = topMargin;
			mTitle.setLayoutParams(mTitleLayoutParams);
			android.widget.RelativeLayout.LayoutParams mRightBtnLayoutParams = (android.widget.RelativeLayout.LayoutParams) mRightBtn.getLayoutParams();
			mRightBtnLayoutParams.topMargin = rightBtnTopMargin;
			mRightBtn.setLayoutParams(mRightBtnLayoutParams);

		}


		mRightBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// 初始化泡泡窗体
				initFilterMenu();

			}
		});
		mListView = (SwipeMenuListView) findViewById(R.id.list);
		mHintLayout = (LinearLayout) findViewById(R.id.hint_layout);
		// 初始化ListView
		mListView.setOnItemClickListener(this);
		List<BmobRecent> queryRecents = BmobDB.create(getActivity()).queryRecents();
		if (queryRecents.isEmpty())
		{
			mHintLayout.setVisibility(View.VISIBLE);
		}
		else
		{
			mHintLayout.setVisibility(View.GONE);
			mAdapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, queryRecents);
			mListView.setAdapter(mAdapter);
		}

		// 初始化侧滑删除
		initSwipeList();

	}

	/**
	 * 点击加号弹出的下拉泡泡窗体
	 */
	private void initFilterMenu()
	{
		LayoutInflater mLayoutInfalter = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View menuView = mLayoutInfalter.inflate(R.layout.popup_window_message, null);

		// 展示下拉列表的ListView
		expandListView = (ListView) menuView.findViewById(R.id.listView1);
		expandListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (mFilterPopupWindow != null)
				{
					mFilterPopupWindow.dismiss();
				}

				switch (position)
				{
					case 0:
						// 添加好友
						Intent intent = new Intent(getActivity(), AddFriendActivity.class);
						startActivity(intent);
						break;
					case 1:
						// 扫一扫

						break;

					case 2:
						// 发起群聊
						break;

					default:
						break;
				}
			}
		});
		expandListView.setAdapter(new PopAdapter(getActivity()));
		mFilterPopupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

		// 解决当pw显示时, 无法响应手机的menu back等键的点击事件
		expandListView.setOnKeyListener(new View.OnKeyListener()
		{
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0)
				{
					if (mFilterPopupWindow != null && mFilterPopupWindow.isShowing())
					{
						mFilterPopupWindow.dismiss();
					}
				}
				return false;
			}
		});

		// 让pw能够被触摸, 并且获取焦点
		mFilterPopupWindow.setTouchable(true);
		mFilterPopupWindow.setFocusable(true);
		menuView.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				View view = menuView.findViewById(R.id.listView1);
				int x = (int) event.getX();
				int y = (int) event.getY();
				Rect viewRect = new Rect();
				view.getHitRect(viewRect);
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					if (!viewRect.contains(x, y))
					{
						if (mFilterPopupWindow != null && mFilterPopupWindow.isShowing())
						{
							mFilterPopupWindow.dismiss();
						}
					}
				}
				return true;
			}
		});
		mFilterPopupWindow.setOutsideTouchable(true);
		mFilterPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		mFilterPopupWindow.update();

		mFilterPopupWindow.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss()
			{

			}
		});

		mFilterPopupWindow.showAsDropDown(mRightBtn);
	}

	/**
	 * 初始化ListView的侧滑删除
	 */
	private void initSwipeList()
	{
		SwipeMenuCreator creator = new SwipeMenuCreator()
		{

			@Override
			public void create(SwipeMenu menu)
			{

				// 创建删除Item
				SwipeMenuItem deleteItem = new SwipeMenuItem(HomeMsgApplication.getInstance());
				// 设置item的背景颜色
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// 设置item的宽度
				deleteItem.setWidth(dp2px(90));
				// 设置title
				deleteItem.setTitle("删除");
				// 设置字体大小
				deleteItem.setTitleSize(18);
				// 设置字体颜色
				deleteItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(deleteItem);

			}
		};

		// 设置ListVIew的侧滑删除
		mListView.setMenuCreator(creator);
		mListView.setOnMenuItemClickListener(this);

	}

	/**
	 * 删除当前会话
	 *
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteRecent(BmobRecent recent)
	{
		mAdapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{

		LogUtil.lsw(position + "onitem");
//		if(position == 0)
//		{
//			return;
//		}
		BmobRecent recent = mAdapter.getItem(position);
		// 重置未读消息
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		// 组装聊天对象
		BmobChatUser user = new BmobChatUser();
		user.setAvatar(recent.getAvatar());
		user.setNick(recent.getNick());
		user.setUsername(recent.getUserName());
		user.setObjectId(recent.getTargetid());
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra("user", user);
		startAnimActivity(intent);
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden)
		{
			refresh();
		}
	}

	/**
	 * 刷新界面消息
	 */
	public void refresh()
	{
		try
		{
			getActivity().runOnUiThread(new Runnable()
			{
				public void run()
				{
					List<BmobRecent> queryRecents = BmobDB.create(getActivity()).queryRecents();
					if (queryRecents.isEmpty())
					{
						mHintLayout.setVisibility(View.VISIBLE);
					}
					else
					{
						mHintLayout.setVisibility(View.GONE);
						mAdapter = new MessageRecentAdapter(getActivity(), R.layout.item_conversation, queryRecents);
						mListView.setAdapter(mAdapter);
					}

				}
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (!hidden)
		{
			refresh();
		}
	}

	private int dp2px(int dp)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}

	/**
	 * 侧滑删除条目点击事件
	 */
	@Override
	public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
	{
		// TODO Auto-generated method stub
		if (index == 0)
		{
			LogUtil.lsw(position + "menu");
			BmobRecent bmobRecent = mAdapter.getItem(position);
			// 删除会话
			deleteRecent(bmobRecent);
		}
		return false;
	}


}
