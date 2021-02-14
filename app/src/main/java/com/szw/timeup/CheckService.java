package com.szw.timeup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
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
import com.szw.timeup.helper.AppUtilV2;
import com.szw.timeup.helper.AppUtilV3;
import com.szw.timeup.helper.Util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
//        new Thread(new Runnable() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void run() {
////                SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
////                sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
////                Date date = new Date();// 获取当前时间
////                System.out.println("现在时间：" + sdf.format(date));
////                Log.d(TAG, sdf.format(date) + " run: ");
//                String currentPkg = AppUtil.getForegroundActivityName(getApplicationContext());
////                String currentPkg2 = AppUtilV2.getForegroundActivityName(getApplicationContext());
////                String currentPkg3 = AppUtilV3.getForegroundActivityName(getApplicationContext());
////                System.out.println("AppUtil usageStats " + currentPkg);
////                System.out.println("AppUtil2 usageStats " + currentPkg2);
////                System.out.println("AppUtil3 usageStats " + currentPkg3);
//                try {
//                    Util.writeFile(getApplicationContext(), "log.txt", "AppUtil usageStats " + currentPkg + "\n");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                System.out.println("AppUtilV2 usageEvent " + currentPkg2);
//
//                // 后台app自动切换到前台
//                if (currentPkg.equalsIgnoreCase(TARGET_AIQIYI_PKG_NAME) ||
//                        currentPkg.equalsIgnoreCase(TARGET_YOUKU_PKG_NAME) ||
//                        currentPkg.equalsIgnoreCase(TARGET_BAIDUCLOUD_PKG_NAME) ||
//                        currentPkg.equalsIgnoreCase(TARGET_QQLIVE_PKG_NAME) ||
//                        currentPkg.equalsIgnoreCase(TARGET_TEST_PKG_NAME)) {
//                    if (activity.isMustfore()) {
//                        Log.i("CheckService onStartCommand ", "monitor app is background, need move foreground.");
//                        try {
//                            Util.writeFile(getApplicationContext(), "log.txt", "monitor app is background, need move foreground.\n");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        toRunningForeground(PKG_NAME);
//                    } else if (videoAppRunning) {
//                        videoAppRunning = false;
//                        activity.setTiming(true);
//                        activity.setMustfore(true);
//                        Log.i("CheckService onStartCommand ", "monitor app is foreground.");
//                        try {
//                            Util.writeFile(getApplicationContext(), "log.txt", "monitor app is foreground.\n");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
////                        toRunningForeground(getApplicationContext());
//                        toRunningForeground(PKG_NAME);
////                        toRunningForeground(getApplicationContext());
//                    } else {
//                        Log.i("CheckService onStartCommand ", "video app is foreground.");
//                        try {
//                            Util.writeFile(getApplicationContext(), "log.txt", "video app is foreground.\n");
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        videoAppRunning = true;
//                    }
//                }
//            }
//        }).start();

        String currentPkg = AppUtil.getForegroundActivityName(getApplicationContext());
        try {
            Util.writeFile(getApplicationContext(), "log.txt", "AppUtil usageStats " + currentPkg + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 后台app自动切换到前台
        if (currentPkg.equalsIgnoreCase(TARGET_AIQIYI_PKG_NAME) ||
                currentPkg.equalsIgnoreCase(TARGET_YOUKU_PKG_NAME) ||
                currentPkg.equalsIgnoreCase(TARGET_BAIDUCLOUD_PKG_NAME) ||
                currentPkg.equalsIgnoreCase(TARGET_QQLIVE_PKG_NAME) ||
                currentPkg.equalsIgnoreCase(TARGET_TEST_PKG_NAME)) {
            if (activity.isMustfore()) {
                Log.i(TAG, "monitor app is background, need move foreground.");
                try {
                    Util.writeFile(getApplicationContext(), "log.txt", "monitor app is background, need move foreground.\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toRunningForeground(PKG_NAME);
            } else if (videoAppRunning) {
                videoAppRunning = false;
                activity.setTiming(true);
                activity.setMustfore(true);
                Log.i(TAG, "monitor app is foreground.");
                try {
                    Util.writeFile(getApplicationContext(), "log.txt", "monitor app is foreground.\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                toRunningForeground(PKG_NAME);
            } else {
                Log.i(TAG, "video app is foreground.");
                try {
                    Util.writeFile(getApplicationContext(), "log.txt", "video app is foreground.\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                videoAppRunning = true;
            }
        }

        if (activity.isMustfore()) {
            Log.i(TAG, "getUpAlarmManagerWorkOnBack.");
            try {
                Util.writeFile(getApplicationContext(), "log.txt", "getUpAlarmManagerWorkOnBack.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            AlarmManagerUtils.getInstance(getApplicationContext()).getUpAlarmManagerWorkOnBack();
        } else {
            Log.i(TAG, "getUpAlarmManagerWorkOnOthers.");
            try {
                Util.writeFile(getApplicationContext(), "log.txt", "getUpAlarmManagerWorkOnOthers.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            AlarmManagerUtils.getInstance(getApplicationContext()).getUpAlarmManagerWorkOnOthers();
        }
        return super.onStartCommand(intent, flags, startId);
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
        activity.startActivity(intent);

    }

    public void toRunningForeground(Activity activity, String packageNameTarget) {
        PackageManager packageManager = activity.getPackageManager();
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
        activity.startActivity(intent);

    }

    public void toRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        /**获得当前运行的task(任务)*/
        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
            Log.i("timerTask", "timerTask  pid " + taskInfo.id);
            Log.i("timerTask", "timerTask  processName " + taskInfo.topActivity.getPackageName());
            Log.i("timerTask", "timerTask  getPackageName " + context.getPackageName());


            /**找到本应用的 task，并将它切换到前台*/
            if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                // 启动Activity
//                Intent intent = new Intent(context, Class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                context.startActivity(intent);
                break;
            }
        }
    }

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
