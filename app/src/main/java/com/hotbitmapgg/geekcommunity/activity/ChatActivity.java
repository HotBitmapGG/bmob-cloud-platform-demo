package com.hotbitmapgg.geekcommunity.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.adapter.MessageChatAdapter;
import com.hotbitmapgg.geekcommunity.adapter.NewRecordPlayClickListener;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.widget.emojicon.Emojicon;
import com.hotbitmapgg.geekcommunity.widget.emojicon.EmojiconEditText;
import com.hotbitmapgg.geekcommunity.widget.emojicon.EmojiconGridFragment;
import com.hotbitmapgg.geekcommunity.widget.emojicon.EmojiconsFragment;
import com.hotbitmapgg.geekcommunity.pick.Action;
import com.hotbitmapgg.geekcommunity.receive.HomeMessageReceiver;
import com.hotbitmapgg.geekcommunity.utils.CommonUtils;
import com.hotbitmapgg.geekcommunity.utils.ImageFile;
import com.hotbitmapgg.geekcommunity.utils.ImageUtil;
import com.hotbitmapgg.geekcommunity.utils.InputMethodUtil;
import com.hotbitmapgg.geekcommunity.utils.LocalFileControl;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.PreferencesUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.hotbitmapgg.geekcommunity.widget.DialogTips;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.v3.listener.PushListener;

@SuppressLint({"ClickableViewAccessibility", "InflateParams", "NewApi"})
public class ChatActivity extends ParentActivity implements OnClickListener, EventListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener
{

    private Button btn_chat_send;

    private ImageView mBtnAdd;

    private Button btn_speak;

    private ListView mListView;

    private EmojiconEditText edit_user_comment;

    private String targetId = "";

    private BmobChatUser targetUser;

    private LinearLayout layout_more;

    private LinearLayout layout_emo;

    private RelativeLayout layout_add;

    //private int[] moreIcons = new int[] {R.drawable.message_more_voice, R.drawable.message_more_pic, R.drawable.message_more_camera, R.drawable.message_more_poi };

    //private String[] moreNames = new String[] { "����", "���", "���", "λ��" };

    private RelativeLayout layout_record;

    private TextView tv_voice_tips;

    private ImageView iv_record;

    private Drawable[] drawable_Anims;

    private BmobRecordManager recordManager;

    private MessageChatAdapter mAdapter;

    private Toast toast;

    private String localCameraPath = "";

    private Queue<String> images = new LinkedList<String>();

    public static final int NEW_MESSAGE = 0x001;

    private NewBroadcastReceiver receiver;

    private ImageView mRightBtn;

    private GridView mMoreLayout;

    private ImageView mBtnEmojicon;

    private LinearLayout layout_voice;

    private FrameLayout mChatBg;

    private ChatBgBroadCast chatBgBroadCast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        StatusBarCompat.compat(this);

        manager = BmobChatManager.getInstance(this);
        targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
        targetId = targetUser.getObjectId();

