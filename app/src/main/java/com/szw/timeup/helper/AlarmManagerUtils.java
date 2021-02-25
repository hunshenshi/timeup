package com.szw.timeup.helper;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.szw.timeup.CheckService;

import java.io.IOException;
import java.util.Calendar;

public class AlarmManagerUtils {

    // 闹钟执行任务的时间间隔
    private static final long TIME_INTERVAL = 15 * 60 * 1000;
//    private static final long TIME_INTERVAL = 15 * 1000;
//        private static final long TIME_INTERVAL_BACK = 60 * 1000;
    private static final long TIME_INTERVAL_BACK = 30 * 1000;
    private Context context;
    public static AlarmManager am;
    public static PendingIntent pendingIntent;

    private Calendar calendar;
    private boolean power = false;

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    //
    private AlarmManagerUtils(Context aContext) {
        this.context = aContext;
    }

    //singleton
    private static AlarmManagerUtils instance = null;

    public static AlarmManagerUtils getInstance(Context aContext) {
        if (instance == null) {
            synchronized (AlarmManagerUtils.class) {
                if (instance == null) {
                    instance = new AlarmManagerUtils(aContext);
                }
            }
        }
        return instance;
    }

    public void createGetUpAlarmManager() {
        power = true;
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CheckService.class);
//        PendingIntent.getBroadcast和PendingIntent.getService区别
        pendingIntent = PendingIntent.getService(context, 0, intent, 0);
    }

    @SuppressLint("NewApi")
    public void getUpAlarmManagerStartWork() {

        calendar = Calendar.getInstance();
        // 10：04开始，何时结束？可以设置不？
        calendar.set(Calendar.HOUR_OF_DAY,9);
        calendar.set(Calendar.MINUTE,04);
        calendar.set(Calendar.SECOND,00);

        //版本适配 System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0及以上
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//                    calendar.getTimeInMillis(), pendingIntent);
            // 是否需要使用WAKEUP？熄屏状态下不需要执行吧？是不是可以省电？
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4及以上
//            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                    pendingIntent);
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(), pendingIntent);
        } else {
//            am.setRepeating(AlarmManager.RTC_WAKEUP,
//                    calendar.getTimeInMillis(), TIME_INTERVAL, pendingIntent);
            am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(), TIME_INTERVAL, pendingIntent);
        }
    }

    @SuppressLint("NewApi")
    public void getUpAlarmManagerWorkOnOthers() {
//        System.out.println("getUpAlarmManagerWorkOnOthers");

        //高版本重复设置闹钟达到低版本中setRepeating相同效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0及以上
//            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis() + TIME_INTERVAL, pendingIntent);
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4及以上
//            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                    + TIME_INTERVAL, pendingIntent);
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        }
    }

    @SuppressLint("NewApi")
    public void getUpAlarmManagerWorkOnBack() {
//        System.out.println("getUpAlarmManagerWorkOnBack");

        //高版本重复设置闹钟达到低版本中setRepeating相同效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0及以上
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + TIME_INTERVAL_BACK, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4及以上
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + TIME_INTERVAL_BACK, pendingIntent);
        }
    }
}