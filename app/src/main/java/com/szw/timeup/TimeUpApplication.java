package com.szw.timeup;

import android.app.Application;

public class TimeUpApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActivityLifeCycle lifecycleCallbacks = new ActivityLifeCycle();
        registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }
}
