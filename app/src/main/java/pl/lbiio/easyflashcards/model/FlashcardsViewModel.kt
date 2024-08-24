package pl.lbiio.easyflashcards.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.api_classes.CardFromBackup
import pl.lbiio.easyflashcards.api_classes.FlashcardToUpdateDTO
import pl.lbiio.easyflashcards.data.MethodCall
import pl.lbiio.easyflashcards.data.ExploreGameItem
import pl.lbiio.easyflashcards.data.FlashcardToDisplayInList
import pl.lbiio.easyflashcards.repository.FlashcardsApiRepository
import pl.lbiio.easyflashcards.repository.FlashcardsRoomRepository
import pl.lbiio.easyflashcards.repository.UncalledMethodsRepository
import javax.inject.Inject

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val flashcardsRoomRepository: FlashcardsRoomRepository,
    private val flashcardsApiRepository: FlashcardsApiRepository,
    private val uncalledMethodsRepository: UncalledMethodsRepository
) :
    ViewModel() {
    private val gson = Gson()

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().uid.toString()
    }

    private var _flashCardListFromPackage =
        MutableStateFlow<List<FlashcardToDisplayInList>>(emptyList())
    val flashCardListFromPackage = _flashCardListFromPackage.asStateFlow()

    private val _wordToTranslations = MutableStateFlow<List<ExploreGameItem>>(emptyList())
    val wordToTranslations = _wordToTranslations.asStateFlow()

    private val _wordToExplanations = MutableStateFlow<List<ExploreGameItem>>(emptyList())
    val wordToExplanations = _wordToExplanations.asStateFlow()

    private val _wordToPhrases = MutableStateFlow<List<ExploreGameItem>>(emptyList())
    val wordToPhrases = _wordToPhrases.asStateFlow()


    suspend fun updateCard(
        Status: Int,
        Card: FlashcardToUpdateDTO
    ) {
        flashcardsRoomRepository.updateFlashcard(Card)
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(MethodCall("updateCard", listOf(Status, Card)))
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun addFlashcard(
        Status: Int,
        Card: FlashcardToUpdateDTO
    ) {
        flashcardsRoomRepository.addFlashcard(Card)
        val methodToInvokeJSON =
            gson.toJson(MethodCall("addFlashcard", listOf(getCurrentUserId(), Status, Card)))
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }


    suspend fun addBackupFlashcard(cardFromBackup: CardFromBackup, packageID: String) =
        withContext(Dispatchers.IO) {
            flashcardsRoomRepository.addBackupFlashcard(cardFromBackup, packageID)
        }


    suspend fun updateImportance(
        CardID: String,
        isImportant: Boolean
    ) {
        flashcardsRoomRepository.updateImportance(isImportant, CardID)
        val methodToInvokeJSON = gson.toJson(
            MethodCall(
                "updateImportance",
                listOf(getCurrentUserId(), CardID, isImportant)
            )
        )
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }


    suspend fun setKnowledgeLevel(
        CardID: String,
        gameType: Int,
        value: Int
    ) {
        flashcardsRoomRepository.setKnowledgeLevel(CardID, gameType, value)
        val methodToInvokeJSON = gson.toJson(
            MethodCall(
                "setKnowledgeLevel",
                listOf(getCurrentUserId(), CardID, gameType, value)
            )
        )
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }


    suspend fun updateCardParametersWhenKnowClicked(
        packageID: String,
        cardID: String,
        gameType: Int,
        scoreValue: Int
    ) {
        flashcardsRoomRepository.updateCardParametersWhenKnowClicked(
            packageID,
            cardID,
            gameType,
            scoreValue
        )
        val methodToInvokeJSON = gson.toJson(
            MethodCall(
                "updateCardParametersWhenKnowClicked",
                listOf(getCurrentUserId(), packageID, cardID, gameType, scoreValue)
            )
        )
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }


    suspend fun deleteFlashcard(
        CardID: String,
        PackageID: String,
        Status: Int
    ) {
        flashcardsRoomRepository.deleteFlashcard(PackageID, CardID)
        val methodToInvokeJSON =
            gson.toJson(MethodCall("deleteFlashcard", listOf(CardID, PackageID, Status)))
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    fun loadWordToTranslationsExploreGame(packageID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            flashcardsRoomRepository.getWordToTranslationExploreGameItems(packageID)
                .distinctUntilChanged()
                .collect { item ->
                    _wordToTranslations.value = item
                }
        }
    }

    fun loadWordToExplanations(packageID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            flashcardsRoomRepository.getWordToExplanationExploreGameItems(packageID)
                .distinctUntilChanged()
                .collect { item ->
                    _wordToExplanations.value = item
                }
        }
    }

    fun loadWordToPhrases(packageID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            flashcardsRoomRepository.getWordToPhraseExploreGameItems(packageID)
                .distinctUntilChanged()
                .collect { item ->
                    _wordToPhrases.value = item
                }
        }
    }

    fun loadCardsFromPackage(packageID: String) {
        viewModelScope.launch(Dispatchers.IO) {
            flashcardsRoomRepository.getFlashcardsToDisplay(packageID).distinctUntilChanged()
                .collect { listOfFlashCards ->
                    if (listOfFlashCards.isEmpty()) {
                        _flashCardListFromPackage.value = emptyList()
                    } else {
                        _flashCardListFromPackage.value = listOfFlashCards
                    }
                }
        }
    }

    suspend fun getFlashcardToUpdate(cardID: String) = withContext(Dispatchers.IO) {
        flashcardsRoomRepository.getFlashcardToUpdate(cardID)
    }

    suspend fun getWordToTranslationLearnGameItems(packageID: String, limit: Int) =
        withContext(Dispatchers.IO) {
            flashcardsRoomRepository.getWordToTranslationLearnGameItems(packageID, limit)
        }

    suspend fun getWordToExplanationsLearnGameItems(packageID: String, limit: Int) =
        withContext(Dispatchers.IO) {
            flashcardsRoomRepository.getWordToExplanationsLearnGameItems(packageID, limit)
        }

    suspend fun getWordToPhrasesLearnGameItems(packageID: String, limit: Int) =
        withContext(Dispatchers.IO) {
            flashcardsRoomRepository.getWordToPhrasesLearnGameItems(packageID, limit)
        }

    suspend fun updateChangedCards(packageID: String) = withContext(Dispatchers.IO) {
        val response = flashcardsApiRepository.getChangedFlashcards(getCurrentUserId(), packageID)
        if (!response.isNullOrEmpty()) {
            Log.d("zmienione fiszki w pakiecie", "$packageID, $response")
            val actions = response.split(',')
            for (action in actions) {
                val parts = action.split(':')
                val cardID = parts[0]
                when (parts[1]) {
                    "up" -> {
                        Log.d("aktualizowanie", "tak")
                        val card = flashcardsApiRepository.getChangedFlashcardToSet(cardID)
                        card?.let {
                            flashcardsApiRepository.updateCard(3, it)
                            flashcardsRoomRepository.updateFlashcard(it)
                        }
                        flashcardsApiRepository.notifyCardChange(
                            getCurrentUserId(),
                            packageID,
                            cardID,
                            "up"
                        )
                    }

                    "ins" -> {
                        val card = flashcardsApiRepository.getChangedFlashcardToSet(cardID)
                        card?.let {
                            flashcardsApiRepository.addFlashcard(getCurrentUserId(), 3, it)
                            flashcardsRoomRepository.addFlashcard(it)
                        }
                        flashcardsApiRepository.notifyCardChange(
                            getCurrentUserId(),
                            packageID,
                            cardID,
                            "ins"
                        )
                    }

                    "del" -> {
                        val card = flashcardsApiRepository.getChangedFlashcardToSet(cardID)
                        card?.let {
                            flashcardsApiRepository.deleteFlashcard(cardID, packageID, 3)
                            flashcardsRoomRepository.deleteFlashcard(packageID, cardID)
                        }
                        flashcardsApiRepository.notifyCardChange(
                            getCurrentUserId(),
                            packageID,
                            cardID,
                            "del"
                        )
                    }

                    else -> println("Unknown action type")
                }
            }
        }
    }

    suspend fun getBackupFlashcards(
        PackageID: String
    ) = withContext(Dispatchers.IO) {
        flashcardsApiRepository.getBackupFlashcards(getCurrentUserId(), PackageID).body()
    }

    suspend fun getBoughtFlashcards(
        PackageID: String
    ) = withContext(Dispatchers.IO) {
        flashcardsApiRepository.getBoughtFlashcards(PackageID).body()
    }

    suspend fun getCurrentData(packageID: String, gameType: Int) = withContext(Dispatchers.IO) {
        flashcardsRoomRepository.getCurrentData(packageID, gameType)
    }

    suspend fun isInternetAvailable(isAvailable: () -> Unit, isNotAvailable: () -> Unit) {
        uncalledMethodsRepository.isInternetAvailable(isAvailable, isNotAvailable)
    }

    suspend fun getQuizQuestionsWithCorrectAnswers(packageID: String, gameType: Int) =
        flashcardsRoomRepository.getQuizQuestionsWithCorrectAnswers(packageID, gameType)

    suspend fun getQuizIncorrectAnswers(packageID: String, cardID: String) =
        flashcardsRoomRepository.getQuizIncorrectAnswers(packageID, cardID)

    suspend fun getMemoryCards(packageID: String) =
        flashcardsRoomRepository.getMemoryCards(packageID)

    suspend fun canQuizBeStarted(packageId: String, gameType: Int) =
        flashcardsRoomRepository.canQuizBeStarted(packageId, gameType)

    suspend fun canMemoryBeStarted(packageId: String) =
        flashcardsRoomRepository.canMemoryBeStarted(packageId)
}