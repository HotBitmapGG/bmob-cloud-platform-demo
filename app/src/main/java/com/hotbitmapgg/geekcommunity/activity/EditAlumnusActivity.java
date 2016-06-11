package com.hotbitmapgg.geekcommunity.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.alumuns.imageutils.CompressImage;
import com.hotbitmapgg.geekcommunity.alumuns.imageutils.ImageLoader;
import com.hotbitmapgg.geekcommunity.bean.AlummusBean;
import com.hotbitmapgg.geekcommunity.bean.User;
import com.hotbitmapgg.geekcommunity.widget.emojicon.Emojicon;
import com.hotbitmapgg.geekcommunity.widget.emojicon.EmojiconEditText;
import com.hotbitmapgg.geekcommunity.widget.emojicon.EmojiconGridFragment;
import com.hotbitmapgg.geekcommunity.widget.emojicon.EmojiconsFragment;
import com.hotbitmapgg.geekcommunity.pick.Action;
import com.hotbitmapgg.geekcommunity.utils.CacheUtils;
import com.hotbitmapgg.geekcommunity.utils.LogUtil;
import com.hotbitmapgg.geekcommunity.utils.StatusBarCompat;
import com.hotbitmapgg.geekcommunity.utils.ToastUtil;
import com.hotbitmapgg.geekcommunity.widget.LoadingDialog;
import com.hotbitmapgg.geekcommunity.widget.SuccessDialog;
import com.ypy.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

/**
 * 说说编辑界面，
 *
 * @author Administrator
 * @hcc
 */
@SuppressLint("ViewHolder")
public class EditAlumnusActivity extends ParentActivity implements OnItemClickListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener
{

    // 进入相册
    private static final int REQUEST_CODE_ALBUM = 1;

    // 进入相机
    private static final int REQUEST_CODE_CAMERA = 2;

    // 进入预览
    private static final int REQUEST_VIEW_PHOTOS = 3;

    // 编辑说说
    private EmojiconEditText content;

    // 发送时间
    private String dateTime;

    public String filePath = "";

    private Button mChoose;

    private Button mCamera;

    private Button mCancle;

    private PopupWindow mAddPopWindown;

    private RelativeLayout mLayout;

    private int imgs = 0;

    // 允许选择的最大图片数量
    private static final int MAX_PHOTOS_COUNT = 9;

    // 图片加载ImageLoad
    private ImageLoader mImageLoader;

    // 显示图片GridView
    private GridView showSelectedPhotosGv;

    // 存储图片路径集合
    private ArrayList<String> photos = new ArrayList<String>();

    // GridView显示缩略图list
    private ArrayList<String> tempImagesPath = new ArrayList<String>();

    // Grid适配器
    private ImagePublishAdapter mImagePublishAdapter;

    // 加载对话框
    private LoadingDialog mDialog;

    // bmbo上传文件path数组
    private String[] imagePaths;

    // 图片缩略图Url的List
    private List<String> thumbnailUrls = new ArrayList<String>();

    // 缩略图是否生成成功
    private boolean flag;

    // 点赞用户名称集合
    private List<String> names = new ArrayList<String>();

    // 添加表情
    private ImageButton mAddEmojis;

    // 表情布局
    private LinearLayout mEmoLayout;

    // 发送成功对话框
    private SuccessDialog mSuccessDialog;

    private Handler mHandler = new Handler()
    {

        public void handleMessage(android.os.Message msg)
        {

            switch (msg.what)
            {
                case 0:
                    // 发送成功 关闭界面 通知圈子界面刷新UI
                    mSuccessDialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                    Bundle mBundle = new Bundle();
                    mBundle.putString("msg", "success");
                    EventBus.getDefault().post(mBundle);

                    break;

                case 1:
                    // 发送失败 关闭对话框 停留当前界面
                    mSuccessDialog.dismiss();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // 设置全局状态栏颜色
        StatusBarCompat.compat(this);

        mDialog = new LoadingDialog(EditAlumnusActivity.this, R.style.Loading);
        mSuccessDialog = new SuccessDialog(EditAlumnusActivity.this, R.style.Loading);
        Bmob.initialize(this, com.hotbitmapgg.geekcommunity.config.Config.applicationId);
        mImageLoader = ImageLoader.getInstance();
        initTitle();
        initView();
        setEmojiconFragment(true);
    }

    private void initTitle()
    {

        View view = findViewById(R.id.edit_top);
        ImageView mLeftBack = (ImageView) view.findViewById(R.id.left_btn);
        TextView mTitle = (TextView) view.findViewById(R.id.top_title);
        mTitle.setVisibility(View.GONE);
        mLeftBack.setVisibility(View.VISIBLE);
        mLeftBack.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                finish();
            }
        });

