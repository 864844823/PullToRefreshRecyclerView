package com.example.duhuihui.pulltorefreshrecyclerview;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.duhuihui.pulltorefreshrecyclerview.adapter.MyAdapter;
import com.example.duhuihui.pulltorefreshrecyclerview.view.PulltoRefreshRecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PulltoRefreshRecyclerView.RefreshLoadMoreListener {

    public PulltoRefreshRecyclerView refreshRecyclerView;
    public ArrayList<String> list;
    public MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
        initData();
        refreshRecyclerView = (PulltoRefreshRecyclerView) findViewById(R.id.ptr_rv);
        refreshRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        refreshRecyclerView.setRefreshLoadMoreListener(this);
        adapter = new MyAdapter(list);
        //扫地
        //sdfsadf
        refreshRecyclerView.setAdapter(adapter);
    }

    private void initData() {
        int size = list.size();
        for (int i = size; i < 40 + size; i++) {
            list.add("当前条目是：" + i);
        }
    }

    @Override
    public void onRefresh() {
        list.clear();
        initData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshRecyclerView.stopRefresh();
                        System.out.println("下拉刷新:size=" + list.size());

                    }
                });
            }
        }).start();
    }

    @Override
    public void onLoadMore() {
        final int size = list.size();
        initData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                //dd
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshRecyclerView.setLoadMoreCompleted(size);
                        System.out.println("加载更多:size=" + list.size());

                    }
                });
            }
        }).start();

    }
}
