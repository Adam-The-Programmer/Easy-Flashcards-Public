package pl.lbiio.easyflashcards.data

import android.os.Parcel
import android.os.Parcelable

data class PurchasedPackageToExtract(
    var firebaseId: String,
    var name: String,
    var artwork: String,
    var amount: Long,
    var frontLanguage: String,
    var backLanguage: String,
    var JSON: String
) : Parcelable {

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firebaseId)
        parcel.writeString(name)
        parcel.writeString(artwork)
        parcel.writeLong(amount)
        parcel.writeString(frontLanguage)
        parcel.writeString(backLanguage)
        parcel.writeString(JSON)
    }

    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<PurchasedPackageToExtract> {
        override fun createFromParcel(parcel: Parcel): PurchasedPackageToExtract {
            return PurchasedPackageToExtract(parcel)
        }
        override fun newArray(size: Int): Array<PurchasedPackageToExtract?> {
            return arrayOfNulls(size)
        }
    }

    private constructor(parcel: Parcel) : this(
        firebaseId = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        artwork = parcel.readString() ?: "",
        amount = parcel.readLong(),
        frontLanguage = parcel.readString() ?: "",
        backLanguage = parcel.readString() ?: "",
        JSON = parcel.readString() ?: ""
    )


}