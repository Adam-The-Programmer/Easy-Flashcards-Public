package pl.lbiio.easyflashcards.model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.lbiio.easyflashcards.EasyFlashcardsApp
import javax.inject.Inject

@HiltViewModel
class SharedPreferencesViewModel @Inject constructor() :
    ViewModel() {

    companion object {
        val mutablePhrase: MutableLiveData<String> by lazy { MutableLiveData("") }
    }

    val PREFERENCE_NAME = "EASY_FLASHCARDS_PREFERENCES"
    private val PREF_NATIVE_LANGUAGE = "PREF_NATIVE_LANGUAGE"
    private val PREF_FOREIGN_LANGUAGE = "PREF_FOREIGN_LANGUAGE"
    private val PREF_MAXIMIM_PRICE = "PREF_MAXIMUM_PRICE"
    private val PREF_CURRENCY = "PREF_CURRENCY"

    private val pref: SharedPreferences = EasyFlashcardsApp.getAppContext()
        .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    private fun String.put(string: String) {
        editor.putString(this, string)
        editor.commit()
    }

    private fun String.put(number: Int) {
        editor.putInt(this, number)
        editor.commit()
    }

    private fun String.getInt() = pref.getInt(this, 0)

    private fun String.getString() = pref.getString(this, "")!!


    fun setNativeLanguage(nativeLanguage: String) {
        PREF_NATIVE_LANGUAGE.put(nativeLanguage)
    }

    fun setForeignLanguage(foreignLanguage: String) {
        PREF_FOREIGN_LANGUAGE.put(foreignLanguage)
    }

    fun setMaximumPrice(maximumPrice: Int) {
        PREF_MAXIMIM_PRICE.put(maximumPrice)
    }

    fun setCurrency(currency: String) {
        PREF_CURRENCY.put(currency)
    }

    fun getNativeLanguage() = PREF_NATIVE_LANGUAGE.getString()

    fun getForeignLanguage() = PREF_FOREIGN_LANGUAGE.getString()

    fun getMaximumPrice() = PREF_MAXIMIM_PRICE.getInt()

    fun getCurrency() = PREF_CURRENCY.getString()


}