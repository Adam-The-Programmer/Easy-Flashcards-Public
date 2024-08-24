package pl.lbiio.easyflashcards.repository

import android.util.Log
import androidx.room.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.api_classes.PackageFromBackup
import pl.lbiio.easyflashcards.api_classes.PackageToBuyDTO
import pl.lbiio.easyflashcards.api_classes.PackageToUpdateDTO
import pl.lbiio.easyflashcards.data.PackageToDisplayInList
import pl.lbiio.easyflashcards.data.PackageToUpdate
import pl.lbiio.easyflashcards.data.SharedPackage
import pl.lbiio.easyflashcards.db_access.EasyFlashcardsDao
import pl.lbiio.easyflashcards.tables.LearningPackageProgress
import pl.lbiio.easyflashcards.tables.MemoryPackageProgress
import pl.lbiio.easyflashcards.tables.QuizPackageProgress
import javax.inject.Inject

class PackagesRoomRepository @Inject constructor(private val easyFlashcardsDao: EasyFlashcardsDao) {

    fun getAllPackages(): Flow<List<PackageToDisplayInList>> = easyFlashcardsDao.getAllPackages().flowOn(
        Dispatchers.IO).conflate()

    suspend fun addPackage(p: PackageToUpdateDTO, status: Int, maxKnowledgeLevel: Int, personalRate: Int) = coroutineScope {
        async {
            easyFlashcardsDao.addPackage(p.packageID, p.name, p.frontLanguage, p.backLanguage, p.path, status, maxKnowledgeLevel, personalRate)
            easyFlashcardsDao.initLearningPackageProgress(LearningPackageProgress(foreignPackageID = p.packageID))
            easyFlashcardsDao.initMemoryPackageProgress(MemoryPackageProgress(foreignPackageID = p.packageID))
            easyFlashcardsDao.initQuizPackageProgress(QuizPackageProgress(foreignPackageID = p.packageID) )
        }
    }.await()

    @Transaction
    suspend fun addPackageFromBackupToRoom(p: PackageFromBackup) = coroutineScope {
        async {
            easyFlashcardsDao.addPackage(p.packageID, p.name, p.frontLanguage, p.backLanguage, p.artwork, p.status, p.maxKnowledgeLevel, p.personalRate)
            Log.d("status repo", p.status.toString())

            if(p.status==2){
                Log.d("description", p.description)
                easyFlashcardsDao.sharePackage(p.packageID, p.description, p.price, p.currency)
                easyFlashcardsDao.setPackageStatusToShared(p.packageID)
            }
            easyFlashcardsDao.initLearningPackageProgress(LearningPackageProgress(foreignPackageID = p.packageID, learningTranslationsBestScore = p.learningTranslationsBestScore, learningExplanationsBestScore = p.learningExplanationsBestScore, learningPhrasesBestScore = p.learningPhrasesBestScore, learningTranslationsCurrentScore = p.learningTranslationsCurrentScore, learningExplanationsCurrentScore = p.learningExplanationsCurrentScore, learningPhrasesCurrentScore = p.learningPhrasesCurrentScore, areLearningTranslationsCompleted = p.areTranslationsCompleted, areLearningExplanationsCompleted = p.areExplanationsCompleted, areLearningPhrasesCompleted = p.arePhrasesCompleted))
            easyFlashcardsDao.initMemoryPackageProgress(MemoryPackageProgress(foreignPackageID = p.packageID, memoryBestScore = p.memoryBestScore))
            easyFlashcardsDao.initQuizPackageProgress(QuizPackageProgress(foreignPackageID = p.packageID, quizBestTranslationsScore = p.quizBestTranslationsScore, quizBestExplanationsScore = p.quizBestExplanationsScore, quizBestPhrasesScore = p.quizBestPhrasesScore))
        }
    }.await()


    suspend fun updatePackage(pack: PackageToUpdateDTO) = coroutineScope { easyFlashcardsDao.updatePackage(pack.packageID, pack.name, pack.frontLanguage, pack.backLanguage, pack.path) }

    suspend fun setLearningBestScore(packageID: String, score: Int, gameType: Int){
        when(gameType){
            1 -> easyFlashcardsDao.setLearningTranslationsBestScore(packageID, score)
            2 -> easyFlashcardsDao.setLearningExplanationsBestScore(packageID, score)
            3 -> easyFlashcardsDao.setLearningPhrasesBestScore(packageID, score)
        }
    }

    suspend fun setQuizBestScore(packageID: String, score: Int, gameType: Int){
        when(gameType){
            1 -> easyFlashcardsDao.setQuizTranslationsBestScore(packageID, score)
            2 -> easyFlashcardsDao.setQuizExplanationsBestScore(packageID, score)
            3 -> easyFlashcardsDao.setQuizPhrasesBestScore(packageID, score)
        }
    }

    suspend fun setMemoryTranslationsBestScore(packageID: String, score: Int) = coroutineScope { easyFlashcardsDao.setMemoryTranslationsBestScore(packageID, score)}

    suspend fun ratePackage(packageID: String, rate: Int) = coroutineScope { easyFlashcardsDao.setPersonalRate(packageID, rate)}

