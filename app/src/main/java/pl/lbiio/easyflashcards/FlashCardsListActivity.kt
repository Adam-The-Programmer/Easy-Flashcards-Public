package pl.lbiio.easyflashcards

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.lbiio.easyflashcards.data.FlashcardToDisplayInList
import pl.lbiio.easyflashcards.databinding.ActivityFlashCardsListBinding
import pl.lbiio.easyflashcards.games.ChooseGameActivity
import pl.lbiio.easyflashcards.model.FlashcardsViewModel
import pl.lbiio.easyflashcards.recycle_adapters.FlashCardListAdapter
import pl.lbiio.easyflashcards.recycle_adapters.OnCardClickListener
import pl.lbiio.easyflashcards.recycle_adapters.OnImportanceStatusClickListener
import pl.lbiio.easyflashcards.recycle_adapters.OnRemoveClickListener

@AndroidEntryPoint
class FlashCardsListActivity : AppCompatActivity() {
    private val flashcardsViewModel: FlashcardsViewModel by viewModels()
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivityFlashCardsListBinding
    private var packageId = ""
    private var status =-1
    private var nativeLanguage=""
    private var foreignLanguage=""
    private lateinit var recyclerViewFlashCards: RecyclerView
    private lateinit var flashCardAdapter: FlashCardListAdapter
    private var listOfFlashCards: List<FlashcardToDisplayInList> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_flash_cards_list)
        setContentView(binding.root)
        toolbar = binding.flashCardsToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        packageId = (savedInstanceState?.getString("package_id") ?: intent.getStringExtra("package_id")) as String
        status = savedInstanceState?.getInt("status", -1) ?: intent.getIntExtra("status", -1)
        nativeLanguage = savedInstanceState?.getString("native_language") ?: intent.getStringExtra("native_language").toString()
        foreignLanguage = savedInstanceState?.getString("foreign_language") ?: intent.getStringExtra("foreign_language").toString()
        Log.d("pobrany status pakietu", status.toString())
        recyclerViewFlashCards = binding.recyclerview
        binding.recyclerview.overScrollMode = View.OVER_SCROLL_NEVER
        flashCardAdapter = FlashCardListAdapter(listOfFlashCards)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("package_id", packageId)
        outState.putString("native_language", nativeLanguage)
        outState.putString("foreign_language", foreignLanguage)
        outState.putInt("status", status)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        observeViewStateUpdates(this)
        if(status==3) binding.addFlashCardFAB.visibility = GONE
        if(status==1 || status==2) binding.addFlashCardFAB.visibility = VISIBLE

        binding.addFlashCardFAB.setOnClickListener {
            val intent = Intent (this, SetFlashCardActivity::class.java)
            intent.putExtra("package_id", packageId)
            intent.putExtra("status", status)
            this.startActivity(intent)
        }

        binding.playFlashCardsFAB.setOnClickListener {
            if(listOfFlashCards.isNotEmpty()) {
                val intent = Intent (this, ChooseGameActivity::class.java)
                intent.putExtra("package_id", packageId)
                //intent.putExtra("type", type)
                intent.putExtra("native_language", nativeLanguage)
                intent.putExtra("foreign_language", foreignLanguage)
                this.startActivity(intent)
            }
            else{
                Toast.makeText(this, getString(R.string.play_cards_requirement_info), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun observeViewStateUpdates(ctx: Context) {
        flashcardsViewModel.loadCardsFromPackage(packageId)
        lifecycleScope.launch {
            flashcardsViewModel.flashCardListFromPackage.collect {
                flashCardAdapter = FlashCardListAdapter(listOfFlashCards)
                recyclerViewFlashCards.adapter = flashCardAdapter
                recyclerViewFlashCards.layoutManager = LinearLayoutManager(ctx)
                recyclerViewFlashCards.setHasFixedSize(true)
                listOfFlashCards = flashcardsViewModel.flashCardListFromPackage.value
                binding.amountInfo.text = getString(R.string.amount_of_cards, listOfFlashCards.size.toString())
                flashCardAdapter.updateList(listOfFlashCards)
                flashCardAdapter.setOnCardClickListener(
                    object : OnCardClickListener {
                        override fun onCardClick(db_position: String, isTranslationKnown: Boolean, isExplanationKnown: Boolean, isPhraseKnown: Boolean) {
                            val intent = Intent (ctx, SetFlashCardActivity::class.java)
                            intent.putExtra("package_id", packageId)
                            intent.putExtra("card_id", db_position)
                            intent.putExtra("status", status)
                            intent.putExtra("is_translation_known", isTranslationKnown)
                            intent.putExtra("is_explanation_known", isExplanationKnown)
                            intent.putExtra("is_phrase_known", isPhraseKnown)
                            ctx.startActivity(intent)
                        }
                    }
                )
                flashCardAdapter.setOnRemoveClickListener(
                    object : OnRemoveClickListener {
                        override fun onRemoveClick(db_position: String, cardMaxKnowledgeLevel: Int, isTranslationKnown: Boolean, isExplanationKnown: Boolean, isPhraseKnown: Boolean) {
                            val removeCardDialog = AppDialog(
                                ctx,
                                getString(R.string.removing_card_dialog_title),
                                getString(R.string.removing_card_dialog_content),
                                true,
                                null
                            )
                            removeCardDialog.setOnPositiveButtonClickListener(
                                object : OnPositiveButtonClickListener {
                                    override fun onPositiveButtonClick() {
                                        lifecycleScope.launch(Dispatchers.IO){
                                            flashcardsViewModel.deleteFlashcard(db_position, packageId, status)
                                        }
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
                                supportFragmentManager,
                                getString(R.string.removing_card_dialog_tag)
                            )
                        }
                    }
                )
                flashCardAdapter.setOnImportanceStatusClickListener(
                    object : OnImportanceStatusClickListener {
                        override fun onImportanceStatusClick(db_position: String, importance: Boolean) {
                            lifecycleScope.launch(Dispatchers.IO){
                                flashcardsViewModel.updateImportance(db_position, !importance)
                            }
                        }
                    }
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cards_list_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.package_leaders_item -> {
                val intent = Intent (this, PackageLeaderBoardActivity::class.java)
                intent.putExtra("package_id", packageId)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}