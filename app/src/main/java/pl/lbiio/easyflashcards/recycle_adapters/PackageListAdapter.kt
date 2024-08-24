package pl.lbiio.easyflashcards.recycle_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.BR
import pl.lbiio.easyflashcards.data.PackageToDisplayInList
import pl.lbiio.easyflashcards.databinding.RecycleListPackageItemBinding
import javax.inject.Inject


interface OnOptionsClickListener {
    fun onOptionsClick(db_position: String, list_position: Int, status: Int, view: View)
}

interface OnPackageClickListener {
    fun onPackageClick(db_position: String, nativeLanguage: String, foreignLanguage: String, status: Int)
}


class PackageListAdapter @Inject constructor(
    private var packageList: List<PackageToDisplayInList>,
    private var onOptionsClickListener: OnOptionsClickListener? = null,
    private var onPackageClickListener: OnPackageClickListener? = null
) : RecyclerView.Adapter<PackageListAdapter.PackageListViewHolder>() {

    lateinit var binding: RecycleListPackageItemBinding
    inner class PackageListViewHolder(
        val binding: RecycleListPackageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cardPackage: PackageToDisplayInList) {
            //binding.packageData = cardPackage
            binding.setVariable(BR.package_data, cardPackage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageListViewHolder {
        binding = RecycleListPackageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PackageListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageListViewHolder, position: Int) {
        val element = packageList[position]
        holder.bind(element)
        holder.binding.options.setOnClickListener { onOptionsClickListener?.onOptionsClick(element.packageID, position, element.status, holder.binding.options) }
        holder.itemView.setOnClickListener { onPackageClickListener?.onPackageClick(element.packageID, element.nativeLanguage, element.foreignLanguage, element.status)}
    }

    override fun getItemCount(): Int = packageList.size

    fun updateList(newList: List<PackageToDisplayInList>) {
        packageList = newList
    }

    fun setOnOptionsClickListener(listener: OnOptionsClickListener) {
        onOptionsClickListener = listener
    }

    fun setOnPackageClickListener(listener: OnPackageClickListener) {
        onPackageClickListener = listener
    }



}