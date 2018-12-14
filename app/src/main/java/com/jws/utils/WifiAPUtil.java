package com.jws.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Method;

public class WifiAPUtil {
    private final static String TAG = "WifiAPUtil";

    private WifiManager mWifiManager;
    private Context mContext;
    private Handler mHandler;
    public final static boolean DEBUG = true;
    public static final int MESSAGE_AP_STATE_ENABLED = 1;
    public static final int MESSAGE_AP_STATE_FAILED = 2;
    //监听wifi热点的状态变化
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
    public static int WIFI_AP_STATE_DISABLING = 10;
    public static int WIFI_AP_STATE_DISABLED = 11;
    public static int WIFI_AP_STATE_ENABLING = 12;
    public static int WIFI_AP_STATE_ENABLED = 13;
    public static int WIFI_AP_STATE_FAILED = 14;
    public enum WifiSecurityType {
        WIFICIPHER_NOPASS, WIFICIPHER_WPA, WIFICIPHER_WEP, WIFICIPHER_INVALID, WIFICIPHER_WPA2
    }

    public WifiAPUtil(Context context){
        mContext = context;
        mHandler = new Handler();//初始化监听wifi热点状态变化反应事件
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

    }

    //监听wifi热点状态变化
    private BroadcastReceiver mWifiStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DEBUG) Log.i(TAG,"WifiAPUtils onReceive: "+intent.getAction());
            if(WIFI_AP_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int cstate = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1);
                if(cstate == WIFI_AP_STATE_ENABLED) {
                    if(mHandler != null){
                        mHandler.sendEmptyMessage(MESSAGE_AP_STATE_ENABLED);
                    }
                }if(cstate == WIFI_AP_STATE_DISABLED  || cstate == WIFI_AP_STATE_FAILED) {
                    if(mHandler != null)
                        mHandler.sendEmptyMessage(MESSAGE_AP_STATE_FAILED);
                }
            }
        }
    };


    /**
     * 判断热点是否开启
     * @return
     */
    public boolean isApOn() {
        try {
            Method method = mWifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (Throwable ignored) {
        }
        return false;
    }

    /**
     * 关闭WiFi
     */
    public  void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 开启热点
     */
    private void openHotpot(){
        mWifiManager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback(){

            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                Log.d(TAG, "Wifi Hotspot is on now");
                //可以直接获取热点信息
                WifiConfiguration config = reservation.getWifiConfiguration();
                String ssid = config.SSID;
                String pwd = config.preSharedKey;
                getValidApSsid();
                getValidPassword();
                getValidSecurity();
            }

            @Override
            public void onStopped() {
                super.onStopped();
                Log.d(TAG, "onStopped: ");
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
                Log.d(TAG, "onFailed: ");
            }
        },new Handler());
    }




    //获取热点ssid
    public String getValidApSsid() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration)method.invoke(mWifiManager);
            return configuration.SSID;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
    //获取热点密码
    public String getValidPassword(){
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration configuration = (WifiConfiguration)method.invoke(mWifiManager);
            return configuration.preSharedKey;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

    }
    //获取热点安全类型
    public int getValidSecurity(){
        WifiConfiguration configuration;
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            configuration = (WifiConfiguration)method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return WifiSecurityType.WIFICIPHER_INVALID.ordinal();
        }

        if(DEBUG)Log.i(TAG,"getSecurity security="+configuration.allowedKeyManagement);
        if(configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
            return WifiSecurityType.WIFICIPHER_NOPASS.ordinal();
        }else if(configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WifiSecurityType.WIFICIPHER_WPA.ordinal();
        }else if(configuration.allowedKeyManagement.get(4)) { //4 means WPA2_PSK
            return WifiSecurityType.WIFICIPHER_WPA2.ordinal();
        }
        return WifiSecurityType.WIFICIPHER_INVALID.ordinal();
    }
}
