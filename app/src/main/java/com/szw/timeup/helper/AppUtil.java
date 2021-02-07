package com.szw.timeup.helper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.usage.UsageEvents.Event.ACTIVITY_RESUMED;

public class AppUtil {
    private static final long END_TIME = System.currentTimeMillis();
    // 24小时
//    private static final long TIME_INTERVAL = 24 * 60 * 60 * 1000L;
    // 近1分钟
    private static final long TIME_INTERVAL = 60 * 1000L;
    private static final long START_TIME = END_TIME - TIME_INTERVAL;
    /**
     * 获取栈顶的应用包名
     */
    public static String getForegroundActivityName(Context context) {
        String currentClassName = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            currentClassName = manager.getRunningTasks(1).get(0).topActivity.getPackageName();
        } else {
            System.out.println("=========================getForegroundUsageStats====================");
            UsageStats initStat = getForegroundUsageStats(context, START_TIME, END_TIME);
            if (initStat != null) {
                currentClassName = initStat.getPackageName();
            }
        }
        return currentClassName;
    }
    /**
     * 判断当前应用是否在前台
     */
    public static boolean isForegroundApp(Context context) {
        return TextUtils.equals(getForegroundActivityName(context), context.getPackageName());
    }
    /**
     * 获取时间段内，
     */
    public static long getTotleForegroundTime(Context context) {
        UsageStats usageStats = getCurrentUsageStats(context, START_TIME, END_TIME);
        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.LOLLIPOP) {
            return usageStats != null ? usageStats.getTotalTimeInForeground() : 0;
        }
        return 0;
    }
    /**
     * 获取记录前台应用的UsageStats对象
     */
    private static UsageStats getForegroundUsageStats(Context context, long startTime, long endTime) {
        UsageStats usageStatsResult = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<UsageStats> usageStatses = getUsageStatsList(context, startTime, endTime);
            if (usageStatses == null || usageStatses.isEmpty()) {
                return null;
            }

            StringBuilder stringBuilder = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
            Date date = new Date();// 获取当前时间
            stringBuilder.append(sdf.format(date)).append("\n");
            stringBuilder.append("############### start list ##########").append("\n");
            System.out.println("############### start list ##########");
            for (UsageStats usageStats : usageStatses) {
                // 华为得到前台运行到app com.huawei.android.launcher
//                System.out.println("szw usageStats " + usageStats.getPackageName() + " " + usageStats.getLastTimeUsed());

                // I/System.out: ########## 时间 #######com.qiyi.video 1612093554820  1612093715708
                //    ########## 时间 #######com.youku.phone 0
                //I/System.out: ########## 时间 #######com.tencent.qqlive 1612092565269

                int lastEvent = 1;
                try {
                    Field mLastEventField = UsageStats.class.getField("mLastEvent");
//                    Field mActivitiesField = usageStats.getClass().getDeclaredField("mActivities");
                    mLastEventField.setAccessible(true);
//                    mActivitiesField.setAccessible(true);
                    lastEvent = mLastEventField.getInt(usageStats);
//                    System.out.println(hasForegroundActivity((SparseIntArray)mActivitiesField.get(usageStats)));
//                    Method method = null;
//                    try {
//                        method = UsageStats.class.getMethod("hasForegroundActivity");
//                        method.setAccessible(true);
//                        Object obj = method.invoke(usageStats);
//                        System.out.println("??????????????????????" + obj.toString());
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

//                boolean foreground = false;
//                try {
//                    Field mActivities = UsageStats.class.getField("mActivities");
//                    SparseIntArray sparseIntArray = (SparseIntArray) mActivities.get(usageStats);
//                    foreground = hasForegroundActivity(sparseIntArray);
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }

                System.out.println(usageStats.getPackageName() + " " + usageStats.getLastTimeUsed()
                        + " " + lastEvent);
                stringBuilder.append(usageStats.getPackageName() + " " + usageStats.getLastTimeUsed()
                        + " " + lastEvent).append("\n");
                if (usageStatsResult != null) {
                    System.out.println(usageStatsResult.getLastTimeUsed() + " " + usageStats.getLastTimeUsed() + (usageStatsResult.getLastTimeUsed() < usageStats.getLastTimeUsed()));
                    stringBuilder.append(usageStatsResult.getLastTimeUsed() + " " + usageStats.getLastTimeUsed() + (usageStatsResult.getLastTimeUsed() < usageStats.getLastTimeUsed())).append("\n");
                }

                // 是否需要把com.huawei.android.launcher去掉
                if (usageStatsResult == null || usageStatsResult.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                    System.out.println(usageStats.getPackageName() + " " + usageStats.getLastTimeUsed()
                            + " " + lastEvent + "===========");
                    stringBuilder.append(usageStats.getPackageName() + " " + usageStats.getLastTimeUsed()
                            + " " + lastEvent + "===========").append("\n");
                    usageStatsResult = usageStats;
                }
            }

            System.out.println("############### start end ##########");
            stringBuilder.append("############### start end ##########").append("\n");
            try {
                Util.writeFile(context, "log.txt",stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return usageStatsResult;
    }

    public static boolean hasForegroundActivity(SparseIntArray mActivities) {
        final int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            if (mActivities.valueAt(i) == ACTIVITY_RESUMED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取记录当前应用的UsageStats对象
     */
    public static UsageStats getCurrentUsageStats(Context context, long startTime, long endTime) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<UsageStats>  usageStatses = getUsageStatsList(context, startTime, endTime);
            if (usageStatses == null || usageStatses.isEmpty()) {
                return null;
            }
            for (UsageStats usageStats : usageStatses) {
                if (TextUtils.equals(usageStats.getPackageName(), context.getPackageName())) {
                    return usageStats;
                }
            }
        }
        return null;
    }
    /**
     * 通过UsageStatsManager获取List<UsageStats 集合
     */
    @SuppressLint("LongLogTag")
    public static List<UsageStats>  getUsageStatsList(Context context, long startTime, long endTime) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager manager = (UsageStatsManager) context.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
            //UsageStatsManager.INTERVAL_WEEKLY，UsageStatsManager的参数定义了5个，具体查阅源码
            List<UsageStats>  usageStatses = manager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
            if (usageStatses == null || usageStatses.size() == 0) {// 没有权限，获取不到数据
//                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.getApplicationContext().startActivity(intent);
                Log.i("AppUtil getUsageStatsList ", "no permission for UsageStatsManager");
                return null;
            }
            return usageStatses;
        }
        return null;
    }
}
