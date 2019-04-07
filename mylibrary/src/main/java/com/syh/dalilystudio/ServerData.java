package com.syh.dalilystudio;

/**
 * 网络数据
 * 
 * @author ahong
 * 
 */
public class ServerData {

    /**
     * 请求成功
     */
    public static final int STATUS_SUCCESS = 0;
    /**
     * 请求失败
     */
    public static final int STATUS_ERROR = 1;
    /**
     * 请求被取消
     */
    public static final int STATUS_CANCEL = 2;
    /**
     * 网络请求结果<br>
     * 
     * @see {@link STATUS_SUCCESS},{@link STATUS_ERROR},{@link STATUS_CANCEL}
     */
    public int status;
    public String data;
    /**
     * 请求的命令(或称request Key)
     */
    public int cmd;
    public int errorType;
    public String errorMsg;

    /**
     * 请求错误Data
     * 
     * @param errorType
     * @param errorMsg
     */
    public ServerData(int cmd, int errorType, String errorMsg) {
        this.cmd = cmd;
        this.status = STATUS_ERROR;
        this.errorType = errorType;
        this.errorMsg = errorMsg;
    }

    public static interface ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOW = -1;
        /**
         * 网络不可用
         */
        public static final int NET_DISABLE = 2;
        /**
         * 网络不可用
         */
        public static final int IO_ERROR = 3;
    }
}
