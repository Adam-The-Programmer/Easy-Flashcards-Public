package pl.lbiio.easyflashcards.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.databinding.BindingAdapter
import com.google.android.material.imageview.ShapeableImageView

data class PackageItem(
    var packageID: String,
    var packageName: String,
    var packageSize: Int,
    var packageArtwork: String?
)