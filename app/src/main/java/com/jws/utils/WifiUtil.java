package com.jws.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.util.List;

public class WifiUtil {
    private final static String TAG = "WifiUtil";

    private Context mContext;
    private WifiManager mWifiManager;
    private List<WifiConfiguration> mWifiConfigurations;


    //TODO 统一加密类型
    public enum WifiSecurityType {
        WIFICIPHER_NOPASS, WIFICIPHER_WEP, WIFICIPHER_WPA,  WIFICIPHER_INVALID, WIFICIPHER_WPA2
    }
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }


    @SuppressLint("MissingPermission")
    public WifiUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();

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
     * 判断WiFi配置是否存在
     * @param ssid
     * @return
     */
    public WifiConfiguration isExsitsWificonfig(String ssid){
        WifiConfiguration ssidWifiConfig = null;
        for(WifiConfiguration config : mWifiConfigurations){
            if(config.SSID.equals(ssid)){
                ssidWifiConfig = config;
            }
        }
        return ssidWifiConfig;
    }

    /**
     * wifi链接方式，已有连接会更新数据
     *
     * @param ssid
     * @param Password
     * @param Type
     * @return true or false
     */
    public void Connect(String ssid, String Password, WifiSecurityType Type) {
        //TODO wifi connect
        WifiConfiguration connectWifiConfig ;
        connectWifiConfig = createWifiInfo(ssid, Password, Type);
        int i = mWifiManager.addNetwork(connectWifiConfig);
        mWifiManager.enableNetwork(i, true);
    }


    public WifiConfiguration createWifiInfo(String SSID, String Password, WifiSecurityType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        // nopass
        if (Type == WifiSecurityType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        // wep
        if (Type == WifiSecurityType.WIFICIPHER_WEP) {
            if (!TextUtils.isEmpty(Password)) {
                config.wepKeys[0] = "\"" + Password + "\"";
            }
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        // wpa
        if (Type == WifiSecurityType.WIFICIPHER_WPA2) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // 此处需要修改否则不能自动重联
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
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
