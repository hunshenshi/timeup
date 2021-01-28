package com.szw.timeup;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.szw.timeup.helper.AlarmManagerUtils;
import com.szw.timeup.helper.AppUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckService extends Service {
    private static final String TAG = "CheckService";
    private static final String PKG_NAME = "com.szw.timeup";
    private static final String TARGET_AIQIYI_PKG_NAME = "com.qiyi.video";
    private static final String TARGET_YOUKU_PKG_NAME = "com.youku.phone";
    private static final String TARGET_QQLIVE_PKG_NAME = "com.tencent.qqlive";
    private static final String TARGET_BAIDUCLOUD_PKG_NAME = "com.baidu.netdisk";
    private static final String TARGET_TEST_PKG_NAME = "com.google.android.apps.maps";

    private boolean videoAppRunning = false;

    private MainActivity activity;

    public CheckService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return new CheckBuild();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @SuppressLint("LongLogTag")
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
                Date date = new Date();// 获取当前时间
                System.out.println("现在时间：" + sdf.format(date));
                Log.d(TAG, sdf.format(date) + " run: ");
                String currentPkg = AppUtil.getForegroundActivityName(getApplicationContext());
                System.out.println("usageStats " + currentPkg);
                if (currentPkg.equalsIgnoreCase(TARGET_AIQIYI_PKG_NAME) ||
                        currentPkg.equalsIgnoreCase(TARGET_YOUKU_PKG_NAME) ||
                        currentPkg.equalsIgnoreCase(TARGET_BAIDUCLOUD_PKG_NAME) ||
                        currentPkg.equalsIgnoreCase(TARGET_QQLIVE_PKG_NAME) ||
                        currentPkg.equalsIgnoreCase(TARGET_TEST_PKG_NAME)) {
                    if (videoAppRunning) {
                        videoAppRunning = false;
                        activity.setTiming(true);
                        Log.i("CheckService onStartCommand ", "monitor app is foreground.");
//                        toRunningForeground(getApplicationContext());
                        toRunningForeground(getApplicationContext(), PKG_NAME);
//                        toRunningForeground(getApplicationContext());
                    } else {
                        Log.i("CheckService onStartCommand ", "video app is foreground.");
                        videoAppRunning = true;
                    }
                }
            }
        }).start();
        // 这一步具体有什么用？
        AlarmManagerUtils.getInstance(getApplicationContext()).getUpAlarmManagerWorkOnOthers();
        return super.onStartCommand(intent, flags, startId);
    }

    public void toRunningForeground(Context context, String packageNameTarget) {
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
        context.startActivity(intent);

    }

//    public void toRunningForeground(Context context) {
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//
//        /**获得当前运行的task(任务)*/
//        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
//        for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
//            Log.i("timerTask", "timerTask  pid " + taskInfo.id);
//            Log.i("timerTask", "timerTask  processName " + taskInfo.topActivity.getPackageName());
//            Log.i("timerTask", "timerTask  getPackageName " + context.getPackageName());
//
//
//            /**找到本应用的 task，并将它切换到前台*/
////            if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
////                activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
////                break;
////            }
//        }
//    }

    public void setMainActivity(MainActivity activity) {
        this.activity = activity;
    }

    class CheckBuild extends Binder {
        public CheckService getMyService()
        {
            return CheckService.this;
        }
    }
}
