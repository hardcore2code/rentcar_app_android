package com.static4u.netcar.activity.detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.gyf.barlibrary.ImmersionBar;
import com.static4u.netcar.R;
import com.static4u.netcar.activity.bag.BagListActivity;
import com.static4u.netcar.activity.comment.CommentListActivity;
import com.static4u.netcar.activity.home.ImagePageChangeListener;
import com.static4u.netcar.activity.home.ImagePagerAdapter;
import com.static4u.netcar.activity.login.LoginPhoneActivity;
import com.static4u.netcar.base.BaseActivity;
import com.static4u.netcar.business.SharedData;
import com.static4u.netcar.constant.GlobalConstant;
import com.static4u.netcar.model.BagInfo;
import com.static4u.netcar.model.CarInfo;
import com.static4u.netcar.model.CommentInfo;
import com.static4u.netcar.utils.Base64Utils;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.image.ImageLoadUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.static4u.netcar.activity.bag.BagListActivity.REQUEST_CODE_BAG;

public class CarDetailActivity extends BaseActivity {

    @Bind(R.id.vp_car)
    ViewPager vpCar;
    @Bind(R.id.ll_index)
    LinearLayout llIndex;
    @Bind(R.id.tv_brand)
    TextView tvBrand;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_company)
    TextView tvCompany;
    @Bind(R.id.tv_power)
    TextView tvPower;
    @Bind(R.id.tv_sit)
    TextView tvSit;
    @Bind(R.id.tv_length)
    TextView tvLength;
    @Bind(R.id.tv_weight)
    TextView tvWeight;
    @Bind(R.id.tv_charge)
    TextView tvCharge;
    @Bind(R.id.tv_loc1)
    TextView tvLoc1;
    @Bind(R.id.tv_loc2)
    TextView tvLoc2;
    @Bind(R.id.map)
    MapView map;
    @Bind(R.id.iv_header)
    ImageView ivHeader;
    @Bind(R.id.tv_header_tip)
    TextView tvHeaderTip;
    @Bind(R.id.tv_time)
    TextView tvCommentTime;
    @Bind(R.id.tv_comment_name)
    TextView tvCommentName;
    @Bind(R.id.ll_comment)
    LinearLayout llComment;
    @Bind(R.id.tv_comment)
    TextView tvCommentContent;
    @Bind(R.id.tv_comment_count)
    TextView tvCommentCount;
    @Bind(R.id.tv_price)
    TextView tvPrice;

    private CarInfo car;
    private BagInfo selectedBag;
    private BaiduMap baiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_car_detail);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        ImmersionBar.with(this).init();

        loadData();
    }

    @Override
    protected void onPause() {
        map.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        map.onResume();

        // 刷新评价
        if (car != null && car.getCommList().size() < 2 && GlobalConstant.addComment) {
            GlobalConstant.addComment = false;

            loadComment();
        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ImmersionBar.with(this).destroy();
        map.onDestroy();
        super.onDestroy();
    }

    public static void startDetail(BaseActivity activity, CarInfo info) {
        Intent it = new Intent(activity, CarDetailActivity.class);
        it.putExtra("car", info);
        activity.myStartActivity(it);
    }

    private void loadData() {
        car = (CarInfo) getIntent().getSerializableExtra("car");

        if (car == null) {
            showToast("车型信息异常，请稍后重试");
            clickBack(vpCar);
            return;
        }

        int w = CommonUtil.getScreenWidth(this);
        vpCar.setAdapter(new ImagePagerAdapter(this, car.getImgList()));
        vpCar.setOnPageChangeListener(new ImagePageChangeListener(this, llIndex, car));
        vpCar.setLayoutParams(new RelativeLayout.LayoutParams(w, w * 9 / 16));
        vpCar.setCurrentItem(car.getpIndex());

        tvBrand.setText(car.getBrand());
        tvName.setText(car.getName());
        tvCompany.setText(car.getCompany());
        tvPower.setText(car.getPower());
        tvSit.setText(car.getSit());
        tvLength.setText(car.getLength());
        tvWeight.setText(car.getWeight());
        tvCharge.setText(car.getCharge());

        tvLoc1.setText(car.getLoc1());
        tvLoc2.setText(car.getLoc2());
        // 设置地图
        MarkerOptions ooa = new MarkerOptions().position(new LatLng(car.getLatitude(), car.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.location_red));
        baiduMap = map.getMap();
        // 不可拖动和手势
        baiduMap.getUiSettings().setScrollGesturesEnabled(false);
        baiduMap.addOverlay(ooa);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(16); // 200米（ 最大21：5米）
        builder.target(ooa.getPosition());
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        loadComment();
    }

    private void loadComment() {
        if (car.getCommList().size() > 0) {
            llComment.setVisibility(View.VISIBLE);
            tvCommentCount.setText(getString(R.string.all_comment, car.getCommList().size()));

            CommentInfo commentInfo = car.getCommList().get(0);
            ImageLoadUtil.loadImage(this, Base64Utils.decode(commentInfo.getHeader()), new ImageLoadUtil.OnImageSavedListener() {
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
            tvCommentName.setText(commentInfo.getName());
            tvCommentTime.setText(commentInfo.getTime());
            tvCommentContent.setText(commentInfo.getContent());
        } else {
            llComment.setVisibility(View.GONE);
            tvCommentCount.setText(getString(R.string.no_comment));
        }
    }

    public void clickSelect(View view) {
        // 选择套餐
        BagListActivity.startActivityForResult(this, car);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BAG && resultCode == RESULT_OK) {
            // 选择套餐返回
            selectedBag = (BagInfo) data.getSerializableExtra("bag");
            tvPrice.setText(selectedBag.getPrice());
        }
    }

    public void clickBuy(View view) {
        if (CommonUtil.isEmptyOrNull(SharedData.getUserPhone(this))) {
            showToast("请先登录");

            Intent it = new Intent(this, LoginPhoneActivity.class);
            it.putExtra("class", "CommentListActivity");
            myStartActivity(it);
            return;
        }

        // 立即下单
        if (selectedBag == null) {
            showToast("请先选择套餐");
            return;
        }

        showToast("下单成功");
    }

    public void clickPhone(View view) {
        // 联系电话
        if (TextUtils.isDigitsOnly(car.getPhone())) {
            CommonUtil.callPhone(this, car.getPhone());
        } else {
            showToast("联系电话格式有误:" + car.getPhone());
        }
    }

    public void clickComment(View view) {
        // 查看全部评论
        CommentListActivity.startActivity(this, car);
    }

    public void clickShare(View view) {
        // 查看全部评论
        showToast("点击分享");
    }

    /**
     * 百度导航
     */
    public void clickGotoNavi(View view) {
        if (CommonUtil.isAppInstalled(this, "com.baidu.BaiduMap")) {
            // 百度地图已安装
            Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("baidumap://map/geocoder?location=" + car.getLatitude() + "," + car.getLongitude()));
            startActivity(naviIntent);
        } else {
            showToast("请先安装百度地图");
        }
    }
}
