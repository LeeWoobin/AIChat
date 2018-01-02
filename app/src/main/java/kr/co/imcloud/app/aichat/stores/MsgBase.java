package kr.co.imcloud.app.aichat.stores;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public abstract class MsgBase {

    private static final String TAG = "MsgBase";

    static final int MSG_START_COMMON = 2000;
    static final int MSG_START_STORE = 3000;
//    static final int MSG_START = 1000;

    public static final int MSG_REMOVE_DEVICE = MSG_START_COMMON+1;
    public static final int MSG_ITEM_REMOVED = MSG_START_COMMON+3;
    public static final int MSG_ITEMS_MAX = MSG_START_COMMON+5;

    public static final int PARSE_ERROR_TYPE_SAVE = 1;
    public static final int PARSE_ERROR_TYPE_GET = 2;
    public static final int PARSE_ERROR_TYPE_PUSH = 3;
    public static final int PARSE_ERROR_TYPE_LOGIN = 4;
    public static final int PARSE_ERROR_TYPE_ACTIVATION = 5;
    public static final int PARSE_ERROR_TYPE_PAIRING = 6;
    public static final int PARSE_ERROR_TYPE_REQUEST = 7;


    private List<Handler> messageHandlers = new ArrayList<Handler>();

    protected Handler getBackHandler() {
        return DataStore.inst().getBackHandler();
    }

    abstract void handleMessage(Message msg);

    protected Message makeMessage(int what) {
        return Message.obtain(this.getBackHandler(), what);
    }

    protected boolean sendMessageDelayed(Message msg, long delay) {
        return this.getBackHandler().sendMessageDelayed(msg, delay);
    }

    protected boolean sendMessage(int what)
    {
        return this.sendMessageDelayed(makeMessage(what), 0);
    }

    protected boolean sendMessage(Message msg)
    {
        return this.sendMessageDelayed(msg, 0);
    }

    public void addMessageHandler(Handler handler) {
        if(messageHandlers.contains(handler))
            return;
//        Log.d(TAG, "addMessageHandler(), handler="+handler);
        messageHandlers.add(handler);
    }

    public void removeMessageHandler(Handler handler) {
//        Log.d(TAG, "removeMessageHandler(), handler="+handler);
        messageHandlers.remove(handler);
    }

    public void removeAllMessageHandler() {
//        Log.d(TAG, "removeMessageHandler(), handler="+handler);
        this.messageHandlers.clear();
    }


    protected void notifyMessage(Message msg) {
        for(Handler handler: this.messageHandlers) {
//            Log.d(TAG, "notifyMessage(), handler="+handler + ", what=" +msg.what);
            handler.sendMessage(Message.obtain(msg));
        }
    }

    protected void notifyMessage(int what) {
        Message msg = makeMessage(what);
        notifyMessage(msg);
    }


    abstract public void clear();

    public void notifyError(Message msgs) {
        msgs.what = msgs.what + 1;
        this.notifyMessage(msgs);
    }
}
