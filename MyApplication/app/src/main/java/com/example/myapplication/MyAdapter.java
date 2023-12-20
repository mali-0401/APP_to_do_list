package com.example.myapplication;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
// TODO：將送來的資料放到Item內，讓RecyclerView顯示(Dashboard畫面)
//-----參考資料：https://ithelp.ithome.com.tw/articles/10187869-----//
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    private final List<String> mData_title;
    private final List<String> mData_thing;
    private final List<String> mData_date;
    private final List<String> mData_cycle;
    private final List<String> mData_time;
    private final List<String> mData_id;
    private final List<String> mData_sort;

    public MyAdapter(List<String> data, List<String> data2, List<String> data3, List<String> data4, List<String> data5, List<String> dataid, List<String> datasort) {
        mData_title = data ;
        mData_thing = data2 ;
        mData_date = data3 ;
        mData_cycle = data4 ;
        mData_time = data5 ;
        mData_id = dataid;
        mData_sort = datasort;
    }

    // 建立ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder{
        // 宣告元件
        private final TextView txtItem;
        private final TextView txtItem1;
        private final TextView txtItem2;
        private final TextView sort_icon;
        private final View mView;

        @SuppressLint("ResourceType")
        ViewHolder(View itemView) {
            super(itemView);
            txtItem = itemView.findViewById(R.id.txtItem);
            txtItem1 = itemView.findViewById(R.id.txtItem1);
            txtItem2 = itemView.findViewById(R.id.txtItem2);
            sort_icon = itemView.findViewById(R.id.sort_icon);
            mView  = itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        // 連結項目布局檔list_item_calendar或list_item_calendar_nullitem
        view = LayoutInflater.from(parent.getContext())
                .inflate(mData_title.isEmpty() ? R.layout.list_item_calendar_nullitem : R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (mData_title.isEmpty()) {
            // 如果 mData 是空的，就設置 "no_data_item_layout" 中的 TextView
            holder.txtItem1.setText("目前還沒有待辦事項喔~");
        } else {
            // 設置txtItem要顯示的內容
            holder.txtItem.setText(mData_title.get(position));
            holder.txtItem2.setText("截止日期："+mData_date.get(position));
            final boolean[] press = {false};

            if (mData_sort.get(position).equals("0")) {
                @SuppressLint("UseCompatLoadingForDrawables") Drawable icon = holder.mView.getContext().getDrawable(R.drawable.baseline_stars_24);
                holder.sort_icon.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            }
            // Item 點選時
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    press[0] = true;
                    // 只處理非長按事件
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    View dialogView = LayoutInflater.from(alertDialog.getContext()).inflate(R.layout.item_dialog, null);
                    alertDialog.setView(dialogView);

                    TextView textView_title = dialogView.findViewById(R.id.textView_title);
                    TextView textView_thing = dialogView.findViewById(R.id.textView_thing);
                    TextView textView_date = dialogView.findViewById(R.id.textView_date);
                    TextView textView_cycle = dialogView.findViewById(R.id.textView_cycle);
                    TextView textView_time = dialogView.findViewById(R.id.textView_time);

                    textView_title.setText(mData_title.get(position));
                    textView_thing.setText(mData_thing.get(position).replaceAll(" ", "\n"));
                    textView_thing.setMovementMethod(ScrollingMovementMethod.getInstance()); //可滾動
                    textView_date.setText(mData_date.get(position));
                    textView_cycle.setText(mData_cycle.get(position).replaceAll(",", "\n"));
                    textView_cycle.setMovementMethod(ScrollingMovementMethod.getInstance()); //可滾動
                    textView_time.setText(mData_time.get(position));

                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                    press[0] = false;
                }
            });

            //-----參考資料：https://blog.51cto.com/u_15073486/3631203-----//
            // item長按時
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint("Range")
                @Override
                public boolean onLongClick(View view) {
                    // 設定資料庫
                    final String DB_TABLE = "mylist";
                    SQLiteDatabase MyDB;
                    // 取得上面指定的檔名資料庫，如果該檔名不存在就會自動建立一個資料庫檔案
                    MyDB = new MyDBHelper(view.getContext(), "mylist.db", null, 1).getWritableDatabase();
                    if (!press[0])
                    {
                        //-----參考資料：ChatGPT-----//
                        // 創建彈出視圖
                        View popupView = LayoutInflater.from(view.getContext()).inflate(R.layout.item_menu, null);
                        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true;
                        PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        // 設置彈出視圖的位置
                        int[] location = new int[2];
                        view.getLocationOnScreen(location);
                        int x = location[0] + view.getWidth();
                        int y = location[1];
                        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);

                        // 在彈出視圖中找到並操作視圖元素
                        TextView edit = popupView.findViewById(R.id.action_edit);
                        TextView delete = popupView.findViewById(R.id.action_delete);
                        TextView order = popupView.findViewById(R.id.action_order);

                        // 依照排序情況設定按鈕文字及背景顏色
                        Cursor c = MyDB.query(
                                true, DB_TABLE, null, null, null,
                                null, null, null, null);
                        if (c.moveToFirst()) {
                            do {
                                if (Objects.equals(c.getString(c.getColumnIndex("id")), mData_id.get(position))) {
                                    if (c.getInt(c.getColumnIndex("sort")) == 1) {
                                        order.setText("釘選");
                                    } else {
                                        order.setText("取消釘選");
                                    }
                                    break; // 找到匹配的ID後跳出循環
                                }
                            } while (c.moveToNext());
                        }
                        c.close(); // 關閉查詢
                        //-----參考資料：ChatGPT-----//

                        edit.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("Range")
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss(); // 關閉PopupWindow

                                // 跳轉到home頁面並傳送數據
                                // 查詢對應資料
                                Cursor c = MyDB.query(
                                        true, DB_TABLE, null, null, null,
                                        null, null, null, null);

                                if (c.moveToFirst()) {
                                    do {
                                        if (Objects.equals(c.getString(c.getColumnIndex("id")), mData_id.get(position))) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("id", c.getString(c.getColumnIndex("id")));
                                            bundle.putString("title", c.getString(c.getColumnIndex("title")));
                                            bundle.putString("thing", c.getString(c.getColumnIndex("thing")));
                                            bundle.putString("date", c.getString(c.getColumnIndex("date")));
                                            bundle.putString("cycle", c.getString(c.getColumnIndex("cycle")));
                                            bundle.putString("time", c.getString(c.getColumnIndex("time")));
                                            Navigation.findNavController(holder.itemView).navigate(R.id.navigation_home, bundle);
                                            break; // 找到匹配的ID後跳出循環
                                        }
                                    } while (c.moveToNext());
                                }
                                c.close(); // 關閉查詢
                            }
                        });

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MyDB.delete(DB_TABLE, "id='" + mData_id.get(position) + "'", null);
                                mData_id.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());

                                // 刪除後則重新導向navigation_dashboard介面，使RecyclerView重新整理一次
                                Navigation.findNavController(holder.itemView).navigate(R.id.navigation_dashboard);
                                popupWindow.dismiss(); // 關閉PopupWindow
                            }
                        });

                        order.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("Range")
                            @Override
                            public void onClick(View v) {
                                Cursor c = MyDB.query(
                                        true, DB_TABLE, null, null, null,
                                        null, null, null, null);
                                if (c.moveToFirst()) {
                                    do {
                                        if (Objects.equals(c.getString(c.getColumnIndex("id")), mData_id.get(position))) {
                                            if (c.getInt(c.getColumnIndex("sort")) == 1) {
                                                ContentValues values = new ContentValues();
                                                values.put("id", c.getString(c.getColumnIndex("id")));
                                                values.put("title", c.getString(c.getColumnIndex("title")));
                                                values.put("thing", c.getString(c.getColumnIndex("thing")));
                                                values.put("date", c.getString(c.getColumnIndex("date")));
                                                values.put("cycle", c.getString(c.getColumnIndex("cycle")));
                                                values.put("time", c.getString(c.getColumnIndex("time")));
                                                values.put("sort", 0);

                                                MyDB.update(DB_TABLE, values,
                                                        "id='" + c.getString(c.getColumnIndex("id")) + "'", null);
                                                Toast.makeText(v.getContext(), "成功釘選 ["+c.getString(c.getColumnIndex("title"))+"]", Toast.LENGTH_LONG).show();
                                                Navigation.findNavController(holder.itemView).navigate(R.id.navigation_dashboard);
                                            } else {
                                                ContentValues values = new ContentValues();
                                                values.put("id", c.getString(c.getColumnIndex("id")));
                                                values.put("title", c.getString(c.getColumnIndex("title")));
                                                values.put("thing", c.getString(c.getColumnIndex("thing")));
                                                values.put("date", c.getString(c.getColumnIndex("date")));
                                                values.put("cycle", c.getString(c.getColumnIndex("cycle")));
                                                values.put("time", c.getString(c.getColumnIndex("time")));
                                                values.put("sort", 1);

                                                MyDB.update(DB_TABLE, values,
                                                        "id='" + c.getString(c.getColumnIndex("id")) + "'", null);
                                                Toast.makeText(v.getContext(), "取消釘選 ["+c.getString(c.getColumnIndex("title"))+"]", Toast.LENGTH_LONG).show();
                                                Navigation.findNavController(holder.itemView).navigate(R.id.navigation_dashboard);
                                            }
                                            break; // 找到匹配的ID後跳出循環
                                        }
                                    } while (c.moveToNext());
                                }
                                c.close(); // 關閉查詢
                                popupWindow.dismiss(); // 點擊後關閉彈出視圖
                            }
                        });
                    }
                    return true;
                }
            });


        }
    }


    @Override
    public int getItemCount() {
        if (mData_title.isEmpty()) {
            return 1;
        } else {
            return mData_title.size();
        }

    }
}

