package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backup_method_tbl")
data class BackupMethod(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "backup_method_id")
    var backupID: Long = 0L,

    @ColumnInfo(name ="timestamp")
    var timestamp: Long,

    @ColumnInfo(name ="command_json")
    var commandJSON: String,

)
