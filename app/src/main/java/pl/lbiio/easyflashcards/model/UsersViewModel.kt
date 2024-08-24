package pl.lbiio.easyflashcards.model

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.lbiio.easyflashcards.EasyFlashcardsApp
import pl.lbiio.easyflashcards.data.MethodCall
import pl.lbiio.easyflashcards.api_classes.UserDTO
import pl.lbiio.easyflashcards.repository.UncalledMethodsRepository
import pl.lbiio.easyflashcards.repository.UsersApiRepository
import pl.lbiio.easyflashcards.repository.UsersFirebaseRepository
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val usersApiRepository: UsersApiRepository,
    private val usersFirebaseRepository: UsersFirebaseRepository,
    private val uncalledMethodsRepository: UncalledMethodsRepository
) :
    ViewModel() {

    val stage = MutableLiveData<String>()
    private val data = UserDTO("", "", "", "")

    fun startLoginProcess(email: String, password: String) {
        data.email = email
        tryLogIn(password)
    }

    fun startRegistrationProcess(email: String, password: String, country: String, phone: String) {
        data.email = email
        Log.d("dane", data.email)
        data.country = country
        data.phone = phone
        tryRegister(password)
    }

    private fun getCurrentUserId(): String {
        return usersFirebaseRepository.getCurrentUserId()
    }

    fun getCurrentUser(): FirebaseUser? {
        return usersFirebaseRepository.getCurrentUser()
    }

    private fun tryLogIn(password: String) {
        viewModelScope.launch {
            stage.value = "checking if can log in" //started
            Log.d("login data", "${data.email}, $password")
            usersFirebaseRepository.canLogin(
                data.email
            ) {
                Log.d("try login", "ok")
                stage.value = "checking completed" //completed
                if (it) {
                    login(data.email, password)
                    stage.value = "log in attempt" //started
                } else Toast.makeText(
                    EasyFlashcardsApp.getAppContext(),
                    "Please create account to log in",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun tryRegister(password: String) {
        viewModelScope.launch {
            stage.value = "checking if can register" //started
            Log.d("rejestracja", data.email)
            usersFirebaseRepository.canRegister(
                data.email
            ) {
                stage.value = "checking completed" //completed
                if (it) {
                    register(data.email, password)
                    stage.value = "register attempt" //started
                } else Toast.makeText(
                    EasyFlashcardsApp.getAppContext(),
                    "Your account exists. Please log in",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun startListeningToEmailVerification() {
        Log.d("krok 1", "wlazl")
        usersFirebaseRepository.getAuth().currentUser?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = usersFirebaseRepository.getAuth().currentUser
                val isEmailVerified = user?.isEmailVerified ?: false
                Log.d("isVerified", isEmailVerified.toString())
                if (!isEmailVerified) {
                    startListeningToEmailVerification()
                } else {
                    if (stage.value == "register attempt") {
                        Log.d("rejestracja rozpoczęta", "tak")
                        getCurrentUserId().let {
                            Log.d("uzyskane ID", it)
                            viewModelScope.launch(Dispatchers.IO) {
                                Log.d("proba dodania usera", "rozpoczeta")
                                usersApiRepository.addUser(
                                    UserDTO(
                                        it,
                                        data.email,
                                        data.phone,
                                        data.country
                                    )
                                )
                                Log.d("proba dodania usera", "zakonczona")
                                Log.d("proba dodania fire", "rozpoczeta")
                                usersFirebaseRepository.uploadUserToFirebase(data.email)
                                Log.d("proba dodania fire", "zakonczona")
                                withContext(Dispatchers.Main) {
                                    stage.value = "registration finished"
                                }
                            }
                        }
                    } else {
                        stage.value = "login finished"
                    }
                }
            } else {
                Log.d("blad", task.exception.toString())
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            usersFirebaseRepository.login(email, password) { isSuccess ->
                if (isSuccess) {
                    if (!usersFirebaseRepository.getCurrentUser()!!.isEmailVerified) {
                        usersFirebaseRepository.getCurrentUser()!!.sendEmailVerification()
                        Toast.makeText(
                            EasyFlashcardsApp.getAppContext(),
                            "We sent verification email on your E-mail",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.d("login", "logowanie")
                    startListeningToEmailVerification()
                } else {
                    Toast.makeText(
                        EasyFlashcardsApp.getAppContext(),
                        "Incorrect Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun register(email: String, password: String) {
        viewModelScope.launch {
            usersFirebaseRepository.register(email, password).addOnSuccessListener {
                usersFirebaseRepository.getCurrentUser()!!.sendEmailVerification()
                Toast.makeText(
                    EasyFlashcardsApp.getAppContext(),
                    "We sent verification email on your E-mail",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("register", "rejestracja")
                startListeningToEmailVerification()
            }
        }
    }

    suspend fun isInternetAvailable(isAvailable: suspend () -> Unit, isNotAvailable: () -> Unit) {
        uncalledMethodsRepository.isInternetAvailable(isAvailable, isNotAvailable)
    }


    suspend fun updateUserPoints(
        packageId: String,
        points: Int,
        awardCode: Int
    ) {
        val gson = Gson()
        val methodToInvokeJSON = gson.toJson(
            MethodCall(
                "updateUserPoints",
                listOf(getCurrentUserId(), packageId, points, awardCode)
            )
        )
        uncalledMethodsRepository.insertUncalledMethod(
            System.currentTimeMillis(),
            methodToInvokeJSON
        )
    }

    suspend fun getAllAwards(
    ) = usersApiRepository.getAllAwards(getCurrentUserId())

    suspend fun getGeneralLeaderBoard(
    ) = usersApiRepository.getGeneralLeaderBoard(getCurrentUserId())

    suspend fun getPackageLeaderBoard(
        packageId: String
    ) = usersApiRepository.getPackageLeaderBoard(getCurrentUserId(), packageId)

}