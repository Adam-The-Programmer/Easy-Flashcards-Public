package pl.lbiio.easyflashcards.repository

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.api_classes.AwardDTO
import pl.lbiio.easyflashcards.api_classes.FlashcardToUpdateDTO
import pl.lbiio.easyflashcards.data.MethodCall
import pl.lbiio.easyflashcards.api_classes.PackageToBuyDTO
import pl.lbiio.easyflashcards.api_classes.PackageToUpdateDTO
import pl.lbiio.easyflashcards.api_classes.UserDTO
import pl.lbiio.easyflashcards.db_access.EasyFlashcardsDao
import pl.lbiio.easyflashcards.tables.BackupMethod
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class UncalledMethodsRepository @Inject constructor(
    private val flashcardsApiRepository: FlashcardsApiRepository,
    private val packagesApiRepository: PackagesApiRepository,
    private val usersApiRepository: UsersApiRepository,
    private val easyFlashcardsDao: EasyFlashcardsDao
) {

    private fun getUnCalledBackupMethods(): Flow<List<BackupMethod>> =
        easyFlashcardsDao.getUncalledBackupMethods().flowOn(
            Dispatchers.IO
        ).conflate()

    private val _uncalledBackupMethods = MutableStateFlow<List<BackupMethod>>(emptyList())
    val uncalledBackupMethods = _uncalledBackupMethods.asStateFlow()

    fun loadUnCalledBackupMethods() {
        CoroutineScope(Dispatchers.IO).launch {
            getUnCalledBackupMethods().distinctUntilChanged()
                .collect { listOfUncalledBackupMethods ->
                    if (listOfUncalledBackupMethods.isEmpty()) {
                        _uncalledBackupMethods.value = emptyList()
                    } else {
                        _uncalledBackupMethods.value = listOfUncalledBackupMethods
                    }
                }
        }
    }

    suspend fun insertUncalledMethod(timestamp: Long, methodCallJSON: String) = withContext(
        Dispatchers.IO
    ) {
        easyFlashcardsDao.insertUncalledBackupMethod(timestamp, methodCallJSON)
    }

    suspend fun deleteUncalledMethod(id: Long) = withContext(Dispatchers.IO) {
        easyFlashcardsDao.deleteUncalledMethod(id)
    }

    suspend fun deleteAllBackupMethods() = withContext(Dispatchers.IO) {
        easyFlashcardsDao.deleteAllBackupMethods()
    }


    suspend fun isInternetAvailable(
        proceedApiCall: suspend () -> Unit,
        handleNoInternetException: suspend () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val timeoutMs = 1000
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53)
                socket.connect(socketAddress, timeoutMs)
                socket.close()
                proceedApiCall() // Connection successful
            } catch (e: IOException) {
                handleNoInternetException() // Connection failed
            }
        }
    }

    private fun <T> Any.convert(classOfT: Class<T>): T =
        Gson().fromJson(Gson().toJson(this), classOfT)

    suspend fun invokeBackupMethod(methodCall: MethodCall) {
        when (methodCall.methodName) {
            "addFlashcard" -> flashcardsApiRepository.addFlashcard(
                methodCall.params[0].toString(),
                methodCall.params[1].toString().toDouble().toInt(),
                methodCall.params[2].convert(FlashcardToUpdateDTO::class.java)
            )

            "deleteFlashcard" -> flashcardsApiRepository.deleteFlashcard(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt()
            )

            "updateImportance" -> flashcardsApiRepository.updateImportance(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toBoolean()
            )

            "updateCard" -> flashcardsApiRepository.updateCard(
                methodCall.params[0].toString().toDouble().toInt(),
                methodCall.params[1].convert(FlashcardToUpdateDTO::class.java)
            )

            "setKnowledgeLevel" -> flashcardsApiRepository.setKnowledgeLevel(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt(),
                methodCall.params[3].toString().toDouble().toInt()
            )

            "setIsTranslationKnown" -> flashcardsApiRepository.setIsTranslationKnown(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toBoolean()
            )

            "setIsExplanationKnown" -> flashcardsApiRepository.setIsExplanationKnown(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toBoolean()
            )

            "setIsPhraseKnown" -> flashcardsApiRepository.setIsPhraseKnown(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toBoolean()
            )

            "setIsKnown" -> flashcardsApiRepository.setIsKnown(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toBoolean(),
                methodCall.params[3].toString().toDouble().toInt()
            )

            "updateCardParametersWhenKnowClicked" -> flashcardsApiRepository.updateCardParametersWhenKnowClicked(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString(),
                methodCall.params[3].toString().toDouble().toInt(),
                methodCall.params[4].toString().toDouble().toInt()
            )

            "addPackage" -> {
                Log.d("wartosc", methodCall.params[2].toString())
                packagesApiRepository.addPackage(
                    methodCall.params[0].toString(),
                    methodCall.params[1].toString().toDouble().toInt(),
                    methodCall.params[2].convert(PackageToUpdateDTO::class.java)
                )
            }

            "deletePackageById" -> packagesApiRepository.deletePackageById(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt()
            )

            "updatePackage" -> packagesApiRepository.updatePackage(
                methodCall.params[0].toString().toDouble().toInt(),
                methodCall.params[1].convert(PackageToUpdateDTO::class.java)
            )

            "sharePackage" -> packagesApiRepository.sharePackage(
                methodCall.params[0].convert(PackageToBuyDTO::class.java)
            )

            "updateSharedPackage" -> packagesApiRepository.updateSharedPackage(
                methodCall.params[0].convert(PackageToBuyDTO::class.java)
            )

            "addPointsToPackageOfUser" -> packagesApiRepository.addPointsToPackageOfUser(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt()
            )

            "ratePackage" -> packagesApiRepository.ratePackage(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt()
            )

            "resetKnowledgeLevel" -> packagesApiRepository.resetKnowledgeLevel(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt()
            )

            "setLearningCompleted" -> packagesApiRepository.setLearningCompleted(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt()
            )

            "setQuizBestScore" -> packagesApiRepository.setQuizBestScore(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt(),
                methodCall.params[3].toString().toDouble().toInt()
            )

            "setLearningBestScore" -> packagesApiRepository.setLearningBestScore(
                methodCall.params[0].toString(),
                methodCall.params[1].toString(),
                methodCall.params[2].toString().toDouble().toInt(),
                methodCall.params[3].toString().toDouble().toInt()
            )

            "updateUser" ->
                usersApiRepository.updateUser(
                    methodCall.params[0].convert(UserDTO::class.java)
                )

            "addPointsToUser" ->
                usersApiRepository.addPointsToUser(
                    methodCall.params[0].toString().toDouble().toInt(),
                    methodCall.params[1].toString()
                )

            "grantAwardToUser" ->
                usersApiRepository.grantAwardToUser(
                    methodCall.params[0].convert(AwardDTO::class.java)
                )

            "updateUserPoints" ->
                usersApiRepository.updateUserPoints(
                    methodCall.params[0].toString(),
                    methodCall.params[1].toString(),
                    methodCall.params[2].toString().toDouble().toInt(),
                    methodCall.params[3].toString().toDouble().toInt()
                )
        }
    }
}