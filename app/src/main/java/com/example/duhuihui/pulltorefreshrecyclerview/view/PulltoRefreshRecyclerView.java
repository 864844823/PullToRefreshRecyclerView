package com.example.duhuihui.pulltorefreshrecyclerview.view;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duhuihui.pulltorefreshrecyclerview.R;


/**
 * Created by dhh on 16-10-15
 */
public class PulltoRefreshRecyclerView extends LinearLayout {
    /**
     * 垂直方向
     */
    static final int VERTICAL = LinearLayoutManager.VERTICAL;
    /**
     * 水平方向
     */
    static final int HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    /**
     * 内容控件
     */
    private RecyclerView recyclerView;
    /**
     * 刷新布局控件
     */
    private SwipeRefreshLayout swipeRfl;
    /*
     * 刷新布局的监听
     */
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    /**
     * 内容控件滚动监听
     */
    private RecyclerView.OnScrollListener mScrollListener;
    /**
     * 内容适配器
     */
    private RecyclerView.Adapter mAdapter;
    /**
     * 刷新加载监听事件
     */
    private RefreshLoadMoreListener mRefreshLoadMoreListner;
    /**
     * 是否可以加载更多
     */
    private boolean hasMore = true;
    /**
     * 是否正在刷新
     */
    private boolean isRefresh = false;
    /**
     * 是否正在加载更多
     */
    private boolean isLoadMore = false;
    private RecyclerView.LayoutManager layoutManager = null;
    public TextView textView;
    private RecyclerView.Adapter adapter;

    public PulltoRefreshRecyclerView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    //    @SuppressWarnings("deprecation")
    public PulltoRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(
                R.layout.recyclerview_pull_to_refersh, this, true);
        swipeRfl = (SwipeRefreshLayout) findViewById(R.id.srl_pull_to_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.rcv_pull_to_refresh);
        textView = (TextView) findViewById(R.id.tv_more);
        // 加载颜色是循环播放的，只要没有完成刷新就会一直循环，color1>color2>color3>color4
        swipeRfl.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE,
                Color.YELLOW);

        /**
         * 监听上拉至底部滚动监听
         */
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //最后显示的项
                int lastVisibleItem = getLastVisiblePosition();
                int totalItemCount = layoutManager.getItemCount();
                // lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
//                if (hasMore && (lastVisibleItem >= totalItemCount - 1)
//                        && dy > 0 && !isLoadMore) {
//                    isLoadMore = true;
//                    loadMore();
//                }
//                if(lastVisibleItem !=totalItemCount) {
//                    textView.setVisibility(INVISIBLE);
//                }
                /**
                 * 无论水平还是垂直
                 */
                if (hasMore && (lastVisibleItem >= totalItemCount - 1)
                        && !isLoadMore) {
                    isLoadMore = true;
                    loadMore();
                }

            }
        };

        /**
         * 下拉至顶部刷新监听
         */
        mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!isRefresh) {
                    isRefresh = true;
                    refresh();
                }
            }
        };
        swipeRfl.setOnRefreshListener(mRefreshListener);
        recyclerView.setHasFixedSize(true);
        //设置一个默认的LinearLayoutManager
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(mScrollListener);
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    private int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    public void setPullLoadMoreEnable(boolean enable) {
        this.hasMore = enable;
    }

    public boolean getPullLoadMoreEnable() {
        return hasMore;
    }

    public void setPullRefreshEnable(boolean enable) {
        swipeRfl.setEnabled(enable);
    }

    public boolean getPullRefreshEnable() {
        return swipeRfl.isEnabled();
    }

    public void setLoadMoreListener() {
        recyclerView.addOnScrollListener(mScrollListener);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        recyclerView.setLayoutManager(layoutManager);
    }

    public void loadMore() {
        if (mRefreshLoadMoreListner != null && hasMore) {
            mRefreshLoadMoreListner.onLoadMore();
            textView.setVisibility(VISIBLE);
            swipeRfl.setRefreshing(true);
        }
    }

    /**
     * 加载更多完毕,为防止频繁网络请求,isLoadMore为false才可再次请求更多数据
     *
     * 将RecyclerView的可见条目移动到刷新出的了第一个条目上
     * @param position
     */
    public void setLoadMoreCompleted(int position) {
        isLoadMore = false;
        swipeRfl.setRefreshing(false);
        textView.setVisibility(INVISIBLE);
        if(position==adapter.getItemCount()) {
            Toast.makeText(getContext(),"没有更多数据了！", Toast.LENGTH_SHORT).show();
            setPullLoadMoreEnable(false);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(position);
        }
    }

    public void stopRefresh() {
        isRefresh = false;
        swipeRfl.setRefreshing(false);
        setPullLoadMoreEnable(true);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setRefreshLoadMoreListener(RefreshLoadMoreListener listener) {
        mRefreshLoadMoreListner = listener;
    }

    public void refresh() {
        if (mRefreshLoadMoreListner != null) {
            mRefreshLoadMoreListner.onRefresh();
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        if (adapter != null)
            recyclerView.setAdapter(adapter);
    }

    public interface RefreshLoadMoreListener {
        public void onRefresh();

        public void onLoadMore();
    }
}
