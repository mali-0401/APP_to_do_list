package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.ui.home.HomeFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
//-----參考資料：https://ithelp.ithome.com.tw/articles/10188916-----//
//-----參考資料：https://oldgrayduck.blogspot.com/2012/10/androidalarmmanager.html-----//
//-----參考資料：https://xiang1023.blogspot.com/2017/11/android-alarmmanager.html-----//
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: 設定開機時自動將資料庫內所有須建立通知的事項重新建立通知

        /* 同一個接收者可以收多個不同行為的廣播，所以可以判斷收進來的行為為何，再做不同的動作 */
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* 收到廣播後要做的事 */
            //設定資料庫
            String DB_TABLE = "mylist";
            SQLiteDatabase MyDB;
            MyDB = new MyDBHelper(context, "mylist.db", null, 1).getWritableDatabase();
            Cursor c = MyDB.query(
                    true, DB_TABLE, null, null, null, null, null, null, null);
            while (c.moveToNext()) {
                @SuppressLint("Range") String dateStr = c.getString(c.getColumnIndex("date"));
                @SuppressLint("Range") String timeStr = c.getString(c.getColumnIndex("time"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                try {
                    // 將日曆字串轉成calendar
                    calendar.setTime(Objects.requireNonNull(dateFormat.parse(dateStr)));

                    // 將時間字串解析為小時和分鐘
                    int hours = Integer.parseInt(timeStr.split(":")[0]);
                    int minutes = Integer.parseInt(timeStr.split(":")[1]);

                    // 將小時和分鐘設定到calendar
                    calendar.set(Calendar.HOUR_OF_DAY, hours);
                    calendar.set(Calendar.MINUTE, minutes);

                    //設定鬧鐘
                    AlarmReceiver.add_alarm(context,calendar, dateStr, timeStr);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
