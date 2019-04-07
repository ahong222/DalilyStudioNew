package com.syh.dalilystudio;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FileUtil {

    public static byte[] readFile(String filePath) throws Exception {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(filePath));
            return readFromStream(fis);
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static byte[] readFromStream(InputStream inputStream) throws Exception {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(inputStream);
        } catch (Exception e) {
            throw e;
        }

        byte[] buffer = new byte[4096];
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            int count = 0;
            while ((count = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }

            return baos.toByteArray();
        } finally {
            if (baos != null) {
                baos.flush();
            }
        }
    }

    public static boolean shareMultiFiles(Context context, ArrayList<Uri> fileList, String title) {
        if (fileList == null || fileList.size() == 0) {
            return false;
        }
        for (int i = fileList.size() - 1; i >= 0; i--) {
            Uri uri = fileList.get(i);
            if (uri.getPath().startsWith("/data/")) {
                File targetFile = new File(context.getExternalFilesDir("cache") + File.separator + new File(uri.getPath()).getName());
                copyFile(new File(uri.getPath()), targetFile);
                fileList.remove(i);
                fileList.add(Uri.fromFile(targetFile));
            }
        }

        Intent shareIntent = new Intent();

        if (fileList.size() > 1) {
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileList);
        } else {
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileList.get(0));
        }

        shareIntent.setType("*/*");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent intent = Intent.createChooser(shareIntent, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFile(File srcFile, File targetFile) {
        try {
            return copyFile(new FileInputStream(srcFile), targetFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFile(InputStream fileInputStream, File targetFile) {
        FileOutputStream fileOutputStream = null;
        try {
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            fileOutputStream = new FileOutputStream(targetFile);
            byte[] buffer = new byte[2048];
            int count = 0;
            while ((count = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
