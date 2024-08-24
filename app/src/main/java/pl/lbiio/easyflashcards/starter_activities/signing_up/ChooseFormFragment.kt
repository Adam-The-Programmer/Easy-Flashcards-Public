package pl.lbiio.easyflashcards.starter_activities.signing_up

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import pl.lbiio.easyflashcards.databinding.FragmentChooseFormBinding
interface OnRegistrationChooseListener{
    fun onRegistrationChoose()
}

interface OnLoginChooseListener{
    fun onLoginChoose()
}

@AndroidEntryPoint
class ChooseFormFragment(
    private var onRegistrationChooseListener: OnRegistrationChooseListener?=null,
    private var onLoginChooseListener: OnLoginChooseListener?=null
) : Fragment() {
    private lateinit var ctx: Context
    private lateinit var binding: FragmentChooseFormBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx = context?.applicationContext!!
        binding = FragmentChooseFormBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        binding.chooseLoginForm.setOnClickListener {
            onLoginChooseListener?.onLoginChoose()
        }

        binding.chooseRegistrationForm.setOnClickListener {
            onRegistrationChooseListener?.onRegistrationChoose()
        }
    }

    fun setOnRegistrationChooseListener(listener: OnRegistrationChooseListener) {
        onRegistrationChooseListener = listener
    }

    fun setOnLoginChooseListener(listener: OnLoginChooseListener) {
        onLoginChooseListener = listener
    }


}