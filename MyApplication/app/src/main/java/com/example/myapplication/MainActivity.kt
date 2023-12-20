package com.example.myapplication

import android.content.ComponentName
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


// TODO：建立應用程式，設定起始畫面
class MainActivity : AppCompatActivity() {

    //其他
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigation=findViewById(R.id.nav_view)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // 設置開啟時先顯示清單
        navView.selectedItemId = R.id.navigation_dashboard

        //開機執行
        val receiver = ComponentName(this, BootUpReceiver::class.java)
        val pm = this.packageManager
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

    }
    fun OK_dialog(title: String, message: String) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(title) //設置標題
        builder.setIcon(R.drawable.baseline_priority_high_24) //標題前面那個小圖示
        builder.setMessage(message) //提示訊息

        //設定確定按鈕
        builder.setPositiveButton("確定",
            DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })

        builder.create().show()
    }
}





