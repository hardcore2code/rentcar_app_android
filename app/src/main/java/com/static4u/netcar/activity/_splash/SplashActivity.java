package com.static4u.netcar.activity._splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.static4u.netcar.R;
import com.static4u.netcar.activity.home.HomeActivity;
import com.static4u.netcar.base.BaseActivity;


public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        super.onCreate(savedInstanceState);

//        SharedData.setUserData(this, "", "", "", "");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goHome();
            }
        }, 1000);
    }


    private void goHome() {
        myStartActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.push_stay);
    }

}
