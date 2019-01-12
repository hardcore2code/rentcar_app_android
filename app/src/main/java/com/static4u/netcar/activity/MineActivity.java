package com.static4u.netcar.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.static4u.netcar.R;
import com.static4u.netcar.activity.login.EditInfoActivity;
import com.static4u.netcar.activity.login.LoginPhoneActivity;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.business.SharedData;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.image.ImageLoadUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MineActivity extends BaseActivity {
    public static final int REQUEST_CODE_MINE = 101;

    @Bind(R.id.iv_header)
    ImageView ivHeader;
    @Bind(R.id.tv_header_tip)
    TextView tvHeaderTip;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.rl_info)
    RelativeLayout rlInfo;

    @Bind(R.id.tv_step)
    TextView tvStep;
    @Bind(R.id.step_1)
    View step1;
    @Bind(R.id.step_2)
    View step2;
    @Bind(R.id.step_3)
    View step3;
    @Bind(R.id.step_4)
    View step4;
    @Bind(R.id.ll_step)
    LinearLayout llStep;

    @Bind(R.id.rl_account)
    RelativeLayout rlAccount;
    @Bind(R.id.rl_order)
    RelativeLayout rlOrder;
    @Bind(R.id.rl_coupon)
    RelativeLayout rlCoupon;
    @Bind(R.id.rl_id)
    RelativeLayout rlId;
    @Bind(R.id.rl_readme)
    RelativeLayout rlReadme;
    @Bind(R.id.rl_line)
    RelativeLayout rlLine;
    @Bind(R.id.rl_friends)
    RelativeLayout rlFriends;
    @Bind(R.id.rl_about)
    RelativeLayout rlAbout;
    @Bind(R.id.rl_pwd)
    RelativeLayout rlPwd;
    @Bind(R.id.rl_phone)
    RelativeLayout rlPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_MINE) {
            // 刷新用户信息
            loadData();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getBooleanExtra("login", false)) {
            // 刷新用户信息
            loadData();
        }
    }

    private void loadData() {
        if (CommonUtil.isEmptyOrNull(SharedData.getUserPhone(this))) {
            // 未登入
            tvName.setText("未登录");
            ivHeader.setImageResource(R.drawable.bg_header);
            tvHeaderTip.setVisibility(View.VISIBLE);

            llStep.setVisibility(View.GONE);
//            rlAccount.setVisibility(View.GONE);
//            rlOrder.setVisibility(View.GONE);
//            rlCoupon.setVisibility(View.GONE);
            rlId.setVisibility(View.GONE);
            rlPwd.setVisibility(View.GONE);
            rlPhone.setVisibility(View.GONE);
        } else {
            // 已登入
            tvName.setText(SharedData.getUserName(this));

            ImageLoadUtil.loadImage(this, SharedData.getStr(this, SharedData.KEY_HEADER), new ImageLoadUtil.OnImageSavedListener() {
                @Override
                public void onImageSavedSuccess(byte[] img) {
                }

                @Override
                public void onImageSavedSuccess(Bitmap img) {
                    ivHeader.setImageBitmap(ImageLoadUtil.createCircleImage(img));
                    tvHeaderTip.setVisibility(View.GONE);
                }

                @Override
                public void onImageSavedFailed() {
                    ivHeader.setImageResource(R.drawable.bg_header);
                    tvHeaderTip.setVisibility(View.VISIBLE);
                }
            });

            llStep.setVisibility(View.VISIBLE);
//            rlAccount.setVisibility(View.VISIBLE);
//            rlOrder.setVisibility(View.VISIBLE);
//            rlCoupon.setVisibility(View.VISIBLE);
            rlId.setVisibility(View.VISIBLE);
            rlPwd.setVisibility(View.VISIBLE);
            rlPhone.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击我的
     */
    public void clickMine(View view) {
        switch (view.getId()) {
            // 需要登入
            case R.id.rl_info:
            case R.id.ll_step:
            case R.id.rl_account:
            case R.id.rl_order:
            case R.id.rl_coupon:
            case R.id.rl_id:
            case R.id.rl_friends:
            case R.id.rl_pwd:
            case R.id.rl_phone:
                if (CommonUtil.isEmptyOrNull(SharedData.getUserPhone(this))) {
                    // 未登入
                    Intent it = new Intent(this, LoginPhoneActivity.class);
                    it.putExtra("class", "MineActivity");
                    myStartActivity(it);
                    return;
                }
                break;
        }

        // 已登入
        switch (view.getId()) {
            case R.id.rl_info:
                // 编辑个人资料
                myStartActivityForResult(new Intent(this, EditInfoActivity.class), REQUEST_CODE_MINE);
                break;
            case R.id.ll_step:
                // 步骤
                break;
            case R.id.rl_account:
                // 我的账户
                break;
            case R.id.rl_order:
                // 我的订单
                break;
            case R.id.rl_coupon:
                // 我的卡券
                break;
            case R.id.rl_id:
                // 实名认证
                break;
            case R.id.rl_readme:
                // 用车手册
                break;
            case R.id.rl_friends:
                // 邀请好友
                break;
            case R.id.rl_line:
                // 热线电话
                break;
            case R.id.rl_about:
                // 关于我们
                break;
            case R.id.rl_pwd:
                // 修改密码
                break;
            case R.id.rl_phone:
                // 修改手机号
                break;
        }
    }
}
