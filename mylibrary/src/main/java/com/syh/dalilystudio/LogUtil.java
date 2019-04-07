package com.syh.dalilystudio;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;

public class LogUtil {

    private static final String THREAD_TAG = "thread:";
    public static int LOG_INFO = 0;
    public static int LOG_DEBUG = 1;
    public static int LOG_WARNING = 2;
    public static int LOG_ERROR = 3;
    public static int LOG_FORBIDDEN = 4;
    private static File file = null;
    private static String LogTag = "syh";
    private static int sLogLevel = LOG_FORBIDDEN;
    private static boolean sWriteFile = false;
    private static String logpath = "/Log/";

    /**
     * 设置Log 的Filter Tag
     *
     * @param tag
     */
    public static void setTag(String tag) {
        LogTag = tag;
    }

    /**
     * 设置log开关
     *
     * @param minLogLevel 打日志的最低级别，如设置为LOG_INFO表示Utils.i()以及以上的日志都将会输出
     * @param writeFile   日志是否写入SD卡，为true时请在manifest.xml中增加android.permission.
     *                    WRITE_EXTERNAL_STORAGE
     */
    public static void setDebug(int minLogLevel, boolean writeFile) {
        sLogLevel = minLogLevel;
        sWriteFile = writeFile;
    }

    public static int getLogLevel() {
        return sLogLevel;
    }

    public static void i(String format, Object... args) {
        i(LogTag, format, args);
    }

    public static void i(String tag, String format, Object... args) {
        if (sLogLevel <= LOG_INFO) {
            String msg;
            try {
                msg = String.format(format, args);
            } catch (Exception e) {
                msg = format;
            }
            try {
                Log.i(tag, msg);
            } catch (Exception e) {
            }

            if (sWriteFile) {
                appendLog(tag, msg, LOG_INFO);
            }

        }
    }

    public static void d(String format, Object... args) {
        d(LogTag, format, args);
    }

    public static void d(String tag, String format, Object... args) {
        if (sLogLevel <= LOG_DEBUG) {
            String msg;
            try {
                msg = String.format(format, args);
            } catch (Exception e) {
                msg = format;
            }
            try {
                Log.i(tag, msg);
            } catch (Exception e) {
            }
            if (sWriteFile) {
                appendLog(tag, msg, LOG_DEBUG);
            }

        }
    }

    public static void w(String format, Object... args) {
        w(LogTag, format, args);
    }

    public static void w(String tag, String format, Object... args) {
        if (sLogLevel <= LOG_WARNING) {
            String msg;
            try {
                msg = String.format(format, args);
            } catch (Exception e) {
                msg = format;
            }
            try {
                Log.w(tag, msg);
            } catch (Exception e) {
            }
            if (sWriteFile) {
                appendLog(tag, msg, LOG_WARNING);
            }
        }
    }

    public static void e(String format, Object... args) {
        e(LogTag, format, args);
    }

    public static void e(String tag, String format, Object... args) {
        if (sLogLevel <= LOG_ERROR) {
            String msg = null;
            if (args != null && args.length > 0 && args[0] instanceof Throwable) {
                Throwable thr = (Throwable) args[0];
                msg = String.format(format, getMessage(thr));
            } else {
                msg = String.format(format, args);
            }

            Log.e(tag, msg);
            if (sWriteFile) {
                appendLog(tag, msg, LOG_ERROR);
            }
        }
    }

    public static void e(Throwable thr) {
        e(LogTag, thr);
    }

    public static void e(String tag, Throwable thr) {
        if (sLogLevel <= LOG_ERROR) {
            String msg = getMessage(thr);
            Log.e(tag, msg);
            appendLog(tag, msg, LOG_ERROR);
        }
    }

    public static void watchCrash() {
        if (sLogLevel <= LOG_ERROR) {
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    e("crash", ex);
                    Log.e("crash",
                            "catch uncaughtException,see file:"
                                    + (file == null ? "no file" : file
                                    .getAbsolutePath()));
                    ex.printStackTrace();
                    try {
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                        System.exit(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static String getMessage(Throwable thr) {
        String str;
        try {
            StringWriter localStringWriter = new StringWriter();
            PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
            thr.printStackTrace(localPrintWriter);
            for (Throwable localThrowable = thr.getCause(); localThrowable != null; localThrowable = localThrowable
                    .getCause()) {
                localThrowable.printStackTrace(localPrintWriter);
            }

            str = localStringWriter.toString();
            localPrintWriter.close();
        } catch (Exception e) {
            return thr.getMessage();
        }
        return str;
    }

    private static File initLogFile(File file) {
        try {
            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_MOUNTED)) {

                if (file == null) {
                    file = new File(CommonUtil.getCacheDir() + "/log/log-"
                            + DateTimeUtil.formatDate(new Date()) + ".txt");
                }

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();

                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                Log.d(LogTag, "initLogFile ,file is null:" + (file == null));
                return file;

            } else {
                Log.e(LogTag, "SD card not mounted,state:" + state);
            }
        } catch (Exception e) {
            Log.e(LogTag, "Exception when creat log file:" + e.toString());
        }
        return null;
    }

    private static void appendLog(String tag, String msg, int level) {

        BufferedWriter out = null;
        try {
            if (file == null || !file.exists() || !file.getName().contains(DateTimeUtil.formatDate(new Date()))) {
                file = initLogFile(file);
            }
            if (file == null) {
                Log.d("", "log file is null");
            }
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true), "UTF-8"), 8000);
            StringBuffer sb = new StringBuffer();
            sb.append(DateTimeUtil.formatFullDateTime(new Date()));
            sb.append("\t ");

            sb.append(tag).append("\t ").append(msg);
            sb.append("\t");
            sb.append("\t");
            sb.append(THREAD_TAG).append(Thread.currentThread().getId());
            sb.append("\r\n");
            out.write(sb.toString());
        } catch (Exception e) {
            Log.e(LogTag, "log output exception,error:" + getMessage(e));
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void shareRecentLogFile() {
        File dir = new File(CommonUtil.getCacheDir() + "/log");
        final String today = DateTimeUtil.formatDate(new Date());
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().contains(today);
            }
        });

        if (files == null || files.length == 0) {
            return;
        }
        ArrayList<Uri> shareList = new ArrayList<Uri>();
        shareList.add(Uri.fromFile(files[0]));
        FileUtil.shareMultiFiles(GlobalAppData.getContext().getApplicationContext(), shareList, "Send Log");
    }
}
