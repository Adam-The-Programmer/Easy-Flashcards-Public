package pl.lbiio.easyflashcards.games

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.ActivityChooseGameBinding
import pl.lbiio.easyflashcards.games.explore.ExploreCardsActivity
import pl.lbiio.easyflashcards.games.learning.LearnCardsActivity
import pl.lbiio.easyflashcards.games.memory.MemoryActivity
import pl.lbiio.easyflashcards.games.quiz.QuizActivity
import pl.lbiio.easyflashcards.model.FlashcardsViewModel

@AndroidEntryPoint
class ChooseGameActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var radioGroup: RadioGroup
    private var packageId = ""
    private var nativeLanguage=""
    private var foreignLanguage=""
    private lateinit var binding: ActivityChooseGameBinding
    private val cardsViewModel: FlashcardsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.chooseGameToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        radioGroup = binding.gameOptions
        packageId = (savedInstanceState?.getString("package_id") ?: intent.getStringExtra("package_id")) as String
        nativeLanguage = savedInstanceState?.getString("native_language") ?: intent.getStringExtra("native_language").toString()
        foreignLanguage = savedInstanceState?.getString("foreign_language") ?: intent.getStringExtra("foreign_language").toString()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("package_id", packageId)
        outState.putString("native_language", nativeLanguage)
        outState.putString("foreign_language", foreignLanguage)
        super.onSaveInstanceState(outState)
    }

    @Volatile
    var selectedOption = 1
    override fun onStart() {
        super.onStart()
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedOption = checkedId
            when(checkedId) {
                R.id.word_translations -> selectedOption = 1
                R.id.word_explanations -> selectedOption = 2
                R.id.word_phrases -> selectedOption = 3
            }
        }

        binding.exploreCards.setOnClickListener {
            val intent = Intent (this, ExploreCardsActivity::class.java)
            intent.putExtra("package_id", packageId)
            intent.putExtra("game_type", selectedOption)
            intent.putExtra("native_language", nativeLanguage)
            intent.putExtra("foreign_language", foreignLanguage)
            this.startActivity(intent)
        }

        binding.learnCards.setOnClickListener {
            val intent = Intent (this, LearnCardsActivity::class.java)
            intent.putExtra("package_id", packageId)
            intent.putExtra("game_type", selectedOption)
            intent.putExtra("native_language", nativeLanguage)
            intent.putExtra("foreign_language", foreignLanguage)
            this.startActivity(intent)
        }

        binding.startQuiz.setOnClickListener {
            lifecycleScope.launch {
                if(cardsViewModel.canQuizBeStarted(packageId, selectedOption)){
                    val intent = Intent (this@ChooseGameActivity, QuizActivity::class.java)
                    intent.putExtra("package_id", packageId)
                    intent.putExtra("game_type", selectedOption)
                    intent.putExtra("native_language", nativeLanguage)
                    intent.putExtra("foreign_language", foreignLanguage)
                    this@ChooseGameActivity.startActivity(intent)
                }else{
                    Toast.makeText(this@ChooseGameActivity, getString(R.string.too_few_items_to_start_game), Toast.LENGTH_SHORT).show()
                }
            }

        }
        binding.startMemory.setOnClickListener {
            lifecycleScope.launch {
                if(cardsViewModel.canMemoryBeStarted(packageId)){
                    val intent = Intent (this@ChooseGameActivity, MemoryActivity::class.java)
                    intent.putExtra("package_id", packageId)
                    intent.putExtra("native_language", nativeLanguage)
                    intent.putExtra("foreign_language", foreignLanguage)
                    Log.d("start memory package id", packageId)
                    this@ChooseGameActivity.startActivity(intent)
                }else{
                    Toast.makeText(this@ChooseGameActivity, getString(R.string.too_few_items_to_start_game), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}