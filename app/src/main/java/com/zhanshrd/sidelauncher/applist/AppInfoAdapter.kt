// AppInfoAdapter.kt  
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zhanshrd.sidelauncher.databinding.ItemAppInfoBinding


class AppInfoAdapter : RecyclerView.Adapter<AppInfoAdapter.ViewHolder>() {

    private var appInfoList = emptyList<Pair<String, Drawable>>()

    fun setAppInfoList(list: List<Pair<String, Drawable>>) {
        appInfoList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (appName, appIcon) = appInfoList[position]
        holder.bind(appName, appIcon)
    }

    override fun getItemCount(): Int = appInfoList.size


    class ViewHolder(private val binding: ItemAppInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(appName: String, appIcon: Drawable) {
            binding.appName.text = appName
            binding.appIcon.setImageDrawable(appIcon)
        }
    }
}