package pl.lbiio.easyflashcards.api_classes

import android.widget.TextView
import androidx.databinding.BindingAdapter
import pl.lbiio.easyflashcards.R
import java.util.concurrent.TimeUnit

data class UserAward(
    val description: String,
    val achievementTimestamp: Long,
    val pointsToEarn: Int
){
    companion object UserAward{
        @BindingAdapter("setDescription")
        @JvmStatic
        fun TextView.setDescription(description: String) {
            this.text = description
        }

        @BindingAdapter("setPoints")
        @JvmStatic
        fun TextView.setPoints(points: String) {
            this.text = points
        }

        @BindingAdapter("setTimeElapsed")
        @JvmStatic
        fun TextView.setTimeElapsed(timestamp: String) {
            val currentTime = System.currentTimeMillis()
            val diff = currentTime-timestamp.toLong()
            val years = TimeUnit.MILLISECONDS.toDays(diff) / 365
            val days = TimeUnit.MILLISECONDS.toDays(diff) % 365
            val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

            this.text = when {
                years > 0 -> context.getString(R.string.num_of_years, years.toString())
                days > 0 -> context.getString(R.string.num_of_days, days.toString())
                hours > 0 -> context.getString(R.string.num_of_hours, hours.toString())
                minutes > 0 -> context.getString(R.string.num_of_minutes, minutes.toString())
                else -> context.getString(R.string.less_than_minute)
            }
        }

    }
}
