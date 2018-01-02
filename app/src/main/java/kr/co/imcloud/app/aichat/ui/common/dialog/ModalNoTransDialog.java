package kr.co.imcloud.app.aichat.ui.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import kr.co.imcloud.app.aichat.R;

/**
 * Created by june on 16. 6. 26.
 */
public class ModalNoTransDialog extends Dialog {

    public ModalNoTransDialog(Context context) {
        super(context, R.style.NetCheckingDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onBackPressed() {
        /* Back button disable */
    }
}