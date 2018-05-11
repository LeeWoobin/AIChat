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

public class LanguageDialog extends ModalDialog {

    @Bind(R.id.button_ko)
    Button button_ko;

    @Bind(R.id.button_en)
    Button button_en;

    public LanguageDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_lang_select);
        ButterKnife.bind(this);
    }

    public void setKoreanClickListener(View.OnClickListener onClickListener) {
        button_ko.setOnClickListener(onClickListener);
    }

    public void setEnglishClickListener(View.OnClickListener onClickListener) {
        button_en.setOnClickListener(onClickListener);
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
}
