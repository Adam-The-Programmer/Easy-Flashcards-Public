package pl.lbiio.easyflashcards.data

import androidx.databinding.BindingAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

data class OwnSharedPackageToDisplayInList(
    var firebaseId: String,
    var artwork: String,
    val name: String,
    var amount: Int,
    var downloads: Int,
    var rate: String
){
    companion object OwnSharedPackageToDisplayInList{
        @BindingAdapter("ownSharedPackageArtwork")
        @JvmStatic
        fun ShapeableImageView.setPicassoSrc(url: String?) {
            if (url != null) {
                Picasso.get().load(url).into(this)
            }
        }

    }
}
