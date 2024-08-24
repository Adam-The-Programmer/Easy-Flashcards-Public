package pl.lbiio.easyflashcards.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.api_classes.CardFromBackup
import pl.lbiio.easyflashcards.api_classes.FlashcardToUpdateDTO
import pl.lbiio.easyflashcards.data.ExploreGameItem
import pl.lbiio.easyflashcards.data.FlashcardToDisplayInList
import pl.lbiio.easyflashcards.data.FlashcardToUpdate
import pl.lbiio.easyflashcards.data.LearnGameItem
import pl.lbiio.easyflashcards.data.MemoryCard
import pl.lbiio.easyflashcards.data.QuizOption
import pl.lbiio.easyflashcards.data.QuizQuestion
import pl.lbiio.easyflashcards.db_access.EasyFlashcardsDao
import pl.lbiio.easyflashcards.tables.Flashcard
import pl.lbiio.easyflashcards.tables.LearningCardProgress
import javax.inject.Inject

class FlashcardsRoomRepository @Inject constructor(private val easyFlashcardsDao: EasyFlashcardsDao){

    suspend fun updateFlashcard(card: FlashcardToUpdateDTO) = coroutineScope {
        val currentCard = withContext(Dispatchers.IO) {
            easyFlashcardsDao.getFlashcardToModify(card.cardID)
        }
        var value=0
        if(currentCard.phrases.isNotEmpty() && card.phrases.isEmpty()) value--
        if(currentCard.explanations.isNotEmpty() && card.explanations.isEmpty())value--
        if(currentCard.explanations.isEmpty() && card.explanations.isNotEmpty())value++
        if(currentCard.phrases.isEmpty() && card.phrases.isNotEmpty())value++
        if(currentCard.phrases != card.phrases){
            easyFlashcardsDao.setIsPhraseKnown(card.cardID, false)
        }
        if(currentCard.translations != card.translations){
            easyFlashcardsDao.setIsTranslationKnown(card.cardID, false)
        }
        if(currentCard.explanations != card.explanations){
            easyFlashcardsDao.setIsExplanationKnown(card.cardID, false)
        }
        easyFlashcardsDao.updateFlashcard(card.cardID, card.word, card.translations, card.explanations, card.phrases)
        easyFlashcardsDao.incrementMaxKnowledgeLevel(card.packageID, value)
    }


    suspend fun addFlashcard(card: FlashcardToUpdateDTO) = coroutineScope {
        easyFlashcardsDao.addFlashcard(Flashcard(card.cardID, card.packageID, card.word, card.translations, card.explanations, card.phrases))
        easyFlashcardsDao.initLearningFlashcardProgress(LearningCardProgress(foreignCardID = card.cardID))
        var value = 1
        if(card.explanations.isNotEmpty()) value++
        if(card.phrases.isNotEmpty()) value++
        easyFlashcardsDao.incrementMaxKnowledgeLevel(card.packageID, value)
    }



    suspend fun addBackupFlashcard(card: CardFromBackup, packageId: String) = coroutineScope {
        easyFlashcardsDao.addFlashcard(
            Flashcard(
                card.cardID,
                packageId,
                card.word,
                card.translations,
                card.explanations,
                card.phrases,
                card.isImportant
            )
        )
        easyFlashcardsDao.initLearningFlashcardProgress(
            LearningCardProgress(
                foreignCardID = card.cardID,
                translationKnowledgeLevel = card.translationKnowledgeLevel,
                explanationKnowledgeLevel = card.explanationKnowledgeLevel,
                phraseKnowledgeLevel = card.phraseKnowledgeLevel,
                isLearningTranslationKnown = card.isLearningTranslationKnown,
                isLearningExplanationKnown = card.isLearningExplanationKnown,
                isLearningPhraseKnown = card.isLearningPhraseKnown
            )
        )
    }

    suspend fun updateImportance(importance: Boolean, cardID: String) = coroutineScope {easyFlashcardsDao.updateImportance(importance, cardID) }


    suspend fun setKnowledgeLevel(cardID: String, gameType: Int, value: Int){
        when(gameType){
            1-> easyFlashcardsDao.setTranslationKnowledgeLevel(cardID, value)
            2-> easyFlashcardsDao.setExplanationKnowledgeLevel(cardID, value)
            3-> easyFlashcardsDao.setPhraseKnowledgeLevel(cardID, value)
        }
    }

    suspend fun updateCardParametersWhenKnowClicked(
        packageID: String,
        cardID: String,
        gameType: Int,
        scoreValue: Int
    ) = coroutineScope {
        when (gameType) {
            1 -> {
                easyFlashcardsDao.setIsTranslationKnown(cardID, true)
                easyFlashcardsDao.setLearningTranslationsCurrentScore(packageID, scoreValue)
            }

            2 -> {
                easyFlashcardsDao.setIsExplanationKnown(cardID, true)
                easyFlashcardsDao.setLearningExplanationsCurrentScore(packageID, scoreValue)

            }

            3 -> {
                easyFlashcardsDao.setIsPhraseKnown(cardID, true)
                easyFlashcardsDao.setLearningPhrasesCurrentScore(packageID, scoreValue)
            }
        }
    }

    /* deleting methods */

