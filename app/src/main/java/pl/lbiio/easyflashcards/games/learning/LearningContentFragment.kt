package pl.lbiio.easyflashcards.games.learning

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.data.LearnGameItem
import pl.lbiio.easyflashcards.databinding.FragmentLearningContentBinding
import pl.lbiio.easyflashcards.databinding.LearnGameCardBinding
import pl.lbiio.easyflashcards.model.FlashcardsViewModel
import java.util.Locale
import java.util.UUID

interface OnLearnGameFinishedListener{
    fun onLearnGameFinished(score: Int)
}

@AndroidEntryPoint
class LearningContentFragment (
    private var onLearnGameFinishedListener: OnLearnGameFinishedListener?=null,
) : Fragment(), TextToSpeech.OnInitListener {
    private lateinit var binding: FragmentLearningContentBinding
    private val flashcardsViewModel: FlashcardsViewModel by viewModels()
    private var packageId = ""
    private var gameType = -1
    private var nativeLanguage=""
    private var foreignLanguage=""
    private var scoreValue = 0
    private lateinit var tts: TextToSpeech

    @Volatile
    private var buffer: MutableList<LearnGameItem> = mutableListOf()
    private var cardViewFlipper: ViewFlipper? = null
    private var timePointer: ProgressBar? = null
    private var score: TextView? = null
    private lateinit var slideInRight: Animation
    private lateinit var slideOutLeft: Animation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningContentBinding.inflate(inflater)
        tts = TextToSpeech(requireContext(), this)
        timePointer = binding.timePointer
        score = binding.score
        cardViewFlipper = binding.cardViewFlipper
        slideInRight = AnimationUtils.loadAnimation(context, R.anim.next_slide_in_right)
        slideOutLeft = AnimationUtils.loadAnimation(context, R.anim.next_slide_out_left)
        cardViewFlipper!!.inAnimation = slideOutLeft
        cardViewFlipper!!.outAnimation = slideInRight

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            packageId = requireArguments().getString("package_id")?:""
            gameType = requireArguments().getInt("game_type")
            nativeLanguage = requireArguments().getString("native_language") ?: ""
            foreignLanguage = requireArguments().getString("foreign_language") ?: ""
            Log.d("typ gry", gameType.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentData()
        runTimer()
        binding.dontKnow.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                if (cardViewFlipper!!.displayedChild < buffer.size - 1) {
                    //withContext(Dispatchers.IO) {
                        flashcardsViewModel.setKnowledgeLevel(buffer[cardViewFlipper!!.displayedChild].cardID, gameType, -1)
                    //}
                    Log.d("buffer.size - 1", (buffer.size - 1).toString())
                    Log.d("cardViewFlipper!!.displayedChild", cardViewFlipper!!.displayedChild.toString())
                    withContext(Dispatchers.Main){
                        cardViewFlipper!!.showNext()
                    }

                }
//                else if (cardViewFlipper!!.displayedChild == buffer.size - 1) {
//                    //withContext(Dispatchers.IO) {
//                        flashcardsViewModel.setKnowledgeLevel(buffer[cardViewFlipper!!.displayedChild].cardID, gameType, -1)
//                    //}
//                    //val handler = Handler()
//                    withContext(Dispatchers.Main){
//                        cardViewFlipper!!.showNext()
//                    }
//                    //handler.postDelayed({ updateFlipCardSet(requireContext()) }, 200)
//                }
                else {
                    flashcardsViewModel.setKnowledgeLevel(buffer[cardViewFlipper!!.displayedChild].cardID, gameType, -1)
                    withContext(Dispatchers.Main){
                        cardViewFlipper!!.showNext()
                        updateFlipCardSet(requireContext())
                    }
                }
                withContext(Dispatchers.Main){
                    timePointer!!.progress = timePointer!!.max
                    runTimer()
                }
            }


        }
        binding.know.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO){
                withContext(Dispatchers.Main){
                    scoreValue += timePointer!!.progress
                    score!!.text = scoreValue.toString()
                    timePointer!!.progress=timePointer!!.max
                    runTimer()
                }
                //withContext(Dispatchers.IO) {
                    flashcardsViewModel.updateCardParametersWhenKnowClicked(packageId, buffer[cardViewFlipper!!.displayedChild].cardID, gameType, scoreValue)
               // }
                Log.d("buffer.size - 1", (buffer.size - 1).toString())
                Log.d("cardViewFlipper!!.displayedChild", cardViewFlipper!!.displayedChild.toString())
                if (cardViewFlipper!!.displayedChild < buffer.size - 1) {
                    //withContext(Dispatchers.IO) {
                        flashcardsViewModel.setKnowledgeLevel(buffer[cardViewFlipper!!.displayedChild].cardID, gameType, 1)
                    //}
                    withContext(Dispatchers.Main){
                        cardViewFlipper!!.showNext()
                    }
                }
//                else if (cardViewFlipper!!.displayedChild == buffer.size - 1) {
//                    //withContext(Dispatchers.IO) {
//                        flashcardsViewModel.setKnowledgeLevel(buffer[cardViewFlipper!!.displayedChild].cardID, gameType,1)
//                    //}
//                    //val handler = Handler()
//                    withContext(Dispatchers.Main){
//                        cardViewFlipper!!.showNext()
//                    }
//                    //handler.postDelayed({ updateFlipCardSet(requireContext()) }, 200)
//
//
//                }
                else {
                    flashcardsViewModel.setKnowledgeLevel(buffer[cardViewFlipper!!.displayedChild].cardID, gameType, 1)
                    withContext(Dispatchers.Main){
                        cardViewFlipper!!.showNext()
                        updateFlipCardSet(requireContext())
                    }
                }
            }
        }
    }


    private fun updateFlipCardSet(ctx: Context) {
        buffer.clear()
        cardViewFlipper!!.removeAllViews()
        lifecycleScope.launch(Dispatchers.Main) {
            when (gameType) {
                1 -> {
                    //lifecycleScope.launch(Dispatchers.Main) {
                        buffer = flashcardsViewModel.getWordToTranslationLearnGameItems(packageId, 4)
                            .toMutableList().map { LearnGameItem(it.cardID, it.frontText, it.backText.replace("|", "\n\n"), it.importance, it.level) } as MutableList<LearnGameItem>

                        if(buffer.isEmpty()){
                            onLearnGameFinishedListener?.onLearnGameFinished(scoreValue)
                        }
                        else{
                            buffer.forEachIndexed { index, item ->
                                val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
                                val cardBinding: LearnGameCardBinding =
                                    LearnGameCardBinding.inflate(layoutInflater, cardViewFlipper, false)
                                val textFront = cardBinding.textContentFront
                                val textBack = cardBinding.textContentBack
                                val cardBack = cardBinding.viewBack
                                val cardFront = cardBinding.viewFront
                                val importanceFront = cardBinding.changeImportanceFront
                                val importanceBack = cardBinding.changeImportanceBack
                                val speakFront = cardBinding.speakFront
                                val speakBack = cardBinding.speakBack
                                textFront.text = item.frontText
                                textBack.text = item.backText
                                speakFront.setOnClickListener {
                                    speak(item.frontText, foreignLanguage)
                                }
                                speakBack.setOnClickListener {
                                    speak(item.backText, nativeLanguage)
                                }
                                cardBack.setOnClickListener {
                                    flipCard(ctx, cardFront, cardBack)
                                    stopTimer()
                                }
                                cardFront.setOnClickListener {
                                    flipCard(ctx, cardBack, cardFront)
                                    stopTimer()
                                }
                                importanceFront.setOnClickListener {
                                    this.launch {
                                        withContext(Dispatchers.IO) {
                                            flashcardsViewModel.updateImportance(buffer[index].cardID, !buffer[index].importance)
                                        }
                                    }
                                    buffer[index].importance = !buffer[index].importance
                                    importanceFront.setImageResource(if (buffer[index].importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                    importanceBack.setImageResource(if (buffer[index].importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                }
                                importanceBack.setOnClickListener {
                                    this.launch {
                                        withContext(Dispatchers.IO) {
                                            flashcardsViewModel.updateImportance(buffer[index].cardID, !buffer[index].importance)
                                        }
                                    }
                                    buffer[index].importance = !buffer[index].importance
                                    importanceFront.setImageResource(if (buffer[index].importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                    importanceBack.setImageResource(if (buffer[index].importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                }
                                importanceFront.setImageResource(if (buffer[index].importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                importanceBack.setImageResource(if (buffer[index].importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                cardViewFlipper!!.addView(cardBinding.root)
                            }
                        }

                    //}
                }
                2->{
//                    lifecycleScope.launch(Dispatchers.Main) {
                        buffer = flashcardsViewModel.getWordToExplanationsLearnGameItems(packageId, 4)
                            .toMutableList().map { LearnGameItem(it.cardID, it.frontText, it.backText.replace("|", "\n\n"), it.importance, it.level) } as MutableList<LearnGameItem>
                        if(buffer.isEmpty()){
                            onLearnGameFinishedListener?.onLearnGameFinished(scoreValue)
                        }
                        else{
                            buffer.forEachIndexed { index, item ->
                                val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
                                val cardBinding: LearnGameCardBinding =
                                    LearnGameCardBinding.inflate(layoutInflater, cardViewFlipper, false)
                                val textFront = cardBinding.textContentFront
                                val textBack = cardBinding.textContentBack
                                val cardBack = cardBinding.viewBack
                                val cardFront = cardBinding.viewFront
                                val importanceFront = cardBinding.changeImportanceFront
                                val importanceBack = cardBinding.changeImportanceBack
                                val speakFront = cardBinding.speakFront
                                val speakBack = cardBinding.speakBack
                                textFront.text = item.frontText
                                textBack.text = item.backText
                                speakFront.setOnClickListener {
                                    speak(item.frontText, foreignLanguage)
                                }
                                speakBack.setOnClickListener {
                                    speak(item.backText, foreignLanguage)
                                }
                                cardBack.setOnClickListener {
                                    flipCard(ctx, cardFront, cardBack)
                                    stopTimer()
                                }
                                cardFront.setOnClickListener {
                                    flipCard(ctx, cardBack, cardFront)
                                    stopTimer()
                                }
                                importanceFront.setOnClickListener {
                                    this.launch {
                                        withContext(Dispatchers.IO) {
                                            flashcardsViewModel.updateImportance(buffer[index].cardID, !item.importance)
                                        }
                                    }

                                    item.importance = !item.importance
                                    importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                    importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                }
                                importanceBack.setOnClickListener {
                                    this.launch {
                                        withContext(Dispatchers.IO) {
                                            flashcardsViewModel.updateImportance(buffer[index].cardID, !item.importance)
                                        }
                                    }
                                    item.importance = !item.importance
                                    importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                    importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                }
                                importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                cardViewFlipper!!.addView(cardBinding.root)
                            }
                        }

                    //}
                }
                3->{
                    //lifecycleScope.launch(Dispatchers.Main) {
                        buffer = flashcardsViewModel.getWordToPhrasesLearnGameItems(packageId, 4)
                            .toMutableList().map { LearnGameItem(it.cardID, it.frontText, it.backText.replace("|", "\n\n"), it.importance, it.level) } as MutableList<LearnGameItem>
                        if(buffer.isEmpty()){
                            onLearnGameFinishedListener?.onLearnGameFinished(scoreValue)
                        }
                        else{
                            buffer.forEachIndexed { index, item ->
                                val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
                                val cardBinding: LearnGameCardBinding =
                                    LearnGameCardBinding.inflate(layoutInflater, cardViewFlipper, false)
                                val textFront = cardBinding.textContentFront
                                val textBack = cardBinding.textContentBack
                                val cardBack = cardBinding.viewBack
                                val cardFront = cardBinding.viewFront
                                val importanceFront = cardBinding.changeImportanceFront
                                val importanceBack = cardBinding.changeImportanceBack
                                val speakFront = cardBinding.speakFront
                                val speakBack = cardBinding.speakBack
                                textFront.text = item.frontText
                                textBack.text = item.backText
                                speakFront.setOnClickListener {
                                    speak(item.frontText, foreignLanguage)
                                }
                                speakBack.setOnClickListener {
                                    speak(item.backText, foreignLanguage)
                                }
                                cardBack.setOnClickListener {
                                    flipCard(ctx, cardFront, cardBack)
                                    stopTimer()
                                }
                                cardFront.setOnClickListener {
                                    flipCard(ctx, cardBack, cardFront)
                                    stopTimer()
                                }
                                importanceFront.setOnClickListener {
                                    this.launch {
                                        withContext(Dispatchers.IO){
                                            flashcardsViewModel.updateImportance(item.cardID, !item.importance)
                                        }
                                    }
                                    item.importance = !buffer[index].importance
                                    importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                    importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                }
                                importanceBack.setOnClickListener {
                                    this.launch {
                                        withContext(Dispatchers.IO) {
                                            flashcardsViewModel.updateImportance(buffer[index].cardID, !buffer[index].importance)
                                        }
                                    }
                                    item.importance = !item.importance
                                    importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                    importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                }
                                importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                cardViewFlipper!!.addView(cardBinding.root)
                            }
                        }

                    //}
                }
            }
        }

    }

    @SuppressLint("ResourceType")
    fun flipCard(context: Context, visibleView: View, inVisibleView: View) {
        try {
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
        } catch (_: Exception) {

        }
    }

    private fun runTimer() {
        binding.know.isEnabled = true
        lifecycleScope.launch {
            val anim = ProgressBarAnimation(timePointer!!, timePointer!!.progress.toFloat(), 0F)
            anim.duration = 10000
            timePointer!!.startAnimation(anim)
        }
    }

    private fun stopTimer(){
        timePointer!!.clearAnimation()
    }

    private fun onTimeIsUp(){
        binding.know.isEnabled = false
    }

    private fun getCurrentData(){
        lifecycleScope.launch(Dispatchers.Main) {
            scoreValue = flashcardsViewModel.getCurrentData(packageId, gameType)
            binding.score.text = scoreValue.toString()
            updateFlipCardSet(requireContext())
        }

    }


    inner class ProgressBarAnimation(
        private val progressBar: ProgressBar,
        private val from: Float,
        private val to: Float
    ) :
        Animation() {
        init {
            interpolator = LinearInterpolator()
            this.duration = duration
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            progressBar.progress = value.toInt()
            if(value.equals(0F)){
                onTimeIsUp()
            }
        }
    }

    override fun onInit(status: Int) {

    }

    private fun speak(text: String, languageTag: String) {
        val utteranceId = UUID.randomUUID().toString()
        val result = tts.setLanguage(Locale.forLanguageTag(languageTag))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(requireContext(), getString(R.string.unsupported_language), Toast.LENGTH_SHORT).show()
        }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                // Called when speech synthesis starts
            }

            override fun onDone(utteranceId: String?) {
                // Called when speech synthesis is completed
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                // Called if there is an error during speech synthesis
            }
        })
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }


    fun setOnLearnGameFinishedListener(listener: OnLearnGameFinishedListener) {
        onLearnGameFinishedListener = listener
    }

}