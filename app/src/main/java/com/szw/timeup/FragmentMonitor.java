package com.szw.timeup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.szw.timeup.helper.AlarmManagerUtils;

public class FragmentMonitor extends Fragment {

    private AlarmManagerUtils alarmManagerUtils;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Log.i("FragmentMontior ", "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monitor, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i("FragmentMontior ", "%%%%%%%%%%%");
        // 创建定时任务管理器
        alarmManagerUtils = AlarmManagerUtils.getInstance(getActivity().getApplicationContext());

        alarmManagerUtils.createGetUpAlarmManager();
        alarmManagerUtils.getUpAlarmManagerStartWork();

        TextView textView = view.findViewById(R.id.textview_monitor);
        if (alarmManagerUtils.isPower()) {
            textView.setText("上次执行时间为：" + TimeUpApplication.getInstance().getMonitorTime()
                    + "\n正在监控应用使用时长，\n可将此应用切换到后台，但不可退出");
        }
        view.findViewById(R.id.button_monitor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("self Logs::", "开始按钮被点击了 id = " + view.getId() + "线程 = " + Thread.currentThread().getName());

                // 启动定时器
//                if (!alarmManagerUtils.isPower()) {
//                    alarmManagerUtils.createGetUpAlarmManager();
//                }
//                alarmManagerUtils.getUpAlarmManagerStartWork();
//
//                TextView textView = getActivity().findViewById(R.id.textview_monitor);
//                textView.setText("正在监控应用使用时长，\n可将此应用切换到后台，但不可退出");
//                Toast.makeText(getActivity().getApplicationContext(),"监控启动成功",Toast.LENGTH_SHORT).show();

                textView.setText("上次执行时间为：" + TimeUpApplication.getInstance().getMonitorTime()
                        + "\n正在监控应用使用时长，\n可将此应用切换到后台，但不可退出");

//                NavHostFragment.findNavController(FragmentMonitor.this)
//                            .navigate(R.id.action_FragmentMonitor_to_FragmentTiming);
            }
        });
    }

}