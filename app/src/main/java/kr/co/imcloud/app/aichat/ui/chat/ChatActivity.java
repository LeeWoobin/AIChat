package kr.co.imcloud.app.aichat.ui.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crazyup.utils.LocationUtil;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.imcloud.app.aichat.R;
import kr.co.imcloud.app.aichat.clova.AudioWriterPCM;
import kr.co.imcloud.app.aichat.clova.NaverRecognizer;
import kr.co.imcloud.app.aichat.models.ChatModel;
import kr.co.imcloud.app.aichat.models.ThreadItem;
import kr.co.imcloud.app.aichat.stores.ChatStore;
import kr.co.imcloud.app.aichat.ui.base.StoreBaseActivity;
import kr.co.imcloud.app.aichat.ui.chat.threads.ThreadsFragment;
import kr.co.imcloud.app.aichat.ui.common.dialog.ConfirmDialog;
import kr.co.imcloud.app.aichat.ui.common.dialog.LanguageDialog;
import kr.co.imcloud.app.aichat.ui.common.dialog.NetErrDialog;
import kr.co.imcloud.app.aichat.ui.common.dialog.NetRetryDialog;
import kr.co.imcloud.app.aichat.ui.last.LastAccessFragment;

public class ChatActivity extends StoreBaseActivity implements ThreadsFragment.OnListFragmentInteractionListener {

    private static final String TAG = "ChatActivity";
    @Bind(R.id.message_input)
    EditText message_input;

    @Bind(R.id.button_send)
    Button button_send;

    @Bind(R.id.stt_btn)
    Button button_stt;

    private ThreadsFragment chatFagment;
    private LastAccessFragment lastAccessFragment;

    private ArrayList<String> mResult;
    private final int GOOGLE_STT = 1000;
    private final static int PERMISSIONS_REQUEST_CODE = 100;

    public MediaPlayer mediaPlayer;

    private String lang = "ko-KR";
    private String lang_text = "말씀해주세요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.getStore().addMessageHandler(messageHandler);
        this.getStore().setChatModel(new ChatModel());
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);


        chatFagment = new ThreadsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.threads_frame_layout, chatFagment, ThreadsFragment.TAG)
                .commit();

        lastAccessFragment = new LastAccessFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.constraintLayout_last_access, lastAccessFragment, LastAccessFragment.TAG)
                .commit();

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //API 23 이상이면
                // 런타임 퍼미션 처리 필요

                int hasMicrophonePermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO);

                if (hasMicrophonePermission == PackageManager.PERMISSION_GRANTED) {
                    ;//이미 퍼미션을 가지고 있음
                } else {
                    //퍼미션 요청
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSIONS_REQUEST_CODE);
                }
            } else {
            }


        } else {
            Toast.makeText(ChatActivity.this, "Mic not supported",
                    Toast.LENGTH_LONG).show();
        }

        final LanguageDialog languageDialog = new LanguageDialog(ChatActivity.this);
        languageDialog.show();
        languageDialog.setKoreanClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = "ko-KR";
                lang_text = "말씀해주세요.";
                languageDialog.dismiss();
            }
        });
        languageDialog.setEnglishClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lang = "en-US";
                lang_text = "Please speak.";
                languageDialog.dismiss();
            }
        });

        button_stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                }

                try {
                    Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
                    i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //호출한 패키지
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang);                            //음성인식 언어 설정
                    i.putExtra(RecognizerIntent.EXTRA_PROMPT, lang_text);                     //사용자에게 보여 줄 글자

                    startActivityForResult(i, GOOGLE_STT);
                } catch (ActivityNotFoundException e) {
                    String appPackageName = "com.google.android.googlequicksearchbox";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                }

            }
        });

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        this.message_input.addTextChangedListener(new MessageInputWatcher(this.message_input));
        this.updateUi();
        setListenerToRootView();
        this.getStore().startChatTimer();
