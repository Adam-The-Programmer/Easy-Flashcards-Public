package pl.lbiio.easyflashcards.recycle_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import pl.lbiio.easyflashcards.BR
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.data.SharedPackageToBuy
import pl.lbiio.easyflashcards.databinding.RecycleListProductItemBinding
import javax.inject.Inject

interface OnBuyClickListener {
    fun onBuyClick(
        packageID: String,
        path: String,
        name: String,
        amount: Int,
        downloads: Int,
        maxPoints: Int,
        acquiredPoints: Int,
        price: Int,
        currency: String
    )
}

class ItemToBuyListAdapter @Inject constructor(
    private var productsList: List<SharedPackageToBuy>,
    private var onBuyClickListener: OnBuyClickListener? = null,
) : RecyclerView.Adapter<ItemToBuyListAdapter.ItemsToBuyViewHolder>() {

    private lateinit var binding: RecycleListProductItemBinding

    inner class ItemsToBuyViewHolder(
        val binding: RecycleListProductItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemToBuy: SharedPackageToBuy) {
            binding.setVariable(BR.item_to_buy_data, itemToBuy)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsToBuyViewHolder {
        binding = RecycleListProductItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemsToBuyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemsToBuyViewHolder, position: Int) {
        val element = productsList[position]
        holder.bind(element)
        holder.binding.buy.setOnClickListener {
            onBuyClickListener?.onBuyClick(
                element.packageID,
                element.path,
                element.name,
                element.amount,
                element.downloads,
                element.maxPoints,
                element.acquiredPoints,
                element.price,
                element.currency
            )
        }
    }

    override fun getItemCount(): Int = productsList.size

    fun updateList(newList: List<SharedPackageToBuy>) {
        productsList = newList
    }

    fun setOnBuyClickListener(listener: OnBuyClickListener) {
        onBuyClickListener = listener
    }


}