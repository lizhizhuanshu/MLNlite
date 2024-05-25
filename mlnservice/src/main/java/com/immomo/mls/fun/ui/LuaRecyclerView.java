/**
  * Created by MomoLuaNative.
  * Copyright (c) 2019, Momo Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
package com.immomo.mls.fun.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.immomo.mls.MLSAdapterContainer;
import com.immomo.mls.MLSFlag;
import com.immomo.mls.R;
import com.immomo.mls.base.ud.lv.ILViewGroup;
import com.immomo.mls.fun.other.Point;
import com.immomo.mls.fun.ud.view.UDView;
import com.immomo.mls.fun.ud.view.recycler.UDBaseRecyclerAdapter;
import com.immomo.mls.fun.ud.view.recycler.UDBaseRecyclerLayout;
import com.immomo.mls.fun.ud.view.recycler.UDRecyclerView;
import com.immomo.mls.fun.weight.BorderRadiusSwipeRefreshLayout;
import com.immomo.mls.fun.weight.MLSRecyclerView;
import com.immomo.mls.util.DimenUtil;
import com.immomo.mls.util.LuaViewUtil;
import com.immomo.mls.utils.MainThreadExecutor;
import com.immomo.mls.weight.load.ILoadViewDelegete;

/**
 * Created by XiongFangyu on 2018/7/19.
 */
