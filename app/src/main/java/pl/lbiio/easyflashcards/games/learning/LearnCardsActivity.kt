package pl.lbiio.easyflashcards.games.learning

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.ActivityLearnCardsBinding

@AndroidEntryPoint
class LearnCardsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityLearnCardsBinding
    private var packageId = ""
    private var nativeLanguage=""
    private var foreignLanguage=""
    var gameType = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.learnGameToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        packageId = (savedInstanceState?.getString("package_id") ?: intent.getStringExtra("package_id")) as String
        gameType = savedInstanceState?.getInt("game_type", -1) ?: intent.getIntExtra("game_type", -1)
        nativeLanguage = savedInstanceState?.getString("native_language") ?: intent.getStringExtra("native_language").toString()
        foreignLanguage = savedInstanceState?.getString("foreign_language") ?: intent.getStringExtra("foreign_language").toString()
    }

    override fun onStart() {
        super.onStart()
        Log.d("start", "start gry")
        val learningContentFragment = LearningContentFragment()
        val contentBundle = Bundle()
        contentBundle.putString("package_id", packageId)
        contentBundle.putInt("game_type", gameType)
        contentBundle.putString("native_language", nativeLanguage)
        contentBundle.putString("foreign_language", foreignLanguage)
        learningContentFragment.arguments = contentBundle
        supportFragmentManager.beginTransaction().replace(R.id.learn_game_content, learningContentFragment).commit()
        learningContentFragment.setOnLearnGameFinishedListener(
            object : OnLearnGameFinishedListener {
                override fun onLearnGameFinished(score: Int) {
                    val learningCompletedFragment = LearningCompletedFragment()
                    val completedBundle = Bundle()
                    completedBundle.putInt("score_value", score)
                    completedBundle.putString("package_id", packageId)
                    completedBundle.putInt("game_type", gameType)
                    contentBundle.putString("native_language", nativeLanguage)
                    contentBundle.putString("foreign_language", foreignLanguage)
                    learningCompletedFragment.arguments = completedBundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.learn_game_content, learningCompletedFragment).commit()
                }
            }
        )

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("package_id", packageId)
        outState.putInt("game_type", gameType)
        outState.putString("native_language", nativeLanguage)
        outState.putString("foreign_language", foreignLanguage)
        super.onSaveInstanceState(outState)
    }
}
