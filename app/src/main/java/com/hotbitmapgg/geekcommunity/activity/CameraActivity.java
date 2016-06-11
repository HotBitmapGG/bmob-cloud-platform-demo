package com.hotbitmapgg.geekcommunity.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.hotbitmapgg.geekcommunity.utils.LocalFileControl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends ParentActivity
{

    private String mImageFilePath;

    public static final String IMAGEFILEPATH = "ImageFilePath";

    public final static String IMAGE_PATH = "image_path";

    static Activity mContext;

    public final static int GET_IMAGE_REQ = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            mImageFilePath = savedInstanceState.getString(IMAGEFILEPATH);
            File mFile = new File(IMAGEFILEPATH);
            if (mFile.exists())
            {
                Intent rsl = new Intent();
                rsl.putExtra(IMAGE_PATH, mImageFilePath);
                setResult(Activity.RESULT_OK, rsl);
                finish();
            } else
            {

            }
        }

        mContext = this;
        if (savedInstanceState == null)
        {
            initialUI();
        }
    }

    public void initialUI()
    {
        mImageFilePath = LocalFileControl.getInstance(CameraActivity.this).getIMGPath() + "/" + _getPhotoFilename(new Date());
        File out = new File(mImageFilePath);
        showCamera(out);
    }

    private String _getPhotoFilename(Date date)
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddKms");
        return dateFormat.format(date) + ".jpg";
    }

    private void showCamera(File out)
    {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
        startActivityForResult(intent, GET_IMAGE_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {

        if (GET_IMAGE_REQ == requestCode && resultCode == Activity.RESULT_OK)
        {
            Intent rsl = new Intent();
            rsl.putExtra(IMAGE_PATH, mImageFilePath);
            mContext.setResult(Activity.RESULT_OK, rsl);
            mContext.finish();
        } else
        {
            mContext.finish();
        }
    }

    @Override
    protected void onDestroy()
    {

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {

        super.onSaveInstanceState(outState);
        outState.putString("ImageFilePath", mImageFilePath + "");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {

        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {

        super.onRestoreInstanceState(savedInstanceState);
    }
}
