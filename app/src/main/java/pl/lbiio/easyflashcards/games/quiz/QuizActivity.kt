package pl.lbiio.easyflashcards.games.quiz

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.ActivityQuizBinding

@AndroidEntryPoint
class QuizActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityQuizBinding
    private var packageId = ""
    private var nativeLanguage=""
    private var foreignLanguage=""
    var gameType = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.quizToolbar.root
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
        val quizContentFragment = QuizContentFragment()
        val contentBundle = Bundle()
        contentBundle.putString("package_id", packageId)
        contentBundle.putString("native_language", nativeLanguage)
        contentBundle.putString("foreign_language", foreignLanguage)
        contentBundle.putInt("game_type", gameType)
        quizContentFragment.arguments = contentBundle
        supportFragmentManager.beginTransaction().replace(R.id.quiz_content, quizContentFragment).commit()
        quizContentFragment.setOnQuizFinishedListener(
            object : OnQuizFinishedListener {
                override fun onQuizFinished(score: Int) {
                    val quizCompletedFragment = QuizCompletedFragment()
                    val completedBundle = Bundle()
                    completedBundle.putInt("score_value", score)
                    completedBundle.putString("package_id", packageId)
                    completedBundle.putInt("game_type", gameType)
                    contentBundle.putString("native_language", nativeLanguage)
                    contentBundle.putString("foreign_language", foreignLanguage)
                    quizCompletedFragment.arguments = completedBundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.quiz_content, quizCompletedFragment).commit()
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