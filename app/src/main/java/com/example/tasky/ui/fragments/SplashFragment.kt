package com.example.tasky.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tasky.R
import com.example.tasky.databinding.FragmentSplashBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.activites.MainActivity
import com.example.tasky.ui.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class SplashFragment : Fragment(), Animation.AnimationListener {

    private var _binding: FragmentSplashBinding? = null

    private val binding get() = _binding!!

    private lateinit var animation: Animation

    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animation = AnimationUtils.loadAnimation(view.context, R.anim.text_anim)
        binding.textViewLogo.startAnimation(animation)

        animation.setAnimationListener(this)
    }

    override fun onAnimationStart(p0: Animation?) {
        // nothing
    }

    override fun onAnimationEnd(p0: Animation?) {

        viewModel.isUserLoggedIn()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result == true) {
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                },
                {
                    if (activity is BaseActivity) {
                        (activity as BaseActivity).getFragmentNavigation()
                            .replaceFragment(LoginFragment(), true)
                    }
                }
            )
    }

    override fun onAnimationRepeat(p0: Animation?) {
        // nothing
    }

    fun onBackPressed() {
        activity?.finish()
    }
}