package com.clg.pullview.adapter;

import android.content.Context;
import android.os.IInterface;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseHeaderAdapter {
    protected LayoutInflater mInflater;
    public BaseHeaderAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 获得 HeaderView
     * @return
     */
    public abstract View getHeaderView();

    /**
     * 下拉时 此事件发生
     * @param offsetY 下拉的距离
     */
    public abstract void pullViewToRefresh(int offsetY);

    /**
     * 下拉后，完全显示时 此事件发生
     * @param offsetY
     */
    public abstract void releaseViewToRefresh(int offsetY);

    /**
     * 正在刷新
     */
    public abstract void headerRefreshing();

    /**
     * 刷新完成
     */
    public abstract void headerRefreshComplete();
}
