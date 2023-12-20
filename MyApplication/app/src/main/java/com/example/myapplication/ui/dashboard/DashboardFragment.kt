package com.example.myapplication.ui.dashboard

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyAdapter
import com.example.myapplication.MyDBHelper
import com.example.myapplication.databinding.FragmentDashboardBinding
import com.example.myapplication.ui.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.*

// TODO: Dashboard畫面(陳列待辦清單的所有事項)，將資料庫的資料送到適配器[MyAdapter]處理
@Suppress("UNREACHABLE_CODE")
class DashboardFragment : Fragment() {
    //定義
    private lateinit var listall: RecyclerView //待辦清單
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val mData_title: ArrayList<String> = ArrayList() //標題陣列(要放入RecyclerView)
    private val mData_thing: ArrayList<String> = ArrayList() //內容陣列(要放入RecyclerView)
    private val mData_date: ArrayList<String> = ArrayList() //日期陣列(要放入RecyclerView)
    private val mData_cycle: ArrayList<String> = ArrayList() //週期陣列(要放入RecyclerView)
    private val mData_time: ArrayList<String> = ArrayList() //時間陣列(要放入RecyclerView)
    private val mData_id: ArrayList<String> = ArrayList() //ID陣列(要放入RecyclerView)
    private val mData_sort: ArrayList<String> = ArrayList() //sort陣列(要放入RecyclerView)
    private var adapter: MyAdapter? = null  //適配器(處理資料並將資料塞入RecyclerView)

    @SuppressLint("Range", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        //初始化
        listall = binding.listall //待辦清單

        //建立資料庫
        val DB_TABLE = "mylist"
        val MyDB: SQLiteDatabase
        val friDbHp = MyDBHelper(context, "mylist.db", null, 1)
        friDbHp.sCreateTableCommand = "CREATE TABLE " + DB_TABLE + "(" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "thing TEXT," +
                "date TEXT," +
                "cycle TEXT," +
                "time TEXT," +
                "sort INTEGER DEFAULT 1)"
        MyDB = friDbHp.writableDatabase
        
        //查詢
        val c = MyDB.query(
            true, DB_TABLE, null, null, null,
            null, null, "sort ASC, date ASC, time ASC", null)
        
        //清空裝取資料的陣列
        mData_title.clear()
        mData_thing.clear()
        mData_date.clear()
        mData_cycle.clear()
        mData_time.clear()
        mData_id.clear()
        mData_sort.clear()
        //每一到下一筆資料
        while (c.moveToNext()) {
            //將資料庫的資料存到變數
            val id = c.getString(c.getColumnIndex("id"))
            val title = c.getString(c.getColumnIndex("title"))
            val thing = c.getString(c.getColumnIndex("thing"))
            val dateStr = c.getString(c.getColumnIndex("date"))
            val timeStr = c.getString(c.getColumnIndex("time"))
            val cycle = c.getString(c.getColumnIndex("cycle"))
            val sort = c.getString(c.getColumnIndex("sort"))
            //並存裝到對應陣列
            mData_title.add(title)
            mData_thing.add(thing)
            mData_date.add(dateStr)
            mData_cycle.add(cycle)
            mData_time.add(timeStr)
            mData_id.add(id)
            mData_sort.add(sort)
        }

        //-----參考資料：https://ithelp.ithome.com.tw/articles/10187869-----//
        // 設置RecyclerView為列表型態
        listall.layoutManager = LinearLayoutManager(context)
        // 設置格線
        listall.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        // 將資料交給adapter
        adapter = MyAdapter(mData_title, mData_thing, mData_date, mData_cycle, mData_time, mData_id, mData_sort)
        adapter!!.notifyDataSetChanged()
        // 設置adapter給recycler_view
        listall.adapter = adapter as RecyclerView.Adapter<*>
        //-----參考資料：https://ithelp.ithome.com.tw/articles/10187869-----//

        // 查詢關閉
        c.close()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

