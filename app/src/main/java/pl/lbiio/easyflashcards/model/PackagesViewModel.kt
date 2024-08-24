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
import pl.lbiio.easyflashcards.api_classes.PackageFromBackup
import pl.lbiio.easyflashcards.api_classes.PackageToBuyDTO
import pl.lbiio.easyflashcards.api_classes.PackageToUpdateDTO
import pl.lbiio.easyflashcards.data.MethodCall
import pl.lbiio.easyflashcards.data.PackageToDisplayInList
import pl.lbiio.easyflashcards.repository.PackagesApiRepository
import pl.lbiio.easyflashcards.repository.PackagesRoomRepository
import pl.lbiio.easyflashcards.repository.UncalledMethodsRepository
import javax.inject.Inject

@HiltViewModel
class PackagesViewModel @Inject constructor(
    private val packagesRoomRepository: PackagesRoomRepository,
    private val packagesApiRepository: PackagesApiRepository,
    private val uncalledMethodsRepository: UncalledMethodsRepository
) :
    ViewModel() {


    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().uid.toString()
    }

    private val _allPackagesList = MutableStateFlow<List<PackageToDisplayInList>>(emptyList())
    val allPackagesList = _allPackagesList.asStateFlow()

    fun loadAllPackages() {
        viewModelScope.launch(Dispatchers.IO) {
            packagesRoomRepository.getAllPackages().distinctUntilChanged()
                .collect { listOfAllPackages ->
                    if (listOfAllPackages.isEmpty()) {
                        _allPackagesList.value = emptyList()
                    } else {
                        _allPackagesList.value = listOfAllPackages
                    }
                }
        }
    }

    suspend fun addPackage(
        Status: Int,
        pack: PackageToUpdateDTO
    ) {
        packagesRoomRepository.addPackage(pack, Status, 0, 0)
        val gson = Gson()
        val methodToInvokeJSON =
            gson.toJson(MethodCall("addPackage", listOf(getCurrentUserId(), Status, pack)))
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun addPackageFromBackupToRoom(pack: PackageFromBackup) {
        packagesRoomRepository.addPackageFromBackupToRoom(pack)
    }


    suspend fun updatePackage(
        Status: Int,
        pack: PackageToUpdateDTO
    ) {
        packagesRoomRepository.updatePackage(pack)
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(MethodCall("updatePackage", listOf(Status, pack)))
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun setLearningBestScore(
        packageID: String,
        score: Int,
        gameType: Int
    ) {
        packagesRoomRepository.setLearningBestScore(packageID, score, gameType)
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(
            MethodCall(
                "setLearningBestScore",
                listOf(getCurrentUserId(), packageID, score, gameType)
            )
        )
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun setQuizBestScore(
        packageID: String,
        score: Int,
        gameType: Int
    ) {
        packagesRoomRepository.setQuizBestScore(packageID, score, gameType)
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(
            MethodCall(
                "setQuizBestScore",
                listOf(getCurrentUserId(), packageID, score, gameType)
            )
        )
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun setMemoryBestScore(
        packageID: String,
        score: Int,
    ) {
        packagesRoomRepository.setMemoryTranslationsBestScore(packageID, score)
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(
            MethodCall(
                "setMemoryTranslationsBestScore",
                listOf(getCurrentUserId(), packageID, score)
            )
        )
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun ratePackage(
        packageID: String,
        rate: Int
    ) {
        packagesRoomRepository.ratePackage(packageID, rate)
        val gson = Gson()
        val methodToInvokeJSON =
            gson.toJson(MethodCall("ratePackage", listOf(getCurrentUserId(), packageID, rate)))
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun sharePackage(
        pack: PackageToBuyDTO
    ) {
        packagesRoomRepository.sharePackage(pack)
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(MethodCall("sharePackage", listOf(pack)))
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun updateSharedPackage(
        pack: PackageToBuyDTO
    ) {
        packagesRoomRepository.updateSharedPackage(pack)
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(MethodCall("updateSharedPackage", listOf(pack)))
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }


    suspend fun searchForPackages(
        backLanguage: String,
        frontLanguage: String,
        currency: String,
        price: Int,
        phrase: String,
    ) = packagesApiRepository.searchForPackages(
        backLanguage,
        frontLanguage,
        currency,
        price,
        phrase,
        getCurrentUserId()
    ).body()


    suspend fun getBackup(
    ) = packagesApiRepository.getBackup(getCurrentUserId())

    suspend fun getBoughtPackage(
        packageID: String
    ) = packagesApiRepository.getBoughtPackage(packageID).body()

    suspend fun getProductDetails(
        packageID: String
    ) = packagesApiRepository.getProductDetails(packageID).body()


    suspend fun checkIsShared(packageID: String) = withContext(Dispatchers.IO) {
        packagesRoomRepository.checkIsShared(packageID)
    }

    suspend fun getPackageName(packageID: String) = withContext(Dispatchers.IO) {
        packagesRoomRepository.getPackageName(packageID)
    }

    suspend fun getPackageToModify(packageID: String) = withContext(Dispatchers.IO) {
        packagesRoomRepository.getPackageToModify(packageID)
    }

    suspend fun getPackageToShare(packageID: String) = withContext(Dispatchers.IO) {
        packagesRoomRepository.getPackageToShare(packageID)
    }

    suspend fun getLearningBestScore(packageID: String, gameType: Int) =
        withContext(Dispatchers.IO) {
            when (gameType) {
                1 -> packagesRoomRepository.getLearningTranslationsBestScore(packageID)
                2 -> packagesRoomRepository.getLearningExplanationsBestScore(packageID)
                3 -> packagesRoomRepository.getLearningPhrasesBestScore(packageID)
                else -> -1
            }
        }

    suspend fun getQuizBestScore(
        packageID: String,
        gameType: Int
    ) = withContext(Dispatchers.IO) {
        when (gameType) {
            1 -> packagesRoomRepository.getQuizTranslationsBestScore(packageID)
            2 -> packagesRoomRepository.getQuizExplanationsBestScore(packageID)
            3 -> packagesRoomRepository.getQuizPhrasesBestScore(packageID)
            else -> -1
        }
    }

    suspend fun getMemoryBestScore(
        packageID: String
    ) = withContext(Dispatchers.IO) {
        packagesRoomRepository.getMemoryBestScore(packageID)
    }


    suspend fun getPersonalRate(packageID: String) = withContext(Dispatchers.IO) {
        packagesRoomRepository.getPersonalRate(packageID)
    }

    suspend fun isDatabaseEmpty() = withContext(Dispatchers.IO) {
        packagesRoomRepository.isDatabaseEmpty()
    }

    suspend fun doesUserHaveBackup() = withContext(Dispatchers.IO) {
        packagesApiRepository.doesUserHaveBackup(getCurrentUserId())
    }

    suspend fun updateChangedPackages() = withContext(Dispatchers.IO) {
        packagesApiRepository.getChangedPackages(getCurrentUserId()).body()?.let {
            Log.d("pakiety do zaktualizowania", it.toString())
            it.forEach { pack ->
                Log.d("pakietdo aktualizacji", pack.toString())
                packagesRoomRepository.updatePackage(
                    PackageToUpdateDTO(
                        pack.packageIDForeign,
                        pack.name,
                        pack.frontLanguage,
                        pack.backLanguage,
                        pack.path
                    )
                )
                Log.d("zaktualizowano", "tak")
                packagesApiRepository.notifyPackageChange(
                    getCurrentUserId(),
                    pack.packageIDForeign,
                    pack.lastPackageChange
                )
                Log.d("notyfikowano", "tak")
            }
        }
    }

    suspend fun getBoughtPackagesIds() = withContext(Dispatchers.IO) {
        packagesRoomRepository.getBoughtPackagesIds()
    }

    suspend fun resetKnowledgeLevel(packageID: String, gameType: Int) =
        withContext(Dispatchers.IO) {
            packagesRoomRepository.resetKnowledgeLevel(packageID, gameType)
            val gson = Gson()
            val methodToInvokeJSON = gson.toJson(
                MethodCall(
                    "resetKnowledgeLevel",
                    listOf(getCurrentUserId(), packageID, gameType)
                )
            )
            uncalledMethodsRepository.insertUncalledMethod(
                System.currentTimeMillis(),
                methodToInvokeJSON
            )
        }

    suspend fun deletePackage(packageID: String, Status: Int) = withContext(Dispatchers.IO) {
        packagesRoomRepository.deleteWholePackageByID(packageID)
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(
            MethodCall(
                "deletePackageById",
                listOf(getCurrentUserId(), packageID, Status)
            )
        )
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun isInternetAvailable(isAvailable: suspend () -> Unit, isNotAvailable: () -> Unit) {
        uncalledMethodsRepository.isInternetAvailable(isAvailable, isNotAvailable)
    }

}