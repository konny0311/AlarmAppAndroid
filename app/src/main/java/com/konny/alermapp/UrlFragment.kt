package com.konny.alermapp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class UrlFragment : Fragment() {

    private lateinit var fragment : Fragment
    private lateinit var parentActivity: AlarmListActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as AlarmListActivity
        fragment = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_url, container, false)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)

        val btClose = view.findViewById<Button>(R.id.btCloseUrlSetting)
        btClose.setOnClickListener{

            // TODO: 入力したurlを残しておきたい
            val addedUrl = etUrl.text
            val editor = parentActivity.pref.edit()
            editor.putString("url", addedUrl.toString())
            editor.apply()
            etUrl.text = addedUrl
            //Fragment終了処理
            val manager = parentActivity.getSupportFragmentManager()
            val transaction = manager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
        }

        return view
    }


}
