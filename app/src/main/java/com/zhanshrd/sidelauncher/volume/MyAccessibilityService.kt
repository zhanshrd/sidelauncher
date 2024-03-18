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

                var alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
                var musicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

                if (musicVolume > 0 && alarmVolume == 0){
                    //符合该条件的情况：1、正常播放音乐中。2、小菱语音过程中。3、熄火再启动时。
                    //只调整媒体音量，不调整闹钟音量
                    var targetMusicVolume = musicVolume
                    // 检查按键事件按键码
                    if (event.keyCode == 131) {//音量加
                        //音量范围控制
                        if(targetMusicVolume<30){
                            targetMusicVolume++
                        }
                        //Toast.makeText(this, "音量加", Toast.LENGTH_SHORT).show()
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetMusicVolume, 0)
                        //return true // 如果事件已经被处理，返回 true
                    }
                    else if (event.keyCode == 132) {//音量减
                        //音量范围控制
                        if(targetMusicVolume>0){
                            targetMusicVolume--
                        }
                        //Toast.makeText(this, "音量减", Toast.LENGTH_SHORT).show()
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetMusicVolume, 0)
                        //return true // 如果事件已经被处理，返回 true
                    }
                }
                else{
                    //媒体音量和闹钟音量同时调整
                    var targetAlarmVolume = alarmVolume
                    var targetMusicVolume = musicVolume

                    // 检查按键事件按键码
                    if (event.keyCode == 131) {//音量加
                        //音量范围控制
                        if(targetAlarmVolume<30){
                            targetAlarmVolume++
                        }
                        if(targetMusicVolume<30){
                            targetMusicVolume++
                        }
                        //Toast.makeText(this, "音量加", Toast.LENGTH_SHORT).show()
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, targetAlarmVolume, 0)
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetMusicVolume, 0)
                        //return true // 如果事件已经被处理，返回 true
                    }
                    else if (event.keyCode == 132) {//音量减
                        //音量范围控制
                        if(targetAlarmVolume>0){
                            targetAlarmVolume--
                        }
                        if(targetMusicVolume>0){
                            targetMusicVolume--
                        }
                        //Toast.makeText(this, "音量减", Toast.LENGTH_SHORT).show()
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, targetAlarmVolume, 0)
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetMusicVolume, 0)
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