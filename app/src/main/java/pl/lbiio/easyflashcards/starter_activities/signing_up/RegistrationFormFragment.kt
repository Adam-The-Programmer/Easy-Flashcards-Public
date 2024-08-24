package pl.lbiio.easyflashcards.starter_activities.signing_up

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.easyflashcards.R
import pl.lbiio.easyflashcards.databinding.FragmentRegistrationFormBinding

interface OnRegisterClickListener{
    fun onRegisterClick(email: String, password: String, country: String, phone: String)
}
@AndroidEntryPoint
class RegistrationFormFragment (
    private var onRegisterClickListener: OnRegisterClickListener?= null
) : Fragment() {
    private lateinit var binding: FragmentRegistrationFormBinding
    private lateinit var email: TextInputEditText
    private lateinit var phone: TextInputEditText
    private lateinit var country: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var passwordRetype: TextInputEditText
    private lateinit var registerButton: MaterialButton
    private lateinit var termsCheckbox: CheckBox
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationFormBinding.inflate(inflater)
        email = binding.email
        phone = binding.phone
        country = binding.country
        password = binding.password
        passwordRetype = binding.passwordRetype
        registerButton = binding.register
        termsCheckbox = binding.termsCheckbox
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        registerButton.setOnClickListener {
            if(password.text.toString() == passwordRetype.text.toString() && termsCheckbox.isChecked){
                if(password.length()>=6) onRegisterClickListener?.onRegisterClick(email.text.toString(), password.text.toString(), country.text.toString(), phone.text.toString())
                else Toast.makeText(context, getString(R.string.password_length_requirement), Toast.LENGTH_LONG).show()

            }
            else{
                Toast.makeText(context, getString(R.string.password_and_terms_criteria), Toast.LENGTH_LONG).show()
            }
        }

    }

    fun setOnRegisterClickListener(listener: OnRegisterClickListener) {
        onRegisterClickListener = listener
    }
}