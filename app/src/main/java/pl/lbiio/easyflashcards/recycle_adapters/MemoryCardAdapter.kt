package pl.lbiio.easyflashcards.recycle_adapters

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.BR
import pl.lbiio.easyflashcards.data.MemoryCard
import pl.lbiio.easyflashcards.databinding.MemoryCardViewBinding
import pl.lbiio.easyflashcards.databinding.MemoryFlipCardBinding

class MemoryCardAdapter(
    private val context: Context,
    private val _dataList: MutableList<MemoryCard>,
    private var onMemoryCardClickListener: OnMemoryCardClickListener? = null,
    private var onGameFinishListener: OnGameFinishListener? = null
) : RecyclerView.Adapter<MemoryCardAdapter.CardListViewHolder>() {

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    interface OnMemoryCardClickListener {
        fun onMemoryCardClick(cardID: String, content: String)
    }

    interface OnGameFinishListener {
        fun onGameFinish()
    }

    private lateinit var binding: MemoryFlipCardBinding
    inner class CardListViewHolder(
        private val binding: MemoryFlipCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(memoryCard: MemoryCard) {
            binding.setVariable(BR.memory_card, memoryCard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardListViewHolder {
        binding = MemoryFlipCardBinding.inflate(layoutInflater, parent, false)
        return CardListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardListViewHolder, position: Int) {
        holder.bind(_dataList[position])
        holder.setIsRecyclable(false)

        val cardBinding: MemoryCardViewBinding =
            MemoryCardViewBinding.inflate(layoutInflater, binding.memoryCardViewFlipper, false)
        val textFront = cardBinding.textFront
        val textBack = cardBinding.textBack
        val cardBack = cardBinding.cardBack
        val cardFront = cardBinding.cardFront
        textFront.text = ""
        textBack.text = _dataList[position].content

        cardBack.visibility = if (_dataList[position].isBackVisible) View.VISIBLE else View.GONE
        cardFront.visibility = if (_dataList[position].isBackVisible) View.GONE else View.VISIBLE
        when (_dataList[position].state) {
            0 -> {
                flipCard(context, cardFront, cardBack)
                _dataList[position].isBackVisible = false
            }

            1 -> {
                flipCard(context, cardBack, cardFront)
                _dataList[position].isBackVisible = true
            }

            2 -> {
                cardBack.visibility = View.VISIBLE
                cardFront.visibility = View.GONE
                _dataList[position].isBackVisible = true
            }
        }
        binding.memoryCardViewFlipper.addView(cardBinding.root)
        cardFront.setOnClickListener {
            onMemoryCardClickListener?.onMemoryCardClick(
                _dataList[position].cardID,
                _dataList[position].content
            )
        }
        if(_dataList.all { it.state==2 }) onGameFinishListener?.onGameFinish()
    }

    override fun getItemCount(): Int = _dataList.size

    fun setOnMemoryCardClickListener(listener: OnMemoryCardClickListener) {
        onMemoryCardClickListener = listener
    }
    fun setOnGameFinishListener(listener: OnGameFinishListener) {
        onGameFinishListener = listener
    }

    @SuppressLint("ResourceType")
    private fun flipCard(context: Context, visibleView: View, inVisibleView: View) {
        try {
            if(visibleView.visibility!= View.VISIBLE && inVisibleView.visibility!= View.GONE){
                visibleView.visibility = View.VISIBLE
                val scale = context.resources.displayMetrics.density
                val cameraDist = 8000 * scale
                visibleView.cameraDistance = cameraDist
                inVisibleView.cameraDistance = cameraDist
                val flipOutAnimatorSet =
                    AnimatorInflater.loadAnimator(
                        context,
                        R.anim.flip_out
                    ) as AnimatorSet
                flipOutAnimatorSet.setTarget(inVisibleView)
                val flipInAnimatorSet =
                    AnimatorInflater.loadAnimator(
                        context,
                        R.anim.flip_in
                    ) as AnimatorSet
                flipInAnimatorSet.setTarget(visibleView)
                flipOutAnimatorSet.start()
                flipInAnimatorSet.start()
                flipInAnimatorSet.doOnEnd {
                    inVisibleView.visibility = View.GONE
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}