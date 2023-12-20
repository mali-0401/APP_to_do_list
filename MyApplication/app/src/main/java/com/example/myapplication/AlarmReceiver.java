package com.example.myapplication;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.ui.home.HomeFragment;

import java.util.Calendar;
import java.util.Objects;
// TODO：收到通知訊息後，將發送資料庫內與日期時間匹配的事項之通知
//-----參考資料：https://ithelp.ithome.com.tw/articles/10188916-----//
//-----參考資料：https://oldgrayduck.blogspot.com/2012/10/androidalarmmanager.html-----//
//-----參考資料：https://xiang1023.blogspot.com/2017/11/android-alarmmanager.html-----//
public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("Range")
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bData = intent.getExtras();
        if (bData.get("title").equals("activity_app")) {
            // 收到鬧鐘時要幹嘛
            // 發送通知
            String dateString = bData.get("cal_time_date").toString();
            String timeString = bData.get("cal_time_time").toString();

            // 設定資料庫
            String DB_TABLE = "mylist";
            SQLiteDatabase MyDB;
            // 取得上面指定的檔名資料庫，如果該檔名不存在就會自動建立一個資料庫檔案
            MyDB = new MyDBHelper(context, "mylist.db", null, 1).getWritableDatabase();

            Cursor c = MyDB.query(
                    DB_TABLE, null,
                    null, null,
                    null, null, null
            );

            while (c.moveToNext()) {
                if (Objects.equals(c.getString(c.getColumnIndex("date")), dateString) &&
                        Objects.equals(c.getString(c.getColumnIndex("time")), timeString))
                {
                    int id = c.getInt(c.getColumnIndex("id"));
                    String title = c.getString(c.getColumnIndex("title"));

                    Intent mainIntent = new Intent(context, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // 發送通知
                    addNotify(pendingIntent, context, "代辦事項通知！", title, id);
                }

            }

            c.close();
        }


    }
    // 建立通知
    public void addNotify(PendingIntent pendingIntent, Context context, String title, String text, int id) {
        NotificationChannel channel = new NotificationChannel("TODO", "待辦事項", NotificationManager.IMPORTANCE_HIGH);
        Notification.Builder builder = new Notification.Builder(context, "TODO");
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.createNotificationChannel(channel);
        builder.setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)  // 點擊通知時開啟應用程式
                .setAutoCancel(true);  // 點擊通知後自動關閉
        Notification notification = builder.build();
        manager.notify(id, notification);
    }

    /***    加入(與系統註冊)鬧鐘    ***/
    public static void add_alarm(Context context, Calendar cal, String date, String time) {
        Log.d("my_alarm_log", "alarm add time: " + cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DATE) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));

        Intent intent = new Intent(context, AlarmReceiver.class);
        // 以日期字串組出不同的 category 以添加多個鬧鐘
        intent.addCategory("ID." + cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DATE) + "-" + cal.get(Calendar.HOUR_OF_DAY) + "." + cal.get(Calendar.MINUTE) + "." + cal.get(Calendar.SECOND));
        String AlarmTimeTag = "Alarmtime " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

        intent.putExtra("title", "activity_app");
        intent.putExtra("time", AlarmTimeTag);
        intent.putExtra("cal_time_date",date);
        intent.putExtra("cal_time_time",time);

        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

}

