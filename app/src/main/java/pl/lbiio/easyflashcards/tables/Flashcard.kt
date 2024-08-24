package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_tbl")
data class Flashcard(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "card_id")
    var cardID: String= "", // UID-timestamp

    @ColumnInfo(name = "foreign_package_id")
    var foreignPackageID: String, //UID-timestamp

    @ColumnInfo(name = "word")
    var word: String,

    @ColumnInfo(name = "translations")
    var translations: String,

    @ColumnInfo(name = "explanations")
    var explanations: String,

    @ColumnInfo(name = "phrases")
    var phrases: String,

    @ColumnInfo(name = "is_important")
    var isImportant: Boolean = false,

)