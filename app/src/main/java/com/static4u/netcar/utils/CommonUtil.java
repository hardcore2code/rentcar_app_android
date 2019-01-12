package com.static4u.netcar.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.static4u.netcar.R;
import com.static4u.netcar.constant.URLConstant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CommonUtil {

    /**
     * textView添加中划线
     */
    public static void addMidLine(TextView textView) {
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    /**
     * textView添加下划线
     */
    public static void addUnderLine(TextView textView) {
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    public static boolean isValidDate(String str) {
        boolean isDate = true;

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            isDate = false;
        }

        return isDate;
    }

    /**
     * 日期格式转换 20170101 -> 2017-01-01
     */
    public static String format6Date(String date) {
        try {
            if (CommonUtil.isEmptyOrNull(date)) {
                return "";
            }
            return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
        } catch (Exception e) {
            if (URLConstant.IS_DEBUG) {
                e.printStackTrace();
            }
        }

        return date;
    }

    /**
     * 获取随机颜色
     *
     * @return
     */
    public static int getRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * 从bitmap中获取指定位置颜色值
     *
     * @param x
     * @param y
     * @param bm
     * @return
     */
    public static int getPixColor(int x, int y, Bitmap bm) {
        if (bm == null || bm.getWidth() < 1 || bm.getHeight() < 1) {
            return 0;
        }
        if (x < 0 || x > bm.getWidth() || y < 0 || y > bm.getHeight()) {
            x = 1;
            y = 1;
        }
        int result = 0;

        int pixColor = bm.getPixel(x, y);
        int a, r, g, b;
        a = Color.alpha(pixColor);
        r = Color.red(pixColor);
        g = Color.green(pixColor);
        b = Color.blue(pixColor);
        result = Color.argb(a, r, g, b);
        return result;
    }

    /**
     * 隐藏手机号码中间4位
     *
     * @param phoneNum
     * @return
     */
    public static String getHalfHiddenPhoneNum(String phoneNum) {
        if (phoneNum != null && phoneNum.length() >= 11) {
            String front = phoneNum.substring(0, 3);
            String back = phoneNum.substring(7, phoneNum.length());

            return front + "****" + back;
        } else {
            SLog.e("CommonUtil", "手机号码错误");
            return "888****8888";
        }
    }

    /**
     * 字符串中包含汉字的个数
     *
     * @param str
     * @return
     */
    public static int haveFullChar(String str) {
        int count = 0;

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i + 1);
            Matcher m = p.matcher(temp);
            if (m.matches()) {
                count++;
            }
        }

        return count;
    }

    /**
     * 数字字符串四舍五入到整数字符串
     */
    public static String getRoundStr(String str) {
        String result = "0";
        if (!CommonUtil.isEmptyOrNull(str)) {
            if (str.contains("-")) {
                SLog.e("ERROR", "价格四舍五入失败，价格为负数");
                return result;
            }
            result = str;
            if (str.length() > 0 && TextUtils.isDigitsOnly(str.replace(".", ""))) {
                result = Math.round(Double.valueOf(str)) + "";
            }
        }
        return result;
    }

    /**
     * 完全退出程序
     *
     * @param context
     */
    // public static void exitApp(Context context) {
    // // 获取API Level
    // int apiLevel = android.os.Build.VERSION.SDK_INT;
    //
    // ActivityManager am = (ActivityManager)
    // context.getSystemService(context.ACTIVITY_SERVICE);
    // if (apiLevel >= 8) {
    // // 该方法在API Level >= 8时才能调用
    // am.killBackgroundProcesses(context.getPackageName());
    // } else if (apiLevel >= 3) {
    // // 该方法在API Level >= 3时才能调用
    // am.restartPackage(context.getPackageName());
    // }
    // System.exit(0);
    // }

    /**
     * sdカードのパスを取得する
     *
     * @return d
     */
    public static String getSDPath() {
        String sdDir = "";
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 获取跟目录
            sdDir = Environment.getExternalStorageDirectory().getPath();
        }
        return sdDir;

    }

    /**
     * 检查是否存在SDCard
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取应用当前版本号
     */
    public static String getAppVersionStr(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            return "Can not find version name";
        }
    }

    /**
     * 获取应用当前版本号
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static String getFormateDate(Long time) {
        Date currentTime = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//你需要的时间格式
        String formateTime = formatter.format(currentTime);//得到字符串类型的时间
        return formateTime;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * @param context
     * @return > 0 success; <= 0 fail
     */
    public static int getStatusHeight(Context context) {
        Rect frame = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 空文字列が判定する
     *
     * @param arg 文字列
     * @return 空文字列を判断する結果
     */
    public static boolean isEmptyOrNull(Object arg) {
        if (arg == null) {
            return true;
        }
        if ("".equals(arg.toString())) {
            return true;
        }
        if ("null".equals(arg.toString())) {
            return true;
        }
        return false;
    }

    /**
     * 画像の圧縮
     *
     * @param maxSize 最大サイズ(KB)
     * @return true 成功 false 失敗
     */
    public static boolean zoomImage(Bitmap bm, String folderName, String fileName, double maxSize) {

        if (bm == null || bm.isRecycled()) {
            return false;
        }
        boolean isSuccess = false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        try {
            baos.close();
            double bmSize = b.length / ((double) 1024);
            // if (bmSize > maxSize) {
            double i = bmSize / maxSize;
            float scale = (float) (1 / Math.sqrt(i));
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bmNew = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            if (bmNew != null) {
                File folder = new File(folderName);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                File file = new File(folder, fileName);
                FileOutputStream out = new FileOutputStream(file);
                if (bmNew.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    isSuccess = true;
                }
                out.flush();
                out.close();
                bmNew.recycle();
                bmNew = null;
            }
            // } else {
            // File folder = new File(folderName);
            // if (!folder.exists()) {
            // folder.mkdirs();
            // }
            // File file = new File(folder, fileName);
            // FileOutputStream out = new FileOutputStream(file);
            // if (bm.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
            // isSuccess = true;
            // }
            // out.flush();
            // out.close();
            // }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    public static Bitmap decodeFile(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;
        // try{
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // opts.inSampleSize = bmScale;
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        // Calculate inSampleSize
        int inSampleSize = CommonUtil.calculateInSampleSize(opts, reqWidth, reqHeight);
        opts.inSampleSize = inSampleSize;
        // System.out.println("options.inSampleSize : " + opts.inSampleSize);

        // Decode bitmap with inSampleSize set
        opts.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, opts);
        try {
            ExifInterface exifInterface;
            exifInterface = new ExifInterface(path);
            int i = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            if (i == ExifInterface.ORIENTATION_ROTATE_90 || i == ExifInterface.ORIENTATION_ROTATE_180 || i == ExifInterface.ORIENTATION_ROTATE_270) {
                int j = 0;
                if (i == ExifInterface.ORIENTATION_ROTATE_90) {
                    j = 90;
                } else if (i == ExifInterface.ORIENTATION_ROTATE_180) {
                    j = 180;
                } else if (i == ExifInterface.ORIENTATION_ROTATE_270) {
                    j = 270;
                }
                Matrix localMatrix = new Matrix();
                localMatrix.postRotate(j);
                try {
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), localMatrix, true);
                } catch (OutOfMemoryError e) {
                    System.gc();
                    // OutputErrorLog log = new
                    // OutputErrorLog(errMsg.toString(),
                    // Constant.LOG_TYPE_ERROR);
                    // log.execute();
                    return bm;
                }
            }
        } catch (IOException e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        //
        // Log.e("ooohhh", "temp-hhh--"+temp.getHeight());
        // Log.e("ooowww", "temp-www--"+temp.getWidth());
        // } catch (OutOfMemoryError e) {
        // if (bm != null) {
        // bm.recycle();
        // System.gc();
        // }
        // bmScale = bmScale*2;
        // if (bmScale <= 2) {
        // bmScale = 4;
        // }
        // Log.e("ERROR", "============OutOfMemoryError===============");
        // bm = decodeFile(path);
        // }
        return bm;
    }

    public static boolean containsEmoji(String str) {
        boolean contain = false;
        for (int i = 0; i < str.length(); i++) {
            if (!notEmojiCharacter((str.charAt(i)))) {
                return true;
            }
        }
        return contain;
    }

    private static boolean notEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        // 横方向の倍数
        float scaleX = reqWidth / (float) width;
        // 縦方向の倍数
        float scaleY = reqHeight / (float) height;
        float scale;
        // 一番小さい倍数を取得する
        if (scaleX < scaleY) {
            scale = scaleX;
        } else {
            scale = scaleY;
        }
        // 一番小さい倍数で拡大する
        reqWidth = (int) (width * scale);
        int inSampleSize = 1;

        if (width > reqWidth) {

            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean isIp(String ipStr) {
        String ip = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipStr);
        return matcher.matches();
    }

    public static String dateFormat(Date date, String format) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.CHINA);
        String dateString = df.format(date);
        return dateString;
    }

    public static String dateFormat(long time, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.CHINA);
        String dateString = df.format(time * 1000);
        return dateString;
    }

    /**
     * GPSの状態を確認
     *
     * @param context アクティビティ
     * @return GPSの状態『true：入；false：切』
     */
    public static boolean isGpsEnable(Context context) {

        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);

        return gpsStatus;
    }

    /**
     * 対象を文字列に変換する
     *
     * @param obj 対象
     * @return 文字列
     */
    public static String objectToString(Object obj) {
        String result = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            result = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (Exception e) {
                    if (URLConstant.FORCE_LOG) {
                        e.printStackTrace();
                    }
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (Exception e) {
                    if (URLConstant.FORCE_LOG) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    /**
     * 文字列を対象に変換する
     *
     * @param str 文字列
     * @return 対象
     */
    public static Object stringToObject(String str) {
        Object obj = null;
        byte[] mobileBytes = Base64.decode(str.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (Exception e) {
                    if (URLConstant.FORCE_LOG) {
                        e.printStackTrace();
                    }
                }
            }
            if (byteArrayInputStream != null) {
                try {
                    byteArrayInputStream.close();
                } catch (Exception e) {
                    if (URLConstant.FORCE_LOG) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return obj;
    }

    /**
     * デバイスの解像度により、ｄｐ単位からｐｘ単位に変換する。
     */
    public static int dipTopx(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        if (context == null) {
            return 0;
        }
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    // 获取随机数
    public static int getRandomNum(int min, int max) {

        Random random = new Random();
        int num = random.nextInt(max);
        if (num < min) {

            num = getRandomNum(min, max);
        }

        return num;
    }

    // 图片压缩
    public static Bitmap getCompressBitmap(String path) {

        float height = 400;
        float width = 500;

        // bitmap属性
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只读图片的宽高，此时把options.inJustDecodeBounds 设回true 注意：bitmap为null
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int bWidth = options.outWidth;
        int bHeight = options.outHeight;

        // 　1表示不缩放
        int zoom = 1;
        // 　如果宽度大的话根据宽度固定大小缩放
        if (bWidth > bHeight && bWidth > width) {

            zoom = (int) (bWidth / width);
        } else if (bHeight > bWidth && bHeight > height) {

            zoom = (int) (bHeight / height);
        }

        // 开始读入图片，此时把options.inJustDecodeBounds 设回false 注意：bitmap不为null
        options.inJustDecodeBounds = false;
        options.inSampleSize = zoom;
        bitmap = BitmapFactory.decodeFile(path, options);

        return compressImage(bitmap);
    }

    // 压缩bitmap
    public static Bitmap getCompressBitmap(Bitmap image) {

        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放

            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放

            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    // 质量压缩
    private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    // 防止二次点击
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 调用系统分享，自定义物品名称
     *
     * @param context
     * @param title
     */
    public static void shareBySystem(Context context, String url, String title) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("text/plain");
        it.putExtra(Intent.EXTRA_SUBJECT, title);
        it.putExtra(Intent.EXTRA_TEXT, "我在西集网看到了［" + title + "］，赶快去看看吧！\n" + url);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(it, "分享"));
    }

    /**
     * 部分文字变颜色
     *
     * @param str       元字符串
     * @param color     颜色
     * @param targetStr 变颜色的文字
     */
    public static SpannableStringBuilder changeColorOfPartTxt(String str, int color, String... targetStr) {
        SpannableStringBuilder strSpan = new SpannableStringBuilder(str);
        int end = 0;
        for (int i = 0; i < targetStr.length; i++) {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            String target = targetStr[i];
            int start = str.indexOf(target, end);
            end = start + target.length();
            strSpan.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }

        return strSpan;
    }

    //应用分发渠道
    public static String getApplicationMeta(Context context) {
        String msg = "";
        try {
            ApplicationInfo appInfo;
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (NameNotFoundException e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return msg;
    }

    public static String getContent(String src, String startTag, String endTag) {
        String content = src;
        int start = src.indexOf(startTag);
        start += startTag.length();

        try {
            if (endTag != null) {
                int end = src.indexOf(endTag);
                int i = 0;
                while (start > end) {
                    end = src.indexOf(endTag, i);
                    i++;
                }
                content = src.substring(start, end);
            } else {
                content = src.substring(start);
            }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }

        return content;
    }

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String createRandomStr(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }


    /**
     * 创建桌面快捷方式
     *
     * @param context
     */
    public static void createShortCut(Context context) {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 设置快捷方式图片
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));
        // 设置快捷方式名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        // 设置是否允许重复创建快捷方式 false表示不允许
        intent.putExtra("duplicate", false);
        // 设置快捷方式要打开的intent
        // 第一种方法创建快捷方式要打开的目标intent
        Intent targetIntent = new Intent();
        // 设置应用程序卸载时同时也删除桌面快捷方式
        targetIntent.setAction(Intent.ACTION_MAIN);
        targetIntent.addCategory("android.intent.category.LAUNCHER");
        ComponentName componentName = new ComponentName(context.getPackageName(), context.getClass().getName());
        targetIntent.setComponent(componentName);
        // 第二种方法创建快捷方式要打开的目标intent
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, targetIntent);
        // 发送广播
        context.sendBroadcast(intent);
    }

    /**
     * 检查权限
     */
    public static boolean checkPermission(Activity activity, String permission) {
        PackageManager pm = activity.getPackageManager();
        boolean result = pm.checkPermission(permission, activity.getPackageName()) == PackageManager.PERMISSION_GRANTED;

        SLog.e("permission", permission + " : " + result);

        return result;
    }

    /**
     * 隐藏手机号
     *
     * @param phone
     * @return
     */
    public static String getCoveredPhoneNum(String phone) {
        String result = phone;
        try {
            String s = phone.substring(0, 3);
            String e = phone.substring(phone.length() - 4);
            result = s + " **** " + e;
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 是否同时包含字母和数字
     *
     * @param res
     * @return
     */
    public static boolean isContainDigitAndLetter(String res) {
        boolean isLetter = false;
        boolean isDigit = false;

        for (int i = 0; i < res.length(); i++) {
            if (Character.isLetter(res.charAt(i))) {
                isLetter = true;
            }
            if (Character.isDigit(res.charAt(i))) {
                isDigit = true;
            }
        }

        return isDigit && isLetter;
    }

    /**
     * 打开文件夹
     *
     * @param context
     * @param path
     */
    public static void openAssignFolder(Context context, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
            Toast.makeText(context, "文件路径： \n" + path, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 判断apk是否安装
     *
     * @param context
     * @param uri
     * @return
     */
    public static boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    /**
     * 显示通知
     *
     * @param context
     * @param title
     * @param msg
     * @param extrasMsg
     * @param pendingIntent
     */
    public static void customPushMessage(Context context, String title, String msg, String extrasMsg, PendingIntent pendingIntent) {
        try {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle(title)//设置通知栏标题
                    .setContentText(msg) //设置通知栏显示内容
//                    .setContentIntent(pendingIntent) //设置通知栏点击意图
                    .setFullScreenIntent(pendingIntent, true)
                    //  .setNumber(number) //设置通知集合的数量
//                    .setTicker(extrasMsg) //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                    .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
            mNotificationManager.notify(90, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开拨号页面
     *
     * @param phoneStr 电话号码
     */
    public static void callPhone(Context context, String phoneStr) {
        if (!phoneStr.startsWith("tel:")) {
            phoneStr = "tel:" + phoneStr;
        }
        Intent contactIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneStr));
        context.startActivity(contactIntent);
        ((Activity) context).overridePendingTransition(R.anim.bottom_in, R.anim.push_stay);
    }
}
