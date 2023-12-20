package com.example.myapplication;

import android.app.DatePickerDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.Calendar;
// TODO：跳出選擇日期的對話框讓使用者選擇
//-----參考資料：https://yayar.medium.com/android-studio-datepicker-timepicker-
// 建立日期選擇元件與時間選擇元件-fc1f37382ff4-----//
public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 獲取當前日期
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // 創建 DatePickerDialog 對話框
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity()
                , year, month, day);
    }
}

