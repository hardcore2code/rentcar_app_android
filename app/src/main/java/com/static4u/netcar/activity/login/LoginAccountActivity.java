package com.static4u.netcar.activity.login;

import android.os.Bundle;
import android.view.View;

import com.static4u.netcar.R;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.business.SharedData;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.ui.MyEditText;
import com.static4u.netcar.utils.network.HttpClientUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginAccountActivity extends BaseActivity {

    @Bind(R.id.et_phone)
    MyEditText etPhone;
    @Bind(R.id.et_pwd)
    MyEditText etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_account);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        setStatusBarColor(R.color.color_yellow);
    }

    public void clickGo(View view) {
        // 登录
        final String phone = etPhone.getText().toString().trim();
        final String pwd = etPwd.getText().toString().trim();

        if (phone.length() < 11) {
            showToast("请输入11位手机号");
            return;
        }
        if (pwd.length() < 1) {
            showToast("请输入密码");
            return;
        }

        progressView.setVisibility(View.VISIBLE);
        // TODO: 2019/1/8
        HttpClientUtil util = new HttpClientUtil(new HttpClientUtil.OnHttpListener() {
            @Override
            public void onSuccess(String responseStr) {
                progressView.setVisibility(View.GONE);

                SharedData.setUserData(LoginAccountActivity.this, phone, pwd, "张三", "https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=8172ae52d31373f0e13267cdc566209e/d52a2834349b033b0b84caf317ce36d3d539bd8e.jpg");

                loginSucAndGoActivity();
            }

            @Override
            public void onFailure() {
                progressView.setVisibility(View.GONE);
                showToast("登录失败");

                SharedData.setUserData(LoginAccountActivity.this, phone, pwd, "张三", "https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=8172ae52d31373f0e13267cdc566209e/d52a2834349b033b0b84caf317ce36d3d539bd8e.jpg");

                loginSucAndGoActivity();
            }
        });
        util.postHttpClient(URLConstant.HOST_FUNC, "");
    }
}
