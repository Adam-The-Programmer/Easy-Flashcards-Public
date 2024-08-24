package pl.lbiio.easyflashcards.recycle_adapters

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.BR
import pl.lbiio.easyflashcards.data.ExplanationItem
import pl.lbiio.easyflashcards.databinding.RecycleListExplanationItemBinding
import javax.inject.Inject


interface OnExplanationChangeListener {
    fun onExplanationChange(list_position: Int, new_text: CharSequence?)
}

interface OnDeleteExplanationClickListener {
    fun onDeleteExplanationClick(list_position: Int)
}

class ExplanationListAdapter @Inject constructor(
    private var explanationList: ArrayList<ExplanationItem>,
    private var type: Int,
    private var onDeleteExplanationClickListener: OnDeleteExplanationClickListener? = null,
    private var onExplanationChangeListener: OnExplanationChangeListener? = null,
) : RecyclerView.Adapter<ExplanationListAdapter.ExplanationListViewHolder>() {

    private lateinit var binding: RecycleListExplanationItemBinding
    inner class ExplanationListViewHolder(
        val binding: RecycleListExplanationItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(explanation: ExplanationItem) {
            binding.setVariable(BR.explanation_data, explanation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExplanationListViewHolder {
        binding = RecycleListExplanationItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExplanationListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExplanationListViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val element = explanationList[position]
        holder.bind(element)
        holder.setIsRecyclable(false)
        holder.binding.explanation.isEnabled = type==1 || type==2 || type==-1
        holder.binding.explanation.setEndIconOnClickListener {
            onDeleteExplanationClickListener?.onDeleteExplanationClick(position)
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
                    onExplanationChangeListener?.onExplanationChange(position, s)
                }

                override fun afterTextChanged(s: Editable?) {
                    // Do something after the text has changed
                }
            }
        )
    }

    override fun getItemCount(): Int = explanationList.size

    fun updateList(newList: ArrayList<ExplanationItem>) {
        explanationList = newList
    }

    fun setOnDeleteExplanationClickListener(listener: OnDeleteExplanationClickListener) {
        onDeleteExplanationClickListener = listener
    }

    fun setOnExplanationChangeListener(listener: OnExplanationChangeListener) {
        onExplanationChangeListener = listener
    }

}