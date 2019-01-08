package com.static4u.netcar.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.static4u.netcar.R;
import com.static4u.netcar.utils.DownloadUtil;

import java.io.File;

/**
 * 消息框
 * Created by xiongjian on 10/10/15.
 */
public class DialogMessageUtil {

    private final String STR_TITLE = "提示";
    private final String STR_MSG = "服务器繁忙，请稍后再试";
    private final String STR_LEFT_BTN = "取消";
    private final String STR_RIGHT_BTN = "确定";
    private final String STR_DOWNLOADING = "正在下载...";
    private final String STR_PERCENT = "%";

    View vPlur;
    ImageView ivClose;
    View closeView, mrlLeft, mrlRight;
    TextView tvTitle;
    TextView tvMsg;

    TextView btnRight;
    TextView btnLeft;

    private boolean isDownloadMode = false;
    ProgressBar downLoadProgress;

    public static DialogMessageUtil util;
    private Activity activity;
    private ViewGroup root;
    private View rlDialog;

    private boolean isShow = false;
    // 左右btn是否已经添加点击事件
    private boolean isBtnLeftSetListener, isBtnRightSetListener;

    // 默认隐藏事件
    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (root != null && rlDialog != null) {
                root.removeView(rlDialog);
                isShow = false;
            }
        }
    };

    public interface OnDialogBtnClickListener {
        /**
         * 按钮回调
         *
         * @param clickView 点击的view
         * @param dialog    整个dialog
         * @param util      当前DialogMessageUtil对象
         * @return 是否需要保留dialog
         */
        boolean onClick(View clickView, View dialog, DialogMessageUtil util);
    }

    /**
     * 获取DialogMessageUtil实例
     *
     * @param activity
     * @return
     */
    public static DialogMessageUtil getInstance(Activity activity) {
        util = new DialogMessageUtil();

        util.activity = activity;
        util.root = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);

        util.initDialog(activity);

        return util;
    }

    /**
     * 普通消息dialog初始化
     *
     * @param activity
     */
    private void initDialog(Activity activity) {
        rlDialog = LayoutInflater.from(activity).inflate(R.layout.view_dialog_message, null);

        vPlur = rlDialog.findViewById(R.id.v_plur);
        tvTitle = (TextView) rlDialog.findViewById(R.id.tv_title);
        tvMsg = (TextView) rlDialog.findViewById(R.id.tv_msg);
        ivClose = (ImageView) rlDialog.findViewById(R.id.iv_close);
        closeView = rlDialog.findViewById(R.id.mrl_close);
        tvMsg = (TextView) rlDialog.findViewById(R.id.tv_msg);
        btnLeft = (TextView) rlDialog.findViewById(R.id.btn_left);
        mrlLeft = rlDialog.findViewById(R.id.mrl_left);
        mrlRight = rlDialog.findViewById(R.id.mrl_right);
        btnRight = (TextView) rlDialog.findViewById(R.id.btn_right);

        downLoadProgress = (ProgressBar) rlDialog.findViewById(R.id.pb_download);
        downLoadProgress.setVisibility(View.INVISIBLE);

        tvTitle.setText(STR_TITLE);
        tvMsg.setText(STR_MSG);
        btnLeft.setText(STR_LEFT_BTN);
        btnRight.setText(STR_RIGHT_BTN);

        vPlur.setOnClickListener(cancelListener);
        btnLeft.setOnClickListener(cancelListener);
        btnRight.setOnClickListener(cancelListener);
        ivClose.setOnClickListener(cancelListener);
    }

    /**
     * 进入下载模式
     * 显示下载进度条
     *
     * @return
     */
    public DialogMessageUtil changeToDownloadMode() {
        isDownloadMode = true;

        setCancelable(false);
        ivClose.setVisibility(View.GONE);
        closeView.setVisibility(View.GONE);
        btnLeft.setVisibility(View.GONE);
        mrlLeft.setVisibility(View.GONE);
        btnRight.setVisibility(View.GONE);
        mrlRight.setVisibility(View.GONE);

        downLoadProgress.setVisibility(View.VISIBLE);
        tvMsg.setText(STR_DOWNLOADING + 0 + STR_PERCENT);
        downLoadProgress.setProgress(0);
        return util;
    }

    public DialogMessageUtil hideCloseBtn() {
        closeView.setVisibility(View.GONE);
        return util;
    }

    String url, path;

    /**
     * 内部下载器开始下载
     *
     * @param url 下载地址
     */
    public void startDownload(String url, String path) {
        this.url = url;
        this.path = path;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String[] str = url.split("/");
        DownloadUtil util = new DownloadUtil(activity, url, str[str.length - 1], downLoadProgress);
        util.setUtil(this.util);
        util.start(path);
    }

    /**
     * 重新开始下载
     */
    private void restartDownload() {
        if (url != null && path != null) {
            startDownload(url, path);
        }
    }

    /**
     * 下载成功
     *
     * @param percent
     */
    public void doDownloadSuccess(int percent) {
        setProgress(percent);
        hide();
        installUpdate(url, path);
    }

    /**
     * 安装下载的APK
     */
    public void installUpdate(String url, String dir) {
        String[] str = url.split("/");
        String path = dir + str[str.length - 1];
        File apk = new File(path);
        if (apk.exists()) {
            Intent it = new Intent(android.content.Intent.ACTION_VIEW);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            it.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
            activity.startActivity(it);
        } else {
            Log.e("", "下载的APK不存在， path:" + path);
        }
    }

    /**
     * 下载失败
     */
    public void doDownloadFail() {
        tvMsg.setText("下载失败");
        ivClose.setVisibility(View.VISIBLE);
        closeView.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        mrlRight.setVisibility(View.VISIBLE);
        btnRight.setText("重新下载");
        downLoadProgress.setVisibility(View.GONE);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartDownload();
            }
        });
    }

    /**
     * 外部自定义下载器，设置下载进度
     *
     * @param percent 下载百分比
     * @return
     */
    public DialogMessageUtil setProgress(int percent) {
        if (isDownloadMode && downLoadProgress != null && downLoadProgress.getVisibility() == View.VISIBLE) {
            tvMsg.setText(STR_DOWNLOADING + percent + STR_PERCENT);
            downLoadProgress.setProgress(percent);
        }
        return util;
    }


    /**
     * 设置title
     */
    public DialogMessageUtil setTitle(String title) {
        if (title != null && title.length() > 0) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.INVISIBLE);
        }
        return util;
    }

    public DialogMessageUtil setTitleFont(int fontColor, int fontSize) {
        if (tvTitle.getText().length() > 0) {
            tvTitle.setTextColor(fontColor);
            tvTitle.setTextSize(fontSize);
        }
        return util;
    }

    /**
     * 设置message
     */
    public DialogMessageUtil setMsg(String msg) {
        if (msg != null && msg.length() > 0) {
            tvMsg.setText(msg);
        }
        return util;
    }

    public DialogMessageUtil setMsgFont(int fontColor, int fontSize) {
        if (tvMsg.getText().length() > 0) {
            tvMsg.setTextColor(fontColor);
            tvMsg.setTextSize(fontSize);
        }
        return util;
    }

    /**
     * 设置左侧按钮
     */
    public DialogMessageUtil setLeftBtnText(String text) {
        if (text != null && text.length() > 0) {
            btnLeft.setText(text);
            btnLeft.setVisibility(View.VISIBLE);
            mrlLeft.setVisibility(View.VISIBLE);
        } else {
            btnLeft.setVisibility(View.GONE);
            mrlLeft.setVisibility(View.GONE);
        }
        return util;
    }

    public DialogMessageUtil setLeftBtnFont(int fontColor, int fontSize) {
        if (btnLeft.getText().length() > 0) {
            btnLeft.setTextColor(fontColor);
            btnLeft.setTextSize(fontSize);
        }
        return util;
    }

    public DialogMessageUtil setLeftBtnTextColor(int fontColor) {
        if (btnLeft.getText().length() > 0) {
            btnLeft.setTextColor(fontColor);
        }
        return util;
    }

    public DialogMessageUtil setRightBtnTextColor(int fontColor) {
        if (btnRight.getText().length() > 0) {
            btnRight.setTextColor(fontColor);
        }
        return util;
    }

    /**
     * 设置右侧按钮比（右侧按钮必定存在）
     */
    public DialogMessageUtil setRightBtnText(String text) {
        if (text != null && text.length() > 0) {
            btnRight.setText(text);
        }
        return util;
    }

    public DialogMessageUtil setRightBtnFont(int fontColor, int fontSize) {
        if (btnRight.getText().length() > 0) {
            btnRight.setTextColor(fontColor);
            btnRight.setTextSize(fontSize);
        }
        return util;
    }

    public DialogMessageUtil setRightBtnEnable(boolean enable) {
        if (btnRight.getText().length() > 0) {
            btnRight.setEnabled(enable);
        }
        return util;
    }

    public DialogMessageUtil setLeftBtnEnable(boolean enable) {
        if (btnLeft.getText().length() > 0) {
            btnLeft.setEnabled(enable);
        }
        return util;
    }

    /**
     * 点击透明区域是否可以关闭dialog
     *
     * @param cancelable
     */
    public DialogMessageUtil setCancelable(boolean cancelable) {
        if (cancelable) {
            vPlur.setOnClickListener(cancelListener);
        } else {
            vPlur.setOnClickListener(null);
        }
        return util;
    }

    /**
     * 设置左侧按钮点击事件
     *
     * @param listener
     */
    public DialogMessageUtil setLeftBtnListener(final OnDialogBtnClickListener listener) {
        if (btnLeft.getVisibility() == View.VISIBLE && btnLeft.getText().length() > 0) {
            isBtnLeftSetListener = true;
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 默认点击按钮不保留dialog
                    boolean dialogStay = false;
                    if (listener != null && btnLeft != null && rlDialog != null) {
                        dialogStay = listener.onClick(btnLeft, rlDialog, util);
                    }
                    if (!dialogStay && root != null && rlDialog != null) {
                        root.removeView(rlDialog);
                    }
                }
            });
        } else {
            btnLeft.setOnClickListener(cancelListener);
        }
        return util;
    }

    public DialogMessageUtil setRightBtnListener(final OnDialogBtnClickListener listener) {
        if (btnRight.getVisibility() == View.VISIBLE && btnRight.getText().length() > 0) {
            isBtnRightSetListener = true;
            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 默认点击按钮不保留dialog
                    boolean dialogStay = false;
                    if (listener != null && btnRight != null && rlDialog != null) {
                        dialogStay = listener.onClick(btnRight, rlDialog, util);
                    }
                    if (!dialogStay && root != null && rlDialog != null) {
                        root.removeView(rlDialog);
                    }
                }
            });
        } else {
            btnRight.setOnClickListener(cancelListener);
        }
        return util;
    }

    /**
     * 重置取消事件
     *
     * @param cancelListener
     * @return
     */
    public DialogMessageUtil setCancelListener(final OnDialogBtnClickListener cancelListener) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 默认点击按钮不保留dialog
                boolean dialogStay = false;
                if (cancelListener != null) {
                    dialogStay = cancelListener.onClick(ivClose, rlDialog, util);
                }
                if (!dialogStay && root != null && rlDialog != null) {
                    root.removeView(rlDialog);
                }
            }
        };
        this.cancelListener = listener;
        vPlur.setOnClickListener(this.cancelListener);
        if (!isBtnLeftSetListener) {
            btnLeft.setOnClickListener(this.cancelListener);
        }
        if (!isBtnRightSetListener) {
            btnRight.setOnClickListener(this.cancelListener);
        }
        ivClose.setOnClickListener(this.cancelListener);
        return util;
    }

    /**
     * 显示dialog
     */
    public DialogMessageUtil show() {
        if (root != null && rlDialog != null) {
            root.removeView(rlDialog);
            root.addView(rlDialog);
            isShow = true;
        }
        return util;
    }

    /**
     * 隐藏dialog
     */
    public void hide() {
        if (root != null && rlDialog != null) {
            root.removeView(rlDialog);
            isShow = false;
        }
    }

    /**
     * 是否正在显示
     *
     * @return
     */
    public boolean isShow() {
        return isShow;
    }

    /**
     * UI控件画面的重绘(更新)是由主线程负责处理的，如果在子线程中更新UI控件的值，更新后的值不会重绘到屏幕上
     * 一定要在主线程里更新UI控件的值，这样才能在屏幕上显示出来，不能在子线程中更新UI控件的值
     */
