package com.syh.dalilystudio;

import android.content.Context;

public class GlobalAppData {
    private static Context sContext;
    private static boolean mIsDebug = false;

    public static Context getContext() {
        return sContext;
    }

    public static void init(Context context, boolean isDebug) {
        GlobalAppData.sContext = context;
        GlobalAppData.mIsDebug = isDebug;

    }

    public static boolean isDebug() {
        return mIsDebug;
    }
}
