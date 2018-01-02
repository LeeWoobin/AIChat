package kr.co.imcloud.app.aichat.ui.common.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.imcloud.app.aichat.R;

/**
 * Created by WB on 2017-08-18.
 */

public class TableDialog extends ModalDialog{

    public Context context;
    public String[][] trtd;
    public String[][] trtdColor;
    public Button applyBtn , changeAllBtn ,closeBtn;
    public TableLayout table1,table2;
    public boolean enableApply = false;
    private InputMethodManager imm;

    public TableDialog(@NonNull Context context) {
        super(context);
    }

    public TableDialog(@NonNull Context context , String[][] trtd ,String[][] trtdColor) {
        super(context);
        this.trtd = trtd;
        this.trtdColor = trtdColor;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_table);

        imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);


        applyBtn = (Button)findViewById(R.id.apply_btn);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                checkEnableApply();
                if(enableApply) {
                    chageApply();
                    cancel();
                }else{
                    Toast.makeText(context,"빈칸을 모두 채워주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
        changeAllBtn = (Button)findViewById(R.id.change_all_btn);
        changeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                TableRow tableRow = (TableRow) table1.getChildAt(2);
                for(int i =1; i < 5 ; i ++){
                    EditText editText = (EditText) tableRow.getChildAt(i);
                    editText.setText(trtd[1][i]);

                }
                TableRow tableRow2 = (TableRow) table2.getChildAt(2);
                for(int i =0; i < 4 ; i ++){
                    EditText editText = (EditText) tableRow2.getChildAt(i);
                    editText.setText(trtd[1][i+5]);
                }
                table1.invalidate();
                table1.refreshDrawableState();
                table2.invalidate();
                table2.refreshDrawableState();
            }
        });
        closeBtn = (Button)findViewById(R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                dismiss();
            }
        });
        table1 = (TableLayout)findViewById(R.id.table);
        table2 = (TableLayout)findViewById(R.id.table2);

       setTable();

    }

    public void checkEnableApply(){
        TableRow tableRow = (TableRow) table1.getChildAt(2);
        for(int i =1; i < 5 ; i ++){
            EditText editText = (EditText) tableRow.getChildAt(i);
            if(editText.getText().toString() != null && !editText.getText().toString().equals("")){
            }else{
                enableApply =false;
                return;
            }
        }

        TableRow tableRow2 = (TableRow) table2.getChildAt(2);
        for(int i =0; i < 4 ; i ++){
            EditText editText = (EditText) tableRow2.getChildAt(i);
            if(editText.getText().toString() != null && !editText.getText().toString().equals("")){
                enableApply =true;
            }else{
                enableApply =false;
                return;
            }
        }
    }

    public void chageApply(){
        TableRow tableRow = (TableRow) table1.getChildAt(2);
        for(int i =1; i < 5 ; i ++){
            EditText editText = (EditText) tableRow.getChildAt(i);
            if(editText.getText().toString() != null && !editText.getText().toString().equals("")){
                trtd[2][i] = editText.getText().toString();
            }
        }

        TableRow tableRow2 = (TableRow) table2.getChildAt(2);
        for(int i =0; i < 4 ; i ++){
            EditText editText = (EditText) tableRow2.getChildAt(i);
            if(editText.getText().toString() != null && !editText.getText().toString().equals("")){
                trtd[2][i+5] = editText.getText().toString();
            }
        }
    }

    public void setTable(){
        for(int tr =0; tr<6; tr++){
            TableRow tableRow = (TableRow) table1.getChildAt(tr);
            if(tableRow != null && tableRow.getChildCount()>0){
                for(int td =0; td < 5 ; td++){
                    if(tr ==2){
                        if(td == 0) {
                            TextView textView = (TextView) tableRow.getChildAt(td);
                            textView.setText(trtd[tr][td]);
                            if (trtdColor[tr][td] != null) {
                                textView.setBackgroundColor(Color.parseColor(trtdColor[tr][td]));
                            }
                        }else{
                            EditText editText = (EditText) tableRow.getChildAt(td);
                            editText.setText(trtd[tr][td]);
                            if (trtdColor[tr][td] != null) {
                                editText.setBackgroundColor(Color.parseColor(trtdColor[tr][td]));
                            }
                        }
                    }else {
                        TextView textView = (TextView) tableRow.getChildAt(td);
                        textView.setText(trtd[tr][td]);
                        if (trtdColor[tr][td] != null) {
                            textView.setBackgroundColor(Color.parseColor(trtdColor[tr][td]));
                        }
                    }
                }
            }
        }

        for(int tr =0; tr<6; tr++){
            TableRow tableRow = (TableRow) table2.getChildAt(tr);
            if(tableRow != null && tableRow.getChildCount()>0){
                for(int td =5; td < 9; td++){
                    if(tr ==2){
                        EditText editText = (EditText) tableRow.getChildAt(td-5);
                        editText.setText(trtd[tr][td]);
                        if (trtdColor[tr][td] != null) {
                            editText.setBackgroundColor(Color.parseColor(trtdColor[tr][td]));
                        }

                    }else {
                        TextView textView = (TextView) tableRow.getChildAt(td-5);
                        textView.setText(trtd[tr][td]);
                        if (trtdColor[tr][td] != null) {
                            textView.setBackgroundColor(Color.parseColor(trtdColor[tr][td]));
                        }
                    }
                }
            }
        }
    }

    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }



}
