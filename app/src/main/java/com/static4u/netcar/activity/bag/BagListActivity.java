package com.static4u.netcar.activity.bag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.static4u.netcar.R;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.base.BaseListActivity;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.model.BagInfo;
import com.static4u.netcar.model.CarInfo;
import com.static4u.netcar.utils.SLog;
import com.static4u.netcar.utils.network.HttpClientUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BagListActivity extends BaseListActivity {
    public static final int REQUEST_CODE_BAG = 103;

    @Bind(R.id.lv)
    PullToRefreshListView listView;
    private BagAdapter adapter;
    private List<BagInfo> data;

    private CarInfo car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bag_list);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        initView();
        loadData();
    }

    private void initView() {
        initListView(listView, null);
    }

    private void loadData() {
        car = (CarInfo) getIntent().getSerializableExtra("car");
        if (car == null) {
            showToast("车型信息异常，请稍后重试");
            clickBack(listView);
            return;
        }
        pageParam = 1;
        requestDataInPage(pageParam, false);
    }

    public static void startActivityForResult(BaseActivity activity, CarInfo info) {
        Intent it = new Intent(activity, BagListActivity.class);
        it.putExtra("car", info);
        activity.myStartActivityForResult(it, REQUEST_CODE_BAG);
        activity.overridePendingTransition(R.anim.bottom_in, R.anim.push_stay);
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
        data = getTestBag();

        if (adapter == null) {
            adapter = new BagAdapter(this, data);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i < data.size()) {
                        Intent it = getIntent();
                        it.putExtra("bag", data.get(i));
                        setResult(RESULT_OK, it);
                        finish();
                        overridePendingTransition(R.anim.push_stay, R.anim.bottom_out);
                    }
                }
            });
        } else {
            adapter.update(data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_stay, R.anim.bottom_out);
    }
}
