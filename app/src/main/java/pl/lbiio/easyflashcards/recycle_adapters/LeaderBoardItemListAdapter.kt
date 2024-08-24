package pl.lbiio.easyflashcards.recycle_adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.data.LeaderBoardItem
import pl.lbiio.easyflashcards.databinding.RecycleListLeaderBoardItemBinding
import javax.inject.Inject

class LeaderBoardListAdapter @Inject constructor(
    private var leaderBoardItem: MutableList<LeaderBoardItem>,
) : RecyclerView.Adapter<LeaderBoardListAdapter. LeaderBoardListViewHolder>() {

    private lateinit var binding: RecycleListLeaderBoardItemBinding
    inner class  LeaderBoardListViewHolder(
        val binding: RecycleListLeaderBoardItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(leaderBoardItem: LeaderBoardItem) {
            binding.leaderBoardItem = leaderBoardItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  LeaderBoardListViewHolder {
        binding = RecycleListLeaderBoardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return  LeaderBoardListViewHolder(binding)
    }

    override fun onBindViewHolder(holder:  LeaderBoardListViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val element = leaderBoardItem[position]
        holder.bind(element)
        holder.setIsRecyclable(true)

    }

    override fun getItemCount(): Int = leaderBoardItem.size



}