package com.clg.pullview.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clg.pullview.R;

public class InitBaseFooterAdapter extends BaseFooterAdapter{

    private TextView footerView;
    private ProgressBar mProgressBar;
    public InitBaseFooterAdapter(Context context) {
        super(context);
    }

    @Override
    public View getFooterView() {
        View mFooterView = mInflater.inflate(R.layout.footer_layout,null,false);
        footerView = mFooterView.findViewById(R.id.footer_text);
        mProgressBar = mFooterView.findViewById(R.id.progressBar);
        return mFooterView;
    }

    @Override
    public void pullViewToRefresh(int offsetY) {
        footerView.setText("上拉加载");
    }

    @Override
    public void releaseViewToRefresh(int offsetY) {
        footerView.setText("松开加载");
    }

    @Override
    public void footerRefreshing() {
        mProgressBar.setVisibility(View.VISIBLE);
        footerView.setText("正在加载。。。");
    }

    @Override
    public void footerRefreshComplete() {
        mProgressBar.setVisibility(View.INVISIBLE);
        footerView.setText("加载完成");

    }
}
