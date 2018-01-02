package kr.co.imcloud.app.aichat.ui.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by jeongmin on 17. 4. 21.
 */

public class PermCheckUtil {

    private static final String TAG = "PermCheckUtil";

    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 100;
    public static final int PERMISSION_REQUEST_WRITE_SETTINGS = 200;
    public static final int PERMISSION_REQUEST_FINE_LOCATION = 300;

    private Activity activity;
    private PermCheckListener listener;

    public PermCheckUtil(Activity activity, PermCheckListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    private void response(int requestCode, boolean result) {
        if (listener != null) {
            listener.checked(requestCode, result);
        }
    }

    public void request(int requestCode) {
        switch(requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                this.reqCoarseLocationPermissions();
                break;
            }
            case PERMISSION_REQUEST_FINE_LOCATION: {
                this.reqFineLocationPermissions();
                break;
            }
            case PERMISSION_REQUEST_WRITE_SETTINGS: {
                this.reqWriteSettingsPermissions();
                break;
            }
        }
    }


    public boolean reqCoarseLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                return true;
            }
        }
        response(PERMISSION_REQUEST_COARSE_LOCATION, true);
        return false;
    }

    public boolean reqWriteSettingsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(activity)) {
                response(PERMISSION_REQUEST_WRITE_SETTINGS, true);
                return false;
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                return true;
            }
        }
        response(PERMISSION_REQUEST_WRITE_SETTINGS, true);
        return false;
    }

    public boolean reqFineLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
                return true;
            }
        }
        response(PERMISSION_REQUEST_FINE_LOCATION, true);
        return false;
    }



    public void onRequestPermissionsResult(final int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_SETTINGS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                    response(requestCode, true);
                } else {
                    response(requestCode, false);
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                    builder.setTitle("Functionality limited");
//                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
//                    builder.setPositiveButton(android.R.string.ok, null);
//                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            Log.d(TAG, "onDismiss()");
//                            response(requestCode, false);
//                        }
//                    });
//                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                    response(requestCode, true);
                } else {
                    response(requestCode, false);
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                    builder.setTitle("Functionality limited");
//                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
//                    builder.setPositiveButton(android.R.string.ok, null);
//                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            Log.d(TAG, "onDismiss()");
//                            response(requestCode, false);
//                        }
//                    });
//                    builder.show();
                }
                return;
            }
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "fine location permission granted");
//                    LocationStore.inst().reqLocation();
                    response(requestCode, true);
                } else {
                    response(requestCode, false);
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                    builder.setTitle("Functionality limited");
//                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
//                    builder.setPositiveButton(android.R.string.ok, null);
//                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            Log.d(TAG, "onDismiss()");
//                            response(requestCode, false);
//                        }
//                    });
//                    builder.show();
                }
                return;
            }

            default:
                break;
        }
    }

}
