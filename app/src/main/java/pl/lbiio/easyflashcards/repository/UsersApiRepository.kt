package pl.lbiio.easyflashcards.repository

import pl.lbiio.easyflashcards.api_classes.AwardDTO
import pl.lbiio.easyflashcards.api_classes.UserDTO
import pl.lbiio.easyflashcards.services.ApiService
import javax.inject.Inject

class UsersApiRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun addUser(
        user: UserDTO
    ) = apiService.addUser(user)

    suspend fun updateUser(
        user: UserDTO
    ) = apiService.updateUser(user)

    suspend fun addPointsToUser(
        points: Int,
        UID: String
    ) = apiService.addPointsToUser(points, UID)

    suspend fun grantAwardToUser(
        award: AwardDTO
    ) = apiService.grantAwardToUser(award)

    suspend fun updateUserPoints(
        UID: String,
        packageId: String,
        points: Int,
        awardCode: Int
    ) = apiService.updateUserPoints(UID, packageId, points, awardCode)

    suspend fun getAllAwards(
        UID: String,
    ) = apiService.getAllAwards(UID)

    suspend fun getGeneralLeaderBoard(
        UID: String,
    ) = apiService.getGeneralLeaderBoard(UID)

    suspend fun getPackageLeaderBoard(
        UID: String,
        packageId: String
    ) = apiService.getPackageLeaderBoard(UID, packageId)

}