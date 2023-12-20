package com.example.myapplication;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
// TODO：將送來的資料放到Item內，讓RecyclerView顯示(Notifications畫面)
//-----參考資料：https://ithelp.ithome.com.tw/articles/10187869-----//
public class MyAdapter_calendar extends RecyclerView.Adapter<MyAdapter_calendar.ViewHolder> {

    private final List<String> mDataTitle;
    private final List<String> mDataTime;
    private final List<String> mDataThing;
    private final List<String> mDataDate;
    private final List<String> mDataId;
    private final List<String> mDataCycle;

    public MyAdapter_calendar(List<String> data1, List<String> data2, List<String> data3, List<String> data4, List<String> data5, List<String> data6) {
        mDataTitle = data1;
        mDataTime = data2;
        mDataThing = data3;
        mDataDate = data4;
        mDataId = data5;
        mDataCycle = data6;
    }

    // 建立ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder{
        // 宣告元件
        private final TextView txtItem1;
        private final TextView txtItem2;
        private final View mView;

        ViewHolder(View itemView) {
            super(itemView);
            txtItem1 = itemView.findViewById(R.id.txtItem1);
            txtItem2 = itemView.findViewById(R.id.txtItem2);
            mView  = itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // 連結項目布局檔list_item_calendar或list_item_calendar_nullitem
        view = LayoutInflater.from(parent.getContext())
                .inflate(mDataTitle.isEmpty() ? R.layout.list_item_calendar_nullitem : R.layout.list_item_calendar, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mDataTitle.isEmpty()) {
            // 如果 mData 是空的，就設置 "no_data_item_layout" 中的 TextView
            holder.txtItem1.setText("今日沒有活動");
        } else {
            // 如果 mData 不是空的，就設置 "list_item_calendar" 中的 TextView
            holder.txtItem1.setText(" ◆ " + mDataTitle.get(position));
            holder.txtItem2.setText(mDataTime.get(position));

            // Item 點選時跳出Dialog
            holder.mView.setOnClickListener((v)->{
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                View dialogView = LayoutInflater.from(alertDialog.getContext()).inflate(R.layout.item_dialog, null);
                alertDialog.setView(dialogView);

                TextView textView_title = dialogView.findViewById(R.id.textView_title);
                TextView textView_thing = dialogView.findViewById(R.id.textView_thing);
                TextView textView_date = dialogView.findViewById(R.id.textView_date);
                TextView textView_cycle = dialogView.findViewById(R.id.textView_cycle);
                TextView textView_time = dialogView.findViewById(R.id.textView_time);

                textView_title.setText(mDataTitle.get(position));
                textView_thing.setText(mDataThing.get(position).replaceAll(" ", "\n"));
                textView_thing.setMovementMethod(ScrollingMovementMethod.getInstance()); //可滾動
                textView_date.setText(mDataDate.get(position));
                textView_cycle.setText(mDataCycle.get(position).replaceAll(",", "\n"));
                textView_cycle.setMovementMethod(ScrollingMovementMethod.getInstance()); //可滾動
                textView_time.setText(mDataTime.get(position));

                AlertDialog dialog = alertDialog.create();
                dialog.show();

            });
        }
    }

    @Override
    public int getItemCount() {
        if (mDataTitle.isEmpty()) {
            return 1;
        } else {
            return mDataTitle.size();
        }

    }
}