package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards_package_tbl")
data class FlashcardsPackage(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "primary_package_id")
    var primaryPackageID: String = "", // jednocześnie backupID i firebaseID - UID-timestamp

    @ColumnInfo(name = "package_name")
    var name: String,

    @ColumnInfo(name = "front_language")
    var frontLanguage: String,

    @ColumnInfo(name = "back_language")
    var backLanguage: String,

    @ColumnInfo(name = "artwork")
    var artwork: String,

    @ColumnInfo(name = "max_knowledge_level")
    var maxKnowledgeLevel: Int = 0,

    @ColumnInfo(name = "personal_rate")
    var personalRate: Int = 0, // 0 dla own

    @ColumnInfo(name = "status")
    var status: Int = 1, //1 - isOwn isNotShared, 2 - isOwn isShared, 3 - isBought


)