package com.syh.dalilystudio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class ImageUtil {

    /**
     * 是否是Gif图片
     * 
     * @param imagePath
     * @return
     */
    public static boolean isGifFile(String imagePath) {
        File image = new File(imagePath);
        InputStream is = null;

        byte[] src = new byte[2];
        try {
            is = new FileInputStream(image);
            is.read(src);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError er) {
            er.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (src == null || src.length <= 0) {
            return false;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;// byte to int
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        if ("4749".equals(stringBuilder.toString())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取正方形的bitmap
     * 
     * @param bitmap
     * @param width
     *            指定方形宽度，如果为0，则根据bitmap的最小宽度来
     * @return
     */
    public static Bitmap getSquareBitmap(Bitmap bitmap, int newWidth) {
        if (bitmap == null) {
            return null;
        }

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int minSize = Math.min(bitmapWidth, bitmapHeight);
        if (newWidth != 0) {
            Matrix m = new Matrix();
            float rate = newWidth * 1.0f / minSize;
            m.setScale(rate, rate);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, m, false);
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
            minSize = Math.min(bitmapWidth, bitmapHeight);
        }
        if (bitmapWidth == bitmapHeight) {
            // 已经是正方形了
            return bitmap;
        }
        Bitmap output = Bitmap.createBitmap(minSize, minSize, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(bitmap, (bitmapWidth - minSize) / 2, (bitmapHeight - minSize) / 2, paint);
        return output;
    }

    /**
     * 获得圆角图片
     * 
     * @param bitmap
     * @param pixels
     *            圆角半径，如果为bitmap宽度的一般，则会切割成圆形
     * @return
     */
    public static Bitmap getRoundBitmap(Bitmap bitmap, float pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap getZoomBitmap(String filePath, int maxWidth, int maxHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        float inSampleSize = 1f;
        if (srcWidth > maxWidth) {
            inSampleSize = Math.min(inSampleSize, ((float) srcWidth) / maxWidth);
        }

        if (srcHeight > maxHeight) {
            inSampleSize = Math.min(inSampleSize, ((float) srcHeight) / maxHeight);
        }
        options.inSampleSize = Math.round(inSampleSize);
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            LogUtil.w("", "getZoomBitmap e:" + e.getMessage());
        }
        return null;
    }

    public static Bitmap getZoomBitmap(InputStream inputStream, int maxWidth, int maxHeight) {
        if (inputStream == null) {
            return null;
        }
        byte[] bytes = null;
        try {
            bytes = FileUtil.readFromStream(inputStream);
        } catch (Exception e1) {
            return null;
        }
        if (bytes == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        float inSampleSize = 1f;
        if (srcWidth > maxWidth) {
            inSampleSize = Math.max(inSampleSize, ((float) srcWidth) / maxWidth);
        }

        if (srcHeight > maxHeight) {
            inSampleSize = Math.max(inSampleSize, ((float) srcHeight) / maxHeight);
        }
        options.inSampleSize = Math.round(inSampleSize);
        LogUtil.d("", "getZoomBitmap inSampleSize:" + options.inSampleSize);
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        } catch (OutOfMemoryError e) {
            LogUtil.w("", "getZoomBitmap e:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static Drawable createShapeDrawable(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);

        return drawable;
    }

    public static Bitmap createShapeBitmap(Bitmap bitmap, float radius) {
        Bitmap output = null;
        try {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        } catch (Throwable e) {
            return bitmap;
        }
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = radius;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static void chooseImage(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void takePicture(Activity activity, int requestCode, Uri outputUri) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (outputUri != null) {
            i.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        }
        activity.startActivityForResult(i, requestCode);
    }

    public static Bitmap parseImageOnActivityResult(Context context, Intent intent, boolean chooseImage) {
        if (chooseImage) {
            // 相册
            Uri uri = intent.getData();
            Cursor cursor = context.getContentResolver().query(uri, new String[] { Media.DATA }, null,
                    null, null);
            cursor.moveToFirst();
            try {
                String path = cursor.getString(0);
                return BitmapFactory.decodeFile(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (MediaStore.ACTION_IMAGE_CAPTURE.equals(intent.getAction())) {
            // 相机
            Uri uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            if (uri != null) {
                String path = uri.getPath();
                return BitmapFactory.decodeFile(path);
            } else {
                if (intent.getExtras() != null) {
                    return (Bitmap) intent.getExtras().get("data");
                }
            }
        }
        return null;
    }

    public static String parseImagePathOnActivityResult(Context context, Intent intent, boolean chooseImage) {
        if (intent == null) {
            return null;
        }
        if (chooseImage) {
            // 相册
            Uri uri = intent.getData();
            if (uri != null && uri.toString().startsWith("file:")) {
                return uri.getPath();
            }
            Cursor cursor = context.getContentResolver().query(uri, new String[] { Media.DATA }, null,
                    null, null);
            if (cursor != null && cursor.moveToFirst()) {
                try {
                    String path = cursor.getString(0);
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        } else if (MediaStore.ACTION_IMAGE_CAPTURE.equals(intent.getAction())) {
            // 相机
            Uri uri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
            if (uri != null) {
                String path = uri.getPath();
                return path;
            } else {
            }
        }
        return null;
    }

    /**
     * 将大于targetSize的图缩小到target，然后取中间 将小于targetSize的图直接按比例取中间。
     *
     * @param path
     * @param targetSize
     * @return 输出图片的大小不一定是targetSize，原始图小会输出等比例小图
     */
    public static Bitmap getSquareBitmap(String path, int targetSize) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, option);
        int width = option.outWidth;
        int height = option.outHeight;
        if (width > targetSize && height > targetSize) {
            int min = (width > height ? height : width);
            float rate = min * 1.0f / targetSize;
            int newRate = (int) Math.floor(rate);
            option.inJustDecodeBounds = false;
            option.inSampleSize = newRate;
            Bitmap bitmap = BitmapFactory.decodeFile(path, option);
            Matrix m = new Matrix();
            float scale = targetSize * 1.0f / (Math.min(bitmap.getWidth(), bitmap.getHeight()));
            m.setScale(scale, scale);
            int srcSize = (int) (targetSize / scale);
            int x = (int) ((bitmap.getWidth() - srcSize) / 2);
            int y = (int) ((bitmap.getHeight() - srcSize) / 2);
            return Bitmap.createBitmap(bitmap, x, y, srcSize, srcSize, m, false);
        }

        return null;
    }
}
