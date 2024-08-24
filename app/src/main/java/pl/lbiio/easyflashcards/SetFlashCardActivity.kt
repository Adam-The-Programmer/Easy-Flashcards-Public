package pl.lbiio.easyflashcards

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.api_classes.FlashcardToUpdateDTO
import pl.lbiio.easyflashcards.data.ExplanationItem
import pl.lbiio.easyflashcards.data.FlashcardToUpdate
import pl.lbiio.easyflashcards.data.PhraseItem
import pl.lbiio.easyflashcards.data.TranslationItem
import pl.lbiio.easyflashcards.databinding.ActivitySetFlashCardBinding
import pl.lbiio.easyflashcards.model.FlashcardsViewModel
import pl.lbiio.easyflashcards.recycle_adapters.ExplanationListAdapter
import pl.lbiio.easyflashcards.recycle_adapters.OnDeleteExplanationClickListener
import pl.lbiio.easyflashcards.recycle_adapters.OnDeletePhraseClickListener
import pl.lbiio.easyflashcards.recycle_adapters.OnDeleteTranslationClickListener
import pl.lbiio.easyflashcards.recycle_adapters.OnExplanationChangeListener
import pl.lbiio.easyflashcards.recycle_adapters.OnPhraseChangeListener
import pl.lbiio.easyflashcards.recycle_adapters.OnTranslationChangeListener
import pl.lbiio.easyflashcards.recycle_adapters.PhraseListAdapter
import pl.lbiio.easyflashcards.recycle_adapters.TranslationListAdapter