        initNewMessageBroadCast();
        initChatBgBroadCat();
        initView();
    }

    private void initChatBgBroadCat()
    {

        chatBgBroadCast = new ChatBgBroadCast();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction("android.homemessage.resid");
        ChatActivity.this.registerReceiver(chatBgBroadCast, mFilter);
    }

    private void initRecordManager()
    {

        recordManager = BmobRecordManager.getInstance(this);
        recordManager.setOnRecordChangeListener(new OnRecordChangeListener()
        {

            @Override
            public void onVolumnChanged(int value)
            {
                // TODO Auto-generated method stub
                iv_record.setImageDrawable(drawable_Anims[value]);
            }

            @Override
            public void onTimeChanged(int recordTime, String localPath)
            {

                if (recordTime >= BmobRecordManager.MAX_RECORD_TIME)
                {
                    btn_speak.setPressed(false);
                    btn_speak.setClickable(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    sendVoiceMessage(localPath, recordTime);
                    handler.postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {
                            // TODO Auto-generated method stub
                            btn_speak.setClickable(true);
                        }
                    }, 1000);
                }
            }
        });
    }

    private void initView()
    {

        View view = findViewById(R.id.chat_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        mLeftBack.setVisibility(View.VISIBLE);
        mLeftBack.setOnClickListener(this);
        mRightBtn = (ImageView) view.findViewById(R.id.right_btn);
        mRightBtn.setVisibility(View.VISIBLE);
        mRightBtn.setImageResource(R.drawable.action_button_all_play_disable_black);
        mRightBtn.setOnClickListener(this);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        String nick = targetUser.getNick();
        String username = targetUser.getUsername();
        if (!TextUtils.isEmpty(nick))
        {
            mTitle.setText(nick);
        } else
        {
            mTitle.setText(username);
        }

        mListView = (ListView) findViewById(R.id.mListView);
        mChatBg = (FrameLayout) findViewById(R.id.chat_bg);
        int chatResId = PreferencesUtil.getIntData(ChatActivity.this, "chatResId");
        if (chatResId != 0)
        {
            mChatBg.setBackgroundResource(chatResId);
        }

        initBottomView();
        initListView();
        initVoiceView();
    }


    private void initVoiceView()
    {

        layout_record = (RelativeLayout) findViewById(R.id.layout_record);
        tv_voice_tips = (TextView) findViewById(R.id.tv_voice_tips);
        iv_record = (ImageView) findViewById(R.id.iv_record);
        btn_speak.setOnTouchListener(new VoiceTouchListen());
        initVoiceAnimRes();
        initRecordManager();
    }


    class VoiceTouchListen implements OnTouchListener
    {

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {

            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    if (!CommonUtils.checkSdCard())
                    {
                        ToastUtil.ShortToast("无法发送语音消息");
                        return false;
                    }
                    try
                    {
                        v.setPressed(true);
                        layout_record.setVisibility(View.VISIBLE);
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        recordManager.startRecording(targetId);
                    } catch (Exception e)
                    {
                        LogUtil.lsw(e.getMessage());
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                {
                    if (event.getY() < 0)
                    {
                        tv_voice_tips.setText(getString(R.string.voice_cancel_tips));
                        tv_voice_tips.setTextColor(Color.RED);
                    } else
                    {
                        tv_voice_tips.setText(getString(R.string.voice_up_tips));
                        tv_voice_tips.setTextColor(Color.WHITE);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    layout_record.setVisibility(View.INVISIBLE);
                    try
                    {
                        if (event.getY() < 0)
                        {
                            recordManager.cancelRecording();
                        } else
                        {
                            int recordTime = recordManager.stopRecording();
                            if (recordTime > 1)
                            {
                                sendVoiceMessage(recordManager.getRecordFilePath(targetId), recordTime);
                            } else
                            {
                                layout_record.setVisibility(View.GONE);
                                showShortToast().show();
                            }
                        }
                    } catch (Exception e)
                    {
                        LogUtil.lsw(e.getMessage());
                    }
                    return true;
                default:
                    return false;
            }
        }
    }


    private void sendVoiceMessage(String voicePath, int length)
    {

        manager.sendVoiceMessage(targetUser, voicePath, length, new UploadListener()
        {

            @Override
            public void onStart(BmobMsg msg)
            {
                // TODO Auto-generated method stub
                refreshMessage(msg);
            }

            @Override
            public void onSuccess()
            {
                // TODO Auto-generated method stub
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error, String errorMsg)
            {

                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private Toast showShortToast()
    {

        if (toast == null)
        {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }


    private void initVoiceAnimRes()
    {

        drawable_Anims = new Drawable[]{getResources().getDrawable(R.drawable.chat_icon_voice2), getResources().getDrawable(R.drawable.chat_icon_voice3), getResources().getDrawable(R.drawable.chat_icon_voice4), getResources().getDrawable(R.drawable.chat_icon_voice5), getResources().getDrawable(R.drawable.chat_icon_voice6)};
    }


    private List<BmobMsg> initMsgData()
    {

        int count = BmobDB.create(this).queryChatTotalCount(targetId);
        List<BmobMsg> list = BmobDB.create(this).queryMessages(targetId, count);
        LogUtil.lsw(list.size() + "");
        return list;
    }


    private void initOrRefresh()
    {

        if (mAdapter != null)
        {
            if (HomeMessageReceiver.mNewNum != 0)
            {

                int news = HomeMessageReceiver.mNewNum;
                int size = initMsgData().size();
                for (int i = (news - 1); i >= 0; i--)
                {

                    mAdapter.add(initMsgData().get(size - (i + 1)));
                }
                mListView.setSelection(mAdapter.getCount() - 1);
            } else
            {
                mAdapter.notifyDataSetChanged();
            }
        } else
        {
            mAdapter = new MessageChatAdapter(this, initMsgData());
            mListView.setAdapter(mAdapter);
        }
    }


    private void initBottomView()
    {

        mBtnAdd = (ImageView) findViewById(R.id.btn_chat_add);
        mBtnAdd.setOnClickListener(this);
        mBtnEmojicon = (ImageView) findViewById(R.id.btn_chat_emote);
        mBtnEmojicon.setOnClickListener(this);
        btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
        btn_chat_send.setOnClickListener(this);
        mBtnVoice = (ImageView) findViewById(R.id.chat_bottom_voice);
        mBtnVoice.setOnClickListener(this);
        mBtnGallery = (ImageView) findViewById(R.id.chat_bottom_gallery);
        mBtnGallery.setOnClickListener(this);
        mBtnCamera = (ImageView) findViewById(R.id.chat_bottom_camera);
        mBtnCamera.setOnClickListener(this);
        mBtnLocation = (ImageView) findViewById(R.id.chat_bottom_location);
        mBtnLocation.setOnClickListener(this);
        mBtnKeybord = (ImageView) findViewById(R.id.btn_chat_keybord);
        mBtnKeybord.setOnClickListener(this);
        layout_more = (LinearLayout) findViewById(R.id.layout_more);
        layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
        layout_add = (RelativeLayout) findViewById(R.id.layout_add);
        layout_voice = (LinearLayout) findViewById(R.id.layout_voice);
        setEmojiconFragment(true);
        btn_speak = (Button) findViewById(R.id.btn_speak);

        edit_user_comment = (EmojiconEditText) findViewById(R.id.edit_user_comment);
        edit_user_comment.setOnClickListener(this);
    }


    private void initListView()
    {

        mListView.setDividerHeight(0);
        initOrRefresh();
        mListView.setSelection(mAdapter.getCount() - 1);
        mListView.setOnTouchListener(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                hideSoftInputView();
                layout_more.setVisibility(View.GONE);
                return false;
            }
        });

        mAdapter.setOnInViewClickListener(R.id.iv_fail_resend, new MessageChatAdapter.onInternalClickListener()
        {

            @Override
            public void OnClickListener(View parentV, View v, Integer position, Object values)
            {

                showResendDialog(parentV, v, values);
            }
        });
    }

    public void showResendDialog(final View parentV, View v, final Object values)
    {

        DialogTips dialog = new DialogTips(this, "确定要重新发送该消息嘛?", "确定", "取消", "提示", true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialogInterface, int userId)
            {

                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE || ((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE)
                {
                    resendFileMsg(parentV, values);
                } else
                {
                    resendTextMsg(parentV, values);
                }
                dialogInterface.dismiss();
            }
        });
        dialog.show();
        dialog = null;
    }

    private void resendTextMsg(final View parentV, final Object values)
    {

        BmobChatManager.getInstance(ChatActivity.this).resendTextMessage(targetUser, (BmobMsg) values, new PushListener()
        {

            @Override
            public void onSuccess()
            {

                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
                parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
                ((TextView) parentV.findViewById(R.id.tv_send_status)).setText("已发送");
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
                parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
                parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    private void resendFileMsg(final View parentV, final Object values)
    {

        BmobChatManager.getInstance(ChatActivity.this).resendFileMessage(targetUser, (BmobMsg) values, new UploadListener()
        {

            @Override
            public void onStart(BmobMsg msg)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void onSuccess()
            {
                // TODO Auto-generated method stub
                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_SUCCESS);
                parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.INVISIBLE);
                if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE)
                {
                    parentV.findViewById(R.id.tv_send_status).setVisibility(View.GONE);
                    parentV.findViewById(R.id.tv_voice_length).setVisibility(View.VISIBLE);
                } else
                {
                    parentV.findViewById(R.id.tv_send_status).setVisibility(View.VISIBLE);
                    ((TextView) parentV.findViewById(R.id.tv_send_status)).setText("已发送");
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {
                // TODO Auto-generated method stub
                ((BmobMsg) values).setStatus(BmobConfig.STATUS_SEND_FAIL);
                parentV.findViewById(R.id.progress_load).setVisibility(View.INVISIBLE);
                parentV.findViewById(R.id.iv_fail_resend).setVisibility(View.VISIBLE);
                parentV.findViewById(R.id.tv_send_status).setVisibility(View.INVISIBLE);
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.edit_user_comment:
                mListView.setSelection(mListView.getCount() - 1);
                if (layout_more.getVisibility() == View.VISIBLE)
                {
                    layout_add.setVisibility(View.GONE);
                    layout_emo.setVisibility(View.GONE);
                    layout_more.setVisibility(View.GONE);
                }
                break;

            case R.id.btn_chat_add:
                if (layout_more.getVisibility() == View.GONE)
                {
                    layout_more.setVisibility(View.VISIBLE);
                    layout_add.setVisibility(View.VISIBLE);
                    layout_emo.setVisibility(View.GONE);
                    layout_voice.setVisibility(View.GONE);
                    hideSoftInputView();
                } else
                {
                    if (layout_emo.getVisibility() == View.VISIBLE)
                    {
                        layout_emo.setVisibility(View.GONE);
                        layout_voice.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else if (layout_voice.getVisibility() == View.VISIBLE)
                    {
                        layout_voice.setVisibility(View.GONE);
                        layout_emo.setVisibility(View.GONE);
                        layout_add.setVisibility(View.VISIBLE);
                    } else
                    {
                        layout_more.setVisibility(View.GONE);
                    }
                }

                break;
            case R.id.btn_chat_keybord:
                showEditState(false);
                break;
            case R.id.btn_chat_send:
                final String msg = edit_user_comment.getText().toString();
                if (msg.equals(""))
                {

                    return;
                }
                boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
                if (!isNetConnected)
                {

                    ToastUtil.ShortToast(R.string.network_tips);
                }
                BmobMsg message = BmobMsg.createTextSendMsg(this, targetId, msg);
                message.setExtra("Bmob");
                manager.sendTextMessage(targetUser, message);
                refreshMessage(message);

                break;

            case R.id.btn_chat_emote:
                selectImageFromEmote();
                break;

            case R.id.left_btn:
                finish();
                break;

            case R.id.right_btn:
                Intent mIntent = new Intent(ChatActivity.this, UserInfoDetailsActivity.class);
                mIntent.putExtra("user", targetUser);
                startActivity(mIntent);

                break;


            case R.id.chat_bottom_voice:
                layout_more.setVisibility(View.VISIBLE);
                layout_voice.setVisibility(View.VISIBLE);
                layout_add.setVisibility(View.GONE);
                layout_emo.setVisibility(View.GONE);
                hideSoftInputView();
                break;

            case R.id.chat_bottom_gallery:
                selectImageFromLocal();
                break;

            case R.id.chat_bottom_camera:
                selectImageFromCamera();
                break;

            default:
                break;
        }
    }

    public void selectImageFromEmote()
    {

        if (layout_more.getVisibility() == View.GONE)
        {
            showEditState(true);
        } else
        {
            if (layout_add.getVisibility() == View.VISIBLE)
            {
                layout_add.setVisibility(View.GONE);
                layout_voice.setVisibility(View.GONE);
                layout_emo.setVisibility(View.VISIBLE);
            } else if (layout_voice.getVisibility() == View.VISIBLE)
            {
                layout_voice.setVisibility(View.GONE);
                layout_add.setVisibility(View.GONE);
                layout_emo.setVisibility(View.VISIBLE);
            } else
            {
                layout_more.setVisibility(View.GONE);
            }
        }
    }

    public void selectImageFromCamera()
    {

        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(RestUtils.BMOB_PICTURE_PATH);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
        localCameraPath = file.getPath();
        LogUtil.lsw(localCameraPath);
        Uri imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, RestUtils.REQUESTCODE_TAKE_CAMERA);
    }

    public void selectImageFromLocal()
    {

        Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
        i.putExtra("limit", 9);
        startActivityForResult(i, RestUtils.REQUESTCODE_TAKE_LOCAL);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case RestUtils.REQUESTCODE_TAKE_CAMERA:
                    LogUtil.lsw(localCameraPath);
                    sendCameraImageMessage(localCameraPath);
                    break;
                case RestUtils.REQUESTCODE_TAKE_LOCAL:
                    final String[] all_path = data.getStringArrayExtra("all_path");
                    for (String one_path : all_path)
                    {
                        if (TextUtils.isEmpty(one_path))
                        {
                            return;
                        }

                        ImageFile imgFile = new ImageFile(one_path);
                        Bitmap bmp = imgFile.getBitmapSample(-1, ImageUtil.IMG_FIX_HEIGHT * ImageUtil.IMG_FIX_WIDTH);
                        one_path = LocalFileControl.getInstance(ChatActivity.this).getIMGPath() + File.separator + System.currentTimeMillis() + ".jpg";
                        imgFile.writeBitmapToFile(bmp, one_path, CompressFormat.JPEG, 50 * 1024);

                        images.add(one_path);
                    }
                    sendImageMessage();

                    break;
            }
        }
    }


    private void sendCameraImageMessage(String imagePath)
    {

        if (layout_more.getVisibility() == View.VISIBLE)
        {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }
        manager.sendImageMessage(targetUser, imagePath, new UploadListener()
        {

            @Override
            public void onStart(BmobMsg msg)
            {

                refreshMessage(msg);
            }

            @Override
            public void onSuccess()
            {

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int error, String errorMsg)
            {

                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private void sendImageMessage()
    {

        if (layout_more.getVisibility() == View.VISIBLE)
        {
            layout_more.setVisibility(View.GONE);
            layout_add.setVisibility(View.GONE);
            layout_emo.setVisibility(View.GONE);
        }

        if (images != null && images.size() > 0)
        {
            String poll = images.poll();
            LogUtil.lsw(poll + images.size());
            manager.sendImageMessage(targetUser, poll, new UploadListener()
            {

                @Override
                public void onStart(BmobMsg msg)
                {

                    refreshMessage(msg);
                }

                @Override
                public void onSuccess()
                {
                    // TODO Auto-generated method stub
                    mAdapter.notifyDataSetChanged();
                    handler.sendEmptyMessage(0);
                }

                @Override
                public void onFailure(int error, String errorMsg)
                {

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void showEditState(boolean isEmo)
    {
        edit_user_comment.requestFocus();
        if (isEmo)
        {
            layout_more.setVisibility(View.VISIBLE);
            layout_more.setVisibility(View.VISIBLE);
            layout_emo.setVisibility(View.VISIBLE);
            layout_add.setVisibility(View.GONE);
            layout_voice.setVisibility(View.GONE);
            hideSoftInputView();
        } else
        {
            layout_more.setVisibility(View.GONE);
            InputMethodUtil.showInputMethod(ChatActivity.this, edit_user_comment, 500);
            showSoftInputView();
        }
    }

    public void showSoftInputView()
    {

        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(edit_user_comment, 0);
        }
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        initOrRefresh();
        HomeMessageReceiver.ehList.add(this);
        BmobNotifyManager.getInstance(this).cancelNotify();
        BmobDB.create(this).resetUnread(targetId);
        HomeMessageReceiver.mNewNum = 0;
    }

    @Override
    protected void onPause()
    {
        // TODO Auto-generated method stub
        super.onPause();
        HomeMessageReceiver.ehList.remove(this);
        if (recordManager.isRecording())
        {
            recordManager.cancelRecording();
            layout_record.setVisibility(View.GONE);
        }
        if (NewRecordPlayClickListener.isPlaying && NewRecordPlayClickListener.currentPlayListener != null)
        {
            NewRecordPlayClickListener.currentPlayListener.stopPlayRecord();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {

        public void handleMessage(Message msg)
        {

            if (msg.what == NEW_MESSAGE)
            {
                BmobMsg message = (BmobMsg) msg.obj;
                String uid = message.getBelongId();
                BmobMsg m = BmobChatManager.getInstance(ChatActivity.this).getMessage(message.getConversationId(), message.getMsgTime());
                if (!uid.equals(targetId))
                    return;
                mAdapter.add(m);
                mListView.setSelection(mAdapter.getCount() - 1);
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            } else if (msg.what == 0)
            {
                sendImageMessage();
            }
        }
    };

    private ImageView mBtnVoice;

    private ImageView mBtnGallery;

    private ImageView mBtnCamera;

    private ImageView mBtnLocation;

    private ImageView mBtnKeybord;

    private void initNewMessageBroadCast()
    {
        receiver = new NewBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
        intentFilter.setPriority(5);
        registerReceiver(receiver, intentFilter);
    }

    private class NewBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            String from = intent.getStringExtra("fromId");
            String msgId = intent.getStringExtra("msgId");
            String msgTime = intent.getStringExtra("msgTime");
            if (TextUtils.isEmpty(from) && TextUtils.isEmpty(msgId) && TextUtils.isEmpty(msgTime))
            {
                BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this).getMessage(msgId, msgTime);
                if (!from.equals(targetId))
                    return;
                mAdapter.add(msg);
                mListView.setSelection(mAdapter.getCount() - 1);
                BmobDB.create(ChatActivity.this).resetUnread(targetId);
            }
            abortBroadcast();
        }
    }

    private void refreshMessage(BmobMsg msg)
    {
        mAdapter.add(msg);
        mListView.setSelection(mAdapter.getCount() - 1);
        edit_user_comment.setText("");
    }

    @Override
    public void onMessage(BmobMsg message)
    {
        // TODO Auto-generated method stub
        Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onNetChange(boolean isNetConnected)
    {
        // TODO Auto-generated method stub
        if (!isNetConnected)
        {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation invite)
    {
        // TODO Auto-generated method stub

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
        if (conversionId.split("&")[1].equals(targetId))
        {
            for (BmobMsg msg : mAdapter.getList())
            {
                if (msg.getConversationId().equals(conversionId) && msg.getMsgTime().equals(msgTime))
                {
                    msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (layout_more.getVisibility() == View.VISIBLE)
            {
                layout_more.setVisibility(View.GONE);
                return false;
            } else
            {
                return super.onKeyDown(keyCode, event);
            }
        } else
        {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        hideSoftInputView();
        try
        {
            unregisterReceiver(receiver);
            unregisterReceiver(chatBgBroadCast);
        } catch (Exception e)
        {
            LogUtil.lsw(e.getMessage());
        }
    }

    private void setEmojiconFragment(boolean useSystemDefault)
    {

        getSupportFragmentManager().beginTransaction().replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault)).commit();
    }

    private class ChatBgBroadCast extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {

            if (intent.getAction().equals("android.homemessage.resid"))
            {
                Bundle bundle = intent.getExtras();
                int resId = bundle.getInt("resId");
                mChatBg.setBackgroundResource(resId);
                PreferencesUtil.setIntData(ChatActivity.this, "chatResId", resId);
            }
        }
    }


    @Override
    public void onEmojiconBackspaceClicked(View v)
    {

        EmojiconsFragment.backspace(edit_user_comment);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon)
    {

        EmojiconsFragment.input(edit_user_comment, emojicon);
    }
}
