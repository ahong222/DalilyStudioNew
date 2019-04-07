package com.syh.dalilystudio;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiUtil {

    /**
     * 是否使用的是WIFI
     * 
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * (WifiManager)context.getSystemService(Context.WIFI_SERVICE)
     * 
     * @param context
     * @return
     */
    public static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    
    /**
     * (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
     * @param context
     * @return
     */
    public static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

//    public static void closeWifi(WifiManager wifiManager) {
//        wifiManager.setWifiEnabled(false);
//    }

    /**
     * 创建热点
     * 
     * @param ssid
     *            热点名
     * @param pwd
     *            密码
     * @param delete
     *            有热点时是否删除
     */
    public static boolean createAP(WifiManager wifiManager, String ssid,
            String pwd, boolean delete) {
        WifiConfiguration exists = getExistingWifi(wifiManager, ssid);
        if (exists != null) {
            if(exists.status==WifiConfiguration.Status.CURRENT && ssid.equals(exists.SSID) && pwd.equals(exists.preSharedKey)){
                return true;
            }else{
                removeWifi(wifiManager, exists);
            }
        }

        //关闭wifi
        wifiManager.setWifiEnabled(false);
        // 如果已启用热点，先关闭
        if (getWifiApState(wifiManager)) {
            WifiConfiguration currentAp = getWifiApConfiguration(wifiManager);
            if (currentAp != null) {
                if(currentAp.status==WifiConfiguration.Status.CURRENT && ssid.equals(currentAp.SSID) && pwd.equals(currentAp.preSharedKey)){
                    return true;
                }else{
                    setWifiApEnabled(wifiManager, currentAp, false);
                }
            }
        }
        
        WifiConfiguration wifi = createWifiConfiguration(wifiManager, ssid,
                pwd, TYPE_WPA);
        return setWifiApEnabled(wifiManager, wifi, true);
    }

    /**
     * 使用反射调用隐藏的创建共享热点方法
     * 
     * @param wifiManager
     * @param paramWifiConfiguration
     * @param enable
     * @return true:success
     */
    public static boolean setWifiApEnabled(WifiManager wifiManager,
            WifiConfiguration paramWifiConfiguration, boolean enable) {
        try {
            Class<? extends WifiManager> localClass = wifiManager.getClass();
            Class<?>[] arrayOfClass = new Class[2];
            arrayOfClass[0] = WifiConfiguration.class;
            arrayOfClass[1] = Boolean.TYPE;
            Method localMethod = localClass.getMethod("setWifiApEnabled",
                    arrayOfClass);
            WifiManager localWifiManager = wifiManager;
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = paramWifiConfiguration;
            arrayOfObject[1] = Boolean.valueOf(enable);
            return (Boolean) localMethod
                    .invoke(localWifiManager, arrayOfObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    /**
//     * 设置wifi是否可用
//     * @param wifiManager
//     * @param enabled
//     */
//    public static void setWifiEnabled(WifiManager wifiManager,boolean enabled){
//        wifiManager.setWifiEnabled(enabled);
//    }
//    
//    /**
//     * wifi是否可用
//     * @param wifiManager
//     */
//    public static void isWifiEnabled(WifiManager wifiManager){
//        wifiManager.isWifiEnabled();
//    }
    
    /**
     * 获取存在的热点
     * 
     * @param wifiManager
     * @param ssid
     * @return may be null
     */
    public static WifiConfiguration getExistingWifi(WifiManager wifiManager,
            String ssid) {
        List<WifiConfiguration> netWorkList = wifiManager
                .getConfiguredNetworks();
        if (netWorkList == null) {
            return null;
        }
        String realSSID = "\"" + ssid + "\"";
        for (WifiConfiguration wifi : netWorkList) {
            if (wifi != null && realSSID.equals(wifi.SSID)) {
                return wifi;
            }

        }
        return null;
    }

    /**
     * 获取网络列表
     * @param wifiManager
     * @return
     */
    public static List<ScanResult> getWifiList(WifiManager wifiManager) {
        if(wifiManager.startScan()){
            return wifiManager.getScanResults();
        }
        return null;
    }

    /**
     * 将Wifi从Wifi列表中删除
     * 
     * @param wifiManager
     * @param wifi
     * @return
     */
    public static boolean removeWifi(WifiManager wifiManager,
            WifiConfiguration wifi) {
        return wifiManager.removeNetwork(wifi.networkId);
    }

    public static final int TYPE_NO_PASSWD = 1;
    public static final int TYPE_WEP = 2;
    public static final int TYPE_WPA = 3;

    /**
     * 
     * @param wifiManager
     * @param ssid
     * @param password
     * @param type
     * @param paramString3
     * @return
     */
    public static WifiConfiguration createWifiConfiguration(
            WifiManager wifiManager, String ssid, String password, int type) {
        Log.d("", "test createWifiInfo ssid" + ssid + " password:" + password
                + " type:" + type);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();
        // if (paramString3.equals("wt")) {
        // wifiConfiguration.SSID = ("\"" + ssid + "\"");
        // WifiConfiguration localWifiConfiguration2 =
        // getExistingWifi(wifiManager,ssid);
        // if (localWifiConfiguration2 != null)
        // wifiManager.removeNetwork(localWifiConfiguration2.networkId);
        // if (type == 1) {
        // wifiConfiguration.wepKeys[0] = "";
        // wifiConfiguration.allowedKeyManagement.set(0);
        // wifiConfiguration.wepTxKeyIndex = 0;
        // } else if (type == 2) {
        // wifiConfiguration.hiddenSSID = true;
        // wifiConfiguration.wepKeys[0] = ("\"" + password + "\"");
        // } else {
        // wifiConfiguration.preSharedKey = ("\"" + password + "\"");
        // wifiConfiguration.hiddenSSID = true;
        // wifiConfiguration.allowedAuthAlgorithms.set(0);
        // wifiConfiguration.allowedGroupCiphers.set(2);
        // wifiConfiguration.allowedKeyManagement.set(1);
        // wifiConfiguration.allowedPairwiseCiphers.set(1);
        // wifiConfiguration.allowedGroupCiphers.set(3);
        // wifiConfiguration.allowedPairwiseCiphers.set(2);
        // }
        // } else
        {
            wifiConfiguration.SSID = ssid;
            wifiConfiguration.allowedAuthAlgorithms.set(1);
            wifiConfiguration.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedKeyManagement.set(0);
            wifiConfiguration.wepTxKeyIndex = 0;
            if (type == 1) {
                wifiConfiguration.wepKeys[0] = "";
                wifiConfiguration.allowedKeyManagement.set(0);
                wifiConfiguration.wepTxKeyIndex = 0;
            } else if (type == 2) {
                wifiConfiguration.hiddenSSID = true;
                wifiConfiguration.wepKeys[0] = password;
            } else if (type == 3) {
                wifiConfiguration.preSharedKey = password;
                wifiConfiguration.allowedAuthAlgorithms.set(0);
                wifiConfiguration.allowedProtocols.set(1);
                wifiConfiguration.allowedProtocols.set(0);
                wifiConfiguration.allowedKeyManagement.set(1);
                wifiConfiguration.allowedPairwiseCiphers.set(2);
                wifiConfiguration.allowedPairwiseCiphers.set(1);
            }
        }
        return wifiConfiguration;
    }

    /**
     * 获取当前热点的SSID
     * 
     * @param wifiManager
     * @return
     */
    public static String getApSSID(WifiManager wifiManager) {
        try {
            Method localMethod = wifiManager.getClass().getDeclaredMethod(
                    "getWifiApConfiguration", new Class[0]);
            if (localMethod == null)
                return null;
            Object localObject1 = localMethod
                    .invoke(wifiManager, new Object[0]);
            if (localObject1 == null)
                return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null)
                return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class
                    .getDeclaredField("mWifiApProfile");
            if (localField1 == null)
                return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null)
                return null;
            Field localField2 = localObject2.getClass()
                    .getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null)
                return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    /**
     * 是否已启用热点
     * 
     * @param wifiManager
     * @return
     */
    public static boolean getWifiApState(WifiManager wifiManager) {
        try {
            return ((Boolean) wifiManager.getClass()
                    .getMethod("isWifiApEnabled", new Class[0])
                    .invoke(wifiManager, new Object[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static WifiConfiguration getWifiApConfiguration(
            WifiManager wifiManager) {
        try {
            return ((WifiConfiguration) wifiManager.getClass()
                    .getMethod("getWifiApConfiguration", new Class[0])
                    .invoke(wifiManager, new Object[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
