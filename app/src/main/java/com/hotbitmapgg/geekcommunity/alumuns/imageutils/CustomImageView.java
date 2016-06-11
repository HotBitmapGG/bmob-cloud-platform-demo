package com.hotbitmapgg.geekcommunity.alumuns.imageutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.hotbitmapgg.geekcommunity.R;


/**
 * @author
 */
public class CustomImageView extends View
{

    /**
     * TYPE_CIRCLE / TYPE_ROUND
     */
    private int type;

    private static final int TYPE_CIRCLE = 0;

    private static final int TYPE_ROUND = 1;

    private int hf = 0;

    private Bitmap mSrc;

    private int mRadius;

    private int mWidth;

    private int mHeight;

    public CustomImageView(Context context, AttributeSet attrs)
    {

        this(context, attrs, 0);
    }

    public CustomImageView(Context context)
    {

        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CustomImageView(Context context, AttributeSet attrs, int defStyle)
    {

        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CustomImageView, defStyle, 0);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.CustomImageView_src:
                    mSrc = BitmapFactory.decodeResource(getResources(),
                            a.getResourceId(attr, 0));
                    break;
                case R.styleable.CustomImageView_type:
                    type = a.getInt(attr, 0);
                    break;
                case R.styleable.CustomImageView_borderRadius:
                    mRadius = a.getDimensionPixelSize(attr, (int) TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,
                                    getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomImageView_hf:
                    hf = a.getInt(attr, 0);
                    break;
            }
        }
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mWidth = specSize;
        } else
        {
            int desireByImg = getPaddingLeft() + getPaddingRight()
                    + mSrc.getWidth();
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mWidth = Math.min(desireByImg, specSize);
            } else

                mWidth = desireByImg;
        }


        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mHeight = specSize;
        } else
        {
            int desire = getPaddingTop() + getPaddingBottom()
                    + mSrc.getHeight();

            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mHeight = Math.min(desire, specSize);
            } else
                mHeight = desire;
        }

        setMeasuredDimension(mWidth, mHeight);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {

        switch (type)
        {

            case TYPE_CIRCLE:
                int min = Math.min(mWidth, mHeight);

                mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
                canvas.drawBitmap(createCircleImage(mSrc, min), 0, 0, null);
                break;
            case TYPE_ROUND:
                canvas.drawBitmap(createRoundConerImage(mSrc), 0, 0, null);
                break;
        }
    }

    /**
     * @param source
     * @param min
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int min)
    {

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);

        Canvas canvas = new Canvas(target);

        canvas.drawCircle(min / 2, min / 2, min / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    /**
     * @param source
     * @return
     */
    private Bitmap createRoundConerImage(Bitmap source)
    {

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        // new RectF(left, top, right, bottom)
        RectF rect, rect2, rect3;
        switch (hf)
        {
            case 0:
                rect = new RectF(0, 0, source.getWidth(), source.getHeight());
                canvas.drawRoundRect(rect, mRadius, mRadius, paint);
                break;
            case 1:
                rect = new RectF(0, 0, source.getWidth(), source.getHeight() / 2);
                canvas.drawRoundRect(rect, mRadius, mRadius, paint);
                rect2 = new RectF(0, source.getHeight() / 3, source.getWidth(),
                        source.getHeight() / 2);
                canvas.drawRoundRect(rect2, 0, 0, paint);
                rect3 = new RectF(0, source.getHeight() / 2, source.getWidth(),
                        source.getHeight());
                canvas.drawRoundRect(rect3, 0, 0, paint);
                break;
            case 2:
                rect = new RectF(0, 0, source.getWidth(), source.getHeight() / 2);
                canvas.drawRoundRect(rect, 0, 0, paint);
                rect2 = new RectF(0, source.getHeight() / 3, source.getWidth(),
                        source.getHeight() / 2);
                canvas.drawRoundRect(rect2, 0, 0, paint);
                rect3 = new RectF(0, source.getHeight() / 2, source.getWidth(),
                        source.getHeight());
                canvas.drawRoundRect(rect3, mRadius, mRadius, paint);
                break;

            default:
                break;
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);

        return target;
    }
}
