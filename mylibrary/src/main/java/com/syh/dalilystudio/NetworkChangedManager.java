package com.syh.dalilystudio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

/**
 * WifiManager
 * 
 * @author ahong
 * 
 */
public class NetworkChangedManager {

    private static NetworkChangedManager instance;
    public Context context;
    public NetworkChangedReceiver networkChangedReceiver;
    public static NetworkChangedManager getInstance() {
        if (instance == null) {
            instance = new NetworkChangedManager();
        }
        return instance;
    }

    public void init(Context context){
        this.context=context;
    }
    
    private Map<CallbackKey,NetworkChangedCallback> callbacks;
    
    public void registNetworkStatus(){
        if(networkChangedReceiver==null){
            networkChangedReceiver=new NetworkChangedReceiver();
            
            IntentFilter filter=new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(networkChangedReceiver, filter);
        }
    }
    
    /**
     * 
     * @param action
     * @param callback
     */
    public void registCallback(CallbackKey action,NetworkChangedCallback callback){
        if(callbacks==null){
            callbacks=new HashMap<CallbackKey,NetworkChangedCallback>();
        }
        
        callbacks.put(action, callback);
        
        registNetworkStatus();
    }
    
    public void unRegistCallback(CallbackKey key){
        if(callbacks!=null){
            callbacks.remove(key);
        }
    }
    
    public static void destroy(){
        if(instance!=null && instance.networkChangedReceiver!=null){
            instance.context.unregisterReceiver(instance.networkChangedReceiver);
            instance.networkChangedReceiver=null;
        }
        instance=null;
    }
    
    public static class CallbackKey{
        public CallbackKey(String action){
            this.action=action;
            this.uuid=UUID.randomUUID().toString();
        }
        public String action;
        public String uuid;
    }
    
    private class NetworkChangedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(callbacks!=null){
                String action=intent.getAction();
                Iterator<CallbackKey> iterator=callbacks.keySet().iterator();
                while(iterator.hasNext()){
                    CallbackKey key=iterator.next();
                    if(key.action.endsWith(action)){
                        callbacks.get(key).onReceive(context,intent);
                    }
                }
            }
            
        }
        
    }
    
    public static interface NetworkChangedCallback{
        public void onReceive(Context context, Intent intent);
    }
}
