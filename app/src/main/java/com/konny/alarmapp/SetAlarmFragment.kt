package com.konny.alarmapp


import android.database.sqlite.SQLiteStatement
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker

class SetAlarmFragment : Fragment() {

    private lateinit var fragment : Fragment
    private lateinit var parentActivity: AlarmListActivity
    private lateinit var tp: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as AlarmListActivity
        fragment = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_set_alerm, container, false)
        tp = view.findViewById<TimePicker>(R.id.tp)
        tp.setIs24HourView(true)

        val btClose = view.findViewById<Button>(R.id.btClose)
        btClose.setOnClickListener{
            this.saveTime(tp, parentActivity)
            parentActivity.dbSync = true
            val manager = parentActivity.getSupportFragmentManager()
            val transaction = manager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
        }
        return view
    }

//    OKボタン押されたら設定時間をSQLiteDBへ保存
    fun saveTime(selectedTp: TimePicker, parentActivity: AlarmListActivity) {
        val hour: String = if (selectedTp.hour >= 10) selectedTp.hour.toString() else "0" + selectedTp.hour.toString()
        val min: String = if (selectedTp.minute >= 10) selectedTp.minute.toString() else "0" + selectedTp.minute.toString()
        val time = "$hour:$min"
        val helper = DatabaseHelper(parentActivity)
        val db = helper.writableDatabase

        try {
            val stmt: SQLiteStatement = db.compileStatement("insert into alermtime(alermtime) values ('$time')")
            stmt.executeInsert()
        } catch (ex: Exception) {Log.i("sql", ex.message)}
        finally {
            db.close()
        }
    }
}
