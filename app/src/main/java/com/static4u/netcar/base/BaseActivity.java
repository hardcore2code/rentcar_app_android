package com.static4u.netcar.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import com.static4u.netcar.activity.comment.CommentListActivity;
import com.static4u.netcar.activity.detail.CarDetailActivity;
import com.static4u.netcar.activity.home.HomeActivity;
import com.static4u.netcar.business.SharedData;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.model.BagInfo;
import com.static4u.netcar.model.CarInfo;
import com.static4u.netcar.model.CommentInfo;
import com.static4u.netcar.ui.DialogMessageUtil;
import com.static4u.netcar.utils.CacheManagerUtil;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.image.ImageLoadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.static4u.netcar.business.SharedData.KEY_ADDRESS;
import static com.static4u.netcar.business.SharedData.KEY_EMAIL;
import static com.static4u.netcar.business.SharedData.KEY_EMERGENCY_NAME;
import static com.static4u.netcar.business.SharedData.KEY_EMERGENCY_PHONE;
import static com.static4u.netcar.business.SharedData.KEY_INCOME;
import static com.static4u.netcar.business.SharedData.KEY_INFO_COME;
import static com.static4u.netcar.business.SharedData.KEY_USE;

/**
 *
 */
public class BaseActivity extends FragmentActivity {
    // 圆形进度条
    protected RelativeLayout progressView;
    protected View emptyView;

    // 图片的各种状态设定
    private View rlHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(this instanceof FakeActivity)) {
            if (!(this instanceof CarDetailActivity)) {
                setStatusBarColor(R.color.white);
            }

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

    /**
     * 退出登录
     */
    public void logout() {
        SharedData.setUserData(this, "", "", "", "");
        SharedData.setStr(this, KEY_EMAIL, "");
        SharedData.setStr(this, KEY_ADDRESS, "");
        SharedData.setStr(this, KEY_EMERGENCY_NAME, "");
        SharedData.setStr(this, KEY_EMERGENCY_PHONE, "");
        SharedData.setStr(this, KEY_INCOME, "");
        SharedData.setStr(this, KEY_INFO_COME, "");
        SharedData.setStr(this, KEY_USE, "");
    }

    public Uri getFileUri(String path) {
        return FileProvider.getUriForFile(this, getPackageName(), new File(path));
    }

    /**
     * 登入成功返回
     */
    public void loginSucAndGoActivity() {
        Intent it = new Intent(this, MineActivity.class);
        switch (getIntent().getStringExtra("class")) {
            case "MineActivity":
                it.setClass(this, MineActivity.class);
                break;
            case "CommentListActivity":
                it.setClass(this, CommentListActivity.class);
                break;
            case "CarDetailActivity":
                it.setClass(this, CarDetailActivity.class);
                break;
        }
        it.putExtra("login", true);
        myStartActivity(it);
        overridePendingTransition(R.anim.push_stay, R.anim.push_right_out);
    }

    /**
     * 获取测试车型
     */
    public List<CarInfo> getTestData(int count) {
        // TODO: 2019/1/9
        List<CarInfo> data = new ArrayList<>();

        List<String> imgs = new ArrayList<>();
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114328-1471319236.jpg");
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114326-558979764.jpg");
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114328-903976311.jpg");
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114329-723161299.jpg");
        imgs.add("https://pic.wenwen.soso.com/p/20151226/20151226114549-682156056.jpg");

        List<CommentInfo> comments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            comments.add(new CommentInfo("李四" + i, "2017年12月", SharedData.getStr(this, SharedData.KEY_HEADER), "评价内容评价内容评价内容评价内容评价内容评价内容评价内容评价内容"));
        }

        for (int i = 0; i < count; i++) {
            CarInfo car = new CarInfo();
            car.setName("新能源物流车" + i);
            car.setCompany("恒大" + i);
            car.setType("新能源");
            car.setPrice("1000");
            car.setBrand("中国恒大" + i);
            car.setPriceOld("1800");
            car.setSub("/月 - 免费保养 提供行驶证");

            car.setPower("电动车");
            car.setLength(100 * (i + 1) + "公里");
            car.setWeight(1000 + 100 * (i + 1) + "kg");
            car.setSit(4 + i + "座");
            car.setCharge((i % 2 == 0 ? "快充" : "") + 1 + i + "小时");
            car.setLoc1("天津市，南开区");
            car.setLoc2("迎水道1号 天津广播电视大学");
            car.setLatitude(39.0978511039);
            car.setLongitude(117.1647923122);
            car.setPhone("123012381238");

            List<CommentInfo> comms = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                comms.add(comments.get(j % comments.size()));
            }
            car.setCommList(comms);

            List<String> imgList = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                imgList.add(imgs.get(j % imgs.size()));
            }
            car.setImgList(imgList);

            data.add(car);
        }
        return data;
    }

    public List<BagInfo> getTestBag() {
        List<BagInfo> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            BagInfo info = new BagInfo();
            info.setId("" + i);
            info.setName("套餐" + (i + 1) + " - 连租" + (i + 1) + "月");
            info.setIntro("测试套餐，全部免费");
            info.setDeposit("" + (i + 1) * 1000);
            info.setPrice("" + (i + 1) * 1000 + "元/" + (i + 1) + "月");
            list.add(info);
        }

        return list;
    }
}
