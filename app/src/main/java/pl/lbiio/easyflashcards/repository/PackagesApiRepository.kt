package pl.lbiio.easyflashcards.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import pl.lbiio.easyflashcards.api_classes.PackageToBuyDTO
import pl.lbiio.easyflashcards.api_classes.PackageToUpdateDTO
import pl.lbiio.easyflashcards.services.ApiService
import java.io.File
import javax.inject.Inject

class PackagesApiRepository @Inject constructor(private val apiService: ApiService){

    private suspend fun uploadImageToFirebase(name: String, path: String): String {
        val file = Uri.fromFile(File(path))
        val storageReference = FirebaseStorage.getInstance().reference.child("images")
        val imageRef = storageReference.child(name)

        return try {
            val uploadTask = imageRef.putFile(file!!).await()
            if (!uploadTask.task.isSuccessful) {
                throw uploadTask.task.exception ?: Exception("Upload failed")
            }

            // Get the download URL of the uploaded image
            val uri = imageRef.downloadUrl.await()
            uri.toString()
        } catch (e: Exception) {
            // Handle any errors
            throw e
        }
    }
    suspend fun addPackage(
        UID: String,
        Status: Int,
        pack: PackageToUpdateDTO
    ) {
        if(Status!=3) pack.path = uploadImageToFirebase("${pack.packageID}-${System.currentTimeMillis()}", pack.path)
        apiService.addPackage(UID, Status, pack)
    }

    suspend fun deletePackageById(UID: String, packageID: String, status: Int) = apiService.deletePackageById(UID, packageID, status)

    suspend fun updatePackage(
        Status: Int,
        pack: PackageToUpdateDTO
    ) {
        if(!pack.path.contains("http")) pack.path = uploadImageToFirebase("${pack.packageID}-${System.currentTimeMillis()}", pack.path)
        apiService.updatePackage(Status, pack)
    }

    suspend fun sharePackage(
        pack: PackageToBuyDTO
    ) = apiService.sharePackage(pack)

    suspend fun updateSharedPackage(
        pack: PackageToBuyDTO
    ) = apiService.updateSharedPackage(pack)

    suspend fun searchForPackages(
        backLanguage: String,
        frontLanguage: String,
        currency: String,
        price: Int,
        phrase: String,
        UID: String
    ) = apiService.searchForPackages(backLanguage, frontLanguage, currency, price, phrase, UID)

    suspend fun addPointsToPackageOfUser(
        UID: String,
        packageID: String,
        points: Int
    ) = apiService.addPointsToPackageOfUser(UID, packageID, points)

    suspend fun ratePackage(
        UID: String,
        packageID: String,
        rate: Int
    ) = apiService.ratePackage(UID, packageID, rate)

    suspend fun getBackup(
        UID: String
    ) = apiService.getBackup(UID)


    suspend fun getBoughtPackage(
        packageID: String
    ) = apiService.getBoughtPackage(packageID)

    suspend fun getProductDetails(
        packageID: String
    ) = apiService.getProductDetails(packageID)


    suspend fun getChangedPackages(
        UID: String
    ) = apiService.getChangedPackages(UID)

    suspend fun notifyPackageChange(UID: String, packageID: String, timestamp: Long) = apiService.notifyPackageChange(UID, packageID, timestamp)

    suspend fun resetKnowledgeLevel(
        UID: String,
        packageID: String,
        gameType: Int
    ) = apiService.resetKnowledgeLevel(UID, packageID, gameType)

    suspend fun setLearningCompleted(
        UID: String,
        packageID: String,
        gameType: Int
    ) = apiService.setLearningCompleted(UID, packageID, gameType)

    suspend fun setQuizBestScore(
        UID: String,
        packageID: String,
        score: Int,
        gameType: Int
    ) = apiService.setQuizBestScore(UID, packageID, score, gameType)

    suspend fun setLearningBestScore(
        UID: String,
        packageID: String,
        score: Int,
        gameType: Int
    ) = apiService.setLearningBestScore(UID, packageID, score, gameType)

    suspend fun doesUserHaveBackup(
        UID: String
    ) = apiService.doesUserHaveBackup(UID)

}