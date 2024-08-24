package pl.lbiio.easyflashcards.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class UsersFirebaseRepository @Inject constructor() {
    private val db = Firebase.firestore

    fun getAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().uid.toString()
    }

    fun login(email: String, password: String, onCompleted:(isSuccess: Boolean) -> Unit) {
        getAuth().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("login success", "yes")
                onCompleted(true)
            }.addOnFailureListener {
                onCompleted(false)
            }
    }

    fun canLogin(inputEmail: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", inputEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val canLogin = !querySnapshot.isEmpty
                callback(canLogin)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(false) // Handle the error condition
            }
    }



    fun register(email: String, password: String): Task<AuthResult> {
        return getAuth().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("register success", "yes")
            }
    }

    fun canRegister(inputEmail: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("email", inputEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val canRegister = querySnapshot.isEmpty
                callback(canRegister)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(false) // Handle the error condition
            }
    }



    fun uploadUserToFirebase(email: String): Task<DocumentReference> {
        val data = hashMapOf(
            "email" to email
        )

        val usersRef = db.collection("users")

        return usersRef.add(data)
            .addOnSuccessListener {
                // Upload success
            }
            .addOnFailureListener {
                // Upload failure
            }
    }
}