package com.clg.pullrefreshlibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.clg.pullrefreshlibrary.adapter.BaseFooterAdapter;
import com.clg.pullrefreshlibrary.adapter.BaseHeaderAdapter;
import com.clg.pullrefreshlibrary.adapter.InitBaseFooterAdapter;
import com.clg.pullrefreshlibrary.adapter.InitBaseHeaderAdapter;
import com.clg.pullrefreshlibrary.interfaces.OnFooterRefreshListener;
import com.clg.pullrefreshlibrary.interfaces.OnHeaderRefreshListener;
import com.clg.pullrefreshlibrary.util.MeasureTools;


public class PullRefreshView extends LinearLayout {

    private static final String TAG = PullRefreshView.class.getSimpleName();
    //上拉还是下拉
    private static final int PULL_UP_STATE = 0;//上拉
    private static final int PULL_DOWN_STATE = 1;//下拉
    //刷新时的状态
    private static final int PULL_TO_REFRESH = 2;//拉动
    private static final int RELEASE_TO_REFRESH = 3;//松开
    private static final int REFRESHING = 4;//刷新

    private int mPullState;

    private int animDuration = 300;//头、尾回弹动画执行时间

    private RecyclerView mRecyclerView;


    //header
    private int mHeaderState;
    private View mHeaderView;
    private int mHeaderViewHeight;
    //footer
    private int mFooterState;
    private View mFooterView;
    private int mFooterViewHeight;
    //action
    private int lastY;

    private BaseHeaderAdapter mBaseHeaderAdapter;
    private BaseFooterAdapter mBaseFooterAdapter;
    private OnHeaderRefreshListener mOnHeaderRefreshListener;
    private OnFooterRefreshListener mOnFooterRefreshListener;

    private Context mContext;

    public PullRefreshView(Context context) {
        this(context, null);
    }

