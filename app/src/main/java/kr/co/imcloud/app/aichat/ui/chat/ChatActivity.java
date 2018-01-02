package kr.co.imcloud.app.aichat.ui.chat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import kr.co.imcloud.app.aichat.ui.common.dialog.NetErrDialog;
import kr.co.imcloud.app.aichat.ui.common.dialog.NetRetryDialog;
import kr.co.imcloud.app.aichat.ui.last.LastAccessFragment;

public class ChatActivity extends StoreBaseActivity implements ThreadsFragment.OnListFragmentInteractionListener{

    private static final String TAG = "ChatActivity";
    @Bind(R.id.message_input)
    EditText message_input;

    @Bind(R.id.button_send)
    Button button_send;

    @Bind(R.id.stt_btn)
    Button button_stt;


//    @Bind(R.id.constraintLayout_last_access)
//    FrameLayout constraintLayout_last_access;

    private ThreadsFragment chatFagment;
    private LastAccessFragment lastAccessFragment;

    private ArrayList<String> mResult;
    private String mSelectedString;
    private final int GOOGLE_STT = 1000;
    private final static int PERMISSIONS_REQUEST_CODE = 100;

    private static final String CLIENT_ID = "LTDeYqvG_QqIFPz_mew3"; // "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
//    private Button btnStart;
    private String result;
    private AudioWriterPCM writer;

    private boolean isGoogle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.getStore().addMessageHandler(messageHandler);
        this.getStore().setChatModel(new ChatModel());
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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

/*
        BannerModel banner = this.getStore().getBanner();
        if(banner != null && banner.get_type() != BannerModel.TB_None) {
            notiFragment = new NotiFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_noti, notiFragment)
                    .commit();
        } else {
            this.fragment_noti.setVisibility(View.GONE);
        }
*/
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //API 23 이상이면
                // 런타임 퍼미션 처리 필요

                int hasMicrophonePermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO);

                if ( hasMicrophonePermission == PackageManager.PERMISSION_GRANTED){
                    ;//이미 퍼미션을 가지고 있음
                }
                else {
                    //퍼미션 요청
                    ActivityCompat.requestPermissions( this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            PERMISSIONS_REQUEST_CODE);
                }
            }
            else{

            }


        } else {
            Toast.makeText(ChatActivity.this, "Mic not supported",
                    Toast.LENGTH_LONG).show();
        }

        button_stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isGoogle) {
                    try {
                        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);            //intent 생성
                        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());    //호출한 패키지
                        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");                            //음성인식 언어 설정
                        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "말해주세요.");                     //사용자에게 보여 줄 글자

                        startActivityForResult(i, GOOGLE_STT);
                    } catch (ActivityNotFoundException e) {
                        String appPackageName = "com.google.android.googlequicksearchbox";
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    }
                }else {
                    if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                        result = "";
//                    btnStart.setText(R.string.str_stop);
                        naverRecognizer.recognize();
                    } else {
                        button_stt.setEnabled(false);
                        naverRecognizer.getSpeechRecognizer().stop();
                    }
                }
            }
        });

//        btnStart = (Button) findViewById(R.id.btn_start);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!naverRecognizer.getSpeechRecognizer().isRunning()) {
//                    result = "";
//                    btnStart.setText(R.string.str_stop);
//                    naverRecognizer.recognize();
//                } else {
//                    btnStart.setEnabled(false);
//                    naverRecognizer.getSpeechRecognizer().stop();
//                }
//            }
//        });

        final ConfirmDialog dlg = new ConfirmDialog(this, "구글로 할래?");
