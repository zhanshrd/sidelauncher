import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.net.Uri
import android.os.Handler


class VolumeChangeObserver(context: Context, private val handler: Handler) : ContentObserver(handler) {

    // 将传入的 context 保存为成员变量
    private val mContext: Context = context

    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)

        val a = uri.toString()

        //闹钟音量发生变化(
        //当前情景中，一般只有系统自带音乐播放或暂停时闹钟音量会变。
        //当音乐暂停，媒体音量自动归零，但闹钟音量自动变成归零前的媒体音量。
        //该特殊场景下，闹钟音量可以作为归零前的媒体音量的快照，用于强制恢复媒体音量
        if(a.equals("content://settings/system/volume_alarm_speaker")){
            var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
            if(currentVolume>0){
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0)
            }
        }
    }

    fun unregister() {
        // 取消注册观察者
        mContext.contentResolver.unregisterContentObserver(this)
    }
}