package com.example.tasky.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tasky.databinding.FragmentLoginBinding
import com.example.tasky.ui.activites.MainActivity
import com.example.tasky.ui.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSingIn.setOnClickListener {

            val email = binding.textInputLayoutEmail.editText?.text.toString()
            val password = binding.textInputLayoutPassword.editText?.text.toString()

            viewModel.onLogin(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result == true) {
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    },
                    {
                        Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show()
                    }
                )
        }
    }

    fun onBackPressed() {
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}