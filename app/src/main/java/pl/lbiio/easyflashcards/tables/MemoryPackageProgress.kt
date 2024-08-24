package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_package_progress_tbl")
data class MemoryPackageProgress(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "memory_package_progress_id")
    var memoryPackageProgressID: Long = 0L,

    @ColumnInfo(name = "foreign_package_id") //foreign key
    var foreignPackageID: String = "",

    @ColumnInfo(name ="memory_best_score")
    var memoryBestScore: Int = 0
)
