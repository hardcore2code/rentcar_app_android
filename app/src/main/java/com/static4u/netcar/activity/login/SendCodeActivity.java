package com.static4u.netcar.activity.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.static4u.netcar.R;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.business.SharedData;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.ui.MyEditText;
import com.static4u.netcar.utils.network.HttpClientUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SendCodeActivity extends BaseActivity {

    @Bind(R.id.tv_send)
    TextView tvCode;
    @Bind(R.id.et_code)
    MyEditText etCode;
    @Bind(R.id.tv_h2)
    TextView tvH2;

    private String phone;

    private final int CODE_PERIOD = 120;
    private int time = CODE_PERIOD;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_send_code);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        setStatusBarColor(R.color.color_yellow);

        phone = getIntent().getStringExtra("phone");

        tvH2.setText(getString(R.string.send_code, phone));

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String code = etCode.getText().toString().trim();
                if (code.length() == 6) {
                    submit(code);
                }
            }
        });

        sendCode();
    }

    private void submit(String code) {
        progressView.setVisibility(View.VISIBLE);

        // TODO: 2019/1/8
        HttpClientUtil util = new HttpClientUtil(new HttpClientUtil.OnHttpListener() {
            @Override
            public void onSuccess(String responseStr) {
                progressView.setVisibility(View.GONE);
                SharedData.setUserData(SendCodeActivity.this, phone, "12", "张三", "https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=8172ae52d31373f0e13267cdc566209e/d52a2834349b033b0b84caf317ce36d3d539bd8e.jpg");

                loginSucAndGoActivity();
            }

            @Override
            public void onFailure() {
                progressView.setVisibility(View.GONE);
                showToast("登录失败");

                // TODO: 2019/1/8
                SharedData.setUserData(SendCodeActivity.this, phone, "12", "张三", "https://gss0.bdstatic.com/94o3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike92%2C5%2C5%2C92%2C30/sign=8172ae52d31373f0e13267cdc566209e/d52a2834349b033b0b84caf317ce36d3d539bd8e.jpg");

                loginSucAndGoActivity();
            }
        });
        util.postHttpClient(URLConstant.HOST_FUNC, code);
    }

    @Override
    protected void onDestroy() {
        stopTimer();
        super.onDestroy();
    }

    public void clickSend(View view) {
        // 重新获取验证码
        sendCode();
    }

    private void sendCode() {
        progressView.setVisibility(View.VISIBLE);

        // TODO: 2019/1/8
        HttpClientUtil util = new HttpClientUtil(new HttpClientUtil.OnHttpListener() {
            @Override
            public void onSuccess(String responseStr) {
                progressView.setVisibility(View.GONE);
                refreshBtnCode(true);
                startTimer();
            }

            @Override
            public void onFailure() {
                progressView.setVisibility(View.GONE);
                showToast("验证码发送失败");
                refreshBtnCode(false);
            }
        });
        util.postHttpClient(URLConstant.HOST_FUNC, phone);
    }

    /**
     * 刷新获取验证码按钮
     */
    private void refreshBtnCode(boolean isSuccess) {
        if (isSuccess) {
            tvCode.setEnabled(false);
            tvCode.setTextColor(getResourcesColor(R.color.font_gray));
            tvCode.setText(String.valueOf(time + "s"));
        } else {
            tvCode.setTextColor(getResourcesColor(R.color.white));
            tvCode.setEnabled(true);
            tvCode.setText("重新获取");
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (time > 0) {
                refreshBtnCode(true);
            } else {
                refreshBtnCode(false);
            }
        }
    };

    private void startTimer() {
        tvCode.setText(String.valueOf(CODE_PERIOD + "s"));
        time = CODE_PERIOD;

        stopTimer();
        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                time--;
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(task, 1000, 1000);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
