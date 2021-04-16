package com.szw.timeup;

import android.accessibilityservice.AccessibilityService;
import android.media.SoundPool;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;

/**
 * Created by monkey on 2021/02/20.
 */

public class MonitorService extends AccessibilityService {
    private static final String TAG = "MonitorService";

    private SoundPool mSoundPool = null;
    private HashMap<Integer, Integer> soundID = new HashMap<Integer, Integer>();

    @Override
    protected void onServiceConnected() {
        // TODO Auto-generated method stub
        super.onServiceConnected();

        mSoundPool = new SoundPool.Builder().setMaxStreams(3).build();
        soundID.put(1, mSoundPool.load(this, R.raw.duang, 1));
        soundID.put(2, mSoundPool.load(this, R.raw.audio, 1));

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();//当前界面的可访问节点信息
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {//界面变化事件
            String packageName = event.getPackageName().toString();
            Log.i(TAG, "packageName:" + packageName + "");
            Log.i(TAG, "source:" + event.getSource() + "");
            Log.i(TAG, "source class:" + event.getClassName().toString() + "");

            // 正在倒计时，则阻止响应app运行，并播放音频
            if (TimeUpApplication.getInstance().isTiming() && TimeUpApplication.isVideoApp(packageName)) {
                mSoundPool.play(soundID.get(2), 30, 30, 0, 2, 1);
                Log.i(TAG, "packageName:" + packageName + " , is running, should be change.");
                TimeUpApplication.getInstance().toRunningForeground(TimeUpApplication.PKG_NAME);
            }

        }

    }

    @Override
    public void onInterrupt() {

    }
}