@AndroidEntryPoint
class SetFlashCardActivity : AppCompatActivity() {
    private val flashcardsViewModel: FlashcardsViewModel by viewModels()
    private lateinit var toolbar: Toolbar
    private lateinit var binding: ActivitySetFlashCardBinding
    private var packageId = ""
    private var cardId = ""
    private var status = -1
    private var isTranslationKnown = false
    private var isExplanationKnown = false
    private var isPhraseKnown = false
    private lateinit var recyclerViewTranslations: RecyclerView
    private lateinit var recyclerViewExplanations: RecyclerView
    private lateinit var recyclerViewPhrases: RecyclerView
    private lateinit var translationAdapter: TranslationListAdapter
    private lateinit var explanationAdapter: ExplanationListAdapter
    private lateinit var phraseAdapter: PhraseListAdapter
    private var flashcardToUpdate: FlashcardToUpdate? = null
    private var listOfExplanations: ArrayList<ExplanationItem> = ArrayList(emptyList())
    private var listOfTranslations: ArrayList<TranslationItem> =
        ArrayList(listOf(TranslationItem("")))
    private var listOfPhrases: ArrayList<PhraseItem> = ArrayList(emptyList())
    private var loadedExplanations = 0
    private var loadedPhrases = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_flash_card)
        setContentView(binding.root)
        toolbar = binding.setFlashCardToolbar.root
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        status = intent.getIntExtra("status", -1)
        packageId = intent.getStringExtra("package_id")?:""
        cardId = intent.getStringExtra("card_id")?:""
        isTranslationKnown = intent.getBooleanExtra("is_translation_known", false)
        isExplanationKnown = intent.getBooleanExtra("is_explanation_known", false)
        isPhraseKnown = intent.getBooleanExtra("is_phrase_known", false)
        recyclerViewTranslations = binding.translationsRecyclerview
        recyclerViewExplanations = binding.explanationsRecyclerview
        recyclerViewPhrases = binding.phrasesRecyclerview
        binding.translationsRecyclerview.overScrollMode = View.OVER_SCROLL_NEVER
        binding.explanationsRecyclerview.overScrollMode = View.OVER_SCROLL_NEVER
        binding.phrasesRecyclerview.overScrollMode = View.OVER_SCROLL_NEVER
        translationAdapter = TranslationListAdapter(listOfTranslations, status)
        explanationAdapter = ExplanationListAdapter(listOfExplanations, status)
        phraseAdapter = PhraseListAdapter(listOfPhrases, status)

            binding.wordInput.isEnabled = status==1 || status==2 || status==-1
            binding.addTranslation.isEnabled = status==1 || status==2 || status==-1
            binding.addExplanation.isEnabled = status==1 || status==2 || status==-1
            binding.addPhrase.isEnabled = status==1 || status==2 || status==-1
            binding.applyFlashCard.isEnabled = status==1 || status==2 || status==-1


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        if (cardId != "") {
            lifecycleScope.launch(Dispatchers.Main) {
                flashcardToUpdate = flashcardsViewModel.getFlashcardToUpdate(cardId)
                binding.word.setText(flashcardToUpdate!!.word)
                listOfTranslations = convertStringToList(flashcardToUpdate!!.translations) { TranslationItem(it) }
                translationAdapter.updateList(listOfTranslations)
                translationAdapter.notifyDataSetChanged()
                listOfExplanations = convertStringToList(flashcardToUpdate!!.explanations) { ExplanationItem(it) }
                explanationAdapter.updateList(listOfExplanations)
                explanationAdapter.notifyDataSetChanged()
                listOfPhrases = convertStringToList(flashcardToUpdate!!.phrases) { PhraseItem(it) }
                phraseAdapter.updateList(listOfPhrases)
                phraseAdapter.notifyDataSetChanged()
                loadedExplanations = listOfExplanations.size
                loadedPhrases = listOfPhrases.size
            }
        }

        displayLists(this)
        binding.addTranslation.setOnClickListener {
            listOfTranslations.add(TranslationItem(""))
            translationAdapter.updateList(listOfTranslations)
            translationAdapter.notifyDataSetChanged()
        }

        binding.addExplanation.setOnClickListener {
            listOfExplanations.add(ExplanationItem(""))
            explanationAdapter.updateList(listOfExplanations)
            explanationAdapter.notifyDataSetChanged()
        }

        binding.addPhrase.setOnClickListener {
            listOfPhrases.add(PhraseItem(""))
            phraseAdapter.updateList(listOfPhrases)
            phraseAdapter.notifyDataSetChanged()
        }

        binding.dismissFlashCard.setOnClickListener {
            finish()
        }

        binding.applyFlashCard.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                val word = binding.word.text.toString()
                var translationsRaw = ""
                var explanationsRaw = ""
                var phrasesRaw = ""
                listOfTranslations.forEach {
                    translationsRaw += "${it.translation}|"
                }
                val translations = cleanText(translationsRaw)

                var explanations = ""
                if (listOfExplanations.size > 0) {
                    listOfExplanations.forEach {
                        explanationsRaw += "${it.explanation}|"
                    }
                    explanations = cleanText(explanationsRaw)
                }

                var phrases = ""
                if (listOfPhrases.size > 0) {
                    listOfPhrases.forEach {
                        phrasesRaw += "${it.phrase}|"
                    }
                    phrases = cleanText(phrasesRaw)
                }

                if (word == "" || translations == "") Toast.makeText(
                    this@SetFlashCardActivity,
                    getString(R.string.at_least_one_translation),
                    Toast.LENGTH_SHORT
                ).show()
                else {
                    if (cardId == "") {
                        this.launch {
                            withContext(Dispatchers.IO){
                                Log.d("status fiszki", status.toString())
                                flashcardsViewModel.addFlashcard(status, FlashcardToUpdateDTO(packageId, "${flashcardsViewModel.getCurrentUserId()}${System.currentTimeMillis()}", word, translations, explanations, phrases))
                            }
                        }

                    } else {
                        this.launch {
                            withContext(Dispatchers.IO){
                                flashcardsViewModel.updateCard(status, FlashcardToUpdateDTO(packageId, cardId, word, translations, explanations, phrases))
                            }
                        }
                    }
                    finish()
                }
            }
        }
    }

    private fun displayLists(ctx: Context) {
        translationAdapter = TranslationListAdapter(listOfTranslations, status)
        recyclerViewTranslations.adapter = translationAdapter
        recyclerViewTranslations.layoutManager = LinearLayoutManager(ctx)
        recyclerViewTranslations.setHasFixedSize(true)
        translationAdapter.setOnDeleteTranslationClickListener(
            object : OnDeleteTranslationClickListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDeleteTranslationClick(list_position: Int) {
                    if (listOfTranslations.size > 1 && listOfTranslations[list_position].translation == "") {
                        listOfTranslations.removeAt(list_position)
                        translationAdapter.updateList(listOfTranslations)
                        translationAdapter.notifyDataSetChanged()
                    } else {
                        listOfTranslations[list_position].translation = ""
                        translationAdapter.notifyDataSetChanged()
                    }
                }
            }
        )
        translationAdapter.setOnTranslationChangeListener(
            object : OnTranslationChangeListener {
                override fun onTranslationChange(list_position: Int, new_text: CharSequence?) {
                    listOfTranslations[list_position].translation = new_text.toString()
                }

            }
        )

        explanationAdapter = ExplanationListAdapter(listOfExplanations, status)
        recyclerViewExplanations.adapter = explanationAdapter
        recyclerViewExplanations.layoutManager = LinearLayoutManager(ctx)
        recyclerViewExplanations.setHasFixedSize(true)
        explanationAdapter.setOnDeleteExplanationClickListener(
            object : OnDeleteExplanationClickListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDeleteExplanationClick(list_position: Int) {
                    if (listOfExplanations[list_position].explanation == "") {
                        listOfExplanations.removeAt(list_position)
                        explanationAdapter.updateList(listOfExplanations)
                        explanationAdapter.notifyDataSetChanged()
                    } else {
                        listOfExplanations[list_position].explanation = ""
                        explanationAdapter.notifyDataSetChanged()
                    }
                }
            }
        )
        explanationAdapter.setOnExplanationChangeListener(
            object : OnExplanationChangeListener {
                override fun onExplanationChange(list_position: Int, new_text: CharSequence?) {
                    listOfExplanations[list_position].explanation = new_text.toString()
                }
            }
        )

        phraseAdapter = PhraseListAdapter(listOfPhrases, status)
        recyclerViewPhrases.adapter = phraseAdapter
        recyclerViewPhrases.layoutManager = LinearLayoutManager(ctx)
        recyclerViewPhrases.setHasFixedSize(true)
        phraseAdapter.setOnDeletePhraseClickListener(
            object : OnDeletePhraseClickListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDeletePhraseClick(list_position: Int) {
                    if (listOfPhrases[list_position].phrase == "") {
                        listOfPhrases.removeAt(list_position)
                        phraseAdapter.updateList(listOfPhrases)
                        phraseAdapter.notifyDataSetChanged()
                    } else {
                        listOfPhrases[list_position].phrase = ""
                        phraseAdapter.notifyDataSetChanged()
                    }
                }
            }
        )
        phraseAdapter.setOnPhraseChangeListener(
            object : OnPhraseChangeListener {
                override fun onPhraseChange(list_position: Int, new_text: CharSequence?) {
                    listOfPhrases[list_position].phrase = new_text.toString()
                }
            }
        )
    }

    private fun cleanText(rawText: String): String {
        val singleDelimiter = rawText.replace(Regex("\\|+"), "|")
        return singleDelimiter.substring(0, singleDelimiter.length - 1)
    }

    private fun <T> convertStringToList(str: String, constructor: (String) -> T): ArrayList<T> {
        val stringArray = str.split("|").toTypedArray()
        val objectList = ArrayList<T>()
        if (stringArray.size == 1 && stringArray[0] == "") {
            return ArrayList(emptyList())
        }
        for (element in stringArray) {
            val obj = constructor(element)
            objectList.add(obj)
        }
        return objectList
    }
}