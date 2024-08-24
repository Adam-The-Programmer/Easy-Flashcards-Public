package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_card_progress_tbl")
data class QuizCardProgress(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "quiz_card_progress_id")
    var quizCardProgressID: Long = 0L,

    @ColumnInfo(name = "foreign_card_id")
    var foreignCardID: String = "",

    @ColumnInfo(name ="is_translation_known")
    var isQuizTranslationKnown: Boolean = false,

    @ColumnInfo(name ="is_explanation_known")
    var isQuizExplanationKnown: Boolean = false,

    @ColumnInfo(name ="is_phrase_known")
    var isQuizPhraseKnown: Boolean = false

)