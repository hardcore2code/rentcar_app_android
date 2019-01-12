package com.static4u.netcar.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ImageView;

import com.static4u.netcar.R;
import com.static4u.netcar.constant.URLConstant;
import com.static4u.netcar.utils.CommonUtil;
import com.static4u.netcar.utils.FileUtil;
import com.static4u.netcar.utils.SLog;
import com.static4u.netcar.utils.image.ImageLoadUtil;
import com.static4u.netcar.utils.image.PhotoPickPop;

import java.io.File;
import java.util.Date;

public class BasePhotoPickActivity extends BaseActivity {

    // 裁剪输出尺寸
    public final static int PIC_OUTPUT_X = 500;
    public final static int PIC_OUTPUT_Y = 500;

    // 裁剪输出比例
    public final static int PIC_ASPECT_X = 1;
    public final static int PIC_ASPECT_Y = 1;

    // 从相册取图片
    public static final int REQUEST_CODE_GALLERY = 123;
    // 照相
    private static final int REQUEST_CODE_CAMERA = 124;
    // 裁剪
    private static final int REQUEST_CODE_CROP = 125;
    // 选择文件
    private static final int REQUEST_CODE_FILE = 126;

    protected ImageView ivPhoto;

    private PhotoPickPop photoPickPop;
    // 图片名称
    private static String imgName = "img.jpg";
    private String cropPath;
    // 文件本地路径
    private String uploadPath = "";
    // 文件名称
    private String uploadName = "";
    // 文件流
    protected String uploadStream = "";

    protected boolean isHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPhotoPickPop();

