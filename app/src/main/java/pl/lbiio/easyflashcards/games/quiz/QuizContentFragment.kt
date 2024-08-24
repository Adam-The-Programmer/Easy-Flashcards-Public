package pl.lbiio.easyflashcards.games.quiz

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.data.QuizItem
import pl.lbiio.easyflashcards.data.QuizOption
import pl.lbiio.easyflashcards.databinding.FragmentQuizContentBinding
import pl.lbiio.easyflashcards.model.FlashcardsViewModel

interface OnQuizFinishedListener{
    fun onQuizFinished(score: Int)
}

@AndroidEntryPoint
class QuizContentFragment(private var onQuizFinishedListener: OnQuizFinishedListener?=null) : Fragment() {
    private lateinit var binding: FragmentQuizContentBinding
    private var step = 1
    private var packageId = ""
    private var gameType = -1
    private var score = 0
    private val quizItems = mutableListOf<QuizItem>()
    private val cardsViewModel: FlashcardsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizContentBinding.inflate(layoutInflater)
        populateListOfQuestions()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            packageId = requireArguments().getString("package_id")?:""
            gameType = requireArguments().getInt("game_type")
            Log.d("typ gry", gameType.toString())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.Main) {
            binding.optionOne.setOnClickListener {
                stopTimer()
                if(isCorrect(step,0)) score += binding.timePointer.progress
                lifecycleScope.launch(Dispatchers.Main) {
                    colorButtonsWhenOptionClick(0, getCorrectAnswer(step))
                    delay(1000)
                    makeButtonsUncolored()
                    withContext(Dispatchers.Main){
                        populateViews(step+1)
                        onButtonClick()
                    }
                }
            }

            binding.optionTwo.setOnClickListener {
                stopTimer()
                if(isCorrect(step,1)) score += binding.timePointer.progress
                lifecycleScope.launch(Dispatchers.Main) {
                    colorButtonsWhenOptionClick(1, getCorrectAnswer(step))
                    delay(1000)
                    makeButtonsUncolored()
                    withContext(Dispatchers.Main){
                        populateViews(step+1)
                        onButtonClick()
                    }
                }
            }
            binding.optionThree.setOnClickListener {
                stopTimer()
                if(isCorrect(step,2)) score += binding.timePointer.progress
                lifecycleScope.launch(Dispatchers.Main) {
                    colorButtonsWhenOptionClick(2, getCorrectAnswer(step))
                    delay(1000)
                    makeButtonsUncolored()
                    withContext(Dispatchers.Main){
                        populateViews(step+1)
                        onButtonClick()
                    }
                }
            }
            binding.optionFour.setOnClickListener {
                stopTimer()
                if(isCorrect(step,3)) score += binding.timePointer.progress
                lifecycleScope.launch(Dispatchers.Main) {
                    colorButtonsWhenOptionClick(3, getCorrectAnswer(step))
                    delay(1000)
                    makeButtonsUncolored()
                    withContext(Dispatchers.Main){
                        populateViews(step+1)
                        onButtonClick()
                    }
                }
            }
        }
    }

    private fun populateListOfQuestions(){
        lifecycleScope.launch {
            val questionsWithCorrectAnswers = cardsViewModel.getQuizQuestionsWithCorrectAnswers(packageId, gameType)
            questionsWithCorrectAnswers.forEach{ questionWithAnswer->
                val wrongAnswersForQuestions = cardsViewModel.getQuizIncorrectAnswers(packageId, questionWithAnswer.cardID) // zwraca quizOption tylko isCorect Zawsze jest false
                val allAnswers = mutableListOf<QuizOption>()
                allAnswers.addAll(wrongAnswersForQuestions)
                allAnswers.add(QuizOption(questionWithAnswer.correctAnswer, true))
                allAnswers.shuffle()
                quizItems.add(QuizItem(questionWithAnswer.cardID, questionWithAnswer.question, allAnswers))
            }
            withContext(Dispatchers.Main){
                populateViews(step)
            }
        }
    }

    private fun populateViews(step: Int){
        if(step == 16){
            onQuizFinishedListener?.onQuizFinished(score)
        }else{
            binding.timePointer.progress = 100
            binding.step.text = getString(R.string.step_counter, step.toString())
            binding.question.text = quizItems[step-1].question.replace("|", "\n\n")
            binding.optionOne.text = quizItems[step-1].options[0].answer
            binding.optionTwo.text = quizItems[step-1].options[1].answer
            binding.optionThree.text = quizItems[step-1].options[2].answer
            binding.optionFour.text = quizItems[step-1].options[3].answer
            runTimer()
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
        }
    }

    private fun isCorrect(step: Int, button: Int): Boolean {
        return quizItems[step-1].options[button].isCorrect
    }

    private fun getCorrectAnswer(step: Int): Int {
        return if(quizItems[step-1].options[0].isCorrect) 0 else if(quizItems[step-1].options[1].isCorrect) 1 else if(quizItems[step-1].options[2].isCorrect) 2 else 3
    }

    private fun runTimer() {
        lifecycleScope.launch {
            val anim = ProgressBarAnimation(
                binding.timePointer,
                binding.timePointer.progress.toFloat(),
                0F
            )
            anim.duration = 10000
            binding.timePointer.startAnimation(anim)
        }
    }

    private fun onButtonClick(){
        step++
        binding.score.text = score.toString()
        binding.timePointer.progress=binding.timePointer.max
    }

    private fun colorButtonsWhenOptionClick(button: Int, correctAnswer: Int) {
        when (button) {
            0 -> {
                if (correctAnswer==0) {
                    binding.optionOne.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                } else {
                    binding.optionOne.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    when(correctAnswer){
                        1 -> binding.optionTwo.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        2 -> binding.optionThree.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        3 -> binding.optionFour.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                    }
                }
            }
            1 -> {
                if (correctAnswer==1) {
                    binding.optionTwo.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                } else {
                    binding.optionTwo.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    when(correctAnswer){
                        0 -> binding.optionOne.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        2 -> binding.optionThree.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        3 -> binding.optionFour.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                    }
                }
            }
            2 -> {
                if (correctAnswer==2) {
                    binding.optionThree.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                } else {
                    binding.optionThree.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    when(correctAnswer){
                        0 -> binding.optionOne.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        1 -> binding.optionTwo.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        3 -> binding.optionFour.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                    }
                }
            }
            3 -> {
                if (correctAnswer==3) {
                    binding.optionFour.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                } else {
                    binding.optionFour.backgroundTintList = ColorStateList.valueOf(Color.RED)
                    when(correctAnswer){
                        0 -> binding.optionOne.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        1 -> binding.optionTwo.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                        2 -> binding.optionThree.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                    }
                }
            }
        }
    }

    private fun makeButtonsUncolored(){
        binding.optionOne.backgroundTintList = ColorStateList.valueOf(Color.rgb(80, 53, 55))
        binding.optionTwo.backgroundTintList = ColorStateList.valueOf(Color.rgb(80, 53, 55))
        binding.optionThree.backgroundTintList = ColorStateList.valueOf(Color.rgb(80, 53, 55))
        binding.optionFour.backgroundTintList = ColorStateList.valueOf(Color.rgb(80, 53, 55))
    }

    private fun stopTimer(){
        binding.timePointer.clearAnimation()
    }

    fun setOnQuizFinishedListener(listener: OnQuizFinishedListener) {
        onQuizFinishedListener = listener
    }

}