//        dlg.setMessage("aa");
        dlg.show();
        dlg.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                isGoogle = false;
            }
        });
        dlg.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                isGoogle = true;
            }
        });


        this.message_input.addTextChangedListener(new MessageInputWatcher(this.message_input));
        this.updateUi();
        setListenerToRootView();
        this.getStore().startChatTimer();
        this.getStore().reqSendEmptyChat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        this.getStore().removeMessageHandler(messageHandler);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        startOrderResultActiviey();
        showExitConfirmDlg();
    }

    protected ChatStore getStore() {return ChatStore.inst();
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
            case ChatStore.MSG_CHAT_RECV+1:
            case ChatStore.MSG_CHAT_SEND+1: {
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

    public void sendChatMessage(String message ) {
        int ret = this.getStore().reqSendChat(message );
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
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    boolean kbShow = false;

    private void onChangeKeyboard() {
        kbShow = true;

        if (this.chatFagment != null) {
//            getSupportFragmentManager().beginTransaction().hide(this.fragment).commit();
//            getSupportFragmentManager().beginTransaction().show(this.fragment).commit();
            getSupportFragmentManager().beginTransaction().detach(this.chatFagment).commitAllowingStateLoss();
            getSupportFragmentManager().beginTransaction().attach(this.chatFagment).commitAllowingStateLoss();
//            this.fragment.scrollToBottom();
        }

//        Fragment f = getSupportFragmentManager().findFragmentById(R.id.threads_frame_layout);
//        if (f != null) {
//            f.de
//            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
//            fragTransaction.detach(f);
//            fragTransaction.attach(f);
//            fragTransaction.commit();
//            this.fragment.scrollToBottom();
//        }


//        android.app.Fragment currentFragment = getFragmentManager().findFragmentByTag(ThreadsFragment.TAG);
//        if (currentFragment != null) {
//            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
//            fragTransaction.detach(currentFragment);
//            fragTransaction.attach(currentFragment);
//            fragTransaction.commit();
////            this.fragment.scrollToBottom();
//        }
    }

    public void setListenerToRootView() {
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
//                onChangeKeyboard();


//                if(fragment != null) {
//                    fragment.scrollToBottom();
//                }

                if (! kbShow && heightDiff > 100) { // 99% of the time the height diff will be due to a keyboard.
//                    onChangeKeyboard();
//                    Toast.makeText(getApplicationContext(), "keyboard show", Toast.LENGTH_SHORT).show();
                } else {
                    kbShow = false;
//                    Toast.makeText(getApplicationContext(), "keyboard hidden", Toast.LENGTH_SHORT).show();
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
//        dlg.setMessage("aa");
        dlg.show();
        dlg.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
//                finish();
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


    /////////////////////////////////////////////////////////////
    // Perm Check
    /////////////////////////////////////////////////////////////

    private PermCheckUtil permCheckUtil;


    private void checkPerm() {
        permCheckUtil = this.createPermCheckUtil(new PermCheckListener() {
            @Override
            public void checked(int requestCode, boolean result) {
                if (result && requestCode == PermCheckUtil.PERMISSION_REQUEST_COARSE_LOCATION) {
                    permCheckUtil.request(PermCheckUtil.PERMISSION_REQUEST_COARSE_LOCATION);
                }
                else if (result && requestCode == PermCheckUtil.PERMISSION_REQUEST_FINE_LOCATION) {
                    LocationUtil.inst().reqLocation(ChatActivity.this);
                }
            }
        });
        permCheckUtil.request(PermCheckUtil.PERMISSION_REQUEST_COARSE_LOCATION);
    }

    private PermCheckUtil createPermCheckUtil(PermCheckListener listener) {
        if (this.permCheckUtil == null) {
            this.permCheckUtil = new PermCheckUtil(this, listener);
        }
        return permCheckUtil;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.permCheckUtil != null) {
            this.permCheckUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
    }
    /////////////////////////////////////////////////////////////



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
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if( resultCode == RESULT_OK  && (requestCode == GOOGLE_STT ) ){
            showSelectDialog(requestCode, data);
        }
        else{
            String msg = null;

            switch(resultCode){
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

            if(msg != null)
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showSelectDialog(int requestCode, Intent data){
        String key = "";
        if(requestCode == GOOGLE_STT)
            key = RecognizerIntent.EXTRA_RESULTS;

        mResult = data.getStringArrayListExtra(key);
        String[] result = new String[mResult.size()];
        mResult.toArray(result);

        AlertDialog ad = new AlertDialog.Builder(this).setTitle("")
                .setSingleChoiceItems(result, -1, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mSelectedString = mResult.get(which);
                    }
                })
                .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        sendChatMessage(mSelectedString);
                    }
                })
                .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        ad.show();
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady: // 음성인식 준비 가능
//                txtResult.setText("Connected");
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;
            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;
            case R.id.partialResult:
                result = (String) (msg.obj);
                this.sendChatMessage(result);
                break;
            case R.id.finalResult: // 최종 인식 결과
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                result = strBuf.toString();
                this.sendChatMessage(result);
                break;
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                result = "Error code : " + msg.obj.toString();
//                txtResult.setText(result);
                button_stt.setEnabled(true);
                break;
            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                button_stt.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart(); // 음성인식 서버 초기화는 여기서
        naverRecognizer.getSpeechRecognizer().initialize();
    }
    @Override
    protected void onResume() {
        super.onResume();
        result = "";
//        txtResult.setText("");
        button_stt.setEnabled(true);
    }
    @Override
    protected void onStop() {
        super.onStop(); // 음성인식 서버 종료
        naverRecognizer.getSpeechRecognizer().release();
    }
    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<ChatActivity> mActivity;
        RecognitionHandler(ChatActivity activity) {
            mActivity = new WeakReference<ChatActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            ChatActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

}