public class LuaRecyclerView<A extends UDBaseRecyclerAdapter, L extends UDBaseRecyclerLayout>
        extends BorderRadiusSwipeRefreshLayout implements ILViewGroup<UDRecyclerView>, IRefreshRecyclerView, OnLoadListener, SwipeRefreshLayout.OnRefreshListener {
    private final MLSRecyclerView recyclerView;
    private final UDRecyclerView userdata;
    private final ILoadViewDelegete loadViewDelegete;
    private SizeChangedListener sizeChangedListener;
    private boolean loadEnable = false;
    private boolean isLoading = false;
    private ViewLifeCycleCallback cycleCallback;

    public LuaRecyclerView(Context context, UDRecyclerView javaUserdata, boolean refreshEnable, boolean loadEnable) {
        super(context);
        userdata = javaUserdata;
        recyclerView = (MLSRecyclerView) LayoutInflater.from(context).inflate(R.layout.default_layout_recycler_view, null);
        loadViewDelegete = MLSAdapterContainer.getLoadViewAdapter().newLoadViewDelegate(context, recyclerView);
        recyclerView.setLoadViewDelegete(loadViewDelegete);
        recyclerView.setOnLoadListener(this);
        recyclerView.setCycleCallback(userdata);
        recyclerView.setClipToPadding(false);
        userdata.setLoadViewDelegete(loadViewDelegete);
        setColorSchemeColors(MLSFlag.getRefreshColor());
        setProgressViewOffset(MLSFlag.isRefreshScale(), 0, MLSFlag.getRefreshEndPx());
//        setDistanceToTriggerSync(MLSFlag.getRefreshEndPx());
        addView(recyclerView, LuaViewUtil.createRelativeLayoutParamsMM());
        setRefreshEnable(refreshEnable);
        setLoadEnable(loadEnable);
    }

    //<editor-fold desc="ILView">

    @Override
    public UDRecyclerView getUserdata() {
        return userdata;
    }

    @Override
    public void setViewLifeCycleCallback(ViewLifeCycleCallback cycleCallback) {
        this.cycleCallback = cycleCallback;
    }

    @Override
    public void setClipChildren(boolean clipChildren) {
        super.setClipChildren(clipChildren);
        getRecyclerView().setClipChildren(clipChildren);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (cycleCallback != null) {
            cycleCallback.onAttached();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    //</editor-fold>

    //<editor-fold desc="IRefreshRecyclerView">
    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    @Override
    public void setRefreshEnable(boolean enable) {
        setEnabled(enable);
        if (enable) {
            setOnRefreshListener(this);
        }
    }

    @Override
    public boolean isRefreshEnable() {
        return isEnabled();
    }

    @Override
    public void startRefreshing() {
        userdata.callScrollToTop(false);
        setRefreshing(true);
        MainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

    @Override
    public void stopRefreshing() {
        setRefreshing(false);
        if (isLoading)
            return;
        loadViewDelegete.setEnable(loadEnable);
    }

    @Override
    public void setLoadEnable(boolean enable) {
        if (loadEnable == enable)
            return;
        loadEnable = enable;
        loadViewDelegete.setEnable(enable);
    }

    @Override
    public boolean isLoadEnable() {
        return loadEnable;
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void stopLoading() {
        isLoading = false;
        loadViewDelegete.getLoadView().stopAnim();
    }

    @Override
    public void noMoreData() {
        loadViewDelegete.noMoreData();
    }

    @Override
    public void startLoading() {
        loadViewDelegete.startLoading();
    }

    @Override
    public void resetLoading() {
        loadViewDelegete.resetLoading();
    }

    @Override
    public void loadError() {
        isLoading = false;
        loadViewDelegete.loadError();
    }

    @Override
    public int getCurrentState() {
        return loadViewDelegete.getCurrentState();
    }

    @Override
    public void setSizeChangedListener(SizeChangedListener sizeChangedListener) {
        this.sizeChangedListener = sizeChangedListener;
    }
    //</editor-fold>

    //<editor-fold desc="OnLoadListener">

    @Override
    public void onLoad() {
        if (isLoading)
            return;
        isLoading = true;
        userdata.callbackLoading();
    }
    //</editor-fold>

    //<editor-fold desc="OnRefreshListener">

    @Override
    public void onRefresh() {
        loadViewDelegete.setEnable(false);
        userdata.callbackRefresh();
    }
    //</editor-fold>

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (sizeChangedListener != null) {
            sizeChangedListener.onSizeChanged(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int childLeft = getPaddingLeft() + left;
        final int childTop = getPaddingTop() + top;
        for (int index = 0, l = getChildCount(); index < l; index++) {
            View c = getChildAt(index);
            if (c == recyclerView)
                continue;
            LayoutParams lp = c.getLayoutParams();
            if (!(lp instanceof MarginLayoutParams))
                continue;
            MarginLayoutParams mlp = (MarginLayoutParams) lp;
            int cl = childLeft + mlp.leftMargin;
            int ct = childTop + mlp.topMargin;
            c.layout(cl, ct, cl + c.getMeasuredWidth(), ct + c.getMeasuredHeight());
        }

        getUserdata().layoutOverLayout(left, top, right, bottom);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int index = 0, l = getChildCount(); index < l; index++) {
            View c = getChildAt(index);
            if (c == recyclerView)
                continue;
            if (!(c.getLayoutParams() instanceof MarginLayoutParams))
                continue;
            measureChildWithMargins(c, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        getUserdata().measureOverLayout(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        getUserdata().drawOverLayout(canvas);
    }
    //<editor-fold desc="ILViewGroup">

    @Override
    public void bringSubviewToFront(UDView child) {

    }

    @Override
    public void sendSubviewToBack(UDView child) {

    }

    /**
     * 滑动rv到指定位置
     */
    @Override
    public void smoothScrollTo(Point p) {
        getRecyclerView().smoothScrollBy((int) p.getXPx() - getRecyclerView().computeHorizontalScrollOffset(), (int) p.getYPx() - getRecyclerView().computeVerticalScrollOffset());
    }

    @Override
    public void setContentOffset(Point p) {
        getRecyclerView().scrollBy((int) p.getXPx() - getRecyclerView().computeHorizontalScrollOffset(), (int) p.getYPx() - getRecyclerView().computeVerticalScrollOffset());
    }

    @Override
    public Point getContentOffset() {
        return new Point(DimenUtil.pxToDpi(getRecyclerView().computeHorizontalScrollOffset()), DimenUtil.pxToDpi(getRecyclerView().computeVerticalScrollOffset()));
    }

    @Override
    public LayoutParams applyLayoutParams(LayoutParams src, UDView.UDLayoutParams udLayoutParams) {
        MarginLayoutParams p = (MarginLayoutParams) parseLayoutParams(src);
        p.setMargins(udLayoutParams.realMarginLeft, udLayoutParams.realMarginTop, udLayoutParams.realMarginRight, udLayoutParams.realMarginBottom);
        return p;
    }

    @Override
    public LayoutParams applyChildCenter(LayoutParams src, UDView.UDLayoutParams udLayoutParams) {
        return src;
    }
    //</editor-fold>

    private LayoutParams parseLayoutParams(LayoutParams src) {
        if (src == null) {
            src = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        } else if (!(src instanceof MarginLayoutParams)) {
            src = new MarginLayoutParams(src);
        }
        return src;
    }
}