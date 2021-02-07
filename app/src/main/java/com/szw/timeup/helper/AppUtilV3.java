package com.szw.timeup.helper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeMap;

public class AppUtilV3 {
    private static final long END_TIME = System.currentTimeMillis();
//    private static final long TIME_INTERVAL = 7 * 24 * 60 * 60 * 1000L;
    // 近5个小时
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
            System.out.println("=========================getForegroundUsageEvent====================");
            UsageEvents.Event event = getForegroundUsageEvent(context, START_TIME, END_TIME);
            if (event != null) {
                currentClassName = event.getPackageName();
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
//    public static long getTotleForegroundTime(Context context) {
//        UsageEvents.Event event = getCurrentUsageEvent(context, START_TIME, END_TIME);
//        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.LOLLIPOP) {
//            return event != null ? event.getTotalTimeInForeground() : 0;
//        }
//        return 0;
//    }
    /**
     * 获取记录前台应用的UsageEvents.Event对象
     */
    private static UsageEvents.Event getForegroundUsageEvent(Context context, long startTime, long endTime) {
        UsageEvents.Event lastEvent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageEvents usageEvents = getUsageEvents(context, startTime, endTime);
            if (usageEvents == null) {
                return null;
            }


            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("############### AppUtil3 getForegroundUsageEvent start list ##########").append("\n");

            System.out.println("############### AppUtil3 getForegroundUsageEvent start list ##########");
            UsageEvents.Event event = null;
            TreeMap<Long, UsageEvents.Event> treeMap = new TreeMap<Long, UsageEvents.Event>();
            while (usageEvents.hasNextEvent()) {
                event = new UsageEvents.Event();
                if (usageEvents.getNextEvent(event)) {
                    if (event != null) {
                        treeMap.put(event.getTimeStamp(), event);
                    } else {
                        Log.i("AppUtil3", "event is null");
                    }
                }
            }

            if (!treeMap.isEmpty()) {
                NavigableSet<Long> set = treeMap.navigableKeySet();
                Iterator<Long> iterator = set.descendingIterator();
                while (iterator.hasNext()) {
                    UsageEvents.Event item = treeMap.get(iterator.next());
                    System.out.println(item.getPackageName() + " " + item.getTimeStamp()
                            + " " + item.getEventType());
                    stringBuilder.append(item.getPackageName() + " " + item.getTimeStamp()
                            + " " + item.getEventType()).append("\n");
                    if (item.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                        if (lastEvent == null) {
                            lastEvent = item;
                        }
//                        break;
                    }
                }
            } else {
                Log.i("AppUtil3", "UsageEvents is null");
            }

            System.out.println("############### AppUtil3 getForegroundUsageEvent start end ##########");
            stringBuilder.append("############### AppUtil3 getForegroundUsageEvent start end ##########").append("\n");
            try {
                Util.writeFile(context, "log.txt", stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lastEvent;
    }

    public static boolean hasForegroundActivity(SparseIntArray mActivities) {
        final int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            if (mActivities.valueAt(i) == UsageEvents.Event.ACTIVITY_RESUMED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取记录当前应用的UsageEvents.Event对象
     */
    public static UsageEvents.Event getCurrentUsageEvent(Context context, long startTime, long endTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageEvents usageEvents = getUsageEvents(context, startTime, endTime);
            if (usageEvents == null) {
                return null;
            }
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.getNextEvent(event)) {
                if (TextUtils.equals(event.getPackageName(), context.getPackageName())) {
                    return event;
                }
            }
        }
        return null;
    }
    /**
     * 通过UsageStatsManager获取List<UsageStats 集合
     */
    @SuppressLint("LongLogTag")
    public static UsageEvents  getUsageEvents(Context context, long startTime, long endTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager manager = (UsageStatsManager) context.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
            UsageEvents usageEvents = manager.queryEvents(startTime, endTime);
            if (usageEvents == null) {// 没有权限，获取不到数据
                Log.i("AppUtilV2 getUsageEventsList ", "no permission for UsageStatsManager");
                return null;
            }
            return usageEvents;
        }
        return null;
    }
}
