package com.matthewqiu.mapboxpractice.wifi;

/**
 * Created by xiaohui on 8/1/16.
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class WifiHandler {
    //constants
    public static final int WEP = 1;
    public static final int WAP = 2;
    public static final int OPEN_NETWORK = 3;

    public static final String TAG = "WifiHandler";


    WifiConfiguration wifiConf;				/* WifiConfiguration object */


    WifiManager wifiMgr;							/* WifiManager object */


    WifiInfo wifiInfo;								/* WifiInfo object */


    List<ScanResult> wifiScan;				/* List of ScanResult objects */

    /**
     * Constructor initializes WifiManager and WifiInfo.
     * @param context
     */
    public WifiHandler(Context context) {
        wifiMgr  = getWifiManager(context);		// gets wifiMgr in the current context
        wifiInfo = getWifiInfo(context);			// gets wifiInfo in the current context
        wifiConf = getWifiConf(context);			// gets wifiConf in the current context
        //wifiScan = getWifiInRange();					// gets wifiScan in the current context
    }

    /**
     * Function checkWifiEnabled checks if the WiFi connection
     * is enabled on the device.

     * @return true  if the WiFi connection is enabled,
     * 				 false if the WiFi connection is disabled
     */
    public boolean checkWifiEnabled() {
        // checks if WiFi is enabled
        return (wifiMgr != null && wifiMgr.isWifiEnabled());
    }

    /**
     * Function enableWifi enables WiFi connection on the device.
     * @return true  if the attempt to enable WiFi succeeded,
     * 				 false if the attempt to enable WiFi failed.
     */
    public boolean enableWifi() {
        // enables WiFi connection
        return wifiMgr.setWifiEnabled(true);
    }

    /**
     * Function disableWifi disables WiFi connection on the device.

     * @return true  if WiFi connection was disabled,
     * 				 false if attempt to disable WiFi failed.
     */
    public boolean disableWifi() {
        // disables WiFi connection
        return wifiMgr.setWifiEnabled(false);
    }

    /**
     * Function getWifiManager gets the WiFiManager object from the device.
     * @param context
     * @return WifiManager object. Also sets the class variable
     * 				 wifiMgr as the WifiManager object returned.
     */
    public WifiManager getWifiManager(Context context) {
        WifiManager wifiMgr = null;

        // gets WifiManager obj from the system
        wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr == null) {
            Log.d("TAG", "WIFI_SERVICE is the wrong service name.");
        }

        return wifiMgr;
    }

    /**
     * Function getWifiInfo gets the current WiFi connection information in a
     * WifiInfo object from the device.
     * @param context
     * @return wifiInfo created object or
     * 				 null 		if wifi is not enabled.
     */
    public WifiInfo getWifiInfo(Context context) {
        WifiInfo wifiInfo = null;

        // gets WiFi network info of the current connection
        if (checkWifiEnabled()) {
            wifiInfo = (WifiInfo) wifiMgr.getConnectionInfo();
        }

        if (wifiInfo == null) {
            Log.d("TAG", "WifiInfo object is empty.");
        }

        return wifiInfo;
    }

    /**
     * Function that returns a WifiConfiguration object from the WifiInfo
     * object from the class. If wifiInfo exists, then we are able to retrieve
     * information from the current connection
     * @param context
     * @return WifiConfiguration object created.
     */
    public WifiConfiguration getWifiConf(Context context) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        if (wifiInfo == null) {
            Log.d("TAG", "WifiInfo object is empty");
            return null;
        }

        wifiConfiguration.SSID = wifiInfo.getSSID();
        wifiConfiguration.networkId = wifiInfo.getNetworkId();

        return wifiConfiguration;
    }

    /**
     * Creates a new WifiConfiguration object for wifiConf.
     */
    public void clearWifiConfig() {
        wifiConf = new WifiConfiguration();
    }

    /**
     * Function getWifiInRange returns all the WiFi networks that are
     * accessible through the access point (device AP) found during the
     * last scan.

     * @return List of ScanResult containing information on all WiFi networks
     * 				 discovered in the range.
     */
    public List<ScanResult> getWifiInRange() {
        // gets ~last~ list of WiFi networks accessible through the access point.
        return (wifiScan = (List<ScanResult>) wifiMgr.getScanResults());
    }

    /**
     * Function that scans for wifi networks available in the devices range.
     * @return true  if scan started
     * 				 false if scan could not be started
     */
    public boolean scanWifiInRange() {
        if (!checkWifiEnabled()) {
            return false;
        }

        if (!wifiMgr.startScan()) {
            Log.d("TAG", "Failed to scan wifi's in range.");
            return false;
        }

        return true;
    }

    /**
     * Function to disconnect from the currently connected WiFi AP.
     * @return true  if disconnection succeeded
     * 				 false if disconnection failed
     */
    public boolean disconnectFromWifi() {
        return (wifiMgr.disconnect());
    }

    private static void downloadFile(String url, File outputFile) {
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();
        } catch(FileNotFoundException e) {
            return; // swallow a 404
        } catch (IOException e) {
            return; // swallow a 404
        }
    }

    /**
     * Function to connect to a selected network
     * @param networkSSID         network SSID name
     * @param	networkPassword     network password

     * @return true  if connection to selected network succeeded
     * 				 false if connection to selected network failed
     */
    public boolean connectToSelectedNetwork(String networkSSID, String networkPassword) {
        int networkId;
        int SecurityProtocol = WAP;

        // Clear wifi configuration variable
        clearWifiConfig();

        // Sets network SSID name on wifiConf
        wifiConf.SSID = "\"" + networkSSID + "\"";
        Log.d(TAG, "SSID Received: " + wifiConf.SSID);
        switch(SecurityProtocol) {
            // WEP "security".
            case WEP:
                wifiConf.wepKeys[0] = "\"" + networkPassword + "\"";
                wifiConf.wepTxKeyIndex = 0;
                wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;

            // WAP security. We have to set preSharedKey.
            case WAP:
                WifiConfiguration conf = new WifiConfiguration();
                conf.status = WifiConfiguration.Status.ENABLED;
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.SSID = "\"" + networkSSID + "\"";
                //conf.wepKeys[0] = "\"" + networkPass + "\"";
                //conf.wepTxKeyIndex = 0;
                //conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                //conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.preSharedKey = "\""+ networkPassword +"\"";
                if(wifiMgr.getWifiState()!=wifiMgr.WIFI_STATE_ENABLED) {
                    wifiMgr.setWifiEnabled(true);
                    Log.d(TAG,"enabling Wifi");
                }

                Log.d(TAG,"connection info "+wifiMgr.getWifiState() + " " +wifiMgr.getConnectionInfo() + wifiMgr.getDhcpInfo());
                Log.d(TAG,wifiMgr.toString());
                int network_id = wifiMgr.addNetwork(conf);
                Log.d(TAG,""+ network_id + " " +wifiMgr.getConfiguredNetworks());

                wifiMgr.disconnect();
                wifiMgr.enableNetwork(network_id, true);
                wifiMgr.reconnect();
                //wifiManager.reassociate();

                Log.d(TAG,"connection info "+wifiMgr.getWifiState() + " " +wifiMgr.getConnectionInfo() + wifiMgr.getDhcpInfo());
                break;

            // Network without security.
            case OPEN_NETWORK:
                wifiConf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
        }

        // Add WiFi configuration to list of recognizable networks
        if ((networkId = wifiMgr.addNetwork(wifiConf)) == -1) {
            Log.d("TAG", "Failed to add network configuration!");
            return false;
        }

        // Disconnect from current WiFi connection
        if (!disconnectFromWifi()) {
            Log.d("TAG", "Failed to disconnect from network!");
            return false;
        }

        // Enable network to be connected
        if (!wifiMgr.enableNetwork(networkId, true)) {
            Log.d("TAG", "Failed to enable network!");
            return false;
        }

        // Connect to network
        if (!wifiMgr.reconnect()) {
            Log.d("TAG", "Failed to connect!");
            return false;
        }

        return true;
    }
}