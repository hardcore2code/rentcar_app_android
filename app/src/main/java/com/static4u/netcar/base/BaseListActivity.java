package com.static4u.netcar.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.static4u.netcar.R;

public class BaseListActivity extends BaseActivity {

    // 请求当前页数
    protected int pageParam = 1;
    // 总页数
    protected int totalPage = 1;

    private int lastListItem = 1;

    private View loadFooter;
    private LinearLayout mLlFooterLoading;
    protected TextView tvFooterNoData;
    protected TextView tvFooterReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void requestDataInPage(final int page, final boolean isRefresh) {
    }

    /**
     * 初始化列表View
     */
    protected void initListView(PullToRefreshListView listView, View header) {
        setEmptyView(R.drawable.ic_empty, getString(R.string.empty), getString(R.string.try_again), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageParam = 1;
                requestDataInPage(1, false);
            }
        });

        loadFooter = LayoutInflater.from(this).inflate(R.layout.listview_footer, null);
        if (loadFooter != null) {
            mLlFooterLoading = loadFooter.findViewById(R.id.list_footer_loading);
            tvFooterNoData = loadFooter.findViewById(R.id.tv_list_footer_no_data);
            tvFooterReload = loadFooter.findViewById(R.id.tv_list_footer_reload);
            tvFooterReload.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    requestDataInPage(pageParam, false);
                }
            });
            mLlFooterLoading.setVisibility(View.GONE);
            tvFooterNoData.setVisibility(View.VISIBLE);
            tvFooterReload.setVisibility(View.GONE);
        }
        ;
        if (header != null) {
            listView.getRefreshableView().addHeaderView(header);
        }
        listView.getRefreshableView().addFooterView(loadFooter);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                pageParam = 1;
                requestDataInPage(1, true);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount == firstVisibleItem + visibleItemCount) {
                    if (totalItemCount != lastListItem) {
                        lastListItem = totalItemCount;
                        if (firstVisibleItem == 0 && !(pageParam < totalPage)) {
                            loadFooter.setVisibility(View.GONE);
                        } else {
                            loadFooter.setVisibility(View.VISIBLE);
                            if (pageParam < totalPage) {
                                mLlFooterLoading.setVisibility(View.VISIBLE);
                                tvFooterNoData.setVisibility(View.GONE);
                                tvFooterReload.setVisibility(View.GONE);
                            } else {
                                mLlFooterLoading.setVisibility(View.GONE);
                                tvFooterNoData.setVisibility(View.VISIBLE);
                                tvFooterReload.setVisibility(View.GONE);
                            }
                        }
                        if (pageParam < totalPage) {
                            requestDataInPage(++pageParam, false);
                        }
                    }
                }
            }
        });
    }
}
