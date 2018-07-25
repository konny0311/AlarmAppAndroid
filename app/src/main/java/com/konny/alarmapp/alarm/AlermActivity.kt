package com.konny.alarmapp.alarm

import android.media.RingtoneManager
import android.media.RingtoneManager.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import com.konny.alarmapp.R
import java.util.*

class AlermActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerm)

        //AlarmSettingクラスからうまくintent移せない
        val extras = intent.extras
        val url = extras.get("url")
        val time = extras.get("time")
        val cal = Calendar.getInstance()
        val tvTime = findViewById<TextView>(R.id.tvTime)
        val wvAlarm = findViewById<WebView>(R.id.wvAlarm)
        val swAlarm = findViewById<Switch>(R.id.swAlarm)
        val layout = findViewById<LinearLayout>(R.id.layoutAlarm)
        val activity = this

        //現在時刻を表示
        tvTime.text = "$time"
        wvAlarm.loadUrl("$url")
        wvAlarm.setWebViewClient(object:WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest):Boolean {
                return false
            }
        })

        swAlarm.switchMinWidth = 40

        val notification = RingtoneManager.getDefaultUri(TYPE_ALARM)
        val ringTone = RingtoneManager.getRingtone(this, notification)
        ringTone.play()

        val ring = ringTone.isPlaying
        if (ring) {Log.i("アラーム:", "鳴ってます")}
        else {Log.i("アラーム:", "鳴ってない")}

        swAlarm.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {

            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                if (isChecked)
                    ringTone.stop()
                    val paramsWV = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0.8f)
                    wvAlarm.layoutParams = paramsWV
//                    val paramsSW = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 0f)
//                    swAlarm.layoutParams = paramsSW

//                    val ll = LinearLayout(activity)
                    layout.removeView(swAlarm)
                    layout.removeView(tvTime)
                    val stopRing = ringTone.isPlaying
                    if (stopRing) {
                        Log.i("アラーム:", "鳴ってます")
                    } else {
                        Log.i("アラーム:", "停止")
                    }

            }
        })
    }
}
