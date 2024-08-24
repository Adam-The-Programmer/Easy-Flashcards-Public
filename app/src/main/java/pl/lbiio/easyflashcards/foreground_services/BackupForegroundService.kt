package pl.lbiio.easyflashcards.foreground_services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pl.lbiio.easyflashcards.EasyFlashcardsApp
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.data.MethodCall
import pl.lbiio.easyflashcards.repository.FlashcardsApiRepository
import pl.lbiio.easyflashcards.repository.PackagesApiRepository
import pl.lbiio.easyflashcards.repository.UncalledMethodsRepository
import pl.lbiio.easyflashcards.repository.UsersApiRepository
import pl.lbiio.easyflashcards.modules.ApiModule
import pl.lbiio.easyflashcards.modules.AppModule

class BackupForegroundService : Service() {

    companion object {
        //private const val TAG = "BackupForegroundService"
        //private const val CHANNEL_ID = "BackupForegroundService"
        private const val INTERVAL: Long = 2000 // Interval in milliseconds
        private const val NOTIFICATION_ID = 101
    }

    data class BackupMethodToInvoke(
        val backupID: Long,
        val timestamp: Long,
        val commandJSON: CommandJSON
    )

    data class CommandJSON(
        val methodName: String,
        val params: List<Any> // Change the type according to your JSON structure
    )

    private val gson = Gson()
    private var mainServiceJob: Job? = null
    private var backupJob: Job? = null
    //private val apiRepository = ApiRepository(ApiModule.provideApiService())
    private val uncalledMethodsRepository = UncalledMethodsRepository(FlashcardsApiRepository(ApiModule.provideApiService()), PackagesApiRepository(ApiModule.provideApiService()), UsersApiRepository(ApiModule.provideApiService()), AppModule.provideFlashCardDao(AppModule.provideAppDatabase(
        EasyFlashcardsApp.getAppContext()
    )))
    private var counter = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onCreate()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            getString(R.string.service_name),
            getString(R.string.service_action_name),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
        val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.service_name))
            .setContentTitle(getString(R.string.service_working_label))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        val notification = notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notification)

        mainServiceJob = CoroutineScope(Dispatchers.IO).launch {
            //uncalledMethodsRepository.deleteAllBackupMethods()
            val flow = flow {
                while (true) {
                    uncalledMethodsRepository.loadUnCalledBackupMethods()
                    emit(uncalledMethodsRepository.uncalledBackupMethods.value)
                    delay(INTERVAL) // Add delay to avoid consuming too much resources
                }
            }
            flow.collect { list ->

                if (backupJob?.isCancelled == true || backupJob == null) {
                    backupJob = CoroutineScope(Dispatchers.IO).launch {
                        updateNotification(counter)
                        counter++
                        var amount = 0
                        for (item in list) {
                            uncalledMethodsRepository.isInternetAvailable({
                                val commandJSON = gson.fromJson(item.commandJSON, CommandJSON::class.java)
                                val backupMethodToInvoke = BackupMethodToInvoke(item.backupID, item.timestamp, commandJSON)
                                Log.d(getString(R.string.service_name), "invoked method $item")
                                Log.d(getString(R.string.service_name), commandJSON.params.toString())
                                uncalledMethodsRepository.invokeBackupMethod(MethodCall(commandJSON.methodName, commandJSON.params))
                                uncalledMethodsRepository.deleteUncalledMethod(backupMethodToInvoke.backupID)
                                amount++
                            }, {
                                Log.d(getString(R.string.service_name), "not invoked method $item")
                                amount++
                            })
                        }
                        if (amount == list.size) backupJob?.cancel()
                    }
                }
            }
        }
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMyActivityNotification(text: String): Notification {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            NotificationChannel(getString(R.string.service_name),
                getString(R.string.service_action_name),
                NotificationManager.IMPORTANCE_LOW)
        )

        // The PendingIntent to launch our activity if the user selects
        // this notification


        return NotificationCompat.Builder(this, getString(R.string.service_name))
            .setContentTitle(text)
            .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNotification(counter: Int) {
        val base = getString(R.string.service_working_label)
        var progress = "."
        for(i in 0 until (counter%3)){
            progress+="."
        }
        val text = base+progress
        val notification = getMyActivityNotification(text)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(getString(R.string.service_name), "Destroyed")
        mainServiceJob?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Return null because this service does not support binding
        return null
    }

}