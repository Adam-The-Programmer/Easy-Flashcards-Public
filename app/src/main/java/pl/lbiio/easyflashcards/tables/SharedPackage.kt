package pl.lbiio.easyflashcards.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shared_package_tbl")
data class SharedPackage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "shared_package_id")
    var sharedPackageID: Long = 0L,

    @ColumnInfo(name = "foreign_package_id")
    var foreignPackageID: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "price")
    var price: Int = 0,

    @ColumnInfo(name = "currency")
    var currency: String = "EUR",
)
