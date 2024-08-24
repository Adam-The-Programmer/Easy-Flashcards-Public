package pl.lbiio.easyflashcards.games.memory

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
import pl.lbiio.easyflashcards.games.ChooseGameActivity
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.FragmentMemoryCompletedBinding
import pl.lbiio.easyflashcards.model.PackagesViewModel
import pl.lbiio.easyflashcards.model.UsersViewModel
import pl.lbiio.easyflashcards.modules.GlideApp

@AndroidEntryPoint
class MemoryCompletedFragment : Fragment() {
    private lateinit var binding: FragmentMemoryCompletedBinding
    private val packagesViewModel: PackagesViewModel by viewModels()
    private val usersViewModel: UsersViewModel by viewModels()
    private var scoreValue = 0
    private var packageId = ""
    private var nativeLanguage = ""
    private var foreignLanguage = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemoryCompletedBinding.inflate(inflater)
        GlideApp.with(this)
            .asGif()
            .load(R.drawable.success)
            .into(binding.successGif)
        if (arguments != null) {
            scoreValue = requireArguments().getInt("score_value")
            packageId = requireArguments().getString("package_id") ?: ""
            nativeLanguage = requireArguments().getString("native_language") ?: ""
            foreignLanguage = requireArguments().getString("foreign_language") ?: ""
            lifecycleScope.launch(Dispatchers.Main) {

                val bestScore = packagesViewModel.getMemoryBestScore(packageId)
                if (scoreValue > bestScore){
                    packagesViewModel.setMemoryBestScore(packageId, scoreValue)
                    usersViewModel.updateUserPoints(packageId, scoreValue, 3)
                }else{
                    if(scoreValue!=0) usersViewModel.updateUserPoints(packageId, scoreValue, 2)
                }


                if (scoreValue > bestScore) {
                    binding.score.text = getString(R.string.new_best_memory_score, scoreValue.toString())
                } else if (scoreValue == 0) {
                    binding.score.text = getString(R.string.best_memory_score, bestScore.toString())
                } else binding.score.text = getString(R.string.your_memory_score, scoreValue.toString())

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
    }
}
