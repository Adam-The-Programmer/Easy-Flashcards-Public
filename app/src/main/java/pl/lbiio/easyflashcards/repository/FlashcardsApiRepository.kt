package pl.lbiio.easyflashcards.repository

import android.util.Log
import pl.lbiio.easyflashcards.api_classes.FlashcardToUpdateDTO
import pl.lbiio.easyflashcards.services.ApiService
import javax.inject.Inject

class FlashcardsApiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun addFlashcard(
        UID: String,
        Status: Int,
        Card: FlashcardToUpdateDTO
    ) = apiService.addFlashcard(UID, Status, Card)

    suspend fun deleteFlashcard(
        CardID: String,
        PackageID: String,
        Status: Int
    ) = apiService.deleteFlashcard(CardID, PackageID, Status)

    suspend fun updateImportance(
        UID: String,
        CardID: String,
        isImportant: Boolean
    ) = apiService.updateImportance(CardID, UID, isImportant)

    suspend fun updateCard(
        Status: Int,
        Card: FlashcardToUpdateDTO
    ) = apiService.updateCard(Status, Card)

    suspend fun setKnowledgeLevel(
        UID: String,
        cardID: String,
        gameType: Int,
        value: Int
    ) = apiService.setKnowledgeLevel(UID, cardID, gameType, value)


    suspend fun setIsTranslationKnown(
        UID: String,
        cardID: String,
        isKnown: Boolean
    ) = apiService.setIsTranslationKnown(UID, cardID, isKnown)

    suspend fun setIsExplanationKnown(
        UID: String,
        cardID: String,
        isKnown: Boolean
    ) = apiService.setIsExplanationKnown(UID, cardID, isKnown)

    suspend fun setIsPhraseKnown(
        UID: String,
        cardID: String,
        isKnown: Boolean
    ) = apiService.setIsPhraseKnown(UID, cardID, isKnown)

    suspend fun setIsKnown(
        UID: String,
        cardID: String,
        isKnown: Boolean,
        gameType: Int
    ) = apiService.setIsKnown(UID, cardID, isKnown, gameType)

    suspend fun getBackupFlashcards(
        UID: String,
        PackageID: String
    ) = apiService.getBackupFlashcards(UID, PackageID)

    suspend fun getBoughtFlashcards(
        PackageID: String
    ) = apiService.getBoughtFlashcards(PackageID)

    suspend fun getChangedFlashcards(
        UID: String,
        PackageID: String
    ) = apiService.getChangedFlashcards(UID, PackageID)

    suspend fun getChangedFlashcardToSet(cardID: String):FlashcardToUpdateDTO? {
        val result = apiService.getChangedFlashcardToSet(cardID).body()
        Log.d("result", result.toString())
        return if(result?.cardID == ""){
            null
        }else
            result
    }

    suspend fun updateCardParametersWhenKnowClicked(
        UID:String,
        packageID: String,
        cardID: String,
        gameType: Int,
        scoreValue: Int
    ) = apiService.updateCardParametersWhenKnowClicked(UID, packageID, cardID, gameType, scoreValue)

    suspend fun notifyCardChange(
        UID: String,
        packageID: String,
        cardID: String,
        mode: String
    ) = apiService.notifyCardChange(UID, packageID, cardID, mode)
}