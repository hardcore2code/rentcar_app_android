package com.static4u.netcar.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.static4u.netcar.R;
import com.static4u.netcar.activity.CarListActivity;
import com.static4u.netcar.activity.MineActivity;
import com.static4u.netcar.activity._splash.FakeActivity;
import com.static4u.netcar.activity.home.HomeActivity;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.ui.DialogMessageUtil;
import com.static4u.netcar.utils.CacheManagerUtil;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.ImageLoadUtil;

public class BaseActivity extends FragmentActivity {
    // 圆形进度条
    protected RelativeLayout progressView;
    protected TextView tvLoading;
    protected View emptyView;

    // 图片的各种状态设定
    private View rlHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(this instanceof FakeActivity)) {
            setStatusBarColor(R.color.white);

            progressView = findViewById(R.id.rl_progressbar);
            emptyView = findViewById(R.id.rl_empty);
            rlHeader = findViewById(R.id.header);

            initFooter();
        }
    }

    /**
     * 设置状态栏颜色
     */
    public void setStatusBarColor(int colorId) {
        StatusBarCompat.setStatusBarColor(this, getResourcesColor(colorId), false);
    }

    /**
     * 初始化footer，如果有必要的话
     */
    public void initFooter() {
        View footer = findViewById(R.id.footer);
        if (footer != null) {
            TextView tabHome = footer.findViewById(R.id.tv_tab_home);
            tabHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(BaseActivity.this instanceof HomeActivity)) {
                        myStartActivity(new Intent(BaseActivity.this, HomeActivity.class));
                        overridePendingTransition(-1, -1);
                    }
                }
            });
            ImageView tabAdd = footer.findViewById(R.id.iv_tab_add);
            tabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doRent(view);
                }
            });
            TextView tabMine = footer.findViewById(R.id.tv_tab_mine);
            tabMine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(BaseActivity.this instanceof MineActivity)) {
                        myStartActivity(new Intent(BaseActivity.this, MineActivity.class));
                        overridePendingTransition(-1, -1);
                    }
                }
            });
            if (BaseActivity.this instanceof HomeActivity) {
                tabHome.setTextColor(getResourcesColor(R.color.font_dark));
            } else if (BaseActivity.this instanceof CarListActivity) {
                // 马上用车
                doRent(null);
            } else if (BaseActivity.this instanceof MineActivity) {
                tabMine.setTextColor(getResourcesColor(R.color.font_dark));
            }
        }
    }

    /**
     * 马上用车
     */
    public void doRent(View view) {
        Intent it = new Intent(this, CarListActivity.class);
        myStartActivity(it);
        overridePendingTransition(R.anim.bottom_in, R.anim.push_stay);
    }

    /**
     * 统一页面打开方式（动画与模式）
     */
    public void myStartActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_stay);
    }

    /**
     * 统一带返回结果的页面打开方式（动画与模式）
     */
    public void myStartActivityForResult(Intent intent, int requestCode) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_stay);
    }

    /**
     * 隐藏header返回按钮，默认显示
     */
    public void hideHeaderBackBtn() {
        if (rlHeader != null) {
            rlHeader.findViewById(R.id.mr_back).setVisibility(View.GONE);
        }
    }

    /**
     * 关闭键盘
     */
    public void clickView(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    /**
     * 点击返回按钮
     */
    public void clickBack(View view) {
        onBackPressed();
    }

    private DialogMessageUtil exitDialog;

    @Override
    public void onBackPressed() {
        if (this instanceof HomeActivity) {
            if (exitDialog == null || !exitDialog.isShow()) {
                exitDialog = DialogMessageUtil.getInstance(this)
                        .setCancelable(true)
                        .setMsg("您确定要退出吗？")
                        .setLeftBtnText("取消")
                        .setRightBtnText("退出")
                        .setRightBtnListener(new DialogMessageUtil.OnDialogBtnClickListener() {
                            @Override
                            public boolean onClick(View clickView, View dialog, DialogMessageUtil util) {
                                // 清理本地数据
                                clearCache();

                                finish();

                                return false;
                            }
                        }).show();
            } else {
                exitDialog.hide();
            }
        } else {
            finish();
            overridePendingTransition(R.anim.push_stay, R.anim.push_right_out);
        }
    }

    /**
     * 设置加载文言
     */
    public void setLoadingMsg(String msg) {
        if (tvLoading != null) {
            tvLoading.setText(msg);
        }
    }

    /**
     * 获取页面根视图
     *
     * @return 根视图
     */
    public ViewGroup getRootView() {
        return (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 获取resources中的颜色
     */
    public int getResourcesColor(int id) {
        return ContextCompat.getColor(this, id);
    }

    /**
     * 清理缓存
     */
    public void clearCache() {
        // 删除图片缓存
        ImageLoadUtil.clearMemoryCache(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CacheManagerUtil.clearAllCache(BaseActivity.this);
                    ImageLoadUtil.clearSDCache(BaseActivity.this);
                } catch (Exception e) {
                    if (URLConstant.FORCE_LOG) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 统一toast显示方式
     */
    public void showToast(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置空状态页面信息
     *
     * @param txt      文本
     * @param imgId    图片
     * @param btnStr   字符
     * @param listener 监听
     */
    public void setEmptyView(int imgId, String txt, String btnStr, View.OnClickListener listener) {
        emptyView = findViewById(R.id.rl_empty);
        if (emptyView != null) {
            TextView tv = emptyView.findViewById(R.id.tv_empty_msg);
            tv.setText(txt);
            ImageView iv = emptyView.findViewById(R.id.iv_empty);
            iv.setImageResource(imgId);

            Button btn = emptyView.findViewById(R.id.btn_empty_action);
            if (CommonUtil.isEmptyOrNull(btnStr)) {
                btn.setVisibility(View.GONE);
            } else {
                btn.setText(btnStr);
                btn.setOnClickListener(listener);
            }
        }
    }

    public void setEmptyViewListener(View.OnClickListener listener) {
        setEmptyView(R.drawable.ic_empty, "没找到您要的东西哦!", "再试一次", listener);
    }

}
