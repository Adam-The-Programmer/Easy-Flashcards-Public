package pl.lbiio.easyflashcards.starter_activities.signing_up

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.easyflashcards.databinding.FragmentLoginFormBinding

interface OnLoginClickListener{
    fun onLoginClick(email: String, password: String)
}
@AndroidEntryPoint
class LoginFormFragment(
    private var onLoginClickListener: OnLoginClickListener?= null
) : Fragment() {
    private lateinit var binding: FragmentLoginFormBinding
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var loginButton: MaterialButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginFormBinding.inflate(inflater)
        email = binding.email
        password = binding.password
        loginButton = binding.login
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loginButton.setOnClickListener {
            onLoginClickListener?.onLoginClick(email.text.toString(), password.text.toString())
        }
    }

    fun setOnLoginClickListener(listener: OnLoginClickListener) {
        onLoginClickListener = listener
    }
}