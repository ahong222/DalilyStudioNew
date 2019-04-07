package com.syh.dalilystudio;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceUtil {

    public static final String KEY_LOGIN_INFO = "login_info";
    private static SharedPreferenceUtil instance;
    private SharedPreferences mDefaultSharedPreferences;

    private SharedPreferenceUtil() {
        Context context = GlobalAppData.getContext().getApplicationContext();
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferenceUtil getInstance() {
        if (instance == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (instance == null) {
                    instance = new SharedPreferenceUtil();
                }
            }
        }
        return instance;
    }

    private static void onDestroy() {
        instance = null;
    }

    public static void destroy() {
        synchronized (SharedPreferenceUtil.class) {
            if (instance == null) {
                instance.onDestroy();
            }
        }
    }

    /**
     * 保存登陆信息
     * @param loginInfo
     */
    public void saveLoginInfo(String loginInfo) {
        mDefaultSharedPreferences.edit().putString(KEY_LOGIN_INFO, loginInfo).apply();
    }

    public String getLoginInfo() {
        return mDefaultSharedPreferences.getString(KEY_LOGIN_INFO, null);
    }

    /**
     * 获取设置数据
     *
     * @param type {@link com.t01.dida.app.setting.SettingConstants#TYPE_FRONT_CAMERA}
     * @param defaultValue
     * @return
     */
    public boolean getSettingBoolean(String type, boolean defaultValue) {
        return mDefaultSharedPreferences.getBoolean(type, defaultValue);
    }

    public int getSettingInt(String type, int defaultValue) {
        return mDefaultSharedPreferences.getInt(type, defaultValue);
    }

    public long getSettingLong(String type, long defaultValue) {
        return mDefaultSharedPreferences.getLong(type, defaultValue);
    }

    public float getSettingFloat(String type, float defaultValue) {
        return mDefaultSharedPreferences.getFloat(type, defaultValue);
    }

    public String getSettingString(String type, String defaultValue) {
        return mDefaultSharedPreferences.getString(type, defaultValue);
    }

    /**
     * 保存设置数据
     *
     * @param type {@link com.t01.dida.app.setting.SettingConstants#TYPE_FRONT_CAMERA}
     * @param value
     * @return
     */
    public void setSettingBoolean(String type, boolean value) {
        mDefaultSharedPreferences.edit().putBoolean(type, value).apply();
    }

    public void setSettingInt(String type, int value) {
        mDefaultSharedPreferences.edit().putInt(type, value).apply();
    }

    public void setSettingString(String type, String value) {
        mDefaultSharedPreferences.edit().putString(type, value).apply();
    }

    public void setSettingLong(String type, long value) {
        mDefaultSharedPreferences.edit().putLong(type, value).apply();
    }

    public void setSettingFloat(String type, float value) {
        mDefaultSharedPreferences.edit().putFloat(type, value).apply();
    }
}
