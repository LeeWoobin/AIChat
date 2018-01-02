package kr.co.imcloud.app.aichat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.crazyup.utils.AppConfig;
import com.crazyup.utils.LocationUtil;
import com.crazyup.utils.NetworkUtils;

import kr.co.imcloud.app.aichat.receiver.NetworkChangeReceiver;
import kr.co.imcloud.app.aichat.stores.AuthStore;
import kr.co.imcloud.app.aichat.ui.chat.ChatActivity;
import kr.co.imcloud.app.aichat.ui.chat.PermCheckListener;
import kr.co.imcloud.app.aichat.ui.chat.PermCheckUtil;
import kr.co.imcloud.app.aichat.ui.common.dialog.ConfirmDialog;
import kr.co.imcloud.app.aichat.ui.common.dialog.NetErrDialog;

/**
 * Created by WB on 2017-08-10.
 */

public class SetIPActivity extends Activity implements NetworkChangeReceiver.OnNetworkChangeListener {
    private static final String TAG = "SetIPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);

        this.getStore().connectInfo.setUserId("imcloud");

        final EditText ip = (EditText) findViewById(R.id.ip_edit_text);
        final String prefIp = getPreferences("ip");
        ip.setText(prefIp);

        this.getStore().removeAllMessageHandler();
        this.getStore().addMessageHandler(messageHandler);


        Button connectBtn = (Button) findViewById(R.id.connect_btn);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppConfig.DEFAULT_APIERVER_URL = "http://" + ip.getText().toString();
                checkNetStatus();
                savePreferences("ip", ip.getText().toString());
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getStore().removeMessageHandler(messageHandler);
//        this.hideSaveWatingDialog();
        NetworkChangeReceiver.removeOnNetworkChangeListener(this);
        System.gc();
    }

    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AuthStore.MSG_WaitAddie:
                case AuthStore.MSG_AUTH: {
                    onMsgCheckHealthy(msg);
                    break;
                }
                case AuthStore.MSG_WaitAddie + 1:
                case AuthStore.MSG_AUTH + 1: {
                    onMsgErrorHealthy(msg);
                    break;
                }
            }
        }
    };

    private void checkNetStatus() {
        int networkStatus = NetworkUtils.getConnectivityStatus(SetIPActivity.this);
        if (networkStatus == NetworkUtils.TYPE_NOT_CONNECTED) {
            showNetErrDlg();
        } else {
            Log.i(TAG, "HealthyChecker");
            checkPerm();
        }
    }


    private void showNetErrDlg() {
        final NetErrDialog dlg = new NetErrDialog(this, (String) this.getText(R.string.dlg_network_message));
//        dlg.setMessage("aa");
        dlg.show();
        dlg.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void changeNetwork(int status) {
        Log.i(TAG, "changeNetwork status is " + status);

        if (status != NetworkUtils.TYPE_NOT_CONNECTED) {
            Log.i(TAG, "Running healthy checker");
//            getStore().reqCheckHealthy(true);
            this.getStore().reqAuth();
        }
    }

    private void onMsgCheckHealthy(Message msg) {
        startInternal();
//        this.runResult();
    }

    private void onMsgErrorHealthy(Message msg) {
        this.showNetworkFailDlg();
    }

    private void showNetworkFailDlg() {
        final ConfirmDialog networkFailConnectionDialog = new ConfirmDialog(SetIPActivity.this);
        networkFailConnectionDialog.show();
        networkFailConnectionDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        networkFailConnectionDialog.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startInternal() {
        Intent intent = new Intent(this, ChatActivity.class);
//        Intent intent = new Intent(this, OrderResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    /////////////////////////////////////////////////////////////
    // Perm Check
    /////////////////////////////////////////////////////////////

    private PermCheckUtil permCheckUtil;


    private void checkPerm() {
        permCheckUtil = this.createPermCheckUtil(new PermCheckListener() {
            @Override
            public void checked(int requestCode, boolean result) {
                if (result && requestCode == PermCheckUtil.PERMISSION_REQUEST_COARSE_LOCATION) {
                    permCheckUtil.request(PermCheckUtil.PERMISSION_REQUEST_FINE_LOCATION);
                } else if (result && requestCode == PermCheckUtil.PERMISSION_REQUEST_FINE_LOCATION) {
                    LocationUtil.inst().reqLocation(SetIPActivity.this);
                }
                getStore().reqAuth();
            }
        });
        permCheckUtil.request(PermCheckUtil.PERMISSION_REQUEST_COARSE_LOCATION);
    }

    private PermCheckUtil createPermCheckUtil(PermCheckListener listener) {
        if (this.permCheckUtil == null) {
            this.permCheckUtil = new PermCheckUtil(this, listener);
        }
        return permCheckUtil;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.permCheckUtil != null) {
            this.permCheckUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
    }
    /////////////////////////////////////////////////////////////

    private AuthStore getStore() {
        return AuthStore.inst();
    }

    // 값 불러오기
    private String getPreferences(String key) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String prefValue = pref.getString(key, "");
        return prefValue;
    }

    // 값 저장하기
    private void savePreferences(String key, String values) {
        if (key != null && values != null) {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(key, values);
            editor.commit();
        }
    }


}