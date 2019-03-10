package com.chx.livemaker.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by cangHX
 * on 2018/12/17  10:27
 */
public class SingleToast {

    private static WeakReference<Context> mContextSoftReference;
    private static WeakReference<Toast> mToastSoftReference;

    private SingleToast() {
    }

    private static class Factory {
        private static final SingleToast mInstance = new SingleToast();
    }

    public static SingleToast getInstance(Context context) {
        if (mContextSoftReference==null||mContextSoftReference.get()==null) {
            if (context instanceof Application) {
                mContextSoftReference = new WeakReference<>(context);
            } else if (context instanceof Activity) {
                mContextSoftReference = new WeakReference<>(context.getApplicationContext());
            }
        }
        return Factory.mInstance;
    }

    public void show(@StringRes int resId){
        if (mContextSoftReference!=null){
            final Context context=mContextSoftReference.get();
            if (context!=null) {
                show(context.getString(resId));
            }
        }
    }

    public void show(String text){
        if (mToastSoftReference==null||mToastSoftReference.get()==null){
            final Context context=mContextSoftReference==null?null:mContextSoftReference.get();
            if (context!=null){
                Toast toast=Toast.makeText(context,text,Toast.LENGTH_SHORT);
                mToastSoftReference=new WeakReference<>(toast);
                toast.show();
            }
        }else {
            final Toast toast=mToastSoftReference.get();
            toast.setText(text);
            toast.show();
        }
    }
}
