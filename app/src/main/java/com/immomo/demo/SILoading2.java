/**
  * Created by MomoLuaNative.
  * Copyright (c) 2019, Momo Group. All rights reserved.
  *
  * This source code is licensed under the MIT.
  * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
  */
package com.immomo.demo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialog;

import com.immomo.mls.LuaViewManager;
import com.immomo.mls.R;
import com.immomo.mls.annotation.LuaBridge;
import com.immomo.mls.annotation.LuaClass;
import com.immomo.mls.annotation.MLN;
import com.immomo.mls.utils.LVCallback;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

/**
 * Author       :   wu.tianlong@immomo.com
 * Date         :   2018/12/7
 * Time         :   下午12:16
 * Description  :    加载中对话框
 */
@MLN
@LuaClass
public class SILoading2 {
    public static final String LUA_CLASS_NAME = "Loading2";

    public Dialog mDialog;

    // 消失后通知 lua
    LVCallback mOnCancelCallBack;

    private Globals globals;

    public SILoading2(Globals globals, LuaValue[] init) {
        this.globals = globals;
    }


    public void __onLuaGc() {
        if (mOnCancelCallBack != null)
            mOnCancelCallBack.destroy();
        globals = null;
    }

    protected Context getContext() {
        LuaViewManager m = (LuaViewManager) globals.getJavaUserdata();
        return m != null ? m.context : null;
    }

    private void init() {

        if (mDialog != null)
            return;
        Context c = getContext();
        if (c == null)
            return;

        mDialog = new AppCompatDialog(c);
        mDialog.setCanceledOnTouchOutside(false);

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

       /* Window window = mDialog.getWindow();
        if (window != null){
            window.setLayout(DimenUtil.dpiToPx(45), DimenUtil.dpiToPx(45));
            window.setGravity(Gravity.CENTER);
        }*/

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface anInterface) {
                if (mOnCancelCallBack != null)
                    mOnCancelCallBack.call();
            }
        });

        mDialog.setContentView(R.layout.luasdk_loading_diloag);
    }


    @LuaBridge
    public void show() {
        try {

            init();

            mDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @LuaBridge(alias = "cancel")
    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        init();
        mDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    // 只有lua中 cancel方法设为true，并且是用户手动点击旁白消失，才会发生回调
    @LuaBridge
    public void setOnCancelCallBack(LVCallback cancelCallBack) {
//        if (mOnCancelCallBack != null)
//            mOnCancelCallBack.destroy();
        mOnCancelCallBack = cancelCallBack;
    }

    @LuaBridge
    public void hide() {
        try {
            mDialog.dismiss();
            // mDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}