package com.jws.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.Inet4Address;

public class WifiUtil {
    private final static String TAG = "WifiUtil";

    private Context mContext;
    private WifiManager mWifiManager;


    //TODO 统一加密类型
    public enum WifiSecurityType {
        WIFICIPHER_NOPASS, WIFICIPHER_WPA, WIFICIPHER_WEP, WIFICIPHER_INVALID, WIFICIPHER_WPA2
    }
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }


    @SuppressLint("MissingPermission")
    public WifiUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);


        /*获取wifi状态 
        // WIFI_STATE_DISABLED   wifi已关闭
        // WIFI_STATE_DISABLING  wifi正在关闭
        // WIFI_STATE_ENABLED    wifi已连接
        // WIFI_STATE_ENABLING   wifi正在连接
        // WIFI_STATE_UNKNOWN  wifi未知状态
        */
        mWifiManager.getWifiState();


        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        /*
        wifiInfo.getBSSID()  得到无线的Mac地址

        wifiInfo.getHiddenSSID()  未知（true if this network does not broadcast its SSID, so an  SSID-specific probe request must be used for scans.）

        wifiInfo.getIpAddress()  int类型ip地址，需要转成String

        wifiInfo.getLinkSpeed()  连接速度，单位Mbps

        wifiInfo.getNetworkId()  网络标识id（wifi名称改掉，会跟着变）

        wifiInfo.getRssi()  当前无线网络的信号强度指示器

        wifiInfo.getSSID()  当前无线网络的名称

        wifiInfo.describeContents()  未知

        wifiManager.startScan(); //开启扫描

        scanResultList = wifiManager.getScanResults(); // 扫描返回结果列表
         */

    }


    /**
     * 打开wifi功能
     *
     * @return true or false
     */
    public boolean OpenWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    /**
     * 关闭wifi
     *
     * @return
     */
    public boolean closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            return true;
        } else {
            return mWifiManager.setWifiEnabled(false);
        }
    }


        /**
         * 无配置记录链接方式
         *
         * @param SSID
         * @param Password
         * @param Type
         * @return true or false
         */
        public boolean Connect(String SSID, String Password, WifiSecurityType Type) {
            //TODO wifi connect
            return false;
        }


    /**
     * int型ip转换为字符
     * @param ip
     * @return
     */
    public String ipIntToString(int ip) {
        try {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (0xff & ip);
            bytes[1] = (byte) ((0xff00 & ip) >> 8);
            bytes[2] = (byte) ((0xff0000 & ip) >> 16);
            bytes[3] = (byte) ((0xff000000 & ip) >> 24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
