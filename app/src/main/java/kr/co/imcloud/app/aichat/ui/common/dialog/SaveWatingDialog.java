package kr.co.imcloud.app.aichat.ui.common.dialog;

import android.content.Context;
import android.os.Bundle;

import kr.co.imcloud.app.aichat.R;

/**
 * Created by june on 16. 6. 26.
 */
public class SaveWatingDialog extends ModalNoTransDialog {

    private static final String TAG = "WatingDialog";

    public SaveWatingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save_wait);
//        Log.d(TAG, "onCreate() >>>>>>>>>>>>>>>>> ");
    }

    @Override
    public void dismiss() {
//        Log.d(TAG, "dismiss() <<<<<<<<<<<<<<<<<< ");
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
        }
    }
}
