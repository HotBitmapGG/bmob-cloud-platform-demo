package com.hotbitmapgg.geekcommunity.alumuns.imageutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.hotbitmapgg.geekcommunity.R;

import java.lang.ref.WeakReference;


public class RoundImageViewByXfermode extends ImageView
{

    private Paint mPaint;

    private Xfermode mXfermode = new PorterDuffXfermode(Mode.DST_IN);

    private Bitmap mMaskBitmap;

    private WeakReference<Bitmap> mWeakBitmap;

    /**
     * 图片的类型，圆形or圆角
     */
    private int type;

    private int hf = 0;

    public static final int TYPE_CIRCLE = 0;

    public static final int TYPE_ROUND = 1;

    /**
     * 圆角大小的默认值
     */
    private static final int BODER_RADIUS_DEFAULT = 10;

    /**
     * 圆角的大小
     */
    private int mBorderRadius;

    Context mcontext;

    int width = 0;

    public RoundImageViewByXfermode(Context context)
    {

        this(context, null);

        width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                60, ((Activity) mcontext).getResources().getDisplayMetrics());
        mcontext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public RoundImageViewByXfermode(Context context, AttributeSet attrs)
    {

        super(context, attrs);
        mcontext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundImageViewByXfermode);

        mBorderRadius = a.getDimensionPixelSize(
                R.styleable.RoundImageViewByXfermode_borderRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        BODER_RADIUS_DEFAULT, getResources()
                                .getDisplayMetrics()));// 默认为10dp
        Log.e("TAG", mBorderRadius + "");
        type = a.getInt(R.styleable.RoundImageViewByXfermode_type, TYPE_CIRCLE);// 默认为Circle

        hf = a.getInt(R.styleable.RoundImageViewByXfermode_hf, 0);// 默认all

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 如果类型是圆形，则强制改变view的宽高一致，以小值为准
         */
        if (type == TYPE_CIRCLE)
        {
            int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            setMeasuredDimension(width, width);
        }
    }

    @Override
    public void invalidate()
    {

        mWeakBitmap = null;
        if (mMaskBitmap != null)
        {
            mMaskBitmap.recycle();
            mMaskBitmap = null;
        }
        super.invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        // 在缓存中取出bitmap
        Bitmap bitmap = mWeakBitmap == null ? null : mWeakBitmap.get();

        if (null == bitmap || bitmap.isRecycled())
        {
            // 拿到Drawable
            Drawable drawable = getDrawable();
            // 获取drawable的宽和高
            int dWidth = drawable.getIntrinsicWidth();
            int dHeight = drawable.getIntrinsicHeight();

            Log.i("textbook", "dWidth  " + dWidth + "    dHeight" + dHeight);

            if (drawable != null)
            {
                // 创建bitmap
                bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                        Config.ARGB_8888);
                float scale = 1.0f;
                // 创建画布
                Canvas drawCanvas = new Canvas(bitmap);
                // 按照bitmap的宽高，以及view的宽高，计算缩放比例；因为设置的src宽高比例可能和imageview的宽高比例不同，这里我们不希望图片失真；
                if (type == TYPE_ROUND)
                {
                    // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
                    scale = Math.max(getWidth() * 1.0f / dWidth, getHeight()
                            * 1.0f / dHeight);
                } else
                {
                    // 如果是圆
                    scale = width * 1.0F / Math.min(dWidth, dHeight);

                    // if(width>getWidth()){
                    // scale = width * 1.0F / Math.min(dWidth, dHeight);
                    // }else{
                    // scale = getWidth() * 1.0F / Math.min(dWidth, dHeight);
                    // }

                }

                // 根据缩放比例，设置bounds，相当于缩放图片了
                if (stype == 1)
                {
                    drawable.setBounds(0, 0, (int) (scale * dWidth),
                            (int) (scale * dHeight));
                }

                // drawable.setBounds(left, top, right, bottom)
                if (stype == 0)
                {
                    Log.i("bbb", (getWidth() / 2 - dWidth / 2) + " "
                            + (getHeight() / 2 - dHeight / 2) + " "
                            + (getWidth() / 2 + dWidth / 2) + " "
                            + (getHeight() / 2 + dHeight / 2));
                    drawable.setBounds(getWidth() / 2 - dWidth / 2, getHeight()
                                    / 2 - dHeight / 2, getWidth() / 2 + dWidth / 2,
                            getHeight() / 2 + dHeight / 2);
                }

                drawable.draw(drawCanvas);
                if (mMaskBitmap == null || mMaskBitmap.isRecycled())
                {
                    mMaskBitmap = getBitmap();
                }
                // Draw Bitmap.
                mPaint.reset();
                mPaint.setFilterBitmap(false);
                mPaint.setXfermode(mXfermode);
                // 绘制形状
                drawCanvas.drawBitmap(mMaskBitmap, 0, 0, mPaint);
                mPaint.setXfermode(null);
                // 将准备好的bitmap绘制出来
                canvas.drawBitmap(bitmap, 0, 0, null);
                // bitmap缓存起来，避免每次调用onDraw，分配内存
                mWeakBitmap = new WeakReference<Bitmap>(bitmap);
            }
        }
        // 如果bitmap还存在，则直接绘制即可
        if (bitmap != null)
        {
            mPaint.setXfermode(null);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, mPaint);
            return;
        }
    }

    /**
     * 绘制形状
     *
     * @return
     */
    public Bitmap getBitmap()
    {

        Bitmap bitmap;
        if (width > getWidth())
        {
            // 缩放一下图片
            bitmap = Bitmap.createBitmap(width, width, Config.ARGB_8888);
        } else
        {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Config.ARGB_8888);
        }

        Log.i("textbook", "getWidth()" + getWidth() + "    getHeight()"
                + getHeight());
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        // paint.setColor(Color.BLACK);

        if (type == TYPE_ROUND)
        {
            RectF rect, rect2, rect3;
            switch (hf)
            {
                case 0:
                    rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    canvas.drawRoundRect(rect, mBorderRadius, mBorderRadius, paint);
                    break;
                case 1:
                    rect = new RectF(0, 0, bitmap.getWidth(),
                            bitmap.getHeight() / 2);
                    canvas.drawRoundRect(rect, mBorderRadius, mBorderRadius, paint);
                    rect2 = new RectF(0, bitmap.getHeight() / 3, bitmap.getWidth(),
                            bitmap.getHeight() / 2);
                    canvas.drawRoundRect(rect2, 0, 0, paint);
                    rect3 = new RectF(0, bitmap.getHeight() / 2, bitmap.getWidth(),
                            bitmap.getHeight());
                    canvas.drawRoundRect(rect3, 0, 0, paint);
                    break;
                case 2:
                    rect = new RectF(0, 0, bitmap.getWidth(),
                            bitmap.getHeight() / 2);
                    canvas.drawRoundRect(rect, 0, 0, paint);
                    rect2 = new RectF(0, bitmap.getHeight() / 3, bitmap.getWidth(),
                            bitmap.getHeight() / 2);
                    canvas.drawRoundRect(rect2, 0, 0, paint);
                    rect3 = new RectF(0, bitmap.getHeight() / 2, bitmap.getWidth(),
                            bitmap.getHeight());
                    canvas.drawRoundRect(rect3, mBorderRadius, mBorderRadius, paint);
                    break;

                default:
                    break;
            }
        } else
        {

            canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2,
                    paint);
            // canvas.drawCircle(60,60, 60,paint);
        }

        return bitmap;
    }

    @Override
    public ScaleType getScaleType()
    {
        // TODO Auto-generated method stub
        return super.getScaleType();
    }

    public int stype = 0;

    @Override
    public void setScaleType(ScaleType scaleType)
    {
        // TODO Auto-generated method stub
        super.setScaleType(scaleType);

        if (scaleType.equals(ScaleType.CENTER_INSIDE))
        {
            stype = 0;
        }

        if (scaleType.equals(ScaleType.FIT_XY))
        {
            stype = 1;
        }
    }
}
