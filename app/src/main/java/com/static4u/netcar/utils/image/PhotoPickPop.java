package com.static4u.netcar.utils.image;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.static4u.netcar.R;


public class PhotoPickPop extends PopupWindow implements View.OnClickListener {

    private OnDialogListener listener;
    private Button btn_choose_photo;

    public PhotoPickPop(Context context, OnDialogListener listener) {
        super(context);
        this.listener = listener;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mPopView = inflater.inflate(R.layout.dialog_get_photo, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        btn_choose_photo = (Button) mPopView.findViewById(R.id.btn_choose_photo);

        mPopView.findViewById(R.id.btn_take_photo).setOnClickListener(this);
        mPopView.findViewById(R.id.btn_choose_photo).setOnClickListener(this);
        mPopView.findViewById(R.id.btn_choose_file).setOnClickListener(this);
        mPopView.findViewById(R.id.btn_show).setOnClickListener(this);
        mPopView.findViewById(R.id.btn_save).setOnClickListener(this);
        mPopView.findViewById(R.id.btn_cancel).setOnClickListener(this);

        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 点击外面的控件也可以使得PopUpWindow dismiss
        this.setOutsideTouchable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);//0xb0000000
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);//半透明颜色
    }

    public void setPickBtnShow(boolean show) {
        if (btn_choose_photo != null) {
            btn_choose_photo.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void setImageMode(boolean isImageMode) {
        LinearLayout llPick = (LinearLayout) getContentView().findViewById(R.id.ll_pick);
        LinearLayout llImage = (LinearLayout) getContentView().findViewById(R.id.ll_image);
        if (isImageMode) {
            llPick.setVisibility(View.GONE);
            llImage.setVisibility(View.VISIBLE);
        } else {
            llPick.setVisibility(View.VISIBLE);
            llImage.setVisibility(View.GONE);
        }
    }

    public void show(ViewGroup view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * Dialog按钮回调接口
     */
    public interface OnDialogListener {
        //选择本地照片
        void onChoosePhoto();

        //照相
        void onTakePhoto();

        //选择文件
        void onChooseFile();

        // 查看图片
        void onImageShow();

        // 保存图片
        void onImageSave();

        //取消
        void onCancel();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                listener.onTakePhoto();
                break;
            case R.id.btn_choose_photo:
                listener.onChoosePhoto();
                break;
            case R.id.btn_choose_file:
                listener.onChooseFile();
                break;
            case R.id.btn_cancel:
                listener.onCancel();
                break;
            case R.id.btn_show:
                listener.onImageShow();
                break;
            case R.id.btn_save:
                listener.onImageSave();
                break;
        }
        dismiss();
    }
}
