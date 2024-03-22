package com.zhanshrd.sidelauncher

import AppInfoAdapter
import VolumeChangeObserver
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.LruCache
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.zhanshrd.sidelauncher.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AppInfoAdapter
    private lateinit var volumeChangeObserver: VolumeChangeObserver
    private val REQUEST_SETTINGS = 1 // 定义一个唯一的请求码
    // 创建 ActivityResultLauncher 对象
    val startActivity= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //首先判断resultCode
        if (it.resultCode == RESULT_OK){
            val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
            var var1Value = sharedPreferences.getInt("var1Value", 50) // 如果找不到键，返回默认值 50
            var var2Value = sharedPreferences.getInt("var2Value", 9) // 如果找不到键，返回默认值 19
            var var3Value = sharedPreferences.getInt("var3Value", 45) // 如果找不到键，返回默认值 45
            var var4Value = sharedPreferences.getInt("var4Value", 20) // 如果找不到键，返回默认值 20
            val sidebarWidth = var1Value;//侧边导航栏宽度
            binding.mainLayout.setPadding(dpToPx(sidebarWidth+16,this),dpToPx(5,this), dpToPx(16,this), dpToPx(16,this))
            binding.buttonSettings.setTextSize((var4Value*10/var2Value).toFloat())
            adapter.notifyDataSetChanged()
        }else{}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        // 从 SharedPreferences 恢复变量值
        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        var var1Value = sharedPreferences.getInt("var1Value", 50) // 如果找不到键，返回默认值 50
        var var2Value = sharedPreferences.getInt("var2Value", 9) // 如果找不到键，返回默认值 19
        var var3Value = sharedPreferences.getInt("var3Value", 45) // 如果找不到键，返回默认值 45
        var var4Value = sharedPreferences.getInt("var4Value", 20) // 如果找不到键，返回默认值 20

        //背景图片
        //TODO
        val backgroundImagePath = ""
        val sidebarWidth = var1Value;//侧边导航栏宽度

        if(backgroundImagePath.equals("")){
            val drawable: Drawable? = resources.getDrawable(R.drawable.bak, null)
            drawable?.let {
                window.setBackgroundDrawable(it)
            }
        }

        // 获取 Window 对象
        val window = this.window

        // 清除所有之前的标志
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

        // 添加新的标志，允许内容扩展到导航栏
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        // 设置内容视图的行为，以便它不会被系统栏覆盖
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // 如果需要隐藏导航栏和状态栏，可以添加以下标志
                // or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // or View.SYSTEM_UI_FLAG_FULLSCREEN
                )


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.mainLayout.setPadding(dpToPx(sidebarWidth+16,this),dpToPx(5,this), dpToPx(16,this), dpToPx(16,this))
        binding.buttonSettings.setTextSize((var4Value*10/var2Value).toFloat())

        val recyclerView = binding.recyclerView
        // 创建FlexboxLayoutManager实例
        val flexboxLayoutManager = FlexboxLayoutManager(this)
        // 设置Flex方向为ROW，即横向排列
        flexboxLayoutManager.flexDirection = FlexDirection.ROW
        // 设置FlexWrap为WRAP，即自动换行
        flexboxLayoutManager.flexWrap = FlexWrap.WRAP
        //定义项目在副轴轴上如何对齐
        flexboxLayoutManager.alignItems = AlignItems.CENTER;

        // 将FlexboxLayoutManager设置为RecyclerView的布局管理器
        recyclerView.layoutManager = flexboxLayoutManager

        adapter = AppInfoAdapter(this)
        recyclerView.adapter = adapter

        val packages = getAppsWithLauncherIcons(this)
        val appInfoList = packages.map { packageInfo ->
            val appName = packageInfo.loadLabel(packageManager).toString()

            //var appIcon = packageInfo.loadIcon(packageManager)
            var appIcon = getCachedIcon(packageInfo.packageName)
            if (appIcon != null) {
                // 缓存命中，直接使用缓存中的图标
            } else {
                // 缓存未命中，需要加载图标
                appIcon = packageInfo.loadIcon(packageManager) // 从系统加载图标
                if (appIcon != null) {
                    // 将新图标添加到缓存中
                    putCachedIcon(packageInfo.packageName, appIcon)
                }
            }
            val packageName = packageInfo.packageName
            Triple(appName, appIcon, packageName)
        }
        adapter.setAppInfoList(appInfoList)

        // 创建并注册闹钟音量变化观察者
        volumeChangeObserver = VolumeChangeObserver(this, Handler(Looper.getMainLooper()));
        getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, volumeChangeObserver);

        //创建音量按键后台监听
        if (!AccessibleHelper.isAccessibilityEnable(this)) {
            Toast.makeText(this, "需要开启无障碍服务", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        val intent = Intent(this, MyAccessibilityService::class.java)
        startService(intent)

        //绑定设置按钮动作
        bindButtons()
    }

    private fun bindButtons() {
        binding.buttonSettings.setOnClickListener {
            openSettingPage()
        }

    }

    fun dpToPx(dp: Int, context: Context): Int {
        return Math.round(dp * (context.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    private fun openSettingPage() {

        val intent = Intent(this, SettingsActivity::class.java)
        startActivity.launch(intent);
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在 Activity 销毁时取消注册闹钟音量观察者
        volumeChangeObserver.unregister()
    }

    override fun onResume() {
        super.onResume()
        if (!AccessibleHelper.isAccessibilityEnable(this)) {
            Toast.makeText(this, "需要开启无障碍服务", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
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

val iconCache = LruCache<String, Drawable>(99)

fun getCachedIcon(packageName: String): Drawable? {
    return iconCache.get(packageName)
}

fun putCachedIcon(packageName: String, icon: Drawable) {
    iconCache.put(packageName, icon)
}