    suspend fun deleteFlashcard(packageID: String, cardID: String) = coroutineScope {
        val flashcard = withContext(Dispatchers.Default) {
            easyFlashcardsDao.getFlashcardToModify(cardID)
        }
        var value=1
        if(flashcard.explanations.isNotEmpty()) value++
        if(flashcard.phrases.isNotEmpty()) value++
        easyFlashcardsDao.deleteLearningCardProgress(cardID)
        easyFlashcardsDao.deleteFlashcardById(cardID)
        easyFlashcardsDao.decrementMaxKnowledgeLevel(packageID, value)
    }

    fun getFlashcardsToDisplay(packageID: String): Flow<List<FlashcardToDisplayInList>> = easyFlashcardsDao.getFlashcardsToDisplay(packageID).flowOn(
        Dispatchers.IO).conflate()

    fun getWordToTranslationExploreGameItems(packageID: String): Flow<List<ExploreGameItem>> = easyFlashcardsDao.getWordToTranslationExploreGameItems(packageID).flowOn(
        Dispatchers.IO).conflate()

    fun getWordToExplanationExploreGameItems(packageID: String): Flow<List<ExploreGameItem>> = easyFlashcardsDao.getWordToExplanationExploreGameItems(packageID).flowOn(
        Dispatchers.IO).conflate()

    fun getWordToPhraseExploreGameItems(packageID: String): Flow<List<ExploreGameItem>> = easyFlashcardsDao.getWordToPhraseExploreGameItems(packageID).flowOn(
        Dispatchers.IO).conflate()

    suspend fun getFlashcardToUpdate(cardID: String): FlashcardToUpdate = coroutineScope {
        async{
            easyFlashcardsDao.getFlashcardToModify(cardID)
        }
    }.await()


    //getting methods

    suspend fun getWordToTranslationLearnGameItems(packageID: String,  limit: Int): List<LearnGameItem> = coroutineScope {
        async{
            easyFlashcardsDao.getWordToTranslationsLearnGameItems(packageID, limit)
        }
    }.await()

    suspend fun getWordToExplanationsLearnGameItems(packageID: String,  limit: Int): List<LearnGameItem> = coroutineScope {
        async{
            easyFlashcardsDao.getWordToExplanationsLearnGameItems(packageID, limit)
        }
    }.await()

    suspend fun getWordToPhrasesLearnGameItems(packageID: String,  limit: Int): List<LearnGameItem>  = coroutineScope {
        async{
            easyFlashcardsDao.getWordToPhrasesLearnGameItems(packageID, limit)
        }
    }.await()

    suspend fun getCurrentData(packageID: String, gameType: Int): Int  = coroutineScope{
        async{
            when(gameType){
                1 -> {
                    easyFlashcardsDao.getLearningTranslationsCurrentScore(packageID)
                }
                2 -> {
                    easyFlashcardsDao.getLearningExplanationsCurrentScore(packageID)
                }
                else -> {
                    easyFlashcardsDao.getLearningPhrasesCurrentScore(packageID)
                }
            }
        }
    }.await()

    suspend fun getQuizQuestionsWithCorrectAnswers(packageID: String, gameType: Int): List<QuizQuestion> = coroutineScope{
        async{
            when(gameType){
                1 -> {
                    easyFlashcardsDao.getQuizTranslationsWithCorrectAnswers(packageID)
                }
                2 -> {
                    easyFlashcardsDao.getQuizExplanationsWithCorrectAnswers(packageID)
                }
                else -> {
                    easyFlashcardsDao.getQuizPhrasesWithCorrectAnswers(packageID)
                }
            }
        }
    }.await()

    suspend fun getQuizIncorrectAnswers(packageID: String, cardID: String): List<QuizOption> = coroutineScope{
        async{
            easyFlashcardsDao.getQuizIncorrectAnswers(packageID, cardID)
        }
    }.await()

    suspend fun getMemoryCards(packageID: String): List<MemoryCard> = coroutineScope{
        async{
            val listOfIds = easyFlashcardsDao.get12IdsForMemory(packageID)
            val memoryCards = mutableListOf<MemoryCard>()
            listOfIds.forEach {
                memoryCards.add(easyFlashcardsDao.getWordForMemory(it))
                val translationCard = easyFlashcardsDao.getTranslationForMemory(it)
                val translations = translationCard.content.split("|")
                val randomTranslation = translations[(translations.indices).random()]
                translationCard.content = randomTranslation
                memoryCards.add(translationCard)
            }
            memoryCards.shuffle()
            memoryCards
        }
    }.await()

    suspend fun canQuizBeStarted(packageID: String, gameType: Int): Boolean = coroutineScope {
        async {
            when(gameType){
                1 -> easyFlashcardsDao.getAmountOfCardsWithTranslations(packageID)>=15
                2 -> easyFlashcardsDao.getAmountOfCardsWithExplanations(packageID)>=15
                else -> easyFlashcardsDao.getAmountOfCardsWithPhrases(packageID)>=15
            }
        }
    }.await()

    suspend fun canMemoryBeStarted(packageID: String): Boolean = coroutineScope {
        async {
           easyFlashcardsDao.getAmountOfCardsWithTranslations(packageID)>=12
        }
    }.await()
}