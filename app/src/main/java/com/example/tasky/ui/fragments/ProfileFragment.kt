package com.example.tasky.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import com.example.tasky.R
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.databinding.FragmnetProfileBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.activites.LoginActivity
import com.example.tasky.ui.activites.MainActivity
import com.example.tasky.ui.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmnetProfileBinding>() {

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmnetProfileBinding.inflate(inflater, container, false)

        if (rootView == null) {
            rootView = viewBinding!!.root
            isFirstLoaded = true
            onBackPressListener()
        } else {
            isFirstLoaded = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFirstLoaded) {
            initButton()
            loadUserDetails()
        }
    }


    override fun getRightIcons(): List<Icon?> {
        return listOf(
            Icon(
                IconType.EDIT_ICON,
                {
                    onEditClicked()
                }
            )
        )
    }

    override fun getLeftIcon(): Icon? {
        return null
    }

    override fun getTitle(): String {
        return "My Profile"
    }

    private fun initButton() {
        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result) {
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                    }, { throwable ->
                        throwable.message?.let { safeThrowable ->
                            Log.e(
                                EditUserDetailsFragment::class.java.canonicalName,
                                safeThrowable
                            )
                        }
                    })
        }
    }

    private fun loadUserDetails() {
        viewModel.getUser()
            .subscribe(
                { result ->
                    if (result.firstName != null && result.lastName != null) {
                        binding.textUserName.text = context?.getString(
                            R.string.user_name,
                            result.firstName,
                            result.lastName
                        )
                    } else {
                        binding.textUserName.text = context?.getString(R.string.new_user)
                    }

                }, { throwable ->
                    throwable.message?.let { safeThrowable ->
                        Log.e(
                            EditUserDetailsFragment::class.java.canonicalName,
                            safeThrowable
                        )
                    }
                })
    }

    private fun onEditClicked() {
        (activity as BaseActivity).getFragmentNavigation()
            .replaceFragment(EditUserDetailsFragment())
    }

    private fun onBackPressListener() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            (activity as MainActivity).onBackPressTask()
        }
    }
}