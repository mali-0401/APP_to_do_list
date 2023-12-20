package com.example.myapplication;

import android.app.DatePickerDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;

import java.util.Calendar;
// TODO：跳出選擇時間的對話框讓使用者選擇
//-----參考資料：https://yayar.medium.com/android-studio-datepicker-timepicker-
// 建立日期選擇元件與時間選擇元件-fc1f37382ff4-----//
public class TimerPickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity()
                , hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}

