package com.example.tasky.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.tasky.data.model.entities.Icon
import com.example.tasky.data.model.entities.IconType
import com.example.tasky.databinding.FragmentEditUserDetailsBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.viewmodels.ProfileViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class EditUserDetailsFragment : ValidatorFragment<FragmentEditUserDetailsBinding>() {

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentEditUserDetailsBinding.inflate(inflater, container, false)

        if (rootView == null) {
            rootView = viewBinding!!.root
            isFirstLoaded = true
        } else {
            isFirstLoaded = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isFirstLoaded) {
            addValidationListeners()
        }
    }

    override fun getRightIcons(): List<Icon?> {
        return listOf(
            Icon(
                IconType.CHECK_ICON,
                {
                    validateOnCheckClicked()
                }
            )
        )
    }

    override fun getLeftIcon(): Icon {
        return Icon(
            IconType.BACK_ICON,
            {
                (activity as BaseActivity).getFragmentNavigation().popBackStack()
            }
        )
    }

    override fun getTitle(): String {
        return "Edit details"
    }


    private fun validateOnCheckClicked() {
        if (!viewModel.isValid()) {
            validate(viewModel.values.ALL_FIELDS, viewModel.getCompletedFields())
            return
        }

        viewModel.updateUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if (result == true) {
                        (activity as BaseActivity).getFragmentNavigation()
                            .replaceFragment(ProfileFragment())
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

    override fun getTextInputsMap(): Map<String, TextInputLayout> {
        val map = HashMap<String, TextInputLayout>()

        map[viewModel.values.FIRST_NAME] = binding.textInputFirstName
        map[viewModel.values.LAST_NAME] = binding.textInputLastName

        return map
    }

    private fun addValidationListeners() {
        addValidationListener(binding.textInputLastName, viewModel.values.LAST_NAME)
        { x: String, y: String -> viewModel.setUserFields(x, y) }
        addValidationListener(binding.textInputFirstName, viewModel.values.FIRST_NAME)
        { x: String, y: String -> viewModel.setUserFields(x, y) }
    }
}