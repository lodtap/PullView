package com.clg.pullview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clg.pullrefreshlibrary.PullRefreshView;
import com.clg.pullrefreshlibrary.interfaces.OnHeaderRefreshListener;
import com.clg.pullview.adapter.TraditionFooterAdapter;
import com.clg.pullview.adapter.TraditionHeaderAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    PullRefreshView pullView;
    RecyclerView recyclerView;
    private List<String> datas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        datas = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            datas.add("This is item "+i);
        }
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
//                        for(int i = 10; i < 20; i++){
//                            datas.add("This is item "+i);
//                        }
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
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder>{

        @NonNull
        @Override
        public MyAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,null,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyHolder holder, int position) {
            holder.mTextView.setText(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView mTextView;
            public MyHolder(@NonNull View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}