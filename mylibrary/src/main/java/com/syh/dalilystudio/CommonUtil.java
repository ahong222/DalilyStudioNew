package com.syh.dalilystudio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shenyh on 2016-06-22.
 */
public class CommonUtil {
    private static final String TAG = "CommonUtil";
    public static int sScreenHeight = 0;
    public static int sScreenWidth = 0;
    private static SimpleDateFormat differentYearFormat = new SimpleDateFormat("yyyy年M月dd日");
    private static SimpleDateFormat sameDayFormat = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat sameYearFormat = new SimpleDateFormat("M月dd日");
    private static String sCacheDir = null;

    public static String getHistoryTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        int argYear = calendar.get(Calendar.YEAR);
        int argMonth = calendar.get(Calendar.MONTH);
        int argDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;

        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH);
        int nowDay = now.get(Calendar.DAY_OF_MONTH) + 1;
        if (argYear != nowYear) {
            return differentYearFormat.format(new Date(time));
        } else {
            if (argMonth == nowMonth && argDay == nowDay) {
                return sameDayFormat.format(new Date(time));
            } else {
                return sameYearFormat.format(new Date(time));
            }
        }
    }

    /**
     * 将字符串转化成数字，一一对应关系，但不存在值的关联
     *
     * @param content
     * @return
     */
    public static int StringToInteger(String content) {
        LogUtil.d(TAG, "StringToInteger content:" + content);
        if (content == null) {
            return 0;
        }
        try {
            return Integer.parseInt(content);
        } catch (Exception e) {
        }

        try {
            String[] numbers = content.split(",");
            return Integer.parseInt(numbers[0]);
        } catch (Exception e) {
        }

        return content.hashCode();
    }

    public static String UnicodeToHex(String content) {
        String enUnicode = null;
        for (int i = 0; i < content.length(); i++) {
            if (i == 0) {
                enUnicode = getHexString(Integer.toHexString(content.charAt(i)).toUpperCase());
            } else {
                enUnicode = enUnicode + getHexString(Integer.toHexString(content.charAt(i)).toUpperCase());
            }
        }
        return enUnicode;
    }

    private static String getHexString(String hexString) {
        String hexStr = "";
        for (int i = hexString.length(); i < 4; i++) {
            if (i == hexString.length())
                hexStr = "0";
            else
                hexStr = hexStr + "0";
        }
        return hexStr + hexString;
    }

    public static int getScreenHeight(Context context) {
        if (sScreenHeight <= 0) {
            sScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        }
        return sScreenHeight;
    }

    public static int getScreenWidth(Context context) {
        if (sScreenWidth <= 0) {
            sScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        }
        return sScreenWidth;
    }

    public static Bitmap getZoomBitmap(String filePath, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        float inSampleSize = 1.0F;
        if (srcWidth > maxWidth) {
            inSampleSize = Math.max(inSampleSize, (float) srcWidth / (float) maxWidth);
        }

        if (srcHeight > maxHeight) {
            inSampleSize = Math.max(inSampleSize, (float) srcHeight / (float) maxHeight);
        }
        options.inSampleSize = Math.round(inSampleSize);
        options.inJustDecodeBounds = false;
        if (GlobalAppData.isDebug())
            LogUtil.d("debug", "getZoomBitmap maxWidth:" + maxWidth + " maxHeight;" + maxWidth + " srcWidth:" + srcWidth + " srcHeight:" + srcHeight + " inSampleSize:" + options.inSampleSize);

        try {
            return BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError var8) {
            var8.printStackTrace();
            LogUtil.w("", "getZoomBitmap e:" + var8.getMessage(), new Object[0]);
            return null;
        }
    }

    public static String getCacheDir() {
        if (sCacheDir == null) {
            sCacheDir = GlobalAppData.getContext().getExternalFilesDir("").getAbsolutePath();
        }
        return sCacheDir;
    }

    public static String getIconText(String userName, int leftCount) {
        if (userName == null) {
            return "";
        }
        if (userName.length() <= leftCount) {
            return userName;
        }
        return userName.substring(userName.length() - leftCount, userName.length());
    }

    public static void hideSoftInputMethod(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getProcessName() {
        File f = new File("/proc/self/cmdline");
        InputStream reader = null;
        try {
            reader = new FileInputStream(f);
            byte[] buffer = new byte[256];
            int length = reader.read(buffer);
            if (length > 0) {
                return new String(buffer, 0, length).trim();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "proc/self/cmdline not found");
        } catch (IOException e) {
            Log.e(TAG, "read cmdline error");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return "";
    }

    public static String getShortProcessName(String processName) {
        String[] items = processName.split(":");
        if (items.length == 2)
            return items[1];
        return "";
    }
}
