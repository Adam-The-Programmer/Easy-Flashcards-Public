package pl.lbiio.easyflashcards.games.learning

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.lbiio.easyflashcards.AppDialog
import pl.lbiio.easyflashcards.games.ChooseGameActivity
import pl.lbiio.easyflashcards.OnNegativeButtonClickListener
import pl.lbiio.easyflashcards.OnPositiveButtonClickListener
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.FragmentLearningCompletedBinding
import pl.lbiio.easyflashcards.model.PackagesViewModel
import pl.lbiio.easyflashcards.model.UsersViewModel
import pl.lbiio.easyflashcards.modules.GlideApp

@AndroidEntryPoint
class LearningCompletedFragment : Fragment() {

    private lateinit var binding: FragmentLearningCompletedBinding
    private val packagesViewModel: PackagesViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()
    private var scoreValue = 0
    private var packageId = ""
    private var gameType = -1
    private var nativeLanguage = ""
    private var foreignLanguage = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningCompletedBinding.inflate(inflater)
        GlideApp.with(this)
            .asGif()
            .load(R.drawable.success)
            .into(binding.successGif)
        if (arguments != null) {
            scoreValue = requireArguments().getInt("score_value")
            packageId = requireArguments().getString("package_id")?:""
            nativeLanguage = requireArguments().getString("native_language") ?: ""
            foreignLanguage = requireArguments().getString("foreign_language") ?: ""
            gameType = requireArguments().getInt("game_type")
            lifecycleScope.launch(Dispatchers.Main){

                //packagesViewModel.setLearningCompleted(packageId, gameType)
                val bestScore = packagesViewModel.getLearningBestScore(packageId, gameType)
                if(scoreValue > bestScore){
                    packagesViewModel.setLearningBestScore(packageId, scoreValue, gameType)
                    usersViewModel.updateUserPoints(packageId, scoreValue, 3)
                }else{
                    if(scoreValue!=0) usersViewModel.updateUserPoints(packageId, scoreValue, 2)
                }


                when(gameType){
                    1 -> {
                        if(scoreValue > bestScore){
                            binding.score.text = getString(R.string.new_best_translation_score, scoreValue.toString())
                        }
                        else if(scoreValue==0){
                            binding.score.text = getString(R.string.best_translations_score, bestScore.toString())
                        }
                        else binding.score.text = getString(R.string.your_translations_score, scoreValue.toString())
                    }
                    2 -> {

                        if(scoreValue > bestScore) {
                            binding.score.text = getString(R.string.new_best_explanations_score, scoreValue.toString())
                        }
                        else if(scoreValue==0){
                            binding.score.text = getString(R.string.best_explanations_score, bestScore.toString())
                        }
                        else binding.score.text = getString(R.string.your_explanations_score, scoreValue.toString())
                    }
                    3 -> {
                        if(scoreValue > bestScore){
                            binding.score.text = getString(R.string.new_best_phrases_score, scoreValue.toString())
                        }
                        else if(scoreValue==0){
                            binding.score.text = getString(R.string.best_phrases_score, bestScore.toString())
                        }
                        else binding.score.text = getString(R.string.your_phrases_score, scoreValue.toString())
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backToMenu.setOnClickListener {
            val intent = Intent(activity, ChooseGameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("package_id", packageId)
            intent.putExtra("native_language", nativeLanguage)
            intent.putExtra("foreign_language", foreignLanguage)
            startActivity(intent)
        }

        binding.playAgain.setOnClickListener {
            val removeCardDialog = AppDialog(
                requireContext(),
                "Resetting Progress",
                "Playing again will clear your current progress",
                true,
                null
            )
            removeCardDialog.setOnPositiveButtonClickListener(
                object : OnPositiveButtonClickListener {
                    override fun onPositiveButtonClick() {
                        lifecycleScope.launch(Dispatchers.IO){
                            packagesViewModel.resetKnowledgeLevel(packageId, gameType)
                        }
                        val intent = Intent(activity, LearnCardsActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra("package_id", packageId)
                        intent.putExtra("native_language", nativeLanguage)
                        intent.putExtra("foreign_language", foreignLanguage)
                        intent.putExtra("game_type", gameType)
                        activity?.finish()
                        startActivity(intent)

                    }
                }
            )
            removeCardDialog.setOnNegativeButtonClickListener(
                object : OnNegativeButtonClickListener {
                    override fun onNegativeButtonClick() {
                        removeCardDialog.dismiss()
                    }
                }
            )
            removeCardDialog.show(
                childFragmentManager,
                "removing card dialog"
            )
        }
    }

}