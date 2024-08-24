package pl.lbiio.easyflashcards.starter_activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.easyflashcards.starter_activities.signing_up.LoginActivity
import pl.lbiio.easyflashcards.databinding.ActivityLauncherBinding
import pl.lbiio.easyflashcards.model.UsersViewModel

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLauncherBinding
    private val usersViewModel: UsersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val activityIntent: Intent
        if (usersViewModel.getCurrentUser() == null || !usersViewModel.getCurrentUser()!!.isEmailVerified) {
            activityIntent = Intent(
                this,
                LoginActivity::class.java
            )
            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        } else {
            activityIntent = Intent(
                this,
                MainActivity::class.java
            )
            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        this.startActivity(activityIntent)

    }
}