package com.konny.alermapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.konny.alermapp.AlarmSetting

class AlarmAdapter(context: Context, itemLayoutId: Int, alermTime: ArrayList<String>) : ArrayAdapter<String>(context, itemLayoutId, alermTime) {

    private var inflater: LayoutInflater
    private var layoutID: Int
    private var alermTime: ArrayList<String>
    private var alarmSet = false

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutID = itemLayoutId
        this.alermTime = alermTime
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        if (convertView == null) {
            val newView = inflater.inflate(layoutID, null)
            val tvAlerm: TextView = newView.findViewById(R.id.tvAlerm)
            val switch = newView.findViewById<CompoundButton>(R.id.switchTime)
            val alarmSetting = AlarmSetting(alermTime[position], context)

            switch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {

                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                    //検証のためにアラーム設定後5秒で発火する
                    if (isChecked) {
                        Log.i("ボタン状態:", "オン")
                        alarmSetting.start()
                        alarmSet = true
                    } else {
                        if (alarmSet) alarmSetting.cancel()
                    }
                }

            })
            tvAlerm.text = alermTime[position]
            return newView
        }
        else {
            val tvAlerm: TextView = convertView.findViewById(R.id.tvAlerm)
            tvAlerm.text = alermTime[position]
            return convertView
        }
    }

    override fun getCount(): Int {
        return alermTime.size
    }

    override fun getItem(position: Int): String{
        return alermTime[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}