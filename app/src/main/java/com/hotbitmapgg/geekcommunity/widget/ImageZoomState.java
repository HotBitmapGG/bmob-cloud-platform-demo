package com.hotbitmapgg.geekcommunity.widget;

import java.util.Observable;

public class ImageZoomState extends Observable
{

    private float mZoom = 1.0f;// 控制图片缩放的变量，表示缩放倍数，值越大图像越大

    private float mPanX = 0.5f;// 控制图片水平方向移动的变量，值越大图片可视区域的左边界距离图片左边界越远，图像越靠左，值为0.5f时居中

    private float mPanY = 0.5f;// 控制图片水平方向移动的变量，值越大图片可视区域的上边界距离图片上边界越远，图像越靠上，值为0.5f时居中

    public float getmZoom()
    {

        return mZoom;
    }

    public void setmZoom(float mZoom)
    {

        if (this.mZoom != mZoom)
        {
            this.mZoom = mZoom < 1.0f ? 1.0f : mZoom;// 保证图片最小为原始状态
            if (this.mZoom == 1.0f)
            {// 返回初始大小时，使其位置也恢复原始位置
                this.mPanX = 0.5f;
                this.mPanY = 0.5f;
            }
            this.setChanged();
        }
    }

    public float getmPanX()
    {

        return mPanX;
    }

    public void setmPanX(float mPanX)
    {

        if (mZoom == 1.0f)
        {// 使图为原始大小时不能移动
            return;
        }
        if (this.mPanX != mPanX)
        {
            this.mPanX = mPanX;
            this.setChanged();
        }
    }

    public float getmPanY()
    {

        return mPanY;
    }

    public void setmPanY(float mPanY)
    {

        if (mZoom == 1.0f)
        {// 使图为原始大小时不能移动
            return;
        }
        if (this.mPanY != mPanY)
        {
            this.mPanY = mPanY;
            this.setChanged();
        }
    }

    public float getZoomX(float aspectQuotient)
    {

        return Math.min(mZoom, mZoom * aspectQuotient);
    }

    public float getZoomY(float aspectQuotient)
    {

        return Math.min(mZoom, mZoom / aspectQuotient);
    }
}
