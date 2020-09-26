package com.reactlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.blinkt.openvpn.DisconnectVPNActivity;
import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;

import static android.app.Activity.RESULT_OK;

public class OpenvpnModule extends ReactContextBaseJavaModule {

    protected OpenVPNService mService;

    private boolean vpnStarted = false;

    Context myContext;

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
//        ProfileManager.setConntectedVpnProfileDisconnected(getReactApplicationContext());
//        if (mService != null && mService.getManagement() != null) {
//            Log.d("TAGGG", "disconnecting 2");
//            mService.getManagement().stopVPN(false);
//        }
//        Toast.makeText(reactContext, "VPN STARTED", Toast.LENGTH_LONG).show();
        Intent disconnectVPN = new Intent(reactContext, DisconnectVPNActivity.class);
        reactContext.startActivity(disconnectVPN);
        promise.resolve(null);
    }
}
