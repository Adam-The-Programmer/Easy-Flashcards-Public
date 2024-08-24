package pl.lbiio.easyflashcards.recycle_adapters

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.BR
import pl.lbiio.easyflashcards.data.ExplanationItem
import pl.lbiio.easyflashcards.data.OwnSharedPackageToDisplayInList
import pl.lbiio.easyflashcards.databinding.RecycleListExplanationItemBinding
import pl.lbiio.easyflashcards.databinding.RecycleListSharedPackageItemBinding
import javax.inject.Inject


interface OnStopSharingClickListener {
    fun onStopSharingClick(firebaseId: String)
}

class OwnSharedPackagesListAdapter @Inject constructor(
    private var packagesList: MutableList<OwnSharedPackageToDisplayInList>,
    private var onStopSharingClickListener: OnStopSharingClickListener? = null,
) : RecyclerView.Adapter<OwnSharedPackagesListAdapter.OwnSharedPackagesListViewHolder>() {

    private lateinit var binding: RecycleListSharedPackageItemBinding
    inner class OwnSharedPackagesListViewHolder(
        val binding: RecycleListSharedPackageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ownSharedPackage: OwnSharedPackageToDisplayInList) {
            binding.setVariable(BR.own_shared_package, ownSharedPackage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnSharedPackagesListViewHolder {
        binding = RecycleListSharedPackageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OwnSharedPackagesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OwnSharedPackagesListViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val element = packagesList[position]
        holder.bind(element)
        holder.binding.delete.setOnClickListener { onStopSharingClickListener?.onStopSharingClick(element.firebaseId) }
    }

    override fun getItemCount(): Int = packagesList.size

    fun updateList(newList: MutableList<OwnSharedPackageToDisplayInList>) {
        packagesList = newList
    }

    fun setOnStopSharingClickListener(listener: OnStopSharingClickListener) {
        onStopSharingClickListener = listener
    }


}