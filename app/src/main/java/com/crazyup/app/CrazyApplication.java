package com.crazyup.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by jeongmin on 17. 3. 17.
 */

public class CrazyApplication extends Application {

    private static final String TAG = "CrazyApplication";

    public static String PACKAGE_NAME;
    private static Resources resources;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        context = this;

        PACKAGE_NAME = getApplicationContext().getPackageName();
        this.resources = this.getResources();
    }

    public static Resources getGlobalResources() {
        return resources;
    }

    public static Context getContext() {
        return context;
    }


}