    suspend fun sharePackage(pack: PackageToBuyDTO) = coroutineScope {
        easyFlashcardsDao.sharePackage(pack.packageID, pack.description, pack.price, pack.currency)
        easyFlashcardsDao.setPackageStatusToShared(pack.packageID)
    }

    suspend fun updateSharedPackage(pack: PackageToBuyDTO) = coroutineScope { easyFlashcardsDao.updateSharing(pack.packageID, pack.description, pack.price, pack.currency)}

    suspend fun checkIsShared(packageID: String): Boolean = coroutineScope {
        async {
            easyFlashcardsDao.checkIsShared(packageID) > 0
        }
    }.await()

    suspend fun getPackageName(packageID: String): String = coroutineScope {
        async {
            easyFlashcardsDao.getPackageName(packageID)
        }
    }.await()

    suspend fun getPackageToModify(packageID: String): PackageToUpdate = coroutineScope {
        async {
            easyFlashcardsDao.getPackageToModify(packageID)
        }
    }.await()

    suspend fun getPackageToShare(packageID: String): SharedPackage = coroutineScope {
        async {
            easyFlashcardsDao.getPackageToShare(packageID)
        }
    }.await()

    suspend fun getLearningTranslationsBestScore(packageID: String): Int = coroutineScope {
        async {
            easyFlashcardsDao.getLearningTranslationsBestScore(packageID)
        }
    }.await()
    suspend fun getLearningExplanationsBestScore(packageID: String): Int = coroutineScope {
        async {
            easyFlashcardsDao.getLearningExplanationsBestScore(packageID)
        }
    }.await()

    suspend fun getLearningPhrasesBestScore(packageID: String): Int = coroutineScope {
        async {
            easyFlashcardsDao.getLearningPhrasesBestScore(packageID)
        }
    }.await()

    suspend fun getQuizTranslationsBestScore(packageID: String): Int = coroutineScope {
        async {
            easyFlashcardsDao.getQuizTranslationsBestScore(packageID)
        }
    }.await()

    suspend fun getQuizExplanationsBestScore(packageID: String): Int = coroutineScope {
        async {
            easyFlashcardsDao.getQuizExplanationsBestScore(packageID)
        }
    }.await()

    suspend fun getQuizPhrasesBestScore(packageID: String): Int = coroutineScope {
        async {
            easyFlashcardsDao.getQuizPhrasesBestScore(packageID)
        }
    }.await()

    suspend fun getMemoryBestScore(packageID: String): Int = coroutineScope {
        async {
            easyFlashcardsDao.getMemoryBestScore(packageID)
        }
    }.await()

    suspend fun getPersonalRate(packageID: String): Int = coroutineScope {
        async {
            easyFlashcardsDao.getPersonalRate(packageID)
        }
    }.await()

    suspend fun isDatabaseEmpty(): Boolean = coroutineScope {
        async {
            easyFlashcardsDao.getAmountOfAllPackages()==0
        }
    }.await()

    suspend fun getBoughtPackagesIds(): List<String> = coroutineScope {
        async {
            easyFlashcardsDao.getBoughtPackagesIds()
        }
    }.await()

    suspend fun resetKnowledgeLevel(packageID: String, gameType: Int) = withContext(Dispatchers.IO){
        when(gameType){
            1 -> {
                val cardIdsToUpdate = easyFlashcardsDao.getCardIdsForPackage(packageID)
                cardIdsToUpdate.forEach { cardID ->
                    easyFlashcardsDao.resetSingleTranslationKnowledgeProgress(cardID)
                }
                easyFlashcardsDao.setLearningTranslationsCurrentScore(packageID, 0)
            }
            2 -> {
                val cardIdsToUpdate = easyFlashcardsDao.getCardIdsForPackage(packageID)
                cardIdsToUpdate.forEach { cardID ->
                    easyFlashcardsDao.resetSingleExplanationKnowledgeProgress(cardID)
                }
                easyFlashcardsDao.setLearningExplanationsCurrentScore(packageID, 0)
            }
            3 -> {
                val cardIdsToUpdate = easyFlashcardsDao.getCardIdsForPackage(packageID)
                cardIdsToUpdate.forEach { cardID ->
                    easyFlashcardsDao.resetSinglePhraseKnowledgeProgress(cardID)
                }
                easyFlashcardsDao.setLearningPhrasesCurrentScore(packageID, 0)
            }
        }

    }

    suspend fun deleteWholePackageByID(packageID: String)  = withContext(Dispatchers.IO){
        val cardIdsToUpdate = easyFlashcardsDao.getCardIdsForPackage(packageID)
        cardIdsToUpdate.forEach { cardID ->
            easyFlashcardsDao.deleteSingleLearningCardsProgress(cardID)
        }
        easyFlashcardsDao.deleteFlashcardsByForeign(packageID)
        easyFlashcardsDao.deleteLearningPackageProgress(packageID)
        easyFlashcardsDao.deleteMemoryPackageProgress(packageID)
        easyFlashcardsDao.deleteQuizPackageProgress(packageID)
        easyFlashcardsDao.deletePackageByID(packageID)
    }

}