        TextView mRightText = (TextView) view.findViewById(R.id.right_tv);
        mRightText.setVisibility(View.VISIBLE);
        mRightText.setText("发送");
        mRightText.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                mEmoLayout.setVisibility(View.GONE);
                String text = content.getText().toString().trim();
                if (TextUtils.isEmpty(text))
                {
                    ToastUtil.ShortToast("内容不能为空");
                    return;
                }
                mDialog.setMessage("发送中...");
                mDialog.show();
                if (!tempImagesPath.isEmpty())
                {
                    // 带图片的说说
                    sendAlumunsContent(text);
                } else
                {
                    // 不带图片的说说
                    sendAlumunsPic(text, null);
                }
            }
        });
    }

    private void initView()
    {

        content = (EmojiconEditText) findViewById(R.id.edit_content);
        mLayout = (RelativeLayout) findViewById(R.id.edit_root);
        mAddEmojis = (ImageButton) findViewById(R.id.add_emojis);
        mEmoLayout = (LinearLayout) findViewById(R.id.edit_layout_emo);
        showSelectedPhotosGv = (GridView) findViewById(R.id.gv_show_photos);
        showSelectedPhotosGv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        showSelectedPhotosGv.setOnItemClickListener(this);
        mImagePublishAdapter = new ImagePublishAdapter(photos);
        showSelectedPhotosGv.setAdapter(mImagePublishAdapter);

        content.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // 隐藏表情布局
                mEmoLayout.setVisibility(View.GONE);
            }
        });

        mAddEmojis.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // 添加表情 显示表情布局
                mEmoLayout.setVisibility(View.VISIBLE);
                // 隐藏软键盘
                hideSoftInputView();
            }
        });
    }

    /**
     * 发送图片的说说
     */
    private void sendAlumunsContent(final String text)
    {

        // 初始化bmob文件上传对象
        BmobProFile bmobProFile = BmobProFile.getInstance(EditAlumnusActivity.this);
        // list转换为file数组进行上传
        int size = tempImagesPath.size();
        String[] paths = tempImagesPath.toArray(new String[size]);
        bmobProFile.uploadBatch(paths, new com.bmob.btp.callback.UploadBatchListener()
        {


            @Override
            public void onError(int errorCode, String errorMsg)
            {
                // TODO Auto-generated method stub
                LogUtil.lsw("上传错误日志:" + errorMsg);
            }

            @Override
            public void onSuccess(boolean isFinish, String[] fileNames, String[] urls, BmobFile[] files)
            {
                // isFinish ：批量上传是否完成 fileNames：文件名数组 urls: url：文件地址数组 files:
                // BmobFile文件数组，`V3.4.1版本`开始提供，用于兼容新旧文件服务。

                if (isFinish)
                {
                    // ToastUtil.ShortToast("发送成功~");
                    sendAlumunsPic(text, files);
                }
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent)
            {
                // curIndex:表示当前第几个文件正在上传,
                // curPercent:表示当前上传文件的进度值（百分比）,total:表示总的上传文件数,totalPercent:表示总的上传进度（百分比）

                LogUtil.lsw(curIndex + "个文件正在上传" + "~~" + curPercent + "%" + "~~~" + "上传的文件数量 : = " + total);
            }
        });
    }

    /**
     * 发送图片说说
     *
     * @param text
     * @param files
     */
    protected void sendAlumunsPic(String text, BmobFile[] files)
    {

        User user = BmobUser.getCurrentUser(EditAlumnusActivity.this, User.class);
        AlummusBean mAlummusBean = new AlummusBean();
        mAlummusBean.setAuthor(user);
        mAlummusBean.setContent(text);
        if (files != null && files.length > 0)
        {
            List<BmobFile> contentfigureurls = Arrays.asList(files);
            mAlummusBean.setContentfigureurls(contentfigureurls);
        }
        // 获取当前用户手机品牌类型
        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        mAlummusBean.setFormPhone(manufacturer + " " + model);
        mAlummusBean.setLove(0);
        mAlummusBean.setHate(0);
        mAlummusBean.setShare(0);
        mAlummusBean.setComment(0);
        mAlummusBean.setPass(true);
        mAlummusBean.setNames(names);
        mAlummusBean.save(EditAlumnusActivity.this, new SaveListener()
        {

            @Override
            public void onSuccess()
            {

                LogUtil.lsw("动态发送成功~");
                mDialog.dismiss();
                // 发送成功
                mSuccessDialog.setMessage("发送成功");
                mSuccessDialog.setImageRes(R.drawable.action_button_ok_preseed_light);
                mSuccessDialog.show();
                // 延迟发送成功消息
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }

            @Override
            public void onFailure(int errorCode, String errorMsg)
            {

                LogUtil.lsw("动态发送失败~" + "错误信息:" + errorMsg);
                mDialog.dismiss();
                // 发送失败
                mSuccessDialog.setMessage("发送失败");
                mSuccessDialog.setImageRes(R.drawable.action_button_close_preseed_light);
                mSuccessDialog.show();
                // 延迟发送失败消息
                mHandler.sendEmptyMessageDelayed(0, 1500);
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_CODE_ALBUM:

                    final String[] all_path = data.getStringArrayExtra("all_path");

                    mDialog.setMessage("加载中...");
                    mDialog.show();
                    new Handler().postDelayed(new Runnable()
                    {

                        @Override
                        public void run()
                        {

                            for (int i = 0; i < all_path.length; i++)
                            {
                                photos.add(all_path[i]);
                                LogUtil.lsw(photos.toString());
                            }

                            // 对选中的图片进行压缩
                            tempImagesPath = CompressImage.compressedImagesPaths(photos);

                            mImagePublishAdapter.photos = tempImagesPath;

                            mImagePublishAdapter.notifyDataSetChanged();

                            mDialog.dismiss();
                        }
                    }, 100);

                    break;
                case REQUEST_CODE_CAMERA:

                    String path = data.getStringExtra(CameraActivity.IMAGE_PATH);
                    photos.add(path);

                    try
                    {
                        // 对选中的图片进行压缩
                        tempImagesPath = CompressImage.compressedImagesPaths(photos);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    mImagePublishAdapter.photos = tempImagesPath;
                    mImagePublishAdapter.notifyDataSetChanged();
                    // 将拍的照片保存在/diandi100/image路径下, 并且插入到系统图库
                    saveImageToGallery(path);

                    break;

                case REQUEST_VIEW_PHOTOS:
                    // 图片预览
                    ArrayList<String> photos = data.getStringArrayListExtra("all_path");
                    this.photos = photos;
                    tempImagesPath = photos;
                    mImagePublishAdapter = new ImagePublishAdapter(photos);
                    showSelectedPhotosGv.setAdapter(mImagePublishAdapter);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 弹出泡泡窗体
     */
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
                // 拍照
                mChoose.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
                mCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_top));

                Intent mIntent = new Intent(EditAlumnusActivity.this, CameraActivity.class);
                startActivityForResult(mIntent, REQUEST_CODE_CAMERA);

                mAddPopWindown.dismiss();
            }
        });
        mChoose.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View view)
            {

                // 相册
                mCamera.setBackgroundColor(getResources().getColor(R.color.base_color_text_white));
                mChoose.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_rounded_top));

                Intent intent = new Intent(Action.ACTION_MULTIPLE_PICK);
                intent.putExtra("limit", 9 - imgs);
                startActivityForResult(intent, REQUEST_CODE_ALBUM);
                mAddPopWindown.dismiss();
            }
        });

        mCancle.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // 取消
                mAddPopWindown.dismiss();
            }
        });

        mAddPopWindown = new PopupWindow(view, mScreenWidth, 600);
        mAddPopWindown.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mAddPopWindown.setTouchInterceptor(new OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                {
                    mAddPopWindown.dismiss();
                    return true;
                }
                return false;
            }
        });
        mAddPopWindown.setOnDismissListener(new OnDismissListener()
        {

            @Override
            public void onDismiss()
            {

                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        mAddPopWindown.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mAddPopWindown.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mAddPopWindown.setTouchable(true);
        mAddPopWindown.setFocusable(true);
        mAddPopWindown.setOutsideTouchable(true);
        mAddPopWindown.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        mAddPopWindown.setAnimationStyle(R.style.anim_popup_dir);
        mAddPopWindown.showAtLocation(mLayout, Gravity.BOTTOM, 0, 0);
        // 设置pop弹出后 背景变暗色
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = .3f;
        getWindow().setAttributes(lp);
    }

    /**
     * 讲本地文件转换为bitmap
     *
     * @param srcPath
     * @return
     */
    private Bitmap compressImageFromFile(String srcPath)
    {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;
        float ww = 480f;
        int be = 1;
        if (w > h && w > ww)
        {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh)
        {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        newOpts.inPreferredConfig = Config.ARGB_8888;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return bitmap;
    }

    /**
     * 保存图片到本地存储
     *
     * @param bitmap
     * @return
     */
    public String saveToSdCard(Bitmap bitmap)
    {

        String files = CacheUtils.getCacheDirectory(EditAlumnusActivity.this, true, "pic") + dateTime + "_11.jpg";
        File file = new File(files);
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out))
            {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    /**
     * 添加图片Adapter
     *
     * @author Administrator
     */
    class ImagePublishAdapter extends BaseAdapter
    {

        public ArrayList<String> photos;

        public ImagePublishAdapter(ArrayList<String> photos)
        {

            this.photos = photos;
        }

        @Override
        public int getCount()
        {

            if (photos == null || photos.size() == 0)
            {
                // 用于展示添加图标
                return 1;
            } else if (photos.size() == MAX_PHOTOS_COUNT)
            {
                return MAX_PHOTOS_COUNT;
            } else
            {
                return photos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position)
        {

            if (photos != null && photos.size() == MAX_PHOTOS_COUNT)
            {
                return photos.get(position);
            } else if (photos == null || (position - 1) < 0 || position > photos.size())
            {
                return null;
            } else
            {
                return photos.get(position);
            }
        }

        @Override
        public long getItemId(int position)
        {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            imgs = photos.size();
            convertView = getLayoutInflater().inflate(R.layout.image_publish_item, parent, false);
            ImageView mImageView = (ImageView) convertView.findViewById(R.id.iv_image);

            if (isShowAddItem(position))
            {
                mImageView.setImageResource(R.drawable.btn_add_photos_selector);
            } else
            {
                String imagePath = photos.get(position);
                mImageLoader.loadImage(imagePath, mImageView, false);
            }
            return convertView;
        }

        /**
         * 根据position确定显示什么item
         *
         * @param position
         * @return
         */
        private boolean isShowAddItem(int position)
        {

            int size = (photos == null ? 0 : photos.size());
            return position == size;
        }
    }

    /**
     * 返回图片的数量
     *
     * @return
     */
    private int getPhotosSize()
    {

        return mImagePublishAdapter.photos == null ? 0 : mImagePublishAdapter.photos.size();
    }

    /**
     * showSelectedPhotosGv 的条目点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        if (position == getPhotosSize())
        {
            showAvatarPop();
        } else
        {
            Intent intent = new Intent(EditAlumnusActivity.this, AlumunsViewImageActivity.class);
            intent.putExtra("path", mImagePublishAdapter.photos.get(position));
            intent.putExtra("index", position);
            intent.putStringArrayListExtra("alls", mImagePublishAdapter.photos);
            startActivityForResult(intent, REQUEST_VIEW_PHOTOS);
        }
    }

    /**
     * 保存到本地
     *
     * @param path
     */
    public void saveImageToGallery(String path)
    {

        if (TextUtils.isEmpty(path))
        {
            return;
        }

        insertImage(path);
    }

    /**
     * 把拍照后的图片插入到系统相册中
     *
     * @param fileName
     */
    public void insertImage(String fileName)
    {

        MediaScannerConnection.scanFile(this, new String[]{fileName}, new String[]{"image/jpeg"}, new MediaScannerConnection.MediaScannerConnectionClient()
        {

            @Override
            public void onMediaScannerConnected()
            {

            }

            @Override
            public void onScanCompleted(String path, Uri uri)
            {

            }
        });
    }

    /**
     * 设置表情布局
     *
     * @param useSystemDefault
     */
    private void setEmojiconFragment(boolean useSystemDefault)
    {

        getSupportFragmentManager().beginTransaction().replace(R.id.edit_emojicons, EmojiconsFragment.newInstance(useSystemDefault)).commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon)
    {

        EmojiconsFragment.input(content, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v)
    {

        EmojiconsFragment.backspace(content);
    }

    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        photos.clear();
        tempImagesPath.clear();
        if (mDialog.isShowing())
        {
            mDialog.dismiss();
        }
        if (mSuccessDialog.isShowing())
        {
            mSuccessDialog.dismiss();
        }
        mDialog = null;
        mSuccessDialog = null;
    }

    /**
     * 隐藏软键盘 hideSoftInputView
     *
     * @param
     * @return void
     * @throws
     * @Title: hideSoftInputView
     * @Description: TODO
     */
    public void hideSoftInputView()
    {

        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView()
    {

        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(content, 0);
        }
    }
}
