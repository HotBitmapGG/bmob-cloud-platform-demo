package com.hotbitmapgg.geekcommunity.widget.pullzoomlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 头部下拉刷新View
 * @author Administrator
 * @hcc
 *
 */
public class SlidingView extends ViewGroup
{

	//无效的点
	private static final int INVALID_POINTER = -1;

	//滑动动画执行的时间
	private static final int MAX_SETTLE_DURATION = 600; // ms

	//最小滑动距离，结合加速度来判断需要滑动的方向
	private static final int MIN_DISTANCE_FOR_FLING = 25; // dips

	//定义了一个时间插值器，根据ViewPage控件来定义的
	private static final Interpolator sInterpolator = new Interpolator()
	{
		public float getInterpolation(float t)
		{
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	//内容View
	private View mContent;

	//是否开始滑动
	private boolean mIsBeingDragged;

	//是否停止滑动
	private boolean mIsUnableToDrag;

	//自动滚动工具
	private Scroller mScroller;

	//判断是否已经在滚动
	private boolean mScrolling;

	//共外面调用的监听事件
	private OnPageChangeListener mListener;

	//内部监听事件
	private OnPageChangeListener mInternalPageChangeListener;

	//记录上一次手指触摸的点
	private float mLastMotionX;
	private float mLastMotionY;

	//记录最初触摸的点
	private float mInitialMotionX;

	//当前活动的点Id,有效的点的Id
	protected int mActivePointerId = INVALID_POINTER;

	//加速度工具类
	protected VelocityTracker mVelocityTracker;

	//最大加速度的值
	private int mMinMunVelocity;

	//最小加速度的值
	private int mMaxMunVelocity;

	//滑动的距离
	private int mFlingDistance;

	//开始滑动的标志距离
	private int mTouchSlop;

	//是否可以触摸滑动的标志
	private boolean isEnable = true;

	//没有滑动，正常显示
	private int mCurItem = 0;

	//用于绘制阴影时的梯度变化
	private float mRatio;

	//页面滑动的距离
	private int mPix;

	//绘制阴影背景的画笔
	private Paint mShadowPaint;

	//页面边缘的阴影图
	private Drawable mLeftShadow;

	//页面边缘阴影的宽度默认值
	private final int SHADOW_WIDTH = 6;

	//页面边缘阴影的宽度
	private int mShadowWidth;

	//这个标志是用来判断是否需要绘制阴影效果
	private boolean mShouldDraw = true;

	public SlidingView(Context context)
	{
		this(context, null);
	}

	public SlidingView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public SlidingView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initCustomView(context);
	}

	private void initCustomView(Context context)
	{
		setWillNotDraw(false);
		mScroller = new Scroller(context, sInterpolator);
		mInternalPageChangeListener = new InternalPageChangeListener();
		mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// mLeftShadow = getResources().getDrawable(R.drawable.left_shadow) ;

		mShadowPaint.setColor(0xff000000);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
		mMinMunVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaxMunVelocity = configuration.getScaledMaximumFlingVelocity();
		final float density = context.getResources().getDisplayMetrics().density;
		mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
		mShadowWidth = (int) (SHADOW_WIDTH * density);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		final int width = r - l;
		final int height = b - t;
		mContent.layout(0, 0, width, height);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if (!isEnable)
		{
			return false;
		}
		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP || action != MotionEvent.ACTION_DOWN && mIsUnableToDrag)
		{
			endToDrag();
			return false;
		}
		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
				//计算 x，y 的距离
				int index = MotionEventCompat.getActionIndex(ev);
				mActivePointerId = MotionEventCompat.getPointerId(ev, index);
				if (mActivePointerId == INVALID_POINTER)
					break;
				mLastMotionX = mInitialMotionX = MotionEventCompat.getX(ev, index);
				mLastMotionY = MotionEventCompat.getY(ev, index);
				//这里判读，如果这个触摸区域是允许滑动拦截的，则拦截事件
				if (thisTouchAllowed(ev))
				{
					mIsBeingDragged = false;
					mIsUnableToDrag = false;
				}
				else
				{
					mIsUnableToDrag = true;
				}
				if (!mScroller.isFinished())
				{
					startDrag();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				//继续判断是否需要拦截
				determineDrag(ev);
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				//这里做了对多点触摸的处理，当有多个手指触摸的时候依然能正确的滑动
				onSecondaryPointerUp(ev);
				break;

		}
		if (!mIsBeingDragged)
		{
			if (mVelocityTracker == null)
			{
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(ev);
		}
		return mIsBeingDragged;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (!isEnable)
		{
			return false;
		}
		if (!mIsBeingDragged && !thisTouchAllowed(event))
			return false;
		final int action = event.getAction();

		if (mVelocityTracker == null)
		{
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (action & MotionEventCompat.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				//按下则结束滚动
				completeScroll();
				int index = MotionEventCompat.getActionIndex(event);
				mActivePointerId = MotionEventCompat.getPointerId(event, index);
				mLastMotionX = mInitialMotionX = event.getX();
				break;
			case MotionEventCompat.ACTION_POINTER_DOWN:
			{
				//有多个点按下的时候，取最后一个按下的点为有效点
				final int indexx = MotionEventCompat.getActionIndex(event);
				mLastMotionX = MotionEventCompat.getX(event, indexx);
				mActivePointerId = MotionEventCompat.getPointerId(event, indexx);
				break;

			}
			case MotionEvent.ACTION_MOVE:
				if (!mIsBeingDragged)
				{
					determineDrag(event);
					if (mIsUnableToDrag)
						return false;
				}
				//如果已经是滑动状态，则根据手势滑动，而改变View 的位置
				if (mIsBeingDragged)
				{
					// 以下代码用来判断和执行View 的滑动
					final int activePointerIndex = getPointerIndex(event, mActivePointerId);
					if (mActivePointerId == INVALID_POINTER)
						break;
					final float x = MotionEventCompat.getX(event, activePointerIndex);
					final float deltaX = mLastMotionX - x;
					mLastMotionX = x;
					float oldScrollX = getScrollX();
					float scrollX = oldScrollX + deltaX;
					final float leftBound = getLeftBound();
					final float rightBound = getRightBound();
					if (scrollX < leftBound)
					{
						scrollX = leftBound;
					}
					else if (scrollX > rightBound)
					{
						scrollX = rightBound;
					}

					mLastMotionX += scrollX - (int) scrollX;
					scrollTo((int) scrollX, getScrollY());

				}
				break;
			case MotionEvent.ACTION_UP:
				//如果已经是滑动状态，抬起手指，需要判断滚动的位置
				if (mIsBeingDragged)
				{
					mIsBeingDragged = false;
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity(1000, mMaxMunVelocity);
					int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, mActivePointerId);
					final int scrollX = getScrollX();
					final float pageOffset = (float) (-scrollX) / getContentWidth();
					final int activePointerIndex = getPointerIndex(event, mActivePointerId);
					if (mActivePointerId != INVALID_POINTER)
					{
						final float x = MotionEventCompat.getX(event, activePointerIndex);
						final int totalDelta = (int) (x - mInitialMotionX);
						//这里判断是否滚动到下一页，还是滚回原位置
						int nextPage = determineTargetPage(pageOffset, initialVelocity, totalDelta);
						setCurrentItemInternal(nextPage, true, initialVelocity);
					}
					else
					{
						setCurrentItemInternal(mCurItem, true, initialVelocity);
					}
					mActivePointerId = INVALID_POINTER;
					endToDrag();
				}
				else
				{
					// setCurrentItemInternal(0, true, 0);
					scrollTo(getScrollX(), getScrollY());
					endToDrag();
				}
				break;
			case MotionEventCompat.ACTION_POINTER_UP:
				onSecondaryPointerUp(event);
				int pointerIndex = getPointerIndex(event, mActivePointerId);
				if (mActivePointerId == INVALID_POINTER)
					break;
				mLastMotionX = MotionEventCompat.getX(event, pointerIndex);
				break;
		}

		return true;
	}

	public void setOnPageChangeListener(OnPageChangeListener listener)
	{
		mListener = listener;
	}

	private float getLeftBound()
	{
		return -mContent.getWidth();
	}

	private float getRightBound()
	{
		return 0;
	}

	private int getContentWidth()
	{
		return mContent.getWidth();
	}

	public void setEnable(boolean isEnable)
	{
		this.isEnable = isEnable;
	}

	/**
	 * 通过事件和点的 id 来获取点的索引
	 *
	 * @param ev
	 * @param id
	 * @return
	 */
	private int getPointerIndex(MotionEvent ev, int id)
	{
		int activePointerIndex = MotionEventCompat.findPointerIndex(ev, id);
		if (activePointerIndex == -1)
			mActivePointerId = INVALID_POINTER;
		return activePointerIndex;
	}

	/**
	 * 结束拖拽
	 */
	private void endToDrag()
	{
		mIsBeingDragged = false;
		mIsUnableToDrag = false;
		mActivePointerId = INVALID_POINTER;
		if (mVelocityTracker != null)
		{
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	/**
	 * 可以拖拽
	 */
	private void startDrag()
	{
		mIsBeingDragged = true;
	}

	/**
	 * 这里是多多点触控的控制
	 *
	 * @param ev
	 */
	private void onSecondaryPointerUp(MotionEvent ev)
	{
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId)
		{
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
			if (mVelocityTracker != null)
			{
				mVelocityTracker.clear();
			}
		}
	}

	/**
	 * 决定是否可以拖拽
	 *
	 * @param event
	 */
	private void determineDrag(MotionEvent event)
	{
		/* 这么一大串代码只有一个目的，就是用来获取和判断手指触摸的位置 */
		int pointIndex = MotionEventCompat.getActionIndex(event);
		int pointId = MotionEventCompat.getPointerId(event, pointIndex);
		if (pointId == INVALID_POINTER)
		{
			return;
		}
		final float x = MotionEventCompat.getX(event, pointIndex);
		final float y = MotionEventCompat.getY(event, pointIndex);
		final float dx = x - mLastMotionX;
		final float dy = y - mLastMotionY;
		final float xDiff = Math.abs(dx);
		final float yDiff = Math.abs(dy);
		//如果滑动的距离大于我们规定的默认位置，并且水平滑动的幅度大于垂直滑动的幅度，则说明可以滑动此View
		if (xDiff > mTouchSlop && xDiff > yDiff && thisSlideAllowed(dx))
		{
			startDrag();
			mLastMotionX = x;
			mLastMotionY = y;
		}
		else if (xDiff > mTouchSlop)
		{
			mIsUnableToDrag = true;
		}
	}

	/**
	 * 这个方法是待扩展用的，主要是想要来过滤某些特殊情况
	 *
	 * @param ev
	 * @return
	 */
	private boolean thisTouchAllowed(MotionEvent ev)
	{

		return true;
	}

	/**
	 * 如果手势是向有滑动返回为 true
	 *
	 * @param dx
	 * @return
	 */
	private boolean thisSlideAllowed(float dx)
	{
		return dx > 0;
	}

	@Override
	public void computeScroll()
	{
		if (!mScroller.isFinished())
		{
			if (mScroller.computeScrollOffset())
			{
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();

				if (oldX != x || oldY != y)
				{
					scrollTo(x, y);
					// pageScrolled(x);
				}

				invalidate();
				return;
			}
		}

		completeScroll();
	}

	private void pageScrolled(int xpos)
	{
		final int widthWithMargin = getWidth();
		//获取当前页面
		int position = Math.abs(xpos) / widthWithMargin;
		//获取当前滑动的距离
		final int offsetPixels = Math.abs(xpos) % widthWithMargin;
		//通过滑动的距离来获取梯度值
		final float offset = (float) offsetPixels / widthWithMargin;
		//这里需要做特殊处理，因为只有一个页面
		position = mIsBeingDragged ? 0 : position;
		onPageScrolled(position, offset, offsetPixels);
	}

	protected void onPageScrolled(int position, float offset, int offsetPixels)
	{
		if (mListener != null)
		{
			mListener.onPageScrolled(position, offset, offsetPixels);
		}
		mInternalPageChangeListener.onPageScrolled(position, offset, offsetPixels);
	}

	private void setCurrentItemInternal(int item, boolean smoothScroll, int velocity)
	{
		mCurItem = item;
		final int destX = getDestScrollX(item);

		if (mListener != null)
		{
			mListener.onPageSelected(mCurItem);
		}
		mInternalPageChangeListener.onPageSelected(mCurItem);
		if (smoothScroll)
		{
			//执行滑动滚动
			smoothScrollTo(destX, 0, velocity);
		}
		else
		{
			//结束滑动滚动
			completeScroll();
			//直接滚动到指定位置
			scrollTo(destX, 0);
		}
	}

	/**
	 * 根据当前页面来获取需要滚动的目的位置
	 *
	 * @param page
	 * @return
	 */
	public int getDestScrollX(int page)
	{
		switch (page)
		{
			case 0:
				return mContent.getLeft();
			case 1:
				return -mContent.getRight();
		}
		return 0;
	}

	/**
	 * 通过偏移位置和加速度来确定需要滚动的页
	 *
	 * @param pageOffset
	 * @param velocity
	 * @param deltaX
	 * @return
	 */
	private int determineTargetPage(float pageOffset, int velocity, int deltaX)
	{
		int targetPage = 0;
		//这里判断是否需要滚动到下一页
		if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinMunVelocity)
		{
			if (velocity > 0 && deltaX > 0)
			{
				targetPage = 1;
			}
			else if (velocity < 0 && deltaX < 0)
			{
				targetPage = 0;
			}
		}
		else
		{
			//根据距离来判断滚动的页码
			targetPage = (int) Math.round(targetPage + pageOffset);
		}
		return targetPage;
	}

	private void completeScroll()
	{
		boolean needPopulate = mScrolling;
		if (needPopulate)
		{
			mScroller.abortAnimation();
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			if (oldX != x || oldY != y)
			{
				// scrollTo(x, y);
			}
		}

		mScrolling = false;
	}

	@Override
	public void scrollTo(int x, int y)
	{
		super.scrollTo(x, y);
		//设置回调事件的值
		pageScrolled(x);
	}

	private void smoothScrollTo(int x, int y, int velocity)
	{
		if (getChildCount() == 0)
		{

			return;
		}
		int sx = getScrollX();
		int sy = getScrollY();
		int dx = x - sx;
		int dy = y - sy;
		if (dx == 0 && dy == 0)
		{
			completeScroll();
			//这里为了解决一个bug，当用手指触摸滑到看不见的时候再用力滑动，如果不做此操作，那么不会回调position = 1
			scrollTo(sx, sy);
			return;
		}

		mScrolling = true;

		final int width = getContentWidth();
		final int halfWidth = width / 2;
		final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
		final float distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio);

		int duration = 0;
		velocity = Math.abs(velocity);
		if (velocity > 0)
		{
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		}
		else
		{
			final float pageDelta = (float) Math.abs(dx) / width;
			duration = (int) ((pageDelta + 1) * 100);
			duration = MAX_SETTLE_DURATION;
		}
		duration = Math.min(duration, MAX_SETTLE_DURATION);
		//开始自定滚动到指定的位置
		mScroller.startScroll(sx, sy, dx, dy, duration);
		invalidate();
	}

