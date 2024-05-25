/**
  * Created by MomoLuaNative.
  * Copyright (c) 2019, Momo Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
package com.immomo.mls.fun.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.TextView;


import com.immomo.mls.base.ud.lv.ILView;
import com.immomo.mls.fun.constants.EditTextViewInputMode;
import com.immomo.mls.fun.ud.view.UDEditText;
import com.immomo.mls.fun.weight.BorderRadiusEditText;

import java.lang.reflect.Field;


/**
 * Created by XiongFangyu on 2018/8/1.
 */
public class LuaEditText extends BorderRadiusEditText implements ILView<UDEditText> {

    private UDEditText udEditText;
    private ViewLifeCycleCallback cycleCallback;

    public LuaEditText(Context context, UDEditText metaTable) {
        super(context);

        this.udEditText = metaTable;

        setBackgroundDrawable(null);
        setViewLifeCycleCallback(udEditText);
        setTextSize(14);
        // 默认多行模式对应左上起点
        setGravity(Gravity.TOP);
        setInputType(EditTextViewInputMode.Normal);
        setSingleLine(false);
        setHintTextColor(Color.rgb(128, 128, 128));
        setTextColor(getResources().getColor(android.R.color.black));
//        setCursorColor(getResources().getColor(android.R.color.black));
        setPadding(0, 0, 0, 0);
        setEllipsize(TextUtils.TruncateAt.END);
        setOnEditorActionListener(getUserdata());
    }


    @Override
    public UDEditText getUserdata() {
        return udEditText;
    }


    @Override
    public void setViewLifeCycleCallback(ViewLifeCycleCallback cycleCallback) {
        this.cycleCallback = cycleCallback;
    }

    public void setCursorColor(int color) {
//未知原因无法实现改变颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.setTextCursorDrawable(new ColorDrawable(Color.BLUE));
        }else{
            try {
                // 获取光标字段
                @SuppressLint("SoonBlockedPrivateApi") Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);

                // 创建一个新的颜色Drawable
                Drawable[] drawables = new Drawable[2];
                drawables[0] = drawables[1] = new ColorDrawable(color); // 替换为你的颜色值
                f.set(this, drawables);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        getUserdata().measureOverLayout(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getUserdata().layoutOverLayout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getUserdata().drawOverLayout(canvas);
    }

    /**
     * 回调beginChangingCallback，和IOS统一效果，改为focusChange为true是回调（即回去焦点时回调）
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            setCursorVisible(true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled())
            return false;
        if (event != null && event.getAction() == MotionEvent.ACTION_DOWN){
            setCursorVisible(true);
            getUserdata().callBeforeTextChanged();
        }
        return super.onTouchEvent(event);
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
        if (cycleCallback != null) {
            cycleCallback.onDetached();
        }
    }
}