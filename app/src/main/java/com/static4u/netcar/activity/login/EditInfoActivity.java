package com.static4u.netcar.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.static4u.netcar.R;
import com.static4u.netcar.base.BasePhotoPickActivity;
import com.static4u.netcar.business.SharedData;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.ui.MyEditText;
import com.static4u.netcar.utils.Base64Utils;
import com.static4u.netcar.utils.image.ImageLoadUtil;
import com.static4u.netcar.utils.network.HttpClientUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.static4u.netcar.business.SharedData.KEY_ADDRESS;
import static com.static4u.netcar.business.SharedData.KEY_EMAIL;
import static com.static4u.netcar.business.SharedData.KEY_EMERGENCY_NAME;
import static com.static4u.netcar.business.SharedData.KEY_EMERGENCY_PHONE;
import static com.static4u.netcar.business.SharedData.KEY_HEADER;
import static com.static4u.netcar.business.SharedData.KEY_INCOME;
import static com.static4u.netcar.business.SharedData.KEY_INFO_COME;
import static com.static4u.netcar.business.SharedData.KEY_NAME;
import static com.static4u.netcar.business.SharedData.KEY_USE;

public class EditInfoActivity extends BasePhotoPickActivity {

    @Bind(R.id.iv_header)
    ImageView ivHeader;
    @Bind(R.id.tv_header_tip)
    TextView tvHeaderTip;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.et_name)
    MyEditText etName;
    @Bind(R.id.et_email)
    MyEditText etEmail;
    @Bind(R.id.et_address)
    MyEditText etAddress;
    @Bind(R.id.et_emergency)
    MyEditText etEmergency;
    @Bind(R.id.et_emergency_phone)
    MyEditText etEmergencyPhone;
    @Bind(R.id.et_income)
    MyEditText etIncome;
    @Bind(R.id.et_info_come)
    MyEditText etInfoCome;
    @Bind(R.id.et_use)
    MyEditText etUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_info);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        isHeader = true;
        ivPhoto = ivHeader;

        tvPhone.setText(SharedData.getUserPhone(this));
        etName.setText(SharedData.getUserName(this));

        loadHeader(SharedData.getStr(this, KEY_HEADER));

        etEmail.setText(SharedData.getStr(this, KEY_EMAIL));
        etAddress.setText(SharedData.getStr(this, KEY_ADDRESS));
        etEmergency.setText(SharedData.getStr(this, KEY_EMERGENCY_NAME));
        etEmergencyPhone.setText(SharedData.getStr(this, KEY_EMERGENCY_PHONE));
        etIncome.setText(SharedData.getStr(this, KEY_INCOME));
        etInfoCome.setText(SharedData.getStr(this, KEY_INFO_COME));
        etUse.setText(SharedData.getStr(this, KEY_USE));
    }

    @Override
    protected void loadHeader(String path) {
        ImageLoadUtil.loadImageWithoutCache(this, path, new ImageLoadUtil.OnImageSavedListener() {
            @Override
            public void onImageSavedSuccess(byte[] img) {
            }

            @Override
            public void onImageSavedSuccess(Bitmap img) {
                Bitmap cb = ImageLoadUtil.createCircleImage(img);
                ivHeader.setImageBitmap(cb);
                tvHeaderTip.setVisibility(View.GONE);
            }

            @Override
            public void onImageSavedFailed() {
                ivHeader.setImageResource(R.drawable.bg_header);
                tvHeaderTip.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void loadHeader(byte[] bytes) {
        ImageLoadUtil.loadImageWithoutCache(this, bytes, new ImageLoadUtil.OnImageSavedListener() {
            @Override
            public void onImageSavedSuccess(byte[] img) {
            }

            @Override
            public void onImageSavedSuccess(Bitmap img) {
                Bitmap cb = ImageLoadUtil.createCircleImage(img);
                uploadStream = Base64Utils.encode(ImageLoadUtil.getBytesByBitmap(cb));
                ivHeader.setImageBitmap(cb);
                tvHeaderTip.setVisibility(View.GONE);
            }

            @Override
            public void onImageSavedFailed() {
                ivHeader.setImageResource(R.drawable.bg_header);
                tvHeaderTip.setVisibility(View.VISIBLE);
            }
        });
    }

    public void clickHeader(View view) {
        // 更换头像
        clickPick(view);
    }

    public void doSubmit(View view) {
        progressView.setVisibility(View.VISIBLE);

        // TODO: 2019/1/8
        HttpClientUtil util = new HttpClientUtil(new HttpClientUtil.OnHttpListener() {
            @Override
            public void onSuccess(String responseStr) {
                progressView.setVisibility(View.GONE);

                saveData("https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D268%3Bg%3D0/sign=4f7bf38ac3fc1e17fdbf8b3772ab913e/d4628535e5dde7119c3d076aabefce1b9c1661ba.jpg");

                Intent it = new Intent();
                setResult(RESULT_OK, it.putExtra("login", true));
                finish();
            }

            @Override
            public void onFailure() {
                progressView.setVisibility(View.GONE);
                showToast("保存失败");

                saveData("https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D268%3Bg%3D0/sign=4f7bf38ac3fc1e17fdbf8b3772ab913e/d4628535e5dde7119c3d076aabefce1b9c1661ba.jpg");
                Intent it = new Intent();
                setResult(RESULT_OK, it.putExtra("login", true));
                finish();
            }
        });
        util.postHttpClient(URLConstant.HOST_FUNC, uploadStream);
    }

    private void saveData(String headerUrl) {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String emergencyName = etEmergency.getText().toString().trim();
        String emergencyPhone = etEmergencyPhone.getText().toString().trim();
        String income = etIncome.getText().toString().trim();
        String infoCome = etInfoCome.getText().toString().trim();
        String use = etUse.getText().toString().trim();

        SharedData.setStr(this, KEY_HEADER, headerUrl);
        loadHeader(SharedData.getStr(this, KEY_HEADER));

        SharedData.setStr(this, KEY_NAME, name);
        SharedData.setStr(this, KEY_EMAIL, email);
        SharedData.setStr(this, KEY_ADDRESS, address);
        SharedData.setStr(this, KEY_EMERGENCY_NAME, emergencyName);
        SharedData.setStr(this, KEY_EMERGENCY_PHONE, emergencyPhone);
        SharedData.setStr(this, KEY_INCOME, income);
        SharedData.setStr(this, KEY_INFO_COME, infoCome);
        SharedData.setStr(this, KEY_USE, use);
    }
}