    public PullRefreshView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //设置为垂直布局，避免每次再xml文件中修改（LinearLayout 默认为horizontal）
        setOrientation(VERTICAL);
        mContext = context;
    }

    public void setBaseHeaderAdapter() {
        this.setBaseHeaderAdapter(new InitBaseHeaderAdapter(mContext));
    }

    public void setBaseHeaderAdapter(BaseHeaderAdapter baseHeaderAdapter) {
        mBaseHeaderAdapter = baseHeaderAdapter;
        initHeaderView();
        initChildViewType();
    }

    public void setBaseFooterAdapter() {
        this.setBaseFooterAdapter(new InitBaseFooterAdapter(mContext));
    }

    public void setBaseFooterAdapter(BaseFooterAdapter baseFooterAdapter) {
        mBaseFooterAdapter = baseFooterAdapter;
        initFooterView();
    }

    /**
     * 计算顶部view高度，将其隐藏
     */
    private void initHeaderView() {
        mHeaderView = mBaseHeaderAdapter.getHeaderView();
        MeasureTools.measureView(mHeaderView);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderViewHeight);
        params.topMargin = -mHeaderViewHeight;
        //在linearlayout中使用addView的时候，如果linearlayout方向是vertical 垂直， index代表添加的child的view在linearlayout的行数，
        // index是0，表示添加的child在linearlayout顶部，-1为底部
        addView(mHeaderView, 0, params);
    }

    /**
     * 计算底部view高度，将其隐藏
     */
    private void initFooterView() {
        mFooterView = mBaseFooterAdapter.getFooterView();
        MeasureTools.measureView(mFooterView);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mFooterViewHeight);
        addView(mFooterView, params);

    }

    /**
     * 确定PullView 内部子视图类型
     */
    private void initChildViewType() {
        int count = getChildCount();

        Log.e(TAG,"PullView child number is "+count);
        if (count < 2) {
            return;
        }
        View view = getChildAt(1);
        mRecyclerView = (RecyclerView) view;

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int y = (int) ev.getRawY();//getRawX()、getRawY()返回的是触摸点相对于屏幕的位置，
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetY = y - lastY;
                if (isParentViewScroll(offsetY)) {
                    Log.e(TAG, "onInterceptTouchEvent: belong to ParentView");
                    return true; //此时,触发onTouchEvent事件
                }
                break;
            default:
                break;

        }
        return false;
    }

    /**
     * 滑动由父View（当前view）处理
     *
     * @param offsetY
     * @return
     */
    private boolean isParentViewScroll(int offsetY) {
        boolean belongToParentView = false;
        if (mHeaderState == REFRESHING) {
            belongToParentView = false;
        }
        if (mRecyclerView != null) {
            if (offsetY > 0) { //下拉
                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    belongToParentView = false;
                }
                LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                int firstPosition = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();//界面显示的第一个item的position

                if (firstPosition == 0) {
                    mPullState = PULL_DOWN_STATE;
                    belongToParentView = true;
                }
            } else if (offsetY < 0) {//上拉
                View child = mRecyclerView.getChildAt(0);
                if (child == null) {
                    belongToParentView = false;
                }
                //computeVerticalScrollExtent() //显示区域的高度
                //computeVerticalScrollOffset() //已经向下滚动的距离，为0时表示已处于顶部
                //computeVerticalScrollRange() //整体的高度，注意是整体，包括在显示区域之外的
                if (mRecyclerView.computeVerticalScrollExtent() + mRecyclerView.computeVerticalScrollOffset() >=
                        mRecyclerView.computeVerticalScrollRange()) {
                    belongToParentView = true;
                    mPullState = PULL_UP_STATE;
                } else {
                    belongToParentView = false;
                }
            }
        }
        return belongToParentView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int offsetY = y - lastY;
                if (mPullState == PULL_DOWN_STATE) {
                    Log.e(TAG, "onTouchEvent: pull down begin-->" + offsetY);
                    initHeaderViewToRefresh(offsetY);
                } else if (mPullState == PULL_UP_STATE) {
//                    Log.e(TAG, "onTouchEvent: pull up begin-->" + offsetY);
                    initFooterViewRefresh(offsetY);
                }
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int topMargin = getHeaderTopMargin();
                Log.e(TAG, "onTouchEvent: topMargin==" + topMargin);
                if (mPullState == PULL_DOWN_STATE) {
                    if (topMargin >= 0) {
                        headerRefreshing();
                    } else {
                        reSetHeaderTopMargin(-mHeaderViewHeight);
                    }
                } else if (mPullState == PULL_UP_STATE) {
                    if (Math.abs(topMargin) >= mHeaderViewHeight + mFooterViewHeight) {
                        footerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        reSetHeaderTopMargin(-mHeaderViewHeight);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 计算下拉刷新相关
     *
     * @param offsetY
     */
    private void initHeaderViewToRefresh(int offsetY) {
        if (mBaseHeaderAdapter == null) {
            return;
        }
        int topDistance = updateHeadViewMarginTop(offsetY);
        if (topDistance < 0 && topDistance > -mHeaderViewHeight) {
            mBaseHeaderAdapter.pullViewToRefresh(offsetY);
            mHeaderState = PULL_TO_REFRESH;
        } else if (topDistance > 0 && mHeaderState != RELEASE_TO_REFRESH) {
            mBaseHeaderAdapter.releaseViewToRefresh(offsetY);
            mHeaderState = RELEASE_TO_REFRESH;
        }
    }

    private int updateHeadViewMarginTop(int offsetY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        float topMargin = params.topMargin + offsetY * 0.3f;
        params.topMargin = (int) topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    public void headerRefreshing() {
        if (mBaseHeaderAdapter == null) {
            return;
        }
        mHeaderState = REFRESHING;
        setHeaderTopMargin(0);
        mBaseHeaderAdapter.headerRefreshing();
        if (mOnHeaderRefreshListener != null) {
            mOnHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    private void initFooterViewRefresh(int offsetY) {
        if (mBaseFooterAdapter == null) {
            return;
        }
        int topDistance = updateHeadViewMarginTop(offsetY);

        // 如果header view topMargin 的绝对值大于或等于(header + footer) 四分之一 的高度
        // 说明footer view 完全显示出来了，修改footer view 的提示状态
        if (Math.abs(topDistance) >= (mHeaderViewHeight + mFooterViewHeight) / 4 &&
                mFooterState != RELEASE_TO_REFRESH) {
            mBaseFooterAdapter.pullViewToRefresh(offsetY);
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(topDistance) < (mHeaderViewHeight + mFooterViewHeight) / 4) {
            mBaseFooterAdapter.releaseViewToRefresh(offsetY);
            mFooterState = PULL_TO_REFRESH;
        }
    }

    public void footerRefreshing() {
        if (mBaseFooterAdapter == null) {
            return;
        }
        mFooterState = REFRESHING;
        int top = mHeaderViewHeight + mFooterViewHeight;
        setHeaderTopMargin(-top);
        mBaseFooterAdapter.footerRefreshing();
        if (mOnFooterRefreshListener != null) {
            mOnFooterRefreshListener.onFooterRefresh(this);
        }
    }

    public void onHeaderRefreshComplete() {
        if (mBaseHeaderAdapter == null) {
            return;
        }
        setHeaderTopMargin(-mHeaderViewHeight);
        mBaseHeaderAdapter.headerRefreshComplete();
        mHeaderState = PULL_TO_REFRESH;
    }

    public void onFooterRefreshComplete() {
        if (mBaseFooterAdapter == null) {
            return;
        }
        setHeaderTopMargin(-mHeaderViewHeight);
        mBaseFooterAdapter.footerRefreshComplete();
        mFooterState = PULL_TO_REFRESH;
    }

    /**
     * 获取header view 的topMargin
     *
     * @return
     */
    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        return params.topMargin;
    }

    /**
     * 设置header view的topMargin的值
     *
     * @param topMargin，为0时 说明header view 刚好完全显示出来；为-mHeaderViewHeight时，说明完全隐藏了
     */
    private void setHeaderTopMargin(int topMargin) {
        smoothMargin(topMargin);
    }

    /**
     * 上拉或下拉至一半时，放弃下来，视为完成一次下拉统一处理，初始化所有内容
     *
     * @param topMargin
     */
    private void reSetHeaderTopMargin(int topMargin) {
        if (mBaseHeaderAdapter != null) {
            mBaseHeaderAdapter.headerRefreshComplete();
        }
        if (mBaseFooterAdapter != null) {
            mBaseFooterAdapter.footerRefreshComplete();
        }
        smoothMargin(topMargin);
    }

    /**
     * 平滑设置header view 的topMargin
     *
     * @param topMargin
     */
    private void smoothMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(params.topMargin, topMargin);
        animator.setDuration(animDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderViewHeight);
                lp.topMargin = (int) animation.getAnimatedValue();
                mHeaderView.setLayoutParams(lp);
            }
        });
        animator.start();
    }

    public void setOnHeaderRefreshListener(OnHeaderRefreshListener onHeaderRefreshListener) {
        this.mOnHeaderRefreshListener = onHeaderRefreshListener;
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener onFooterRefreshListener) {
        this.mOnFooterRefreshListener = onFooterRefreshListener;
    }
}
