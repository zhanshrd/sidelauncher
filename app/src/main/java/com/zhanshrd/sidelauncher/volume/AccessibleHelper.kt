import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import com.zhanshrd.sidelauncher.MyAccessibilityService


object AccessibleHelper {

    // 系统辅助功能是否已开启
    @Throws(RuntimeException::class)
    fun isAccessibilityEnable(context: Context): Boolean {
        val service = context.packageName.toString() + "/" + MyAccessibilityService::class.java.canonicalName
        val accessibilityEnabled = try {
            Settings.Secure.getInt(context.applicationContext.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            0
        }
        val mStringColonSplitter: TextUtils.SimpleStringSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(context.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService: String = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }
}