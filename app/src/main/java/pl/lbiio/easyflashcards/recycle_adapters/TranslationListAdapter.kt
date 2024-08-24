package pl.lbiio.easyflashcards.recycle_adapters

import android.annotation.SuppressLint
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import android.text.TextWatcher
import pl.lbiio.easyflashcards.data.TranslationItem
import pl.lbiio.easyflashcards.databinding.RecycleListTranslationItemBinding
import javax.inject.Inject

interface OnDeleteTranslationClickListener {
    fun onDeleteTranslationClick(list_position: Int)
}

interface OnTranslationChangeListener {
    fun onTranslationChange(list_position: Int, new_text: CharSequence?)
}

class TranslationListAdapter @Inject constructor(
    private var translationList: ArrayList<TranslationItem>,
    private var type: Int,
    private var onDeleteTranslationClickListener: OnDeleteTranslationClickListener? = null,
    private var onTranslationChangeListener: OnTranslationChangeListener? = null,
) : RecyclerView.Adapter<TranslationListAdapter.TranslationListViewHolder>() {

    private lateinit var binding: RecycleListTranslationItemBinding
    inner class TranslationListViewHolder(
        val binding: RecycleListTranslationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(translation: TranslationItem) {
            binding.translationData = translation
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranslationListViewHolder {
        binding = RecycleListTranslationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TranslationListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TranslationListViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val element = translationList[position]
        holder.bind(element)
        holder.setIsRecyclable(false)
        holder.binding.translation.isEnabled = type==1 || type==2 || type==-1
        holder.binding.translation.setEndIconOnClickListener {
            onDeleteTranslationClickListener?.onDeleteTranslationClick(position)
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
                    onTranslationChangeListener?.onTranslationChange(position, s)
                }

                override fun afterTextChanged(s: Editable?) {
                    // Do something after the text has changed
                }
            }
        )
    }

    override fun getItemCount(): Int = translationList.size

    fun updateList(newList: ArrayList<TranslationItem>) {
        translationList = newList
    }

    fun setOnDeleteTranslationClickListener(listener: OnDeleteTranslationClickListener) {
        onDeleteTranslationClickListener = listener
    }

    fun setOnTranslationChangeListener(listener: OnTranslationChangeListener) {
        onTranslationChangeListener = listener
    }

}