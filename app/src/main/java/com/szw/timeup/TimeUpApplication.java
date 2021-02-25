package com.szw.timeup;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class TimeUpApplication extends Application {

    public static final String PKG_NAME = "com.szw.timeup";
    public static final String TARGET_AIQIYI_PKG_NAME = "com.qiyi.video";
    public static final String TARGET_YOUKU_PKG_NAME = "com.youku.phone";
    public static final String TARGET_QQLIVE_PKG_NAME = "com.tencent.qqlive";
    public static final String TARGET_BAIDUCLOUD_PKG_NAME = "com.baidu.netdisk";
    public static final String TARGET_TEST_PKG_NAME = "com.google.android.apps.maps";

    private boolean isTiming = false;
    private boolean changeFrag = false;

    private static TimeUpApplication timeUpApplication = null;
    public static TimeUpApplication getInstance(){
        return timeUpApplication;
    }

    public boolean isChangeFrag() {
        return changeFrag;
    }

    public void setChangeFrag(boolean changeFrag) {
        this.changeFrag = changeFrag;
    }

    public boolean isTiming() {
        return isTiming;
    }

    public void setTiming(boolean timing) {
        isTiming = timing;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityLifeCycle lifecycleCallbacks = new ActivityLifeCycle();
        registerActivityLifecycleCallbacks(lifecycleCallbacks);
        timeUpApplication = this;
    }

    public static boolean isVideoApp(String pkg) {
        Boolean result = false;
        if (pkg.equalsIgnoreCase(TimeUpApplication.TARGET_AIQIYI_PKG_NAME) ||
                pkg.equalsIgnoreCase(TimeUpApplication.TARGET_YOUKU_PKG_NAME) ||
                pkg.equalsIgnoreCase(TimeUpApplication.TARGET_BAIDUCLOUD_PKG_NAME) ||
                pkg.equalsIgnoreCase(TimeUpApplication.TARGET_QQLIVE_PKG_NAME) ||
                pkg.equalsIgnoreCase(TimeUpApplication.TARGET_TEST_PKG_NAME)) {
            result = true;
        }
        return result;
    }

    public void toRunningForeground(String packageNameTarget) {
        PackageManager packageManager = getPackageManager();
        Log.i("szw logs::", "android.intent.action.MAIN： APP=" + packageNameTarget);

        Intent intent = packageManager.getLaunchIntentForPackage(packageNameTarget);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        /**android.intent.action.MAIN：打开另一程序
         */
        intent.setAction("android.intent.action.MAIN");
        /**
         * FLAG_ACTIVITY_SINGLE_TOP:
         * 如果当前栈顶的activity就是要启动的activity,则不会再启动一个新的activity
         */
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("fragment", 2);
        getApplicationContext().startActivity(intent);
    }
}
