package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_card_progress_tbl")
data class LearningCardProgress(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "learning_card_id")
    var learningCardID: Long= 0,

    @ColumnInfo(name = "foreign_card_id") //foreign key
    var foreignCardID: String = "",

    @ColumnInfo(name ="translation_knowledge_lvl")
    var translationKnowledgeLevel: Int = 0,

    @ColumnInfo(name ="explanation_knowledge_lvl")
    var explanationKnowledgeLevel: Int = 0,

    @ColumnInfo(name ="phrase_knowledge_lvl")
    var phraseKnowledgeLevel: Int = 0,

    @ColumnInfo(name ="is_translation_known")
    var isLearningTranslationKnown: Boolean = false,

    @ColumnInfo(name ="is_explanation_known")
    var isLearningExplanationKnown: Boolean = false, // true jeśli kiedykolwiek kliknięto know - potrzebne do bliczania progresu

    @ColumnInfo(name ="is_phrase_known")
    var isLearningPhraseKnown: Boolean = false

)