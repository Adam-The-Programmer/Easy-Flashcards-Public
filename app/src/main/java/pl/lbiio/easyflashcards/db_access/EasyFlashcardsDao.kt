package pl.lbiio.easyflashcards.db_access

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import pl.lbiio.easyflashcards.data.ExploreGameItem
import pl.lbiio.easyflashcards.data.FlashcardToDisplayInList
import pl.lbiio.easyflashcards.data.FlashcardToUpdate
import pl.lbiio.easyflashcards.data.LearnGameItem
import pl.lbiio.easyflashcards.data.MemoryCard
import pl.lbiio.easyflashcards.data.PackageToDisplayInList
import pl.lbiio.easyflashcards.data.PackageToUpdate
import pl.lbiio.easyflashcards.data.QuizOption
import pl.lbiio.easyflashcards.data.QuizQuestion
import pl.lbiio.easyflashcards.data.RawFlashcard
import pl.lbiio.easyflashcards.data.SharedPackage
import pl.lbiio.easyflashcards.tables.BackupMethod
import pl.lbiio.easyflashcards.tables.Flashcard
import pl.lbiio.easyflashcards.tables.LearningCardProgress
import pl.lbiio.easyflashcards.tables.LearningPackageProgress
import pl.lbiio.easyflashcards.tables.MemoryPackageProgress
import pl.lbiio.easyflashcards.tables.QuizPackageProgress

@Dao
interface EasyFlashcardsDao {

    @Transaction
    @Query(
        "SELECT primary_package_id as packageID," +
                " status," +
                " package_name as name," +
                " (SELECT COUNT(*) FROM flashcard_tbl WHERE foreign_package_id=primary_package_id)  as amount," +
                " artwork as artwork," +
                "CASE " +
                "   WHEN front_language == 'Polish' THEN 'PL' " +
                "   WHEN front_language == 'English' THEN 'EN'  " +
                "   WHEN front_language == 'French' THEN 'FR'  " +
                "   WHEN front_language == 'Spanish' THEN 'ES'  " +
                "   WHEN front_language == 'German' THEN 'DE'  " +
                "   WHEN front_language == 'Russian' THEN 'RU'  " +
                "   WHEN front_language == 'Japanese' THEN 'JA'  " +
                "   ELSE 'US' " +
                "END AS nativeLanguage, " +
                "CASE " +
                "   WHEN back_language == 'Polish' THEN 'PL' " +
                "   WHEN back_language == 'English' THEN 'EN'  " +
                "   WHEN back_language == 'French' THEN 'FR'  " +
                "   WHEN back_language == 'Spanish' THEN 'ES'  " +
                "   WHEN back_language == 'German' THEN 'DE'  " +
                "   WHEN back_language == 'Russian' THEN 'RU'  " +
                "   WHEN back_language == 'Japanese' THEN 'JA'  " +
                "   ELSE 'US' " +
                "END AS foreignLanguage, " +
                " COALESCE(ROUND(( "+
                " (SELECT SUM(is_explanation_known+is_phrase_known+is_translation_known) FROM learning_card_progress_tbl JOIN flashcard_tbl ON learning_card_progress_tbl.foreign_card_id=flashcard_tbl.card_id WHERE flashcard_tbl.foreign_package_id=primary_package_id) "+
                " *100.0/max_knowledge_level)), 0) as progress" +
                " from flashcards_package_tbl"
    )
    fun getAllPackages(): Flow<List<PackageToDisplayInList>>

    @Query("SELECT package_name as name," +
            " description as description," +
            " currency as currency," +
            " price as price" +
            " FROM shared_package_tbl" +
            " JOIN flashcards_package_tbl ON flashcards_package_tbl.primary_package_id=shared_package_tbl.foreign_package_id " +
            " WHERE foreign_package_id=:packageID"
    )
    suspend fun getPackageToShare(packageID: String): SharedPackage

    @Query("SELECT primary_package_id as packageID," +
            " package_name as name," +
            " front_language as nativeLanguage," +
            " back_language as foreignLanguage," +
            " artwork as artwork" +
            " FROM flashcards_package_tbl" +
            " WHERE primary_package_id=:packageID"
    )
    suspend fun getPackageToModify(packageID: String): PackageToUpdate