        cropPath = FileUtil.getDefaultPath() + "crop/" + imgName;
        File file = new File(FileUtil.getDefaultPath() + "crop/");
        if (!file.isDirectory()) {
            // 必须先创建图片目录，否则部分手机（小米4等）无法保存
            file.mkdirs();
        }
    }

    public void clickPick(View view) {
        // 收回键盘
        clickView(view);

        // 检查照相权限
        if (CommonUtil.checkPermission(this, Manifest.permission.CAMERA)) {
            // 检查本地存储权限
            if (CommonUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 显示选择按钮
                photoPickPop.show(getRootView());
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        } else {
            // 没有权限时，申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        clickPick(ivPhoto);
    }

    /**
     * 初始化获取照片弹出框
     */
    private void initPhotoPickPop() {
        photoPickPop = new PhotoPickPop(this, new PhotoPickPop.OnDialogListener() {
            @Override
            public void onChoosePhoto() {
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        if (FileUtil.hasSDCard()) {
                            File file = new File(FileUtil.getDefaultPath());
                            if (!file.isDirectory()) {
                                file.mkdirs();
                            }
                        }
                        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                        getImage.setType("image/*");
                        startActivityForResult(getImage, REQUEST_CODE_GALLERY);
                    } catch (ActivityNotFoundException e) {
                        showToast("没有找到储存目录");
                    }
                } else {
                    showToast("未找到存储卡，无法存储照片！");
                }
            }

            @Override
            public void onTakePhoto() {
                try {
                    SLog.i("有照相机权限");
                    Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 判断存储卡是否可以用，可用进行存储
                    if (FileUtil.hasSDCard()) {
                        String[] paths = imgName.split("/");
                        String path = imgName.substring(0, imgName.indexOf(paths[paths.length - 1]));
                        File file = new File(FileUtil.getDefaultPath() + path + "/");
                        if (!file.isDirectory()) {
                            // 必须先创建图片目录，否则部分手机（小米4等）无法保存
                            file.mkdirs();
                        }
                        intentFromCapture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(FileUtil.getDefaultPath() + imgName));
                    }
                    startActivityForResult(intentFromCapture, REQUEST_CODE_CAMERA);
                } catch (Exception e) {
                    if (URLConstant.FORCE_LOG) {
                        e.printStackTrace();
                    }
                    SLog.i("无照相机权限");
                }
            }

            @Override
            public void onChooseFile() {
                chooseFile();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onImageShow() {

            }

            @Override
            public void onImageSave() {

            }
        });
        photoPickPop.setPickBtnShow(true);
    }

    /**
     * 选择文件上传
     */
    private void chooseFile() {
        // 判断存储卡是否可以用，可用进行存储
        if (FileUtil.hasSDCard()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("text/plain"
//                    + ";"
//                    + "application/msword"
//                    + ";"
//                    + "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
//                    + ";"
//                    + "application/vnd.ms-excel"
//                    + ";"
//                    + "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//                    + ";"
//                    + "application/pdf"
//                    + ";"
//                    + "application/x-zip-compressed"
//                    + ";"
//                    + "image/jpg"
//                    + ";"
//                    + "image/jpeg"
//                    + ";"
//                    + "image/png"
//                    + ";"
//                    + "image/bmp"
//            );
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE_FILE);
        } else {
            showToast("没有找到储存目录");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 照片返回
        if (resultCode != RESULT_CANCELED) {
            // 结果码不等于取消时候
            switch (requestCode) {
                case REQUEST_CODE_GALLERY:
                    // 相册获取图片
//                    startPhotoCrop(data.getData().getPath());
                    loadFile(getPath(this, data.getData()));
                    break;
                case REQUEST_CODE_CAMERA:
                    // 相机拍照
                    if (CommonUtil.hasSdcard()) {
//                        startPhotoCrop(FileUtil.getDefaultPath() + imgName);
                        loadFile(FileUtil.getDefaultPath() + imgName);
                    } else {
                        showToast("没有SD卡，无法保存图片");
                    }
                    break;
                case REQUEST_CODE_CROP:
                    // 裁剪返回
                    loadFile(cropPath);
                    break;
                case REQUEST_CODE_FILE:
                    // 文件选择返回
                    try {
                        String path = getPath(this, data.getData());
                        if (!CommonUtil.isEmptyOrNull(path)) {
                            File file = new File(path);
                            long length = file.length();
                            if (length > 1024 * 1000) {
                                showToast("凭证文件大小请不要超过1MB，当前大小:" + FileUtil.getFormatSize(file.length()));
                            } else {
//                                showToast("文件路径 : " + path + "    \n 文件大小 : " + FileUtil.getFormatSize(file.length()));
//                                boolean isImage = path.toLowerCase().endsWith("png") || path.toLowerCase().endsWith("jpg");
                                loadFile(path);
//                                startPhotoCrop(path);
                            }
                        } else {
                            showToast("文件选择失败，请重试");
                        }
                    } catch (Exception e) {
                        if (URLConstant.FORCE_LOG) {
                            e.printStackTrace();
                        }
                        showToast("文件选择失败，请重试");
                    }
                    break;
            }
        }
    }

    /**
     * 显示文件
     */
    private void loadFile(String path) {
        try {
            uploadPath = path;
            uploadName = getNameByPath(uploadPath);

            SLog.i(String.valueOf("本地路径：" + uploadPath));

            // 先压缩图片
            compressImage();
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            SLog.e("上传页面，文件加载异常");
        }
    }

    private void compressImage() {
        progressView.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 异步图片压缩
                String tempPath = FileUtil.getDefaultPath() + "tempFile/";
                String tempName = "file_" + CommonUtil.dateFormat(new Date(), "yyyyMMdd-HHmmss") + ".jpg";

                boolean result = ImageLoadUtil.compressAndSaveTo(uploadPath, 200, tempPath, tempName);

                if (result) {
                    uploadPath = tempPath + tempName;
                    uploadName = tempName;
                    if (!uploadName.toLowerCase().endsWith(".jpg") && !uploadName.toLowerCase().endsWith(".png")) {
                        uploadName += ".jpg";
                    }
                    compressHandler.sendEmptyMessage(0);
                } else {
                    compressHandler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    // 图片压缩回调
    @SuppressLint("HandlerLeak")
    Handler compressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressView.setVisibility(View.GONE);

            if (msg.what == 0) {
                SLog.i(String.valueOf("本地路径：" + uploadPath));

                // 显示压缩之后的图片
                if (isHeader) {
                    loadHeader(uploadPath);
                } else {
                    ImageLoadUtil.loadImageWithoutCache(BasePhotoPickActivity.this, uploadPath, ivPhoto, R.drawable.drawable_empty);
                    uploadStream = FileUtil.readSDFile(uploadPath);
                }
            } else {
                showToast("文件压缩失败，请选择其他文件");
            }
        }
    };

    /**
     * 加载头像
     */
    protected void loadHeader(String path) {
    }

    /**
     * 裁剪图片方法实现
     */
    public void startPhotoCrop(String url) {
        Intent intent = new Intent("com.android.activity_capture.action.CROP");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(getFileUri(url), "image/*");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setDataAndType(Uri.fromFile(new File(getPath(this, Uri.fromFile(new File(url))))), "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", PIC_ASPECT_X);
        intent.putExtra("aspectY", PIC_ASPECT_Y);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", PIC_OUTPUT_X);
        intent.putExtra("outputY", PIC_OUTPUT_Y);
        intent.putExtra("return-data", false);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getFileUri(cropPath));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    // 以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 从文件路径获取文件名称
     *
     * @param path 路径
     * @return 名称
     */
    private String getNameByPath(@NonNull String path) {
        String[] strs = path.split("/");
        return strs[strs.length - 1];
    }
}
