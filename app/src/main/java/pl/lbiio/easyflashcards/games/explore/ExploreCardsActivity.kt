package pl.lbiio.easyflashcards.games.explore

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
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.animation.doOnEnd
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.data.ExploreGameItem
import pl.lbiio.easyflashcards.databinding.ActivityExploreCardsBinding
import pl.lbiio.easyflashcards.databinding.ExploreGameCardBinding
import pl.lbiio.easyflashcards.model.FlashcardsViewModel
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class ExploreCardsActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val flashcardsViewModel: FlashcardsViewModel by viewModels()
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityExploreCardsBinding
    private var packageId = ""
    //private var type = ""
    private var nativeLanguage=""
    private var foreignLanguage=""
    private var listOfGameItems: List<ExploreGameItem> = emptyList()
    var gameType = -1
    private var cardViewFlipper: ViewFlipper? = null
    private var progressBar: ProgressBar? = null
    private lateinit var tts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.exploringGameToolbar.root
        progressBar = binding.progressBar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        packageId = (savedInstanceState?.getString("package_id") ?: intent.getStringExtra("package_id")) as String
        gameType = savedInstanceState?.getInt("game_type", -1) ?: intent.getIntExtra("game_type", -1)
        nativeLanguage = savedInstanceState?.getString("native_language") ?: intent.getStringExtra("native_language").toString()
        foreignLanguage = savedInstanceState?.getString("foreign_language") ?: intent.getStringExtra("foreign_language").toString()
        cardViewFlipper = binding.cardViewFlipper
        tts = TextToSpeech(this, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("package_id", packageId)
        outState.putInt("game_type", gameType)
        outState.putString("native_language", nativeLanguage)
        outState.putString("foreign_language", foreignLanguage)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()

        loadDataFromDataBase(this)

        binding.previousCard.setOnClickListener {
            if (cardViewFlipper!!.displayedChild > 0) {
                val slideInLeft = AnimationUtils.loadAnimation(this, R.anim.previous_slide_in_left)
                val slideOutRight =
                    AnimationUtils.loadAnimation(this, R.anim.previous_slide_out_right)
                cardViewFlipper!!.inAnimation = slideInLeft
                cardViewFlipper!!.outAnimation = slideOutRight
                cardViewFlipper!!.showPrevious()
                progressBar?.progress = progressBar?.progress?.minus(1)!!
            }
        }
        binding.nextCard.setOnClickListener {
            if (cardViewFlipper!!.displayedChild < listOfGameItems.size - 1) {
                val slideInRight = AnimationUtils.loadAnimation(this, R.anim.next_slide_in_right)
                val slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.next_slide_out_left)
                cardViewFlipper!!.inAnimation = slideOutLeft
                cardViewFlipper!!.outAnimation = slideInRight
                cardViewFlipper!!.showNext()
                progressBar?.progress = progressBar?.progress?.plus(1)!!
            }
        }

    }

    private fun loadDataFromDataBase(ctx: Context) {
        when (gameType) {
            1 -> {
                flashcardsViewModel.loadWordToTranslationsExploreGame(packageId)
                lifecycleScope.launch {
                    flashcardsViewModel.wordToTranslations.collect {
                        listOfGameItems = flashcardsViewModel.wordToTranslations.value
                        progressBar?.max = listOfGameItems.size
                        progressBar?.progress = cardViewFlipper!!.displayedChild + 1
                        listOfGameItems.forEachIndexed { index, item ->
                            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
                            val cardBinding: ExploreGameCardBinding =
                                ExploreGameCardBinding.inflate(
                                    layoutInflater,
                                    cardViewFlipper,
                                    false
                                )
                            val textFront = cardBinding.textContentFront
                            val textBack = cardBinding.textContentBack
                            val textProgressFront = cardBinding.progressTextFront
                            val textProgressBack = cardBinding.progressTextBack
                            val cardBack = cardBinding.viewBack
                            val cardFront = cardBinding.viewFront
                            val importanceFront = cardBinding.changeImportanceFront
                            val importanceBack = cardBinding.changeImportanceBack
                            val speakFront = cardBinding.speakFront
                            val speakBack = cardBinding.speakBack


                            textProgressFront.text = getString(R.string.explore_progress, (index + 1).toString(),  listOfGameItems.size.toString())
                            textProgressBack.text = getString(R.string.explore_progress, (index + 1).toString(),  listOfGameItems.size.toString())
                            textFront.text = item.frontText
                            textBack.text = item.backText
                            cardBack.setOnClickListener {
                                flipCard(ctx, cardFront, cardBack)
                            }
                            cardFront.setOnClickListener {
                                flipCard(ctx, cardBack, cardFront)
                            }
                            speakFront.setOnClickListener {
                                speak(item.frontText, foreignLanguage)
                                Log.d("jezyk", "$nativeLanguage, $foreignLanguage")
                            }
                            speakBack.setOnClickListener {
                                speak(item.backText, nativeLanguage)
                            }
                            importanceFront.setOnClickListener {
                                this.launch {
                                    flashcardsViewModel.updateImportance(
                                        item.cardID,
                                        !item.importance
                                    )
                                }
                                item.importance = !item.importance
                                importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                            }
                            importanceBack.setOnClickListener {
                                this.launch {
                                    flashcardsViewModel.updateImportance(
                                        item.cardID,
                                        !item.importance
                                    )
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
                }
            }

            2 -> {
                flashcardsViewModel.loadWordToExplanations(packageId)
                lifecycleScope.launch {
                    flashcardsViewModel.wordToExplanations.collect {
                        listOfGameItems = flashcardsViewModel.wordToExplanations.value.map {
                            ExploreGameItem(
                                it.cardID,
                                it.frontText,
                                it.backText.replace("|", "\n\n"),
                                it.importance
                            )
                        }
                        progressBar?.max = listOfGameItems.size
                        progressBar?.progress = cardViewFlipper!!.displayedChild + 1
                        listOfGameItems.forEachIndexed { index, item ->
                            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
                            val cardBinding: ExploreGameCardBinding =
                                ExploreGameCardBinding.inflate(
                                    layoutInflater,
                                    cardViewFlipper,
                                    false
                                )
                            val textFront = cardBinding.textContentFront
                            val textBack = cardBinding.textContentBack
                            val textProgressFront = cardBinding.progressTextFront
                            val textProgressBack = cardBinding.progressTextBack
                            val cardBack = cardBinding.viewBack
                            val cardFront = cardBinding.viewFront
                            val importanceFront = cardBinding.changeImportanceFront
                            val importanceBack = cardBinding.changeImportanceBack
                            val speakFront = cardBinding.speakFront
                            val speakBack = cardBinding.speakBack

                            textProgressFront.text = getString(R.string.explore_progress, (index + 1).toString(),  listOfGameItems.size.toString())
                            textProgressBack.text = getString(R.string.explore_progress, (index + 1).toString(),  listOfGameItems.size.toString())
                            textFront.text = item.frontText
                            textBack.text = item.backText
                            cardBack.setOnClickListener {
                                flipCard(ctx, cardFront, cardBack)
                            }
                            cardFront.setOnClickListener {
                                flipCard(ctx, cardBack, cardFront)
                            }
                            speakFront.setOnClickListener {
                                speak(item.frontText, foreignLanguage)
                            }
                            speakBack.setOnClickListener {
                                speak(item.backText, foreignLanguage)
                            }
                            importanceFront.setOnClickListener {
                                this.launch {
                                    flashcardsViewModel.updateImportance(
                                        item.cardID,
                                        !item.importance
                                    )
                                }
                                item.importance = !item.importance
                                importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                            }
                            importanceBack.setOnClickListener {
                                this.launch {
                                    flashcardsViewModel.updateImportance(
                                        item.cardID,
                                        !item.importance
                                    )
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
                }
            }

            3 -> {
                flashcardsViewModel.loadWordToPhrases(packageId)
                lifecycleScope.launch {
                    flashcardsViewModel.wordToPhrases.collect {
                        listOfGameItems = flashcardsViewModel.wordToPhrases.value.map {
                            ExploreGameItem(
                                it.cardID,
                                it.frontText,
                                it.backText.replace("|", "\n\n"),
                                it.importance
                            )
                        }
                        progressBar?.max = listOfGameItems.size
                        progressBar?.progress = cardViewFlipper!!.displayedChild + 1
                        listOfGameItems.forEachIndexed { index, item ->
                            val layoutInflater: LayoutInflater = LayoutInflater.from(ctx)
                            val cardBinding: ExploreGameCardBinding =
                                ExploreGameCardBinding.inflate(
                                    layoutInflater,
                                    cardViewFlipper,
                                    false
                                )
                            val textFront = cardBinding.textContentFront
                            val textBack = cardBinding.textContentBack
                            val textProgressFront = cardBinding.progressTextFront
                            val textProgressBack = cardBinding.progressTextBack
                            val cardBack = cardBinding.viewBack
                            val cardFront = cardBinding.viewFront
                            val importanceFront = cardBinding.changeImportanceFront
                            val importanceBack = cardBinding.changeImportanceBack
                            val speakFront = cardBinding.speakFront
                            val speakBack = cardBinding.speakBack

                            textProgressFront.text = getString(R.string.explore_progress, (index + 1).toString(),  listOfGameItems.size.toString())
                            textProgressBack.text = getString(R.string.explore_progress, (index + 1).toString(),  listOfGameItems.size.toString())
                            textFront.text = item.frontText
                            textBack.text = item.backText
                            cardBack.setOnClickListener {
                                flipCard(ctx, cardFront, cardBack)
                            }
                            cardFront.setOnClickListener {
                                flipCard(ctx, cardBack, cardFront)
                            }
                            speakFront.setOnClickListener {
                                speak(item.frontText, foreignLanguage)
                            }
                            speakBack.setOnClickListener {
                                speak(item.backText, foreignLanguage)
                            }
                            importanceFront.setOnClickListener {
                                this.launch {
                                    flashcardsViewModel.updateImportance(
                                        item.cardID,
                                        !item.importance
                                    )
                                }
                                item.importance = !item.importance
                                importanceFront.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                                importanceBack.setImageResource(if (item.importance) R.drawable.ic_filled_book_mark else R.drawable.ic_outline_bookmark)
                            }
                            importanceBack.setOnClickListener {
                                this.launch {
                                    flashcardsViewModel.updateImportance(
                                        item.cardID,
                                        !item.importance
                                    )
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

    override fun onInit(status: Int) {

    }

    private fun speak(text: String, languageTag: String) {
        val utteranceId = UUID.randomUUID().toString()
        val result = tts.setLanguage(Locale.forLanguageTag(languageTag))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, getString(R.string.unsupported_language), Toast.LENGTH_SHORT).show()
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

}