    @Query("SELECT translations_best_score" +
            " FROM learning_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID"
    )
    suspend fun getLearningTranslationsBestScore(packageID: String): Int


    @Query("SELECT explanations_best_score" +
            " FROM learning_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID")
    suspend fun getLearningExplanationsBestScore(packageID: String): Int


    @Query("SELECT phrases_best_score" +
            " FROM learning_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID")
    suspend fun getLearningPhrasesBestScore(packageID: String): Int


    @Query("SELECT quiz_best_translations_score" +
            " FROM quiz_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID"
    )
    suspend fun getQuizTranslationsBestScore(packageID: String): Int


    @Query("SELECT quiz_best_explanations_score" +
            " FROM quiz_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID")
    suspend fun getQuizExplanationsBestScore(packageID: String): Int


    @Query("SELECT quiz_best_phrases_score" +
            " FROM quiz_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID")
    suspend fun getQuizPhrasesBestScore(packageID: String): Int


    @Query("SELECT memory_best_score" +
            " FROM memory_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID")
    suspend fun getMemoryBestScore(packageID: String): Int


    @Query("SELECT translations_current_score" +
            " FROM learning_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID"
    )
    suspend fun getLearningTranslationsCurrentScore(packageID: String): Int


    @Query("SELECT explanations_current_score" +
            " FROM learning_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID")
    suspend fun getLearningExplanationsCurrentScore(packageID: String): Int


    @Query("SELECT phrases_current_score" +
            " FROM learning_package_progress_tbl" +
            " WHERE foreign_package_id=:packageID")
    suspend fun getLearningPhrasesCurrentScore(packageID: String): Int

    @Query("SELECT COUNT(*) from flashcards_package_tbl")
    suspend fun getAmountOfAllPackages(): Int

    @Query("SELECT primary_package_id from flashcards_package_tbl WHERE status=3")
    suspend fun getBoughtPackagesIds(): List<String>

