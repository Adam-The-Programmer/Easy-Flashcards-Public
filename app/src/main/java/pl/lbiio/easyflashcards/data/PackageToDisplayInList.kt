package pl.lbiio.easyflashcards.data

import android.graphics.BitmapFactory
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import pl.lbiio.easyflashcards.R

data class PackageToDisplayInList(
    var packageID: String,
    var status: Int,
    var name: String,
    var amount: Int,
    var artwork: String,
    var nativeLanguage: String,
    var foreignLanguage: String,
    var progress: String
)
{
    companion object PackageToDisplayInList{
        @BindingAdapter("packageArtwork")
        @JvmStatic
        fun ShapeableImageView.setPackageArtwork(artwork: String) {
            if(artwork.contains("http")){
                Picasso.get().load(artwork).into(this)
            }
            else{
                val bitmap = BitmapFactory.decodeFile(artwork, BitmapFactory.Options())
                if (bitmap != null) {
                    this.setImageBitmap(bitmap)
                }
            }
        }

        @BindingAdapter("setStatus")
        @JvmStatic
        fun TextView.setContent(status: String) {
            when (status.toInt()) {
                1 -> {
                    this.text=context.getString(R.string.own)
                }
                2 -> {
                    this.text=context.getString(R.string.shared)
                }
                else -> {
                    this.text=context.getString(R.string.bought)
                }
            }
        }

    }
}