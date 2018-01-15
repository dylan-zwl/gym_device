package com.android.module.retrofit.common;


import io.reactivex.Observer;

/**
 * Created by Administrator on 2017/12/29.
 */

public abstract class ProgressObserver<T> implements Observer<T> {
    abstract public void onProgress(long progress, long total, boolean done);
}
