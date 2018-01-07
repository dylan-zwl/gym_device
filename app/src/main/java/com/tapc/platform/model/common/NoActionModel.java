package com.tapc.platform.model.common;

/**
 * Created by dylan on 2018/1/7.
 */

public class NoActionModel {
    private static NoActionModel sNoActionModel;
    private int mNoActionCount = 0;
    private Listener mListener;

    private NoActionModel() {

    }

    public static NoActionModel getInstance() {
        if (sNoActionModel == null) {
            synchronized (NoActionModel.class) {
                if (sNoActionModel == null) {
                    sNoActionModel = new NoActionModel();
                }
            }
        }
        return sNoActionModel;
    }

    public void cleanNoActionCount() {
        mNoActionCount = 0;
    }

    public interface Listener {
        boolean restriction();

        void count(int total);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void increaseCount() {
        if (mListener != null && mListener.restriction()) {
            mNoActionCount++;
            mListener.count(mNoActionCount);
        }
    }
}
