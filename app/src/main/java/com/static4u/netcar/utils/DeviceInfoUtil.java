package com.static4u.netcar.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.static4u.netcar.constant.URLConstant;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * 设备信息获取
 * Created by tancongcong on 15/9/9.
 */
public class DeviceInfoUtil {

    //设备型号
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    //品牌
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    //厂商
    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    //系统版本
    public static String getDevicRelease() {
        return Build.VERSION.RELEASE;
    }

    //序列号（SN）
    public static String getDevicSN() {
        return Build.SERIAL;
    }

    //SDK或API版本
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    //国际移动设备识别码（IMEI）
    public static String getDevicIMEI(Context context) {
        TelephonyManager telephonemanager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonemanager.getDeviceId();
    }

    //激活日期
    public static String getFirstInstallTime(Context context) {
        String pkName = context.getPackageName();
        String firstInstallTime = "";
        try {
            Long time = context.getPackageManager().getPackageInfo(pkName, 0).firstInstallTime;
            Date currentTime = new Date(time);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//你需要的时间格式
            firstInstallTime = formatter.format(currentTime);//得到字符串类型的时间
        } catch (PackageManager.NameNotFoundException e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return firstInstallTime;
    }

    //网卡（MAC）地址
    public static String getLocalMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }

    // 设备编号
    public static String getDeviceId(Context context) {
        try {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            return device_id;
        } catch (Exception e) {
            if (URLConstant.FORCE_LOG) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取设备ip
     *
     * @return
     */
    public static String getIP() {
        String IP = "";
        StringBuilder IPStringBuilder = new StringBuilder();
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaceEnumeration.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                while (inetAddressEnumeration.hasMoreElements()) {
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress() &&
                            !inetAddress.isLinkLocalAddress() &&
                            inetAddress.isSiteLocalAddress()) {
                        IPStringBuilder.append(inetAddress.getHostAddress()+ "\n");
                    }
                }
            }
        } catch (SocketException ex) {

        }

        IP = IPStringBuilder.toString();
        return IP;
    }

    /**
     * 设备网络类型
     * 网络类型（1：2G, 2:3G, 3:4G, 4:WIFI, 0:其他）
     *
     * @param context
     * @return
     */
    public static String getNetType(Context context) {
        String type = "0";
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                // WIFI
                type = "4";
            } else {
                // 手机网络
                if (info.getSubtype() == NETWORK_TYPE_CDMA || info.getSubtype() == NETWORK_TYPE_GPRS || info.getSubtype() == NETWORK_TYPE_EDGE) {
                    // 2G
                    type = "1";
                } else if (info.getSubtype() == NETWORK_TYPE_UMTS || info.getSubtype() == NETWORK_TYPE_HSDPA || info.getSubtype() == NETWORK_TYPE_EVDO_0 || info.getSubtype() == NETWORK_TYPE_EVDO_A) {
                    // 3G
                    type = "2";
                } else {
                    // 4G
                    type = "3";
                }
            }
        }

        return type;
    }

    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_IDEN = 11;
}
