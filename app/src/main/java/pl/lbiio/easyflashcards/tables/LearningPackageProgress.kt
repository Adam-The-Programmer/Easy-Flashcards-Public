package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_package_progress_tbl")
data class LearningPackageProgress (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "learning_package_progress_id")
    var learningPackageID: Long= 0,

    @ColumnInfo(name = "foreign_package_id")
    var foreignPackageID: String = "",

    @ColumnInfo(name = "translations_best_score")
    var learningTranslationsBestScore: Int = 0,

    @ColumnInfo(name = "explanations_best_score")
    var learningExplanationsBestScore: Int = 0,

    @ColumnInfo(name = "phrases_best_score")
    var learningPhrasesBestScore: Int = 0,

    @ColumnInfo(name = "translations_current_score")
    var learningTranslationsCurrentScore: Int = 0,

    @ColumnInfo(name = "explanations_current_score")
    var learningExplanationsCurrentScore: Int = 0,

    @ColumnInfo(name = "phrases_current_score")
    var learningPhrasesCurrentScore: Int = 0,

    @ColumnInfo(name = "are_translations_completed")
    var areLearningTranslationsCompleted: Boolean = false,

    @ColumnInfo(name = "are_explanations_completed")
    var areLearningExplanationsCompleted: Boolean = false,

    @ColumnInfo(name = "are_phrases_completed")
    var areLearningPhrasesCompleted: Boolean = false
)