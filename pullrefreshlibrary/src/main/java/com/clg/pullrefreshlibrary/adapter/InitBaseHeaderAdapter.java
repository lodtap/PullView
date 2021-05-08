package com.clg.pullrefreshlibrary.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.clg.pullrefreshlibrary.R;

import static android.view.View.VISIBLE;

public class InitBaseHeaderAdapter extends BaseHeaderAdapter{

    private TextView headerText;
    private ProgressBar mProgressBar;
    public InitBaseHeaderAdapter(Context context) {
        super(context);
    }

    @Override
    public View getHeaderView() {
        View mHeaderView = mInflater.inflate(R.layout.header_layout,null,false);
        headerText = mHeaderView.findViewById(R.id.header_text);
        mProgressBar = mHeaderView.findViewById(R.id.progressBar);
        return mHeaderView;
    }

    @Override
    public void pullViewToRefresh(int offsetY) {
        headerText.setText("下拉刷新");
    }

    @Override
    public void releaseViewToRefresh(int offsetY) {
        headerText.setText("松开刷新");
    }

    @Override
    public void headerRefreshing() {
        mProgressBar.setVisibility(VISIBLE);
        headerText.setText("正在刷新");
    }

    @Override
    public void headerRefreshComplete() {
        mProgressBar.setVisibility(View.INVISIBLE);
        headerText.setVisibility(VISIBLE);
        headerText.setText("下拉刷新");
    }
}
