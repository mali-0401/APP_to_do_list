package com.example.myapplication.ui.home

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.example.myapplication.AlarmReceiver
import com.example.myapplication.MainActivity
import com.example.myapplication.MyDBHelper
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

// TODO：Home畫面(新增、編輯待辦事項)，將元件內容儲存到資料庫，並新增通知
class HomeFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    //定義
    private lateinit var btcancel: TextView //取消按鈕
    private lateinit var btadd: TextView //新增按鈕
    private lateinit var texttitle: EditText //標題
    private lateinit var textthing: EditText //內容
    private lateinit var pickdate: TextView //選擇日期
    private lateinit var check1: CheckBox //星期一
    private lateinit var check2: CheckBox //星期二
    private lateinit var check3: CheckBox //星期三
    private lateinit var check4: CheckBox //星期四
    private lateinit var check5: CheckBox //星期五
    private lateinit var check6: CheckBox //星期六
    private lateinit var check7: CheckBox //星期日
    private lateinit var checkno: CheckBox //不開提醒
    private lateinit var checkevery: CheckBox //每天
    private lateinit var checkday: CheckBox //截止日期當天
    private lateinit var picktime: TextView //選擇時間
    private lateinit var cyclebox: TextView //週期顯示框
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    //日期轉換
    val date_format1 = SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.getDefault())

    @SuppressLint("Range", "UnspecifiedImmutableFlag", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //初始化
        btcancel = binding.btcancel //取消按鈕
        btadd = binding.btadd //新增按鈕
        texttitle = binding.texttitle //標題
        textthing = binding.textthing //內容
        pickdate = binding.pickdate //選擇日期
        check1 = binding.check1 //星期一
        check2 = binding.check2 //星期二
        check3 = binding.check3 //星期三
        check4 = binding.check4 //星期四
        check5 = binding.check5 //星期五
        check6 = binding.check6 //星期六
        check7 = binding.check7 //星期日
        checkno = binding.checkno //不開提醒
        checkevery = binding.checkevery //每天
        checkday = binding.checkday //選擇時間
        picktime = binding.picktime //選擇時間
        cyclebox = binding.cycleBox //週期顯示框

        cyclebox.movementMethod = ScrollingMovementMethod.getInstance() //設定可滑動
        val dateboolen = true //是否觸發日期選擇
        var timeboolen = true //是否觸發時間選擇
        val mainActivity = requireActivity() as MainActivity
        pickdate.text = "[ 選擇日期 ]"
        picktime.text = "[ 選擇時間 ]"
        btadd.text = "新增"
        btcancel.text = "取消新增"

        //設定資料庫
        val DB_TABLE = "mylist"
        val MyDB: SQLiteDatabase
        // 取得上面指定的檔名資料庫，如果該檔名不存在就會自動建立一個資料庫檔案
        MyDB = MyDBHelper(context, "mylist.db", null, 1).writableDatabase

        // 若有傳遞的數據(編輯動作)
        if (arguments != null) {
            btadd.text = "修改"
            btcancel.text = "取消修改"
            // 將數據放到對應元件中讓使用者修改
            Log.d("text_id_0607", "進到編輯畫面，id：" + arguments!!.getString("id"))
            texttitle.setText(arguments!!.getString("title"))
            textthing.setText(arguments!!.getString("thing"))
            pickdate.setText(arguments!!.getString("date"))
            picktime.setText(arguments!!.getString("time"))
            val cycle = arguments!!.getString("cycle")
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE", Locale.CHINA)

            if(cycle != "無" && cycle != "")
            {
                val dateStringArray = cycle?.split("\n")
                val stringBuilder = StringBuilder()
                if (!dateStringArray.isNullOrEmpty()) {
                    for (dateString in dateStringArray) {
                        stringBuilder.append(dateString.trim())
                        stringBuilder.append("\n")
                    }
                }
                cyclebox.setText("* 目前提醒日期：\n$stringBuilder")
                if (!dateStringArray.isNullOrEmpty()) {
                    for (dateString in dateStringArray) {
                        if (dateStringArray.size == 1 &&
                            dateStringArray.getOrNull(0).toString().equals(pickdate.text.toString())) {
                            checkday.isChecked = true
                        } else {
                            val date = LocalDate.parse(dateString.trim(), formatter)

                            val dayOfWeek = date.dayOfWeek

                            when (dayOfWeek) {
                                DayOfWeek.MONDAY -> check1.isChecked = true
                                DayOfWeek.TUESDAY -> check2.isChecked = true
                                DayOfWeek.WEDNESDAY -> check3.isChecked = true
                                DayOfWeek.THURSDAY -> check4.isChecked = true
                                DayOfWeek.FRIDAY -> check5.isChecked = true
                                DayOfWeek.SATURDAY -> check6.isChecked = true
                                DayOfWeek.SUNDAY -> check7.isChecked = true
                            }
                        }
                    }
                }
            } else {
                cyclebox.setText("* 目前提醒日期：\n無")
            }
        }
        //計算週期
        fun cycle(today:Calendar, pickDate:Calendar): ArrayList<String> {
            val dateList = ArrayList<String>()
            val dayDiff = (pickDate.timeInMillis - today.timeInMillis) / (24*60*60*1000) + if (pickDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) 0 else 1
            for (i in 0..dayDiff) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = today.timeInMillis + i * (24*60*60*1000) // 計算日期
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 獲取星期
                // 檢查勾選並將符合的日期加入ArrayList
                if (checkno.isChecked)
                {
                    dateList.add("無")
                    break
                }
                else {
                    if (check1.isChecked && dayOfWeek == Calendar.MONDAY) {
                        dateList.add(date_format1.format(calendar.time))
                    }
                    else if (check2.isChecked && dayOfWeek == Calendar.TUESDAY) {
                        dateList.add(date_format1.format(calendar.time))
                    }
                    else if (check3.isChecked && dayOfWeek == Calendar.WEDNESDAY) {
                        dateList.add(date_format1.format(calendar.time))
                    }
                    else if (check4.isChecked && dayOfWeek == Calendar.THURSDAY) {
                        dateList.add(date_format1.format(calendar.time))
                    }
                    else if (check5.isChecked && dayOfWeek == Calendar.FRIDAY) {
                        dateList.add(date_format1.format(calendar.time))
                    }
                    else if (check6.isChecked && dayOfWeek == Calendar.SATURDAY) {
                        dateList.add(date_format1.format(calendar.time))
                    }
                    else if (check7.isChecked && dayOfWeek == Calendar.SUNDAY) {
                        dateList.add(date_format1.format(calendar.time))
                    }
                }
            }
            if(checkday.isChecked && !dateList.contains(date_format1.format(pickDate.time))) {
                dateList.add(date_format1.format(pickDate.time))
            }
            return dateList
        }
        //-----參考資料：https://yayar.medium.com/android-studio-datepicker-timepicker-
        // %E5%BB%BA%E7%AB%8B%E6%97%A5%E6%9C%9F%E9%81%B8%E6%93%87%E5%85%83%E4%BB%B6%E
        // 8%88%87%E6%99%82%E9%96%93%E9%81%B8%E6%93%87%E5%85%83%E4%BB%B6-fc1f37382ff4-----//
        // 選擇日期 - 按下textview後觸發
        pickdate.setOnClickListener {
            if (dateboolen) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)
                val dialog = DatePickerDialog(requireContext(), this@HomeFragment, year, month, day)
                dialog.show()
            }
        }
        //-----參考資料：https://yayar.medium.com/android-studio-datepicker-timepicker-
        // %E5%BB%BA%E7%AB%8B%E6%97%A5%E6%9C%9F%E9%81%B8%E6%93%87%E5%85%83%E4%BB%B6%E
        // 8%88%87%E6%99%82%E9%96%93%E9%81%B8%E6%93%87%E5%85%83%E4%BB%B6-fc1f37382ff4-----//
        // 選擇時間 - 按下textview後觸發
        picktime.setOnClickListener {
            if (timeboolen) {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                val dialog = TimePickerDialog(requireContext(), this@HomeFragment, hour, minute, true)
                dialog.show()
            }
        }

        // 星期一至星期天的監聽，若有其中一個勾選，則"每天"的選項應該取消勾選
        val checkBoxList = listOf(check1, check2, check3, check4, check5, check6, check7)
        checkBoxList.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener(null) // 移除現有的監聽
            checkBox.setOnClickListener {
                checkevery.isChecked = !(checkBoxList.any { !it.isChecked })
                if(pickdate.text.equals("[ 選擇日期 ]") && picktime.text.equals("[ 選擇時間 ]")) {
                    mainActivity.OK_dialog("提示", "請先選擇截止日期及提醒時間！")
                    checkBox.isChecked = false
                } else{
                    val today = Calendar.getInstance() //今天
                    val pickDate = Calendar.getInstance() //截止日期
                    pickDate.time = date_format1.parse(pickdate.text.toString()) as Date
                    cyclebox.text = "* 目前提醒日期：\n"+cycle(today, pickDate).joinToString(separator = "\n")
                }
            }
        }
        // 截止日期當天 - 勾選時觸發判斷
        checkday.setOnClickListener {
            if (!pickdate.text.equals("[ 選擇日期 ]") && !picktime.text.equals("[ 選擇時間 ]")){
                val today = Calendar.getInstance() //今天
                val pickDate = Calendar.getInstance() //截止日期
                pickDate.time = date_format1.parse(pickdate.text.toString()) as Date
                cyclebox.text = "* 目前提醒日期：\n"+cycle(today, pickDate).joinToString(separator = "\n")
            } else{
                mainActivity.OK_dialog("提示", "請先選擇截止日期及提醒時間！")
                checkday.isChecked = false
            }
        }

        // 每天 - 勾選時觸發判斷
        checkevery.setOnCheckedChangeListener { _, isChecked ->
            if (!pickdate.text.equals("[ 選擇日期 ]") && !picktime.text.equals("[ 選擇時間 ]")) {
                // 如果其他天都有被勾選，則取消勾選其他天
                if (isChecked && (checkBoxList.any { !it.isChecked })) {
                    checkBoxList.forEach { it.isChecked = true }
                }
                // 如果其他天都有未被勾選，則勾選其他天
                else if (!isChecked && !(checkBoxList.any { !it.isChecked })) {
                    checkBoxList.forEach { it.isChecked = false }
                }
                val today = Calendar.getInstance() //今天
                val pickDate = Calendar.getInstance() //截止日期
                pickDate.time = date_format1.parse(pickdate.text.toString()) as Date
                cyclebox.text = "* 目前提醒日期：\n" + cycle(today, pickDate).joinToString(separator = "\n")
            } else {
                mainActivity.OK_dialog("提示", "請先選擇截止日期及提醒時間！")
                checkevery.isChecked = false
            }
        }

        // 不開提醒 - 勾選時觸發判斷
        checkno.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                check1.isChecked = false
                check2.isChecked = false
                check3.isChecked = false
                check4.isChecked = false
                check5.isChecked = false
                check6.isChecked = false
                check7.isChecked = false
                checkday.isChecked = false
                checkevery.isChecked = false
                check1.isEnabled = false
                check2.isEnabled = false
                check3.isEnabled = false
                check4.isEnabled = false
                check5.isEnabled = false
                check6.isEnabled = false
                check7.isEnabled = false
                checkevery.isEnabled = false
                checkday.isEnabled = false
                picktime.text = "無"
                cyclebox.text = "* 目前提醒日期：\n無"
                timeboolen = false
            }
            else {
                check1.isEnabled = true
                check2.isEnabled = true
                check3.isEnabled = true
                check4.isEnabled = true
                check5.isEnabled = true
                check6.isEnabled = true
                check7.isEnabled = true
                checkday.isEnabled = true
                checkevery.isEnabled = true
                picktime.text = "[ 選擇時間 ]"
                cyclebox.text = "* 目前提醒日期：\n"
                timeboolen = true
            }
        }

        // 取消按鈕-按下後觸發監聽
        btcancel.setOnClickListener {

            // 跳轉回待辦清單畫面
            findNavController(it).navigate(R.id.navigation_dashboard)
        }

        // 新增按鈕-按下後觸發監聽
        btadd.setOnClickListener { it ->
            val messages = mutableListOf<String>()
            if (texttitle.text.isBlank()) {
                messages.add("記得填寫 標題 欄位！")
            }
            if (pickdate.text == "[ 選擇日期 ]") {
                messages.add("記得填寫 日期 欄位！")
            }
            if (picktime.text == "[ 選擇時間 ]") {
                messages.add("記得填寫 時間 欄位！")
            }
            if (!check1.isChecked && !check2.isChecked && !check3.isChecked && !check4.isChecked
                && !check5.isChecked && !check6.isChecked && !check7.isChecked && !checkevery.isChecked && !checkday.isChecked) {
                if (picktime.text == "[ 選擇時間 ]") {
                    messages.add("未設定提醒周期及時間，將自動設定為\"不開提醒\"")
                    check1.isEnabled = false
                    check2.isEnabled = false
                    check3.isEnabled = false
                    check4.isEnabled = false
                    check5.isEnabled = false
                    check6.isEnabled = false
                    check7.isEnabled = false
                    checkevery.isEnabled = false
                    checkno.isChecked = true
                    picktime.text = "無"
                    timeboolen = false
                }else if (!checkno.isChecked) {
                    messages.add("若選擇了提醒時間，則須設定提醒週期")
                }
            }
            if (messages.isNotEmpty()) {
                mainActivity.OK_dialog("提示", messages.joinToString(separator = "\n"))
            }
            else {
                // 提取日期並放入計算週期函式回傳arraylist
                val today = Calendar.getInstance() //今天
                val pickDate = Calendar.getInstance() //截止日期
                pickDate.time = date_format1.parse(pickdate.text.toString()) as Date
                val cycle = cycle(today, pickDate).joinToString(separator = "\n")

                // 宣告一ontentValues並放入資料
                val newRow = ContentValues()
                newRow.put("title", texttitle.text.toString())
                newRow.put("thing", textthing.text.toString())
                newRow.put("date", pickdate.text.toString())
                newRow.put("cycle", cycle)
                newRow.put("time", picktime.text.toString())
                if (arguments == null) {
                    // 將ContentValues中的資料，放至資料表中
                    val db = MyDB.insert(DB_TABLE, null, newRow)
                    if (db != -1L) {
                        Toast.makeText(context, "成功新增 [${texttitle.text}]！", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "新增失敗！", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    // 宣告一ContentValues
                    val newRow = ContentValues()
                    // 將要更新的欄位放入ContentValues中
                    newRow.put("title", texttitle.text.toString())
                    newRow.put("thing", textthing.text.toString())
                    newRow.put("date", pickdate.text.toString())
                    newRow.put("time", picktime.text.toString())
                    // 將cyclebox的第一行去除並存入資料庫
                    val cycleboxText = cyclebox.text.toString().trim()
                    val lines = cycleboxText.split("\n")
                    val stringBuilder = StringBuilder()
                    for (i in 1 until lines.size) {
                        stringBuilder.append(lines[i].trim())
                        stringBuilder.append("\n")
                    }
                    val result = stringBuilder.toString().trim()
                    newRow.put("cycle", result)
                    // 將ContentValues中的資料，放至資料表中
                    MyDB.update(DB_TABLE, newRow,
                        "id='" + arguments!!.getString("id") + "'", null)
                    Toast.makeText(context, "成功修改 [${texttitle.text}]！", Toast.LENGTH_LONG).show()
                }

                if (picktime.text.toString() != "無")
                {
                    // 取出日期和時間字串
                    val dateString = pickdate.text.toString()
                    val timeString = picktime.text.toString()

                    // 解析日期字串並設定到 calendar
                    val date = SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.getDefault()).parse(dateString)
                    val calendar = Calendar.getInstance()
                    if (date != null) {
                        calendar.time = date
                    }

                    // 解析時間字串並設定到 calendar
                    val (hours, minutes) = timeString.split(" : ").map { it.trim().toInt() }
                    calendar.set(Calendar.HOUR_OF_DAY, hours)
                    calendar.set(Calendar.MINUTE, minutes)

                    //設定鬧鐘
                    AlarmReceiver.add_alarm(context, calendar, dateString, timeString)
                }
                texttitle.setText("")
                textthing.setText("")
                pickdate.text = "[ 選擇日期 ]"
                picktime.text = "[ 選擇時間 ]"
                check1.isChecked = false
                check2.isChecked = false
                check3.isChecked = false
                check4.isChecked = false
                check5.isChecked = false
                check6.isChecked = false
                check7.isChecked = false

                // 跳轉回待辦清單畫面
                findNavController(it).navigate(R.id.navigation_dashboard)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //-----參考資料：https://yayar.medium.com/android-studio-datepicker-timepicker-
    // %E5%BB%BA%E7%AB%8B%E6%97%A5%E6%9C%9F%E9%81%B8%E6%93%87%E5%85%83%E4%BB%B6%E
    // 8%88%87%E6%99%82%E9%96%93%E9%81%B8%E6%93%87%E5%85%83%E4%BB%B6-fc1f37382ff4-----//
    //選擇日期
    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c[Calendar.YEAR] = year
        c[Calendar.MONTH] = month
        c[Calendar.DAY_OF_MONTH] = dayOfMonth
        pickdate.text = date_format1.format(c.time)
    }

    //-----參考資料：https://yayar.medium.com/android-studio-datepicker-timepicker-
    // %E5%BB%BA%E7%AB%8B%E6%97%A5%E6%9C%9F%E9%81%B8%E6%93%87%E5%85%83%E4%BB%B6%E
    // 8%88%87%E6%99%82%E9%96%93%E9%81%B8%E6%93%87%E5%85%83%E4%BB%B6-fc1f37382ff4-----//
    //選擇時間
    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val t = Calendar.getInstance()
        t[Calendar.HOUR_OF_DAY] = hourOfDay
        t[Calendar.MINUTE] = minute

        // 使用 String.format() 方法補零
        val formattedHour = String.format("%02d", hourOfDay)
        val formattedMinute = String.format("%02d", minute)

        picktime.text = "$formattedHour : $formattedMinute"
    }
}
