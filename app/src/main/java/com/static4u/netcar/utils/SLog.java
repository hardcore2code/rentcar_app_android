package com.static4u.netcar.utils;

import android.util.Log;

import com.static4u.netcar.constant.URLConstant;

import static com.static4u.netcar.constant.URLConstant.FORCE_LOG;

/**
 * Log输出
 */
public class SLog {

    /**
     * LOG文字.
     */
    public static final String TAG = "SLog";

    /**
     * 错误log输出
     *
     * @param tag 标签
     * @param msg log
     */
    public static void e(String tag, String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        Log.e(tag, msg);
    }

    /**
     * 错误log输出
     *
     * @param msg log
     */
    public static void e(String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        SLog.e(TAG, msg);
    }

    /**
     * Debug log输出
     *
     * @param tag 标签
     * @param msg log
     */
    public static void d(String tag, String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        Log.d(tag, msg);
    }

    /**
     * Debug log输出
     *
     * @param msg log
     */
    public static void d(String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        SLog.d(TAG, msg);
    }

    /**
     * Info log输出
     *
     * @param tag 标签
     * @param msg log
     */
    public static void i(String tag, String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        Log.i(tag, msg);
    }

    /**
     * Info log输出
     *
     * @param msg log
     */
    public static void i(String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        SLog.i(TAG, msg);
    }

    /**
     * 警告 log输出
     *
     * @param tag 标签
     * @param msg log
     */
    public static void w(String tag, String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        Log.w(tag, msg);
    }

    /**
     * 警告 log输出
     *
     * @param msg log
     */
    public static void w(String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        SLog.w(TAG, msg);
    }

    /**
     * Verbose log输出
     *
     * @param tag 标签
     * @param msg log
     */
    public static void v(String tag, String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        Log.v(tag, msg);
    }

    /**
     * Verbose log输出
     *
     * @param msg log
     */
    public static void v(String msg) {
        if (!URLConstant.IS_DEBUG && !FORCE_LOG) {
            return;
        }
        SLog.v(TAG, msg);
    }

}
