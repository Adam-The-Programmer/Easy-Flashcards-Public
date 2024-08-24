package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_card_progress_tbl")
data class MemoryCardProgress(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "memory_card_progress_id")
    var memoryCardProgressID: Long = 0L,

    @ColumnInfo(name = "foreign_card_id") //foreign key
    var foreignCardID: String = "",

    @ColumnInfo(name ="is_translation_known")
    var isMemoryTranslationKnown: Boolean = false

)