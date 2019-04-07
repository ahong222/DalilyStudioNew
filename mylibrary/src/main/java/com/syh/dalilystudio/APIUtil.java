package com.syh.dalilystudio;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by shenyh on 2016-06-26.
 */
public class APIUtil {
    public static Drawable getDrawable(Context context, int drawableId) {
        if (Build.VERSION.SDK_INT < 21) {
            return context.getDrawable(drawableId);
        } else {
            return context.getResources().getDrawable(drawableId, context.getTheme());
        }

    }

    public static int getColor(Context context, int colorId) {
        if (Build.VERSION.SDK_INT < 23) {
            return context.getResources().getColor(colorId);
        } else {
            return context.getResources().getColor(colorId, context.getTheme());
        }

    }
}
