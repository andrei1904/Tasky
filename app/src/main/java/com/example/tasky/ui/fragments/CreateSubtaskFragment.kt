package com.example.tasky.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.tasky.data.model.Icon
import com.example.tasky.data.model.IconType
import com.example.tasky.databinding.FragmentCreateSubtasksBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.viewmodels.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateSubtaskFragment : BaseFragment<FragmentCreateSubtasksBinding>() {

    private val viewModel by viewModels<TasksViewModel>()

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentCreateSubtasksBinding.inflate(inflater, container, false)
    }

    override fun getRightIcon(): Icon {
        return Icon(IconType.CREATE_ICON, {})
    }

    override fun getLeftIcon(): Icon {
        return Icon(
            IconType.BACK_ICON,
            {
                (activity as BaseActivity).getFragmentNavigation()
                    .replaceFragment(CreateTaskFragment())
            }
        )
    }

    override fun getTitle(): String {
        return "Add subtask"
    }
}