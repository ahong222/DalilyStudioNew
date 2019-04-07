package com.syh.dalilystudio;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppUtil {

    /**
     * 获取Meta字符串
     * 
     * @param context
     * @param key
     * @return
     */
    public static String getMetaString(Context context, String key) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return applicationInfo.metaData.get(key).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static int getMetaInteger(Context context, String key) throws Exception {
        ApplicationInfo applicationInfo = context.getPackageManager()
                .getApplicationInfo(context.getPackageName(),
                        PackageManager.GET_META_DATA);
        return Integer.parseInt(applicationInfo.metaData.get(key).toString());
    }
    
    /**
     * 获取VersionName
     * @param context
     * @return 未找到返回""
     */
    public static String getAppVersion(Context context){
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }
    
    
}
