// AppInfoAdapter.kt  
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.zhanshrd.sidelauncher.R
import com.zhanshrd.sidelauncher.databinding.ItemAppInfoBinding


class AppInfoAdapter(private val context: Context) : RecyclerView.Adapter<AppInfoAdapter.ViewHolder>() {

    private var appInfoList = emptyList<Triple<String, Drawable, String>>()

    //获取屏幕宽度
    // 获取 WindowManager 的实例
    val windowManager: WindowManager =  context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // 创建一个 DisplayMetrics 对象来保存屏幕的信息
    val displayMetrics = DisplayMetrics()

    fun setAppInfoList(list: List<Triple<String, Drawable, String>>) {
        appInfoList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // 使用 defaultDisplay 来获取屏幕信息
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (appName, appIcon, packageName) = appInfoList[position]
        holder.bind(appName, appIcon, packageName)

        // 从 SharedPreferences 恢复变量值
        val sharedPreferences = context.getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        var var1Value = sharedPreferences.getInt("var1Value", 50) // 如果找不到键，返回默认值 50
        var var2Value = sharedPreferences.getInt("var2Value", 10) // 如果找不到键，返回默认值 10
        var var3Value = sharedPreferences.getInt("var3Value", 20) // 如果找不到键，返回默认值 20

        val sidebarWidth = var1Value;//侧边导航栏宽度
        val rowCount = var2Value;//每行图标个数
        val rowspace = var3Value;//行间距

        // 现在你可以使用 displayMetrics 来获取屏幕的宽度和高度
        val screenWidth = displayMetrics.widthPixels-dpToPx(32+sidebarWidth,context)
        val screenHeight = displayMetrics.heightPixels


        //强制动态调整屏幕适配，忽略dpi设置值
        val view = holder.itemView
        val lp = view.layoutParams as FlexboxLayoutManager.LayoutParams
        lp.width = screenWidth/(rowCount+1)
        lp.height = (lp.width*rowCount/5)
        lp.setMargins(lp.width/rowCount/2,lp.width/rowCount/2,lp.width/rowCount/2,lp.width/rowCount/2+rowspace)
        view.setLayoutParams(lp)

        val imageView: ImageView  = holder.itemView.findViewById(R.id.appIcon);
        val params = imageView.layoutParams
        params.width = lp.width*9/10
        imageView.setLayoutParams(params)

        val textView: TextView = holder.itemView.findViewById(R.id.appName);
        textView.setTextSize((120/rowCount).toFloat())

    }

    override fun getItemCount(): Int = appInfoList.size
    fun dpToPx(dp: Int, context: Context): Int {
        return Math.round(dp * (context.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    class ViewHolder(private val binding: ItemAppInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appName: String, appIcon: Drawable, packageName: String) {
            binding.appName.text = appName
            binding.appIcon.setImageDrawable(appIcon)

            // 设置点击监听器
            itemView.setOnClickListener {
                // 处理点击事件
                val packageManager = binding.root.context.packageManager
                val intent = packageManager.getLaunchIntentForPackage(packageName)
                try {
                    binding.root.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(binding.root.context, "此应用不支持打开", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}