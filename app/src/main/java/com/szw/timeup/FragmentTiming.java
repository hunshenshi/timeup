package com.szw.timeup;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FragmentTiming extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timing, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i("FragmentTiming ", "start view");
        getCountDownTime();

//        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity().getApplicationContext(),"FragmentTiming...", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void getCountDownTime() {

        Log.i("FragmentTiming ", "start timing");
        int timeStemp = 30 * 60 * 1000; // 30分钟
//        int timeStemp = 2*60 * 1000; // 30秒
        CountDownTimer timer = new CountDownTimer(timeStemp, 1000) {
            @Override
            public void onTick(long l) {

                long day = l / (1000 * 24 * 60 * 60); //单位天
                long hour = (l - day * (1000 * 24 * 60 * 60)) / (1000 * 60 * 60); //单位时
                long minute = (l - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60)) / (1000 * 60); //单位分
                long second = (l - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;//单位秒

                TextView textView = getActivity().findViewById(R.id.textview_second);
                textView.setText(hour + "小时" + minute + "分钟" + second + "秒");

                TimeUpApplication.getInstance().setTiming(true);

            }

            @Override
            public void onFinish() {
                //倒计时为0时执行此方法
                Toast.makeText(getActivity().getApplicationContext(),"time up...", Toast.LENGTH_SHORT).show();
//                NavHostFragment.findNavController(FragmentTiming.this)
//                        .navigate(R.id.action_FragmentTiming_to_FragmentMonitor); // 空白页面
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .hide(getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_main2_fragment))
//                        .show(getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment))
//                        .commitAllowingStateLoss(); // 空白页面
//                ((MainActivity)getActivity()).setMustfore(false);
                TimeUpApplication.getInstance().setTiming(false);
                Log.i("time ", " finish");
//                FragmentMonitor fragmentMonitor = new FragmentMonitor();
//                getActivity().getSupportFragmentManager().beginTransaction()
////                        .hide(getActivity().getSupportFragmentManager().findFragmentByTag(FragmentTiming.class.getName()))
//                        .hide(getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_main2_fragment))
//                        .add(fragmentMonitor, FragmentMonitor.class.getName())
//                        .show(fragmentMonitor).commitAllowingStateLoss(); // 空白页面
//                ((MainActivity)getActivity()).changeMainFragment();
            }
        };

        timer.start();
    }

}