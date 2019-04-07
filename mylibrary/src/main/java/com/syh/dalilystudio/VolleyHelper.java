package com.syh.dalilystudio;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyHelper {
    private static final String TAG = "VolleyHelper";
    //volley连接超时时间
    private static final int SOCKETTIMEOUT = 2500;
    private static VolleyHelper mVolleyHelper = null;
    private static String sShortCommonParam = "";
    private static int idGenerator = 1000;
    private Context mContext;
    private volatile RequestQueue mRequestQueue = null;


    private VolleyHelper(Context context) {
        mContext = context;
        try {
            mRequestQueue = Volley.newRequestQueue(context);
            //查看volley源码，不需要手动调用这个start
            //mRequestQueue.start();
        } catch (Throwable ex) {
            mRequestQueue = null;
            ex.printStackTrace();
        }
    }

    public static VolleyHelper shareInstance() {
        if (null == mVolleyHelper) {
            mVolleyHelper = new VolleyHelper(GlobalAppData.getContext().getApplicationContext());
        }

        return mVolleyHelper;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public void StringHttpRequest(String requestUrl, final VolleyHelperStringReqeuestListener listener, RetryPolicy policy) {

        try {
            if (null == mRequestQueue) {
                if (null != listener) {
                    listener.onResponseFailed(-10000, "mRequestQueue null");
                }
            }
            StringRequest stringRequest = new StringRequest(requestUrl,
                    new Listener<String>() {
                        @Override
                        public void onResponse(final String response) {
                            if (null != listener) {
                                ThreadManager.getInstance().postOnBgThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onResponseSucceeded(response);
                                    }
                                });
                            }
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    if (null != listener) {
                        ThreadManager.getInstance().postOnBgThread(
                                new Runnable() {

                                    @Override
                                    public void run() {
                                        listener.onResponseFailed(error.hashCode(), error.toString());
                                    }
                                });
                    }
                }
            });
            if (policy != null) {
                stringRequest.setRetryPolicy(policy);
            }
            mRequestQueue.add(stringRequest);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

    }

    public void StringHttpRequest(String requestUrl, final VolleyHelperStringReqeuestListener listener) {
        StringHttpRequest(requestUrl, listener, null);
    }

    public void JSONHttpRequest(String requestUrl, final VolleyHelperListener listener, RetryPolicy policy) {

        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestUrl, null,
                    new Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            if (null != listener) {

                                ThreadManager.getInstance().postOnBgThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onResponseSucceeded(response);
                                    }
                                });

                            }
                        }
                    }, new ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    if (null != listener) {
                        ThreadManager.getInstance().postOnBgThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onResponseFailed(error.hashCode(), error.toString());
                            }
                        });
                    }
                }
            });
            if (policy != null) {
                jsonObjectRequest.setRetryPolicy(policy);
            }
            mRequestQueue.add(jsonObjectRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void JSONHttpRequest(String requestUrl, final VolleyHelperListener listener) {
        JSONHttpRequest(requestUrl, listener, null);
    }

    public void BytesHttpRequest(String requestUrl,
                                 String requestBody,
                                 final VolleyHelperByteReqeuestListener lis) {
        int method = Request.Method.GET;
        if (!TextUtils.isEmpty(requestBody)) {
            method = Request.Method.POST;
        }

        ByteArrayRequest byterequest = new ByteArrayRequest(method, requestUrl,
                requestBody,

                new Listener<byte[]>() {
                    @Override
                    public void onResponse(final byte[] data) {
                        if (lis != null) {
                            ThreadManager.getInstance().postOnBgThread(new Runnable() {
                                @Override
                                public void run() {
                                    lis.onResponseSucceeded(data);
                                }
                            });
                        }
                    }
                },

                new ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        if (lis != null) {
                            ThreadManager.getInstance().postOnBgThread(new Runnable() {
                                @Override
                                public void run() {
                                    lis.onResponseFailed(error.hashCode(), error.toString());
                                }
                            });
                        }
                    }
                }

        );

        mRequestQueue.add(byterequest);
    }

    public JSONObject SynJSONHttpRequest(String requestUrl) {
        try {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest JSONRequest = new JsonObjectRequest(requestUrl, null, future,
                    future);
            int socketTimeout = 5000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            JSONRequest.setRetryPolicy(policy);
            mRequestQueue.add(JSONRequest);

            return future.get(); // this will block

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 以同步的方式获取Http请求的String返回值
     *
     * @param url;             Http请求的url
     * @param method;Http请求的方式
     * @param requestBody;Http post请求参数
     * @return 请求的String结果
     */
    public String HttpSyncRequest(String url, int method, final String requestBody) throws Exception {
        try {
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest stringRequest = new StringRequest(method, url, future, future) {
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseData;
                    try {
                        responseData = new String(response.data, "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        LogUtil.d(TAG, "encoding response failed!");
                        ex.printStackTrace();
                        return null;
                    }

                    return Response.success(responseData, null);
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        LogUtil.e(TAG, "Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(SOCKETTIMEOUT,
                    2 * DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            mRequestQueue.add(stringRequest);

            return future.get(); // this will block

        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * 以同步的方式获取Http请求的字节数组返回值
     *
     * @param url;    Http请求的url
     * @param method; Http请求的方式
     * @param header; Http请求可选的头部
     * @return 字节数组返回值
     */
    public byte[] BytesHttpRequestSync(String url, int method, final Map<String, String> header) throws Exception {
        try {
            RequestFuture<byte[]> future = RequestFuture.newFuture();
            ByteArrayRequest byteArrayRequest = new ByteArrayRequest(method, url, null, future, future) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    if (header != null) {
                        return header;
                    } else {
                        return super.getHeaders();
                    }
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(SOCKETTIMEOUT,
                    2 * DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            byteArrayRequest.setRetryPolicy(policy);
            mRequestQueue.add(byteArrayRequest);

            return future.get(); // this will block

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * 取消正在进行的请求
     *
     * @param taskId
     */
    public void cancelPendingRequests(int taskId) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(taskId);
        }
    }

    /**
     * 异步方式请求字节数组类型数据并返回本次请求的ID
     *
     * @param requestUrl;  Http请求的url
     * @param requestBody; Http Post请求的参数
     * @param listener;    监听listener
     * @param header;      Http请求头部
     * @return 返回标识请求的ID号
     */
    public int BytesHttpRequest(String requestUrl,
                                String requestBody,
                                final VolleyListener<byte[]> listener, final Map<String, String> header) {
        int taskID;
        int method = Request.Method.GET;
        if (!TextUtils.isEmpty(requestBody)) {
            method = Request.Method.POST;
        }

        if (listener != null) {
            listener.beforeSend();
        }
        ByteArrayRequest byteArrayRequest = new ByteArrayRequest(method, requestUrl,
                requestBody,

                new Listener<byte[]>() {
                    @Override
                    public void onResponse(final byte[] data) {
                        if (listener != null) {
                            ThreadManager.getInstance().postOnBgThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onResponse(data);
                                }
                            });
                        }
                    }
                },

                new ErrorListener() {
                    @Override
                    public void onErrorResponse(final VolleyError error) {
                        if (listener != null) {
                            ThreadManager.getInstance().postOnBgThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onError(new Exception());
                                }
                            });
                        }
                    }
                }

        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (header != null) {
                    return header;
                } else {
                    return super.getHeaders();
                }
            }
        };
        synchronized (VolleyHelper.class) {
            taskID = idGenerator++;
        }

        byteArrayRequest.setTag(taskID);
        RetryPolicy policy = new DefaultRetryPolicy(SOCKETTIMEOUT,
                2 * DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        byteArrayRequest.setRetryPolicy(policy);
        mRequestQueue.add(byteArrayRequest);
        if (listener != null) {
            listener.afterSend();
        }
        return taskID;
    }

    /**
     * 异步方式请求String类型数据并返回本次请求的ID
     *
     * @param requestUrl;  Http请求的url
     * @param requestBody; Http Post请求的参数
     * @param listener;    监听listener
     * @return 返回标识请求的ID号
     */
    public int StringHttpRequest(String requestUrl, final String requestBody, final VolleyListener<String> listener) {

        int taskID;
        int method = Request.Method.GET;
        if (!TextUtils.isEmpty(requestBody)) {
            method = Request.Method.POST;
        }

        if (listener != null) {
            listener.beforeSend();
        }
        StringRequest stringRequest = new StringRequest(method, requestUrl,
                new Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        if (null != listener) {
                            ThreadManager.getInstance().postOnBgThread(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onResponse(response);
                                }
                            });
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                if (null != listener) {
                    ThreadManager.getInstance().postOnBgThread(

                            new Runnable() {

                                @Override
                                public void run() {
                                    listener.onError(new Exception());
                                }
                            });
                }
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {

                    return null;
                }
            }
        };
        synchronized (VolleyHelper.class) {
            taskID = idGenerator++;
        }

        stringRequest.setTag(taskID);
        RetryPolicy policy = new DefaultRetryPolicy(SOCKETTIMEOUT,
                2 * DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
        if (listener != null) {
            listener.afterSend();
        }
        return taskID;

    }

    public interface VolleyHelperListener {
        public void onResponseSucceeded(JSONObject response);

        public void onResponseFailed(int errorCode, String errorMsg);

    }

    public interface VolleyHelperByteReqeuestListener {
        public void onResponseSucceeded(byte[] response);

        public void onResponseFailed(int errorCode, String errorMsg);
    }

    public interface VolleyHelperStringReqeuestListener {
        public void onResponseSucceeded(String response);

        public void onResponseFailed(int errorCode, String errorMsg);
    }

    public static interface VolleyListener<T> {
        public void beforeSend();

        public void afterSend();

        public void onError(Throwable e);

        public void onResponse(T e);
    }

    class ByteArrayRequest extends Request<byte[]> {
        /**
         * Charset for request.
         */
        private static final String PROTOCOL_CHARSET = "utf-8";
        private final Listener<byte[]> mListener;
        private String mRequestBody;


        public ByteArrayRequest(int method,
                                String url,
                                String requestBody,
                                Listener<byte[]> listener,
                                ErrorListener errlistener) {
            super(method, url, errlistener);
            mListener = listener;
            mRequestBody = requestBody;
        }


        @Override
        public byte[] getBody() {
            try {
                return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
            } catch (UnsupportedEncodingException uee) {
                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                        mRequestBody, PROTOCOL_CHARSET);
                return null;
            }
        }


        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<String, String>();
            // 设置httpPost请求参数
            headers.put("Accept", "*/*");
            headers.put("Accept-Encoding", "gzip");
            headers.put("Accept-Language", "en-US,en");
            return headers;
        }


        @Override
        protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
            if (GlobalAppData.isDebug()) {
                LogUtil.d(TAG, "parseNetworkResponse  response = " + response);
            }


            if (response == null) {
                return null;
            }


            if (response.statusCode != 200) {
                return null;
            }
            byte[] responseBytes = response.data;
            //TODO gzip
            return Response.success(responseBytes, null);
        }

        @Override
        protected void deliverResponse(byte[] response) {
            if (mListener != null) {
                mListener.onResponse(response);
            }
        }
    }
}
