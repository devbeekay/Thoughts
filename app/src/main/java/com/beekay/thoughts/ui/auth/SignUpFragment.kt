package com.beekay.thoughts.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.beekay.thoughts.R
import com.beekay.thoughts.databinding.FragmentSignUpBinding
import com.beekay.thoughts.model.User
import com.beekay.thoughts.util.UserStatusListener
import com.beekay.thoughts.viewmodel.UserViewModel
import com.beekay.thoughts.viewmodel.factory.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar


class SignUpFragment : Fragment(), View.OnClickListener, UserStatusListener {

    private lateinit var binding: FragmentSignUpBinding
    val viewModel: UserViewModel by activityViewModels { UserViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignup.setOnClickListener(this)
    }

    override fun onSuccess(user: User) {
        viewModel.insertUser(user)
        dismissProgressBar()
        findNavController().navigate(R.id.nav_signup_success)
    }

    override fun onError(message: String) {
        dismissProgressBar()
        showError(message)
    }

    override fun onClick(v: View?) {
        validateAndSignUp()
    }

    private fun validateAndSignUp() {
        activateProgressBar()
        val name: String = binding.nameRegister.text.toString()
        val mail: String = binding.usernameRegister.text.toString()
        val pass: String = binding.passwordRegister.text.toString()
        if (name.isEmpty()) {
            validationFailed("Name")
        } else if (mail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            validationFailed("Mail")
        } else if (pass.isBlank() || pass.trim().length < 6) {
            validationFailed("Password")
        } else {
            validationSuccess(name, mail, pass)
        }
    }

    private fun validationSuccess(name: String, mail: String, pass: String) {
        FirebaseSignUpUtil(this).signUp(name, mail, pass)
    }

    private fun validationFailed(message: String) {
        dismissProgressBar()
        val text = when {
            (message == "Name") -> "Name cannot be empty"
            (message == "Mail") -> "Mail should not be empty and be of abc@xyz.def"
            (message == "Password") -> "Password should be of 6 characters"
            else -> message
        }
        showError(text)
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).apply {
            view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            view.textAlignment = View.TEXT_ALIGNMENT_CENTER
            show()
        }
    }

    private fun activateProgressBar() {
        binding.progresslayout.visibility = View.VISIBLE
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun dismissProgressBar() {
        binding.progresslayout.visibility = View.GONE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}