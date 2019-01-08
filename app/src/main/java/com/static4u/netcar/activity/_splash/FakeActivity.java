package com.static4u.netcar.activity._splash;

import android.content.Intent;
import android.os.Bundle;

import com.static4u.netcar.base.BaseActivity;

public class FakeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myStartActivity(new Intent(this, SplashActivity.class));
        finish();
    }
}
