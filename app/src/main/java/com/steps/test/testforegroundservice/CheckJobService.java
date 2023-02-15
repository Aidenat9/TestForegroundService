package com.steps.test.testforegroundservice;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


/**
 * Created by raoyongchao on 8/2/16.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CheckJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("testforeground", "getJobId: " + params.getJobId());
        Intent intent = getAlarmJobIntent();
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        // manifest里可以处理显式广播，不再使用隐式广播
        sendBroadcast(intent);
        Log.d("testforeground", "onStartJob: 发送广播");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private Intent getAlarmJobIntent() {
        Intent intent = new Intent();
        // 改为显式广播
        intent.setPackage(BuildConfig.APPLICATION_ID);
        intent.setAction("ACTION_BROADCAST_ALARM_JOB");
        ComponentName componentName = new ComponentName(BuildConfig.APPLICATION_ID, PedometerReceiver.class.getName());
        intent.setComponent(componentName);
        return intent;
    }

}
