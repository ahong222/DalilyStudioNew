
package com.syh.dalilystudio;

import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;

public class DeviceUtil {

    /**
     * 获取WIFI下MAC地址，从没开启过WIFI可能返回空<br>
     * 需要android.permission.ACCESS_WIFI_STATE权限
     * 
     * @param context
     * @return
     */
    public static String getWlanMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 剩余可用空间 （byte）
     * 
     * @return
     */
    public static long getAvailableSpace() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            StatFs statFs = new StatFs(Environment
                    .getExternalStorageDirectory().getPath());
            long blocksize = statFs.getBlockSize();
            long availableblock = statFs.getAvailableBlocks() - 4;

            return (long) ((availableblock > 0 ? availableblock : 0) * 1.0
                    * blocksize * 1.0);
        }
        return 0;
    }

    public static Point getScreenMaxPoint(Context context) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        return new Point(displayMetrics.widthPixels,
                displayMetrics.heightPixels);
    }

    public static int getSDKLevel() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 网络链接是否畅通
     * 
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return info != null && info.isConnected();

    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return info.getType() == ConnectivityManager.TYPE_WIFI;
        }

        return false;
    }

    public static boolean getScreenShot(Context context, View view, String fileName) {

        view.setDrawingCacheEnabled(true);

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();

        if (bitmap != null) {

            try {

                FileOutputStream out = new FileOutputStream(fileName);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

                return true;
            } catch (Exception e) {

                e.printStackTrace();

            }

        } else {

        }

        return false;
    }

    /**
     * 获取厂商
     * 
     * @return
     */
    public static String getMenufacture() {
        return android.os.Build.MANUFACTURER;
    }

    /**
     * 获取IMEI，Requires Permission: READ_PHONE_STATE
     * 
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static Point getScreen(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return new Point(dm.widthPixels, dm.heightPixels);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public enum  NetWorkTypeEnum {
        NETWORK_TYPE_UNAVAILABLE,
        NETWORK_TYPE_UNKNOWN,
        NETWORK_TYPE_WIFI,
        NETWORK_TYPE_2G,
        NETWORK_TYPE_3G,
        NETWORK_TYPE_4G,
    }

    public static NetWorkTypeEnum getNetworkType(Context context) {
        try {
            final NetworkInfo network = ((ConnectivityManager)context
                    .getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable()
                    && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    return NetWorkTypeEnum.NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                                    Context.TELEPHONY_SERVICE);
                    getNetworkTypeByMobileType(telephonyManager.getNetworkType());
                }
            } else {
                return NetWorkTypeEnum.NETWORK_TYPE_UNAVAILABLE;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return NetWorkTypeEnum.NETWORK_TYPE_UNAVAILABLE;
    }

    private static NetWorkTypeEnum getNetworkTypeByMobileType(int mobileType) {
        switch (mobileType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NetWorkTypeEnum.NETWORK_TYPE_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NetWorkTypeEnum.NETWORK_TYPE_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NetWorkTypeEnum.NETWORK_TYPE_4G;
            default:
                return NetWorkTypeEnum.NETWORK_TYPE_UNKNOWN;
        }
    }
}
