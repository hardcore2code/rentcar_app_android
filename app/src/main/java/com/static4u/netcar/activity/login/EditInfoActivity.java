package com.static4u.netcar.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.static4u.netcar.R;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.business.SharedData;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.utils.network.HttpClientUtil;

public class EditInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_info);
        super.onCreate(savedInstanceState);

    }

    private void submit() {
        progressView.setVisibility(View.VISIBLE);

        // TODO: 2019/1/8
        HttpClientUtil util = new HttpClientUtil(new HttpClientUtil.OnHttpListener() {
            @Override
            public void onSuccess(String responseStr) {
                progressView.setVisibility(View.GONE);

                SharedData.setUserData(EditInfoActivity.this, "", "", "", "");

                Intent it = new Intent();
                setResult(RESULT_OK, it.putExtra("login", true));
                finish();
            }

            @Override
            public void onFailure() {
                progressView.setVisibility(View.GONE);
                showToast("保存失败");

                Intent it = new Intent();
                setResult(RESULT_OK, it.putExtra("login", true));
                finish();
            }
        });
        util.postHttpClient(URLConstant.HOST_FUNC, "");
    }
}
