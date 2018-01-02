package kr.co.imcloud.app.aichat.stores;

import android.util.Log;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public class DataStore extends MsgHandler {
    private static final String TAG = "DataStore";
    private static DataStore _inst = null;

    public static DataStore inst() {
        if (_inst == null) {
            _inst = new DataStore();
            Log.d(TAG, "inst(), _inst="+_inst);
        }
        _inst.waitForThreadInit();
        return _inst;
    }

    private DataStore() {
        this.start();
    }

}
