package kr.co.imcloud.app.aichat.ui.common.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.co.imcloud.app.aichat.R;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public class ConfirmDialog extends ModalDialog {

    @Bind(R.id.button_cancel)
    Button button_cancel;

    @Bind(R.id.button_ok)
    Button button_ok;

    @Bind(R.id.textView_dlg_message)
    TextView textView_dlg_message;

    private String message;


    public ConfirmDialog(Context context) {
        super(context);
    }

    public ConfirmDialog(Context context, String message) {
        super(context);
        this.message = message;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        ButterKnife.bind(this);
        if (this.message != null) {
            this.textView_dlg_message.setText(message);
        }
    }

    public void setCancelClickListener(View.OnClickListener onClickListener) {
        button_cancel.setOnClickListener(onClickListener);
    }

    public void setOkClickListener(View.OnClickListener onClickListener) {
        button_ok.setOnClickListener(onClickListener);
    }


    @Override
    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public void setMessage(String message) {

    }
}
