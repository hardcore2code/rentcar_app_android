package com.static4u.netcar.business;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.static4u.netcar.utils.DESUtils;

public class SharedData {
    private static final String APP_DATA = "netCar";
    public static final String KEY_PHONE = "key_phone";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_PWD = "key_pwd";
    public static final String KEY_HEADER = "key_header";

    /**
     * 保存用户信息
     */
    public static void setUserData(Context context, String phone, String pwd, String name, String header) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(APP_DATA, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(KEY_PHONE, DESUtils.encrypt(phone));
        editor.putString(KEY_NAME, DESUtils.encrypt(name));
        editor.putString(KEY_PWD, DESUtils.encrypt(pwd));
        editor.putString(KEY_HEADER, DESUtils.encrypt(header));
        editor.apply();
    }

    /**
     * 获取手机号
     */
    public static String getUserPhone(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(APP_DATA, Activity.MODE_PRIVATE);
        return DESUtils.decrypt(mySharedPreferences.getString(KEY_PHONE, ""));
    }

    /**
     * 获取用户名
     */
    public static String getUserName(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(APP_DATA, Activity.MODE_PRIVATE);
        return DESUtils.decrypt(mySharedPreferences.getString(KEY_NAME, "未设置"));
    }

    /**
     * 获取密码
     */
    public static String getUserPwd(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(APP_DATA, Activity.MODE_PRIVATE);
        return DESUtils.decrypt(mySharedPreferences.getString(KEY_PHONE, ""));
    }

    public static void setStr(Context context, String key, String value) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(APP_DATA, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(key, DESUtils.encrypt(value));
        editor.apply();
    }

    public static String getStr(Context context, String key) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(APP_DATA, Activity.MODE_PRIVATE);
        return DESUtils.decrypt(mySharedPreferences.getString(key, ""));
    }
}
