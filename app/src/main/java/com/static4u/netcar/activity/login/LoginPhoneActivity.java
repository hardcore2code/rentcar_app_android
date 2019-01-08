package com.static4u.netcar.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.static4u.netcar.R;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.ui.MyEditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginPhoneActivity extends BaseActivity {

    @Bind(R.id.et_phone)
    MyEditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        setStatusBarColor(R.color.color_yellow);
    }

    public void clickGo(View view) {
        // 下一步
        String phone = etPhone.getText().toString().trim();
        if (phone.length() < 11) {
            showToast("请输入11位手机号");
            return;
        }

        Intent it = new Intent(this, SendCodeActivity.class);
        it.putExtra("phone", phone);
        myStartActivity(it);
    }

    public void clickOther(View view) {
        // 更多方式登入
        myStartActivity(new Intent(this, LoginTypeActivity.class));
    }
}
