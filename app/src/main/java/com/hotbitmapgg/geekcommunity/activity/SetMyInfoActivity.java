package com.hotbitmapgg.geekcommunity.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.base.HomeMsgApplication;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.config.RestUtils;
import com.hotbitmapgg.geekcommunity.utils.CollectionUtils;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.PhotoUtil;
import com.hotbitmapgg.geekcommunity.utils.PreferencesUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.hotbitmapgg.geekcommunity.widget.DialogTips;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;
import com.hotbitmapgg.geekcommunity.widget.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ypy.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
@SuppressLint({"SimpleDateFormat", "ClickableViewAccessibility", "InflateParams"})
public class SetMyInfoActivity extends ParentActivity implements OnClickListener
{

    private TextView tv_set_name;

    private TextView tv_set_nick;

    private TextView tv_set_gender;

    private RoundedImageView iv_set_avator;

    private ImageView iv_arraw;

    private ImageView iv_nickarraw;

    private RelativeLayout layout_all;

    private LinearLayout btn_chat;

    private LinearLayout btn_back;

    private Button btn_add_friend;

    private RelativeLayout layout_head;

    private RelativeLayout layout_nick;

    private RelativeLayout layout_gender;

    private RelativeLayout layout_black_tips;

    private String from = "";

    private String username = "";

    private User user;

    private Button mChoose;

    private Button mCamera;

    private PopupWindow avatorPop;

    public String filePath = "";

    private String path;

    private TextView mTitle;

    Bitmap newBitmap;

    boolean isFromCamera = false;

    int degree = 0;

    private Button mCancle;

    String[] sexs = new String[]{"男", "女"};

    private ImageView iv_gerder_arrow;

    private RelativeLayout layout_sign;

    private ImageView iv_sign_arrow;

    private TextView tv_sing;

