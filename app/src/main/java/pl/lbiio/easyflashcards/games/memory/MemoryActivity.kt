package pl.lbiio.easyflashcards.games.memory

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.ActivityMemoryBinding

@AndroidEntryPoint
class MemoryActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityMemoryBinding
    private var packageId = ""
    private var nativeLanguage=""
    private var foreignLanguage=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.memoryToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        packageId = (savedInstanceState?.getString("package_id") ?: intent.getStringExtra("package_id")) as String
        nativeLanguage = savedInstanceState?.getString("native_language") ?: intent.getStringExtra("native_language").toString()
        foreignLanguage = savedInstanceState?.getString("foreign_language") ?: intent.getStringExtra("foreign_language").toString()
    }

    override fun onStart() {
        super.onStart()
        Log.d("start", "start gry")
        val memoryContentFragment = MemoryContentFragment()
        val contentBundle = Bundle()
        contentBundle.putString("package_id", packageId)
        contentBundle.putString("native_language", nativeLanguage)
        contentBundle.putString("foreign_language", foreignLanguage)
        memoryContentFragment.arguments = contentBundle
        supportFragmentManager.beginTransaction().replace(R.id.memory_content, memoryContentFragment).commit()
        memoryContentFragment.setOnMemoryFinishedListener(
            object : OnMemoryFinishedListener {
                override fun onMemoryFinished(score: Int) {
                    val memoryCompletedFragment = MemoryCompletedFragment()
                    val completedBundle = Bundle()
                    completedBundle.putInt("score_value", score)
                    completedBundle.putString("package_id", packageId)
                    contentBundle.putString("native_language", nativeLanguage)
                    contentBundle.putString("foreign_language", foreignLanguage)
                    memoryCompletedFragment.arguments = completedBundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.memory_content, memoryCompletedFragment).commit()
                }
            }
        )

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("package_id", packageId)
        outState.putString("native_language", nativeLanguage)
        outState.putString("foreign_language", foreignLanguage)
        super.onSaveInstanceState(outState)
    }
}