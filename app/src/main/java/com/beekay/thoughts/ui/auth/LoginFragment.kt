package com.beekay.thoughts.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.beekay.thoughts.R
import com.beekay.thoughts.databinding.FragmentLoginBinding
import com.beekay.thoughts.model.User
import com.beekay.thoughts.util.FirebaseLoginUtil
import com.beekay.thoughts.util.UserStatusListener
import com.beekay.thoughts.viewmodel.UserViewModel
import com.beekay.thoughts.viewmodel.factory.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment(), View.OnClickListener, UserStatusListener {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: UserViewModel by activityViewModels { UserViewModelFactory(requireContext()) }
    private lateinit var firebaseUtil: FirebaseLoginUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseUtil = FirebaseLoginUtil(this, viewModel)
        binding.btnLogin.setOnClickListener(this)
        binding.linkSignup.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        activateProgressBar()
        firebaseUtil.isAuthenticated()
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_login) {
            activateProgressBar()
            if (isValidLogin()) onValidationSuccess(
                binding.username.text.toString(),
                binding.password.text.toString()
            ) else onValidationFailed()
        } else if (v?.id == R.id.link_signup) {
            findNavController().navigate(R.id.nav_login_signup)
        }
    }

    override fun onSuccess(user: User) {
        dismissProgressBar()
        findNavController().navigate(R.id.nav_login_success)
    }

    override fun onError(message: String) {
        if (!message.contains("User not found. Please re-login")) {
            dismissProgressBar()
            showError(message)
        } else {
            dismissProgressBar()
        }
    }

    private fun isValidLogin(): Boolean {
        var valid = true
        val mail = binding.username.text.toString()
        val pass = binding.password.text.toString()
        if (mail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            binding.username.error = "Enter a valid email address"
            valid = false
        } else {
            binding.username.error = null
        }

        if (pass.isBlank() || pass.length < 6) {
            binding.password.error = "Password should be of minimum 6 characters"
            valid = false
        } else {
            binding.password.error = null
        }

        return valid
    }

    private fun onValidationSuccess(mail: String, pass: String) {
        firebaseUtil.login(mail, pass)
    }

    private fun onValidationFailed() {
        dismissProgressBar()
        showError("Validation Failed")
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

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
            .apply {
                view.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorAccent
                    )
                )
                view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                show()
            }
    }
}