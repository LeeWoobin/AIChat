package kr.co.imcloud.app.aichat.stores;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public class MsgHandler extends MsgBase {

    private static final String TAG = "MsgHandler";


    private Handler backHandler;

    private Thread backThread = new Thread() {
        public void run() {
            Log.d(TAG, "run(), prepare ==================================== ");
            initNet();
            Looper.prepare();
            backHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
//                    boolean isLogined = ParseRegister.inst().isLogined();
//                Log.d(TAG, "handleMessage(), what=" + msg.what);
                    if(msg.what > AuthStore.MSG_START && msg.what < AuthStore.MSG_END) {
                        AuthStore.inst().handleMessage(msg);
                        return;
                    }
                    else if(msg.what > ChatStore.MSG_START && msg.what < ChatStore.MSG_END) {
                        ChatStore.inst().handleMessage(msg);
                        return;
                    }
                    Log.e(TAG, "handleMessage(), what=" + msg.what);
                }
            };
            Looper.loop();
//            Log.d(TAG, "run(), exit =================== ");
        }
    };

    public void clearHandlers() {
        ChatStore.inst().removeAllMessageHandler();
    }

    @Override
    void handleMessage(Message msg) {

    }

    @Override
    public void clear() {

    }


    protected void start() {
        Log.d(TAG, "start()");
        this.backThread.start();
    }

    protected void waitForThreadInit() {
        while(this.backHandler == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected android.os.Handler getBackHandler() {
        return backHandler;
    }


    public final Message obtainMessage() {
        return this.getBackHandler().obtainMessage();
    }

    protected boolean sendMessage(Message msg) {
        return this.getBackHandler().sendMessage(msg);
    }

    private void initNet() {
    }

}
