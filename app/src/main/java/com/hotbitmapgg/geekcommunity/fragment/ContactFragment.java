package com.hotbitmapgg.geekcommunity.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.activity.FragmentBase;
import com.hotbitmapgg.geekcommunity.activity.NewFriendActivity;
import com.hotbitmapgg.geekcommunity.activity.SetMyInfoActivity;
import com.hotbitmapgg.geekcommunity.adapter.MenuAdapter;
import com.hotbitmapgg.geekcommunity.adapter.UserFriendAdapter;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.widget.CircleProgressView;
import com.hotbitmapgg.geekcommunity.utils.CharacterParser;
import com.hotbitmapgg.geekcommunity.utils.CollectionUtils;
import com.hotbitmapgg.geekcommunity.utils.PinyinComparator;
import com.hotbitmapgg.geekcommunity.widget.ClearEditText;
import com.hotbitmapgg.geekcommunity.widget.DialogTips;
import com.hotbitmapgg.geekcommunity.widget.LetterView;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

public class ContactFragment extends FragmentBase implements OnItemClickListener, OnItemLongClickListener
{

    private ClearEditText mClearEditText;

    private TextView dialog;

    private ListView list_friends;

    private LetterView right_letter;

    private UserFriendAdapter userAdapter;

    private List<User> friends = new ArrayList<User>();

    private InputMethodManager inputMethodManager;

    private CharacterParser characterParser;

    private PinyinComparator pinyinComparator;

    private ImageView mTips;

    private boolean hidden;

    private ImageView mRightButton;

    private ListView expandListView;

    private PopupWindow mMenuPopupWindow;

    private CircleProgressView mCircleProgressView;

    private RelativeLayout mLayout;

    private Handler mHandler = new Handler()
    {

        public void handleMessage(android.os.Message msg)
        {

            if (msg.what == 0)
            {
                mCircleProgressView.setVisibility(View.GONE);
                mCircleProgressView.stopSpinning();
                mLayout.setVisibility(View.VISIBLE);
            }
        }

        ;
    };

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.activity_contacts, container, false);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        init();
        return view;
    }

    //	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_contacts);
