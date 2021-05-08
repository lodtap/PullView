# PullView
自定义上拉加载下拉刷新
1.继承BaseHeaderAdapter、BaseFooterAdapter 可以替换自定义的header布局和footer布局
2.使用方式
布局中添加
<com.clg.pullrefreshlibrary.PullRefreshView
        android:id="@+id/pull_view"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="@id/sample_text"
        app:layout_constraintBottom_toBottomOf="parent"
        >
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:overScrollMode="never"
            />
    </com.clg.pullrefreshlibrary.PullRefreshView>
    
    onCreate()中的使用
    recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,RecyclerView.VERTICAL));
        recyclerView.setAdapter(new MyAdapter());
        pullView = findViewById(R.id.pull_view);
        pullView.setBaseHeaderAdapter(new TraditionHeaderAdapter(this));
        pullView.setBaseFooterAdapter(new TraditionFooterAdapter(this));

        pullView.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullRefreshView view) {

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.onHeaderRefreshComplete();
                    }
                },2000);
            }
        });
        pullView.setOnFooterRefreshListener(view -> {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.onFooterRefreshComplete();
                }
            },800);
        });
        pullView.headerRefreshing();
