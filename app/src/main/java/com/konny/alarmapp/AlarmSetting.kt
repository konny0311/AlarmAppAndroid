package com.konny.alarmapp

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import com.konny.alarmapp.alarm.AlermActivity
import java.util.*
import java.util.Calendar.*

/**
 *
 */
class AlarmSetting(time: String, context: Context) {

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    //AlermReceiver経由せずにAlermActivityに飛んでる
    val alarmListActivity = context as AlarmListActivity
    val intent = Intent(alarmListActivity, AlermActivity::class.java)
    lateinit var pendingIntent: PendingIntent
    val calendar = Calendar.getInstance()
    val time = time

    //検証用にアラーム設定後5秒で発火する設定
    init {
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(SECOND, 1)
    }

    fun start() {
        Thread(object : Runnable {
            public override fun run() {
                Log.i("別スレッド:", "起動しました")
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
                //alarmlistactivityのonCreateで入れた値が入ってる
                val url = alarmListActivity.pref.getString("url", "aaa")
                intent.putExtra("url", url)
                intent.putExtra("time", time)
                pendingIntent = PendingIntent.getActivity(alarmListActivity, 0, intent, FLAG_UPDATE_CURRENT)

//                第一引数をELAPSED_REALTIME -> RTC_WAKEUPにするとアラーム動作する。
                alarmManager.setExact(RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        }).start()
    }

    fun cancel() {
        alarmManager.cancel(pendingIntent)
    }
}