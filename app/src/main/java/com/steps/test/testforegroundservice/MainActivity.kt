package com.steps.test.testforegroundservice

import android.app.AlarmManager
import android.app.ForegroundServiceStartNotAllowedException
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "testforeground"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_click).setOnClickListener { startForegroundService() }
        findViewById<Button>(R.id.btn_click_fix).setOnClickListener { startForegroundService(true) }
        if(!checkIsNotifyEnabled(this)){
            openNotify()
        }
    }

    fun checkIsNotifyEnabled(context: Context?): Boolean {
        val managerCompat = NotificationManagerCompat.from(context!!)
        return managerCompat.areNotificationsEnabled()
    }

    fun hasNotifyPerm(context: Context?): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O || checkIsNotifyEnabled(context)
    }

    fun openNotify(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent()
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
            startActivity(intent)
        }
    }

    private fun startForegroundService(fix: Boolean = false) {
        moveTaskToBack(false)
        Handler(mainLooper).postDelayed({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (fix) {
                    try {
                        val intent = Intent(this, ForegroundService::class.java)
                        startForegroundService(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if(e is ForegroundServiceStartNotAllowedException){
                            Log.e(TAG, "模拟后台启动服务出错 ForegroundServiceStartNotAllowedException出错误: ")
                            setAlarm()
//                                setJobSchedule()
                        }
                    }
                } else {
                    val intent = Intent(this, ForegroundService::class.java)
                    startForegroundService(intent)
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(
                        this,
                        ForegroundService::class.java
                ) // Build the intent for the service
                startForegroundService(intent)
            }
        }, 8000)
    }

    /**
     * job不能在后台拉起前台服务，依然会报此错误
     */
    private fun setJobSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val mServiceComponent = ComponentName(this, CheckJobService::class.java)
            val jobInfo = JobInfo.Builder(
                    1,
                    mServiceComponent
            )
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setMinimumLatency(3000)
                .setOverrideDeadline(3000 + 30000)
                .build()
            try {
                val service = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
                service.schedule(jobInfo)
                Log.d(TAG, "setJobSchedule: ")
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.d(TAG, "setJobSchedule Exception: ${e.message}")
            }
        }
    }

    /**
     * alarm可在后台拉起前台服务
     */
    private fun setAlarm() {
        val intent: Intent = getAlarmJobIntent()
        val sender = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)
        val systemTime = System.currentTimeMillis()
        val selectTime: Long = systemTime + 1000
        // 进行闹铃注册
        val manager = this.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        try {
            manager.cancel(sender)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, selectTime, sender)
            } else {
                manager[AlarmManager.RTC_WAKEUP, selectTime] = sender
            }
            Log.e(TAG, "setAlarm: 设置了精确闹钟，1s后响应的")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAlarmJobIntent(): Intent {
        val intent = Intent()
        // 改为显式广播
        intent.setPackage(BuildConfig.APPLICATION_ID)
        intent.action = "ACTION_BROADCAST_ALARM_JOB"
        val componentName: ComponentName = ComponentName(
                BuildConfig.APPLICATION_ID,
                PedometerReceiver::class.java.getName()
        )
        intent.component = componentName
        return intent
    }
}