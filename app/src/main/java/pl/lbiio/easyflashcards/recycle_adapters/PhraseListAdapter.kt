package pl.lbiio.easyflashcards.recycle_adapters

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.data.PhraseItem
import pl.lbiio.easyflashcards.databinding.RecycleListPhraseItemBinding
import javax.inject.Inject

interface OnPhraseChangeListener {
    fun onPhraseChange(list_position: Int, new_text: CharSequence?)
}

interface OnDeletePhraseClickListener {
    fun onDeletePhraseClick(list_position: Int)
}

class PhraseListAdapter @Inject constructor(
    private var phraseList: ArrayList<PhraseItem>,
    private var type: Int,
    private var onDeletePhraseClickListener: OnDeletePhraseClickListener? = null,
    private var onPhraseChangeListener: OnPhraseChangeListener? = null,
) : RecyclerView.Adapter<PhraseListAdapter.PhraseListViewHolder>() {

    private lateinit var binding: RecycleListPhraseItemBinding

    inner class PhraseListViewHolder(
        val binding: RecycleListPhraseItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(phrase: PhraseItem) {
            binding.phraseData = phrase
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseListViewHolder {
        binding = RecycleListPhraseItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhraseListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PhraseListViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val element = phraseList[position]
        holder.bind(element)
        holder.setIsRecyclable(false)
        holder.binding.phrase.isEnabled = type==1 || type==2 || type==-1
        holder.binding.phrase.setEndIconOnClickListener {
            onDeletePhraseClickListener?.onDeletePhraseClick(position)
        }

        holder.binding.word.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do something before the text is changed
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    onPhraseChangeListener?.onPhraseChange(position, s)
                }

                override fun afterTextChanged(s: Editable?) {
                    // Do something after the text has changed
                }
            }
        )
    }

    override fun getItemCount(): Int = phraseList.size

    fun updateList(newList: ArrayList<PhraseItem>) {
        phraseList = newList
    }

    fun setOnDeletePhraseClickListener(listener: OnDeletePhraseClickListener) {
        onDeletePhraseClickListener = listener
    }

    fun setOnPhraseChangeListener(listener: OnPhraseChangeListener) {
        onPhraseChangeListener = listener
    }

}