    private ProgressBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= 14)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        setContentView(R.layout.activity_set_info);

        StatusBarCompat.compat(this);

        from = getIntent().getStringExtra("from");
        username = getIntent().getStringExtra("username");
        initView();
    }

    private void initView()
    {

        View view = findViewById(R.id.set_info_top);
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
        mTitle = (TextView) view.findViewById(R.id.top_title);
        mBar = (ProgressBar) view.findViewById(R.id.top_pro);
        mBar.setVisibility(View.VISIBLE);

        layout_all = (RelativeLayout) findViewById(R.id.layout_all);
        iv_set_avator = (RoundedImageView) findViewById(R.id.iv_set_avator);
        iv_arraw = (ImageView) findViewById(R.id.iv_arraw);
        iv_nickarraw = (ImageView) findViewById(R.id.iv_nickarraw);
        tv_set_name = (TextView) findViewById(R.id.tv_set_name);
        tv_set_nick = (TextView) findViewById(R.id.tv_set_nick);
        layout_head = (RelativeLayout) findViewById(R.id.layout_head);
        layout_nick = (RelativeLayout) findViewById(R.id.layout_nick);
        layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
        iv_gerder_arrow = (ImageView) findViewById(R.id.iv_gender_arrow);
        layout_sign = (RelativeLayout) findViewById(R.id.layout_sign);
        iv_sign_arrow = (ImageView) findViewById(R.id.iv_sing_arrow);
        tv_sing = (TextView) findViewById(R.id.tv_sign);
        layout_black_tips = (RelativeLayout) findViewById(R.id.layout_black_tips);
        tv_set_gender = (TextView) findViewById(R.id.tv_set_gender);
        btn_chat = (LinearLayout) findViewById(R.id.btn_chat);
        btn_back = (LinearLayout) findViewById(R.id.btn_back);
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        btn_add_friend.setEnabled(false);
        btn_chat.setEnabled(false);
        btn_back.setEnabled(false);
        if (from.equals("me"))
        {
            mTitle.setText("个人信息");
            layout_head.setOnClickListener(this);
            layout_nick.setOnClickListener(this);
            //layout_gender.setOnClickListener(this);
            layout_sign.setOnClickListener(this);
            iv_nickarraw.setVisibility(View.VISIBLE);
            iv_gerder_arrow.setVisibility(View.VISIBLE);
            iv_sign_arrow.setVisibility(View.VISIBLE);
            iv_arraw.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
            btn_add_friend.setVisibility(View.GONE);
        } else
        {

            mTitle.setText("详细信息");
            iv_nickarraw.setVisibility(View.INVISIBLE);
            iv_gerder_arrow.setVisibility(View.INVISIBLE);
            iv_sign_arrow.setVisibility(View.INVISIBLE);
            iv_arraw.setVisibility(View.INVISIBLE);
            btn_chat.setVisibility(View.VISIBLE);
            btn_chat.setOnClickListener(this);
            if (from.equals("add"))
            {
                if (mApplication.getContactList().containsKey(username))
                {
                    btn_back.setVisibility(View.VISIBLE);
                    btn_back.setOnClickListener(this);
                } else
                {
                    btn_back.setVisibility(View.GONE);
                    btn_add_friend.setVisibility(View.VISIBLE);
                    btn_add_friend.setOnClickListener(this);
                }
            } else
            {
                btn_back.setVisibility(View.VISIBLE);
                btn_back.setOnClickListener(this);
            }
            initOtherData(username);
        }

        String sex = PreferencesUtil.getStringData(SetMyInfoActivity.this, "sex");
        if (!TextUtils.isEmpty(sex))
        {
            if (sex.equals("男"))
            {
                tv_set_gender.setText("男");
            } else if (sex.equals("女"))
            {
                tv_set_gender.setText("女");
            }
        } else
        {
            tv_set_gender.setText("设置性别");
        }
    }

    private void initMeData()
    {

        User user = userManager.getCurrentUser(User.class);
        LogUtil.lsw("hight = " + user.getHight() + ",sex= " + user.getSex());
        initOtherData(user.getUsername());
    }

    private void initOtherData(String name)
    {

        userManager.queryUser(name, new FindListener<User>()
        {

            @Override
            public void onError(int errorCode, String errorMsg)
            {

                mBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(List<User> users)
            {
                // TODO Auto-generated method stub
                if (users != null && users.size() > 0)
                {
                    user = users.get(0);
                    btn_chat.setEnabled(true);
                    btn_back.setEnabled(true);
                    btn_add_friend.setEnabled(true);
                    updateUser(user);
                } else
                {
                    mBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateUser(User user)
    {

        refreshAvatar(user.getAvatar());
        try
        {
            tv_set_name.setText(user.getUsername());
            tv_set_nick.setText(user.getNick());
            tv_sing.setText(user.getSignature());
            tv_set_gender.setText(user.getSex() == true ? "男" : "女");
            mBar.setVisibility(View.GONE);
        } catch (Exception e)
        {

        }
        if (from.equals("other"))
        {
            if (BmobDB.create(this).isBlackUser(user.getUsername()))
            {
                btn_back.setVisibility(View.GONE);
                layout_black_tips.setVisibility(View.VISIBLE);
            } else
            {
                btn_back.setVisibility(View.VISIBLE);
                layout_black_tips.setVisibility(View.GONE);
            }
        }
    }

    private void refreshAvatar(String avatar)
    {

        if (avatar != null && !avatar.equals(""))
        {
            ImageLoader.getInstance().displayImage(avatar, iv_set_avator, ImageLoadUtil.defaultOptions());
        } else
        {
            iv_set_avator.setImageResource(R.drawable.ico_user_default);
        }
    }

    @Override
    public void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        if (from.equals("me"))
        {
            initMeData();
        }
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        switch (v.getId())
        {
            case R.id.btn_chat:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("user", user);
                startAnimActivity(intent);
                finish();
                break;
            case R.id.layout_head:
                showAvatarPop();
                break;
            case R.id.layout_nick:
                startAnimActivity(UpdateInfoActivity.class);
                break;
            case R.id.btn_back:
                showBlackDialog(user.getUsername());
                break;
            case R.id.btn_add_friend:
                addFriend();
                break;
            case R.id.layout_sign:
                Intent mIntent2 = new Intent(SetMyInfoActivity.this, SetSignTextActivity.class);
                startActivityForResult(mIntent2, 300);
                break;
        }
    }

    private void addFriend()
    {

        final LoadingDialog mLoadingDialog = new LoadingDialog(SetMyInfoActivity.this, R.style.Loading);
        mLoadingDialog.setMessage("请稍候...");
        mLoadingDialog.show();
        BmobChatManager.getInstance(this).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, user.getObjectId(), new PushListener()
        {

            @Override
            public void onSuccess()
            {

                mLoadingDialog.dismiss();
                ToastUtil.ShortToast("已发送,等待对方验证");
            }

            @Override
            public void onFailure(int errorCode, final String errorMsg)
            {

                mLoadingDialog.dismiss();
                LogUtil.lsw(errorMsg);
            }
        });
    }

    private void showBlackDialog(final String username)
    {

        DialogTips dialog = new DialogTips(this, "添加黑名单", "添加黑名单后,你将不再接收到对方的消息,确定要添加嘛?", "确定", true, true);
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialogInterface, int userId)
            {

                userManager.addBlack(username, new UpdateListener()
                {

                    @Override
                    public void onSuccess()
                    {

                        btn_back.setVisibility(View.GONE);
                        layout_black_tips.setVisibility(View.VISIBLE);
                        HomeMsgApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(SetMyInfoActivity.this).getContactList()));
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

    @SuppressWarnings("deprecation")
    private void showAvatarPop()
    {

        View view = LayoutInflater.from(this).inflate(R.layout.pop_showavator, null);
        mChoose = (Button) view.findViewById(R.id.layout_choose);
        mCamera = (Button) view.findViewById(R.id.layout_photo);
        mCancle = (Button) view.findViewById(R.id.cancel);
        mCamera.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                mChoose.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
                mCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_top));
                File dir = new File(RestUtils.MyAvatarDir);
                if (!dir.exists())
                {
                    dir.mkdirs();
                }
                File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
                filePath = file.getAbsolutePath();
                Uri imageUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, RestUtils.REQUESTCODE_UPLOADAVATAR_CAMERA);
            }
        });
        mChoose.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                mCamera.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
                mChoose.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_top));
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, RestUtils.REQUESTCODE_UPLOADAVATAR_LOCATION);
            }
        });

        mCancle.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                avatorPop.dismiss();
            }
        });

        avatorPop = new PopupWindow(view, mScreenWidth, 600);
        avatorPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        avatorPop.setTouchInterceptor(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });
        avatorPop.setOnDismissListener(new OnDismissListener()
        {

            @Override
            public void onDismiss()
            {

                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        avatorPop.setAnimationStyle(R.style.anim_popup_dir);
        avatorPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);
    }

    private void startImageAction(Uri uri, int outputX, int outputY, int requestCode, boolean isCrop)
    {

        Intent intent = null;
        if (isCrop)
        {
            intent = new Intent("com.android.camera.action.CROP");
        } else
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case RestUtils.REQUESTCODE_UPLOADAVATAR_CAMERA:
                if (resultCode == RESULT_OK)
                {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    {
                        return;
                    }
                    isFromCamera = true;
                    File file = new File(filePath);
                    degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
                    startImageAction(Uri.fromFile(file), 200, 200, RestUtils.REQUESTCODE_UPLOADAVATAR_CROP, true);
                }
                break;
            case RestUtils.REQUESTCODE_UPLOADAVATAR_LOCATION:

                if (avatorPop != null)
                {
                    avatorPop.dismiss();
                }
                Uri uri = null;
                if (data == null)
                {
                    return;
                }
                if (resultCode == RESULT_OK)
                {
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    {
                        return;
                    }
                    isFromCamera = false;
                    uri = data.getData();
                    startImageAction(uri, 200, 200, RestUtils.REQUESTCODE_UPLOADAVATAR_CROP, true);
                }

                break;
            case RestUtils.REQUESTCODE_UPLOADAVATAR_CROP:
                if (avatorPop != null)
                {
                    avatorPop.dismiss();
                }
                if (data == null)
                {

                    return;
                } else
                {
                    saveCropAvator(data);
                }
                filePath = "";
                uploadAvatar();
                break;

            case 100:
                if (resultCode == 200)
                {
                    String sex = data.getStringExtra("sex");
                    if (sex.equals("男"))
                    {
                        tv_set_gender.setText(sex);
                        PreferencesUtil.setStringData(SetMyInfoActivity.this, "sex", "男");
                        User currentUser = BmobUser.getCurrentUser(HomeMsgApplication.getInstance(), User.class);
                        currentUser.setSex(true);
                        currentUser.update(this, new UpdateListener()
                        {

                            @Override
                            public void onSuccess()
                            {


                            }

                            @Override
                            public void onFailure(int errorCode, String errorMsg)
                            {


                            }
                        });
                    } else if (sex.equals("女"))
                    {
                        tv_set_gender.setText(sex);
                        PreferencesUtil.setStringData(SetMyInfoActivity.this, "sex", "女");
                        User currentUser = BmobUser.getCurrentUser(HomeMsgApplication.getInstance(), User.class);
                        currentUser.setSex(false);
                        currentUser.update(this, new UpdateListener()
                        {

                            @Override
                            public void onSuccess()
                            {


                            }

                            @Override
                            public void onFailure(int errorCode, String errorMsg)
                            {


                            }
                        });
                    }
                }
                break;

            case 300:
                if (resultCode == 400)
                {
                    Bundle bundle = data.getExtras();
                    String sign = bundle.getString("sign");
                    if (!TextUtils.isEmpty(sign))
                    {
                        tv_sing.setText(sign);
                    }
                }
                break;

            default:
                break;
        }
    }

    private void uploadAvatar()
    {

        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener()
        {

            @Override
            public void onSuccess()
            {

                User currentUser = BmobUser.getCurrentUser(SetMyInfoActivity.this, User.class);
                currentUser.setAvatars(bmobFile);

                updateUserAvatar(currentUser);
            }

            @Override
            public void onProgress(Integer pro)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

            }
        });
    }

    private void updateUserAvatar(final User user)
    {

        BmobFile bmobFile = user.getAvatars();
        final String url = bmobFile.getFileUrl(SetMyInfoActivity.this);
        user.setAvatar(url);
        user.update(SetMyInfoActivity.this, new UpdateListener()
        {

            @Override
            public void onSuccess()
            {

                refreshAvatar(url);
                Bundle mBundle = new Bundle();
                mBundle.putString("head_photo", url);
                EventBus.getDefault().post(mBundle);
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {


            }
        });
    }


    private void saveCropAvator(Intent data)
    {

        Bundle extras = data.getExtras();
        if (extras != null)
        {
            Bitmap bitmap = extras.getParcelable("data");
            Log.i("life", "avatar - bitmap = " + bitmap);
            if (bitmap != null)
            {
                bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
                if (isFromCamera && degree != 0)
                {
                    bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
                }
                iv_set_avator.setImageBitmap(bitmap);
                String filename = new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".png";
                path = RestUtils.MyAvatarDir + filename;
                PhotoUtil.saveBitmap(RestUtils.MyAvatarDir, filename, bitmap, true);
                if (bitmap != null && bitmap.isRecycled())
                {
                    bitmap.recycle();
                }
            }
        }
    }


    private void updateUserData(User user, UpdateListener listener)
    {

        User current = (User) userManager.getCurrentUser(User.class);
        user.setObjectId(current.getObjectId());
        user.update(this, listener);
    }
}
