package com.reactlibrary;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.VpnService;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

import de.blinkt.openvpn.DisconnectVPNActivity;
import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;

public class OpenvpnModule extends ReactContextBaseJavaModule {

    protected OpenVPNService mService;

    private boolean vpnStarted = false;

//    protected OpenVPNService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }
    };

    private final ReactApplicationContext reactContext;

    public OpenvpnModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "Openvpn";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void connect(Promise promise) {
        Log.d("CACHE DIR", String.valueOf(reactContext.getCacheDir()));
        VpnStatus.initLogCache(reactContext.getCacheDir());
        vpnStarted = true;
        String config = "";
        try {
            InputStream conf = reactContext.getAssets().open("client.ovpn");// TODO replace your own authentication file in /assets/client.bin
            InputStreamReader isr = new InputStreamReader(conf);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                config += line + "\n";
            }
        } catch (IOException ignore) {
            Log.i("ERROR", "error");
        }
        try {
            OpenVpnApi.startVpn(reactContext, config, null, null);
            Log.i("ERROR", "STARTING VPN");
        } catch (RemoteException e) {
            Log.i("ERROR", "ERROR");
        }
        promise.resolve(null);
    }

    @ReactMethod
    public void disconnect(Promise promise) {
        Log.d("TAGGG", "disconnecting");
        Intent disconnectVPN = new Intent(reactContext, DisconnectVPNActivity.class);
        getCurrentActivity().startActivity(disconnectVPN);
        promise.resolve(null);
    }

    @ReactMethod
    public void checkVPNStatus(Promise promise) {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
                Log.d("DEBUG", "IFACE NAME: " + iface);
                if ( iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    Log.d("CHECK VPN SUCCESS", "CONNECTED");
                    // return true;
                    promise.resolve(true);
                }
                else {
                    Log.d("CHECK VPN SUCCESS", "NOT CONNECTED");
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
            Log.d("CHECK VPN ERROR", String.valueOf(e1));
        }

        // return false;
        promise.resolve(false);
    }
}
