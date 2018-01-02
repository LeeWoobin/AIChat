package kr.co.imcloud.app.aichat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.crashlytics.android.Crashlytics;
import com.crazyup.utils.LocationUtil;
import com.crazyup.utils.NetworkUtils;

import io.fabric.sdk.android.Fabric;
import kr.co.imcloud.app.aichat.receiver.NetworkChangeReceiver;
import kr.co.imcloud.app.aichat.stores.AuthStore;
import kr.co.imcloud.app.aichat.ui.chat.ChatActivity;
import kr.co.imcloud.app.aichat.ui.chat.PermCheckListener;
import kr.co.imcloud.app.aichat.ui.chat.PermCheckUtil;
import kr.co.imcloud.app.aichat.ui.common.dialog.ConfirmDialog;
import kr.co.imcloud.app.aichat.ui.common.dialog.NetErrDialog;

/**
 * Created by WB on 2017-08-21.
 */

public class SplashActivity extends AppCompatActivity implements NetworkChangeReceiver.OnNetworkChangeListener {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeNoTitle);
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        this.getStore().removeAllMessageHandler();
        this.getStore().addMessageHandler(messageHandler);

        checkNetStatus();
    }



    private void setGif(){
        ImageView imageView = (ImageView)findViewById(R.id.imageView_intro);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.drawable.samsung_splash).into(imageViewTarget);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getStore().removeMessageHandler(messageHandler);
        NetworkChangeReceiver.removeOnNetworkChangeListener(this);
        System.gc();
    }


    private AuthStore getStore() {
        return AuthStore.inst();
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
        int networkStatus = NetworkUtils.getConnectivityStatus(SplashActivity.this);
        if (networkStatus == NetworkUtils.TYPE_NOT_CONNECTED) {
            showNetErrDlg();
        } else {
            Log.i(TAG, "HealthyChecker");
//            setGif();
            checkPerm();
        }
    }


    private void showNetErrDlg() {
        final NetErrDialog dlg = new NetErrDialog(this, (String) this.getText(R.string.dlg_network_message));
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
    }

    private void onMsgErrorHealthy(Message msg) {
        this.showNetworkFailDlg();
    }

    private void showNetworkFailDlg() {
        final ConfirmDialog networkFailConnectionDialog = new ConfirmDialog(SplashActivity.this);
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
                }
                else if (result && requestCode == PermCheckUtil.PERMISSION_REQUEST_FINE_LOCATION) {
                    LocationUtil.inst().reqLocation(SplashActivity.this);
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

}
