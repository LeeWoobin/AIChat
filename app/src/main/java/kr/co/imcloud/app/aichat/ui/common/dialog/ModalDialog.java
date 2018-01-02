package kr.co.imcloud.app.aichat.ui.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import kr.co.imcloud.app.aichat.R;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public class ModalDialog extends Dialog {

    public ModalDialog(Context context) {
        super(context, R.style.ModalDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onBackPressed() {
        /* Back button disable */
    }

}
