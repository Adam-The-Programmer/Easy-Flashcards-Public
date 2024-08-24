package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_tbl")
data class Achievement(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "achievement_id")
    var achievementID: String = "",

    @ColumnInfo(name ="award_code")
    var awardCode: Int,

    @ColumnInfo(name ="description")
    var description: String

)