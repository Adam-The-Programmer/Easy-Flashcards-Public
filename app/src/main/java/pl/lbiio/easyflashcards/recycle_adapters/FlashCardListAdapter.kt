package pl.lbiio.easyflashcards.recycle_adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.BR
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.data.FlashcardToDisplayInList
import pl.lbiio.easyflashcards.databinding.RecycleListFlashCardItemBinding
import javax.inject.Inject

interface OnRemoveClickListener {
    fun onRemoveClick(db_position: String, cardMaxKnowledgeLevel: Int, isTranslationKnown: Boolean, isExplanationKnown: Boolean, isPhraseKnown: Boolean)
}

interface OnImportanceStatusClickListener {
    fun onImportanceStatusClick(db_position: String, importance: Boolean)
}

interface OnCardClickListener {
    fun onCardClick(db_position: String, isTranslationKnown: Boolean, isExplanationKnown: Boolean, isPhraseKnown: Boolean)
}


class FlashCardListAdapter @Inject constructor(
    private var flashCardsList: List<FlashcardToDisplayInList>,
    private var onRemoveClickListener: OnRemoveClickListener? = null,
    private var onImportanceStatusClickListener: OnImportanceStatusClickListener? = null,
    private var onCardClickListener: OnCardClickListener? = null
) : RecyclerView.Adapter<FlashCardListAdapter.FlashCardListViewHolder>() {

    private lateinit var binding: RecycleListFlashCardItemBinding

    inner class FlashCardListViewHolder(
        val binding: RecycleListFlashCardItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: FlashcardToDisplayInList) {
            binding.setVariable(BR.card_data, card)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashCardListViewHolder {
        binding = RecycleListFlashCardItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FlashCardListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlashCardListViewHolder, position: Int) {
        val element = flashCardsList[position]
        holder.bind(element)
        if(element.status==1 || element.status==2)
        {
            holder.binding.removeFlashCard.visibility = VISIBLE
        }
        else{
            holder.binding.removeFlashCard.visibility = GONE
        }
        holder.binding.flashCardImportance.setImageResource(if(element.isImportant) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
        holder.binding.removeFlashCard.setOnClickListener { onRemoveClickListener?.onRemoveClick(element.cardID, element.cardMaxKnowledgeLevel, element.isTranslationKnown, element.isExplanationKnown, element.isPhraseKnown) }
        holder.binding.flashCardImportance.setOnClickListener { onImportanceStatusClickListener?.onImportanceStatusClick(element.cardID, element.isImportant) }
        holder.itemView.setOnClickListener { onCardClickListener?.onCardClick(element.cardID, element.isTranslationKnown, element.isExplanationKnown, element.isPhraseKnown)}
    }

    override fun getItemCount(): Int = flashCardsList.size

    fun updateList(newList: List<FlashcardToDisplayInList>) {
        flashCardsList = newList
    }

    fun setOnRemoveClickListener(listener: OnRemoveClickListener) {
        onRemoveClickListener = listener
    }

    fun setOnImportanceStatusClickListener(listener: OnImportanceStatusClickListener) {
        onImportanceStatusClickListener = listener
    }

    fun setOnCardClickListener(listener: OnCardClickListener) {
        onCardClickListener = listener
    }

}