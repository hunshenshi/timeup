package com.szw.timeup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;


/**
 * Created by monkey on 2021/1/20.
 */

public class ActivityLifeCycle implements Application.ActivityLifecycleCallbacks {
    //前台Activity数量
    private int foregroundActivityCount = 0;
    // Activity是否在修改配置，
    private boolean isChangingConfigActivity = false;
    // 应用即将切换到前台
    private boolean willSwitchToForeground = false;
    // 应用在前台
    private boolean isForegroundNow = true;

    private MainActivity mainActivity;

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.i("ActivityLifeCycle onActivityCreated ", getActivityName(activity));
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityStarted(Activity activity) {
        Log.i("#### ActivityLifeCycle onActivityStarted ####", activity.getClass().getSimpleName() + " " + foregroundActivityCount);
        //前台没有Activity，说明新启动或者将从从后台恢复
        if (foregroundActivityCount == 0 || isForegroundNow) {
            willSwitchToForeground = true;
        }
        if (isChangingConfigActivity) {
            isChangingConfigActivity = false;
            return;
        }
        foregroundActivityCount += 1;

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResumed(Activity activity) {
        Log.i("#### ActivityLifeCycle onActivityResumed ####", getActivityName(activity));
        if (willSwitchToForeground && isInteractive(activity)) {
            // 应用从后台跳转到前台，而非第一次启动，并且需要计时才进行fragment切换
            mainActivity = (MainActivity)activity;
            if (!isForegroundNow && mainActivity.isTiming()) {
                Log.i("#### ActivityLifeCycle ####","switch to foreground action ");
                mainActivity.changeFragment();
                mainActivity.setTiming(false);
            }
            isForegroundNow = true;
            Log.i("#### ActivityLifeCycle ####","switch to foreground" );
        }

        if (isForegroundNow) {
            willSwitchToForeground = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i("ActivityLifeCycle ","onActivityPaused" + getActivityName(activity));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i("ActivityLifeCycle ","onActivityStopped" + getActivityName(activity));
        //如果这个Activity实在修改配置，如旋转等，则不保存时间直接返回
        if (activity.isChangingConfigurations()) {
            isChangingConfigActivity = true;
            return;
        }
        //该Activity要进入后台，前台Activity数量-1。
        foregroundActivityCount -= 1;
        //当前已经是最后的一个Activity，代表此时应用退出了
        if (foregroundActivityCount == 0) {
            isForegroundNow = false;
            Log.i("ActivityLifeCycle ","switch to background");
//            if (mainActivity.isMustfore()) {
//                Log.i("ActivityLifeCycle ","It is timing now, switch to foreground");
//                CheckService checkService = new CheckService();
//                checkService.toRunningForeground(activity, "com.szw.timeup");
//            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Log.i("ActivityLifeCycle ", "onActivitySaveInstanceState" + getActivityName(activity));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i("ActivityLifeCycle ","onActivityDestroyed");
    }

    private boolean isInteractive(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    private String getActivityName(final Activity activity) {
        return activity.getClass().getCanonicalName();
    }
}