//    public final class DownloadTask implements Runnable {
//        private String path;
//        private File saveDir;
//        private FileDownloader loader;
//
//        public DownloadTask(String path, File saveDir) {
//            this.path = path;
//            this.saveDir = saveDir;
//        }
//
//        /**
//         * 退出下载
//         */
//        public void exit() {
//            if (loader != null)
//                loader.exit();
//        }
//
//        DownloadProgressListener downloadProgressListener = new DownloadProgressListener() {
//            @Override
//            public void onDownloadSize(int size) {
//                Message msg = new Message();
//                msg.what = PROCESSING;
//                msg.getData().putInt("size", size);
//                msg.getData().putInt("maxSize", loader.getFileSize());
//                SLog.e(size + "  /  " + loader.getFileSize());
//                handler.sendMessage(msg);
//            }
//        };
//
//        public void run() {
//            try {
//                // 实例化一个文件下载器
//                loader = new FileDownloader(com.szht.myf.activity, path, saveDir, 3);
//                // 设置进度条最大值
////                downLoadProgress.setMax(loader.getFileSize());
//                downLoadProgress.setMax(100);
//                downLoadProgress.setProgress(0);
//                loader.download(downloadProgressListener);
//            } catch (Exception e) {
//                e.printStackTrace();
//                handler.sendMessage(handler.obtainMessage(FAILURE)); // 发送一条空消息对象
//            }
//        }
//
//        private Handler handler = new Handler(new Handler.Callback() {
//
//            @Override
//            public boolean handleMessage(Message msg) {
//                switch (msg.what) {
//                    case PROCESSING: // 更新进度
////                        downLoadProgress.setProgress(msg.getData().getInt("size"));
//                        int curSize = msg.getData().getInt("size");
//                        int maxSize = msg.getData().getInt("maxSize");
//                        float num = (float) downLoadProgress.getProgress() / (float) downLoadProgress.getMax();
//                        int percent = (int) (num * 100); // 计算进度
//                        setProgress(percent);
//                        // resultView.setText(result + "%");
//                        if (maxSize <= curSize) { // 下载完成
//                            Toast.makeText(com.szht.myf.activity, "下载完成", Toast.LENGTH_LONG).show();
//                            doDownloadSuccess(percent);
//                            exit();
//                        }
//                        break;
//                    case FAILURE: // 下载失败
//                        Toast.makeText(com.szht.myf.activity, "下载失败", Toast.LENGTH_LONG).show();
//                        // downLoadProgress.setVisibility(View.GONE);
//                        // ivDownload.setEnabled(false);
//                        doDownloadFail();
//                        exit();
//                        break;
//                }
//                return false;
//            }
//        });
//    }
//
//    private static final int PROCESSING = 1;
//    private static final int FAILURE = -1;
}
