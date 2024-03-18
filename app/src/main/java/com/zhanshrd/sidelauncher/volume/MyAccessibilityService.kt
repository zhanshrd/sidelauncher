package com.zhanshrd.sidelauncher

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class MyAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onKeyEvent(event: KeyEvent?): Boolean {
        event?.let {
            // 检查按键事件类型（按下或抬起)
            if (event?.action == KeyEvent.ACTION_UP){
                //Toast.makeText(this, "按下按钮", Toast.LENGTH_SHORT).show()
                val audioManager: AudioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                // 检查按键事件按键码
                if (event.keyCode == 131) {//音量加
                    var alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
                    if(alarmVolume>0){
                        Toast.makeText(this, "音量加", Toast.LENGTH_SHORT).show()
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
                    if(alarmVolume>0){
                        Toast.makeText(this, "音量减", Toast.LENGTH_SHORT).show()
                        audioManager.adjustSuggestedStreamVolume(
                            AudioManager.STREAM_MUSIC, // 要调整的音频流类型
                            AudioManager.ADJUST_LOWER, // 调整方向：增加音量
                            0 // 可选的标志位，通常设置为0
                        )
                        //return true // 如果事件已经被处理，返回 true
                    }
                }
            }
        }
        return super.onKeyEvent(event)
    }

    override fun onInterrupt() {

    }
}