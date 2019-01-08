package com.static4u.netcar.utils;

import android.content.Context;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.static4u.netcar.constant.URLConstant;

import java.io.File;

public class CacheManagerUtil {

    /**
     * 获取APP缓存大小
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getTotalCacheSize(Context context) {
        // 获取文件
        // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/目录，一般放一些长时间保存的数据
        // Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
        long cacheSize = 0;
        try {
            cacheSize = FileUtil.getFolderSize(context.getCacheDir());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                cacheSize += FileUtil.getFolderSize(context.getExternalCacheDir());
            }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return FileUtil.getFormatSize(cacheSize);
    }

    /**
     * 清除APP缓存
     *
     * @param context
     */
    public static void clearAllCache(Context context) {
        // 清除APP缓存
        FileUtil.deleteDir(context.getCacheDir());

        // 清除APP外存储缓存
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileUtil.deleteDir(context.getExternalCacheDir());
        }

        try {
            // 清除webView缓存
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");

            String cacheDir = "cache/";
            //WebView 缓存文件
            File appCacheDir = new File(cacheDir);

            String wbDir = context.getCacheDir().getAbsolutePath() + "/webviewCache";
            File webviewCacheDir = new File(wbDir);

            //删除webview 缓存目录
            if (webviewCacheDir.exists()) {
                context.deleteFile(cacheDir);
            }
            //删除webview 缓存 缓存目录
            if (appCacheDir.exists()) {
                context.deleteFile(wbDir);
            }
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清除cookie
     *
     * @param context
     */
    public static void clearCookies(Context context) {
        // Edge case: an illegal state exception is thrown if an instance of
        // CookieSyncManager has not be created.  CookieSyncManager is normally
        // created by a WebKit view, but this might happen if you start the
        // app, restore saved state, and click logout before running a UI
        // dialog in a WebView -- in which case the app crashes
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

//    /**
//     * 同步一下cookie
//     */
//    public static void synCookies(Context context, String url) {
//        CookieSyncManager.createInstance(context);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
//        cookieManager.setCookie(url, cookies);//指定要修改的cookies
//        CookieSyncManager.getInstance().sync();
//    }
}