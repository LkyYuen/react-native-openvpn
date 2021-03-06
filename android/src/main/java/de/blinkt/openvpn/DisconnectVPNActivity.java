package de.blinkt.openvpn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.reactlibrary.R;

import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;

public class DisconnectVPNActivity extends Activity {
    protected OpenVPNService mService;
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

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        View view = new View(this);
//        view.setBackgroundColor(0xff0000);
//        setContentView(view);
//    }

    @Override
    protected void onResume() {//创建Dialog之前调用
        super.onResume();
        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//        showDisconnectDialog();
        ProfileManager.setConntectedVpnProfileDisconnected(this);
        new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // do something after 1s
            }

            @Override
            public void onFinish() {
                Log.d("DISCONNECT 1", String.valueOf(mService));
                if (mService != null && mService.getManagement() != null) {
                    Log.d("DISCONNECT 2", String.valueOf(mService));
                    mService.getManagement().stopVPN(false);
                }
                finish();
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

//    private void showDisconnectDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.title_cancel);
//        builder.setMessage(R.string.cancel_connection_query);
//        builder.setNegativeButton(android.R.string.no, this);
//        builder.setPositiveButton(android.R.string.yes, this);
//        builder.setOnCancelListener(this);
//        builder.show();
//    }

//    @Override
//    public void onClick(DialogInterface dialog, int which) {
//        if (which == DialogInterface.BUTTON_POSITIVE) {
//            ProfileManager.setConntectedVpnProfileDisconnected(this);
//            if (mService != null && mService.getManagement() != null) {
//                mService.getManagement().stopVPN(false);
//            }
//        }
//        finish();
//    }

//    @Override
//    public void onCancel(DialogInterface dialog) {
//        finish();
//    }
}