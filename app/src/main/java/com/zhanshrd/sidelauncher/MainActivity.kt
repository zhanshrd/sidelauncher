package com.zhanshrd.sidelauncher

import AppInfoAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.zhanshrd.sidelauncher.databinding.ActivityMainBinding

import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AppInfoAdapter

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
            Pair(appName, appIcon)
        }
        adapter.setAppInfoList(appInfoList)
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