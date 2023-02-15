package com.steps.test.testforegroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created on 2023/2/14 14:44.
 *
 * @author sunwei
 *
 * <p>description：   </p>
 */
public class PedometerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            action = "";
        }
        Log.e(MainActivity.TAG, "onReceive接收到: "+action );
        if("ACTION_BROADCAST_ALARM_JOB".equals(action)){
            Intent it = new Intent(context, ForegroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(it);
            }else{
                context.startService(it);
            }
            Log.e(MainActivity.TAG, "onReceive: 已执行了启动服务");
        }
    }
}
