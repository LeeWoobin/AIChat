package kr.co.imcloud.app.aichat.receiver;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.crazyup.utils.NetworkUtils;
import com.crazyup.utils.ProcessUtils;

import java.util.ArrayList;
import java.util.List;

import kr.co.imcloud.app.aichat.ui.common.dialog.NetworkDialogActivity;


/**
 * Created by jeongmin on 17. 3. 17.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetChangeReceiver";

    public static List<OnNetworkChangeListener> onNetworkChangeListeners = new ArrayList<OnNetworkChangeListener>();
    public interface OnNetworkChangeListener {
        void changeNetwork(int status);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtils.getConnectivityStatus(context);
        Log.d(TAG, "Network setting changed status is " + status);

//        JoonBoxPreference.getInst().setProp(JoonBoxPreference.NETWORK_STATUS, status);
        synchronized (onNetworkChangeListeners) {
            for (int i = 0; i < onNetworkChangeListeners.size(); i++) {
                onNetworkChangeListeners.get(i).changeNetwork(status);
            }
        }

        if (ProcessUtils.isRunningProcess(context)) {
            Log.i(TAG, "Process is running foreground!");
            if (status == 0) {
                if (! NetworkUtils.isDlgShow()) {
                    Log.i(TAG, "NetworkDialogActivity is deactivated");
                    Intent popupIntent = new Intent(context, NetworkDialogActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(context, 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);

                    try {
                        pi.send();
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG);
                    }
                } else {
                }
            } else {
                Log.i(TAG, "NetworkDialogActivity is activated");
            }
        }
    }

    public static void setOnNetworkChangeListener(OnNetworkChangeListener onNetworkChangeListener) {
        synchronized (onNetworkChangeListeners) {
            onNetworkChangeListeners.add(onNetworkChangeListener);
        }
    }

    public static void resetOnNetworkChangeListener() {
        synchronized (onNetworkChangeListeners) {
            onNetworkChangeListeners.clear();
        }
    }

    public static void removeOnNetworkChangeListener(OnNetworkChangeListener onNetworkChangeListener) {
        synchronized (onNetworkChangeListeners) {
            try {
                onNetworkChangeListeners.remove(onNetworkChangeListener);
            } catch (Exception e) {

            }
        }
    }


    public static ActivityManager.RunningAppProcessInfo isRunningProcess(Context context) {
        String strPackage = "";
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> proceses = am.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo process : proceses) {
            if(process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (process.processName.equals("kr.co.infomark.joonbox")) {
                    return process;
                } else {
                    break;
                }
            }
        }
        return null;
    }


}
