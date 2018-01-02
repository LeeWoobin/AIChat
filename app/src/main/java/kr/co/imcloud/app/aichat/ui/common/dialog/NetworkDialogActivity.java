package kr.co.imcloud.app.aichat.ui.common.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;

import com.crazyup.utils.NetworkUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.imcloud.app.aichat.R;
import kr.co.imcloud.app.aichat.receiver.NetworkChangeReceiver;
import kr.co.imcloud.app.aichat.ui.base.CommonDialogActivity;

public class NetworkDialogActivity extends CommonDialogActivity implements NetworkChangeReceiver.OnNetworkChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        ButterKnife.bind(this);
        NetworkChangeReceiver.setOnNetworkChangeListener(this);
    }

    @OnClick(R.id.okLinearLayout)
    public void networkSetLinearLayout() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void changeNetwork(int status) {
        if (status != NetworkUtils.TYPE_NOT_CONNECTED) {
            NetworkChangeReceiver.removeOnNetworkChangeListener(this);
            NetworkUtils.setIsDlgShow(false);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
