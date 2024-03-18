// AppInfoAdapter.kt  
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zhanshrd.sidelauncher.databinding.ItemAppInfoBinding


class AppInfoAdapter : RecyclerView.Adapter<AppInfoAdapter.ViewHolder>() {

    private var appInfoList = emptyList<Triple<String, Drawable, String>>()

    fun setAppInfoList(list: List<Triple<String, Drawable, String>>) {
        appInfoList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (appName, appIcon, packageName) = appInfoList[position]
        holder.bind(appName, appIcon, packageName)
    }

    override fun getItemCount(): Int = appInfoList.size


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