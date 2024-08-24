package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_package_progress_tbl")
data class QuizPackageProgress(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "quiz_package_progress_id")
    var quizPackageProgressID: Long = 0L,

    @ColumnInfo(name = "foreign_package_id")
    var foreignPackageID: String = "",

    @ColumnInfo(name ="quiz_best_translations_score")
    var quizBestTranslationsScore: Int = 0,

    @ColumnInfo(name ="quiz_best_explanations_score")
    var quizBestExplanationsScore: Int = 0,

    @ColumnInfo(name ="quiz_best_phrases_score")
    var quizBestPhrasesScore: Int = 0
)
