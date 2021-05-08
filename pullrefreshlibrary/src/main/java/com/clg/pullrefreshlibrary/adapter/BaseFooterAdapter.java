package com.clg.pullrefreshlibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseFooterAdapter {
    protected LayoutInflater mInflater;

    public BaseFooterAdapter(Context context){
        mInflater = LayoutInflater.from(context);
    }

    /**
     * 获取FooterView
     * @return
     */
    public abstract View getFooterView();

    /**
     * 被上拉时此事件发生
     * @param offsetY 上拉的距离
     */
    public abstract void pullViewToRefresh(int offsetY);

    /**
     * 上拉后，完全显示时此事件发生
     * @param offsetY 上拉的距离
     */
    public abstract void releaseViewToRefresh(int offsetY);

    /**
     * FooterView 正在刷新
     */
    public abstract void footerRefreshing();

    /**
     * FooterView 刷新完成
     */
    public abstract void footerRefreshComplete();
}
