package pl.lbiio.easyflashcards.starter_activities.signing_up

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.AppDialog
import pl.lbiio.easyflashcards.starter_activities.MainActivity
import pl.lbiio.easyflashcards.OnNegativeButtonClickListener
import pl.lbiio.easyflashcards.OnPositiveButtonClickListener
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.ActivityLoginBinding
import pl.lbiio.easyflashcards.model.UsersViewModel

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginContainer: FrameLayout
    private lateinit var toolbar: Toolbar
    private val chooseFormFragment = ChooseFormFragment()
    private val chooseFragmentTransaction = supportFragmentManager.beginTransaction()
    private val disposables: CompositeDisposable = CompositeDisposable()
    private val PERMISSIONS_REQUEST_CODE = 1
    private var PERMISSIONS = mutableListOf<String>()
    private val usersViewModel: UsersViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        toolbar = binding.loginToolbar.root
        loginContainer = binding.loginContainer
        setSupportActionBar(toolbar)
        setContentView(binding.root)
        auth = Firebase.auth
        PERMISSIONS = mutableListOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PERMISSIONS.add(Manifest.permission.POST_NOTIFICATIONS)
            PERMISSIONS.add(Manifest.permission.FOREGROUND_SERVICE)
            PERMISSIONS.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        chooseFragmentTransaction
            .add(R.id.login_container, chooseFormFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        setupPermissions()

        usersViewModel.stage.observe(this) {
            Log.d("zaobserwowano wartosc", "tak")
            if (it == "registration finished" || it == "login finished") {
                Log.d("wartosc zarejestrowana prawidlowo", it)
                val mainActivityIntent = Intent(
                    this,
                    MainActivity::class.java
                )
                mainActivityIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                this.startActivity(mainActivityIntent)
            }
        }

        chooseFormFragment.setOnLoginChooseListener(
            object : OnLoginChooseListener {
                override fun onLoginChoose() {
                    val loginFormFragment = LoginFormFragment()
                    val loginFragmentTransaction = supportFragmentManager.beginTransaction()
                    loginFormFragment.setOnLoginClickListener(
                        object : OnLoginClickListener {
                            override fun onLoginClick(email: String, password: String) {
                                lifecycleScope.launch(Dispatchers.Main) {
                                    usersViewModel.isInternetAvailable(
                                        {
                                            usersViewModel.startLoginProcess(email, password)
                                        },
                                        {
                                            val internetLossDialog = AppDialog(
                                                this@LoginActivity,
                                                getString(R.string.no_internet_connection_dialog_title),
                                                getString(R.string.no_internet_connection_dialog_content),
                                                true,
                                                null
                                            )
                                            internetLossDialog.setOnPositiveButtonClickListener(
                                                object : OnPositiveButtonClickListener {
                                                    override fun onPositiveButtonClick() {
                                                        internetLossDialog.dismiss()
                                                    }
                                                }
                                            )
                                            internetLossDialog.setOnNegativeButtonClickListener(
                                                object : OnNegativeButtonClickListener {
                                                    override fun onNegativeButtonClick() {
                                                        internetLossDialog.dismiss()
                                                    }

                                                }
                                            )
                                            internetLossDialog.show(
                                                supportFragmentManager,
                                                getString(R.string.no_internet_connection_dialog_tag)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    )
                    loginFragmentTransaction
                        .replace(R.id.login_container, loginFormFragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }
        )
        chooseFormFragment.setOnRegistrationChooseListener(
            object : OnRegistrationChooseListener {
                override fun onRegistrationChoose() {
                    val registrationFormFragment = RegistrationFormFragment()
                    val registrationFragmentTransaction = supportFragmentManager.beginTransaction()
                    registrationFormFragment.setOnRegisterClickListener(
                        object : OnRegisterClickListener {
                            override fun onRegisterClick(
                                email: String,
                                password: String,
                                country: String,
                                phone: String
                            ) {
                                lifecycleScope.launch(Dispatchers.Main) {

                                    usersViewModel.isInternetAvailable(
                                        {
                                            withContext(Dispatchers.Main) {
                                                usersViewModel.startRegistrationProcess(
                                                    email,
                                                    password,
                                                    country,
                                                    phone
                                                )
                                            }
                                        },
                                        {
                                            val internetLossDialog = AppDialog(
                                                this@LoginActivity,
                                                getString(R.string.no_internet_connection_dialog_title),
                                                getString(R.string.no_internet_connection_dialog_content),
                                                true,
                                                null
                                            )
                                            internetLossDialog.setOnPositiveButtonClickListener(
                                                object : OnPositiveButtonClickListener {
                                                    override fun onPositiveButtonClick() {
                                                        internetLossDialog.dismiss()
                                                    }
                                                }
                                            )
                                            internetLossDialog.setOnNegativeButtonClickListener(
                                                object : OnNegativeButtonClickListener {
                                                    override fun onNegativeButtonClick() {
                                                        internetLossDialog.dismiss()
                                                    }

                                                }
                                            )
                                            internetLossDialog.show(
                                                supportFragmentManager,
                                                getString(R.string.no_internet_connection_dialog_tag)
                                            )
                                        }
                                    )

                                }

                            }
                        }
                    )
                    registrationFragmentTransaction
                        .replace(R.id.login_container, registrationFormFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit()
                }

            }
        )

    }


    private fun makeRequest() {

        ActivityCompat.requestPermissions(
            this,
            PERMISSIONS.toTypedArray(),
            PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                grantResults.forEach {
                    if (grantResults.isEmpty() || it != PackageManager.PERMISSION_GRANTED) {
                        Log.i(
                            getString(R.string.permission_tag),
                            "Permission has been denied by user"
                        )
                    } else {
                        Log.i(
                            getString(R.string.permission_tag),
                            "Permission has been granted by user"
                        )
                    }
                }
            }
        }
    }

    private fun setupPermissions() {
        val selfPermissions = mutableListOf(
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.VIBRATE
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            selfPermissions.add(
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.FOREGROUND_SERVICE
                )
            )
            selfPermissions.add(
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            )
            selfPermissions.add(
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        }


        selfPermissions.forEachIndexed { index, permission ->
            if (permission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        PERMISSIONS[index]
                    )
                ) {
                    Log.d(PERMISSIONS[index], "denied")
                } else {
                    makeRequest()
                }
            } else {
                Log.d(PERMISSIONS[index], "granted")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}