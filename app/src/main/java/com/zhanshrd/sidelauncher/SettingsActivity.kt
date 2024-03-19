package com.zhanshrd.sidelauncher

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {

    private lateinit var var1ValueTextView: TextView
    private lateinit var var1DecrementButton: Button
    private lateinit var var1IncrementButton: Button
    private var var1Value = 0
    private lateinit var var2ValueTextView: TextView
    private lateinit var var2DecrementButton: Button
    private lateinit var var2IncrementButton: Button
    private var var2Value = 0
    private lateinit var var3ValueTextView: TextView
    private lateinit var var3DecrementButton: Button
    private lateinit var var3IncrementButton: Button
    private var var3Value = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        var var1Value = sharedPreferences.getInt("var1Value", 50)
        var var2Value = sharedPreferences.getInt("var2Value", 10)
        var var3Value = sharedPreferences.getInt("var3Value", 20)

        // 初始化界面元素
        var1ValueTextView = findViewById(R.id.var1Value)
        var1DecrementButton = findViewById(R.id.var1Decrement)
        var1IncrementButton = findViewById(R.id.var1Increment)
        var1ValueTextView.text = var1Value.toString() // 显示初始值

        var2ValueTextView = findViewById(R.id.var2Value)
        var2DecrementButton = findViewById(R.id.var2Decrement)
        var2IncrementButton = findViewById(R.id.var2Increment)
        var2ValueTextView.text = var2Value.toString() // 显示初始值

        var3ValueTextView = findViewById(R.id.var3Value)
        var3DecrementButton = findViewById(R.id.var3Decrement)
        var3IncrementButton = findViewById(R.id.var3Increment)
        var3ValueTextView.text = var3Value.toString() // 显示初始值

        // 设置加减按钮的点击事件监听器
        var1DecrementButton.setOnClickListener {
            var1Value--
            var1ValueTextView.text = var1Value.toString()
        }
        var1IncrementButton.setOnClickListener {
            var1Value++
            var1ValueTextView.text = var1Value.toString()
        }

        var2DecrementButton.setOnClickListener {
            var2Value--
            var2ValueTextView.text = var2Value.toString()
        }
        var2IncrementButton.setOnClickListener {
            var2Value++
            var2ValueTextView.text = var2Value.toString()
        }

        var3DecrementButton.setOnClickListener {
            var3Value--
            var3ValueTextView.text = var3Value.toString()
        }
        var3IncrementButton.setOnClickListener {
            var3Value++
            var3ValueTextView.text = var3Value.toString()
        }

        // 设置保存按钮的点击事件监听器
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            // 获取 SharedPreferences 实例
            val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
            // 获取编辑器来修改 SharedPreferences
            val editor = sharedPreferences.edit()
            // 保存变量值
            editor.putInt("var1Value", var1Value)
            editor.putInt("var2Value", var2Value)
            editor.putInt("var3Value", var3Value)
            // ... 保存其他变量值 ...
            // 提交修改
            editor.apply()

            //刷新主页布局
            setResult(RESULT_OK); // 设置返回结果

            // 完成后返回前一个 Activity 或执行其他操作
            finish() // 或者使用 Intent 跳转到其他 Activity
        }
    }
}