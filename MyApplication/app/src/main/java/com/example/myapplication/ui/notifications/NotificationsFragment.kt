package com.example.myapplication.ui.notifications

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyAdapter_calendar
import com.example.myapplication.MyDBHelper
import com.example.myapplication.databinding.FragmentNotificationsBinding
import java.text.SimpleDateFormat
import java.util.*
// TODO:Notifications畫面(待辦事項依日期分類)，結合日曆將對應日期的事項放入適配器[MyAdapter_calendar]處理
class NotificationsFragment : Fragment() {
    //定義
    private lateinit var calendar: CalendarView //日曆
    private lateinit var list: RecyclerView //小清單
    private lateinit var text: TextView
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val mData_title: ArrayList<String> = ArrayList()
    private val mData_thing: ArrayList<String> = ArrayList()
    private val mData_date: ArrayList<String> = ArrayList()
    private val mData_cycle: ArrayList<String> = ArrayList()
    private val mData_time: ArrayList<String> = ArrayList()
    private val mData_id: ArrayList<String> = ArrayList()
    private var adapter: MyAdapter_calendar? = null

    @SuppressLint("Range", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        //初始化
        calendar = binding.calendar
        list = binding.list
        text = binding.textView12

        //設定資料庫
        val DB_TABLE = "mylist"
        val MyDB: SQLiteDatabase
        // 取得上面指定的檔名資料庫，如果該檔名不存在就會自動建立一個資料庫檔案
        MyDB = MyDBHelper(context, "mylist.db", null, 1).writableDatabase

        // 日曆觸發動作
        fun onDateSelected(year: Int, month: Int, dayOfMonth: Int) {
            val c = MyDB.query(
                true, DB_TABLE, null, null, null,
                null, null, "datetime(date) ASC, time ASC", null)

            //清空裝取資料的陣列
            mData_title.clear()
            mData_thing.clear()
            mData_date.clear()
            mData_cycle.clear()
            mData_time.clear()
            mData_id.clear()
            while (c.moveToNext()) {
                val id = c.getString(c.getColumnIndex("id")) //id
                val title = c.getString(c.getColumnIndex("title")) //標題
                val thing = c.getString(c.getColumnIndex("thing")) //內容
                val dateStr = c.getString(c.getColumnIndex("date")) //截止日期
                val timeStr = c.getString(c.getColumnIndex("time")) //時間
                val cycle = c.getString(c.getColumnIndex("cycle")) //週期
                val dateArray = cycle.split("\n").toTypedArray() //將日期分割放入陣列
                for (s in dateArray){
                    val selectedDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).parse("$year/${month + 1}/$dayOfMonth") // 字串轉為日期
                    val formattedDate =
                        selectedDate?.let {
                            SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.getDefault()).format(
                                it
                            )
                        } // 格式化日期
                    if (formattedDate == s) {
                        //存裝到對應陣列
                        mData_title.add(title)
                        mData_thing.add(thing)
                        mData_date.add(dateStr)
                        mData_cycle.add(cycle)
                        mData_time.add(timeStr)
                        mData_id.add(id)
                    }
                }
            }
            //-----參考資料：https://ithelp.ithome.com.tw/articles/10187869-----//
            // 設置RecyclerView為列表型態
            list.setLayoutManager(LinearLayoutManager(context))
            // 設置格線
            list.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
            // 將資料交給adapter
            adapter = MyAdapter_calendar(mData_title, mData_time, mData_thing, mData_date, mData_id, mData_cycle)
            // 設置adapter給recycler_view
            list.adapter = adapter as RecyclerView.Adapter<*>
            //-----參考資料：https://ithelp.ithome.com.tw/articles/10187869-----//

            //關閉查詢
            c.close()
        }

        // 日曆監聽
        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            if (list.itemDecorationCount != 0)
            {
                list.removeItemDecoration(list.getItemDecorationAt(list.itemDecorationCount-1)) //先清除所有Item
            }
            val monthStr = String.format("%02d", month + 1) //將月份補成兩位數
            val dayOfMonthStr = String.format("%02d", dayOfMonth) //將日期補成兩位數
            text.text = "$year 年 $monthStr 月 $dayOfMonthStr 日"
            onDateSelected(year, month, dayOfMonth) //再新增這一次的Item
        }

        // 今天日期
        val cc = Calendar.getInstance()
        val year = cc.get(Calendar.YEAR)
        val month = cc.get(Calendar.MONTH)
        val dayOfMonth = cc.get(Calendar.DAY_OF_MONTH)
        // 設定今天日期
        calendar.date = cc.timeInMillis
        val monthStr = String.format("%02d", month + 1) //將月份補成兩位數
        val dayOfMonthStr = String.format("%02d", dayOfMonth) //將日期補成兩位數
        text.text = "$year 年 $monthStr 月 $dayOfMonthStr 日"
        onDateSelected(year, month, dayOfMonth)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}