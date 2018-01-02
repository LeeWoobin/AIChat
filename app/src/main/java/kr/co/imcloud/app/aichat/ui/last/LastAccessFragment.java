package kr.co.imcloud.app.aichat.ui.last;


import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.co.imcloud.app.aichat.R;
import kr.co.imcloud.app.aichat.models.LastAccessModel;
import kr.co.imcloud.app.aichat.stores.AuthStore;
import kr.co.imcloud.app.aichat.stores.ChatStore;

public class LastAccessFragment extends Fragment {

    public static final String TAG = "LastAccessFragment";

    @Bind(R.id.textView_chat_time)
    TextView textView_chat_time;

    @Bind(R.id.textView_last_access)
    TextView textView_last_access;

    public LastAccessFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_last_access, container, false);
        ButterKnife.bind(this, view);
        updateUi();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ChatStore.inst().reqHideKeyboard();
                return false;
            }
        });
        return view;
    }

    private void updateUi() {
        updateChatTime();
        updateLast();
    }

    private void updateLast() {
        LastAccessModel lastAccessModel = AuthStore.inst().getLastAccess();
        if (lastAccessModel != null) {
            String last_time = lastAccessModel.get_last_time();
            if (last_time == null) {
                this.textView_last_access.setText("");
            } else {
                String[] ss = last_time.split(" ");
                if (ss.length < 2) {
                    this.textView_last_access.setText("");
                    return;
                }
                String date = ss[0].replace("-", ".");
//                String[] dates = ss[0].split("-");
                String[] times = ss[1].split(":");
                String time = String.format(getResources().getString(R.string.format_last_access_time), times[0], times[1]);
                String text = String.format(getResources().getString(R.string.textview_last_access), date + " " + time);
                this.textView_last_access.setText(text);
            }


//            TODO: re formating date time
        }
    }

    private void updateChatTime() {
        int time = ChatStore.inst().getChatTime();
//        Log.d(TAG, "updateChatTime(), time=" + time);
        int minute = time/60;
        int second = time%60;
        String s = String.format("%02d", second);
        String text = String.format(getResources().getString(R.string.textView_chat_time), ""+minute, s);
        this.textView_chat_time.setText(text);
    }

    public void onChatTime(Message msg) {
        updateChatTime();
    }
}
