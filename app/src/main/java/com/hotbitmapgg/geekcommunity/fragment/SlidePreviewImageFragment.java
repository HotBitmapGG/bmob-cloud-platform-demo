package com.hotbitmapgg.geekcommunity.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hotbitmapgg.geekcommunity.R;
import com.hotbitmapgg.geekcommunity.utils.ImageLoadUtil;
import com.hotbitmapgg.geekcommunity.utils.UIHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

/**
 * 图片预览fragment
 *
 * @author Administrator
 */
public class SlidePreviewImageFragment extends Fragment
{

    private DisplayImageOptions options;
//	private ImageZoomView zoomView;// 自定义的图片显示组件
//	private ImageZoomState zoomState;// 图片缩放和移动状态类
//	private SlideImageZoomListener zoomListener;// 缩放事件监听器


    private ProgressBar mProgressBar;

    private String filePath;

    private AlertDialog showSavePictureAlertDialog;

    private MediaScannerConnection msc = null;

    private PhotoView mPhotoView;

    public static Fragment newInstance(String url)
    {

        SlidePreviewImageFragment fragment = new SlidePreviewImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.slide_preview_image_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        super.onViewCreated(view, savedInstanceState);
//		zoomState = new ImageZoomState();
//		zoomListener = new SlideImageZoomListener();
//		zoomListener.setZoomState(zoomState);

        //zoomView = (ImageZoomView) view.findViewById(R.id.zoomView);

        mPhotoView = (PhotoView) view.findViewById(R.id.photoview);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {

        super.onActivityCreated(savedInstanceState);

        // 设置下载的图片是否缓存在内存中
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        Bundle bundle = getArguments();
        filePath = bundle.getString("url");
        if (filePath != null && !filePath.startsWith("http"))
        {
            filePath = "file://" + filePath;
        }
        ImageLoader.getInstance().loadImage(filePath, options, new ImageLoadingListener()
        {

            @Override
            public void onLoadingStarted(String arg0, View arg1)
            {

                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2)
            {

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap)
            {

                mProgressBar.setVisibility(View.GONE);
                mPhotoView.setImageBitmap(bitmap);
                mPhotoView.setOnPhotoTapListener(new OnPhotoTapListener()
                {

                    @Override
                    public void onPhotoTap(View arg0, float arg1, float arg2)
                    {

                        getActivity().finish();
                    }

                    @Override
                    public void onOutsidePhotoTap()
                    {

                    }
                });
                mPhotoView.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {

                        getActivity().finish();
                    }
                });
                mPhotoView.setOnLongClickListener(new OnLongClickListener()
                {

                    @Override
                    public boolean onLongClick(View v)
                    {

                        showSaveDialog(filePath);

                        return true;
                    }
                });
//				zoomView.setImage(bitmap);
//				zoomView.setImageZoomState(zoomState);
//				zoomView.setOnTouchListener(zoomListener);
//				zoomView.setOnClickListener(new OnClickListener()
//				{
//					@Override
//					public void onClick(View v)
//					{
//						if (zoomListener.isSingleTouch())
//						{
//							getActivity().finish();
//						}
//					}
//				});
//				zoomView.setOnLongClickListener(new OnLongClickListener()
//				{
//
//					@Override
//					public boolean onLongClick(View v)
//					{
//						// 这个长按监听不准确，刚点击下去就能触发长按，可能是监听的OnTouchListener造成的，所以这里再加个长按判断
//						if (zoomListener.isSingleTouch() && !zoomListener.isMulPoint())
//						{
//							showSaveDialog(filePath);
//						}
//						return true;
//					}
//
//				});
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1)
            {

                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showSaveDialog(final String url)
    {

        showSavePictureAlertDialog = new AlertDialog.Builder(getActivity()).create();
        showSavePictureAlertDialog.setOnDismissListener(new OnDismissListener()
        {

            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
                // TODO Auto-generated method stub
            }
        });
        showSavePictureAlertDialog.show();
        Window window = showSavePictureAlertDialog.getWindow();
        window.setContentView(R.layout.archives_save_dialog);

        TextView tv_content = (TextView) window.findViewById(R.id.content);

        tv_content.setText("是否保存该图片？");

        Button okButton = (Button) window.findViewById(R.id.okBtn);
        okButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                showSavePictureAlertDialog.dismiss();
                savePicture(url);
            }
        });

        Button cancelButton = (Button) window.findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                showSavePictureAlertDialog.dismiss();
            }
        });
        Display d = getActivity().getWindow().getWindowManager().getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams layoutParams = showSavePictureAlertDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        layoutParams.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.85
        showSavePictureAlertDialog.getWindow().setAttributes(layoutParams);
    }

    private void savePicture(String url)
    {

        ImageLoader.getInstance().loadImage(url, ImageLoadUtil.defaultOptions(), new ImageLoadingListener()
        {

            @Override
            public void onLoadingStarted(String imageUri, View view)
            {

                UIHelper.showDialog(getActivity(), "正在保存...");
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason)
            {

                UIHelper.dismissdialog();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
            {

                UIHelper.dismissdialog();
                try
                {

                    String take_photo_save_path = Environment.getExternalStorageDirectory() + "/homemessage/";
                    File file = new File(take_photo_save_path);
                    if (!file.exists())
                    {
                        file.mkdirs();
                    }

                    String filename = DateFormat.format("yyyy-MM-dd HH.mm.ss", System.currentTimeMillis()).toString() + ".jpg";
                    final String photo_name_path = take_photo_save_path + filename;

                    try
                    {
                        FileOutputStream fos = new FileOutputStream(photo_name_path);
                        loadedImage.compress(CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                        Toast.makeText(getActivity(), "已保存到相册", Toast.LENGTH_SHORT).show();

                        // 其次把文件插入到系统图库
                        try
                        {
                            // MediaStore.Images.Media.insertImage(context.getContentResolver(),photo_name_path,
                            // filename, null);

                            // 最后通知图库更新，速度快
                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + photo_name_path)));

                            // 速度慢
                            msc = new MediaScannerConnection(getActivity(), new MediaScannerConnectionClient()
                            {

                                public void onMediaScannerConnected()
                                {

                                    msc.scanFile(photo_name_path, "image/jpeg");
                                }

                                public void onScanCompleted(String path, Uri uri)
                                {

                                    msc.disconnect();
                                }
                            });
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            Log.i("eee", e.toString());
                        }
                    } catch (Exception e)
                    {
                        Log.i("eee", e.toString());
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view)
            {
                // TODO Auto-generated method stub

            }
        });
    }
}
