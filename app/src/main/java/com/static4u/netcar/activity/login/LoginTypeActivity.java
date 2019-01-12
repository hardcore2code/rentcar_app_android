package com.static4u.netcar.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.static4u.netcar.R;
import com.static4u.netcar.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginTypeActivity extends BaseActivity {

    @Bind(R.id.tv_account)
    TextView tvAccount;
    @Bind(R.id.tv_wechat)
    TextView tvWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login_type);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        setStatusBarColor(R.color.color_yellow);

        tvAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithAccount();
            }
        });

        tvWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("敬请期待");
            }
        });
    }

    private void loginWithAccount() {
        // 帐号密码登入
        finish();

        Intent it = new Intent(this, LoginAccountActivity.class);
        it.putExtra("class", getIntent().getStringExtra("class"));
        myStartActivity(it);
    }
}