//        this.getStore().reqSendEmptyChat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        this.getStore().removeMessageHandler(messageHandler);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        startOrderResultActiviey();
        showExitConfirmDlg();
    }

    protected ChatStore getStore() {
        return ChatStore.inst();
    }

    protected void onMessage(Message msg) {
        switch (msg.what) {
            case ChatStore.MSG_CHAT_RECV:
            case ChatStore.MSG_CHAT_SEND: {
                onChat(msg);
                break;
            }
            case ChatStore.MSG_ChatTime: {
                onChatTime(msg);
                break;
            }
            case ChatStore.MSG_OrderCancel: {
                onMsg_OrderCancel(msg);
                break;
            }
            case ChatStore.MSG_HIDE_KEYBOARD: {
                this.hideKeyboard();
                break;
            }
            case ChatStore.MSG_CHAT_RECV + 1:
            case ChatStore.MSG_CHAT_SEND + 1: {
                showNetServerErrDlg(msg);
                break;
            }
        }
    }


    private void onMsg_OrderCancel(Message msg) {
        showConfirmOrderCancel();
    }

    private void showConfirmOrderCancel() {
        String message = (String) this.getText(R.string.dlg_message_order_cancel);
        final NetErrDialog dlg = new NetErrDialog(this, message);
        dlg.show();
        dlg.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                finish();
            }
        });
    }


    private void updateUi() {
        String message = this.message_input.getText().toString().trim();
        if (message != null && message.length() > 0) {
            this.button_send.setVisibility(View.VISIBLE);
        } else {
            this.button_send.setVisibility(View.INVISIBLE);
        }
    }

    private void onChat(Message msg) {
        this.message_input.setText("");
    }

    private void onChatTime(Message msg) {
        if (this.lastAccessFragment != null) {
            lastAccessFragment.onChatTime(msg);
        }
    }


    @OnClick(R.id.button_send)
    public void onClickSend() {
        String message = this.message_input.getText().toString().trim();
        this.sendChatMessage(message);
    }

    public void sendChatMessage(String message) {
        int ret = this.getStore().reqSendChat(message);
        if (ret == 4) {
            showConfirmNetErr();
        } else if (ret > 0) {
            showConfirmRetry(ret, message);
        }
    }

    private void showConfirmRetry(int count, final String sendingMessage) {
        String format = (String) this.getText(R.string.dlg_format_message_net_retry);
        String message = String.format(format, count);
        final NetRetryDialog dlg = new NetRetryDialog(this, message);
        dlg.show();
        dlg.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                sendChatMessage(sendingMessage);
            }
        });
        dlg.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                finish();
            }
        });


    }

    private void showConfirmNetErr() {
        String message = (String) this.getText(R.string.dlg_message_net_err2);
        final NetErrDialog dlg = new NetErrDialog(this, message);
        dlg.show();
        dlg.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                finish();
            }
        });
    }


    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    boolean kbShow = false;


    public void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                if (!kbShow && heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
                } else {
                    kbShow = false;
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.keyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        } else if (newConfig.keyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard show", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onListFragmentInteraction(ThreadItem item) {
//        Log.d(TAG, "onListFragmentInteraction(), item=" + item.id);
    }

    private void showExitConfirmDlg() {
        final ConfirmDialog dlg = new ConfirmDialog(this, (String) this.getText(R.string.confirm_exit));
        dlg.show();
        dlg.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        dlg.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                finish();
            }
        });

    }

    private void showNetServerErrDlg(Message msg) {
        int responseCode = msg.getData().getInt("responseCode", 0);
        String message = (String) this.getText(R.string.dlg_message_net_server_err);
        if (responseCode == 401) {
            message = (String) this.getText(R.string.dlg_message_net_session_err);
        }
        final NetErrDialog dlg = new NetErrDialog(this, message);
        dlg.show();
        dlg.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                finish();
            }
        });

    }



    protected class MessageInputWatcher implements TextWatcher {

        private EditText view;

        public MessageInputWatcher(EditText view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateUi();
        }
    }

    ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && (requestCode == GOOGLE_STT)) {
            String key = "";
            if (requestCode == GOOGLE_STT)
                key = RecognizerIntent.EXTRA_RESULTS;

            mResult = data.getStringArrayListExtra(key);
            String[] result = new String[mResult.size()];
            mResult.toArray(result);

            sendChatMessage(mResult.get(0));
        } else {
            String msg = null;

            switch (resultCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    break;
            }

            if (msg != null)
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
