package com.crazyup.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.crazyup.app.CrazyApplication;

import java.util.List;


/**
 * Created by jeongmin on 17. 3. 17.
 */

public class ProcessUtils {
    private final static String TAG = "ProcessUtils";

    public static boolean isRunningProcess(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> proceses = am.getRunningAppProcesses();

        if(proceses == null) {
            Log.d(TAG, "isRunningProcess(), processes is null");
            return false;
        }

        Log.i(TAG, "START");
        for(ActivityManager.RunningAppProcessInfo process : proceses) {
            Log.i(TAG, "process  " + process.processName + " importance : " + process.importance);
            if(process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Log.i("IMPORTANCE_FOREGROUND", "processName " + process.processName);
                if (process.processName.equals(CrazyApplication.PACKAGE_NAME)) {
                    return true;
                } else {
                    break;
                }
            }
        }
        Log.i(TAG, "END");
        return false;
    }

}
