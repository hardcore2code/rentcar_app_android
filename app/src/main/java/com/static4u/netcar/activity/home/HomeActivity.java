package com.static4u.netcar.activity.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.static4u.netcar.R;
import com.static4u.netcar.base.BaseListActivity;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.model.CarInfo;
import com.static4u.netcar.utils.SLog;
import com.static4u.netcar.utils.network.HttpClientUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends BaseListActivity {

    @Bind(R.id.lv)
    PullToRefreshListView listView;
    private HomeAdapter adapter;
    private List<CarInfo> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        initView();
        loadData();
    }

    private void initView() {
        View header = LayoutInflater.from(this).inflate(R.layout.view_header_home, null);
        initListView(listView, header);
    }

    private void loadData() {
        pageParam = 1;
        requestDataInPage(pageParam, false);
    }

    @Override
    public void requestDataInPage(final int page, final boolean isRefresh) {
        if (page == 1 && !isRefresh) {
            progressView.setVisibility(View.VISIBLE);
        }

        HttpClientUtil util = new HttpClientUtil(new HttpClientUtil.OnHttpListener() {
            @Override
            public void onSuccess(String responseStr) {
                listView.onRefreshComplete();
                progressView.setVisibility(View.GONE);

                showList();
            }

            @Override
            public void onFailure() {
                listView.onRefreshComplete();
                progressView.setVisibility(View.GONE);

                if (isRefresh) {
                    showToast("刷新失败");
                } else {
                    if (pageParam == 1) {
                        showList();
                    } else {
                        tvFooterReload.setVisibility(View.VISIBLE);
                    }
                }
                SLog.i("首页列表查询失败，稍后重试");
            }
        });
        util.postHttpClient(URLConstant.HOST_FUNC, "");
    }

    private void showList() {
        // TODO: 2019/1/7
        data = getTestData(4);


        if (adapter == null) {
            adapter = new HomeAdapter(this, data);
            listView.setAdapter(adapter);
        } else {
            adapter.update(data);
        }
    }

}
