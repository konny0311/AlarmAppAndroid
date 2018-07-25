package com.konny.alarmapp

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteStatement
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

class AlarmListActivity : AppCompatActivity() {

    lateinit var alermTime: ArrayList<String>
    var dbSync = true
    lateinit var refresher: SwipeRefreshLayout
    val activity = this
    var url = "" //setUrl fragmentでurl設定、それを受け継ぐ
    lateinit var pref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerm_list)

        //アプリ終了してもkey-valueを保持してる。
        pref = getSharedPreferences("url", Context.MODE_PRIVATE)
        val editor = pref.edit()
//        editor.putString("url", "google.com")
//        editor.apply()

        alermTime = getAlarmTime()
        var adapter = AlarmAdapter(this, R.layout.lv_alerm, alermTime)
        val lvAlerm = findViewById<ListView>(R.id.lvAlerm)
        lvAlerm.adapter = adapter
        lvAlerm.setOnItemLongClickListener(ListItemClickListener(this))

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener{
            val manager = getSupportFragmentManager()
            val transaction = manager.beginTransaction()
            val saf = SetAlarmFragment()
            transaction.add(R.id.alermList, saf)
            transaction.commit()
        }

//       微妙なコードだが、下スワイプで時刻リスト更新される
        refresher = findViewById(R.id.swiperefresh)
        refresher.setOnRefreshListener(
                object :SwipeRefreshLayout.OnRefreshListener {
                    override fun onRefresh() {
                        Log.i("refresher","作動中")
                        dbSync = true
                        alermTime = getAlarmTime()
                        var adapter = AlarmAdapter(activity, R.layout.lv_alerm, alermTime)
                        val lvAlerm = findViewById<ListView>(R.id.lvAlerm)
                        lvAlerm.adapter = adapter
                        lvAlerm.setOnItemLongClickListener(ListItemClickListener(activity))
                        refresher.isRefreshing = false
                    }
                }
        )

//        アラーム設定機能
//        val time:Long = 24153015 /* calendar使う */
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, AlarmListActivity::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
//        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(time, null), pendingIntent)
    }

    //画面右上メニューボタン表示
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options_menu_list, menu)
//      オプションメニューのをinflateさせて、親クラスの同メソッド呼ぶ(trueを返す)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when(itemId){
            R.id.menuListOptionSetURL -> {
//                ホーム画面へ遷移
                val manager = getSupportFragmentManager()
                val transaction = manager.beginTransaction()
                val saf = UrlFragment()
                transaction.add(R.id.alermList, saf)
                transaction.commit()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private class ListItemClickListener(alarmListActivity: AlarmListActivity) : AdapterView.OnItemLongClickListener {

        val activity = alarmListActivity
//        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//
//            val timeSelected = parent!!.getItemAtPosition(position) as String
////                "12:00"のようなStringが返ってくるので:で分けて、hour, minとする。
//            val hour = timeSelected.substring(0, 2)
//            val min = timeSelected.substring(3, 5)
//            var intent = Intent(activity, AlermActivity::class.java)
//            intent.putExtra("hour", hour)
//            intent.putExtra("min", min)
//            activity.startActivity(intent)
//        }

        /**
         * アラーム時刻長押しでDBより消去
         * TODO: 即時消去されるようにしたい(追加時も)。swiperefreshで実装。(微妙やけど)
         */
        override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {

            Log.i("長押し","されました")
            val timeSelected = parent!!.getItemAtPosition(position) as String
//                "12:00"のようなStringが返ってくるので:で分けて、hour, minとする。
            val helper = DatabaseHelper(activity, "alermtime")
            val db = helper.writableDatabase
            Log.i("ここまで","きました")
            try {
                val stmt: SQLiteStatement = db.compileStatement("delete from alermtime where alermtime = '$timeSelected'") //要確認
                stmt.executeInsert()
                Log.i("delete","成功しました")
            } catch (ex: Exception) {Log.i("sql", ex.message)}
            finally {
                db.close()
                Log.i("db処理","終了しました")
                return true
            }
        }
    }

    /**
     * DBからアラーム時間のリストを取ってくる。
     */
    private fun getAlarmTime(): ArrayList<String> {
        //        fragment終了時にdbと同期させたい(dbSync=trueとなる)
        if (dbSync) {
//        sqliteDBから時間取得
            val helper = DatabaseHelper(this)
            val db = helper.writableDatabase
            alermTime = arrayListOf()
            try {
                val sql = "select alermtime from alermtime"
                var cursor = db.rawQuery(sql, null)
                while (cursor.moveToNext()) {
                    val idx = cursor.getColumnIndex("alermtime")
                    alermTime.add(cursor.getString(idx))
                }
            } catch (ex: Exception) {
                Log.i("sql selectで", "エラーです。${ex.message}")
            } finally {
                dbSync = false
            }
        }
        return alermTime
    }
}