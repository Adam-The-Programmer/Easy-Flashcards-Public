package pl.lbiio.easyflashcards.services

import com.google.gson.GsonBuilder
import pl.lbiio.easyflashcards.api_classes.*
import pl.lbiio.easyflashcards.data.LeaderBoardItem
import pl.lbiio.easyflashcards.data.SharedPackageToBuy
import pl.lbiio.easyflashcards.api_classes.UserAward
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    companion object {
        fun createRetrofit(baseUrl: String): Retrofit {
            val gson = GsonBuilder()
                .setLenient()
                .serializeNulls()
                .create()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }

    // for flashcards

    @POST("/api/addFlashcard/{UID}/{Status}")
    suspend fun addFlashcard(
        @Path("UID") UID: String,
        @Path("Status") Status: Int,
        @Body Card: FlashcardToUpdateDTO
    ): Int

    @PATCH("/api/deleteFlashcard/{CardID}/{PackageID}/{Status}")
    suspend fun deleteFlashcard(
        @Path("CardID") CardID: String,
        @Path("PackageID") PackageID: String,
        @Path("Status") Status: Int
    ): Int

    @PATCH("/api/updateImportance/{UID}/{cardID}/{isImportant}")
    suspend fun updateImportance(
        @Path("UID") UID: String,
        @Path("cardID") CardID: String,
        @Path("isImportant") isImportant: Boolean
    ): Int

    @PATCH("/api/updateCard/{Status}")
    suspend fun updateCard(
        @Path("Status") Status: Int,
        @Body Card: FlashcardToUpdateDTO
    ): Int

    @PATCH("/api/setKnowledgeLevel/{UID}/{cardID}/{gameType}/{value}")
    suspend fun setKnowledgeLevel(
        @Path("UID") UID: String,
        @Path("cardID") cardID: String,
        @Path("gameType") gameType: Int,
        @Path("value") value: Int,
    ): Int

    @PATCH("/api/setIsTranslationKnown/{UID}/{cardID}/{isKnown}")
    suspend fun setIsTranslationKnown(
        @Path("UID") UID: String,
        @Path("cardID") cardID: String,
        @Path("isKnown") isKnown: Boolean
    ): Int

    @PATCH("/api/setIsExplanationKnown/{UID}/{cardID}/{isKnown}")
    suspend fun setIsExplanationKnown(
        @Path("UID") UID: String,
        @Path("cardID") cardID: String,
        @Path("isKnown") isKnown: Boolean
    ): Int

    @PATCH("/api/setIsPhraseKnown/{UID}/{cardID}/{isKnown}")
    suspend fun setIsPhraseKnown(
        @Path("UID") UID: String,
        @Path("cardID") cardID: String,
        @Path("isKnown") isKnown: Boolean
    ): Int

    @PATCH("/api/setIsKnown/{UID}/{cardID}/{isKnown}/{gameType}")
    suspend fun setIsKnown(
        @Path("UID") UID: String,
        @Path("cardID") cardID: String,
        @Path("isKnown") isKnown: Boolean,
        @Path("gameType") gameType: Int
    ): Int

    @GET("/api/getBackupFlashcards/{UID}/{PackageID}")
    suspend fun getBackupFlashcards(
        @Path("UID") UID: String,
        @Path("PackageID") PackageID: String
    ): Response<List<CardFromBackup>>

    @GET("/api/getBoughtFlashcards/{PackageID}")
    suspend fun getBoughtFlashcards(
        @Path("PackageID") PackageID: String
    ): Response<List<RawFlashcard>>

    @GET("/api/getChangedFlashcards/{UID}/{PackageID}")
    suspend fun getChangedFlashcards(
        @Path("UID") UID: String,
        @Path("PackageID") PackageID: String
    ): String?

    @GET("/api/getChangedFlashcardToSet/{cardID}")
    suspend fun getChangedFlashcardToSet(
        @Path("cardID") cardID: String
    ): Response<FlashcardToUpdateDTO?>

    @PATCH("/api/notifyCardChange/{UID}/{PackageID}/{CardID}/{mode}")
    suspend fun notifyCardChange(
        @Path("UID") UID: String,
        @Path("PackageID") PackageID: String,
        @Path("CardID") CardID: String,
        @Path("mode") mode: String)




    // for packages

    @POST("/api/addPackage/{UID}/{Status}")
    suspend fun addPackage(
        @Path("UID") UID: String,
        @Path("Status") Status: Int,
        @Body pack: PackageToUpdateDTO
    ): Int


    @DELETE("/api/deletePackageById/{UID}/{packageID}/{Status}")
    suspend fun deletePackageById(
        @Path("UID") UID: String,
        @Path("packageID") packageID: String,
        @Path("Status") Status: Int
    ): Int

    @PUT("/api/updatePackage/{Status}")
    suspend fun updatePackage(
        @Path("Status") Status: Int,
        @Body pack: PackageToUpdateDTO
    ): Int

    @PUT("/api/sharePackage")
    suspend fun sharePackage(
        @Body pack: PackageToBuyDTO
    ): Int

    @PUT("/api/updateSharedPackage")
    suspend fun updateSharedPackage(
        @Body pack: PackageToBuyDTO
    ): Int

    @GET("/api/searchForPackages/{backLanguage}/{frontLanguage}/{currency}/{price}/{phrase}/{UID}")
    suspend fun searchForPackages(
        @Path("backLanguage") backLanguage: String,
        @Path("frontLanguage") frontLanguage: String,
        @Path("currency") currency: String,
        @Path("price") price: Int,
        @Path("phrase") phrase: String,
        @Path("UID") UID: String
    ): Response<List<SharedPackageToBuy>>

    @PATCH("/api/addPointsToPackageOfUser/{UID}/{packageID}/{points}")
    suspend fun addPointsToPackageOfUser(
        @Path("UID") UID: String,
        @Path("packageID") packageID: String,
        @Path("points") points: Int
    ): Int

    @PATCH("/api/ratePackage/{UID}/{packageID}/{rate}")
    suspend fun ratePackage(
        @Path("UID") UID: String,
        @Path("packageID") packageID: String,
        @Path("rate") rate: Int
    ): Int

    @GET("/api/getBackup/{UID}")
    suspend fun getBackup(
        @Path("UID") UID: String
    ): List<PackageFromBackup>

    @PATCH("/api/setLearningCompleted/{UID}/{packageID}/{gameType}")
    suspend fun setLearningCompleted(
        @Path("UID") UID: String,
        @Path("packageID") packageID: String,
        @Path("gameType") gameType: Int
    ): Int

    @GET("/api/getBoughtPackage/{packageID}")
    suspend fun getBoughtPackage(
        @Path("packageID") packageID: String
    ): Response<RawPackage>

    @GET("/api/getProductDetails/{packageID}")
    suspend fun getProductDetails(
        @Path("packageID") packageID: String
    ): Response<ProductDetails>

    @GET("/api/getChangedPackages/{UID}")
    suspend fun getChangedPackages(
        @Path("UID") UID: String
    ): Response<List<ChangedPackage>>

    @PATCH("/api/notifyPackageChange/{UID}/{PackageID}/{timestamp}")
    suspend fun notifyPackageChange(
        @Path("UID") UID: String,
        @Path("PackageID") PackageID: String,
        @Path("timestamp") timestamp: Long,
    ): Int


    @PATCH("/api/setLearningBestScore/{UID}/{packageID}/{score}/{gameType}")
    suspend fun setLearningBestScore(
        @Path("UID") UID: String,
        @Path("packageID") packageID: String,
        @Path("score") score: Int,
        @Path("gameType") gameType: Int
    ): Int

    @PATCH("/api/setQuizBestScore/{UID}/{packageID}/{score}/{gameType}")
    suspend fun setQuizBestScore(
        @Path("UID") UID: String,
        @Path("packageID") packageID: String,
        @Path("score") score: Int,
        @Path("gameType") gameType: Int
    ): Int

    @PATCH("/api/resetKnowledgeLevel/{UID}/{packageID}/{gameType}")
    suspend fun resetKnowledgeLevel(
        @Path("UID") UID: String,
        @Path("packageID") packageID: String,
        @Path("gameType") gameType: Int
    ): Int

    @PATCH("/api/updateCardWhenKnowClicked/{UID}/{packageID}/{cardID}/{gameType}/{scoreValue}")
    suspend fun updateCardParametersWhenKnowClicked(
        @Path("UID") UID: String,
        @Path("packageID") packageID: String,
        @Path("cardID") cardID: String,
        @Path("gameType") gameType: Int,
        @Path("scoreValue") scoreValue: Int
    ): Int



    // for users

    @POST("/api/addUser")
    suspend fun addUser(
        @Body user: UserDTO
    ): Int

    @PUT("/api/updateUser")
    suspend fun updateUser(
        @Body user: UserDTO
    ): Int

    @PATCH("/api/addPointsToUser/{points}/{UID}")
    suspend fun addPointsToUser(
        @Path("points") points: Int,
        @Path("UID") UID: String
    ): Int

    @PUT("/api/grantAwardToUser")
    suspend fun grantAwardToUser(
        @Body award: AwardDTO
    ): Int

    @GET("/api/doesUserHaveBackup/{UID}")
    suspend fun doesUserHaveBackup(
        @Path("UID") UID: String
    ): Boolean

    @PATCH("/api/updateUserPoints/{UID}/{packageId}/{points}/{awardCode}")
    suspend fun updateUserPoints(
        @Path("UID") UID: String,
        @Path("packageId") packageId: String,
        @Path("points") points: Int,
        @Path("awardCode") awardCode: Int
    ): Int

    @GET("/api/getAllAwards/{UID}")
    suspend fun getAllAwards(
        @Path("UID") UID: String
    ): List<UserAward>

    @GET("/api/getGeneralLeaderBoard/{UID}")
    suspend fun getGeneralLeaderBoard(
        @Path("UID") UID: String
    ): List<LeaderBoardItem>

    @GET("/api/getPackageLeaderBoard/{UID}/{packageId}")
    suspend fun getPackageLeaderBoard(
        @Path("UID") UID: String,
        @Path("packageId") packageId: String
    ): List<LeaderBoardItem>



}