//
//		StatusBarCompat.compat(this);
//
//		inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
//		init();
//	}

    private void init()
    {

        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mCircleProgressView = (CircleProgressView) view.findViewById(R.id.contact_load);
        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
        mLayout = (RelativeLayout) view.findViewById(R.id.layout_list);
        View topView = view.findViewById(R.id.contact_top);
        mRightButton = (ImageView) topView.findViewById(R.id.contact_right_btn);
        mTips = (ImageView) topView.findViewById(R.id.contact_tips);
        ImageView mBack = (ImageView) topView.findViewById(R.id.left_btn);
//        mBack.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//
//                finish();
//            }
//        });
        mRightButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                initPopWindown();
            }
        });

        initListView();
        initRightLetterView();

        queryMyfriends();
    }

    private void initPopWindown()
    {

        LayoutInflater mLayoutInfalter = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View menuView = mLayoutInfalter.inflate(R.layout.popup_window_message, null);

        expandListView = (ListView) menuView.findViewById(R.id.listView1);
        expandListView.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                if (mMenuPopupWindow != null)
                {
                    mMenuPopupWindow.dismiss();
                }

                switch (position)
                {
                    case 0:
                        Intent mIntent2 = new Intent(getActivity(), NewFriendActivity.class);
                        mIntent2.putExtra("from", "contact");
                        ContactFragment.this.startActivity(mIntent2);
                        break;
                    case 1:

                        break;

                    default:
                        break;
                }
            }
        });
        expandListView.setAdapter(new MenuAdapter(getActivity()));
        mMenuPopupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);

        expandListView.setOnKeyListener(new View.OnKeyListener()
        {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {

                if ((keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) && event.getRepeatCount() == 0)
                {
                    if (mMenuPopupWindow != null && mMenuPopupWindow.isShowing())
                    {
                        mMenuPopupWindow.dismiss();
                    }
                }
                return false;
            }
        });

        mMenuPopupWindow.setTouchable(true);
        mMenuPopupWindow.setFocusable(true);
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
                        if (mMenuPopupWindow != null && mMenuPopupWindow.isShowing())
                        {
                            mMenuPopupWindow.dismiss();
                        }
                    }
                }
                return true;
            }
        });
        mMenuPopupWindow.setOutsideTouchable(true);
        mMenuPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mMenuPopupWindow.update();

        mMenuPopupWindow.setOnDismissListener(new OnDismissListener()
        {

            @Override
            public void onDismiss()
            {

            }
        });

        mMenuPopupWindow.showAsDropDown(mRightButton);
    }

    private void initEditText()
    {

        mClearEditText.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    private void filterData(String filterStr)
    {

        List<User> filterDateList = new ArrayList<User>();
        if (TextUtils.isEmpty(filterStr))
        {
            filterDateList = friends;
        } else
        {
            filterDateList.clear();
            for (User sortModel : friends)
            {
                String name = sortModel.getUsername();
                if (name != null)
                {
                    if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString()))
                    {
                        filterDateList.add(sortModel);
                    }
                }
            }
        }
        Collections.sort(filterDateList, pinyinComparator);
        userAdapter.updateListView(filterDateList);
    }

    private void filledData(List<BmobChatUser> datas)
    {

        friends.clear();
        int total = datas.size();
        for (int i = 0; i < total; i++)
        {
            BmobChatUser user = datas.get(i);
            User sortModel = new User();
            sortModel.setAvatar(user.getAvatar());
            sortModel.setNick(user.getNick());
            sortModel.setUsername(user.getUsername());
            sortModel.setObjectId(user.getObjectId());
            sortModel.setContacts(user.getContacts());
            String username = sortModel.getUsername();
            String nickName = sortModel.getNick();
            if (!TextUtils.isEmpty(nickName))
            {
                String pinyin = characterParser.getSelling(sortModel.getNick());
                String sortString = pinyin.substring(0, 1).toUpperCase();
                if (sortString.matches("[A-Z]"))
                {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else
                {
                    sortModel.setSortLetters("#");
                }
            } else
            {
                if (username != null)
                {
                    String pinyin = characterParser.getSelling(sortModel.getUsername());
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    if (sortString.matches("[A-Z]"))
                    {
                        sortModel.setSortLetters(sortString.toUpperCase());
                    } else
                    {
                        sortModel.setSortLetters("#");
                    }
                } else
                {
                    sortModel.setSortLetters("#");
                }
            }

            friends.add(sortModel);
        }
        Collections.sort(friends, pinyinComparator);
    }

    @SuppressLint("InflateParams")
    private void initListView()
    {

        list_friends = (ListView) view.findViewById(R.id.list_friends);
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.contact_head, null);
        mClearEditText = (ClearEditText) headView.findViewById(R.id.et_msg_search);
        initEditText();
        list_friends.addHeaderView(headView);
        userAdapter = new UserFriendAdapter(getActivity(), friends);
        list_friends.setAdapter(userAdapter);
        list_friends.setOnItemClickListener(this);
        list_friends.setOnItemLongClickListener(this);

        list_friends.setOnTouchListener(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
    }


    private void initRightLetterView()
    {

        right_letter = (LetterView) view.findViewById(R.id.right_letter);
        dialog = (TextView) view.findViewById(R.id.dialog);
        right_letter.setTextView(dialog);
        right_letter.setOnTouchingLetterChangedListener(new LetterListViewListener());
    }

    private class LetterListViewListener implements LetterView.OnTouchingLetterChangedListener
    {

        @Override
        public void onTouchingLetterChanged(String s)
        {

            int position = userAdapter.getPositionForSection(s.charAt(0));
            if (position != -1)
            {
                list_friends.setSelection(position);
            }
        }
    }

    private void queryMyfriends()
    {

        if (BmobDB.create(getActivity()).hasNewInvite())
        {
            mTips.setVisibility(View.VISIBLE);
        } else
        {
            mTips.setVisibility(View.GONE);
        }
        HomeMsgApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(getActivity()).getContactList()));

        Map<String,BmobChatUser> users = HomeMsgApplication.getInstance().getContactList();
        filledData(CollectionUtils.map2list(users));
        if (userAdapter == null)
        {
            userAdapter = new UserFriendAdapter(getActivity(), friends);
            list_friends.setAdapter(userAdapter);
        } else
        {
            userAdapter.notifyDataSetChanged();
        }

        mHandler.sendEmptyMessageDelayed(0, 3000);
    }

    public void showDeleteDialog(final User user)
    {

        DialogTips dialog = new DialogTips(getActivity(), user.getUsername(), "是否删除好友?", "确定", true, true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialogInterface, int userId)
            {

                deleteContact(user);
            }
        });
        dialog.show();
        dialog = null;
    }

    private void deleteContact(final User user)
    {

        final LoadingDialog mLoadingDialog = new LoadingDialog(getActivity(), R.style.Loading);
        mLoadingDialog.setMessage("请稍候...");
        mLoadingDialog.show();
        userManager.deleteContact(user.getObjectId(), new UpdateListener()
        {

            @Override
            public void onSuccess()
            {
                // TODO Auto-generated method stub
                ShowToast("删除成功");
                HomeMsgApplication.getInstance().getContactList().remove(user.getUsername());
                ContactFragment.this.runOnUiThread(new Runnable()
                {

                    public void run()
                    {

                        mLoadingDialog.dismiss();
                        userAdapter.remove(user);
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                mLoadingDialog.dismiss();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        User user = (User) userAdapter.getItem(position - 1);
        Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
        intent.putExtra("from", "other");
        intent.putExtra("username", user.getUsername());
        startAnimActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {

        User user = (User) userAdapter.getItem(position - 1);
        showDeleteDialog(user);
        return true;
    }
}
