package com.zhanshrd.sidelauncher

import AppInfoAdapter
import VolumeChangeObserver
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.zhanshrd.sidelauncher.databinding.ActivityMainBinding

import com.google.android.flexbox.FlexboxLayoutManager


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AppInfoAdapter
    private lateinit var volumeChangeObserver: VolumeChangeObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.recyclerView
        // 创建FlexboxLayoutManager实例
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        // 设置Flex方向为ROW，即横向排列
        flexboxLayoutManager.flexDirection = FlexDirection.ROW
        // 设置FlexWrap为WRAP，即自动换行
        flexboxLayoutManager.flexWrap = FlexWrap.WRAP

        // 将FlexboxLayoutManager设置为RecyclerView的布局管理器
        recyclerView.layoutManager = flexboxLayoutManager

        adapter = AppInfoAdapter()
        recyclerView.adapter = adapter

        val packages = getAppsWithLauncherIcons(this)
        val appInfoList = packages.map { packageInfo ->
            val appName = packageInfo.loadLabel(packageManager).toString()
            val appIcon = packageInfo.loadIcon(packageManager)
            val packageName = packageInfo.packageName
            Triple(appName, appIcon, packageName)
        }
        adapter.setAppInfoList(appInfoList)

        // 创建并注册闹钟音量变化观察者
        volumeChangeObserver = VolumeChangeObserver(this, Handler(Looper.getMainLooper()));
        getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, volumeChangeObserver);
    }

    //监听按钮触发事件
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        // 检查按键事件类型（按下或抬起)
        if (event?.action == KeyEvent.ACTION_UP){
            //Toast.makeText(this@MainActivity, "按下按钮", Toast.LENGTH_LONG).show()
            val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            // 检查按键事件按键码
            if (event.keyCode == 131) {//音量加
                var alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
                if(alarmVolume>1){
                    audioManager.adjustSuggestedStreamVolume(
                        AudioManager.STREAM_MUSIC, // 要调整的音频流类型
                        AudioManager.ADJUST_RAISE, // 调整方向：增加音量
                        0 // 可选的标志位，通常设置为0
                    )
                    //return true // 如果事件已经被处理，返回 true
                }
            }
            else if (event.keyCode == 132) {//音量减
                var alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
                if(alarmVolume>1){
                    audioManager.adjustSuggestedStreamVolume(
                        AudioManager.STREAM_MUSIC, // 要调整的音频流类型
                        AudioManager.ADJUST_LOWER, // 调整方向：增加音量
                        0 // 可选的标志位，通常设置为0
                    )
                    //return true // 如果事件已经被处理，返回 true
                }
            }
        }
        // 如果不处理该事件或需要继续传递事件，调用 super 方法
        return super.dispatchKeyEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在 Activity 销毁时取消注册闹钟音量观察者
        volumeChangeObserver.unregister()
    }
}

fun getAppsWithLauncherIcons(context: Context): List<ApplicationInfo> {
    val packageManager = context.packageManager
    val installedApps = packageManager.getInstalledApplications(0)
    val appsWithLauncherIcons = mutableListOf<ApplicationInfo>()

    for (appInfo in installedApps) {
        // 尝试获取应用的所有活动信息
        val activities = packageManager.queryIntentActivities(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER),
            0
        )

        // 检查是否有任何活动标记为 CATEGORY_LAUNCHER
        for (resolveInfo in activities) {
            if (resolveInfo.activityInfo.packageName == appInfo.packageName) {
                // 如果找到匹配的包名，说明该应用有桌面图标
                appsWithLauncherIcons.add(appInfo)
                break
            }
        }
    }
    return appsWithLauncherIcons.toList()
}