package pl.lbiio.easyflashcards.recycle_adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.api_classes.UserAward
import pl.lbiio.easyflashcards.databinding.RecycleListUserAwardItemBinding
import javax.inject.Inject

class UserAwardListAdapter @Inject constructor(
    private var userAwards: MutableList<UserAward>,
) : RecyclerView.Adapter<UserAwardListAdapter. UserAwardListViewHolder>() {

    private lateinit var binding: RecycleListUserAwardItemBinding
    inner class  UserAwardListViewHolder(
        val binding: RecycleListUserAwardItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userAward: UserAward) {
            binding.userAward = userAward
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  UserAwardListViewHolder {
        binding = RecycleListUserAwardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return  UserAwardListViewHolder(binding)
    }

    override fun onBindViewHolder(holder:  UserAwardListViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val element = userAwards[position]
        holder.bind(element)
        holder.setIsRecyclable(true)

    }

    override fun getItemCount(): Int = userAwards.size



}