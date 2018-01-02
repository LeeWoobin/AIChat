package kr.co.imcloud.app.aichat.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import kr.co.imcloud.app.aichat.stores.MsgBase;
import kr.co.imcloud.app.aichat.ui.common.dialog.SaveWatingDialog;

/**
 * Created by jeongmin on 17. 4. 3.
 */

public abstract class StoreBaseActivity extends AppCompatActivity {

    private SaveWatingDialog saveWatingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMessageHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeMessageHandler();
    }

    protected void addMessageHandler() {
        this.getStore().addMessageHandler(messageHandler);
    }

    protected void removeMessageHandler() {
        this.getStore().removeMessageHandler(messageHandler);
    }

    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            onMessage(msg);
        }
    };

    protected void onMessage(Message msg) {
    }

    protected abstract MsgBase getStore();


    protected void showSaveWatingDialog() {
        this.hideKeyboard();
        if(saveWatingDialog == null) {
            saveWatingDialog = new SaveWatingDialog(this);
            saveWatingDialog.show();
        }
    }

    protected void hideSaveWatingDialog() {
        if(saveWatingDialog != null) {
            saveWatingDialog.dismiss();
            saveWatingDialog = null;
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}
