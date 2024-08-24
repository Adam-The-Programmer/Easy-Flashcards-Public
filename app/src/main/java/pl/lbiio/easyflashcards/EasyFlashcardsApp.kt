package pl.lbiio.easyflashcards

import android.app.Application
import android.content.Context
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import pl.lbiio.easyflashcards.foreground_services.BackupForegroundService

@HiltAndroidApp
class EasyFlashcardsApp : Application() {
    companion object {
        private lateinit var instance: EasyFlashcardsApp
        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        val serviceIntent = Intent(applicationContext, BackupForegroundService::class.java)
        this.stopService(serviceIntent)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance("https://easy-flashcards-fireserver01-default-rtdb.europe-west1.firebasedatabase.app").setPersistenceEnabled(true)
        instance = this

    }
}