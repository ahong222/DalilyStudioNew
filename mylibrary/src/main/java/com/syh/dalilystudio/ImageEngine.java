package com.syh.dalilystudio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * 图片引擎，用于处理大量网络图片的缓存 <br>
 * 使用请加user-permission:WRITE_EXTERNAL_STORAGE
 * 
 * @author syh
 * 
 */
public class ImageEngine {
    /**
     * 软引用缓存
     */
    private HashMap<String, SoftReference<Bitmap>> mImageCaches;

    private String mCacheDir;

    public ImageEngine(String cacheDir) {
        mCacheDir = cacheDir;
        mImageCaches = new HashMap<String, SoftReference<Bitmap>>();
    }

    private void saveBitmapToCache(String imageUrl, Bitmap value) {
        String key = getFileName(imageUrl);
        mImageCaches.put(key, new SoftReference<Bitmap>(value));
        value = null;
    }

    private Bitmap getBitmapFromCache(String imageUrl) {
        String key = getFileName(imageUrl);
        SoftReference<Bitmap> softReference = mImageCaches.get(key);
        if (softReference != null) {
            return softReference.get();
        } else {
            return null;
        }
    }

    private File getCachedImageFile(String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        String path = getCacheDir();
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }

        return new File(path, getFileName(uri));
    }

    public String getImageFilePath(String url){
        return getCachedImageFile(url).getAbsolutePath();
    }
    
    private static final int SERVER_CONN_TIME_OUT = 2000;// 2s
    private static final int IMAGE_READ_TIME_OUT = 5000;// 5s

    public boolean download(String uri) {
        File file = getCachedImageFile(uri);
        if (file == null) {
            return false;
        }

        if (file.exists() && file.length() > 0) {
            return true;
        }

        URLConnection uCon = null;
        try {
            URL url = new URL(uri);
            uCon = url.openConnection();
            uCon.setConnectTimeout(SERVER_CONN_TIME_OUT);
            uCon.setReadTimeout(IMAGE_READ_TIME_OUT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (uCon != null) {
            OutputStream output = null;
            InputStream inputStream = null;
            try {
                inputStream = uCon.getInputStream();
                if (DeviceUtil.getAvailableSpace() < 5 * 1024 * 1024) {
                    return true;
                }
                output = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(buffer)) >0) {
                    output.write(buffer, 0, length);
                }

                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 删除内存和磁盘中的图片
     * @param url
     * @return
     */
    public boolean deleteImageByUrl(String url) {
        File file = getCachedImageFile(url);
        if (file == null || !file.exists()) {
            return false;
        }

        String key = getFileName(url);
        mImageCaches.remove(key);
        
        return file.delete();
    }

    /**
     * 将图片Url转化成文件名称
     * 
     * @param url
     * @return
     */
    private String getFileName(String url) {
        return AesDesUtil.MD5(url);
    }

    public String getCacheDir() {
        return mCacheDir;
    }

    /**
     * 该加载图片为异步加载，如果需要停止加载，则通过返回的ImageAsyncLoader，调用ImageAsyncLoader.cancel(true
     * );
     * 
     * @param imageUrl
     * @param callback
     *            ,bitmap of onImageLoaded may be null
     * @return
     */
    public ImageAsyncLoader loadImage(String imageUrl,
            OnImageLoadedCallback callback) {
        ImageAsyncLoader loader = new ImageAsyncLoader(callback);
        Bitmap bitmap = loader.load(imageUrl);
        if (bitmap != null && callback != null) {
            callback.onImageLoaded(bitmap);
        }
        return loader;
    }

    /**
     * 异步加载，不适宜加载ListView中图片，适宜静态图片
     * @param imageUrl
     * @param imageView
     */
    public void loadImage(String imageUrl, final ImageView imageView) {
        loadImage(imageUrl, new OnImageLoadedCallback() {

            @Override
            public void onImageLoaded(Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
    }

    /**
     * Loading image from web asynchronously.</br> 1.checking whether the image
     * has been downloaded in external storage</br> 2.if no found in external
     * storage, then loading it from web in background</br> 3.if necessary
     * resize the image based density of the screen.
     */
    public class ImageAsyncLoader extends AsyncTask<String, Void, Bitmap> {
        private OnImageLoadedCallback mListener;
        private boolean mCanceled = false;

        /**
         * @param callback
         *            it will be called after loaded.
         * @param autoScale
         *            Indicates whether the generated bitmap should be scaled
         *            based on the current density of the screen.
         */
        public ImageAsyncLoader(OnImageLoadedCallback callback) {
            mListener = callback;
        }

        @Override
        protected void onCancelled(Bitmap result) {
            mCanceled = true;
        }

        private String loadedImageUrl;

        /**
         * 需先判断返回值
         * 
         * @param imageUrl
         * @return cache中的bitmap
         */
        public Bitmap load(String imageUrl) {
            mCanceled = false;
            loadedImageUrl = imageUrl;
            Bitmap result = getBitmapFromCache(imageUrl);
            if (result == null) {
                execute(imageUrl);
            }
            return result;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageUri = params[0];
            File cacheFile = getCachedImageFile(imageUri);

            Bitmap result = null;
            if (cacheFile == null || !cacheFile.exists()
                    || cacheFile.length() == 0) {
                try {
                    download(imageUri);
                } catch (Exception e) {
                    LogUtil.e(e);
                }
                cacheFile = ImageEngine.this.getCachedImageFile(imageUri);
            }

            // don't let this block at the else of above if
            if (cacheFile != null && cacheFile.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(cacheFile);
                    result = BitmapFactory.decodeStream(fis, null, null);
                } catch (FileNotFoundException e) {
                    LogUtil.w("FileNotFoundException when loading %s", imageUri);
                } catch (OutOfMemoryError e) {
                    LogUtil.w("OutOfMemoryError when loading %s", imageUri);
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                LogUtil.w("cacheFile is null when loading " + imageUri);
            }

            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (mListener != null && !mCanceled) {
                saveBitmapToCache(loadedImageUrl, result);
                mListener.onImageLoaded(result);
            }
        }

    }

    public static abstract class OnImageLoadedCallback {
        /**
         * always be invoked
         * 
         * @param result
         *            may be null
         */
        public abstract void onImageLoaded(Bitmap result);
    }
}
