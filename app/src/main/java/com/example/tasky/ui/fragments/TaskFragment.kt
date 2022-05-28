package com.example.tasky.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasky.data.model.Icon
import com.example.tasky.data.model.IconType
import com.example.tasky.data.model.TaskWithSubtasks
import com.example.tasky.databinding.FragmentTaskBinding
import com.example.tasky.databinding.FragmentTasksBinding
import com.example.tasky.ui.activites.BaseActivity
import com.example.tasky.ui.adapter.TaskSubtasksAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskFragment(private val taskWithSubtasks: TaskWithSubtasks) : BaseFragment<FragmentTaskBinding>() {

    private lateinit var taskSubtasksAdapter: TaskSubtasksAdapter

    override fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentTaskBinding.inflate(inflater, container, false)

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
            initData()
        }
    }

    override fun getRightIcons(): List<Icon?> {
        return listOf(Icon(
            IconType.BACK_ICON,
            {
                (activity as BaseActivity).getFragmentNavigation().popBackStack()
            }
        ))
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
        return TITLE
    }

    private fun initData() {
        taskSubtasksAdapter = TaskSubtasksAdapter()
        initRecyclerView()

        taskSubtasksAdapter.setData(taskWithSubtasks)
    }

    private fun initRecyclerView() {
        val recyclerView = binding.tasksRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = taskSubtasksAdapter
    }

    companion object {
        private const val TITLE = "Task Details"
    }
}