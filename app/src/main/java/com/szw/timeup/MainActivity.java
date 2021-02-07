package com.szw.timeup;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.szw.timeup.helper.SystemHelper;

public class MainActivity extends AppCompatActivity {

    private boolean firstStart = true;

    private CheckService checkService;
    private ConnectionService connectionService;
    private boolean isTiming = false;

    public boolean isTiming() {
        return isTiming;
    }

    public void setTiming(boolean timing) {
        isTiming = timing;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // 申请加入电池白名单
        SystemHelper systemHelper = new SystemHelper();
        if (!systemHelper.isIgnoringBatteryOptimizations(MainActivity.this)) {
            systemHelper.requestIgnoreBatteryOptimizations(MainActivity.this);
        }
        // 申请UsageStatsManager使用权限
        if (systemHelper.needPermissionForUsageStats(MainActivity.this)) {
            systemHelper.requestPermissionForUsageStats(MainActivity.this);
        }

        if (!Settings.canDrawOverlays(this)) {
            //若未授权则请求权限
            systemHelper.requestOverlayPermission(MainActivity.this);
        }

        connectionService = new ConnectionService();
        bindService(new Intent(this, CheckService.class), connectionService, BIND_AUTO_CREATE);
    }

//    @SuppressLint("MissingSuperCall")
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        //No call for super(). Bug on API Level > 11.
//    }

    public void changeFragment() {
        if (firstStart) {
            setContentView(R.layout.content_main2);
            firstStart = false;
        }
        FragmentTiming fragmentTiming = new FragmentTiming();
//        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, fragmentTiming).commitAllowingStateLoss(); // fragment重叠
        getSupportFragmentManager().beginTransaction().hide(getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment))
                .add(fragmentTiming, FragmentTiming.class.getName()).show(fragmentTiming).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();//要去掉这句，否则会结束当前Activity，无法起到屏蔽的作用
        //处理自己的逻辑
        Log.i("MainActivity", " onBackPressed");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.i("MainActivity", " onKeyDown");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class ConnectionService implements ServiceConnection {
        @SuppressLint("LongLogTag")
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            checkService = ((CheckService.CheckBuild)service).getMyService(); //获取Myservice对象

            Log.i("##### MainActivity #####", "set setMainActivity");
            /**
             * 直接把当前对象传给service，这样service就可以随心所欲的调用本activity的各种可用方法
             * 把当前对象传递给checkService
             */
            checkService.setMainActivity(MainActivity.this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}