	float distanceInfluenceForSnapDuration(float f)
	{
		f -= 0.5f;
		f *= 0.3f * Math.PI / 2.0f;
		return (float) Math.sin(f);
	}

	public void setContent(View v)
	{
		if (mContent != null)
			this.removeView(mContent);
		mContent = v;
		addView(mContent);
	}

	public View getContentView()
	{
		return mContent;
	}

	public void setShouldDraw(boolean shouldDraw)
	{
		mShouldDraw = shouldDraw;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		//获取默认的宽度
		int width = getDefaultSize(0, widthMeasureSpec);
		//获取默认的高度
		int height = getDefaultSize(0, heightMeasureSpec);
		//设置ViewGroup 的宽高
		setMeasuredDimension(width, height);
		//获取子 View 的宽度
		final int contentWidth = getChildMeasureSpec(widthMeasureSpec, 0, width);
		//获取子View 的高度
		final int contentHeight = getChildMeasureSpec(heightMeasureSpec, 0, height);
		//设置子View 的大小
		mContent.measure(contentWidth, contentHeight);
	}

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
		if (mShouldDraw)
		{
			//绘制偏移的背影颜色
			drawBackground(canvas);
			//绘制边缘的阴影
			drawShadow(canvas);
		}
	}

	/**
	 * 绘制背景颜色，随着距离的改变而改变
	 *
	 * @param canvas
	 */
	private void drawBackground(Canvas canvas)
	{
		mShadowPaint.setAlpha((int) ((1 - mRatio) * 180));
		canvas.drawRect(-mPix, 0, 0, getHeight(), mShadowPaint);
	}

	/**
	 * 绘制shadow阴影
	 *
	 * @param canvas
	 */
	private void drawShadow(Canvas canvas)
	{
		//保存画布当前的状态，这个用法在我前面将自定义View 的时候将的很详细
		// canvas.save() ;
		//设置 drawable 的大小范围
		mLeftShadow.setBounds(-mShadowWidth, 0, 0, getHeight());
		//让画布平移一定距离
		// canvas.translate(-mShadowWidth,0);
		//绘制Drawable
		mLeftShadow.draw(canvas);
		//恢复画布的状态
		// canvas.restore();
	}

	private class InternalPageChangeListener implements OnPageChangeListener
	{
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{

			if (mShouldDraw)
			{
				mRatio = positionOffset;
				mPix = positionOffsetPixels;
				invalidate();
			}
		}

		@Override
		public void onPageSelected(int position)
		{

		}
	}

	public interface OnPageChangeListener
	{

		//滑动页面滑动状态，当前页和页面的偏移梯度，页面的偏移位置
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		//当前页面
		public void onPageSelected(int position);
	}
}
