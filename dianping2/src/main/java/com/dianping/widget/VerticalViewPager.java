package com.dianping.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.ScrollView;
import android.widget.Scroller;
import com.dianping.util.Log;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class VerticalViewPager extends ViewGroup
{
  private static final int CLOSE_ENOUGH = 2;
  private static final Comparator<ItemInfo> COMPARATOR;
  private static final boolean DEBUG = false;
  private static final int DEFAULT_GUTTER_SIZE = 16;
  private static final int DEFAULT_OFFSCREEN_PAGES = 1;
  private static final int DRAW_ORDER_DEFAULT = 0;
  private static final int DRAW_ORDER_FORWARD = 1;
  private static final int DRAW_ORDER_REVERSE = 2;
  private static final int INVALID_POINTER = -1;
  private static final int[] LAYOUT_ATTRS = { 16842931 };
  private static final int MAX_SETTLE_DURATION = 800;
  private static final int MIN_DISTANCE_FOR_FLING = 5;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  private static final String TAG = "VerticalViewPager";
  private static final boolean USE_CACHE = false;
  private static final Interpolator sInterpolator;
  private static final ViewPositionComparator sPositionComparator;
  private int mActivePointerId = -1;
  private PagerAdapter mAdapter;
  private OnAdapterChangeListener mAdapterChangeListener;
  private EdgeEffectCompat mBottomEdge;
  private boolean mCalledSuper;
  private int mChildWidthMeasureSpec;
  private int mCloseEnough;
  private int mCurItem;
  private int mDecorChildCount;
  private int mDefaultGutterSize;
  private int mDrawingOrder;
  private ArrayList<View> mDrawingOrderedChildren;
  private final Runnable mEndScrollRunnable = new Runnable()
  {
    public void run()
    {
      VerticalViewPager.this.setScrollState(0);
      VerticalViewPager.this.populate();
    }
  };
  private long mFakeDragBeginTime;
  private boolean mFakeDragging;
  private boolean mFirstLayout = true;
  private float mFirstOffset = -3.402824E+038F;
  private int mFlingDistance;
  private int mGutterSize;
  private boolean mInLayout;
  private float mInitialMotionX;
  private float mInitialMotionY;
  private OnPageChangeListener mInternalPageChangeListener;
  private boolean mIsBeingDragged;
  private boolean mIsUnableToDrag;
  private final ArrayList<ItemInfo> mItems = new ArrayList();
  private float mLastMotionX;
  private float mLastMotionY;
  private float mLastOffset = 3.4028235E+38F;
  private int mLeftPageBounds;
  private Drawable mMarginDrawable;
  private int mMaximumVelocity;
  private int mMinimumVelocity;
  private PagerObserver mObserver;
  private int mOffscreenPageLimit = 1;
  private OnPageChangeListener mOnPageChangeListener;
  private int mPageMargin;
  private PageTransformer mPageTransformer;
  private boolean mPopulatePending;
  private Parcelable mRestoredAdapterState = null;
  private ClassLoader mRestoredClassLoader = null;
  private int mRestoredCurItem = -1;
  private int mRightPageBounds;
  private int mScrollState = 0;
  private Scroller mScroller;
  private boolean mScrollingCacheEnabled;
  private int mSeenPositionMax;
  private int mSeenPositionMin;
  private Method mSetChildrenDrawingOrderEnabled;
  private final ItemInfo mTempItem = new ItemInfo();
  private final Rect mTempRect = new Rect();
  private EdgeEffectCompat mTopEdge;
  private int mTouchSlop;
  private VelocityTracker mVelocityTracker;

  static
  {
    COMPARATOR = new Comparator()
    {
      public int compare(VerticalViewPager.ItemInfo paramItemInfo1, VerticalViewPager.ItemInfo paramItemInfo2)
      {
        return paramItemInfo1.position - paramItemInfo2.position;
      }
    };
    sInterpolator = new Interpolator()
    {
      public float getInterpolation(float paramFloat)
      {
        paramFloat -= 1.0F;
        return paramFloat * paramFloat * paramFloat * paramFloat * paramFloat + 1.0F;
      }
    };
    sPositionComparator = new ViewPositionComparator();
  }

  public VerticalViewPager(Context paramContext)
  {
    super(paramContext);
    initViewPager();
  }

  public VerticalViewPager(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initViewPager();
  }

  private void calculatePageOffsets(ItemInfo paramItemInfo1, int paramInt, ItemInfo paramItemInfo2)
  {
    int m = this.mAdapter.getCount();
    int i = getHeight();
    float f2;
    if (i > 0)
    {
      f2 = this.mPageMargin / i;
      if (paramItemInfo2 == null)
        break label409;
      i = paramItemInfo2.position;
      if (i < paramItemInfo1.position)
      {
        j = 0;
        f1 = paramItemInfo2.offset + paramItemInfo2.heightFactor + f2;
        i += 1;
      }
    }
    else
    {
      while (true)
      {
        if ((i > paramItemInfo1.position) || (j >= this.mItems.size()))
          break label409;
        paramItemInfo2 = (ItemInfo)this.mItems.get(j);
        while (true)
        {
          f3 = f1;
          k = i;
          if (i > paramItemInfo2.position)
          {
            f3 = f1;
            k = i;
            if (j < this.mItems.size() - 1)
            {
              j += 1;
              paramItemInfo2 = (ItemInfo)this.mItems.get(j);
              continue;
              f2 = 0.0F;
              break;
            }
          }
        }
        while (k < paramItemInfo2.position)
        {
          f3 += this.mAdapter.getPageHeight(k) + f2;
          k += 1;
        }
        paramItemInfo2.offset = f3;
        f1 = f3 + (paramItemInfo2.heightFactor + f2);
        i = k + 1;
      }
    }
    if (i > paramItemInfo1.position)
    {
      j = this.mItems.size() - 1;
      f1 = paramItemInfo2.offset;
      i -= 1;
      while ((i >= paramItemInfo1.position) && (j >= 0))
      {
        for (paramItemInfo2 = (ItemInfo)this.mItems.get(j); ; paramItemInfo2 = (ItemInfo)this.mItems.get(j))
        {
          f3 = f1;
          k = i;
          if (i >= paramItemInfo2.position)
            break;
          f3 = f1;
          k = i;
          if (j <= 0)
            break;
          j -= 1;
        }
        while (k > paramItemInfo2.position)
        {
          f3 -= this.mAdapter.getPageHeight(k) + f2;
          k -= 1;
        }
        f1 = f3 - (paramItemInfo2.heightFactor + f2);
        paramItemInfo2.offset = f1;
        i = k - 1;
      }
    }
    label409: int k = this.mItems.size();
    float f3 = paramItemInfo1.offset;
    i = paramItemInfo1.position - 1;
    if (paramItemInfo1.position == 0)
    {
      f1 = paramItemInfo1.offset;
      this.mFirstOffset = f1;
      if (paramItemInfo1.position != m - 1)
        break label550;
      f1 = paramItemInfo1.offset + paramItemInfo1.heightFactor - 1.0F;
      label475: this.mLastOffset = f1;
      j = paramInt - 1;
      f1 = f3;
    }
    while (true)
    {
      if (j < 0)
        break label603;
      paramItemInfo2 = (ItemInfo)this.mItems.get(j);
      while (true)
        if (i > paramItemInfo2.position)
        {
          f1 -= this.mAdapter.getPageHeight(i) + f2;
          i -= 1;
          continue;
          f1 = -3.402824E+038F;
          break;
          label550: f1 = 3.4028235E+38F;
          break label475;
        }
      f1 -= paramItemInfo2.heightFactor + f2;
      paramItemInfo2.offset = f1;
      if (paramItemInfo2.position == 0)
        this.mFirstOffset = f1;
      j -= 1;
      i -= 1;
    }
    label603: float f1 = paramItemInfo1.offset + paramItemInfo1.heightFactor + f2;
    i = paramItemInfo1.position + 1;
    int j = paramInt + 1;
    paramInt = i;
    i = j;
    while (i < k)
    {
      paramItemInfo1 = (ItemInfo)this.mItems.get(i);
      while (paramInt < paramItemInfo1.position)
      {
        f1 += this.mAdapter.getPageHeight(paramInt) + f2;
        paramInt += 1;
      }
      if (paramItemInfo1.position == m - 1)
        this.mLastOffset = (paramItemInfo1.heightFactor + f1 - 1.0F);
      paramItemInfo1.offset = f1;
      f1 += paramItemInfo1.heightFactor + f2;
      i += 1;
      paramInt += 1;
    }
  }

  private void completeScroll(boolean paramBoolean)
  {
    if (this.mScrollState == 2);
    int j;
    for (int i = 1; ; i = 0)
    {
      if (i != 0)
      {
        setScrollingCacheEnabled(false);
        this.mScroller.abortAnimation();
        j = getScrollX();
        k = getScrollY();
        int m = this.mScroller.getCurrX();
        int n = this.mScroller.getCurrY();
        if ((j != m) || (k != n))
          scrollTo(m, n);
      }
      this.mPopulatePending = false;
      int k = 0;
      j = i;
      i = k;
      while (i < this.mItems.size())
      {
        ItemInfo localItemInfo = (ItemInfo)this.mItems.get(i);
        if (localItemInfo.scrolling)
        {
          j = 1;
          localItemInfo.scrolling = false;
        }
        i += 1;
      }
    }
    if (j != 0)
    {
      if (paramBoolean)
        ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
    }
    else
      return;
    this.mEndScrollRunnable.run();
  }

  private int determineTargetPage(int paramInt1, float paramFloat, int paramInt2, int paramInt3)
  {
    if ((Math.abs(paramInt3) > this.mFlingDistance) && (Math.abs(paramInt2) > this.mMinimumVelocity))
      if (paramInt2 <= 0);
    while (true)
    {
      paramInt2 = paramInt1;
      if (this.mItems.size() > 0)
      {
        ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(0);
        ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
        paramInt2 = Math.max(localItemInfo1.position, Math.min(paramInt1, localItemInfo2.position));
      }
      return paramInt2;
      paramInt1 += 1;
      continue;
      if ((this.mSeenPositionMin >= 0) && (this.mSeenPositionMin < paramInt1) && (paramFloat < 0.5F))
      {
        paramInt1 += 1;
        continue;
      }
      if ((this.mSeenPositionMax >= 0) && (this.mSeenPositionMax > paramInt1 + 1) && (paramFloat >= 0.5F))
      {
        paramInt1 -= 1;
        continue;
      }
      paramInt1 = (int)(paramInt1 + paramFloat + 0.5F);
    }
  }

  private void enableLayers(boolean paramBoolean)
  {
    int k = getChildCount();
    int i = 0;
    if (i < k)
    {
      if (paramBoolean);
      for (int j = 2; ; j = 0)
      {
        ViewCompat.setLayerType(getChildAt(i), j, null);
        i += 1;
        break;
      }
    }
  }

  private void endDrag()
  {
    this.mIsBeingDragged = false;
    this.mIsUnableToDrag = false;
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }

  private Rect getChildRectInPagerCoordinates(Rect paramRect, View paramView)
  {
    Rect localRect = paramRect;
    if (paramRect == null)
      localRect = new Rect();
    if (paramView == null)
      localRect.set(0, 0, 0, 0);
    while (true)
    {
      return localRect;
      localRect.left = paramView.getLeft();
      localRect.right = paramView.getRight();
      localRect.top = paramView.getTop();
      localRect.bottom = paramView.getBottom();
      for (paramRect = paramView.getParent(); ((paramRect instanceof ViewGroup)) && (paramRect != this); paramRect = paramRect.getParent())
      {
        paramRect = (ViewGroup)paramRect;
        localRect.left += paramRect.getLeft();
        localRect.right += paramRect.getRight();
        localRect.top += paramRect.getTop();
        localRect.bottom += paramRect.getBottom();
      }
    }
  }

  private ItemInfo infoForCurrentScrollPosition()
  {
    float f2 = 0.0F;
    int i = getHeight();
    float f1;
    int k;
    float f4;
    float f3;
    int j;
    Object localObject2;
    if (i > 0)
    {
      f1 = getScrollY() / i;
      if (i > 0)
        f2 = this.mPageMargin / i;
      k = -1;
      f4 = 0.0F;
      f3 = 0.0F;
      j = 1;
      localObject2 = null;
      i = 0;
    }
    while (true)
    {
      Object localObject3 = localObject2;
      int m;
      Object localObject1;
      if (i < this.mItems.size())
      {
        localObject3 = (ItemInfo)this.mItems.get(i);
        m = i;
        localObject1 = localObject3;
        if (j == 0)
        {
          m = i;
          localObject1 = localObject3;
          if (((ItemInfo)localObject3).position != k + 1)
          {
            localObject1 = this.mTempItem;
            ((ItemInfo)localObject1).offset = (f4 + f3 + f2);
            ((ItemInfo)localObject1).position = (k + 1);
            ((ItemInfo)localObject1).widthFactor = this.mAdapter.getPageWidth(((ItemInfo)localObject1).position);
            m = i - 1;
          }
        }
        f4 = ((ItemInfo)localObject1).offset;
        f3 = ((ItemInfo)localObject1).heightFactor;
        if (j == 0)
        {
          localObject3 = localObject2;
          if (f1 < f4);
        }
        else
        {
          if ((f1 >= f3 + f4 + f2) && (m != this.mItems.size() - 1))
            break label233;
          localObject3 = localObject1;
        }
      }
      return localObject3;
      f1 = 0.0F;
      break;
      label233: j = 0;
      k = ((ItemInfo)localObject1).position;
      f3 = ((ItemInfo)localObject1).heightFactor;
      i = m + 1;
      localObject2 = localObject1;
    }
  }

  private boolean isGutterDrag(float paramFloat1, float paramFloat2)
  {
    return ((paramFloat1 < this.mGutterSize) && (paramFloat2 > 0.0F)) || ((paramFloat1 > getHeight() - this.mGutterSize) && (paramFloat2 < 0.0F));
  }

  private void onSecondaryPointerUp(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (MotionEventCompat.getPointerId(paramMotionEvent, i) == this.mActivePointerId)
      if (i != 0)
        break label56;
    label56: for (i = 1; ; i = 0)
    {
      this.mLastMotionY = MotionEventCompat.getY(paramMotionEvent, i);
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, i);
      if (this.mVelocityTracker != null)
        this.mVelocityTracker.clear();
      return;
    }
  }

  private boolean pageScrolled(int paramInt)
  {
    int m = 0;
    if (this.mItems.size() == 0)
    {
      this.mCalledSuper = false;
      onPageScrolled(0, 0.0F, 0);
      if (!this.mCalledSuper)
        throw new IllegalStateException("onPageScrolled did not call superclass implementation");
    }
    else
    {
      ItemInfo localItemInfo = infoForCurrentScrollPosition();
      int j = getHeight();
      int k = this.mPageMargin;
      float f = this.mPageMargin / j;
      int i = localItemInfo.position;
      f = (paramInt / j - localItemInfo.offset) / (localItemInfo.heightFactor + f);
      paramInt = (int)((j + k) * f);
      this.mCalledSuper = false;
      onPageScrolled(i, f, paramInt);
      if (!this.mCalledSuper)
        throw new IllegalStateException("onPageScrolled did not call superclass implementation");
      m = 1;
    }
    return m;
  }

  private boolean performDrag(float paramFloat)
  {
    boolean bool3 = false;
    boolean bool2 = false;
    boolean bool1 = false;
    float f1 = this.mLastMotionY;
    this.mLastMotionY = paramFloat;
    float f2 = getScrollY() + (f1 - paramFloat);
    int k = getHeight();
    paramFloat = k * this.mFirstOffset;
    f1 = k * this.mLastOffset;
    int i = 1;
    int j = 1;
    ItemInfo localItemInfo1 = (ItemInfo)this.mItems.get(0);
    ItemInfo localItemInfo2 = (ItemInfo)this.mItems.get(this.mItems.size() - 1);
    if (localItemInfo1.position != 0)
    {
      i = 0;
      paramFloat = localItemInfo1.offset * k;
    }
    if (localItemInfo2.position != this.mAdapter.getCount() - 1)
    {
      j = 0;
      f1 = localItemInfo2.offset * k;
    }
    if (f2 < paramFloat)
      if (i != 0)
        bool1 = this.mTopEdge.onPull(Math.abs(paramFloat - f2) / k);
    while (true)
    {
      this.mLastMotionY += paramFloat - (int)paramFloat;
      scrollTo(getScrollX(), (int)paramFloat);
      pageScrolled((int)paramFloat);
      return bool1;
      bool1 = bool3;
      paramFloat = f2;
      if (f2 <= f1)
        continue;
      bool1 = bool2;
      if (j != 0)
        bool1 = this.mBottomEdge.onPull(Math.abs(f2 - f1) / k);
      paramFloat = f1;
    }
  }

  private void recomputeScrollPosition(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float f;
    if ((paramInt2 > 0) && (!this.mItems.isEmpty()))
    {
      f = getScrollY() / (paramInt2 + paramInt4);
      paramInt2 = (int)((paramInt1 + paramInt3) * f);
      scrollTo(getScrollX(), paramInt2);
      if (!this.mScroller.isFinished())
      {
        paramInt3 = this.mScroller.getDuration();
        paramInt4 = this.mScroller.timePassed();
        localItemInfo = infoForPosition(this.mCurItem);
        this.mScroller.startScroll(0, paramInt2, 0, (int)(localItemInfo.offset * paramInt1), paramInt3 - paramInt4);
      }
      return;
    }
    ItemInfo localItemInfo = infoForPosition(this.mCurItem);
    if (localItemInfo != null)
      f = Math.min(localItemInfo.offset, this.mLastOffset);
    while (true)
    {
      paramInt1 = (int)(paramInt1 * f);
      if (paramInt1 == getScrollY())
        break;
      completeScroll(false);
      scrollTo(getScrollX(), paramInt1);
      return;
      f = 0.0F;
    }
  }

  private void removeNonDecorViews()
  {
    int j;
    for (int i = 0; i < getChildCount(); i = j + 1)
    {
      j = i;
      if (((LayoutParams)getChildAt(i).getLayoutParams()).isDecor)
        continue;
      removeViewAt(i);
      j = i - 1;
    }
  }

  private void scrollToItem(int paramInt1, boolean paramBoolean1, int paramInt2, boolean paramBoolean2)
  {
    ItemInfo localItemInfo = infoForPosition(paramInt1);
    int i = 0;
    if (localItemInfo != null)
      i = (int)(getHeight() * Math.max(this.mFirstOffset, Math.min(localItemInfo.offset, this.mLastOffset)));
    if (paramBoolean1)
    {
      smoothScrollTo(0, i, paramInt2);
      if ((paramBoolean2) && (this.mOnPageChangeListener != null))
        this.mOnPageChangeListener.onPageSelected(paramInt1);
      if ((paramBoolean2) && (this.mInternalPageChangeListener != null))
        this.mInternalPageChangeListener.onPageSelected(paramInt1);
      return;
    }
    if ((paramBoolean2) && (this.mOnPageChangeListener != null))
      this.mOnPageChangeListener.onPageSelected(paramInt1);
    if ((paramBoolean2) && (this.mInternalPageChangeListener != null))
      this.mInternalPageChangeListener.onPageSelected(paramInt1);
    completeScroll(false);
    scrollTo(0, i);
  }

  private void setScrollState(int paramInt)
  {
    boolean bool = true;
    if (this.mScrollState == paramInt)
      return;
    this.mScrollState = paramInt;
    if (paramInt == 1)
    {
      this.mSeenPositionMax = -1;
      this.mSeenPositionMin = -1;
    }
    if (this.mPageTransformer != null)
      if (paramInt == 0)
        break label65;
    while (true)
    {
      enableLayers(bool);
      if (this.mOnPageChangeListener == null)
        break;
      this.mOnPageChangeListener.onPageScrollStateChanged(paramInt);
      return;
      label65: bool = false;
    }
  }

  private void setScrollingCacheEnabled(boolean paramBoolean)
  {
    if (this.mScrollingCacheEnabled != paramBoolean)
      this.mScrollingCacheEnabled = paramBoolean;
  }

  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if (paramArrayList == null);
    int j;
    int k;
    do
    {
      return;
      j = paramArrayList.size();
      k = getDescendantFocusability();
      if (k == 393216)
        continue;
      int i = 0;
      while (i < getChildCount())
      {
        View localView = getChildAt(i);
        if (localView.getVisibility() == 0)
        {
          ItemInfo localItemInfo = infoForChild(localView);
          if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem))
            localView.addFocusables(paramArrayList, paramInt1, paramInt2);
        }
        i += 1;
      }
    }
    while (((k == 262144) && (j != paramArrayList.size())) || (!isFocusable()) || (((paramInt2 & 0x1) == 1) && (isInTouchMode()) && (!isFocusableInTouchMode())));
    paramArrayList.add(this);
  }

  ItemInfo addNewItem(int paramInt1, int paramInt2)
  {
    ItemInfo localItemInfo = new ItemInfo();
    localItemInfo.position = paramInt1;
    localItemInfo.object = this.mAdapter.instantiateItem(this, paramInt1);
    localItemInfo.widthFactor = this.mAdapter.getPageWidth(paramInt1);
    localItemInfo.heightFactor = this.mAdapter.getPageHeight(paramInt1);
    if ((paramInt2 < 0) || (paramInt2 >= this.mItems.size()))
    {
      this.mItems.add(localItemInfo);
      return localItemInfo;
    }
    this.mItems.add(paramInt2, localItemInfo);
    return localItemInfo;
  }

  public void addTouchables(ArrayList<View> paramArrayList)
  {
    int i = 0;
    while (i < getChildCount())
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem))
          localView.addTouchables(paramArrayList);
      }
      i += 1;
    }
  }

  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    ViewGroup.LayoutParams localLayoutParams = paramLayoutParams;
    if (!checkLayoutParams(paramLayoutParams))
      localLayoutParams = generateLayoutParams(paramLayoutParams);
    paramLayoutParams = (LayoutParams)localLayoutParams;
    paramLayoutParams.isDecor |= paramView instanceof Decor;
    if (this.mInLayout)
    {
      if (paramLayoutParams.isDecor)
        throw new IllegalStateException("Cannot add pager decor view during layout");
      paramLayoutParams.needsMeasure = true;
      addViewInLayout(paramView, paramInt, localLayoutParams);
      return;
    }
    super.addView(paramView, paramInt, localLayoutParams);
  }

  public boolean arrowScroll(int paramInt)
  {
    View localView2 = findFocus();
    View localView1 = localView2;
    if (localView2 == this)
      localView1 = null;
    boolean bool = false;
    localView2 = FocusFinder.getInstance().findNextFocus(this, localView1, paramInt);
    int i;
    int j;
    if ((localView2 != null) && (localView2 != localView1))
      if (paramInt == 33)
      {
        i = getChildRectInPagerCoordinates(this.mTempRect, localView2).top;
        j = getChildRectInPagerCoordinates(this.mTempRect, localView1).top;
        if ((localView1 != null) && (i >= j))
          bool = pageUp();
      }
    while (true)
    {
      if (bool)
        playSoundEffect(SoundEffectConstants.getContantForFocusDirection(paramInt));
      return bool;
      bool = localView2.requestFocus();
      continue;
      if (paramInt != 66)
        continue;
      i = getChildRectInPagerCoordinates(this.mTempRect, localView2).bottom;
      j = getChildRectInPagerCoordinates(this.mTempRect, localView1).bottom;
      if ((localView1 != null) && (i <= j))
      {
        bool = pageDown();
        continue;
      }
      bool = localView2.requestFocus();
      continue;
      if ((paramInt == 17) || (paramInt == 1))
      {
        bool = pageUp();
        continue;
      }
      if ((paramInt != 66) && (paramInt != 2))
        continue;
      bool = pageDown();
    }
  }

  public boolean beginFakeDrag()
  {
    if (this.mIsBeingDragged)
      return false;
    this.mFakeDragging = true;
    setScrollState(1);
    this.mLastMotionY = 0.0F;
    this.mInitialMotionY = 0.0F;
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain();
    while (true)
    {
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 0, 0.0F, 0.0F, 0);
      this.mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      this.mFakeDragBeginTime = l;
      return true;
      this.mVelocityTracker.clear();
    }
  }

  protected boolean canScroll(View paramView, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramView instanceof ViewGroup))
    {
      ViewGroup localViewGroup = (ViewGroup)paramView;
      int j = paramView.getScrollX();
      int k = paramView.getScrollY();
      int i = localViewGroup.getChildCount() - 1;
      while (i >= 0)
      {
        View localView = localViewGroup.getChildAt(i);
        if ((paramInt2 + j >= localView.getLeft()) && (paramInt2 + j < localView.getRight()) && (paramInt3 + k >= localView.getTop()) && (paramInt3 + k < localView.getBottom()) && (canScroll(localView, true, paramInt1, paramInt2 + j - localView.getLeft(), paramInt3 + k - localView.getTop())))
          return true;
        i -= 1;
      }
    }
    if (Build.VERSION.SDK_INT >= 14)
      return (paramBoolean) && (ViewCompat.canScrollVertically(paramView, -paramInt1));
    if (paramInt1 >= 0)
    {
      if ((paramView instanceof ScrollView))
      {
        Log.d("Alex", "向下,v.getScrollY():" + paramView.getScrollY());
        return paramView.getScrollY() > 0;
      }
      return false;
    }
    if ((paramView instanceof ScrollView))
    {
      paramView = (ScrollView)paramView;
      return paramView.getScrollY() + 1 + paramView.getHeight() < paramView.getChildAt(0).getMeasuredHeight();
    }
    return false;
  }

  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return ((paramLayoutParams instanceof LayoutParams)) && (super.checkLayoutParams(paramLayoutParams));
  }

  public void computeScroll()
  {
    if ((!this.mScroller.isFinished()) && (this.mScroller.computeScrollOffset()))
    {
      int i = getScrollX();
      int j = getScrollY();
      int k = this.mScroller.getCurrX();
      int m = this.mScroller.getCurrY();
      if ((i != k) || (j != m))
      {
        scrollTo(k, m);
        if (!pageScrolled(k))
        {
          this.mScroller.abortAnimation();
          scrollTo(0, m);
        }
      }
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    }
    completeScroll(true);
  }

  void dataSetChanged()
  {
    int i;
    int j;
    int k;
    int m;
    label48: Object localObject;
    int i3;
    int n;
    int i1;
    int i2;
    if ((this.mItems.size() < this.mOffscreenPageLimit * 2 + 1) && (this.mItems.size() < this.mAdapter.getCount()))
    {
      i = 1;
      j = this.mCurItem;
      k = 0;
      m = 0;
      if (m >= this.mItems.size())
        break label298;
      localObject = (ItemInfo)this.mItems.get(m);
      i3 = this.mAdapter.getItemPosition(((ItemInfo)localObject).object);
      if (i3 != -1)
        break label124;
      n = j;
      i1 = k;
      i2 = m;
    }
    while (true)
    {
      m = i2 + 1;
      k = i1;
      j = n;
      break label48;
      i = 0;
      break;
      label124: if (i3 == -2)
      {
        this.mItems.remove(m);
        i3 = m - 1;
        m = k;
        if (k == 0)
        {
          this.mAdapter.startUpdate(this);
          m = 1;
        }
        this.mAdapter.destroyItem(this, ((ItemInfo)localObject).position, ((ItemInfo)localObject).object);
        i = 1;
        i2 = i3;
        i1 = m;
        n = j;
        if (this.mCurItem != ((ItemInfo)localObject).position)
          continue;
        n = Math.max(0, Math.min(this.mCurItem, this.mAdapter.getCount() - 1));
        i = 1;
        i2 = i3;
        i1 = m;
        continue;
      }
      i2 = m;
      i1 = k;
      n = j;
      if (((ItemInfo)localObject).position == i3)
        continue;
      if (((ItemInfo)localObject).position == this.mCurItem)
        j = i3;
      ((ItemInfo)localObject).position = i3;
      i = 1;
      i2 = m;
      i1 = k;
      n = j;
    }
    label298: if (k != 0)
      this.mAdapter.finishUpdate(this);
    Collections.sort(this.mItems, COMPARATOR);
    if (i != 0)
    {
      k = getChildCount();
      i = 0;
      while (i < k)
      {
        localObject = (LayoutParams)getChildAt(i).getLayoutParams();
        if (!((LayoutParams)localObject).isDecor)
        {
          ((LayoutParams)localObject).widthFactor = 0.0F;
          ((LayoutParams)localObject).heightFactor = 0.0F;
        }
        i += 1;
      }
      setCurrentItemInternal(j, false, true);
      requestLayout();
    }
  }

  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return (super.dispatchKeyEvent(paramKeyEvent)) || (executeKeyEvent(paramKeyEvent));
  }

  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem) && (localView.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent)))
          return true;
      }
      i += 1;
    }
    return false;
  }

  float distanceInfluenceForSnapDuration(float paramFloat)
  {
    return (float)Math.sin((float)((paramFloat - 0.5F) * 0.47123891676382D));
  }

  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    int k = 0;
    int i = 0;
    int m = ViewCompat.getOverScrollMode(this);
    boolean bool;
    if ((m == 0) || ((m == 1) && (this.mAdapter != null) && (this.mAdapter.getCount() > 1)))
    {
      int n;
      int j;
      if (!this.mTopEdge.isFinished())
      {
        i = getHeight();
        k = getWidth();
        m = getPaddingLeft();
        n = getPaddingRight();
        this.mTopEdge.setSize(k - m - n, i);
        j = false | this.mTopEdge.draw(paramCanvas);
      }
      k = j;
      if (!this.mBottomEdge.isFinished())
      {
        k = getHeight();
        m = getWidth();
        n = getPaddingLeft();
        int i1 = getPaddingRight();
        this.mBottomEdge.setSize(m - n - i1, k);
        bool = j | this.mBottomEdge.draw(paramCanvas);
      }
    }
    while (true)
    {
      if (bool)
        ViewCompat.postInvalidateOnAnimation(this);
      return;
      this.mTopEdge.finish();
      this.mBottomEdge.finish();
    }
  }

  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mMarginDrawable;
    if ((localDrawable != null) && (localDrawable.isStateful()))
      localDrawable.setState(getDrawableState());
  }

  public boolean executeKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() == 0)
      switch (paramKeyEvent.getKeyCode())
      {
      default:
      case 21:
      case 22:
      case 61:
      }
    do
    {
      do
      {
        return false;
        return arrowScroll(17);
        return arrowScroll(66);
      }
      while (Build.VERSION.SDK_INT < 11);
      if (KeyEventCompat.hasNoModifiers(paramKeyEvent))
        return arrowScroll(2);
    }
    while (!KeyEventCompat.hasModifiers(paramKeyEvent, 1));
    return arrowScroll(1);
  }

  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams();
  }

  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }

  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return generateDefaultLayoutParams();
  }

  public PagerAdapter getAdapter()
  {
    return this.mAdapter;
  }

  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mDrawingOrder == 2);
    for (paramInt1 = paramInt1 - 1 - paramInt2; ; paramInt1 = paramInt2)
      return ((LayoutParams)((View)this.mDrawingOrderedChildren.get(paramInt1)).getLayoutParams()).childIndex;
  }

  public int getCurrentItem()
  {
    return this.mCurItem;
  }

  ItemInfo infoForAnyChild(View paramView)
  {
    while (true)
    {
      ViewParent localViewParent = paramView.getParent();
      if (localViewParent == this)
        break;
      if ((localViewParent == null) || (!(localViewParent instanceof View)))
        return null;
      paramView = (View)localViewParent;
    }
    return infoForChild(paramView);
  }

  ItemInfo infoForChild(View paramView)
  {
    int i = 0;
    while (i < this.mItems.size())
    {
      ItemInfo localItemInfo = (ItemInfo)this.mItems.get(i);
      if (this.mAdapter.isViewFromObject(paramView, localItemInfo.object))
        return localItemInfo;
      i += 1;
    }
    return null;
  }

  ItemInfo infoForPosition(int paramInt)
  {
    int i = 0;
    while (i < this.mItems.size())
    {
      ItemInfo localItemInfo = (ItemInfo)this.mItems.get(i);
      if (localItemInfo.position == paramInt)
        return localItemInfo;
      i += 1;
    }
    return null;
  }

  void initViewPager()
  {
    setWillNotDraw(false);
    setDescendantFocusability(262144);
    setFocusable(true);
    Context localContext = getContext();
    this.mScroller = new Scroller(localContext, sInterpolator);
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(localContext);
    this.mTouchSlop = 10;
    this.mMinimumVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
    this.mTopEdge = new EdgeEffectCompat(localContext);
    this.mBottomEdge = new EdgeEffectCompat(localContext);
    float f = localContext.getResources().getDisplayMetrics().density;
    this.mFlingDistance = (int)(5.0F * f);
    this.mCloseEnough = (int)(2.0F * f);
    this.mDefaultGutterSize = (int)(16.0F * f);
    ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
    if (ViewCompat.getImportantForAccessibility(this) == 0)
      ViewCompat.setImportantForAccessibility(this, 1);
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.mFirstLayout = true;
  }

  protected void onDetachedFromWindow()
  {
    removeCallbacks(this.mEndScrollRunnable);
    super.onDetachedFromWindow();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int k;
    int m;
    float f3;
    int j;
    Object localObject;
    float f1;
    int n;
    int i;
    int i1;
    if ((this.mPageMargin > 0) && (this.mMarginDrawable != null) && (this.mItems.size() > 0) && (this.mAdapter != null))
    {
      k = getScrollY();
      m = getHeight();
      f3 = this.mPageMargin / m;
      j = 0;
      localObject = (ItemInfo)this.mItems.get(0);
      f1 = ((ItemInfo)localObject).offset;
      n = this.mItems.size();
      i = ((ItemInfo)localObject).position;
      i1 = ((ItemInfo)this.mItems.get(n - 1)).position;
    }
    while (true)
    {
      float f2;
      if (i < i1)
      {
        while ((i > ((ItemInfo)localObject).position) && (j < n))
        {
          localObject = this.mItems;
          j += 1;
          localObject = (ItemInfo)((ArrayList)localObject).get(j);
        }
        if (i != ((ItemInfo)localObject).position)
          break label271;
        f2 = (((ItemInfo)localObject).offset + ((ItemInfo)localObject).heightFactor) * m;
        f1 = ((ItemInfo)localObject).offset + ((ItemInfo)localObject).heightFactor + f3;
      }
      while (true)
      {
        if (this.mPageMargin + f2 > k)
        {
          this.mMarginDrawable.setBounds(this.mLeftPageBounds, (int)f2, this.mRightPageBounds, (int)(this.mPageMargin + f2 + 0.5F));
          this.mMarginDrawable.draw(paramCanvas);
        }
        if (f2 <= k + m)
          break;
        return;
        label271: float f4 = this.mAdapter.getPageHeight(i);
        f2 = (f1 + f4) * m;
        f1 += f4 + f3;
      }
      i += 1;
    }
  }

  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction() & 0xFF;
    if ((i == 3) || (i == 1))
    {
      this.mIsBeingDragged = false;
      this.mIsUnableToDrag = false;
      this.mActivePointerId = -1;
      if (this.mVelocityTracker != null)
      {
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
      }
      return false;
    }
    if (i != 0)
    {
      if (this.mIsBeingDragged)
        return true;
      if (this.mIsUnableToDrag)
        return false;
    }
    switch (i)
    {
    default:
    case 2:
    case 0:
    case 6:
    }
    while (true)
    {
      if (this.mVelocityTracker == null)
        this.mVelocityTracker = VelocityTracker.obtain();
      this.mVelocityTracker.addMovement(paramMotionEvent);
      return this.mIsBeingDragged;
      i = this.mActivePointerId;
      if (i == -1)
        continue;
      i = MotionEventCompat.findPointerIndex(paramMotionEvent, i);
      float f1 = MotionEventCompat.getX(paramMotionEvent, i);
      float f3 = Math.abs(f1 - this.mLastMotionX);
      float f2 = MotionEventCompat.getY(paramMotionEvent, i);
      float f4 = f2 - this.mLastMotionY;
      float f5 = Math.abs(f2 - this.mLastMotionY);
      if ((f4 != 0.0F) && (!isGutterDrag(this.mLastMotionY, f4)) && (canScroll(this, false, (int)f4, (int)f1, (int)f2)))
      {
        this.mLastMotionY = f2;
        this.mInitialMotionY = f2;
        this.mLastMotionX = f1;
        this.mIsUnableToDrag = true;
        return false;
      }
      if ((f5 > this.mTouchSlop) && (f5 > f3))
      {
        this.mIsBeingDragged = true;
        setScrollState(1);
        if (f4 > 0.0F)
        {
          f1 = this.mInitialMotionY + this.mTouchSlop;
          label312: this.mLastMotionY = f1;
          setScrollingCacheEnabled(true);
        }
      }
      while ((this.mIsBeingDragged) && (performDrag(f2)))
      {
        ViewCompat.postInvalidateOnAnimation(this);
        break;
        f1 = this.mInitialMotionY - this.mTouchSlop;
        break label312;
        if (f3 <= this.mTouchSlop)
          continue;
        this.mIsUnableToDrag = true;
      }
      f1 = paramMotionEvent.getY();
      this.mInitialMotionY = f1;
      this.mLastMotionY = f1;
      this.mLastMotionX = paramMotionEvent.getX();
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      this.mIsUnableToDrag = false;
      this.mScroller.computeScrollOffset();
      if ((this.mScrollState == 2) && (Math.abs(this.mScroller.getFinalY() - this.mScroller.getCurrY()) > this.mCloseEnough))
      {
        this.mScroller.abortAnimation();
        this.mPopulatePending = false;
        populate();
        this.mIsBeingDragged = true;
        setScrollState(1);
        continue;
      }
      completeScroll(false);
      this.mIsBeingDragged = false;
      continue;
      onSecondaryPointerUp(paramMotionEvent);
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mInLayout = true;
    populate();
    this.mInLayout = false;
    int i3 = getChildCount();
    int i4 = paramInt3 - paramInt1;
    int i5 = paramInt4 - paramInt2;
    paramInt2 = getPaddingLeft();
    paramInt1 = getPaddingTop();
    int i = getPaddingRight();
    paramInt4 = getPaddingBottom();
    int i6 = getScrollY();
    int k = 0;
    int m = 0;
    View localView;
    LayoutParams localLayoutParams;
    if (m < i3)
    {
      localView = getChildAt(m);
      int i2 = k;
      int i1 = paramInt4;
      int j = paramInt2;
      int n = i;
      paramInt3 = paramInt1;
      if (localView.getVisibility() != 8)
      {
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        i2 = k;
        i1 = paramInt4;
        j = paramInt2;
        n = i;
        paramInt3 = paramInt1;
        if (localLayoutParams.isDecor)
        {
          paramInt3 = localLayoutParams.gravity;
          n = localLayoutParams.gravity;
          switch (paramInt3 & 0x7)
          {
          case 2:
          case 4:
          default:
            paramInt3 = paramInt2;
            j = paramInt2;
            label206: switch (n & 0x70)
            {
            default:
              paramInt2 = paramInt1;
            case 48:
            case 16:
            case 80:
            }
          case 3:
          case 1:
          case 5:
          }
        }
      }
      while (true)
      {
        paramInt2 += i6;
        localView.layout(paramInt3, paramInt2, localView.getMeasuredWidth() + paramInt3, localView.getMeasuredHeight() + paramInt2);
        i2 = k + 1;
        paramInt3 = paramInt1;
        n = i;
        i1 = paramInt4;
        m += 1;
        k = i2;
        paramInt4 = i1;
        paramInt2 = j;
        i = n;
        paramInt1 = paramInt3;
        break;
        paramInt3 = paramInt2;
        j = paramInt2 + localView.getMeasuredWidth();
        break label206;
        paramInt3 = Math.max((i4 - localView.getMeasuredWidth()) / 2, paramInt2);
        j = paramInt2;
        break label206;
        paramInt3 = i4 - i - localView.getMeasuredWidth();
        i += localView.getMeasuredWidth();
        j = paramInt2;
        break label206;
        paramInt2 = paramInt1;
        paramInt1 += localView.getMeasuredHeight();
        continue;
        paramInt2 = Math.max((i5 - localView.getMeasuredHeight()) / 2, paramInt1);
        continue;
        paramInt2 = i5 - paramInt4 - localView.getMeasuredHeight();
        paramInt4 += localView.getMeasuredHeight();
      }
    }
    paramInt1 = 0;
    while (paramInt1 < i3)
    {
      localView = getChildAt(paramInt1);
      if (localView.getVisibility() != 8)
      {
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (!localLayoutParams.isDecor)
        {
          ItemInfo localItemInfo = infoForChild(localView);
          if (localItemInfo != null)
          {
            paramInt3 = (int)(i5 * localItemInfo.offset);
            if (localLayoutParams.needsMeasure)
            {
              localLayoutParams.needsMeasure = false;
              localView.measure(View.MeasureSpec.makeMeasureSpec((int)((i4 - paramInt2 - i) * localLayoutParams.widthFactor), 1073741824), View.MeasureSpec.makeMeasureSpec(i5 - paramInt3 - paramInt4, 1073741824));
            }
            localView.layout(paramInt2, paramInt3, localView.getMeasuredWidth() + paramInt2, localView.getMeasuredHeight() + paramInt3);
          }
        }
      }
      paramInt1 += 1;
    }
    this.mLeftPageBounds = paramInt2;
    this.mRightPageBounds = (i4 - i);
    this.mDecorChildCount = k;
    this.mFirstLayout = false;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(getDefaultSize(0, paramInt1), getDefaultSize(0, paramInt2));
    paramInt2 = getMeasuredWidth();
    paramInt1 = getMeasuredHeight();
    this.mGutterSize = Math.min(paramInt1 / 10, this.mDefaultGutterSize);
    paramInt2 = paramInt2 - getPaddingLeft() - getPaddingRight();
    paramInt1 = paramInt1 - getPaddingTop() - getPaddingBottom();
    int i5 = getChildCount();
    int k = 0;
    View localView;
    int j;
    LayoutParams localLayoutParams;
    if (k < i5)
    {
      localView = getChildAt(k);
      i = paramInt1;
      j = paramInt2;
      int m;
      int i1;
      label182: int n;
      if (localView.getVisibility() != 8)
      {
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        i = paramInt1;
        j = paramInt2;
        if (localLayoutParams != null)
        {
          i = paramInt1;
          j = paramInt2;
          if (localLayoutParams.isDecor)
          {
            j = localLayoutParams.gravity & 0x7;
            m = localLayoutParams.gravity & 0x70;
            i1 = -2147483648;
            i = -2147483648;
            if ((m != 48) && (m != 80))
              break label356;
            m = 1;
            if ((j != 3) && (j != 5))
              break label362;
            n = 1;
            label197: if (m == 0)
              break label368;
            j = 1073741824;
            label207: int i3 = paramInt2;
            i1 = paramInt1;
            int i2 = i3;
            int i4;
            if (localLayoutParams.width != -2)
            {
              i4 = 1073741824;
              j = i4;
              i2 = i3;
              if (localLayoutParams.width != -1)
              {
                i2 = localLayoutParams.width;
                j = i4;
              }
            }
            i3 = i1;
            if (localLayoutParams.height != -2)
            {
              i4 = 1073741824;
              i = i4;
              i3 = i1;
              if (localLayoutParams.height != -1)
              {
                i3 = localLayoutParams.height;
                i = i4;
              }
            }
            localView.measure(View.MeasureSpec.makeMeasureSpec(i2, j), View.MeasureSpec.makeMeasureSpec(i3, i));
            if (m == 0)
              break label389;
            i = paramInt1 - localView.getMeasuredHeight();
            j = paramInt2;
          }
        }
      }
      while (true)
      {
        k += 1;
        paramInt1 = i;
        paramInt2 = j;
        break;
        label356: m = 0;
        break label182;
        label362: n = 0;
        break label197;
        label368: j = i1;
        if (n == 0)
          break label207;
        i = 1073741824;
        j = i1;
        break label207;
        label389: i = paramInt1;
        j = paramInt2;
        if (n == 0)
          continue;
        j = paramInt2 - localView.getMeasuredWidth();
        i = paramInt1;
      }
    }
    this.mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824);
    this.mInLayout = true;
    populate();
    this.mInLayout = false;
    int i = getChildCount();
    paramInt2 = 0;
    while (paramInt2 < i)
    {
      localView = getChildAt(paramInt2);
      if (localView.getVisibility() != 8)
      {
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if ((localLayoutParams != null) && (!localLayoutParams.isDecor))
        {
          j = View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * localLayoutParams.heightFactor), 1073741824);
          localView.measure(this.mChildWidthMeasureSpec, j);
        }
      }
      paramInt2 += 1;
    }
  }

  protected void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
  {
    int i;
    View localView;
    if (this.mDecorChildCount > 0)
    {
      int i1 = getScrollY();
      i = getPaddingTop();
      int k = getPaddingBottom();
      int i2 = getHeight();
      int i3 = getChildCount();
      int m = 0;
      while (m < i3)
      {
        localView = getChildAt(m);
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int j;
        int n;
        if (!localLayoutParams.isDecor)
        {
          j = i;
          n = k;
          m += 1;
          k = n;
          i = j;
          continue;
        }
        switch (localLayoutParams.gravity & 0x70)
        {
        default:
          j = i;
        case 48:
        case 16:
        case 80:
        }
        while (true)
        {
          int i4 = j + i1 - localView.getTop();
          n = k;
          j = i;
          if (i4 == 0)
            break;
          localView.offsetTopAndBottom(i4);
          n = k;
          j = i;
          break;
          j = i;
          i += localView.getHeight();
          continue;
          j = Math.max((i2 - localView.getMeasuredHeight()) / 2, i);
          continue;
          j = i2 - k - localView.getMeasuredHeight();
          k += localView.getMeasuredHeight();
        }
      }
    }
    if ((this.mSeenPositionMin < 0) || (paramInt1 < this.mSeenPositionMin))
      this.mSeenPositionMin = paramInt1;
    if ((this.mSeenPositionMax < 0) || (Math.ceil(paramInt1 + paramFloat) > this.mSeenPositionMax))
      this.mSeenPositionMax = (paramInt1 + 1);
    if (this.mOnPageChangeListener != null)
      this.mOnPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
    if (this.mInternalPageChangeListener != null)
      this.mInternalPageChangeListener.onPageScrolled(paramInt1, paramFloat, paramInt2);
    if (this.mPageTransformer != null)
    {
      paramInt2 = getScrollY();
      i = getChildCount();
      paramInt1 = 0;
      if (paramInt1 < i)
      {
        localView = getChildAt(paramInt1);
        if (((LayoutParams)localView.getLayoutParams()).isDecor);
        while (true)
        {
          paramInt1 += 1;
          break;
          paramFloat = (localView.getTop() - paramInt2) / getHeight();
          this.mPageTransformer.transformPage(localView, paramFloat);
        }
      }
    }
    this.mCalledSuper = true;
  }

  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    int j = getChildCount();
    int i;
    int k;
    if ((paramInt & 0x82) != 0)
    {
      i = 0;
      k = 1;
    }
    while (i != j)
    {
      View localView = getChildAt(i);
      if (localView.getVisibility() == 0)
      {
        ItemInfo localItemInfo = infoForChild(localView);
        if ((localItemInfo != null) && (localItemInfo.position == this.mCurItem) && (localView.requestFocus(paramInt, paramRect)))
        {
          return true;
          i = j - 1;
          k = -1;
          j = -1;
          continue;
        }
      }
      i += k;
    }
    return false;
  }

  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    if (this.mAdapter != null)
    {
      this.mAdapter.restoreState(paramParcelable.adapterState, paramParcelable.loader);
      setCurrentItemInternal(paramParcelable.position, false, true);
      return;
    }
    this.mRestoredCurItem = paramParcelable.position;
    this.mRestoredAdapterState = paramParcelable.adapterState;
    this.mRestoredClassLoader = paramParcelable.loader;
  }

  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.position = this.mCurItem;
    if (this.mAdapter != null)
      localSavedState.adapterState = this.mAdapter.saveState();
    return localSavedState;
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3)
      recomputeScrollPosition(paramInt1, paramInt3, this.mPageMargin, this.mPageMargin);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mFakeDragging)
      return true;
    if ((paramMotionEvent.getAction() == 0) && (paramMotionEvent.getEdgeFlags() != 0))
      return false;
    if ((this.mAdapter == null) || (this.mAdapter.getCount() == 0))
      return false;
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain();
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int n = paramMotionEvent.getAction();
    int m = 0;
    int i = m;
    switch (n & 0xFF)
    {
    default:
      i = m;
    case 4:
    case 0:
    case 2:
    case 1:
    case 3:
    case 5:
    case 6:
    }
    while (true)
    {
      if (i != 0)
        ViewCompat.postInvalidateOnAnimation(this);
      return true;
      this.mScroller.abortAnimation();
      this.mPopulatePending = false;
      populate();
      this.mIsBeingDragged = true;
      setScrollState(1);
      float f1 = paramMotionEvent.getY();
      this.mInitialMotionY = f1;
      this.mLastMotionY = f1;
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, 0);
      i = m;
      continue;
      if (!this.mIsBeingDragged)
      {
        i = MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId);
        f1 = Math.abs(MotionEventCompat.getX(paramMotionEvent, i) - this.mLastMotionX);
        float f2 = MotionEventCompat.getY(paramMotionEvent, i);
        float f3 = Math.abs(f2 - this.mLastMotionY);
        if ((f3 > this.mTouchSlop) && (f3 > f1))
        {
          this.mIsBeingDragged = true;
          if (f2 - this.mInitialMotionY <= 0.0F)
            break label344;
          f1 = this.mInitialMotionY + this.mTouchSlop;
        }
      }
      while (true)
      {
        this.mLastMotionY = f1;
        setScrollState(1);
        setScrollingCacheEnabled(true);
        i = m;
        if (!this.mIsBeingDragged)
          break;
        bool1 = false | performDrag(MotionEventCompat.getY(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId)));
        break;
        label344: f1 = this.mInitialMotionY - this.mTouchSlop;
      }
      boolean bool1 = m;
      if (!this.mIsBeingDragged)
        continue;
      Object localObject = this.mVelocityTracker;
      ((VelocityTracker)localObject).computeCurrentVelocity(1000, this.mMaximumVelocity);
      int j = (int)VelocityTrackerCompat.getYVelocity((VelocityTracker)localObject, this.mActivePointerId);
      this.mPopulatePending = true;
      m = getHeight();
      n = getScrollY();
      localObject = infoForCurrentScrollPosition();
      setCurrentItemInternal(determineTargetPage(((ItemInfo)localObject).position, (n / m - ((ItemInfo)localObject).offset) / ((ItemInfo)localObject).heightFactor, j, (int)(MotionEventCompat.getY(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId)) - this.mInitialMotionX)), true, true, j);
      this.mActivePointerId = -1;
      endDrag();
      boolean bool2 = this.mTopEdge.onRelease() | this.mBottomEdge.onRelease();
      continue;
      bool2 = m;
      if (!this.mIsBeingDragged)
        continue;
      scrollToItem(this.mCurItem, true, 0, false);
      this.mActivePointerId = -1;
      endDrag();
      bool2 = this.mTopEdge.onRelease() | this.mBottomEdge.onRelease();
      continue;
      int k = MotionEventCompat.getActionIndex(paramMotionEvent);
      this.mLastMotionY = MotionEventCompat.getY(paramMotionEvent, k);
      this.mActivePointerId = MotionEventCompat.getPointerId(paramMotionEvent, k);
      k = m;
      continue;
      onSecondaryPointerUp(paramMotionEvent);
      this.mLastMotionY = MotionEventCompat.getY(paramMotionEvent, MotionEventCompat.findPointerIndex(paramMotionEvent, this.mActivePointerId));
      k = m;
    }
  }

  boolean pageDown()
  {
    if ((this.mAdapter != null) && (this.mCurItem < this.mAdapter.getCount() - 1))
    {
      setCurrentItem(this.mCurItem + 1, true);
      return true;
    }
    return false;
  }

  boolean pageUp()
  {
    if (this.mCurItem > 0)
    {
      setCurrentItem(this.mCurItem - 1, true);
      return true;
    }
    return false;
  }

  void populate()
  {
    populate(this.mCurItem);
  }

  void populate(int paramInt)
  {
    Object localObject2 = null;
    if (this.mCurItem != paramInt)
    {
      localObject2 = infoForPosition(this.mCurItem);
      this.mCurItem = paramInt;
    }
    if (this.mAdapter == null)
      break label33;
    label33: label547: label804: label1188: 
    while (true)
    {
      return;
      if ((this.mPopulatePending) || (getWindowToken() == null))
        continue;
      this.mAdapter.startUpdate(this);
      paramInt = this.mOffscreenPageLimit;
      int i2 = Math.max(0, this.mCurItem - paramInt);
      int n = this.mAdapter.getCount();
      int i1 = Math.min(n - 1, this.mCurItem + paramInt);
      Object localObject3 = null;
      paramInt = 0;
      Object localObject1 = localObject3;
      Object localObject4;
      if (paramInt < this.mItems.size())
      {
        localObject4 = (ItemInfo)this.mItems.get(paramInt);
        if (((ItemInfo)localObject4).position < this.mCurItem)
          break label547;
        localObject1 = localObject3;
        if (((ItemInfo)localObject4).position == this.mCurItem)
          localObject1 = localObject4;
      }
      localObject3 = localObject1;
      if (localObject1 == null)
      {
        localObject3 = localObject1;
        if (n > 0)
          localObject3 = addNewItem(this.mCurItem, paramInt);
      }
      float f2;
      int m;
      label222: int k;
      int j;
      label314: int i;
      if (localObject3 != null)
      {
        f2 = 0.0F;
        m = paramInt - 1;
        if (m >= 0)
        {
          localObject1 = (ItemInfo)this.mItems.get(m);
          float f3 = ((ItemInfo)localObject3).heightFactor;
          k = this.mCurItem - 1;
          localObject4 = localObject1;
          j = paramInt;
          if (k >= 0)
          {
            if ((f2 < 2.0F - f3) || (k >= i2))
              break label691;
            if (localObject4 != null)
              break label560;
          }
          f2 = ((ItemInfo)localObject3).heightFactor;
          k = j + 1;
          if (f2 < 2.0F)
          {
            if (k >= this.mItems.size())
              break label804;
            localObject1 = (ItemInfo)this.mItems.get(k);
            i = this.mCurItem + 1;
            localObject4 = localObject1;
            if (i < n)
            {
              if ((f2 < 2.0F) || (i <= i1))
                break label931;
              if (localObject4 != null)
                break label810;
            }
          }
          calculatePageOffsets((ItemInfo)localObject3, j, (ItemInfo)localObject2);
        }
      }
      else
      {
        localObject2 = this.mAdapter;
        paramInt = this.mCurItem;
        if (localObject3 == null)
          break label1048;
        localObject1 = ((ItemInfo)localObject3).object;
        ((PagerAdapter)localObject2).setPrimaryItem(this, paramInt, localObject1);
        this.mAdapter.finishUpdate(this);
        if (this.mDrawingOrder == 0)
          break label1054;
        paramInt = 1;
        if (paramInt != 0)
        {
          if (this.mDrawingOrderedChildren != null)
            break label1059;
          this.mDrawingOrderedChildren = new ArrayList();
        }
      }
      while (true)
      {
        j = getChildCount();
        i = 0;
        while (i < j)
        {
          localObject1 = getChildAt(i);
          localObject2 = (LayoutParams)((View)localObject1).getLayoutParams();
          ((LayoutParams)localObject2).childIndex = i;
          if ((!((LayoutParams)localObject2).isDecor) && (((LayoutParams)localObject2).heightFactor == 0.0F))
          {
            localObject3 = infoForChild((View)localObject1);
            if (localObject3 != null)
            {
              ((LayoutParams)localObject2).heightFactor = ((ItemInfo)localObject3).heightFactor;
              ((LayoutParams)localObject2).position = ((ItemInfo)localObject3).position;
            }
          }
          if (paramInt != 0)
            this.mDrawingOrderedChildren.add(localObject1);
          i += 1;
        }
        paramInt += 1;
        break;
        localObject1 = null;
        break label222;
        label560: paramInt = j;
        float f1 = f2;
        localObject1 = localObject4;
        i = m;
        if (k == ((ItemInfo)localObject4).position)
        {
          paramInt = j;
          f1 = f2;
          localObject1 = localObject4;
          i = m;
          if (!((ItemInfo)localObject4).scrolling)
          {
            this.mItems.remove(m);
            this.mAdapter.destroyItem(this, k, ((ItemInfo)localObject4).object);
            i = m - 1;
            paramInt = j - 1;
            if (i < 0)
              break label683;
            localObject1 = (ItemInfo)this.mItems.get(i);
            f1 = f2;
          }
        }
        while (true)
        {
          k -= 1;
          j = paramInt;
          f2 = f1;
          localObject4 = localObject1;
          m = i;
          break;
          label683: localObject1 = null;
          f1 = f2;
        }
        label691: if ((localObject4 != null) && (k == ((ItemInfo)localObject4).position))
        {
          f1 = f2 + ((ItemInfo)localObject4).heightFactor;
          i = m - 1;
          if (i >= 0);
          for (localObject1 = (ItemInfo)this.mItems.get(i); ; localObject1 = null)
          {
            paramInt = j;
            break;
          }
        }
        f1 = f2 + addNewItem(k, m + 1).heightFactor;
        paramInt = j + 1;
        if (m >= 0);
        for (localObject1 = (ItemInfo)this.mItems.get(m); ; localObject1 = null)
        {
          i = m;
          break;
        }
        localObject1 = null;
        break label314;
        label810: f1 = f2;
        localObject1 = localObject4;
        paramInt = k;
        if (i == ((ItemInfo)localObject4).position)
        {
          f1 = f2;
          localObject1 = localObject4;
          paramInt = k;
          if (!((ItemInfo)localObject4).scrolling)
          {
            this.mItems.remove(k);
            this.mAdapter.destroyItem(this, i, ((ItemInfo)localObject4).object);
            if (k >= this.mItems.size())
              break label920;
            localObject1 = (ItemInfo)this.mItems.get(k);
            paramInt = k;
            f1 = f2;
          }
        }
        while (true)
        {
          i += 1;
          f2 = f1;
          localObject4 = localObject1;
          k = paramInt;
          break;
          localObject1 = null;
          f1 = f2;
          paramInt = k;
        }
        if ((localObject4 != null) && (i == ((ItemInfo)localObject4).position))
        {
          f1 = f2 + ((ItemInfo)localObject4).heightFactor;
          paramInt = k + 1;
          if (paramInt < this.mItems.size());
          for (localObject1 = (ItemInfo)this.mItems.get(paramInt); ; localObject1 = null)
            break;
        }
        localObject1 = addNewItem(i, k);
        paramInt = k + 1;
        f1 = f2 + ((ItemInfo)localObject1).heightFactor;
        if (paramInt < this.mItems.size());
        for (localObject1 = (ItemInfo)this.mItems.get(paramInt); ; localObject1 = null)
          break;
        localObject1 = null;
        break label384;
        paramInt = 0;
        break label410;
        this.mDrawingOrderedChildren.clear();
      }
      if (paramInt != 0)
        Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
      if (!hasFocus())
        break;
      localObject1 = findFocus();
      if (localObject1 != null);
      for (localObject1 = infoForAnyChild((View)localObject1); ; localObject1 = null)
      {
        if ((localObject1 != null) && (((ItemInfo)localObject1).position == this.mCurItem))
          break label1188;
        paramInt = 0;
        while (true)
        {
          if (paramInt >= getChildCount())
            break label1182;
          localObject1 = getChildAt(paramInt);
          localObject2 = infoForChild((View)localObject1);
          if ((localObject2 != null) && (((ItemInfo)localObject2).position == this.mCurItem) && (((View)localObject1).requestFocus(2)))
            break;
          paramInt += 1;
        }
        break label33;
      }
    }
  }

  public void setAdapter(PagerAdapter paramPagerAdapter)
  {
    if (this.mAdapter != null)
    {
      this.mAdapter.unregisterDataSetObserver(this.mObserver);
      this.mAdapter.startUpdate(this);
      int i = 0;
      while (i < this.mItems.size())
      {
        localObject = (ItemInfo)this.mItems.get(i);
        this.mAdapter.destroyItem(this, ((ItemInfo)localObject).position, ((ItemInfo)localObject).object);
        i += 1;
      }
      this.mAdapter.finishUpdate(this);
      this.mItems.clear();
      removeNonDecorViews();
      this.mCurItem = 0;
      scrollTo(0, 0);
    }
    Object localObject = this.mAdapter;
    this.mAdapter = paramPagerAdapter;
    if (this.mAdapter != null)
    {
      if (this.mObserver == null)
        this.mObserver = new PagerObserver(null);
      this.mAdapter.registerDataSetObserver(this.mObserver);
      this.mPopulatePending = false;
      this.mFirstLayout = true;
      if (this.mRestoredCurItem < 0)
        break label233;
      this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
      setCurrentItemInternal(this.mRestoredCurItem, false, true);
      this.mRestoredCurItem = -1;
      this.mRestoredAdapterState = null;
      this.mRestoredClassLoader = null;
    }
    while (true)
    {
      if ((this.mAdapterChangeListener != null) && (localObject != paramPagerAdapter))
        this.mAdapterChangeListener.onAdapterChanged((PagerAdapter)localObject, paramPagerAdapter);
      return;
      label233: populate();
    }
  }

  void setChildrenDrawingOrderEnabledCompat(boolean paramBoolean)
  {
    if (this.mSetChildrenDrawingOrderEnabled == null);
    try
    {
      this.mSetChildrenDrawingOrderEnabled = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", new Class[] { Boolean.TYPE });
    }
    catch (NoSuchMethodException localException)
    {
      try
      {
        while (true)
        {
          if (this.mSetChildrenDrawingOrderEnabled != null)
            this.mSetChildrenDrawingOrderEnabled.invoke(this, new Object[] { Boolean.valueOf(paramBoolean) });
          return;
          localNoSuchMethodException = localNoSuchMethodException;
          Log.e("VerticalViewPager", "Can't find setChildrenDrawingOrderEnabled", localNoSuchMethodException);
        }
      }
      catch (Exception localException)
      {
        Log.e("VerticalViewPager", "Error changing children drawing order", localException);
      }
    }
  }

  public void setCurrentItem(int paramInt)
  {
    this.mPopulatePending = false;
    if (!this.mFirstLayout);
    for (boolean bool = true; ; bool = false)
    {
      setCurrentItemInternal(paramInt, bool, false);
      return;
    }
  }

  public void setCurrentItem(int paramInt, boolean paramBoolean)
  {
    this.mPopulatePending = false;
    setCurrentItemInternal(paramInt, paramBoolean, false);
  }

  void setCurrentItemInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    setCurrentItemInternal(paramInt, paramBoolean1, paramBoolean2, 0);
  }

  void setCurrentItemInternal(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2)
  {
    boolean bool = true;
    if ((this.mAdapter == null) || (this.mAdapter.getCount() <= 0))
    {
      setScrollingCacheEnabled(false);
      return;
    }
    if ((!paramBoolean2) && (this.mCurItem == paramInt1) && (this.mItems.size() != 0))
    {
      setScrollingCacheEnabled(false);
      return;
    }
    int i;
    if (paramInt1 < 0)
      i = 0;
    while (true)
    {
      paramInt1 = this.mOffscreenPageLimit;
      if ((i <= this.mCurItem + paramInt1) && (i >= this.mCurItem - paramInt1))
        break;
      paramInt1 = 0;
      while (paramInt1 < this.mItems.size())
      {
        ((ItemInfo)this.mItems.get(paramInt1)).scrolling = true;
        paramInt1 += 1;
      }
      i = paramInt1;
      if (paramInt1 < this.mAdapter.getCount())
        continue;
      i = this.mAdapter.getCount() - 1;
    }
    if (this.mCurItem != i);
    for (paramBoolean2 = bool; ; paramBoolean2 = false)
    {
      populate(i);
      scrollToItem(i, paramBoolean1, paramInt2, paramBoolean2);
      return;
    }
  }

  public void setOnPageChangeListener(OnPageChangeListener paramOnPageChangeListener)
  {
    this.mOnPageChangeListener = paramOnPageChangeListener;
  }

  public void setPageMarginDrawable(Drawable paramDrawable)
  {
    this.mMarginDrawable = paramDrawable;
    if (paramDrawable != null)
      refreshDrawableState();
    if (paramDrawable == null);
    for (boolean bool = true; ; bool = false)
    {
      setWillNotDraw(bool);
      invalidate();
      return;
    }
  }

  public void setPageTransformer(boolean paramBoolean, PageTransformer paramPageTransformer)
  {
    int j = 1;
    boolean bool1;
    boolean bool2;
    label28: int i;
    if (Build.VERSION.SDK_INT >= 11)
    {
      if (paramPageTransformer == null)
        break label75;
      bool1 = true;
      if (this.mPageTransformer == null)
        break label81;
      bool2 = true;
      if (bool1 == bool2)
        break label87;
      i = 1;
      label37: this.mPageTransformer = paramPageTransformer;
      setChildrenDrawingOrderEnabledCompat(bool1);
      if (!bool1)
        break label92;
      if (paramBoolean)
        j = 2;
    }
    label75: label81: label87: label92: for (this.mDrawingOrder = j; ; this.mDrawingOrder = 0)
    {
      if (i != 0)
        populate();
      return;
      bool1 = false;
      break;
      bool2 = false;
      break label28;
      i = 0;
      break label37;
    }
  }

  void smoothScrollTo(int paramInt1, int paramInt2, int paramInt3)
  {
    if (getChildCount() == 0)
    {
      setScrollingCacheEnabled(false);
      return;
    }
    int i = getScrollX();
    int j = getScrollY();
    int k = paramInt1 - i;
    paramInt2 -= j;
    if ((k == 0) && (paramInt2 == 0))
    {
      completeScroll(false);
      populate();
      setScrollState(0);
      return;
    }
    setScrollingCacheEnabled(true);
    setScrollState(2);
    paramInt1 = getHeight();
    int m = paramInt1 / 2;
    float f3 = Math.min(1.0F, 1.0F * Math.abs(k) / paramInt1);
    float f1 = m;
    float f2 = m;
    f3 = distanceInfluenceForSnapDuration(f3);
    paramInt3 = Math.abs(paramInt3);
    if (paramInt3 > 0);
    for (paramInt1 = Math.round(1000.0F * Math.abs((f1 + f2 * f3) / paramInt3)) * 4; ; paramInt1 = (int)((1.0F + Math.abs(k) / (this.mPageMargin + f1 * f2)) * 100.0F))
    {
      paramInt1 = Math.min(paramInt1, 800);
      this.mScroller.startScroll(i, j, k, paramInt2, paramInt1);
      ViewCompat.postInvalidateOnAnimation(this);
      return;
      f1 = paramInt1;
      f2 = this.mAdapter.getPageHeight(this.mCurItem);
    }
  }

  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mMarginDrawable);
  }

  static abstract interface Decor
  {
  }

  static class ItemInfo
  {
    float heightFactor;
    Object object;
    float offset;
    int position;
    boolean scrolling;
    float widthFactor;
  }

  public static class LayoutParams extends ViewGroup.LayoutParams
  {
    int childIndex;
    public int gravity;
    float heightFactor = 0.0F;
    public boolean isDecor;
    boolean needsMeasure;
    int position;
    float widthFactor = 0.0F;

    public LayoutParams()
    {
      super(-1);
    }

    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, VerticalViewPager.LAYOUT_ATTRS);
      this.gravity = paramContext.getInteger(0, 48);
      paramContext.recycle();
    }
  }

  class MyAccessibilityDelegate extends AccessibilityDelegateCompat
  {
    MyAccessibilityDelegate()
    {
    }

    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(VerticalViewPager.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      boolean bool = true;
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      paramAccessibilityNodeInfoCompat.setClassName(VerticalViewPager.class.getName());
      if ((VerticalViewPager.this.mAdapter != null) && (VerticalViewPager.this.mAdapter.getCount() > 1));
      while (true)
      {
        paramAccessibilityNodeInfoCompat.setScrollable(bool);
        if ((VerticalViewPager.this.mAdapter != null) && (VerticalViewPager.this.mCurItem >= 0) && (VerticalViewPager.this.mCurItem < VerticalViewPager.this.mAdapter.getCount() - 1))
          paramAccessibilityNodeInfoCompat.addAction(4096);
        if ((VerticalViewPager.this.mAdapter != null) && (VerticalViewPager.this.mCurItem > 0) && (VerticalViewPager.this.mCurItem < VerticalViewPager.this.mAdapter.getCount()))
          paramAccessibilityNodeInfoCompat.addAction(8192);
        return;
        bool = false;
      }
    }

    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if (super.performAccessibilityAction(paramView, paramInt, paramBundle))
        return true;
      switch (paramInt)
      {
      default:
        return false;
      case 4096:
        if ((VerticalViewPager.this.mAdapter != null) && (VerticalViewPager.this.mCurItem >= 0) && (VerticalViewPager.this.mCurItem < VerticalViewPager.this.mAdapter.getCount() - 1))
        {
          VerticalViewPager.this.setCurrentItem(VerticalViewPager.this.mCurItem + 1);
          return true;
        }
        return false;
      case 8192:
      }
      if ((VerticalViewPager.this.mAdapter != null) && (VerticalViewPager.this.mCurItem > 0) && (VerticalViewPager.this.mCurItem < VerticalViewPager.this.mAdapter.getCount()))
      {
        VerticalViewPager.this.setCurrentItem(VerticalViewPager.this.mCurItem - 1);
        return true;
      }
      return false;
    }
  }

  static abstract interface OnAdapterChangeListener
  {
    public abstract void onAdapterChanged(PagerAdapter paramPagerAdapter1, PagerAdapter paramPagerAdapter2);
  }

  public static abstract interface OnPageChangeListener
  {
    public abstract void onPageScrollStateChanged(int paramInt);

    public abstract void onPageScrolled(int paramInt1, float paramFloat, int paramInt2);

    public abstract void onPageSelected(int paramInt);
  }

  public static abstract interface PageTransformer
  {
    public abstract void transformPage(View paramView, float paramFloat);
  }

  private class PagerObserver extends DataSetObserver
  {
    private PagerObserver()
    {
    }

    public void onChanged()
    {
      VerticalViewPager.this.dataSetChanged();
    }

    public void onInvalidated()
    {
      VerticalViewPager.this.dataSetChanged();
    }
  }

  public static class SavedState extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public VerticalViewPager.SavedState createFromParcel(Parcel paramParcel, ClassLoader paramClassLoader)
      {
        return new VerticalViewPager.SavedState(paramParcel, paramClassLoader);
      }

      public VerticalViewPager.SavedState[] newArray(int paramInt)
      {
        return new VerticalViewPager.SavedState[paramInt];
      }
    });
    Parcelable adapterState;
    ClassLoader loader;
    int position;

    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super();
      ClassLoader localClassLoader = paramClassLoader;
      if (paramClassLoader == null)
        localClassLoader = getClass().getClassLoader();
      this.position = paramParcel.readInt();
      this.adapterState = paramParcel.readParcelable(localClassLoader);
      this.loader = localClassLoader;
    }

    public SavedState(Parcelable paramParcelable)
    {
      super();
    }

    public String toString()
    {
      return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.position);
      paramParcel.writeParcelable(this.adapterState, paramInt);
    }
  }

  static class ViewPositionComparator
    implements Comparator<View>
  {
    public int compare(View paramView1, View paramView2)
    {
      paramView1 = (VerticalViewPager.LayoutParams)paramView1.getLayoutParams();
      paramView2 = (VerticalViewPager.LayoutParams)paramView2.getLayoutParams();
      if (paramView1.isDecor != paramView2.isDecor)
      {
        if (paramView1.isDecor)
          return 1;
        return -1;
      }
      return paramView1.position - paramView2.position;
    }
  }
}

/* Location:           C:\Users\xuetong\Desktop\dazhongdianping7.9.6\ProjectSrc\classes-dex2jar.jar
 * Qualified Name:     com.dianping.widget.VerticalViewPager
 * JD-Core Version:    0.6.0
 */