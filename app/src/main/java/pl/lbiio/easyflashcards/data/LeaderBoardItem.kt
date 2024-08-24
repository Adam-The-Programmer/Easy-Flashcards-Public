package pl.lbiio.easyflashcards.data

import android.graphics.Color
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import pl.lbiio.easyflashcards.data.LeaderBoardItem.LeaderBoardItem.setLeaderBoardItemPoints
import java.util.concurrent.TimeUnit

data class LeaderBoardItem(
    var index: Int,
    val UID: String,
    val nick: String,
    val points: Int,
    var color: String
){
    companion object LeaderBoardItem{
        @BindingAdapter("setNick")
        @JvmStatic
        fun TextView.setNick(nick: String) {
            this.text = nick
        }

        @BindingAdapter("setLeaderBoardItemPoints")
        @JvmStatic
        fun TextView.setLeaderBoardItemPoints(points: String) {
            this.text = points
        }

        @BindingAdapter("setIndex")
        @JvmStatic
        fun TextView.setIndex(index: String) {
            this.text = index
        }

        @BindingAdapter("setColor")
        @JvmStatic
        fun CardView.setColor(color: String) {
            val colorInt = when (color) {
                "green" -> Color.rgb(0, 255, 0) // Green
                "bronze" -> Color.rgb(165, 42, 42) // Brown
                "silver" -> Color.rgb(192, 192, 192) // Silver
                "gold" -> Color.rgb(255, 215, 0) // Gold
                else -> Color.WHITE
            }
            this.setCardBackgroundColor(colorInt)
        }

    }
}

