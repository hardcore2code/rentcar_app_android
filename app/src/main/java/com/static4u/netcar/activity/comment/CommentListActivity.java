package com.static4u.netcar.activity.comment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.static4u.netcar.R;
import com.static4u.netcar.activity.login.LoginPhoneActivity;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.base.BaseListActivity;
import com.static4u.netcar.business.SharedData;
import com.static4u.netcar.constant.GlobalConstant;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.model.CarInfo;
import com.static4u.netcar.model.CommentInfo;
import com.static4u.netcar.ui.MyEditText;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.SLog;
import com.static4u.netcar.utils.network.HttpClientUtil;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentListActivity extends BaseListActivity {

    @Bind(R.id.lv)
    PullToRefreshListView listView;
    @Bind(R.id.et_comment)
    MyEditText etComment;
    private CommentAdapter adapter;
    private List<CommentInfo> data;

    private CarInfo car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_comment_list);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        initView();
        loadData();
    }

    private void initView() {
        initListView(listView, null);
        setEmptyView(R.drawable.ic_empty, "还没有评价，去抢沙发吧", "", null);
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

    public static void startActivity(BaseActivity activity, CarInfo info) {
        Intent it = new Intent(activity, CommentListActivity.class);
        it.putExtra("car", info);
        activity.myStartActivity(it);
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
        data = car.getCommList();

        if (data.size() < 1) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        if (adapter == null) {
            adapter = new CommentAdapter(this, data);
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

    /**
     * 提交评价
     */
    public void clickSubmit(View view) {
        if (CommonUtil.isEmptyOrNull(SharedData.getUserPhone(this))) {
            showToast("请先登录");

            Intent it = new Intent(this, LoginPhoneActivity.class);
            it.putExtra("class", "CommentListActivity");
            myStartActivity(it);
            return;
        }

        String comment = etComment.getText().toString().trim();
        if (CommonUtil.isEmptyOrNull(comment)) {
            showToast("请先输入评价");
            return;
        }

        car.getCommList().add(new CommentInfo(SharedData.getUserName(this), CommonUtil.dateFormat(new Date(), "yyyy-MM-dd HH:mm:ss"), SharedData.getStr(this, SharedData.KEY_HEADER), comment));
        pageParam = 1;
        requestDataInPage(pageParam, false);
        GlobalConstant.addComment = true;

        etComment.setText("");
        clickView(etComment);
        showToast("评价成功");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_stay, R.anim.bottom_out);
    }
}