    @Query("INSERT OR REPLACE INTO flashcards_package_tbl(primary_package_id, package_name, front_language, back_language, artwork, max_knowledge_level, personal_rate, status) VALUES(:packageID, :name, :frontLanguage, :backLanguage, :path, :maxKnowledgeLevel, :personalRate, :status)")
    suspend fun addPackage(packageID: String, name: String, frontLanguage: String, backLanguage: String, path: String,  status: Int, maxKnowledgeLevel: Int, personalRate: Int): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun initLearningPackageProgress(pack: LearningPackageProgress): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun initMemoryPackageProgress(memoryPackageProgress: MemoryPackageProgress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun initQuizPackageProgress(quizPackageProgress: QuizPackageProgress)

    @Query("UPDATE flashcards_package_tbl SET package_name= :name, front_language= :frontLanguage, back_language= :backLanguage, artwork= :artwork WHERE primary_package_id= :packageID")
    suspend fun updatePackage(
        packageID: String,
        name: String,
        frontLanguage: String,
        backLanguage: String,
        artwork: String
    )

    @Query("UPDATE flashcards_package_tbl SET max_knowledge_level= max_knowledge_level+:value WHERE primary_package_id=:packageID")
    suspend fun incrementMaxKnowledgeLevel(packageID: String, value: Int)

    @Query("UPDATE flashcards_package_tbl SET max_knowledge_level= max_knowledge_level-:value WHERE primary_package_id=:packageID")
    suspend fun decrementMaxKnowledgeLevel(packageID: String, value: Int)

    @Query("INSERT OR REPLACE INTO shared_package_tbl(foreign_package_id, description, price, currency) VALUES(:foreignPackageID, :description, :price, :currency)")
    suspend fun sharePackage(foreignPackageID: String, description: String, price: Int, currency: String)

    @Query("UPDATE flashcards_package_tbl SET status = 2 WHERE primary_package_id=:foreignPackageID")
    suspend fun setPackageStatusToShared(foreignPackageID: String)

    @Query("UPDATE shared_package_tbl SET description=:description, price=:price, currency=:currency WHERE foreign_package_id=:foreignPackageID")
    suspend fun updateSharing(foreignPackageID: String, description: String, price: Int, currency: String)

    @Query("SELECT COUNT(*) FROM shared_package_tbl WHERE foreign_package_id=:packageId")
    suspend fun checkIsShared(packageId: String): Int

    @Query("SELECT package_name FROM flashcards_package_tbl WHERE primary_package_id=:packageId")
    suspend fun getPackageName(packageId: String): String

    @Query("UPDATE learning_package_progress_tbl SET translations_best_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setLearningTranslationsBestScore(packageID: String, score: Int)

    @Query("UPDATE learning_package_progress_tbl SET explanations_best_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setLearningExplanationsBestScore(packageID: String, score: Int)

    @Query("UPDATE learning_package_progress_tbl SET phrases_best_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setLearningPhrasesBestScore(packageID: String, score: Int)


    @Query("UPDATE quiz_package_progress_tbl SET quiz_best_translations_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setQuizTranslationsBestScore(packageID: String, score: Int)

    @Query("UPDATE quiz_package_progress_tbl SET quiz_best_translations_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setQuizExplanationsBestScore(packageID: String, score: Int)

    @Query("UPDATE quiz_package_progress_tbl SET quiz_best_translations_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setQuizPhrasesBestScore(packageID: String, score: Int)

    @Query("UPDATE memory_package_progress_tbl SET memory_best_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setMemoryTranslationsBestScore(packageID: String, score: Int)


    @Query("UPDATE learning_package_progress_tbl SET translations_current_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setLearningTranslationsCurrentScore(packageID: String, score: Int)

    @Query("UPDATE learning_package_progress_tbl SET explanations_current_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setLearningExplanationsCurrentScore(packageID: String, score: Int)

    @Query("UPDATE learning_package_progress_tbl SET phrases_current_score=:score WHERE foreign_package_id=:packageID")
    suspend fun setLearningPhrasesCurrentScore(packageID: String, score: Int)

    @Query("UPDATE learning_package_progress_tbl SET are_translations_completed=1 WHERE foreign_package_id=:packageID")
    suspend fun setLearningTranslationsCompleted(packageID: String)

    @Query("UPDATE learning_package_progress_tbl SET are_explanations_completed=1 WHERE foreign_package_id=:packageID")
    suspend fun setLearningExplanationsCompleted(packageID: String)

    @Query("UPDATE learning_package_progress_tbl SET are_phrases_completed=1 WHERE foreign_package_id=:packageID")
    suspend fun setLearningPhrasesCompleted(packageID: String)

    @Query("SELECT are_translations_completed from learning_package_progress_tbl WHERE foreign_package_id=:packageID")
    suspend fun areTranslationsCompleted(packageID: String): Boolean

    @Query("SELECT are_explanations_completed from learning_package_progress_tbl WHERE foreign_package_id=:packageID")
    suspend fun areExplanationsCompleted(packageID: String): Boolean

    @Query("SELECT are_phrases_completed from learning_package_progress_tbl WHERE foreign_package_id=:packageID")
    suspend fun arePhrasesCompleted(packageID: String): Boolean

    @Query("DELETE from flashcards_package_tbl where primary_package_id =:packageID")
    suspend fun deletePackageByID(packageID: String)

    @Query("DELETE from learning_package_progress_tbl where foreign_package_id =:packageID")
    suspend fun deleteLearningPackageProgress(packageID: String)

    @Query("DELETE from memory_package_progress_tbl where foreign_package_id =:packageID")
    suspend fun deleteMemoryPackageProgress(packageID: String)

    @Query("DELETE from quiz_package_progress_tbl where foreign_package_id =:packageID")
    suspend fun deleteQuizPackageProgress(packageID: String)

    @Query("UPDATE flashcards_package_tbl SET personal_rate=:personalRate WHERE primary_package_id=:packageID")
    suspend fun setPersonalRate(packageID: String, personalRate: Int): Int

    @Query("SELECT personal_rate from flashcards_package_tbl WHERE primary_package_id=:packageID")
    suspend fun getPersonalRate(packageID: String): Int

    @Transaction
    @Query(
        "SELECT " +
                "card_id as cardID, " +
                "word as word, " +
                "status as status, " +
                "REPLACE(translations, '|', ', ') as translations, " +
                "CASE " +
                "   WHEN explanations != '' AND phrases != '' THEN 3 " +
                "   WHEN explanations != '' AND phrases = '' THEN 2 " +
                "   WHEN explanations = '' AND phrases != '' THEN 2 " +
                "   WHEN explanations = '' AND phrases = '' THEN 1 " +
                "   ELSE 0 " +
                "END AS cardMaxKnowledgeLevel, " +
                "is_translation_known as isTranslationKnown, " +
                "is_explanation_known as isExplanationKnown, " +
                "is_phrase_known as isPhraseKnown, " +
                "is_important as isImportant " +
                "FROM flashcard_tbl f " +
                "INNER JOIN learning_card_progress_tbl l ON f.card_id = l.foreign_card_id " +
                "INNER JOIN flashcards_package_tbl p ON f.foreign_package_id = p.primary_package_id " +
                "WHERE f.foreign_package_id = :packageID " +
                "GROUP BY f.card_id"
    )
    fun getFlashcardsToDisplay(packageID: String): Flow<List<FlashcardToDisplayInList>>


    @Query(
        "SELECT card_id as cardID," +
                " word as frontText," +
                " REPLACE(translations, '|', '\n\n') as backText," +
                " is_important as importance" +
                " from flashcard_tbl" +
                " where foreign_package_id =:packageID"
    )
    fun getWordToTranslationExploreGameItems(packageID: String): Flow<List<ExploreGameItem>>


    @Query(
        "SELECT" +
                " card_id as cardID," +
                " word as frontText," +
                " REPLACE(explanations, '|', '\n\n') as backText," +
                " is_important as importance" +
                " from flashcard_tbl" +
                " where foreign_package_id =:packageID and explanations!=''"
    )
    fun getWordToExplanationExploreGameItems(packageID: String): Flow<List<ExploreGameItem>>

    @Query(
        "SELECT card_id as cardID," +
                " word as frontText," +
                " REPLACE(phrases, '|', '\n\n') as backText," +
                " is_important as importance" +
                " from flashcard_tbl" +
                " where foreign_package_id =:packageID and phrases!=''"
    )
    fun getWordToPhraseExploreGameItems(packageID: String): Flow<List<ExploreGameItem>>

    @Transaction
    @Query("SELECT primary_package_id as FlashcardIDForeign, word as word, translations as translations, explanations as explanations, phrases as phrases FROM flashcard_tbl INNER JOIN flashcards_package_tbl ON foreign_package_id = primary_package_id WHERE primary_package_id = :packageID")
    fun getRawFlashcards(packageID: String): List<RawFlashcard>

    @Query("SELECT" +
            " card_id as cardID," +
            " word as word," +
            " translations as translations," +
            " explanations as explanations," +
            " phrases as phrases" +
            " from flashcard_tbl" +
            " where card_id=:cardID")
    fun getFlashcardToModify(cardID: String): FlashcardToUpdate

    @Query("SELECT" +
            " card_id as cardID" +
            " from flashcard_tbl" +
            " WHERE foreign_package_id=:packageID " +
            " ORDER BY RANDOM() LIMIT 12")
    fun get12IdsForMemory(packageID: String): List<String>

    @Query("SELECT" +
            " card_id as cardID," +
            " word as content," +
            " 0 as state," +
            " 0 as isBackVisible" +
            " from flashcard_tbl" +
            " WHERE card_id =:cardID")
    fun getWordForMemory(cardID: String): MemoryCard

    @Query("SELECT" +
            " card_id as cardID," +
            " translations as content," +
            " 0 as state," +
            " 0 as isBackVisible" +
            " from flashcard_tbl" +
            " WHERE card_id =:cardID")
    fun getTranslationForMemory(cardID: String): MemoryCard

    @Transaction
    @Query(
        "SELECT card_id as cardID, word as frontText, translations as backText, is_important as importance, translation_knowledge_lvl as level " +
                "FROM flashcard_tbl f " +
                "INNER JOIN learning_card_progress_tbl l ON  f.card_id=l.foreign_card_id "+
                "WHERE foreign_package_id = :packageID " +
                "AND translation_knowledge_lvl <= 0 " +
                "ORDER BY card_id ASC " +
                "LIMIT :limit"
    )
    fun getWordToTranslationsLearnGameItems(
        packageID: String,
        limit: Int
    ): List<LearnGameItem>

    @Transaction
    @Query(
        "SELECT card_id as cardID, word as frontText, explanations as backText, is_important as importance, explanation_knowledge_lvl as level " +
                "FROM flashcard_tbl f " +
                "INNER JOIN learning_card_progress_tbl l ON  f.card_id=l.foreign_card_id "+
                "WHERE foreign_package_id = :packageID " +
                "AND explanation_knowledge_lvl <= 0 " +
                "ORDER BY card_id ASC " +
                "LIMIT :limit"
    )
    fun getWordToExplanationsLearnGameItems(
        packageID: String,
        limit: Int
    ): List<LearnGameItem>

    @Transaction
    @Query(
        "SELECT card_id as cardID, word as frontText, phrases as backText, is_important as importance, phrase_knowledge_lvl as level " +
                "FROM flashcard_tbl f " +
                "INNER JOIN learning_card_progress_tbl l ON  f.card_id=l.foreign_card_id "+
                "WHERE foreign_package_id = :packageID " +
                "AND phrase_knowledge_lvl <= 0 " +
                "ORDER BY card_id ASC " +
                "LIMIT :limit"
    )
    fun getWordToPhrasesLearnGameItems(
        packageID: String,
        limit: Int
    ): List<LearnGameItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFlashcard(flashCard: Flashcard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun initLearningFlashcardProgress(learningCardProgress: LearningCardProgress)

    @Query("UPDATE flashcard_tbl SET is_important =:importance WHERE card_id = :cardID")
    suspend fun updateImportance(importance: Boolean, cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET translation_knowledge_lvl = 1 WHERE foreign_card_id = :cardID")
    suspend fun incrementTranslationKnowledgeLevel(cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET translation_knowledge_lvl = 0 WHERE foreign_card_id = :cardID")
    suspend fun decrementTranslationKnowledgeLevel(cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET translation_knowledge_lvl = :value WHERE foreign_card_id = :cardID")
    suspend fun setTranslationKnowledgeLevel(cardID: String, value: Int)

    @Query("UPDATE learning_card_progress_tbl SET explanation_knowledge_lvl = 1 WHERE foreign_card_id = :cardID")
    suspend fun incrementExplanationKnowledgeLevel(cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET explanation_knowledge_lvl = 0 WHERE foreign_card_id = :cardID")
    suspend fun decrementExplanationKnowledgeLevel(cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET explanation_knowledge_lvl = :value WHERE foreign_card_id = :cardID")
    suspend fun setExplanationKnowledgeLevel(cardID: String, value: Int)

    @Query("UPDATE learning_card_progress_tbl SET phrase_knowledge_lvl = 1 WHERE foreign_card_id = :cardID")
    suspend fun incrementPhraseKnowledgeLevel(cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET phrase_knowledge_lvl = 0 WHERE foreign_card_id = :cardID")
    suspend fun decrementPhraseKnowledgeLevel(cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET phrase_knowledge_lvl = :value WHERE foreign_card_id = :cardID")
    suspend fun setPhraseKnowledgeLevel(cardID: String, value: Int)


    @Transaction
    @Query("SELECT f.card_id FROM flashcard_tbl f INNER JOIN flashcards_package_tbl p ON f.foreign_package_id = p.primary_package_id WHERE f.foreign_package_id = :packageID")
    suspend fun getCardIdsForPackage(packageID: String): List<String>


    @Query("UPDATE learning_card_progress_tbl SET translation_knowledge_lvl = 0 WHERE foreign_card_id=:cardID")
    suspend fun resetSingleTranslationKnowledgeProgress(cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET explanation_knowledge_lvl = 0 WHERE foreign_card_id=:cardID")
    suspend fun resetSingleExplanationKnowledgeProgress(cardID: String)

    @Query("UPDATE learning_card_progress_tbl SET explanation_knowledge_lvl = 0 WHERE foreign_card_id=:cardID")
    suspend fun resetSinglePhraseKnowledgeProgress(cardID: String)


    @Query("UPDATE learning_card_progress_tbl SET is_translation_known=:value  where foreign_card_id=:cardID")
    suspend fun setIsTranslationKnown(cardID: String, value: Boolean)

    @Query("UPDATE learning_card_progress_tbl SET is_explanation_known=:value  where foreign_card_id=:cardID")
    suspend fun setIsExplanationKnown(cardID: String, value: Boolean)

    @Query("UPDATE learning_card_progress_tbl SET is_phrase_known=:value  where foreign_card_id=:cardID")
    suspend fun setIsPhraseKnown(cardID: String, value: Boolean)

    @Query("UPDATE flashcard_tbl SET word =:word, translations =:translations, explanations =:explanations, phrases =:phrases  WHERE card_id = :cardID")
    suspend fun updateFlashcard(
        cardID: String,
        word: String,
        translations: String,
        explanations: String,
        phrases: String
    )

    @Query("DELETE from flashcard_tbl where card_id =:cardID")
    suspend fun deleteFlashcardById(cardID: String)

    @Query("DELETE from learning_card_progress_tbl where foreign_card_id =:cardID")
    suspend fun deleteLearningCardProgress(cardID: String)

    @Query("DELETE from flashcard_tbl where foreign_package_id =:packageID")
    suspend fun deleteFlashcardsByForeign(packageID: String)

    @Query("DELETE from learning_card_progress_tbl where foreign_card_id =:cardID")
    suspend fun deleteSingleLearningCardsProgress(cardID: String)

    @Query("SELECT card_id as cardID, translations as question, word as correctAnswer FROM flashcard_tbl WHERE foreign_package_id=:packageID ORDER BY RANDOM() LIMIT 15")
    suspend fun getQuizTranslationsWithCorrectAnswers(packageID: String): List<QuizQuestion>

    @Query("SELECT card_id as cardID, explanations as question, word as correctAnswer FROM flashcard_tbl WHERE foreign_package_id=:packageID ORDER BY RANDOM() LIMIT 15")
    suspend fun getQuizExplanationsWithCorrectAnswers(packageID: String): List<QuizQuestion>

    @Query("SELECT card_id as cardID, phrases as question, word as correctAnswer FROM flashcard_tbl WHERE foreign_package_id=:packageID ORDER BY RANDOM() LIMIT 15")
    suspend fun getQuizPhrasesWithCorrectAnswers(packageID: String): List<QuizQuestion>

    @Query("SELECT word as answer, 0 as isCorrect FROM flashcard_tbl WHERE foreign_package_id=:packageID and card_id!=:cardID ORDER BY RANDOM() LIMIT 3")
    suspend fun getQuizIncorrectAnswers(packageID: String, cardID: String): List<QuizOption>

    @Query("SELECT COUNT(*) FROM flashcard_tbl WHERE foreign_package_id=:packageID and translations!=''")
    suspend fun getAmountOfCardsWithTranslations(packageID: String): Int

    @Query("SELECT COUNT(*) FROM flashcard_tbl WHERE foreign_package_id=:packageID and explanations!=''")
    suspend fun getAmountOfCardsWithExplanations(packageID: String): Int

    @Query("SELECT COUNT(*) FROM flashcard_tbl WHERE foreign_package_id=:packageID and phrases!=''")
    suspend fun getAmountOfCardsWithPhrases(packageID: String): Int

    @Query("INSERT INTO backup_method_tbl(timestamp, command_json) VALUES(:timestamp, :commandJSON)")
    suspend fun insertUncalledBackupMethod(timestamp: Long, commandJSON: String)

    @Query("SELECT * FROM backup_method_tbl ORDER BY timestamp ASC")
    fun getUncalledBackupMethods(): Flow<List<BackupMethod>>

    @Query("DELETE FROM backup_method_tbl WHERE backup_method_id=:id")
    suspend fun deleteUncalledMethod(id: Long)

    @Query("DELETE FROM backup_method_tbl")
    suspend fun deleteAllBackupMethods()
}