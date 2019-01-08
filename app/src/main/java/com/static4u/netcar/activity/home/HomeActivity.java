package com.static4u.netcar.activity.home;

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
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.model.CarInfo;
import com.static4u.netcar.utils.SLog;
import com.static4u.netcar.utils.network.HttpClientUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity {

    @Bind(R.id.lv)
    PullToRefreshListView listView;
    private HomeAdapter adapter;
    private List<CarInfo> data;

    // 请求当前页数
    protected int pageParam = 1;
    // 总页数
    protected int totalPage = 1;

    private int lastListItem = 1;
    private View loadFooter;
    private LinearLayout mLlFooterLoading;
    private TextView tvFooterNoData;
    private TextView tvFooterReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        initView();
        requestDataInPage(1, false);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setEmptyView(R.drawable.ic_empty, getString(R.string.empty), getString(R.string.try_again), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageParam = 1;
                requestDataInPage(1, false);
            }
        });

        loadFooter = LayoutInflater.from(this).inflate(R.layout.listview_footer, null);
        if (loadFooter != null) {
            mLlFooterLoading = (LinearLayout) loadFooter.findViewById(R.id.list_footer_loading);
            tvFooterNoData = (TextView) loadFooter.findViewById(R.id.tv_list_footer_no_data);
            tvFooterReload = (TextView) loadFooter.findViewById(R.id.tv_list_footer_reload);
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
        View header = LayoutInflater.from(this).inflate(R.layout.view_header_home, null);
        listView.getRefreshableView().addHeaderView(header);
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

    private void requestDataInPage(final int page, final boolean isRefresh) {
        if (page == 1 && !isRefresh) {
            progressView.setVisibility(View.VISIBLE);
        }

        HttpClientUtil util = new HttpClientUtil(new HttpClientUtil.OnHttpListener() {
            @Override
            public void onSuccess(String responseStr) {
                listView.onRefreshComplete();
                progressView.setVisibility(View.GONE);

                loadData();
            }

            @Override
            public void onFailure() {
                listView.onRefreshComplete();
                progressView.setVisibility(View.GONE);

                if (isRefresh) {
                    showToast("刷新失败");
                } else {
                    if (pageParam == 1) {
                        loadData();
                    } else {
                        tvFooterReload.setVisibility(View.VISIBLE);
                    }
                }
                SLog.i("首页列表查询失败，稍后重试");
            }
        });
        util.postHttpClient(URLConstant.HOST_FUNC, "");
    }

    private void loadData() {
        // TODO: 2019/1/7
        data = new ArrayList<>();

        List<String> imgs = new ArrayList<>();
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114328-1471319236.jpg");
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114326-558979764.jpg");
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114328-903976311.jpg");
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114329-723161299.jpg");
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114549-682156056.jpg");

        for (int i = 0; i < 3; i++) {
            CarInfo car = new CarInfo();
            car.setName("新能源物流车" + i);
            car.setCompany("中国恒大" + i);
            car.setType("新能源");
            car.setPrice("1000");
            car.setPriceOld("1800");
            car.setSub("每月 - 免费保养 提供行驶证");


            List<String> imgList = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                imgList.add(imgs.get(j % imgs.size()));
            }
            car.setImgList(imgList);

            data.add(car);
        }


        if (adapter == null) {
            adapter = new HomeAdapter(this, data);
            listView.setAdapter(adapter);
        } else {
            adapter.update(data);
        }
    }
}
