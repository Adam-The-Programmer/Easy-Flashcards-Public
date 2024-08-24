package pl.lbiio.easyflashcards.data

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import pl.lbiio.easyflashcards.R

data class SharedPackageToBuy (
    var packageID: String,
    var name: String,
    var path: String,
    var amount: Int,
    var downloads: Int,
    var price: Int,
    var currency: String,
    var maxPoints: Int,
    var acquiredPoints: Int,
)
{
    companion object SharedPackageToBuy{
        @BindingAdapter("sharedPackageArtwork")
        @JvmStatic
        fun ShapeableImageView.setPicassoSrc(url: String?) {
            if (url != null) {
                Picasso.get().load(url).into(this)
            }
        }

        @BindingAdapter("maxPointsForSharedPackageToBuy", "acquiredPointsForSharedPackageToBuy")
        @JvmStatic
        fun TextView.setPoints(maxPoints: String, acquiredPoints: String) {
            if(maxPoints.toInt()==0) this.text="N/A"
            else{
                val rate = String.format("%.2f", ((acquiredPoints).toFloat()/(maxPoints.toFloat()/5)))
                this.text = rate
            }
        }

        @BindingAdapter("priceForSharedPackageToBuy", "currencyForSharedPackageToBuy")
        @JvmStatic
        fun MaterialButton.setValue(price: String, currency: String) {
           if(price=="0"){
               this.text = context.getString(R.string.download_for_free)
           }else{
               this.text = context.getString(R.string.download_for_price, price, currency)
           }
        }

    }
}